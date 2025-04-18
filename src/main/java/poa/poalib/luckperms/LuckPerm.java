package poa.poalib.luckperms;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import poa.poalib.PoaLib;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPerm {


    public static String getPrimaryGroupOfOnline(UUID uuid) {
        User user = PoaLib.lpAPI.getUserManager().getUser(uuid);
        if (user != null)
            return user.getPrimaryGroup();
        return null;
    }

    public static CompletableFuture<String> getPrimaryGroup(UUID uuid) {
        final CompletableFuture<String> future = new CompletableFuture<>();
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            future.complete(user.getPrimaryGroup());
        });
        return future;
    }

    public static List<String> getGroups(UUID uuid){
        return List.of(PoaLib.perms.getPlayerGroups(null, Bukkit.getOfflinePlayer(uuid)));
    }

    public static void setGroup(UUID uuid, String group){
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            user.setPrimaryGroup(group);
        });
    }

    public static void setNode(UUID uuid, String node){
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            user.getNodes().add(Node.builder(node).build());
        });
    }

    public static String getPrefix(Player player){
        return PoaLib.lpAPI.getPlayerAdapter(Player.class).getMetaData(player).getPrefix();
    }

    public static String getSuffix(Player player){
        return PoaLib.lpAPI.getPlayerAdapter(Player.class).getMetaData(player).getSuffix();
    }

    public static CompletableFuture<Boolean> hasPermission(UUID uuid, String permission){
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        PoaLib.lpAPI.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            future.complete(user.getCachedData().getPermissionData().checkPermission(permission).asBoolean());
        });
        return future;
    }






}
