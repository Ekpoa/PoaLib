package poa.poalib;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PoaLib extends JavaPlugin {

    public static PoaLib libINSTANCE;

    @Override
    public void onEnable() {
        libINSTANCE = this;

    }


    public static void isLoaded(){
        libINSTANCE.getLogger().log(Level.INFO, "PoaLib Loaded");
    }

}
