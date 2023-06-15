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

package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;
import java.util.UUID;

import static net.minecraft.client.gui.screen.world.CreateWorldScreen.FOOTER_SEPARATOR_TEXTURE;
import static net.minecraft.client.gui.screen.world.CreateWorldScreen.LIGHT_DIRT_BACKGROUND_TEXTURE;

public class PresetEditScreen extends SpruceScreen {
    public static final int PREVIEW_SPAN_X = 100 / 2;
    public static final int PREVIEW_SPAN_Y = 160 / 2;
    private static final int BUTTON_WIDTH = 160;

    private final Screen parent;
    private final SkinPreset preset;
    private final SkinPreset originalPreset;
    private final LivingEntity entity;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private TabNavigationWidget tabNavigation;
    private GridWidget grid;

    public PresetEditScreen(Screen parent, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.title"));
        this.parent = parent;
        this.preset = preset.copy();
        this.originalPreset = preset;
        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                () -> preset.getSkin().getTexture(),
                () -> preset.getSkin().getModel()
        );
    }

    @Override
    protected void init() {
        // Setup tabs, selecting the first one by default
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(new GeneralTab(), new SkinTab(), new GamePlayTab())
                .build();
        this.addDrawableChild(this.tabNavigation);
        this.tabNavigation.selectTab(0, false);

        // Setup grid layout for cancel and ok buttons
        this.grid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = this.grid.createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
            this.close();
        }).build());
        adder.add(ButtonWidget.builder(ScreenTexts.OK, (button) -> {
            this.originalPreset.copyFrom(this.preset);
            SkinShuffleConfig.savePresets();
            this.close();
        }).build());
        this.grid.forEachChild((child) -> {
            child.setNavigationOrder(1);
            this.addDrawableChild(child);
        });

        this.initTabNavigation();
    }

    @Override
    protected void initTabNavigation() {
        if (this.tabNavigation != null && this.grid != null) {
            this.tabNavigation.setWidth(this.width);
            this.tabNavigation.init();

            this.grid.refreshPositions();
            SimplePositioningWidget.setPos(this.grid, 0, this.height - 36, this.width, 36);

            int i = this.tabNavigation.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, i, this.width, this.grid.getY() - i);
            this.tabManager.setTabArea(screenRect);
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int previewCenterX = this.width / 4;
        int previewCenterY = this.height / 2;
        graphics.drawBorder(previewCenterX - PREVIEW_SPAN_X, previewCenterY - PREVIEW_SPAN_Y,
                PREVIEW_SPAN_X * 2, PREVIEW_SPAN_Y * 2, 0xDF000000);
        graphics.fill(previewCenterX - PREVIEW_SPAN_X + 1, previewCenterY - PREVIEW_SPAN_Y + 1,
                previewCenterX + PREVIEW_SPAN_X - 1, previewCenterY + PREVIEW_SPAN_Y - 1, 0x7F000000);
        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), previewCenterX, previewCenterY + PREVIEW_SPAN_Y / 10 * 8, PREVIEW_SPAN_Y / 10 * 8,
                getEntityRotation(), 0, 0, entity
        );

        graphics.drawTexture(FOOTER_SEPARATOR_TEXTURE, 0, MathHelper.roundUpToMultiple(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        context.drawTexture(LIGHT_DIRT_BACKGROUND_TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
    }

    private float getEntityRotation() {
        return (float) GlfwUtil.getTime() * 35.0f;
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(this.parent);
        }
    }

    private class GeneralTab extends GridScreenTab {
        public GeneralTab() {
            super(Text.translatable("skinshuffle.edit.general.title"));

            this.grid.getMainPositioner().marginLeft(width / 4 + PREVIEW_SPAN_X);
            var gridAdder = this.grid.setColumnSpacing(10).setRowSpacing(4).createAdder(1);

            gridAdder.add(new TextWidget(Text.translatable("skinshuffle.edit.general.name"), Objects.requireNonNull(client).textRenderer));

            var nameField = new TextFieldWidget(
                    MinecraftClient.getInstance().textRenderer,
                    0, 0, BUTTON_WIDTH, 20,
                    Text.translatable("skinshuffle.edit.general.name.enter_name")
            );
            nameField.setText(preset.getName());
            nameField.setChangedListener(preset::setName);
            gridAdder.add(nameField);
        }
    }

    private class SkinTab extends GridScreenTab {
        public SkinTab() {
            super(Text.translatable("skinshuffle.edit.skin.title"));
        }
    }

    private class GamePlayTab extends GridScreenTab {
        public GamePlayTab() {
            super(Text.translatable("skinshuffle.edit.gameplay.title"));
        }
    }
}
