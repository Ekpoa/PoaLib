package poa.poalib.advancements;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import poa.poalib.messages.Messages;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public final class AdvancementToast {

    private static final AtomicLong IDS = new AtomicLong();

    private AdvancementToast() {
    }

    public static ToastBuilder builder() {
        return new ToastBuilder();
    }

    public static void show(Player player, String title, String description, Material icon) {
        show(player, title, description, icon, AdvancementTree.Frame.TASK);
    }

    public static void show(Player player, String title, String description, Material icon, AdvancementTree.Frame frame) {
        builder()
                .title(title)
                .description(description)
                .icon(icon)
                .frame(frame)
                .show(player);
    }

    public static void show(Player player, Component title, Component description, ItemStack icon) {
        show(player, title, description, icon, AdvancementTree.Frame.TASK);
    }

    public static void show(Player player, Component title, Component description, ItemStack icon, AdvancementTree.Frame frame) {
        builder()
                .title(title)
                .description(description)
                .icon(icon)
                .frame(frame)
                .show(player);
    }

    public static final class ToastBuilder {

        private String namespace = "poalib";
        private Component title = Component.empty();
        private Component description = Component.empty();
        private ItemStack icon = new ItemStack(Material.STONE);
        private AdvancementTree.Frame frame = AdvancementTree.Frame.TASK;
        private long removeAfterTicks = 10L;

        public ToastBuilder namespace(String namespace) {
            if (namespace == null || namespace.isBlank())
                throw new IllegalArgumentException("Namespace cannot be empty");
            if (!Identifier.isValidNamespace(namespace))
                throw new IllegalArgumentException("Invalid namespace: " + namespace);
            this.namespace = namespace;
            return this;
        }

        public ToastBuilder title(String miniMessage) {
            return title(Messages.simpleComponent(miniMessage));
        }

        public ToastBuilder title(Component title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public ToastBuilder description(String miniMessage) {
            return description(Messages.simpleComponent(miniMessage));
        }

        public ToastBuilder description(Component description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        public ToastBuilder icon(Material material) {
            Objects.requireNonNull(material, "material");
            if (material.isAir())
                throw new IllegalArgumentException("Toast icon cannot be air");
            return icon(new ItemStack(material));
        }

        public ToastBuilder icon(ItemStack icon) {
            Objects.requireNonNull(icon, "icon");
            if (icon.getType().isAir())
                throw new IllegalArgumentException("Toast icon cannot be air");
            this.icon = icon.clone();
            return this;
        }

        public ToastBuilder frame(AdvancementTree.Frame frame) {
            this.frame = Objects.requireNonNull(frame, "frame");
            return this;
        }

        public ToastBuilder removeAfterTicks(long ticks) {
            if (ticks < 1L)
                throw new IllegalArgumentException("Removal delay must be at least 1 tick");
            this.removeAfterTicks = ticks;
            return this;
        }

        public void show(Player player) {
            Objects.requireNonNull(player, "player");
            if (!player.isOnline())
                return;

            long id = IDS.incrementAndGet();
            String unique = player.getUniqueId().toString().replace("-", "") + "/" + Long.toUnsignedString(id, 36);
            NamespacedKey rootKey = new NamespacedKey(namespace, "toast/" + unique + "/root");
            NamespacedKey toastKey = new NamespacedKey(namespace, "toast/" + unique + "/display");
            Identifier rootId = AdvancementTree.resource(rootKey);
            Identifier toastId = AdvancementTree.resource(toastKey);

            Advancement rootAdvancement = new Advancement(
                    Optional.empty(),
                    Optional.empty(),
                    AdvancementRewards.EMPTY,
                    Map.of(AdvancementTree.CRITERION_NAME, AdvancementTree.CRITERION),
                    AdvancementTree.REQUIREMENTS,
                    false
            );

            DisplayInfo display = new DisplayInfo(
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(icon),
                    PaperAdventure.asVanilla(title),
                    PaperAdventure.asVanilla(description),
                    Optional.empty(),
                    frame.type,
                    true,
                    false,
                    true
            );
            display.setLocation(0.0F, 0.0F);

            Advancement toastAdvancement = new Advancement(
                    Optional.of(rootId),
                    Optional.of(display),
                    AdvancementRewards.EMPTY,
                    Map.of(AdvancementTree.CRITERION_NAME, AdvancementTree.CRITERION),
                    AdvancementTree.REQUIREMENTS,
                    false
            );

            AdvancementHolder rootHolder = new AdvancementHolder(rootId, rootAdvancement);
            AdvancementHolder toastHolder = new AdvancementHolder(toastId, toastAdvancement);

            AdvancementTree.send(player, new ClientboundUpdateAdvancementsPacket(
                    false,
                    List.of(rootHolder, toastHolder),
                    Set.of(),
                    Map.of(
                            rootId, AdvancementTree.progress(false),
                            toastId, AdvancementTree.progress(false)
                    ),
                    false
            ));

            AdvancementTree.send(player, new ClientboundUpdateAdvancementsPacket(
                    false,
                    List.of(),
                    Set.of(),
                    Map.of(toastId, AdvancementTree.progress(true)),
                    true
            ));

            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AdvancementToast.class);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline())
                    return;

                AdvancementTree.send(player, new ClientboundUpdateAdvancementsPacket(
                        false,
                        List.of(),
                        Set.of(toastId, rootId),
                        Map.of(),
                        false
                ));
            }, removeAfterTicks);
        }

        public void show(Collection<? extends Player> players) {
            Objects.requireNonNull(players, "players");
            for (Player player : players)
                show(player);
        }

        public void show(Player... players) {
            Objects.requireNonNull(players, "players");
            for (Player player : players)
                show(player);
        }
    }
}
