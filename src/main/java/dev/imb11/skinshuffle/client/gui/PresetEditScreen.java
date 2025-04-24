package dev.imb11.skinshuffle.client.gui;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.carousels.CarouselScreen;
import dev.imb11.skinshuffle.client.gui.components.SkinCustomizationTabComponent;
import dev.imb11.skinshuffle.client.gui.components.SkinSourceTabComponent;
import dev.imb11.skinshuffle.client.gui.renderer.SkinPreviewRenderer;
import dev.imb11.skinshuffle.client.gui.widgets.presets.PresetWidget;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

/**
 * Screen for editing skin presets.
 */
public class PresetEditScreen extends SpruceScreen {
    public static final int MAX_WIDTH = 400;

    // Core properties
    private final CarouselScreen parent;
    private final SkinPreset originalPreset;
    private final SkinPreset preset;
    private final PresetWidget<?> presetWidget;
    private final SkinPreviewRenderer previewRenderer;
    private int sideMargins;
    
    // Tabs and navigation
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private TabNavigationWidget tabNavigation;
    private SkinSourceTabComponent skinSourceTab;
    private SkinCustomizationTabComponent skinCustomizationTab;
    
    // UI components
    private GridWidget actionButtonsGrid;
    private ButtonWidget exitButton;
    private boolean isValid = true;

    /**
     * Constructor for PresetEditScreen.
     */
    public PresetEditScreen(PresetWidget<?> presetWidget, CarouselScreen parent, SkinPreset preset) {
        super(Text.translatable("skinshuffle.edit.title"));
        this.presetWidget = presetWidget;
        this.preset = preset.copy();
        this.originalPreset = preset;
        this.parent = parent;
        this.previewRenderer = new SkinPreviewRenderer(client);
    }

    @Override
    protected void init() {
        super.init();
        
        // Initialize tabs
        this.skinSourceTab = new SkinSourceTabComponent(
                textRenderer, 
                preset, 
                client, 
                isValid -> {
                    this.isValid = isValid;
                    updateButtonStates();
                });
                
        this.skinCustomizationTab = new SkinCustomizationTabComponent(
                textRenderer, 
                preset);
        
        // Set up tab navigation
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(skinSourceTab, skinCustomizationTab).build();
        this.addDrawableChild(this.tabNavigation);
        this.tabNavigation.selectTab(0, false);
        
        // Create action buttons
        this.actionButtonsGrid = new GridWidget().setColumnSpacing(10);
        GridWidget.Adder adder = this.actionButtonsGrid.createAdder(2);
        
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
            this.close();
        }).build());

        this.exitButton = ButtonWidget.builder(ScreenTexts.OK, (button) -> {
            saveChanges();
            parent.hasEditedPreset = true;
            this.close();
        }).build();

        adder.add(exitButton);
        
        this.actionButtonsGrid.forEachChild((child) -> {
            child.setNavigationOrder(1);
            this.addDrawableChild(child);
        });

        // Initialize the UI components
        initTabNavigation();
    }

    /**
     * Initialize tab navigation and layout components.
     */
    protected void initTabNavigation() {
        this.sideMargins = Math.max(this.width - MAX_WIDTH, 0) / 2;

        if (this.tabNavigation != null && this.actionButtonsGrid != null) {
            // Update tab navigation
            this.tabNavigation.setWidth(this.width);
            this.tabNavigation.init();

            // Position action buttons at the bottom
            this.actionButtonsGrid.refreshPositions();
            SimplePositioningWidget.setPos(this.actionButtonsGrid, 0, this.height - 36, this.width, 36);

            // Set tab area between navigation and buttons
            int navBottom = this.tabNavigation.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, navBottom, this.width, this.actionButtonsGrid.getY() - navBottom);
            this.tabManager.setTabArea(screenRect);
            
            // Initialize tabs with screen dimensions
            this.skinSourceTab.initialize(this.width, this.height, this.sideMargins);
            this.skinCustomizationTab.initialize(this.width, this.height, this.sideMargins);
        }
    }

    /**
     * Updates button states based on validation and changes.
     */
    private void updateButtonStates() {
        this.exitButton.active = !this.preset.equals(this.originalPreset);
    }

    /**
     * Saves changes to the preset.
     */
    private void saveChanges() {
        this.originalPreset.copyFrom(this.preset);
        
        // Try to save the skin to config, but if it fails, it's safe to ignore
        try {
            this.originalPreset.setSkin(this.preset.getSkin().saveToConfig());
        } catch (Exception ignored) {
        }

        SkinPresetManager.savePresets();
    }

    /**
     * Handle files dropped onto the screen.
     */
    public void onFilesDropped(List<Path> paths) {
        if (!paths.isEmpty()) {
            Path firstPath = paths.getFirst();
            this.tabNavigation.selectTab(0, false);
            this.skinSourceTab.handleFileDrop(firstPath);
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        // Calculate preview dimensions
        int ratioMulTen = 16;
        int topBottomMargin = 40;
        int leftRightMargin = 20;
        int previewSpanX = MAX_WIDTH / 6 - leftRightMargin;
        int previewSpanY = Math.min(this.height - topBottomMargin * 2, previewSpanX * 2 * ratioMulTen / 10) / 2;
        int previewCenterX = MAX_WIDTH / 6 + this.sideMargins;
        int previewCenterY = Math.max(height / 4 + previewSpanY / 2, 120);
        
        // Render preview area
        previewRenderer.renderPreviewArea(graphics, previewCenterX, previewCenterY, previewSpanX, previewSpanY);
        
        // Render preview
        var renderStyle = SkinShuffleConfig.get().presetEditScreenRenderStyle;
        previewRenderer.renderSkinPreview(
                graphics, 
                this.preset, 
                mouseX, 
                mouseY, 
                previewCenterX, 
                previewCenterY, 
                previewSpanY,
                renderStyle,
                this.skinSourceTab != null && this.skinSourceTab.isLoading()
        );

        // Update exitButton state
        this.exitButton.active = !this.preset.equals(this.originalPreset);

        // Render drag and drop hint
        Text text = Text.translatable("skinshuffle.edit.drag_and_drop");
        int x = this.exitButton.getX() - (this.textRenderer.getWidth(text) / 2);
        int y = this.exitButton.getY() - this.textRenderer.fontHeight - 5;
        graphics.drawTextWithShadow(this.textRenderer, text, x, y, 0xCFFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}

