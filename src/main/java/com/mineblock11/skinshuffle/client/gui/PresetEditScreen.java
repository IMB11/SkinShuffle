package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.gui.widgets.TexturedIconButtonWidget;
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
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
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

import static net.minecraft.client.gui.screen.world.CreateWorldScreen.LIGHT_DIRT_BACKGROUND_TEXTURE;

public class PresetEditScreen extends SpruceScreen {
    public static final int PREVIEW_SPAN_X = 100 / 2;
    public static final int PREVIEW_SPAN_Y = 160 / 2;

    private final SkinCarouselScreen parent;
    private final LivingEntity entity;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    private final SkinPreset originalPreset;
    private final SkinPreset preset;
    private TabNavigationWidget tabNavigation;
    private SkinSourceTab skinSourceTab;
    private SkinCustomizationTab skinCustomizationTab;
    private boolean isValid = true;
    private GridWidget grid;
    private ButtonWidget exitButton;

    public PresetEditScreen(SkinCarouselScreen parent, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.title"));
        this.preset = preset.copy();
        this.originalPreset = preset;
        this.parent = parent;

        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                () -> this.preset.getSkin().getTexture(),
                () -> this.preset.getSkin().getModel()
        );
    }

    @Override
    protected void init() {
        super.init();

        this.skinSourceTab = new SkinSourceTab();
        this.skinCustomizationTab = new SkinCustomizationTab();
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
            this.originalPreset.copyFrom(this.preset);
            // We try to save the skin to config, but if it fails, it's safe to ignore.
            try {
                this.originalPreset.setSkin(this.preset.getSkin().saveToConfig());
            } catch (Exception ignored) {
            }

            SkinPresetManager.savePresets();
            parent.hasEditedPreset = true;
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

        updateValidity();
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
        return username.matches("([a-zA-Z0-9]|_)*") && username.length() >= 3 && username.length() <= 16;
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

    private void updateValidity() {
        if (skinSourceTab.currentSourceType != SourceType.UNCHANGED) {
            this.isValid = this.validate();
            if (!this.isValid) {
                this.skinSourceTab.errorLabel.setMessage(skinSourceTab.currentSourceType.getInvalidInputText());
            } else {
                this.skinSourceTab.errorLabel.setMessage(Text.empty());
            }
            this.grid.refreshPositions();
        }

        this.skinSourceTab.textFieldWidget.setVisible(skinSourceTab.currentSourceType != SourceType.UNCHANGED);
        this.skinSourceTab.loadButton.visible = skinSourceTab.currentSourceType != SourceType.UNCHANGED;
        this.skinSourceTab.loadButton.active = skinSourceTab.currentSourceType != SourceType.UNCHANGED && isValid;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int previewCenterX = this.width / 6;
        int previewCenterY = this.height / 2;
        graphics.drawBorder(previewCenterX - PREVIEW_SPAN_X, previewCenterY - PREVIEW_SPAN_Y,
                PREVIEW_SPAN_X * 2, PREVIEW_SPAN_Y * 2, 0xDF000000);
        graphics.fill(previewCenterX - PREVIEW_SPAN_X + 1, previewCenterY - PREVIEW_SPAN_Y + 1,
                previewCenterX + PREVIEW_SPAN_X - 1, previewCenterY + PREVIEW_SPAN_Y - 1, 0x7F000000);

        if (!this.preset.getSkin().isLoading()) {
            var entityX = previewCenterX;
            var entityY = previewCenterY + PREVIEW_SPAN_Y / 10 * 8;

            float followX = entityX - mouseX;
            float followY = entityY - PREVIEW_SPAN_Y - mouseY;
            float rotation = 0;

            SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().carouselSkinRenderStyle;

            if (renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
                followX = 0;
                followY = 0;
                rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
            }

            GuiEntityRenderer.drawEntity(
                    graphics.getMatrices(), entityX, entityY, PREVIEW_SPAN_Y / 10 * 8,
                    rotation, followX, followY, entity
            );
        } else {
            // We call getTexture() anyway to make sure the texture is being loaded in the background.
            this.preset.getSkin().getTexture();
        }

        this.exitButton.active = !this.preset.equals(this.originalPreset);
    }

    public void renderBackgroundTexture(DrawContext context) {
        // If we don't explicitly have this, the background color will be slightly off from the tab color.
        context.drawTexture(LIGHT_DIRT_BACKGROUND_TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
    }

    private float getEntityRotation() {
        return (float) GlfwUtil.getTime() * 35.0f;
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

    private class SkinSourceTab extends GridScreenTab {
        private final TextFieldWidget textFieldWidget;
        private final MultilineTextWidget errorLabel;
        public PresetEditScreen.SourceType currentSourceType;
        private final CyclingButtonWidget<String> skinModelButton;
        private final ButtonWidget loadButton;

        private SkinSourceTab() {
            super(Text.translatable("skinshuffle.edit.source.title"));

            this.grid.getMainPositioner().marginLeft(parent.width / 3).alignHorizontalCenter();
            var gridAdder = this.grid.setRowSpacing(4).createAdder(1);

            this.currentSourceType = SourceType.UNCHANGED;

            this.textFieldWidget = new TextFieldWidget(textRenderer, 0, 0, 230, 20, Text.empty());
            this.textFieldWidget.setMaxLength(2048);

            this.errorLabel = new MultilineTextWidget(0, 0, Text.empty(), textRenderer) {
                @Override
                public int getHeight() {
                    int minHeight = textRenderer.fontHeight * 5;
                    return Math.max(super.getHeight(), minHeight);
                }
            };

            loadButton = new TexturedIconButtonWidget(
                    0, 0, 0, 2,
                    16, 16, 16, 16, 32,
                    SkinShuffle.id("textures/gui/reload-button-icon.png"),
                    button -> {
                        if (currentSourceType != SourceType.UNCHANGED) {
                            loadSkin();
                        }
                    }
            );

            this.textFieldWidget.setChangedListener(str -> updateValidity());

            skinModelButton = new CyclingButtonWidget.Builder<>(Text::of)
                    .values("default", "slim")
                    .build(0, 0, 192, 20, Text.translatable("skinshuffle.edit.source.skin_model"));

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

                            updateValidity();
                        },
                        value -> null,
                        false), grid.copyPositioner().marginTop(12));
            } else {
                ToastHelper.showErrorEdit();
                close();
            }

            gridAdder.add(skinModelButton);

            var subGrid = new GridWidget();
            var subGridAdder = subGrid.setColumnSpacing(4).createAdder(2);
            gridAdder.add(subGrid, grid.copyPositioner().marginTop(6).marginBottom(6));
            subGridAdder.add(textFieldWidget);
            subGridAdder.add(loadButton);

            gridAdder.add(errorLabel, grid.copyPositioner().alignLeft());
        }

        private void loadSkin() {
            String skinSource = textFieldWidget.getText();
            String model = skinModelButton.getValue();

            if (!skinSource.isEmpty() && currentSourceType != SourceType.UNCHANGED) {
                Skin skin = switch (currentSourceType) {
                    case URL -> new UrlSkin(skinSource, model);
                    case FILE -> new FileSkin(Path.of(skinSource), model);
                    case UUID -> new UUIDSkin(UUID.fromString(skinSource), model);
                    case USERNAME -> new UsernameSkin(skinSource, model);
                    case RESOURCE_LOCATION -> new ResourceSkin(new Identifier(skinSource), model);
                    default -> SkinPreset.generateDefaultPreset().getSkin();
                };

                preset.setSkin(skin);
            }
        }
    }

    private class SkinCustomizationTab extends GridScreenTab {

        public SkinCustomizationTab() {
            super(Text.translatable("skinshuffle.edit.customize.title"));

            this.grid.getMainPositioner().marginLeft(parent.width / 3).alignHorizontalCenter();
            var gridAdder = this.grid.setRowSpacing(8).createAdder(1);

            var presetNameField = new TextFieldWidget(textRenderer, 0, 0, 256, 20, Text.empty());
            presetNameField.setText(preset.getName());
            presetNameField.setChangedListener(preset::setName);
            presetNameField.setMaxLength(2048);

            gridAdder.add(new TextWidget(Text.translatable("skinshuffle.edit.customize.preset_name"), textRenderer));
            gridAdder.add(presetNameField);
        }
    }
}

