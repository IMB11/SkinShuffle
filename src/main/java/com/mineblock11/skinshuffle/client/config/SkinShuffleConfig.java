package com.mineblock11.skinshuffle.client.config;

import com.google.gson.GsonBuilder;
import com.mineblock11.skinshuffle.SkinShuffle;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.FloatSliderControllerBuilderImpl;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
                (defaults, config, builder) -> {
                    // Rendering Options
                    var carouselRenderStyle = Option.<SkinRenderStyle>createBuilder()
                            .name(translatable("skinshuffle.config.rendering.carousel_rendering_style.name"))
                            .description(OptionDescription.createBuilder()
                                    .text(translatable("skinshuffle.config.rendering.carousel_rendering_style.description"), translatable("skinshuffle.config.rendering.rendering_style")).build())
                            .binding(defaults.carouselSkinRenderStyle, () -> config.carouselSkinRenderStyle, val -> config.carouselSkinRenderStyle = val)
                            .controller(opt -> EnumControllerBuilder.create(opt)
                                    .enumClass(SkinRenderStyle.class)
                                    .valueFormatter(skinRenderStyle -> Text.translatable("skinshuffle.config.rendering." + skinRenderStyle.name().toLowerCase())))
                            .build();

                    var widgetRenderStyle = Option.<SkinRenderStyle>createBuilder()
                            .name(translatable("skinshuffle.config.rendering.widget_rendering_style.name"))
                            .description(OptionDescription.createBuilder()
                                    .text(translatable("skinshuffle.config.rendering.widget_rendering_style.description"), translatable("skinshuffle.config.rendering.rendering_style")).build())
                            .binding(defaults.widgetSkinRenderStyle, () -> config.widgetSkinRenderStyle, val -> config.widgetSkinRenderStyle = val)
                            .controller(opt -> EnumControllerBuilder.create(opt)
                                    .enumClass(SkinRenderStyle.class)
                                    .valueFormatter(skinRenderStyle -> Text.translatable("skinshuffle.config.rendering." + skinRenderStyle.name().toLowerCase())))
                            .build();

                    var rotationMultiplier = Option.<Float>createBuilder()
                            .name(translatable("skinshuffle.config.rendering.rotation_speed.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.rendering.rotation_speed.description")).build())
                            .binding(defaults.rotationMultiplier, () -> config.rotationMultiplier, val -> config.rotationMultiplier = val)
                            .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0f, 5f).step(0.5f)).build();

                    return builder
                            .title(translatable("skinshuffle.config.title"))
                            .category(ConfigCategory.createBuilder()
                                    .name(translatable("skinshuffle.config.rendering.title"))
                                    .tooltip(translatable("skinshuffle.config.rendering.description"))

                                    // Rendering Options
                                    .options(List.of(carouselRenderStyle, widgetRenderStyle, rotationMultiplier))
                                    .build()
                            );
                }
        );
    }

    @ConfigEntry public boolean enableInstalledToast = true;
    @ConfigEntry public boolean enableCooldownToast = true;

    @ConfigEntry public boolean renderClientSkinRegardless = true;

    @ConfigEntry public boolean displayInPauseMenu = true;
    @ConfigEntry public boolean displayInTitleScreen = true;

    @ConfigEntry public SkinRenderStyle widgetSkinRenderStyle = SkinRenderStyle.CURSOR;
    @ConfigEntry public SkinRenderStyle carouselSkinRenderStyle = SkinRenderStyle.ROTATION;
    @ConfigEntry public float rotationMultiplier = 1.0f;

    public enum SkinRenderStyle {
        ROTATION,
        CURSOR
    }
}
