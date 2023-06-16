package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.widgets.SkinPresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.*;
import com.mineblock11.skinshuffle.util.ToastHelper;
import dev.isxander.yacl3.gui.controllers.cycling.CyclingControllerElement;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextAreaWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PresetEditScreen extends SpruceScreen {
    private SkinPreset preset;
    private final Screen parent;
    private SkinPresetWidget presetWidget;
    public SpruceTextAreaWidget textFieldWidget;
    private TabNavigationWidget tabNavigation;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);

    public PresetEditScreen(Screen parent, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.title"));
        this.preset = preset;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(new SkinSourceTab(this), new SkinCustomizationTab(this)).build();
        this.addDrawableChild(this.tabNavigation);
        this.tabNavigation.selectTab(0, false);

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 128 - 5, this.height - 23), 128, 20, ScreenTexts.CANCEL, button -> {
            this.close();
        }));

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 + 5, this.height - 23), 128, 20, Text.translatable("skinshuffle.carousel.save_button"), button -> {
            this.close();
        }));

        this.presetWidget = new SkinPresetWidget(null, this.width / 4, this.height - 45, this.preset, false);
        this.presetWidget.overridePosition(Position.of(5, 5));

        this.textFieldWidget = new SpruceTextAreaWidget(Position.of(this.width / 4 + 10, 68), this.width - (this.width / 4 + 15),
                this.height - 68 - 30 - 30, Text.empty());
        this.textFieldWidget.setText(this.preset.getSkin().getSourceString());
        this.addDrawableChild(this.textFieldWidget);

        this.addDrawableChild(this.presetWidget);
        this.initTabNavigation();
    }

    @Override
    protected void initTabNavigation() {
        if (this.tabNavigation != null) {
            this.tabNavigation.setWidth(this.width);
            this.tabNavigation.init();

            int i = this.tabNavigation.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, i, this.width, i);
            this.tabManager.setTabArea(screenRect);
        }
    }

    public void validate() {

    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        this.presetWidget.overrideDimensions(getCardWidth(), getCardHeight());
        this.presetWidget.overridePosition(Position.of(5, (this.height / 2) - (getCardHeight() / 2)));

        if (this.tabManager.getCurrentTab() instanceof SkinSourceTab sourceTab) {
            this.textFieldWidget.setVisible(true);
        } else {
            this.textFieldWidget.setVisible(false);
        }
    }

    private int getCardWidth() {
        return this.width / 4;
    }

    private int getCardHeight() {
        return (int) (this.height / 1.5);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    private static class SkinSourceTab extends GridScreenTab {
        public final @NotNull PresetEditScreen parent;


        private enum SourceType {
            USERNAME,
            UUID,
            URL,
            RESOURCE_LOCATION,
            DEFAULT_SKIN,
            FILE;

            public static SourceType getFromPreset(SkinPreset preset) {
                var skin = preset.getSkin();
                switch (skin.getSerializationId().getPath()) {
                    case "uuid":
                        return SourceType.UUID;
                    case "username":
                        return SourceType.USERNAME;
                    case "url":
                        return SourceType.URL;
                    case "file":
                        return SourceType.FILE;
                    case "resource":
                        return SourceType.RESOURCE_LOCATION;
                }
                SkinShuffle.LOGGER.error("Unknown serialization id: " + skin.getSerializationId());
                return null;
            }

            public Text getTranslation() {
                return Text.translatable("skinshuffle.edit.source." + name().toLowerCase());
            }
        }

        private SkinSourceTab(@NotNull PresetEditScreen parent) {
            super(Text.translatable("skinshuffle.edit.source.title"));
            this.parent = parent;

            this.grid.getMainPositioner().marginLeft(parent.width / 4 + 10);
//            this.grid.setY((parent.height / 2) - (parent.getCardHeight() / 2));
            var gridAdder = this.grid.createAdder(1);

            Positioner positioner = this.grid.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();

            SourceType defaultSourceType = SourceType.getFromPreset(parent.preset);

            if(defaultSourceType != null) {
                    gridAdder.add(new CyclingButtonWidget<>(0,
                            0,
                            192,
                            20,
                            Text.translatable("skinshuffle.edit.source.cycle_prefix").append(": ").append(defaultSourceType.getTranslation()),
                            Text.translatable("skinshuffle.edit.source.cycle_prefix"),
                            0,
                            SourceType.URL,
                            CyclingButtonWidget.Values.of(List.of(SourceType.values())),
                            SourceType::getTranslation,
                            sourceTypeCyclingButtonWidget -> Text.of("").copy(),
                            (button, value) -> {

                            },
                            value -> null,
                            false), positioner.copy().marginTop(18));
            } else {
                ToastHelper.showErrorEdit();
                parent.close();
                return;
            }
        }

        public int getContainerWidth() {
            return parent.width - (parent.width / 4) - 10;
        }

        public int getContainerHeight() {
            return parent.height - (parent.height / 2);
        }
    }

    private static class SkinCustomizationTab implements Tab {
        public final PresetEditScreen parent;

        private SkinCustomizationTab(PresetEditScreen parent) {
            this.parent = parent;
        }

        @Override
        public Text getTitle() {
            return Text.of("Skin Customization");
        }

        @Override
        public void forEachChild(Consumer<ClickableWidget> consumer) {

        }

        @Override
        public void refreshGrid(ScreenRect tabArea) {

        }
    }
}

