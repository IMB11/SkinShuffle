package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.PresetEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class WarningIndicatorButton extends TexturedIconButtonWidget {
    @Override
    public Text getMessage() {
        return Text.translatable("skinshuffle.indicator");
    }

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
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawTexture(context, this.iconTexture, this.getIconX(), this.getIconY(), this.iconU, this.iconV + (hovered ? 16 : 0), 0, this.iconWidth, this.iconHeight, this.iconTextureWidth, this.iconTextureHeight);
    }
}
