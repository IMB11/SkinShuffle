package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.util.ToastHelper;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PresetEditScreen extends SpruceScreen {
    private SkinPreset preset;
    private SkinPreset originalPreset;
    private final Screen parent;
    private TabNavigationWidget tabNavigation;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private SkinSourceTab skinSourceTab;
    private SkinCustomizationTab skinCustomizationTab;
    private boolean isValid = false;
    private GridWidget grid;

    public PresetEditScreen(Screen parent, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.title"));
        this.preset = preset.copy();
        this.originalPreset = preset;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.skinSourceTab = new SkinSourceTab(this);
        this.skinCustomizationTab = new SkinCustomizationTab(this);
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(skinSourceTab, skinCustomizationTab).build();
        this.addDrawableChild(this.tabNavigation);
        this.tabNavigation.selectTab(0, false);

        this.grid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = this.grid.createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
            this.close();
        }).build());
        adder.add(ButtonWidget.builder(ScreenTexts.OK, (button) -> {
            this.originalPreset.copyFrom(this.preset);
            try {
                this.originalPreset.setSkin(this.preset.getSkin().saveToConfig());
            } catch (Exception ignored) {}
            SkinPresetManager.savePresets();
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

        this.isValid = validate();
    }

    private final UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

    private boolean isValidFilePath(String path) {
        File f = new File(path);
        return f.exists() && FilenameUtils.getExtension(path).equals(".png");
    }

    private boolean isValidUUID(String uuid) {
        try{
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException exception){
            return false;
        }
    }

    private boolean isValidUsername(String username) {
        return username.matches("([a-zA-Z0-9]|_)*") && username.length() >= 3 && username.length() <= 16;
    }

    public boolean validate() {
        if(this.skinCustomizationTab != null && this.skinSourceTab != null) {
            SkinSourceTab.SourceType type = this.skinSourceTab.currentSourceType;
            TextFieldWidget widget = this.skinSourceTab.textFieldWidget;
            // URL
            switch (type) {
                case URL -> {
                    return urlValidator.isValid(widget.getText());
                }
                case FILE -> {
                    return isValidFilePath(widget.getText());
                }
                case RESOURCE_LOCATION -> {
                    if(Identifier.isValid(widget.getText())) {
                        return client.getResourceManager().getResource(new Identifier(widget.getText())).isPresent();
                    } else return false;
                }
                case USERNAME -> {
                    return isValidUsername(widget.getText());
                }
                case UUID -> {
                    return isValidUUID(widget.getText());
                }
                default -> {
                    return false;
                }
            }
        } else return false;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        if(!isValid) {
            this.skinSourceTab.errorLabel.setMessage(this.skinSourceTab.currentSourceType.getInvalidInputText());
        } else {
            this.skinSourceTab.errorLabel.setMessage(Text.empty());
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
        private final TextFieldWidget textFieldWidget;
        private final MultilineTextWidget errorLabel;
        public SourceType currentSourceType;

        private enum SourceType {
            USERNAME,
            UUID,
            URL,
            RESOURCE_LOCATION,
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

            public Text getInvalidInputText() {
                return Text.translatable("skinshuffle.edit.source.invalid_" + name().toLowerCase());
            }

            public Text getTranslation() {
                return Text.translatable("skinshuffle.edit.source." + name().toLowerCase());
            }
        }

        private SkinSourceTab(@NotNull PresetEditScreen parent) {
            super(Text.translatable("skinshuffle.edit.source.title"));
            this.parent = parent;

            var gridAdder = this.grid.createAdder(1);

            Positioner positioner = this.grid.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();

            this.currentSourceType = SourceType.getFromPreset(parent.preset);

            this.textFieldWidget = new TextFieldWidget(parent.textRenderer, 0, 0, 256, 20, Text.empty());
            this.textFieldWidget.setMaxLength(2048);
            this.textFieldWidget.setChangedListener(str -> parent.isValid = parent.validate());
            this.textFieldWidget.setText(parent.preset.getSkin().getSourceString());
            this.textFieldWidget.setCursor(0);

            this.errorLabel = new MultilineTextWidget(0, 0, Text.empty(), parent.textRenderer);

            if(currentSourceType != null) {
                    gridAdder.add(new CyclingButtonWidget<>(0,
                            0,
                            192,
                            20,
                            Text.translatable("skinshuffle.edit.source.cycle_prefix").append(": ").append(currentSourceType.getTranslation()),
                            Text.translatable("skinshuffle.edit.source.cycle_prefix"),
                            0,
                            currentSourceType,
                            CyclingButtonWidget.Values.of(List.of(SourceType.values())),
                            SourceType::getTranslation,
                            sourceTypeCyclingButtonWidget -> Text.of("").copy(),
                            (button, value) -> {
                                this.currentSourceType = value;
                                parent.isValid = parent.validate();
                            },
                            value -> null,
                            false), positioner.copy());
            } else {
                ToastHelper.showErrorEdit();
                parent.close();
                return;
            }

            gridAdder.add(textFieldWidget, positioner.marginTop(10));
            gridAdder.add(errorLabel, positioner.marginTop(10));
//            gridAdder.add(new SkinPresetWidget(null, 256, 256, parent.preset, false), positioner);
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

