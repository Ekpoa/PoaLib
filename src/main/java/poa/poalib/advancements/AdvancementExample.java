package poa.poalib.advancements;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import poa.poalib.PoaLib;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class AdvancementExample implements Listener {

    
    private final AdvancementTree tree;

    private final ConcurrentHashMap<UUID, Integer> blocksMined = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> playerKills = new ConcurrentHashMap<>();

    public AdvancementExample(AdvancementTree tree) {
        this.tree = tree;
    }


    private AdvancementTree createTree() {
        AdvancementTree tree = AdvancementTree.create("poa");

        ItemStack miningRootIcon = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta miningRootMeta = miningRootIcon.getItemMeta();
        miningRootMeta.displayName(Component.text("Mining Progression"));
        miningRootIcon.setItemMeta(miningRootMeta);

        tree.tab("mining/root")
                .title("<aqua><bold>Mining")
                .description("<gray>Complete mining challenges")
                .icon(miningRootIcon)
                .background("minecraft:textures/gui/advancements/backgrounds/stone.png")
                .frame(AdvancementTree.Frame.TASK)
                .position(0.0F, 0.0F)
                .showToast(false)
                .announceToChat(false)
                .hidden(false)
                .add();

        tree.advancement("mining/first_block", "mining/root")
                .title("<yellow>Getting Started")
                .description("<gray>Break your first block")
                .icon(Material.STONE_PICKAXE)
                .frame(AdvancementTree.Frame.TASK)
                .position(2.0F, 0.0F)
                .showToast(true)
                .announceToChat(false)
                .hidden(false)
                .add();

        tree.advancement("mining/one_hundred_blocks", "mining/first_block")
                .title("<gold>Dedicated Miner")
                .description("<gray>Break 100 blocks")
                .icon(Material.IRON_PICKAXE)
                .frame(AdvancementTree.Frame.GOAL)
                .position(4.0F, 0.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(false)
                .add();

        tree.advancement("mining/first_diamond", "mining/first_block")
                .title("<aqua>Diamonds!")
                .description("<gray>Mine your first diamond ore")
                .icon(Material.DIAMOND)
                .frame(AdvancementTree.Frame.GOAL)
                .position(4.0F, 2.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(false)
                .add();

        tree.advancement("mining/ancient_debris", "mining/first_diamond")
                .title("<dark_purple>Ancient Discovery")
                .description("<gray>Mine ancient debris")
                .icon(Material.ANCIENT_DEBRIS)
                .frame(AdvancementTree.Frame.CHALLENGE)
                .position(6.0F, 2.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(true)
                .add();

        tree.tab("combat/root")
                .title("<red><bold>Combat")
                .description("<gray>Complete combat challenges")
                .icon(Material.DIAMOND_SWORD)
                .background("minecraft:textures/gui/advancements/backgrounds/adventure.png")
                .frame(AdvancementTree.Frame.TASK)
                .position(0.0F, 0.0F)
                .showToast(false)
                .announceToChat(false)
                .hidden(false)
                .add();

        tree.advancement("combat/first_kill", "combat/root")
                .title("<red>First Blood")
                .description("<gray>Kill another player")
                .icon(Material.IRON_SWORD)
                .frame(AdvancementTree.Frame.TASK)
                .position(2.0F, 0.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(false)
                .add();

        tree.advancement("combat/ten_kills", "combat/first_kill")
                .title("<dark_red>Experienced Fighter")
                .description("<gray>Kill 10 players")
                .icon(Material.DIAMOND_SWORD)
                .frame(AdvancementTree.Frame.GOAL)
                .position(4.0F, 0.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(false)
                .add();

        tree.advancement("combat/secret_challenge", "combat/ten_kills")
                .title("<light_purple>Secret Challenge")
                .description("<gray>Complete the hidden combat challenge")
                .icon(Material.NETHER_STAR)
                .frame(AdvancementTree.Frame.CHALLENGE)
                .position(6.0F, 0.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(true)
                .add();

        return tree;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(PoaLib.LIB_INSTANCE, () -> {
            if (!player.isOnline())
                return;

            tree.showExclusive(player);
            tree.complete(player, "mining/root", false);
            tree.complete(player, "combat/root", false);
            tree.selectTab(player, "mining/root");
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        tree.forget(player);
        blocksMined.remove(player.getUniqueId());
        playerKills.remove(player.getUniqueId());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        tree.complete(player, "mining/first_block");

        int amount = blocksMined.merge(player.getUniqueId(), 1, Integer::sum);

        if (amount >= 100)
            tree.complete(player, "mining/one_hundred_blocks");

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE ->
                    tree.complete(player, "mining/first_diamond");

            case ANCIENT_DEBRIS ->
                    tree.complete(player, "mining/ancient_debris");

            default -> {
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getPlayer().getKiller();

        if (killer == null)
            return;

        tree.complete(killer, "combat/first_kill");

        int kills = playerKills.merge(killer.getUniqueId(), 1, Integer::sum);

        if (kills >= 10)
            tree.complete(killer, "combat/ten_kills");
    }

    public AdvancementTree getTree() {
        return tree;
    }

    public void showAlongsideExistingAdvancements(Player player) {
        tree.show(player);
    }

    public void replaceAllExistingAdvancements(Player player) {
        tree.showExclusive(player);
    }

    public void replaceAllExistingAdvancements(
            Player player,
            Collection<String> completedAdvancements
    ) {
        tree.showExclusive(player, completedAdvancements);
    }

    public void hideCustomAdvancements(Player player) {
        tree.hide(player);
    }

    public void refreshTree(Player player) {
        tree.refresh(player);
    }

    public void refreshExclusiveTree(Player player) {
        tree.refreshExclusive(player);
    }

    public void openMiningTab(Player player) {
        tree.selectTab(player, "mining/root");
    }

    public void openCombatTab(Player player) {
        tree.selectTab(player, "combat/root");
    }

    public boolean grantAdvancement(Player player, String path) {
        return tree.complete(player, path);
    }

    public boolean grantWithoutToast(Player player, String path) {
        return tree.complete(player, path, false);
    }

    public boolean grantWithoutAnnouncement(Player player, String path) {
        NamespacedKey key = new NamespacedKey("poa", path);
        return tree.complete(player, key, true, false);
    }

    public boolean grantSilently(Player player, String path) {
        NamespacedKey key = new NamespacedKey("poa", path);
        return tree.complete(player, key, false, false);
    }

    public boolean revokeAdvancement(Player player, String path) {
        return tree.revoke(player, path);
    }

    public void setAdvancementState(
            Player player,
            String path,
            boolean completed,
            boolean showToast
    ) {
        tree.setComplete(player, path, completed, showToast);
    }

    public boolean hasAdvancement(Player player, String path) {
        return tree.isComplete(player, path);
    }

    public void resetPlayerAdvancements(Player player) {
        tree.clearProgress(player);
        tree.complete(player, "mining/root", false);
        tree.complete(player, "combat/root", false);
    }

    public Set<NamespacedKey> getCompletedAdvancements(Player player) {
        return tree.getCompleted(player);
    }

    public List<String> getCompletedPaths(Player player) {
        return tree.getCompleted(player)
                .stream()
                .filter(key -> key.getNamespace().equals(tree.getNamespace()))
                .map(NamespacedKey::getKey)
                .collect(Collectors.toList());
    }

    public void loadCompletedPaths(
            Player player,
            Collection<String> completedPaths
    ) {
        tree.showExclusive(player, completedPaths);
    }

    public void removeAdvancementFromClient(Player player, String path) {
        tree.removeNode(player, path);
    }

    public void sendAdvancementBackToClient(Player player, String path) {
        tree.updateNode(player, path);
    }

    public void changeFirstDiamondDisplay(Player player) {
        AdvancementTree.Node node = tree.get("mining/first_diamond");

        node.edit(tree)
                .title("<aqua><bold>Diamond Hunter")
                .description("<gray>You discovered valuable diamond ore")
                .icon(Material.DIAMOND_BLOCK)
                .frame(AdvancementTree.Frame.CHALLENGE)
                .position(5.0F, 2.0F)
                .showToast(true)
                .announceToChat(true)
                .hidden(false)
                .add();

        tree.updateNode(player, "mining/first_diamond");
    }

    public void showBasicToast(Player player) {
        AdvancementToast.show(
                player,
                "<green><bold>Success",
                "<gray>Your action was completed",
                Material.EMERALD
        );
    }

    public void showGoalToast(Player player) {
        AdvancementToast.show(
                player,
                "<gold><bold>Level Up",
                "<gray>You reached the next level",
                Material.EXPERIENCE_BOTTLE,
                AdvancementTree.Frame.GOAL
        );
    }

    public void showChallengeToast(Player player) {
        AdvancementToast.show(
                player,
                "<light_purple><bold>Challenge Complete",
                "<gray>You completed a difficult challenge",
                Material.NETHER_STAR,
                AdvancementTree.Frame.CHALLENGE
        );
    }

    public void showCustomItemToast(Player player, ItemStack item) {
        AdvancementToast.builder()
                .namespace("poa")
                .title("<aqua><bold>Item Unlocked")
                .description("<gray>You unlocked a custom item")
                .icon(item)
                .frame(AdvancementTree.Frame.GOAL)
                .removeAfterTicks(20L)
                .show(player);
    }

    public void showToastToPlayers(
            Collection<? extends Player> players
    ) {
        AdvancementToast.builder()
                .namespace("poa")
                .title("<gold><bold>Server Event")
                .description("<gray>A server event has started")
                .icon(Material.BEACON)
                .frame(AdvancementTree.Frame.CHALLENGE)
                .removeAfterTicks(20L)
                .show(players);
    }

    public void showToastToEveryone() {
        AdvancementToast.builder()
                .namespace("poa")
                .title("<yellow><bold>Announcement")
                .description("<gray>A global event has started")
                .icon(Material.BELL)
                .frame(AdvancementTree.Frame.TASK)
                .show(Bukkit.getOnlinePlayers());
    }

    public void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (tree.isShown(player))
                tree.hide(player);
        }
    }
}