package dev.imb11.skinshuffle.client.gui.components;

import dev.imb11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

/**
 * Tab component for customizing skin preset properties.
 */
public class SkinCustomizationTabComponent extends TabComponent {

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
        
        // TODO: Keybind toggle stuff.
    }
}