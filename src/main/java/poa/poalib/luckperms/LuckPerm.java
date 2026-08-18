package poa.poalib.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import poa.poalib.PoaLib;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPerm {

    private static LuckPerms requireLuckPerms() {
        if (PoaLib.lpAPI == null) {
            throw new IllegalStateException("LuckPerms is not available.");
        }

        return PoaLib.lpAPI;
    }

    private static Permission requireVaultPermissions() {
        if (PoaLib.perms == null) {
            throw new IllegalStateException("Vault permissions are not available.");
        }

        return PoaLib.perms;
    }

    private static <T> CompletableFuture<T> failedFuture(String message) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException(message));
        return future;
    }

    public static String getPrimaryGroupOfOnline(UUID uuid) {
        User user = requireLuckPerms().getUserManager().getUser(uuid);
        if (user != null)
            return user.getPrimaryGroup();
        return null;
    }

    public static CompletableFuture<String> getPrimaryGroup(UUID uuid) {
        if (PoaLib.lpAPI == null)
            return failedFuture("LuckPerms is not available.");

        final CompletableFuture<String> future = new CompletableFuture<>();
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> future.complete(user.getPrimaryGroup()));
        return future;
    }

    public static List<String> getGroups(UUID uuid) {
        return List.of(requireVaultPermissions().getPlayerGroups(null, Bukkit.getOfflinePlayer(uuid)));
    }

    public static void setGroup(UUID uuid, String group) {
        requireLuckPerms().getUserManager().loadUser(uuid).thenAcceptAsync(user -> user.setPrimaryGroup(group));
    }

    public static void setNode(UUID uuid, String node) {
        requireLuckPerms().getUserManager().loadUser(uuid).thenAcceptAsync(user -> user.getNodes().add(Node.builder(node).build()));
    }

    public static String getPrefix(Player player) {
        return requireLuckPerms().getPlayerAdapter(Player.class).getMetaData(player).getPrefix();
    }

    public static CompletableFuture<String> getPrefix(UUID uuid) {
        if (PoaLib.lpAPI == null)
            return failedFuture("LuckPerms is not available.");

        CompletableFuture<String> future = new CompletableFuture<>();
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAccept(user -> {
            QueryOptions queryOptions = PoaLib.lpAPI.getContextManager().getStaticQueryOptions();
            CachedMetaData meta = user.getCachedData().getMetaData(queryOptions);

            String prefix = meta.getPrefix();

            future.complete(prefix != null ? prefix : "");
        });
        return future;
    }

    public static String getSuffix(Player player) {
        return requireLuckPerms().getPlayerAdapter(Player.class).getMetaData(player).getSuffix();
    }

    public static CompletableFuture<Boolean> hasPermission(UUID uuid, String permission) {
        if (PoaLib.lpAPI == null)
            return failedFuture("LuckPerms is not available.");

        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            future.complete(user.getCachedData().getPermissionData().checkPermission(permission).asBoolean());
        });
        return future;
    }

    public static void setPrefix(UUID uuid, String prefix) {
        requireLuckPerms().getUserManager().loadUser(uuid).thenAccept(user -> {
            user.data().clear(node -> node instanceof PrefixNode);

            PrefixNode prefixNode = PrefixNode.builder(prefix, 100).build();

            user.data().add(prefixNode);

            PoaLib.lpAPI.getUserManager().saveUser(user);
        });
    }

    public static void clearPrefix(UUID uuid) {
        requireLuckPerms().getUserManager().loadUser(uuid).thenAccept(user -> {
            user.data().clear(node -> node instanceof PrefixNode);
            PoaLib.lpAPI.getUserManager().saveUser(user);
        });
    }

    public static List<String> getPermissions(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();

        User user = luckPerms.getUserManager().loadUser(uuid).join();

        Map<String, Boolean> permissions = user.getCachedData()
                .getPermissionData()
                .getPermissionMap();

        return permissions.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }
}
