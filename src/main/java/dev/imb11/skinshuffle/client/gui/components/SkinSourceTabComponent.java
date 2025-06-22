package dev.imb11.skinshuffle.client.gui.components;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.IconButtonWidget;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.imb11.skinshuffle.client.util.SkinLoader;
import dev.imb11.skinshuffle.client.util.ValidationUtils;
import dev.imb11.skinshuffle.util.ToastHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Tab component for selecting and configuring skin sources.
 */
public class SkinSourceTabComponent extends TabComponent {
    private final MinecraftClient client;
    private final Consumer<Boolean> onValidationChanged;

    private TextFieldWidget textFieldWidget;
    private MultilineTextWidget errorLabel;
    private CyclingButtonWidget<String> skinModelButton;
    private IconButtonWidget loadButton;
    private SkinLoader.SourceType currentSourceType;
    private boolean loading = false;

    /**
     * Constructor for the skin source tab component.
     *
     * @param textRenderer        The text renderer
     * @param preset              The skin preset being edited
     * @param client              The Minecraft client instance
     * @param onValidationChanged Callback when validation status changes
     */
    public SkinSourceTabComponent(TextRenderer textRenderer, SkinPreset preset,
                                  MinecraftClient client, Consumer<Boolean> onValidationChanged) {
        super(Text.translatable("skinshuffle.edit.source.title"), textRenderer, preset);
        this.client = client;
        this.onValidationChanged = onValidationChanged;
        this.currentSourceType = SkinLoader.SourceType.UNCHANGED;
    }

    @Override
    public void initialize(int width, int height, int sideMargins) {
        this.grid.getMainPositioner().marginLeft(width / 3).marginRight(sideMargins).alignHorizontalCenter();
        var gridAdder = this.grid.setRowSpacing(4).createAdder(1);

        // Create text field for source input
        this.textFieldWidget = new TextFieldWidget(textRenderer, 0, 0, 230, 20, Text.empty());
        this.textFieldWidget.setMaxLength(2048);
        this.textFieldWidget.setChangedListener(str -> validateInput());

        // Create error label
        this.errorLabel = new MultilineTextWidget(0, 0, Text.empty(), textRenderer) {
            @Override
            public int getHeight() {
                int minHeight = textRenderer.fontHeight * 5;
                return Math.max(super.getHeight(), minHeight);
            }
        };

        // Create load button
        this.loadButton = new IconButtonWidget(
                0, 0, 20, 20,
                0, 0, 0, 2,
                32, 16, 16, 16, 48,
                SkinShuffle.id("textures/gui/reload-button-icon.png"),
                button -> {
                    if (currentSourceType != SkinLoader.SourceType.UNCHANGED) {
                        loadSkin();
                    }
                }
        );

        // Create skin model selection button
        this.skinModelButton = new CyclingButtonWidget.Builder<String>(Text::of)
                .values("classic", "slim")
                .build(0, 0, 192, 20, Text.translatable("skinshuffle.edit.source.skin_model"), (widget, val) -> {
                    preset.getSkin().setModel(val);
                });

        // Create source type selection button
        gridAdder.add(new CyclingButtonWidget<>(
                0, 0, 192, 20,
                Text.translatable("skinshuffle.edit.source.cycle_prefix").append(": ")
                        .append(Text.translatable(currentSourceType.getTranslationKey())),
                Text.translatable("skinshuffle.edit.source.cycle_prefix"),
                Arrays.stream(SkinLoader.SourceType.values()).toList().indexOf(this.currentSourceType),
                currentSourceType,
                CyclingButtonWidget.Values.of(List.of(SkinLoader.SourceType.values())),
                sourceType -> Text.translatable(sourceType.getTranslationKey()),
                sourceTypeCyclingButtonWidget -> Text.of("").copy(),
                (button, value) -> {
                    this.currentSourceType = value;
                    this.errorLabel.setMessage(Text.empty());
                    validateInput();
                },
                value -> null,
                false
        ), grid.copyPositioner().marginTop(Math.min(height / 2 - 60, 20)));

        gridAdder.add(skinModelButton);

        // Add text field and load button in a subgrid
        var subGrid = new net.minecraft.client.gui.widget.GridWidget();
        var subGridAdder = subGrid.setColumnSpacing(4).createAdder(2);
        gridAdder.add(subGrid, grid.copyPositioner().marginTop(6).marginBottom(6));
        subGridAdder.add(textFieldWidget);
        subGridAdder.add(loadButton);

        gridAdder.add(errorLabel, grid.copyPositioner().alignLeft());

        // Initial validation
        validateInput();
    }

    /**
     * Validates the current input and updates UI based on validity.
     */
    public void validateInput() {
        boolean isValid = true;

        if (currentSourceType != SkinLoader.SourceType.UNCHANGED) {
            String input = textFieldWidget.getText();

            isValid = switch (currentSourceType) {
                case URL -> ValidationUtils.isValidUrl(input);
                case FILE -> ValidationUtils.isValidPngFilePath(input);
                case RESOURCE_LOCATION -> ValidationUtils.isValidResourceLocation(input, client);
                case USERNAME -> ValidationUtils.isValidUsername(input);
                case UUID -> ValidationUtils.isValidUUID(input);
                default -> false;
            };

            // Update error message
            if (!isValid) {
                errorLabel.setMessage(Text.translatable(currentSourceType.getInvalidInputTranslationKey()));
            } else {
                errorLabel.setMessage(Text.empty());
            }
        }

        // Update visibility of input fields
        textFieldWidget.setVisible(currentSourceType != SkinLoader.SourceType.UNCHANGED);
        loadButton.visible = currentSourceType != SkinLoader.SourceType.UNCHANGED;
        loadButton.active = currentSourceType != SkinLoader.SourceType.UNCHANGED && isValid;
        skinModelButton.visible = currentSourceType != SkinLoader.SourceType.UNCHANGED;

        // Notify validation status change
        onValidationChanged.accept(isValid);
    }

    /**
     * Loads the skin from the specified source.
     */
    public void loadSkin() {
        loading = true;
        String skinSource = textFieldWidget.getText();
        String model = skinModelButton.getValue();

        CompletableFuture<Void> future = SkinLoader.loadSkin(
                currentSourceType, skinSource, model, preset);

        future.thenRun(() -> loading = false);
    }

    /**
     * Handles files being dropped onto the screen.
     */
    public void handleFileDrop(Path path) {
        if (ValidationUtils.isValidPngFilePath(path.toString())) {
            currentSourceType = SkinLoader.SourceType.FILE;
            errorLabel.setMessage(Text.empty());
            textFieldWidget.setText(path.toString());
            validateInput();
            loadSkin();
        } else {
            ToastHelper.showToast("invalid_dropped_file");
        }
    }

    /**
     * Gets the current source type.
     */
    public SkinLoader.SourceType getCurrentSourceType() {
        return currentSourceType;
    }

    /**
     * Gets the source text field.
     */
    public TextFieldWidget getTextFieldWidget() {
        return textFieldWidget;
    }

    /**
     * Returns whether a skin is currently loading.
     */
    public boolean isLoading() {
        return loading;
    }
}