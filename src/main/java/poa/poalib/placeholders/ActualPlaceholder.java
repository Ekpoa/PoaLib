package poa.poalib.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActualPlaceholder extends PlaceholderExpansion {

    private static final PluginManager pluginManager = Bukkit.getPluginManager();


    @Override
    public @NotNull String getIdentifier() {
        return "poalib";
    }

    @Override
    public @NotNull String getAuthor() {
        return "poa";
    }

    @Override
    public @NotNull String getVersion() {
        return "1";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        final CustomPlaceholderEvent event = new CustomPlaceholderEvent(params);
        pluginManager.callEvent(event);

        return event.getOutput();
    }
}
