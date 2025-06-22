package dev.imb11.skinshuffle.client.gui.widgets.buttons;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.render.SpruceGuiGraphics;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class SkinShuffleIconButton extends SpruceIconButtonWidget {
    private final Function<SkinShuffleIconButton, Identifier> iconTexture;

    public SkinShuffleIconButton(Position position, int width, int height, Text message, PressAction action, Function<SkinShuffleIconButton, Identifier> iconTexture) {
        super(position, width, height, message, action);
        this.iconTexture = iconTexture;
        this.setTooltip(message);
    }

    @Override
    protected int renderIcon(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                iconTexture.apply(this),
                this.getX() + this.getWidth() / 2 - (16 / 2),
                this.getY() + this.getHeight() / 2 - (16 / 2),
                0,
                isMouseHovered() ? 16 : 0,
                16,
                16,
                16,
                32
        );
        return 16;
    }

    @Override
    protected void renderWidget(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderButton(graphics, mouseX, mouseY, delta);
    }
}
