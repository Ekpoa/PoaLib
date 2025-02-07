package poa.poalib.NMSRelated;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Biomes {

    public static List<String> getBiomeNames(){

        return Registry.BIOME.stream().map(Biome::name).toList();


//        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
//
//        final Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
//
//        List<String> tr = new ArrayList<>();
//
//        for (Map.Entry<ResourceKey<Biome>, Biome> resourceKeyBiomeEntry : biomes.entrySet()) {
//            tr.add(resourceKeyBiomeEntry.getKey().location().toString());
//        }


    }


}
