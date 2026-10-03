package poa.poalib.wurstsupport;

import org.bukkit.plugin.java.JavaPlugin;

public final class PoaWurstSupport extends JavaPlugin {
    private NoClipManager noClipManager;
    private PoaJumpManager poaJumpManager;
    private PoaReachManager poaReachManager;
    private PoaAttackManager poaAttackManager;
    private PoaInvManager poaInvManager;

    public static PoaWurstSupport INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        noClipManager = new NoClipManager();
        poaJumpManager = new PoaJumpManager();
        poaAttackManager = new PoaAttackManager();
        poaReachManager = new PoaReachManager(this);
        poaInvManager = new PoaInvManager();

        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                NoClipManager.CHANNEL,
                noClipManager
        );
        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                PoaJumpManager.CHANNEL,
                poaJumpManager
        );
        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                PoaReachManager.CHANNEL,
                poaReachManager
        );

        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                PoaAttackManager.CHANNEL,
                poaAttackManager
        );

        getServer().getMessenger().registerIncomingPluginChannel(
                this,
                PoaInvManager.CHANNEL,
                poaInvManager
        );


        getServer().getPluginManager().registerEvents(noClipManager, this);
        getServer().getPluginManager().registerEvents(poaReachManager, this);
        getServer().getPluginManager().registerEvents(new PoaInvManager(), this);

    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, NoClipManager.CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PoaJumpManager.CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PoaReachManager.CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PoaAttackManager.CHANNEL);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, PoaInvManager.CHANNEL);

        if (noClipManager != null)
            noClipManager.clear();

        if (poaReachManager != null)
            poaReachManager.clear();
    }

    public NoClipManager getNoClipManager() {
        return noClipManager;
    }

    public PoaJumpManager getPoaJumpManager() {
        return poaJumpManager;
    }

    public PoaReachManager getPoaReachManager() {
        return poaReachManager;
    }

    public PoaAttackManager getPoaAttackManager() {
        return poaAttackManager;
    }
    public PoaInvManager getPoaInvManager() {
        return poaInvManager;
    }
}
