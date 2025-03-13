package poa.poalib.LuckPerms;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import poa.poalib.PoaLib;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LuckPerm {


    public static String getPrimaryGroupOfOnline(UUID uuid) {
        User user = PoaLib.lpAPI.getUserManager().getUser(uuid);
        if (user != null)
            return user.getPrimaryGroup();
        return null;
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






}
