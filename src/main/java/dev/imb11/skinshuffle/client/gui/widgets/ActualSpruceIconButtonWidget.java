

package dev.imb11.skinshuffle.client.gui.widgets;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public class ActualSpruceIconButtonWidget extends SpruceIconButtonWidget {
    private final Function<ActualSpruceIconButtonWidget, Identifier> iconTexture;

    public ActualSpruceIconButtonWidget(Position position, int width, int height, Text message, PressAction action, Function<ActualSpruceIconButtonWidget, Identifier> iconTexture) {
        super(position, width, height, message, action);
        this.iconTexture = iconTexture;
    }

    @Override
    protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(
                //? >=1.21.2 {
                RenderLayer::getGuiTextured,
                //?}
                iconTexture.apply(this),
                this.getX() + this.getWidth() / 2 - (16 / 2),
                this.getY() + this.getHeight() / 2 - (16 / 2),
                //? <1.21.2 {
                /*16,
                16,
                0,
                isMouseHovered() ? 16 : 0,
                *///?} else {
                0,
                isMouseHovered() ? 16 : 0,
                16,
                16,
                //?}
                16,
                16,
                16,
                32
        );
        return 16;
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.renderButton(graphics, mouseX, mouseY, delta);

        var tooltip = getTooltip();
        if (!this.dragging && isMouseHovered() && tooltip.isPresent()) {
            Tooltip.create(mouseX, mouseY, List.of(tooltip.get().asOrderedText())).queue();
        }
    }
}
