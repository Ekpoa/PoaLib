package poa.poalib.NMSRelated;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Biomes {

    public static List<String> getBiomeNames(){

        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();

        final Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);

        List<String> tr = new ArrayList<>();

        for (Map.Entry<ResourceKey<Biome>, Biome> resourceKeyBiomeEntry : biomes.entrySet()) {
            tr.add(resourceKeyBiomeEntry.getKey().location().toString());
        }

        return tr;
    }


}
