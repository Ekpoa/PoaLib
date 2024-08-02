package poa.poalib.Files;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import poa.poalib.PoaLib;

import java.io.File;
import java.io.IOException;

public class PlayerDataFile {

    public static File getPlayerFile(Plugin plugin, OfflinePlayer player){
        String name = player.getUniqueId().toString();



        return createFile(plugin, name);
    }

    public static FileConfiguration getPlayerYML(File file){
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        try {
            yml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return yml;
    }

    public static void saveYML(File file, FileConfiguration yml){
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveYMLAsync(File file, FileConfiguration yml){
        Bukkit.getScheduler().runTaskAsynchronously(PoaLib.libINSTANCE, () -> {
            try {
                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static File createFile(Plugin plugin, String nameNoExtension){
        File folder = new File(PoaLib.libINSTANCE.getDataFolder() + File.separator + plugin.getName());
        folder.mkdirs();
        File file = new File(folder, nameNoExtension + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
