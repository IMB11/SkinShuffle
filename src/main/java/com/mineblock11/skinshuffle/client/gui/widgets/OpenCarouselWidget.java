/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.mixin.accessor.GameMenuScreenAccessor;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceTexturedButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;
import java.util.function.Consumer;

public class OpenCarouselWidget extends SpruceContainerWidget {
    private final Screen parent;
    private final MutableText warningTooltip;
    private SpruceTexturedButtonWidget warningIcon;
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;
    private double currentTime = 0;

    private OpenCarouselWidget(Position position, int width, int height, Screen screen) {
        super(position, width, height);

        this.parent = screen;

        currentTime = GlfwUtil.getTime();

        this.addChild(new SpruceButtonWidget(Position.of(0, 0), width - 18, 20, Text.translatable("skinshuffle.button"), button -> {
            this.client.setScreen(new SkinCarouselScreen(parent));
        }));

        this.warningTooltip = Text.literal(I18n.translate("skinshuffle.reconnect.warning",
                client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.rejoin") : I18n.translate("skinshuffle.reconnect.reconnect"))).formatted(Formatting.RED, Formatting.BOLD);

        if(screen instanceof GameMenuScreen) {
            this.warningIcon = new SpruceTexturedButtonWidget(Position.of(width - 16, 2), 16, 16, Text.empty(), true, btn -> {
                this.client.setScreen(GeneratedScreens.getReconnectScreen(screen));
            }, 0, 0, 16, SkinShuffle.id("textures/gui/warning-icon.png"), 16, 32) {
                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (!this.isActive() || !this.isVisible() || !this.isMouseHovered())
                        return false;

                    return this.onMouseClick(mouseX, mouseY, button);
                }
            };

            this.addChild(warningIcon);
        }
    }



    public static void safelyCreateWidget(Screen screen, Consumer<OpenCarouselWidget> widgetConsumer) {
        int y = (screen.height / 4 + 48) + 72 + 12;
        int x = screen.width / 2 + 104 + 25;

        if(screen instanceof GameMenuScreen gameMenuScreen) {
            if(!SkinShuffleConfig.get().displayInPauseMenu) return;
            y = ((GameMenuScreenAccessor) gameMenuScreen).getExitButton().getY();
            x -= 25 / 2;
        }

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            if(!SkinShuffleConfig.get().displayInTitleScreen) return;
            if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.TitleMenuButtonStyle.CLASSIC && screen instanceof TitleScreen) {
                y += 51 / 4;
            }
        }

        widgetConsumer.accept(new OpenCarouselWidget(Position.of(x, y), 72 + 20, screen.height / 4, screen));
    }

    public void disposed() {
        this.entity.kill();
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        var skin = selectedPreset.getSkin();
        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );
    }

    private float getEntityRotation() {
        return (float) ((GlfwUtil.getTime() - currentTime) * 35.0f);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        if (this.entity != null) {
            // Don't want to render the entity if the skin is still loading
            if (!selectedPreset.getSkin().isLoading()) {
                float followX = (float) (this.getX() + (this.getWidth() / 2)) - mouseX;
                float followY = (float) (this.getY() - 90) - mouseY;
                float rotation = 0;

                SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().widgetSkinRenderStyle;

                if (renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
                    followX = 0;
                    followY = 0;
                    rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
                }

                GuiEntityRenderer.drawEntity(
                        graphics.getMatrices(), this.getX() + (this.getWidth() / 2), this.getY() - 12,
                        45, rotation, followX, followY, entity
                );
            } else {
                // Make sure to call getTexture anyway, otherwise the skin will never load
                selectedPreset.getSkin().getTexture();
            }
        }

        if(this.warningIcon != null) {
            this.warningIcon.setVisible(ClientSkinHandling.isReconnectRequired());
            if(this.warningIcon.isMouseHovered())
                Tooltip.create(mouseX, mouseY, this.warningTooltip, this.width).render(graphics);
        }


        super.renderWidget(graphics, mouseX, mouseY, delta);
    }
}
