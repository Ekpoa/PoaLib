package poa.poalib.blockutil;


import de.tr7zw.changeme.nbtapi.NBTFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Locations {


    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + " ££ " + location.getX() + " ££ " + location.getY() + " ££ " + location.getZ() + " ££ " + location.getYaw() + " ££ " + location.getPitch();
    }

    public static Location deserializeLocation(String string) {
        final String[] split = string.split(" ££ ");
        return new Location(Bukkit.getWorld(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }


    public static CompletableFuture<Location> getLastLocationAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            World defaultWorld = Bukkit.getWorlds().get(0); // fallback
            File playerFile = new File(defaultWorld.getWorldFolder(), "playerdata/" + uuid + ".dat");

            if (!playerFile.exists()) {
                return null; // never joined
            }

            try {
                NBTFile nbtFile = new NBTFile(playerFile);

                // Get position list
                double x = nbtFile.getDoubleList("Pos").get(0);
                double y = nbtFile.getDoubleList("Pos").get(1);
                double z = nbtFile.getDoubleList("Pos").get(2);

                // Get rotation list
                float yaw = 0f, pitch = 0f;
                if (nbtFile.hasTag("Rotation")) {
                    yaw = nbtFile.getFloatList("Rotation").get(0);
                    pitch = nbtFile.getFloatList("Rotation").get(1);
                }

                // Get world
                String dim = nbtFile.getString("Dimension"); // e.g. minecraft:overworld
                String worldName = dim.replace("minecraft:", "");
                World world = Bukkit.getWorld(worldName);
                if (world == null) world = defaultWorld;

                return new Location(world, x, y, z, yaw, pitch);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

