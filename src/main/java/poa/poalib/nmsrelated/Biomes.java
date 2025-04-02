package poa.poalib.nmsrelated;

import org.bukkit.Registry;
import org.bukkit.block.Biome;

import java.util.List;

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
