package dev.imb11.skinshuffle.client.gui.widgets.buttons;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WarningIndicatorButton extends IconButtonWidget {
    public WarningIndicatorButton(int x, int y, Screen parent) {
        super(x, y, 20, 20,
                0, 0, 0, 2,
                16, 16, 16, 16, 32,
                SkinShuffle.id("textures/gui/warning-icon.png"),
                button -> {
                    var client = MinecraftClient.getInstance();
                    client.setScreen(GeneratedScreens.getReconnectScreen(parent));
                }
        );

        var client = MinecraftClient.getInstance();

        this.setTooltip(Tooltip.of(Text.literal(I18n.translate("skinshuffle.reconnect.warning",
                client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.rejoin") : I18n.translate("skinshuffle.reconnect.reconnect"))).formatted(Formatting.RED, Formatting.BOLD)));
    }

    @Override
    public Text getMessage() {
        return Text.translatable("skinshuffle.indicator");
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                this.iconTexture,
                this.getIconX(),
                this.getIconY(),
                this.iconU,
                this.iconV + (active ? (hovered ? 16 : 0) : this.iconDisabledVOffset),
                this.iconWidth,
                this.iconHeight,
                this.iconTextureWidth,
                this.iconTextureHeight
        );
    }
}