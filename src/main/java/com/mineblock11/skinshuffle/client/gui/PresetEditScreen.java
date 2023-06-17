package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.*;
import com.mineblock11.skinshuffle.util.ToastHelper;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PresetEditScreen extends SpruceScreen {
    private final Screen parent;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    private final SkinPreset originalPreset;
    private SkinPreset preset;
    private TabNavigationWidget tabNavigation;
    private SkinSourceTab skinSourceTab;
    private SkinCustomizationTab skinCustomizationTab;
    private boolean isValid = true;
    private GridWidget grid;
    private ButtonWidget exitButton;

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

        this.exitButton = ButtonWidget.builder(ScreenTexts.OK, (button) -> {
            String skinSource = this.skinSourceTab.textFieldWidget.getText();
            String model = this.originalPreset.getSkin().getModel(); // TODO: Model type cycling button in customize tab.

            if (!skinSource.isEmpty() && this.skinSourceTab.currentSourceType != SourceType.UNCHANGED) {
                Skin skin = null;

                switch (this.skinSourceTab.currentSourceType) {
                    case URL -> skin = new UrlSkin(skinSource, model);
                    case FILE -> skin = new FileSkin(Path.of(skinSource), model);
                    case UUID -> skin = new UUIDSkin(UUID.fromString(skinSource), model);
                    case USERNAME -> skin = new UsernameSkin(skinSource, model);
                    case RESOURCE_LOCATION -> skin = new ResourceSkin(new Identifier(skinSource), model);
                    default -> preset = SkinPreset.generateDefaultPreset();
                }

                try {
                    this.originalPreset.setSkin(skin.saveToConfig());
                } catch (Exception e) {
                    SkinShuffle.LOGGER.error(String.valueOf(e));
                    ToastHelper.showErrorEdit();
                    this.close();
                }
            }

            if(!model.equals(this.skinCustomizationTab.currentModelType.value)) {
                this.originalPreset.getSkin().setModel(this.skinCustomizationTab.currentModelType.value);
            }

            SkinPresetManager.savePresets();
            this.close();
        }).build();

        adder.add(exitButton);
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

        if(skinSourceTab.currentSourceType != SourceType.UNCHANGED) {
            this.isValid = this.validate();
            if (!this.isValid) {
                this.skinSourceTab.errorLabel.setMessage(skinSourceTab.currentSourceType.getInvalidInputText());
            } else {
                this.skinSourceTab.errorLabel.setMessage(Text.empty());
            }
            this.grid.refreshPositions();
        }

        this.skinSourceTab.textFieldWidget.setVisible(skinSourceTab.currentSourceType != SourceType.UNCHANGED);
    }

    private boolean isValidFilePath(String path) {
        File f = new File(path);
        return f.exists() && FilenameUtils.getExtension(path).equals("png");
    }

    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isValidUsername(String username) {
        if(username.matches("([a-zA-Z0-9]|_)*") && username.length() >= 3 && username.length() <= 16) {
            return MojangSkinAPI.getUUIDFromUsername(username).isPresent();
        } else return false;
    }

    public boolean validate() {
        if (this.skinCustomizationTab != null && this.skinSourceTab != null) {
            SourceType type = this.skinSourceTab.currentSourceType;
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
                    if (Identifier.isValid(widget.getText())) {
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

        this.exitButton.active = isValid || this.skinSourceTab.currentSourceType == SourceType.UNCHANGED;
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

    private enum SourceType {
        UNCHANGED,
        USERNAME,
        UUID,
        URL,
        RESOURCE_LOCATION,
        FILE;

        public Text getInvalidInputText() {
            return Text.translatable("skinshuffle.edit.source.invalid_" + name().toLowerCase());
        }

        public Text getTranslation() {
            return Text.translatable("skinshuffle.edit.source." + name().toLowerCase());
        }
    }

    private enum ModelType {
        CLASSIC("default"),
        SLIM("slim");


        private final String value;

        ModelType(String value) {
            this.value = value;
        }

        public static ModelType from(Skin skin) {
            switch (skin.getModel()) {
                case "slim" -> {
                    return ModelType.SLIM;
                }
                default -> {
                    return ModelType.CLASSIC;
                }
            }
        }

        public String getValue() {
            return value;
        }

        public Text getTranslation() {
            return Text.translatable("skinshuffle.edit.customize." + name().toLowerCase());
        }
    }

    private static class SkinSourceTab extends GridScreenTab {
        public final @NotNull PresetEditScreen parent;
        private final TextFieldWidget textFieldWidget;
        private final MultilineTextWidget errorLabel;
        public PresetEditScreen.SourceType currentSourceType;

        private SkinSourceTab(@NotNull PresetEditScreen parent) {
            super(Text.translatable("skinshuffle.edit.source.title"));
            this.parent = parent;

            var gridAdder = this.grid.createAdder(1);

            Positioner positioner = this.grid.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();

            this.currentSourceType = SourceType.UNCHANGED;

            this.textFieldWidget = new TextFieldWidget(parent.textRenderer, 0, 0, 256, 20, Text.empty());
            this.textFieldWidget.setMaxLength(2048);

            this.errorLabel = new MultilineTextWidget(0, 0, Text.empty(), parent.textRenderer);

            this.textFieldWidget.setChangedListener(str -> {
                parent.isValid = parent.validate();
                if (!parent.isValid) {
                    this.errorLabel.setMessage(currentSourceType.getInvalidInputText());
                } else {
                    errorLabel.setMessage(Text.empty());
                }
                this.grid.refreshPositions();
            });

            if (currentSourceType != null) {
                gridAdder.add(new CyclingButtonWidget<>(0,
                        0,
                        192,
                        20,
                        Text.translatable("skinshuffle.edit.source.cycle_prefix").append(": ").append(currentSourceType.getTranslation()),
                        Text.translatable("skinshuffle.edit.source.cycle_prefix"),
                        Arrays.stream(SourceType.values()).toList().indexOf(this.currentSourceType),
                        currentSourceType,
                        CyclingButtonWidget.Values.of(List.of(PresetEditScreen.SourceType.values())),
                        PresetEditScreen.SourceType::getTranslation,
                        sourceTypeCyclingButtonWidget -> Text.of("").copy(),
                        (button, value) -> {
                            this.currentSourceType = value;
                            this.errorLabel.setMessage(Text.empty());

                            textFieldWidget.setVisible(currentSourceType != SourceType.UNCHANGED);

                            if(currentSourceType != SourceType.UNCHANGED) {
                                parent.isValid = parent.validate();
                                if (!parent.isValid) {
                                    this.errorLabel.setMessage(currentSourceType.getInvalidInputText());
                                } else {
                                    errorLabel.setMessage(Text.empty());
                                }
                                this.grid.refreshPositions();
                            }
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

    private static class SkinCustomizationTab extends GridScreenTab {

        private final PresetEditScreen parent;
        private final TextFieldWidget presetNameField;
        private ModelType currentModelType;

        public SkinCustomizationTab(@NotNull PresetEditScreen parent) {
            super(Text.translatable("skinshuffle.edit.customize.title"));
            this.parent = parent;

            this.currentModelType = ModelType.from(parent.preset.getSkin());

            var gridAdder = this.grid.createAdder(1);
            Positioner positioner = this.grid.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();

            this.presetNameField = new TextFieldWidget(parent.textRenderer, 0, 0, 256, 20, Text.empty());
            this.presetNameField.setMaxLength(2048);

            gridAdder.add(new TextWidget(Text.translatable("skinshuffle.edit.customize.preset_name"), parent.textRenderer), positioner);
            gridAdder.add(presetNameField, positioner);

            gridAdder.add(new CyclingButtonWidget<>(0,
                    0,
                    192,
                    20,
                    Text.translatable("skinshuffle.edit.customize.model_cycle_prefix").append(": ").append(currentModelType.getTranslation()),
                    Text.translatable("skinshuffle.edit.customize.model_cycle_prefix"),
                    Arrays.stream(ModelType.values()).toList().indexOf(this.currentModelType),
                    currentModelType,
                    CyclingButtonWidget.Values.of(List.of(ModelType.values())),
                    ModelType::getTranslation,
                    sourceTypeCyclingButtonWidget -> Text.of("").copy(),
                    (button, value) -> {
                        this.currentModelType = value;
                    },
                    value -> null,
                    false), positioner);
        }
    }
}

