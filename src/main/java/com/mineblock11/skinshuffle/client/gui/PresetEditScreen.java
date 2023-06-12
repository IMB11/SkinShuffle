package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

import static net.minecraft.client.gui.screen.world.CreateWorldScreen.FOOTER_SEPARATOR_TEXTURE;
import static net.minecraft.client.gui.screen.world.CreateWorldScreen.LIGHT_DIRT_BACKGROUND_TEXTURE;

public class PresetEditScreen extends SpruceScreen {
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
            // TODO
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

        int previewSpanX = 100 / 2;
        int previewSpanY = 160 / 2;
        int previewCenterX = this.width / 4;
        int previewCenterY = this.height / 2;
        graphics.drawBorder(previewCenterX - previewSpanX, previewCenterY - previewSpanY,
                previewSpanX * 2, previewSpanY * 2, 0xDF000000);
        graphics.fill(previewCenterX - previewSpanX + 1, previewCenterY - previewSpanY + 1,
                previewCenterX + previewSpanX - 1, previewCenterY + previewSpanY - 1, 0x7F000000);
        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), previewCenterX, previewCenterY + previewSpanY / 10 * 8, previewSpanY / 10 * 8,
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
