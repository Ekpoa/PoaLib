package poa.poalib.advancements;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import poa.poalib.messages.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AdvancementTree {

    static final String CRITERION_NAME = "done";
    static final AdvancementRequirements REQUIREMENTS = AdvancementRequirements.allOf(List.of(CRITERION_NAME));
    static final Criterion<ImpossibleTrigger.TriggerInstance> CRITERION = new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance());
    static final NamespacedKey DEFAULT_BACKGROUND = key("minecraft:textures/gui/advancements/backgrounds/stone.png");

    private final String namespace;
    private final LinkedHashMap<NamespacedKey, Node> nodes = new LinkedHashMap<>();
    private final Map<UUID, Set<NamespacedKey>> completed = new ConcurrentHashMap<>();
    private final Set<UUID> shown = ConcurrentHashMap.newKeySet();

    public AdvancementTree(String namespace) {
        if (namespace == null || namespace.isBlank())
            throw new IllegalArgumentException("Namespace cannot be empty");

        if (!Identifier.isValidNamespace(namespace))
            throw new IllegalArgumentException("Invalid namespace: " + namespace);

        this.namespace = namespace;
    }

    public static AdvancementTree create(String namespace) {
        return new AdvancementTree(namespace);
    }

    public String getNamespace() {
        return namespace;
    }

    public NodeBuilder tab(String path) {
        return new NodeBuilder(this, localKey(path), null, true);
    }

    public NodeBuilder advancement(String path, String parentPath) {
        return new NodeBuilder(this, localKey(path), localKey(parentPath), false);
    }

    public NodeBuilder advancement(String path, NamespacedKey parent) {
        return new NodeBuilder(this, localKey(path), Objects.requireNonNull(parent, "parent"), false);
    }

    public NodeBuilder advancement(NamespacedKey key, NamespacedKey parent) {
        return new NodeBuilder(this, Objects.requireNonNull(key, "key"), Objects.requireNonNull(parent, "parent"), false);
    }

    public AdvancementTree add(Node node) {
        Objects.requireNonNull(node, "node");
        nodes.put(node.key, node);
        return this;
    }

    public boolean contains(String path) {
        return nodes.containsKey(localKey(path));
    }

    public boolean contains(NamespacedKey key) {
        return nodes.containsKey(key);
    }

    public Node get(String path) {
        return nodes.get(localKey(path));
    }

    public Node get(NamespacedKey key) {
        return nodes.get(key);
    }

    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public Set<NamespacedKey> getKeys() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    public AdvancementTree removeDefinition(String path) {
        return removeDefinition(localKey(path));
    }

    public AdvancementTree removeDefinition(NamespacedKey key) {
        nodes.remove(key);
        for (Set<NamespacedKey> values : completed.values())
            values.remove(key);
        return this;
    }

    public AdvancementTree clearDefinitions() {
        nodes.clear();
        completed.clear();
        shown.clear();
        return this;
    }

    public void show(Player player) {
        sendTree(player, false);
    }

    public void showExclusive(Player player) {
        sendTree(player, true);
    }

    public void show(Player player, Collection<String> completedPaths) {
        Objects.requireNonNull(completedPaths, "completedPaths");
        Set<NamespacedKey> values = completionSet(player);
        values.clear();

        for (String path : completedPaths) {
            NamespacedKey key = localKey(path);
            requireNode(key);
            values.add(key);
        }

        show(player);
    }

    public void showExclusive(Player player, Collection<String> completedPaths) {
        Objects.requireNonNull(completedPaths, "completedPaths");
        Set<NamespacedKey> values = completionSet(player);
        values.clear();

        for (String path : completedPaths) {
            NamespacedKey key = localKey(path);
            requireNode(key);
            values.add(key);
        }

        showExclusive(player);
    }

    public void hide(Player player) {
        requirePlayer(player);
        Set<Identifier> removed = new LinkedHashSet<>();

        for (NamespacedKey key : nodes.keySet())
            removed.add(resource(key));

        if (!removed.isEmpty())
            send(player, new ClientboundUpdateAdvancementsPacket(false, List.of(), removed, Map.of(), false));

        shown.remove(player.getUniqueId());
    }

    public void refresh(Player player) {
        show(player);
    }

    public void refreshExclusive(Player player) {
        showExclusive(player);
    }

    public boolean complete(Player player, String path) {
        return complete(player, localKey(path), true, true);
    }

    public boolean complete(Player player, String path, boolean showToast) {
        return complete(player, localKey(path), showToast, true);
    }

    public boolean complete(Player player, NamespacedKey key) {
        return complete(player, key, true, true);
    }

    public boolean complete(Player player, NamespacedKey key, boolean showToast) {
        return complete(player, key, showToast, true);
    }

    public boolean complete(Player player, NamespacedKey key, boolean showToast, boolean announceToChat) {
        requirePlayer(player);
        Node node = requireNode(key);
        ensureShown(player);

        boolean changed = completionSet(player).add(key);
        sendProgress(player, key, true, changed && showToast);

        if (changed && announceToChat && node.announceToChat)
            Bukkit.broadcast(Component.translatable(node.frame.announcementKey, player.displayName(), node.title));

        return changed;
    }

    public boolean revoke(Player player, String path) {
        return revoke(player, localKey(path));
    }

    public boolean revoke(Player player, NamespacedKey key) {
        requirePlayer(player);
        requireNode(key);
        ensureShown(player);

        boolean changed = completionSet(player).remove(key);
        sendProgress(player, key, false, false);
        return changed;
    }

    public void setComplete(Player player, String path, boolean complete) {
        setComplete(player, localKey(path), complete, complete);
    }

    public void setComplete(Player player, String path, boolean complete, boolean showToast) {
        setComplete(player, localKey(path), complete, showToast);
    }

    public void setComplete(Player player, NamespacedKey key, boolean complete, boolean showToast) {
        if (complete)
            complete(player, key, showToast, true);
        else
            revoke(player, key);
    }

    public boolean isComplete(Player player, String path) {
        return isComplete(player.getUniqueId(), localKey(path));
    }

    public boolean isComplete(Player player, NamespacedKey key) {
        return isComplete(player.getUniqueId(), key);
    }

    public boolean isComplete(UUID uuid, String path) {
        return isComplete(uuid, localKey(path));
    }

    public boolean isComplete(UUID uuid, NamespacedKey key) {
        Set<NamespacedKey> values = completed.get(uuid);
        return values != null && values.contains(key);
    }

    public Set<NamespacedKey> getCompleted(Player player) {
        return getCompleted(player.getUniqueId());
    }

    public Set<NamespacedKey> getCompleted(UUID uuid) {
        Set<NamespacedKey> values = completed.get(uuid);
        if (values == null)
            return Set.of();
        return Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }

    public void clearProgress(Player player) {
        Set<NamespacedKey> values = completionSet(player);
        values.clear();

        if (!shown.contains(player.getUniqueId()))
            return;

        Map<Identifier, AdvancementProgress> progress = new LinkedHashMap<>();
        for (NamespacedKey key : nodes.keySet())
            progress.put(resource(key), progress(false));

        send(player, new ClientboundUpdateAdvancementsPacket(false, List.of(), Set.of(), progress, false));
    }

    public void clearStoredProgress(UUID uuid) {
        completed.remove(uuid);
    }

    public void forget(Player player) {
        shown.remove(player.getUniqueId());
    }

    public void forget(UUID uuid) {
        shown.remove(uuid);
    }

    public boolean isShown(Player player) {
        return shown.contains(player.getUniqueId());
    }

    public void selectTab(Player player, String tabPath) {
        selectTab(player, localKey(tabPath));
    }

    public void selectTab(Player player, NamespacedKey tabKey) {
        requirePlayer(player);
        Node node = requireNode(tabKey);
        if (node.parent != null)
            throw new IllegalArgumentException(tabKey + " is not a root tab");

        ensureShown(player);
        send(player, new ClientboundSelectAdvancementsTabPacket(resource(tabKey)));
    }

    public void updateNode(Player player, String path) {
        updateNode(player, localKey(path));
    }

    public void updateNode(Player player, NamespacedKey key) {
        requirePlayer(player);
        Node node = requireNode(key);
        AdvancementProgress progress = progress(isComplete(player, key));
        send(player, new ClientboundUpdateAdvancementsPacket(false, List.of(holder(node)), Set.of(), Map.of(resource(key), progress), false));
    }

    public void removeNode(Player player, String path) {
        removeNode(player, localKey(path));
    }

    public void removeNode(Player player, NamespacedKey key) {
        requirePlayer(player);
        requireNode(key);
        send(player, new ClientboundUpdateAdvancementsPacket(false, List.of(), Set.of(resource(key)), Map.of(), false));
    }

    public void validate() {
        orderedNodes();
    }

    private void sendTree(Player player, boolean clearCurrent) {
        requirePlayer(player);
        List<Node> ordered = orderedNodes();
        List<AdvancementHolder> added = new ArrayList<>(ordered.size());
        Map<Identifier, AdvancementProgress> progress = new LinkedHashMap<>();
        Set<NamespacedKey> completedKeys = completionSet(player);

        for (Node node : ordered) {
            added.add(holder(node));
            progress.put(resource(node.key), progress(completedKeys.contains(node.key)));
        }

        send(player, new ClientboundUpdateAdvancementsPacket(clearCurrent, added, Set.of(), progress, false));
        shown.add(player.getUniqueId());
    }

    private void ensureShown(Player player) {
        if (!shown.contains(player.getUniqueId()))
            show(player);
    }

    private void sendProgress(Player player, NamespacedKey key, boolean complete, boolean showToast) {
        send(player, new ClientboundUpdateAdvancementsPacket(false, List.of(), Set.of(), Map.of(resource(key), progress(complete)), showToast));
    }

    private List<Node> orderedNodes() {
        List<Node> ordered = new ArrayList<>(nodes.size());
        Set<NamespacedKey> visiting = new HashSet<>();
        Set<NamespacedKey> visited = new HashSet<>();

        for (Node node : nodes.values())
            visit(node, visiting, visited, ordered);

        return ordered;
    }

    private void visit(Node node, Set<NamespacedKey> visiting, Set<NamespacedKey> visited, List<Node> ordered) {
        if (visited.contains(node.key))
            return;

        if (!visiting.add(node.key))
            throw new IllegalStateException("Circular advancement parent chain at " + node.key);

        if (node.parent != null) {
            Node parent = nodes.get(node.parent);
            if (parent != null)
                visit(parent, visiting, visited, ordered);
            else if (node.parent.getNamespace().equals(namespace))
                throw new IllegalStateException("Missing parent " + node.parent + " for " + node.key);
        }

        visiting.remove(node.key);
        visited.add(node.key);
        ordered.add(node);
    }

    private AdvancementHolder holder(Node node) {
        NamespacedKey background = node.background;
        if (node.parent == null && background == null)
            background = DEFAULT_BACKGROUND;

        DisplayInfo display = new DisplayInfo(
                CraftItemStack.asNMSCopy(node.icon),
                PaperAdventure.asVanilla(node.title),
                PaperAdventure.asVanilla(node.description),
                Optional.ofNullable(background).map(AdvancementTree::texture),
                node.frame.type,
                node.showToast,
                node.announceToChat,
                node.hidden
        );
        display.setLocation(node.x, node.y);

        Advancement advancement = new Advancement(
                Optional.ofNullable(node.parent).map(AdvancementTree::resource),
                Optional.of(display),
                AdvancementRewards.EMPTY,
                Map.of(CRITERION_NAME, CRITERION),
                REQUIREMENTS,
                false
        );

        return new AdvancementHolder(resource(node.key), advancement);
    }

    private Set<NamespacedKey> completionSet(Player player) {
        return completed.computeIfAbsent(player.getUniqueId(), ignored -> ConcurrentHashMap.newKeySet());
    }

    private Node requireNode(NamespacedKey key) {
        Node node = nodes.get(key);
        if (node == null)
            throw new IllegalArgumentException("Unknown advancement: " + key);
        return node;
    }

    private NamespacedKey localKey(String path) {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Advancement path cannot be empty");
        return new NamespacedKey(namespace, path);
    }

    private static void requirePlayer(Player player) {
        Objects.requireNonNull(player, "player");
        if (!player.isOnline())
            throw new IllegalArgumentException("Player must be online");
    }

    static AdvancementProgress progress(boolean complete) {
        AdvancementProgress progress = new AdvancementProgress();
        progress.update(REQUIREMENTS);
        if (complete)
            progress.grantProgress(CRITERION_NAME);
        return progress;
    }

    static Identifier resource(NamespacedKey key) {
        return Identifier.fromNamespaceAndPath(key.getNamespace(), key.getKey());
    }

    static ClientAsset.ResourceTexture texture(NamespacedKey key) {
        return new ClientAsset.ResourceTexture(resource(key));
    }

    static void send(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    static NamespacedKey key(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Key cannot be empty");

        int separator = value.indexOf(':');
        if (separator == -1)
            return new NamespacedKey("minecraft", value);

        return new NamespacedKey(value.substring(0, separator), value.substring(separator + 1));
    }

    public enum Frame {
        TASK(AdvancementType.TASK, "chat.type.advancement.task"),
        GOAL(AdvancementType.GOAL, "chat.type.advancement.goal"),
        CHALLENGE(AdvancementType.CHALLENGE, "chat.type.advancement.challenge");

        final AdvancementType type;
        final String announcementKey;

        Frame(AdvancementType type, String announcementKey) {
            this.type = type;
            this.announcementKey = announcementKey;
        }
    }

    public static final class Node {

        private final NamespacedKey key;
        private final NamespacedKey parent;
        private final Component title;
        private final Component description;
        private final ItemStack icon;
        private final NamespacedKey background;
        private final Frame frame;
        private final boolean showToast;
        private final boolean announceToChat;
        private final boolean hidden;
        private final float x;
        private final float y;

        private Node(NamespacedKey key, NamespacedKey parent, Component title, Component description, ItemStack icon,
                     NamespacedKey background, Frame frame, boolean showToast, boolean announceToChat,
                     boolean hidden, float x, float y) {
            this.key = key;
            this.parent = parent;
            this.title = title;
            this.description = description;
            this.icon = icon.clone();
            this.background = background;
            this.frame = frame;
            this.showToast = showToast;
            this.announceToChat = announceToChat;
            this.hidden = hidden;
            this.x = x;
            this.y = y;
        }

        public NamespacedKey getKey() {
            return key;
        }

        public NamespacedKey getParent() {
            return parent;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public Component getTitle() {
            return title;
        }

        public Component getDescription() {
            return description;
        }

        public ItemStack getIcon() {
            return icon.clone();
        }

        public NamespacedKey getBackground() {
            return background;
        }

        public Frame getFrame() {
            return frame;
        }

        public boolean shouldShowToast() {
            return showToast;
        }

        public boolean shouldAnnounceToChat() {
            return announceToChat;
        }

        public boolean isHidden() {
            return hidden;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public NodeBuilder edit(AdvancementTree tree) {
            return new NodeBuilder(tree, key, parent, parent == null)
                    .title(title)
                    .description(description)
                    .icon(icon)
                    .background(background)
                    .frame(frame)
                    .showToast(showToast)
                    .announceToChat(announceToChat)
                    .hidden(hidden)
                    .position(x, y);
        }
    }

    public static final class NodeBuilder {

        private final AdvancementTree tree;
        private final NamespacedKey key;
        private NamespacedKey parent;
        private Component title = Component.empty();
        private Component description = Component.empty();
        private ItemStack icon = new ItemStack(Material.STONE);
        private NamespacedKey background;
        private Frame frame = Frame.TASK;
        private boolean showToast = true;
        private boolean announceToChat;
        private boolean hidden;
        private float x;
        private float y;

        private NodeBuilder(AdvancementTree tree, NamespacedKey key, NamespacedKey parent, boolean root) {
            this.tree = tree;
            this.key = key;
            this.parent = parent;
            if (root)
                this.background = DEFAULT_BACKGROUND;
        }

        public NodeBuilder parent(String path) {
            this.parent = tree.localKey(path);
            return this;
        }

        public NodeBuilder parent(NamespacedKey parent) {
            this.parent = parent;
            return this;
        }

        public NodeBuilder root() {
            this.parent = null;
            return this;
        }

        public NodeBuilder title(String miniMessage) {
            return title(Messages.simpleComponent(miniMessage));
        }

        public NodeBuilder title(Component title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public NodeBuilder description(String miniMessage) {
            return description(Messages.simpleComponent(miniMessage));
        }

        public NodeBuilder description(Component description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        public NodeBuilder icon(Material material) {
            Objects.requireNonNull(material, "material");
            if (material.isAir())
                throw new IllegalArgumentException("Advancement icon cannot be air");
            return icon(new ItemStack(material));
        }

        public NodeBuilder icon(ItemStack icon) {
            Objects.requireNonNull(icon, "icon");
            if (icon.getType().isAir())
                throw new IllegalArgumentException("Advancement icon cannot be air");
            this.icon = icon.clone();
            return this;
        }

        public NodeBuilder background(String key) {
            return background(AdvancementTree.key(key));
        }

        public NodeBuilder background(NamespacedKey background) {
            this.background = background;
            return this;
        }

        public NodeBuilder noBackground() {
            this.background = null;
            return this;
        }

        public NodeBuilder frame(Frame frame) {
            this.frame = Objects.requireNonNull(frame, "frame");
            return this;
        }

        public NodeBuilder showToast(boolean showToast) {
            this.showToast = showToast;
            return this;
        }

        public NodeBuilder announceToChat(boolean announceToChat) {
            this.announceToChat = announceToChat;
            return this;
        }

        public NodeBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public NodeBuilder position(float x, float y) {
            if (!Float.isFinite(x) || !Float.isFinite(y))
                throw new IllegalArgumentException("Position must be finite");
            this.x = x;
            this.y = y;
            return this;
        }

        public Node build() {
            return new Node(key, parent, title, description, icon, background, frame, showToast, announceToChat, hidden, x, y);
        }

        public AdvancementTree add() {
            return tree.add(build());
        }
    }
}
