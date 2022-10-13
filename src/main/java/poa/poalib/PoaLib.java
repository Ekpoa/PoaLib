package poa.poalib;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PoaLib extends JavaPlugin {



    public void isLoaded(){
        this.getLogger().log(Level.INFO, "PoaLib Loaded");
    }

}
