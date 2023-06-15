package com.mineblock11.skinshuffle.client.config;

import com.google.gson.GsonBuilder;
import com.mineblock11.skinshuffle.SkinShuffle;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.FloatSliderControllerBuilderImpl;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

import static net.minecraft.text.Text.*;

public class SkinShuffleConfig {
    private static final Path CONFIG_FILE_PATH = SkinShuffle.DATA_DIR.resolve("config.json");
    public static final GsonConfigInstance<SkinShuffleConfig> GSON = GsonConfigInstance.createBuilder(SkinShuffleConfig.class)
            .overrideGsonBuilder(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create())
            .setPath(CONFIG_FILE_PATH)
            .build();

    public static SkinShuffleConfig get() {
        return GSON.getConfig();
    }

    public static YetAnotherConfigLib getInstance() {
        return YetAnotherConfigLib.create(GSON,
                (defaults, config, builder) -> builder
                        .title(translatable("skinshuffle.config.title"))
                        .category(ConfigCategory.createBuilder()
                            .name(translatable("skinshuffle.config.widget.title"))
                                .tooltip(translatable("skinshuffle.config.widget.description"))
                            .options(List.of(
                                    Option.<Boolean>createBuilder()
                                            .name(translatable("skinshuffle.config.widget.follow_cursor.name"))
                                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.widget.follow_cursor.description")).build())
                                            .binding(defaults.widgetSkinFollowCursor, () -> config.widgetSkinFollowCursor, val -> config.widgetSkinFollowCursor = val)
                                            .controller(BooleanControllerBuilderImpl::new).build(),
                                    Option.<Boolean>createBuilder()
                                            .name(translatable("skinshuffle.config.widget.rotate_widget.name"))
                                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.rotate_widget.description")).build())
                                            .binding(defaults.rotateWidgetSkin, () -> config.rotateWidgetSkin, val -> config.rotateWidgetSkin = val)
                                            .controller(BooleanControllerBuilderImpl::new).build(),
                                    Option.<Boolean>createBuilder()
                                            .name(translatable("skinshuffle.config.widget.rotate_carousel.name"))
                                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.widget.rotate_carousel.description")).build())
                                            .binding(defaults.rotateCarouselSkin, () -> config.rotateCarouselSkin, val -> config.rotateCarouselSkin = val)
                                            .controller(BooleanControllerBuilderImpl::new).build(),
                                    Option.<Float>createBuilder()
                                            .name(translatable("skinshuffle.config.widget.rotation_speed.name"))
                                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.widget.rotation_speed.description")).build())
                                            .binding(defaults.rotationMultiplier, () -> config.rotationMultiplier, val -> config.rotationMultiplier = val)
                                            .controller(FloatSliderControllerBuilderImpl::new).build()
                            ))
                                .build()
                        )
        );
    }

    @ConfigEntry public boolean enableInstalledToast = true;
    @ConfigEntry public boolean enableCooldownToast = true;

    @ConfigEntry public boolean renderClientSkinRegardless = true;

    @ConfigEntry public boolean displayInPauseMenu = true;
    @ConfigEntry public boolean displayInTitleScreen = true;

    @ConfigEntry public boolean widgetSkinFollowCursor = true;
    @ConfigEntry public boolean rotateWidgetSkin = false;
    @ConfigEntry public boolean rotateCarouselSkin = true;
    @ConfigEntry public float rotationMultiplier = 1.0f;
}
