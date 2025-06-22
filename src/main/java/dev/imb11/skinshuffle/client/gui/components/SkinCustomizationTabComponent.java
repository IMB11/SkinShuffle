package dev.imb11.skinshuffle.client.gui.components;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Tab component for customizing skin preset properties.
 */
public class SkinCustomizationTabComponent extends TabComponent {

    private static final int MAX_KEYBIND_ID = 9;

    /**
     * Constructor for the skin customization tab component.
     */
    public SkinCustomizationTabComponent(TextRenderer textRenderer, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.customize.title"), textRenderer, preset);
    }

    @Override
    public void initialize(int width, int height, int sideMargins) {
        this.grid.getMainPositioner().marginLeft(width / 3).alignHorizontalCenter();
        var gridAdder = this.grid.setRowSpacing(8).createAdder(1);

        // Add preset name field
        var presetNameField = new TextFieldWidget(textRenderer, 0, 0, 256, 20, Text.empty());
        presetNameField.setText(preset.getName());
        presetNameField.setChangedListener(preset::setName);
        presetNameField.setMaxLength(2048);

        gridAdder.add(new TextWidget(Text.translatable("skinshuffle.edit.customize.preset_name"), textRenderer));
        gridAdder.add(presetNameField);

        // Add keybind ID selector
        gridAdder.add(new TextWidget(Text.translatable("skinshuffle.edit.customize.keybind_id"), textRenderer));

        // Create list of available keybind IDs (1-9 and None/-1)
        List<Integer> availableKeybindIds = new ArrayList<>();
        availableKeybindIds.add(-1); // None

        // Get all keybind IDs that are already in use (except by this preset)
        List<Integer> usedKeybindIds = SkinPresetManager.getLoadedPresets().stream()
                .filter(p -> p != preset && p.getKeybindId() >= 0)
                .map(SkinPreset::getKeybindId)
                .toList();

        // Add all unused keybind IDs
        IntStream.rangeClosed(1, MAX_KEYBIND_ID)
                .filter(id -> !usedKeybindIds.contains(id))
                .forEach(availableKeybindIds::add);

        // Always add currently assigned ID if it exists
        if (preset.getKeybindId() > 0 && !availableKeybindIds.contains(preset.getKeybindId())) {
            availableKeybindIds.add(preset.getKeybindId());
        }

        // Sort the IDs for nicer display
        availableKeybindIds.sort(Integer::compare);

        // Find the starting index for the current keybind ID
        int currentIndex = availableKeybindIds.indexOf(preset.getKeybindId());
        if (currentIndex < 0) currentIndex = 0; // Fallback to "None" if not found

        // Create the cycling button
        var keybindIdButton = new CyclingButtonWidget<>(
                0, 0, 256, 20,
                Text.translatable("skinshuffle.edit.customize.keybind_id_prefix").append(": ")
                        .append(formatKeybindIdText(preset.getKeybindId())),
                Text.translatable("skinshuffle.edit.customize.keybind_id_prefix"),
                currentIndex,
                preset.getKeybindId(),
                CyclingButtonWidget.Values.of(availableKeybindIds),
                this::formatKeybindIdText,
                keybindIdCyclingButtonWidget -> Text.of("").copy(),
                (button, value) -> {
                    preset.setKeybindId(value);
                },
                value -> null,
                false
        );
        gridAdder.add(keybindIdButton);
    }

    /**
     * Format the keybind ID for display in the cycling button.
     *
     * @param keybindId The keybind ID to format
     * @return Formatted text for the given keybind ID
     */
    private Text formatKeybindIdText(int keybindId) {
        return keybindId < 0 ?
                Text.translatable("skinshuffle.edit.customize.keybind_id.none") :
                Text.of(String.valueOf(keybindId));
    }
}