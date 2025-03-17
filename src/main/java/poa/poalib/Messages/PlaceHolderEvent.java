package poa.poalib.Messages;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceHolderEvent extends PlaceholderExpansion {

    public static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "poa";
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
        if(map.containsKey(params))
            return map.get(params) + "";

        return null;
    }
}
