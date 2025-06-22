package dev.imb11.skinshuffle.client.gui.widgets.presets;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.gui.carousels.CompactCarouselScreen;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.VariableButton;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.render.SpruceGuiGraphics;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

public class CompactPresetWidget extends PresetWidget<CompactCarouselScreen> {
    private static final Random WIGGLE_RANDOM = Random.create();
    private final float wiggleAmount = WIGGLE_RANDOM.nextFloat() + 1f;
    private final float wiggleSpeed = WIGGLE_RANDOM.nextFloat() * 4f + 16f;
    protected VariableButton selectButton;

    public CompactPresetWidget(CompactCarouselScreen parent, SkinPreset skinPreset) {
        super(parent, skinPreset);

        editButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);

        selectButton = new VariableButton(
                Position.of((getWidth() / 2) + 4, getHeight() - 24), getWidth() / 2 - (4 * 2), 20,
                Text.translatable("skinshuffle.carousel.preset_widget.select"),
                button -> {
                    SkinPresetManager.setChosenPreset(getPreset(), parent.hasEditedPreset);
                    SkinPresetManager.savePresets();
                }
        );
        addChild(selectButton);

        copyButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        copyButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        copyButton.setVisible(false);

        deleteButton.overridePosition((getWidth() / 2) + 4, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        deleteButton.setMessage(Text.translatable("skinshuffle.carousel.preset_widget.delete").formatted(Formatting.RED));
        deleteButton.setVisible(false);
    }

    @Override
    public void updateVisibility(int index) {
        var editMode = parent.isEditMode();

        selectButton.setVisible(!editMode);
        editButton.setVisible(!editMode);
        copyButton.setVisible(editMode);
        deleteButton.setVisible(editMode);
    }

    @Override
    public void render(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack matrices = graphics.vanilla().getMatrices();
        matrices.pushMatrix();

        // Wiggle effect when in edit mode and draggable
        if (parent.isEditMode() && isMovable()) {
            float cx = getX() + getWidth() * 0.5f;
            float cy = getY() + getHeight() * 0.5f;
            matrices.translate(cx, cy);

            float angle = (float) Math.sin(GlfwUtil.getTime() * wiggleSpeed) * wiggleAmount;
            matrices.rotate(angle);

            matrices.translate(-cx, -cy);
        }

        super.render(graphics, mouseX, mouseY, delta);
        matrices.popMatrix();
    }

    @Override
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        for (SpruceWidget widget : this) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.setDragging(true);
            this.setDragStart(mouseX - getX(), mouseY - getY());
            return true;
        }

        return false;
    }

    @Override
    protected float getPreviewScaling() {
        return 0.5f;
    }

    @Override
    protected int[] getPreviewBounds() {
        int margin = 5;

        int x1 = getX() + margin;
        int y1 = getY() + margin + this.client.textRenderer.fontHeight + 1; // Below the title
        int x2 = x1 + getWidth() / 2;
        int y2 = y1 + (int) (getHeight() / 1.5f);
        
        return new int[]{x1, y1, x2, y2};
    }
}
