package poa.poalib.nmsrelated;

import lombok.SneakyThrows;
import org.bukkit.Location;

import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import io.netty.buffer.Unpooled;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Biomes {

    public static List<String> getBiomeNames(){
        return Registry.BIOME.stream().map(Biome::name).toList();
    }

    public static void sendBiomeBetween(Player viewer, Location a, Location b, String biomeName) {
        if (viewer == null || a == null || b == null) return;
        if (!a.getWorld().equals(b.getWorld()) || !a.getWorld().equals(viewer.getWorld())) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
        ServerLevel level = ((CraftWorld) a.getWorld()).getHandle();

        // bounds
        int minX = Math.min(a.getBlockX(), b.getBlockX());
        int maxX = Math.max(a.getBlockX(), b.getBlockX());
        int minY = Math.max(level.getMinY(), Math.min(a.getBlockY(), b.getBlockY()));
        int maxY = Math.min(level.getMaxY() - 1, Math.max(a.getBlockY(), b.getBlockY()));
        int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
        int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

        // resolve target biome holder (custom/vanilla)
        Holder<net.minecraft.world.level.biome.Biome> TARGET = resolveBiomeHolder(level, biomeName);

        int cMinX = Math.floorDiv(minX, 16), cMaxX = Math.floorDiv(maxX, 16);
        int cMinZ = Math.floorDiv(minZ, 16), cMaxZ = Math.floorDiv(maxZ, 16);

        List<Object> chunkDatas = new ArrayList<>();
        Constructor<?> cbdListCtor  = getChunkBiomeDataListCtor();
        Constructor<?> cbdBytesCtor = getChunkBiomeDataBytesCtor();

        for (int cx = cMinX; cx <= cMaxX; cx++) {
            for (int cz = cMinZ; cz <= cMaxZ; cz++) {
                LevelChunk chunk = level.getChunk(cx, cz);
                LevelChunkSection[] sections = chunk.getSections();

                List<PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>>> copies = new ArrayList<>(sections.length);

                for (int si = 0; si < sections.length; si++) {
                    LevelChunkSection sec = sections[si];

                    PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>> copy;
                    if (sec != null) {
                        PalettedContainerRO<Holder<net.minecraft.world.level.biome.Biome>> ro = sec.getBiomes();
                        copy = (ro != null) ? ro.copy() : newEmptyBiomeContainer(TARGET);
                    } else {
                        copy = newEmptyBiomeContainer(TARGET);
                    }

                    int secMinY = level.getMinY() + si * 16;
                    int secMaxY = secMinY + 15;
                    if (secMaxY < minY || secMinY > maxY) { copies.add(copy); continue; }

                    int boxMinX = Math.max(minX, cx * 16);
                    int boxMaxX = Math.min(maxX, cx * 16 + 15);
                    int boxMinY = Math.max(minY, secMinY);
                    int boxMaxY = Math.min(maxY, secMaxY);
                    int boxMinZ = Math.max(minZ, cz * 16);
                    int boxMaxZ = Math.min(maxZ, cz * 16 + 15);

                    int sMinX = (boxMinX & 15) >> 2, sMaxX = (boxMaxX & 15) >> 2;
                    int sMinY = (boxMinY - secMinY) >> 2, sMaxY = (boxMaxY - secMinY) >> 2;
                    int sMinZ = (boxMinZ & 15) >> 2, sMaxZ = (boxMaxZ & 15) >> 2;

                    for (int sx = sMinX; sx <= sMaxX; sx++)
                        for (int sy = sMinY; sy <= sMaxY; sy++)
                            for (int sz = sMinZ; sz <= sMaxZ; sz++)
                                copy.set(sx, sy, sz, TARGET);

                    copies.add(copy);
                }

                Object cbd;
                ChunkPos pos = new ChunkPos(cx, cz);

                if (cbdListCtor != null) {
                    List<PalettedContainerRO<Holder<net.minecraft.world.level.biome.Biome>>> asRO = new ArrayList<>(copies);
                    try {
                        cbd = cbdListCtor.newInstance(pos, asRO);
                    } catch (Exception e) {
                        throw new IllegalStateException("ChunkBiomeData(list) failed for " + pos, e);
                    }
                } else if (cbdBytesCtor != null) {
                    byte[] payload = serializeBiomeContainers(copies);
                    try {
                        cbd = cbdBytesCtor.newInstance(pos, payload);
                    } catch (Exception e) {
                        throw new IllegalStateException("ChunkBiomeData(bytes) failed for " + pos, e);
                    }
                } else {
                    throw new IllegalStateException("No usable ChunkBiomeData constructor found.");
                }

                chunkDatas.add(cbd);
            }
        }

        if (!chunkDatas.isEmpty()) {
            ClientboundChunksBiomesPacket pkt =
                    new ClientboundChunksBiomesPacket(castUncheckedList(chunkDatas));
            nmsPlayer.connection.send(pkt);
        }
    }

    // --- Biome resolver that works across mapping variants ---
    @SneakyThrows
    private static Holder<net.minecraft.world.level.biome.Biome> resolveBiomeHolder(ServerLevel level, String biomeName) {
        // normalize id: accept "plains" -> "minecraft:plains"
        String idStr = biomeName.contains(":") ? biomeName : "minecraft:" + biomeName;
        ResourceLocation id = ResourceLocation.parse(idStr);
        ResourceKey<net.minecraft.world.level.biome.Biome> key =
                ResourceKey.create(Registries.BIOME, id);

        Object regOrLookup;
        try {
            // prefer lookupOrThrow if present
            Method m = level.registryAccess().getClass().getMethod("lookupOrThrow", ResourceKey.class);
            regOrLookup = m.invoke(level.registryAccess(), Registries.BIOME);
        } catch (NoSuchMethodException e) {
            // fall back to registryOrThrow/registry
            try {
                Method m = level.registryAccess().getClass().getMethod("registryOrThrow", ResourceKey.class);
                regOrLookup = m.invoke(level.registryAccess(), Registries.BIOME);
            } catch (Throwable t) {
                try {
                    Method m = level.registryAccess().getClass().getMethod("registry", ResourceKey.class);
                    regOrLookup = ((java.util.Optional<?>) m.invoke(level.registryAccess(), Registries.BIOME))
                            .orElseThrow();
                } catch (Throwable t2) {
                    throw new IllegalStateException("Cannot access biome registry/lookup", t2);
                }
            }
        }

        // If it's a HolderGetter-like, call getOrThrow(ResourceKey)
        try {
            Method getOrThrow = regOrLookup.getClass().getMethod("getOrThrow", ResourceKey.class);
            @SuppressWarnings("unchecked")
            Holder<net.minecraft.world.level.biome.Biome> h =
                    (Holder<net.minecraft.world.level.biome.Biome>) getOrThrow.invoke(regOrLookup, key);
            return h;
        } catch (NoSuchMethodException ignored) { }

        // If it's a Registry<Biome>, try getHolder(ResourceKey) -> Optional<Holder<Biome>>
        try {
            Method getHolder = regOrLookup.getClass().getMethod("getHolder", ResourceKey.class);
            Object opt = getHolder.invoke(regOrLookup, key);
            @SuppressWarnings("unchecked")
            java.util.Optional<Holder<net.minecraft.world.level.biome.Biome>> oh =
                    (java.util.Optional<Holder<net.minecraft.world.level.biome.Biome>>) opt;
            return oh.orElseThrow(() -> new IllegalArgumentException("Biome not found: " + idStr));
        } catch (NoSuchMethodException e) {
            // last chance: get(ResourceKey) returning Biome, then wrap into a direct holder
            try {
                Method get = regOrLookup.getClass().getMethod("get", ResourceKey.class);
                Object biome = get.invoke(regOrLookup, key);
                if (biome == null) throw new IllegalArgumentException("Biome not found: " + idStr);
                // Direct holder factory (static): Holder.direct(T)
                Method direct = Holder.class.getMethod("direct", Object.class);
                @SuppressWarnings("unchecked")
                Holder<net.minecraft.world.level.biome.Biome> h =
                        (Holder<net.minecraft.world.level.biome.Biome>) direct.invoke(null, biome);
                return h;
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to resolve biome holder for " + idStr, t);
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to resolve biome holder (registry path)", t);
        }
    }

    // --- reflection helpers & packet plumbing ---

    @SuppressWarnings("unchecked")
    private static PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>>
    newEmptyBiomeContainer(Holder<net.minecraft.world.level.biome.Biome> fill) {
        try {
            var cfg   = Class.forName("net.minecraft.world.level.chunk.PalettedContainer$Configuration")
                    .getField("BIOME").get(null);
            var strat = Class.forName("net.minecraft.world.level.chunk.PalettedContainer$Strategy")
                    .getField("SECTION_BIOMES").get(null);
            return (PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>>) Class
                    .forName("net.minecraft.world.level.chunk.PalettedContainer")
                    .getConstructor(cfg.getClass(), Object.class, strat.getClass())
                    .newInstance(cfg, fill, strat);
        } catch (Throwable t) {
            throw new IllegalStateException("Unable to construct biome container", t);
        }
    }

    private static Constructor<?> getChunkBiomeDataListCtor() {
        try {
            return ClientboundChunksBiomesPacket.ChunkBiomeData.class
                    .getDeclaredConstructor(ChunkPos.class, List.class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Constructor<?> getChunkBiomeDataBytesCtor() {
        try {
            return ClientboundChunksBiomesPacket.ChunkBiomeData.class
                    .getDeclaredConstructor(ChunkPos.class, byte[].class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static byte[] serializeBiomeContainers(
            List<PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>>> containers) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        for (PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>> pc : containers) {
            pc.write(buf);
        }
        byte[] out = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), out);
        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<ClientboundChunksBiomesPacket.ChunkBiomeData>
    castUncheckedList(List<Object> list) {
        return (List<ClientboundChunksBiomesPacket.ChunkBiomeData>)(List<?>) list;
    }


}
