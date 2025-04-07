package poa.poalib.blockutil;


import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Locations {
    
    
    public static String serializeLocation(Location location){
        return location.getWorld().getName() + " ££ " + location.getX() + " ££ " + location.getY() + " ££ " + location.getZ() + " ££ " + location.getYaw() + " ££ " + location.getPitch();
    }
    
    public static Location deserializeLocation(String string){
        final String[] split = string.split(" ££ ");
        return new Location(Bukkit.getWorld(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
    
    
}
