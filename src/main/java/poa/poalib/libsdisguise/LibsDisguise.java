package poa.poalib.libsdisguise;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class LibsDisguise {

    public static void undisguise(Player player, List<Player> viewers) {
        for (Disguise disguise : DisguiseAPI.getDisguises(player)) {
            if (!(disguise instanceof TargetedDisguise targetedDisguise))
                continue;

            for (Player viewer : viewers) {
                if (!targetedDisguise.canSee(viewer))
                    continue;

                if (targetedDisguise.getDisguiseTarget() == TargetedDisguise.TargetType.HIDE_DISGUISE_TO_EVERYONE_BUT_THESE_PLAYERS)
                    targetedDisguise.removePlayer(viewer);
                else
                    targetedDisguise.addPlayer(viewer);
            }

            if (targetedDisguise.getDisguiseTarget() == TargetedDisguise.TargetType.HIDE_DISGUISE_TO_EVERYONE_BUT_THESE_PLAYERS
                    && targetedDisguise.getObservers().isEmpty())
                targetedDisguise.removeDisguise();
        }
    }

    public static void fakeName(Player player, List<Player> viewers, String name) {
        undisguise(player, viewers);

        PlayerDisguise disguise = new PlayerDisguise(player, player);
        disguise.setName(name);

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    public static void fakeSkin(Player player, List<Player> viewers, String skin) {
        undisguise(player, viewers);

        PlayerDisguise disguise = new PlayerDisguise(player);
        disguise.setSkin(skin);

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    public static void fakeNameAndSkin(Player player, List<Player> viewers, String name, String skin) {
        undisguise(player, viewers);

        PlayerDisguise disguise = new PlayerDisguise(name, skin);

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    public static void fakeSkinUrl(Player player, List<Player> viewers, String textureUrl) {
        undisguise(player, viewers);

        PlayerDisguise disguise = new PlayerDisguise(player);
        disguise.setSkin(createSkinProfile(player.getName(), textureUrl));

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    public static void fakeNameAndSkinUrl(Player player, List<Player> viewers, String name, String textureUrl) {
        undisguise(player, viewers);

        PlayerDisguise disguise = new PlayerDisguise(name);
        disguise.setSkin(createSkinProfile(name, textureUrl));

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    public static void fakeEntity(Player player, List<Player> viewers, EntityType entityType) {
        undisguise(player, viewers);

        DisguiseType disguiseType = DisguiseType.getType(entityType);

        TargetedDisguise disguise;

        if (disguiseType.isMob())
            disguise = new MobDisguise(disguiseType);
        else if (disguiseType.isMisc())
            disguise = new MiscDisguise(disguiseType);
        else
            return;

        DisguiseAPI.disguiseToPlayers(player, disguise, viewers);
    }

    private static UserProfile createSkinProfile(String name, String textureUrl) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";

        String value = Base64.getEncoder().encodeToString(
                json.getBytes(StandardCharsets.UTF_8)
        );

        TextureProperty textureProperty = new TextureProperty(
                "textures",
                value,
                null
        );

        return new UserProfile(
                UUID.randomUUID(),
                name,
                List.of(textureProperty)
        );
    }

}
