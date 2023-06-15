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

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.mixin.accessor.GameMenuScreenAccessor;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

public class OpenCarouselWidget extends SpruceContainerWidget {
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;

    private OpenCarouselWidget(Position position, int width, int height) {
        super(position, width, height);

        this.addChild(new SpruceButtonWidget(Position.of(0, 0), width, 20, Text.translatable("skinshuffle.button"), button -> {
            this.client.setScreen(new SkinCarouselScreen());
        }));

        setSelectedPreset(SkinPresetManager.getChosenPreset());
    }



    public static void safelyCreateWidget(Screen screen, Consumer<OpenCarouselWidget> widgetConsumer) {
        int y = (screen.height / 4 + 48) + 72 + 12;
        int x = screen.width / 2 + 104 + 25;

        if(screen instanceof GameMenuScreen gameMenuScreen) {
            y = ((GameMenuScreenAccessor) gameMenuScreen).getExitButton().getY();
            x -= 25 / 2;
        }

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            if (ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.TitleMenuButtonStyle.CLASSIC && screen instanceof TitleScreen) {
                y += 51 / 4;
            }
        }

        widgetConsumer.accept(new OpenCarouselWidget(Position.of(x, y), 72, screen.height / 4));
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        var skin = selectedPreset.getSkin();
        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        if(this.entity != null) {
            GuiEntityRenderer.drawEntity(
                    graphics.getMatrices(), getX() + (this.getWidth() / 2), this.getY() - 12,
                    (int)(45), 0, (float)(getX() + (this.getWidth() / 2)) - mouseX, (float)(this.getY() - this.height * 1.25) - mouseY, entity
            );
        }

        super.renderWidget(graphics, mouseX, mouseY, delta);
    }
}
