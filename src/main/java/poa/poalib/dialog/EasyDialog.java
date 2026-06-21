package poa.poalib.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class EasyDialog {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private Component title = Component.empty();
    private Component externalTitle;
    private boolean canCloseWithEscape = true;
    private boolean pause;
    private DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.CLOSE;

    private final List<DialogBody> bodies = new ArrayList<>();
    private final List<DialogInput> inputs = new ArrayList<>();
    private final Set<String> inputKeys = new HashSet<>();
    private final List<Button> buttons = new ArrayList<>();

    private Button exitButton;
    private Mode mode = Mode.AUTO;
    private int columns = 1;
    private int buttonWidth = 150;
    private RegistrySet<Dialog> dialogSet;
    private DialogType rawType;

    public EasyDialog() {
    }

    public EasyDialog(String title) {
        this.title = component(title);
    }

    public EasyDialog(Component title) {
        this.title = Objects.requireNonNull(title, "title");
    }

    public EasyDialog title(String title) {
        return title(component(title));
    }

    public EasyDialog title(Component title) {
        this.title = Objects.requireNonNull(title, "title");
        return this;
    }

    public EasyDialog externalTitle(String externalTitle) {
        return externalTitle(component(externalTitle));
    }

    public EasyDialog externalTitle(Component externalTitle) {
        this.externalTitle = externalTitle;
        return this;
    }

    public EasyDialog clearExternalTitle() {
        this.externalTitle = null;
        return this;
    }

    public EasyDialog canCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
        return this;
    }

    public EasyDialog pause(boolean pause) {
        this.pause = pause;
        return this;
    }

    public EasyDialog afterAction(DialogBase.DialogAfterAction afterAction) {
        this.afterAction = Objects.requireNonNull(afterAction, "afterAction");
        return this;
    }

    public EasyDialog closeAfterAction() {
        return afterAction(DialogBase.DialogAfterAction.CLOSE);
    }

    public EasyDialog keepOpenAfterAction() {
        return afterAction(DialogBase.DialogAfterAction.NONE);
    }

    public EasyDialog waitAfterAction() {
        return afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE);
    }

    public EasyDialog addBody(DialogBody body) {
        bodies.add(Objects.requireNonNull(body, "body"));
        return this;
    }

    public EasyDialog clearBodies() {
        bodies.clear();
        return this;
    }

    public EasyDialog message(String message) {
        return message(component(message));
    }

    public EasyDialog message(Component message) {
        return addBody(DialogBody.plainMessage(Objects.requireNonNull(message, "message")));
    }

    public EasyDialog message(String message, int width) {
        return message(component(message), width);
    }

    public EasyDialog message(Component message, int width) {
        checkRange(width, 1, 1024, "width");
        return addBody(DialogBody.plainMessage(Objects.requireNonNull(message, "message"), width));
    }

    public EasyDialog item(ItemStack item) {
        return addBody(DialogBody.item(Objects.requireNonNull(item, "item")).build());
    }

    public EasyDialog item(ItemStack item, String description) {
        return item(item, component(description));
    }

    public EasyDialog item(ItemStack item, Component description) {
        return item(item, description, 200, true, true, 16, 16);
    }

    public EasyDialog item(
            ItemStack item,
            String description,
            int descriptionWidth,
            boolean showDecorations,
            boolean showTooltip,
            int width,
            int height
    ) {
        return item(item, component(description), descriptionWidth, showDecorations, showTooltip, width, height);
    }

    public EasyDialog item(
            ItemStack item,
            Component description,
            int descriptionWidth,
            boolean showDecorations,
            boolean showTooltip,
            int width,
            int height
    ) {
        checkRange(descriptionWidth, 1, 1024, "descriptionWidth");
        checkRange(width, 1, 256, "width");
        checkRange(height, 1, 256, "height");

        return addBody(DialogBody.item(
                Objects.requireNonNull(item, "item"),
                description == null ? null : DialogBody.plainMessage(description, descriptionWidth),
                showDecorations,
                showTooltip,
                width,
                height
        ));
    }

    public EasyDialog addInput(DialogInput input) {
        Objects.requireNonNull(input, "input");
        registerInputKey(input.key());
        inputs.add(input);
        return this;
    }

    public EasyDialog clearInputs() {
        inputs.clear();
        inputKeys.clear();
        return this;
    }

    public EasyDialog checkbox(String key, String label) {
        return checkbox(key, component(label));
    }

    public EasyDialog checkbox(String key, Component label) {
        return checkbox(key, label, false);
    }

    public EasyDialog checkbox(String key, String label, boolean initial) {
        return checkbox(key, component(label), initial);
    }

    public EasyDialog checkbox(String key, Component label, boolean initial) {
        return checkbox(key, label, initial, "true", "false");
    }

    public EasyDialog checkbox(
            String key,
            String label,
            boolean initial,
            String onTrue,
            String onFalse
    ) {
        return checkbox(key, component(label), initial, onTrue, onFalse);
    }

    public EasyDialog checkbox(
            String key,
            Component label,
            boolean initial,
            String onTrue,
            String onFalse
    ) {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(onTrue, "onTrue");
        Objects.requireNonNull(onFalse, "onFalse");
        registerInputKey(key);
        inputs.add(DialogInput.bool(
                key,
                label,
                initial,
                onTrue,
                onFalse
        ));
        return this;
    }

    public EasyDialog slider(String key, String label, float start, float end) {
        return slider(key, component(label), start, end);
    }

    public EasyDialog slider(String key, Component label, float start, float end) {
        return slider(key, label, 200, "%s: %s", start, end, null, null);
    }

    public EasyDialog slider(
            String key,
            String label,
            float start,
            float end,
            Float initial,
            Float step
    ) {
        return slider(key, component(label), 200, "%s: %s", start, end, initial, step);
    }

    public EasyDialog slider(
            String key,
            Component label,
            float start,
            float end,
            Float initial,
            Float step
    ) {
        return slider(key, label, 200, "%s: %s", start, end, initial, step);
    }

    public EasyDialog slider(
            String key,
            String label,
            int width,
            String labelFormat,
            float start,
            float end,
            Float initial,
            Float step
    ) {
        return slider(key, component(label), width, labelFormat, start, end, initial, step);
    }

    public EasyDialog slider(
            String key,
            Component label,
            int width,
            String labelFormat,
            float start,
            float end,
            Float initial,
            Float step
    ) {
        checkRange(width, 1, 1024, "width");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(labelFormat, "labelFormat");

        if (!Float.isFinite(start) || !Float.isFinite(end) || start >= end) {
            throw new IllegalArgumentException("start must be lower than end and both must be finite");
        }

        if (initial != null && (!Float.isFinite(initial) || initial < start || initial > end)) {
            throw new IllegalArgumentException("initial must be between start and end");
        }

        if (step != null && (!Float.isFinite(step) || step <= 0.0F)) {
            throw new IllegalArgumentException("step must be greater than 0");
        }

        registerInputKey(key);
        inputs.add(DialogInput.numberRange(
                key,
                width,
                label,
                labelFormat,
                start,
                end,
                initial,
                step
        ));
        return this;
    }

    public EasyDialog text(String key, String label) {
        return text(key, component(label));
    }

    public EasyDialog text(String key, Component label) {
        return text(key, label, 200, true, "", 32);
    }

    public EasyDialog text(
            String key,
            String label,
            int width,
            boolean labelVisible,
            String initial,
            int maxLength
    ) {
        return text(key, component(label), width, labelVisible, initial, maxLength);
    }

    public EasyDialog text(
            String key,
            Component label,
            int width,
            boolean labelVisible,
            String initial,
            int maxLength
    ) {
        return text(key, label, width, labelVisible, initial, maxLength, null, null);
    }

    public EasyDialog multilineText(
            String key,
            String label,
            int width,
            boolean labelVisible,
            String initial,
            int maxLength,
            Integer maxLines,
            Integer height
    ) {
        return text(key, component(label), width, labelVisible, initial, maxLength, maxLines, height);
    }

    public EasyDialog multilineText(
            String key,
            Component label,
            int width,
            boolean labelVisible,
            String initial,
            int maxLength,
            Integer maxLines,
            Integer height
    ) {
        return text(key, label, width, labelVisible, initial, maxLength, maxLines, height);
    }

    public EasyDialog text(
            String key,
            Component label,
            int width,
            boolean labelVisible,
            String initial,
            int maxLength,
            Integer maxLines,
            Integer height
    ) {
        checkRange(width, 1, 1024, "width");
        checkPositive(maxLength, "maxLength");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(initial, "initial");

        if (maxLines != null) {
            checkPositive(maxLines, "maxLines");
        }

        if (height != null) {
            checkRange(height, 1, 512, "height");
        }

        TextDialogInput.MultilineOptions multiline = maxLines == null && height == null
                ? null
                : TextDialogInput.MultilineOptions.create(maxLines, height);

        registerInputKey(key);
        inputs.add(DialogInput.text(
                key,
                width,
                label,
                labelVisible,
                initial,
                maxLength,
                multiline
        ));
        return this;
    }

    public EasyDialog singleOption(
            String key,
            String label,
            Option... options
    ) {
        return singleOption(key, component(label), List.of(options));
    }

    public EasyDialog singleOption(
            String key,
            Component label,
            Option... options
    ) {
        return singleOption(key, label, List.of(options));
    }

    public EasyDialog singleOption(
            String key,
            String label,
            List<Option> options
    ) {
        return singleOption(key, component(label), options);
    }

    public EasyDialog singleOption(
            String key,
            Component label,
            List<Option> options
    ) {
        return singleOption(key, label, options, 200, true);
    }

    public EasyDialog singleOption(
            String key,
            String label,
            List<Option> options,
            int width,
            boolean labelVisible
    ) {
        return singleOption(key, component(label), options, width, labelVisible);
    }

    public EasyDialog singleOption(
            String key,
            Component label,
            List<Option> options,
            int width,
            boolean labelVisible
    ) {
        checkRange(width, 1, 1024, "width");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(options, "options");

        if (options.isEmpty()) {
            throw new IllegalArgumentException("options cannot be empty");
        }

        long initialCount = options.stream().filter(Option::initial).count();
        if (initialCount > 1) {
            throw new IllegalArgumentException("only one option can be initially selected");
        }

        Set<String> ids = new HashSet<>();
        List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();

        for (Option option : options) {
            Objects.requireNonNull(option, "option");
            if (!ids.add(option.id())) {
                throw new IllegalArgumentException("duplicate option id: " + option.id());
            }
            entries.add(option.build());
        }

        registerInputKey(key);
        inputs.add(DialogInput.singleOption(
                key,
                width,
                entries,
                label,
                labelVisible
        ));
        return this;
    }

    public EasyDialog addButton(Button button) {
        buttons.add(Objects.requireNonNull(button, "button"));
        return this;
    }

    public EasyDialog addButton(String label) {
        return addButton(Button.of(label));
    }

    public EasyDialog addButton(Component label) {
        return addButton(Button.of(label));
    }

    public EasyDialog addButton(String label, DialogClick callback) {
        return addButton(Button.of(label).onClick(callback));
    }

    public EasyDialog addButton(Component label, DialogClick callback) {
        return addButton(Button.of(label).onClick(callback));
    }

    public EasyDialog addButton(String label, PlayerDialogClick callback) {
        return addButton(Button.of(label).onPlayerClick(callback));
    }

    public EasyDialog addButton(Component label, PlayerDialogClick callback) {
        return addButton(Button.of(label).onPlayerClick(callback));
    }

    public EasyDialog addButton(
            String label,
            String tooltip,
            int width,
            DialogClick callback
    ) {
        return addButton(Button.of(label).tooltip(tooltip).width(width).onClick(callback));
    }

    public EasyDialog addButton(
            Component label,
            Component tooltip,
            int width,
            DialogClick callback
    ) {
        return addButton(Button.of(label).tooltip(tooltip).width(width).onClick(callback));
    }

    public EasyDialog clearButtons() {
        buttons.clear();
        return this;
    }

    public EasyDialog exitButton(Button exitButton) {
        this.exitButton = Objects.requireNonNull(exitButton, "exitButton");
        return this;
    }

    public EasyDialog exitButton(String label) {
        return exitButton(Button.of(label));
    }

    public EasyDialog exitButton(Component label) {
        return exitButton(Button.of(label));
    }

    public EasyDialog clearExitButton() {
        this.exitButton = null;
        return this;
    }

    public EasyDialog autoType() {
        this.mode = Mode.AUTO;
        this.rawType = null;
        return this;
    }

    public EasyDialog notice() {
        this.mode = Mode.NOTICE;
        this.rawType = null;
        return this;
    }

    public EasyDialog confirmation() {
        this.mode = Mode.CONFIRMATION;
        this.rawType = null;
        return this;
    }

    public EasyDialog multiAction(int columns) {
        checkPositive(columns, "columns");
        this.mode = Mode.MULTI_ACTION;
        this.columns = columns;
        this.rawType = null;
        return this;
    }

    public EasyDialog serverLinks(int columns, int buttonWidth) {
        checkPositive(columns, "columns");
        checkRange(buttonWidth, 1, 1024, "buttonWidth");
        this.mode = Mode.SERVER_LINKS;
        this.columns = columns;
        this.buttonWidth = buttonWidth;
        this.rawType = null;
        return this;
    }

    public EasyDialog dialogList(
            RegistrySet<Dialog> dialogs,
            int columns,
            int buttonWidth
    ) {
        checkPositive(columns, "columns");
        checkRange(buttonWidth, 1, 1024, "buttonWidth");
        this.mode = Mode.DIALOG_LIST;
        this.dialogSet = Objects.requireNonNull(dialogs, "dialogs");
        this.columns = columns;
        this.buttonWidth = buttonWidth;
        this.rawType = null;
        return this;
    }

    public EasyDialog dialogList(
            Collection<? extends Dialog> anonymousDialogs,
            int columns,
            int buttonWidth
    ) {
        Objects.requireNonNull(anonymousDialogs, "anonymousDialogs");
        return dialogList(
                RegistrySet.valueSet(RegistryKey.DIALOG, anonymousDialogs),
                columns,
                buttonWidth
        );
    }

    public EasyDialog registeredDialogList(
            Collection<? extends Dialog> registeredDialogs,
            int columns,
            int buttonWidth
    ) {
        Objects.requireNonNull(registeredDialogs, "registeredDialogs");
        return dialogList(
                RegistrySet.keySetFromValues(RegistryKey.DIALOG, registeredDialogs),
                columns,
                buttonWidth
        );
    }

    public EasyDialog type(DialogType type) {
        this.mode = Mode.RAW;
        this.rawType = Objects.requireNonNull(type, "type");
        return this;
    }

    public Dialog build() {
        DialogBase base = DialogBase.builder(title)
                .externalTitle(externalTitle)
                .canCloseWithEscape(canCloseWithEscape)
                .pause(pause)
                .afterAction(afterAction)
                .body(List.copyOf(bodies))
                .inputs(List.copyOf(inputs))
                .build();

        DialogType type = buildType();

        return Dialog.create(builder -> builder.empty()
                .base(base)
                .type(type)
        );
    }

    public EasyDialog show(Audience audience) {
        Objects.requireNonNull(audience, "audience").showDialog(build());
        return this;
    }

    public EasyDialog show(Iterable<? extends Audience> audiences) {
        Objects.requireNonNull(audiences, "audiences");
        for (Audience audience : audiences) {
            show(audience);
        }
        return this;
    }

    public EasyDialog close(Audience audience) {
        Objects.requireNonNull(audience, "audience").closeDialog();
        return this;
    }

    public ClickEvent clickEvent() {
        return ClickEvent.showDialog(build());
    }

    public static Option option(String id, String display) {
        return new Option(id, component(display), false);
    }

    public static Option option(String id, String display, boolean initial) {
        return new Option(id, component(display), initial);
    }

    public static Option option(String id, Component display) {
        return new Option(id, display, false);
    }

    public static Option option(String id, Component display, boolean initial) {
        return new Option(id, display, initial);
    }

    public static Button button(String label) {
        return Button.of(label);
    }

    public static Button button(Component label) {
        return Button.of(label);
    }

    public static Component component(String miniMessage) {
        return MINI_MESSAGE.deserialize(Objects.requireNonNull(miniMessage, "miniMessage"));
    }

    private DialogType buildType() {
        List<ActionButton> builtButtons = buttons.stream().map(Button::build).toList();
        ActionButton builtExitButton = exitButton == null ? null : exitButton.build();

        return switch (mode) {
            case AUTO -> {
                if (builtButtons.isEmpty()) {
                    yield DialogType.notice();
                }
                if (builtButtons.size() == 1) {
                    yield DialogType.notice(builtButtons.getFirst());
                }
                yield DialogType.multiAction(builtButtons, builtExitButton, columns);
            }
            case NOTICE -> {
                if (builtButtons.size() > 1) {
                    throw new IllegalStateException("notice dialogs can only contain one action button");
                }
                yield builtButtons.isEmpty()
                        ? DialogType.notice()
                        : DialogType.notice(builtButtons.getFirst());
            }
            case CONFIRMATION -> {
                if (builtButtons.size() != 2) {
                    throw new IllegalStateException("confirmation dialogs require exactly two action buttons");
                }
                yield DialogType.confirmation(builtButtons.get(0), builtButtons.get(1));
            }
            case MULTI_ACTION -> {
                if (builtButtons.isEmpty()) {
                    throw new IllegalStateException("multi-action dialogs require at least one action button");
                }
                yield DialogType.multiAction(builtButtons, builtExitButton, columns);
            }
            case SERVER_LINKS -> DialogType.serverLinks(builtExitButton, columns, buttonWidth);
            case DIALOG_LIST -> {
                if (dialogSet == null || dialogSet.isEmpty()) {
                    throw new IllegalStateException("dialog-list dialogs require at least one dialog");
                }
                yield DialogType.dialogList(dialogSet, builtExitButton, columns, buttonWidth);
            }
            case RAW -> Objects.requireNonNull(rawType, "rawType");
        };
    }

    private void registerInputKey(String key) {
        Objects.requireNonNull(key, "key");
        if (key.isBlank()) {
            throw new IllegalArgumentException("input key cannot be blank");
        }
        if (!inputKeys.add(key)) {
            throw new IllegalArgumentException("duplicate input key: " + key);
        }
    }

    private static void checkPositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
    }

    private static void checkRange(int value, int minimum, int maximum, String name) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum);
        }
    }

    private enum Mode {
        AUTO,
        NOTICE,
        CONFIRMATION,
        MULTI_ACTION,
        DIALOG_LIST,
        SERVER_LINKS,
        RAW
    }

    @FunctionalInterface
    public interface DialogClick {
        void click(Response response);
    }

    @FunctionalInterface
    public interface PlayerDialogClick {
        void click(Player player, Response response);
    }

    @FunctionalInterface
    private interface ActionFactory {
        DialogAction create();
    }

    public static final class Button {

        private Component label;
        private Component tooltip;
        private int width = 150;
        private ActionFactory actionFactory = () -> null;

        private Button(Component label) {
            this.label = Objects.requireNonNull(label, "label");
        }

        public static Button of(String label) {
            return new Button(component(label));
        }

        public static Button of(Component label) {
            return new Button(label);
        }

        public Button label(String label) {
            return label(component(label));
        }

        public Button label(Component label) {
            this.label = Objects.requireNonNull(label, "label");
            return this;
        }

        public Button tooltip(String tooltip) {
            return tooltip(component(tooltip));
        }

        public Button tooltip(Component tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Button clearTooltip() {
            this.tooltip = null;
            return this;
        }

        public Button width(int width) {
            checkRange(width, 1, 1024, "width");
            this.width = width;
            return this;
        }

        public Button noAction() {
            this.actionFactory = () -> null;
            return this;
        }

        public Button action(DialogAction action) {
            Objects.requireNonNull(action, "action");
            this.actionFactory = () -> action;
            return this;
        }

        public Button onClick(DialogClick callback) {
            return onClick(1, ClickCallback.DEFAULT_LIFETIME, callback);
        }

        public Button onClickUnlimited(DialogClick callback) {
            return onClick(ClickCallback.UNLIMITED_USES, ClickCallback.DEFAULT_LIFETIME, callback);
        }

        public Button onClick(
                int uses,
                TemporalAmount lifetime,
                DialogClick callback
        ) {
            Objects.requireNonNull(lifetime, "lifetime");
            Objects.requireNonNull(callback, "callback");

            if (uses != ClickCallback.UNLIMITED_USES && uses <= 0) {
                throw new IllegalArgumentException("uses must be greater than 0 or ClickCallback.UNLIMITED_USES");
            }

            this.actionFactory = () -> DialogAction.customClick(
                    (view, audience) -> callback.click(new Response(view, audience)),
                    ClickCallback.Options.builder()
                            .uses(uses)
                            .lifetime(lifetime)
                            .build()
            );
            return this;
        }

        public Button onPlayerClick(PlayerDialogClick callback) {
            return onPlayerClick(1, ClickCallback.DEFAULT_LIFETIME, callback);
        }

        public Button onPlayerClickUnlimited(PlayerDialogClick callback) {
            return onPlayerClick(ClickCallback.UNLIMITED_USES, ClickCallback.DEFAULT_LIFETIME, callback);
        }

        public Button onPlayerClick(
                int uses,
                TemporalAmount lifetime,
                PlayerDialogClick callback
        ) {
            Objects.requireNonNull(callback, "callback");
            return onClick(uses, lifetime, response -> {
                Player player = response.playerOrNull();
                if (player != null) {
                    callback.click(player, response);
                }
            });
        }

        public Button commandTemplate(String template) {
            Objects.requireNonNull(template, "template");
            this.actionFactory = () -> DialogAction.commandTemplate(template);
            return this;
        }

        public Button staticAction(ClickEvent clickEvent) {
            Objects.requireNonNull(clickEvent, "clickEvent");
            this.actionFactory = () -> DialogAction.staticAction(clickEvent);
            return this;
        }

        public Button runCommand(String command) {
            return staticAction(ClickEvent.runCommand(command));
        }

        public Button suggestCommand(String command) {
            return staticAction(ClickEvent.suggestCommand(command));
        }

        public Button openUrl(String url) {
            return staticAction(ClickEvent.openUrl(url));
        }

        public Button openFile(String file) {
            return staticAction(ClickEvent.openFile(file));
        }

        public Button copyToClipboard(String text) {
            return staticAction(ClickEvent.copyToClipboard(text));
        }

        public Button changePage(int page) {
            return staticAction(ClickEvent.changePage(page));
        }

        public Button showDialog(DialogLike dialog) {
            return staticAction(ClickEvent.showDialog(dialog));
        }

        public Button showDialog(EasyDialog dialog) {
            Objects.requireNonNull(dialog, "dialog");
            this.actionFactory = () -> DialogAction.staticAction(ClickEvent.showDialog(dialog.build()));
            return this;
        }

        public Button customClick(Key id, BinaryTagHolder additions) {
            Objects.requireNonNull(id, "id");
            this.actionFactory = () -> DialogAction.customClick(id, additions);
            return this;
        }

        public Button customClick(String id, BinaryTagHolder additions) {
            return customClick(Key.key(id), additions);
        }

        public Button customStaticEvent(Key id, BinaryTagHolder data) {
            return staticAction(ClickEvent.custom(
                    Objects.requireNonNull(id, "id"),
                    Objects.requireNonNull(data, "data")
            ));
        }

        public Button customStaticEvent(String id, BinaryTagHolder data) {
            return customStaticEvent(Key.key(id), data);
        }

        public ActionButton build() {
            return ActionButton.create(label, tooltip, width, actionFactory.create());
        }
    }

    public record Option(String id, Component display, boolean initial) {

        public Option {
            Objects.requireNonNull(id, "id");
            if (id.isBlank()) {
                throw new IllegalArgumentException("option id cannot be blank");
            }
            Objects.requireNonNull(display, "display");
        }

        private SingleOptionDialogInput.OptionEntry build() {
            return SingleOptionDialogInput.OptionEntry.create(id, display, initial);
        }
    }

    public static final class Response {

        private final DialogResponseView response;
        private final Audience audience;

        private Response(DialogResponseView response, Audience audience) {
            this.response = Objects.requireNonNull(response, "response");
            this.audience = Objects.requireNonNull(audience, "audience");
        }

        public DialogResponseView raw() {
            return response;
        }

        public Audience audience() {
            return audience;
        }

        public Player playerOrNull() {
            return audience instanceof Player player ? player : null;
        }

        public Player player() {
            Player player = playerOrNull();
            if (player == null) {
                throw new IllegalStateException("the dialog response audience is not an in-game player");
            }
            return player;
        }

        public String textOrNull(String key) {
            return response.getText(key);
        }

        public String text(String key) {
            String value = textOrNull(key);
            if (value == null) {
                throw missingValue(key, "text");
            }
            return value;
        }

        public String text(String key, String fallback) {
            String value = textOrNull(key);
            return value == null ? fallback : value;
        }

        public String optionOrNull(String key) {
            return textOrNull(key);
        }

        public String option(String key) {
            return text(key);
        }

        public String option(String key, String fallback) {
            return text(key, fallback);
        }

        public Boolean booleanOrNull(String key) {
            return response.getBoolean(key);
        }

        public boolean bool(String key) {
            Boolean value = booleanOrNull(key);
            if (value == null) {
                throw missingValue(key, "boolean");
            }
            return value;
        }

        public boolean bool(String key, boolean fallback) {
            Boolean value = booleanOrNull(key);
            return value == null ? fallback : value;
        }

        public Float numberOrNull(String key) {
            return response.getFloat(key);
        }

        public float number(String key) {
            Float value = numberOrNull(key);
            if (value == null) {
                throw missingValue(key, "number");
            }
            return value;
        }

        public float number(String key, float fallback) {
            Float value = numberOrNull(key);
            return value == null ? fallback : value;
        }

        public float slider(String key) {
            return number(key);
        }

        public float slider(String key, float fallback) {
            return number(key, fallback);
        }

        public int sliderInt(String key) {
            return (int) number(key);
        }

        public int sliderInt(String key, int fallback) {
            Float value = numberOrNull(key);
            return value == null ? fallback : value.intValue();
        }

        public int roundedSliderInt(String key) {
            return Math.round(number(key));
        }

        public double sliderDouble(String key) {
            return number(key);
        }

        public BinaryTagHolder payload() {
            return response.payload();
        }

        public void close() {
            audience.closeDialog();
        }

        public void show(DialogLike dialog) {
            audience.showDialog(Objects.requireNonNull(dialog, "dialog"));
        }

        public void show(EasyDialog dialog) {
            Objects.requireNonNull(dialog, "dialog").show(audience);
        }

        private static IllegalStateException missingValue(String key, String type) {
            return new IllegalStateException("missing " + type + " dialog value for key: " + key);
        }
    }
}
