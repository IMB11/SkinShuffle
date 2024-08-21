/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.client.config;

import com.google.gson.GsonBuilder;
import com.mineblock11.skinshuffle.SkinShuffle;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

import static net.minecraft.text.Text.translatable;

public class SkinShuffleConfig {
    private static final Path CONFIG_FILE_PATH = SkinShuffle.DATA_DIR.resolve("config.json");
    private static final GsonConfigInstance<SkinShuffleConfig> GSON = GsonConfigInstance.createBuilder(SkinShuffleConfig.class)
            .overrideGsonBuilder(new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create())
            .setPath(CONFIG_FILE_PATH)
            .build();

    public static SkinShuffleConfig get() {
        return GSON.getConfig();
    }

    public static void load() {
        GSON.load();
        get().postLoad();
    }

    public static void save() {
        GSON.save();
    }

    public void postLoad() {
        save();
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

                    var presetEditScreenRenderStyle = Option.<SkinRenderStyle>createBuilder()
                            .name(translatable("skinshuffle.config.rendering.preset_edit_screen_rendering_style.name"))
                            .description(OptionDescription.createBuilder()
                                    .text(translatable("skinshuffle.config.rendering.preset_edit_screen_rendering_style.description"), translatable("skinshuffle.config.rendering.rendering_style")).build())
                            .binding(defaults.presetEditScreenRenderStyle, () -> config.presetEditScreenRenderStyle, val -> config.presetEditScreenRenderStyle = val)
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

                    var showCapeInPreview = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.rendering.show_cape_in_preview.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.rendering.show_cape_in_preview.description")).build())
                            .binding(defaults.showCapeInPreview, () -> config.showCapeInPreview, val -> config.showCapeInPreview = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    var disableApi = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.general.disable_api.name"))
                            .description(OptionDescription.createBuilder()
                                    .text(translatable("skinshuffle.config.general.disable_api.description")).build())
                            .binding(defaults.disableAPIUpload, () -> config.disableAPIUpload, val -> config.disableAPIUpload = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    var displayWidgetPause = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.general.display_pause_screen.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.general.display_pause_screen.description")).build())
                            .binding(defaults.displayInPauseMenu, () -> config.displayInPauseMenu, val -> config.displayInPauseMenu = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    var displayWidgetTitleScreen = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.general.display_title_screen.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.general.display_title_screen.description")).build())
                            .binding(defaults.displayInTitleScreen, () -> config.displayInTitleScreen, val -> config.displayInTitleScreen = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    var carouselScrollSensitivity = Option.<Float>createBuilder()
                            .name(translatable("skinshuffle.config.general.carousel_scroll_sensitivity.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.general.carousel_scroll_sensitivity.description")).build())
                            .binding(defaults.carouselScrollSensitivity, () -> config.carouselScrollSensitivity, val -> config.carouselScrollSensitivity = val)
                            .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption).range(0.1f, 5f).step(0.1f)).build();
                    var invertCarouselScroll = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.general.invert_carousel_scroll.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.general.invert_carousel_scroll.description")).build())
                            .binding(defaults.invertCarouselScroll, () -> config.invertCarouselScroll, val -> config.invertCarouselScroll = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    // Popup Options
                    var disableRnnoctToast = Option.<Boolean>createBuilder()
                            .name(translatable("skinshuffle.config.popups.reconnect.name"))
                            .description(OptionDescription.createBuilder().text(translatable("skinshuffle.config.popups.reconnect.description")).build())
                            .binding(defaults.disableReconnectToast, () -> config.disableReconnectToast, val -> config.disableReconnectToast = val)
                            .controller(TickBoxControllerBuilder::create).build();

                    return builder
                            .title(translatable("skinshuffle.config.title"))
                            .category(ConfigCategory.createBuilder()
                                    .name(translatable("skinshuffle.config.general.title"))
                                    .tooltip(translatable("skinshuffle.config.general.description"))
                                    .group(OptionGroup.createBuilder()
                                            .name(translatable("skinshuffle.config.general.behaviour.title"))
                                            .options(List.of(disableApi, carouselScrollSensitivity, invertCarouselScroll))
                                            .build())
                                    .group(OptionGroup.createBuilder()
                                            .name(translatable("skinshuffle.config.general.display.title"))
                                            .options(List.of(displayWidgetPause, displayWidgetTitleScreen))
                                            .build())
                                    .build()
                            ).category(ConfigCategory.createBuilder()
                                    .name(translatable("skinshuffle.config.rendering.title"))
                                    .tooltip(translatable("skinshuffle.config.rendering.description"))
                                    .options(List.of(carouselRenderStyle, presetEditScreenRenderStyle, widgetRenderStyle, rotationMultiplier))
                                    .build()
                            ).category(ConfigCategory.createBuilder()
                                    .name(translatable("skinshuffle.config.popups.title"))
                                    .tooltip(translatable("skinshuffle.config.popups.description"))
                                    .options(List.of(disableRnnoctToast))
                                    .build());
                }
        );
    }

    @SerialEntry public boolean disableReconnectToast = false;

    @SerialEntry public boolean disableAPIUpload = false;

    @SerialEntry public boolean displayInPauseMenu = true;
    @SerialEntry public boolean displayInTitleScreen = true;

    @SerialEntry public float carouselScrollSensitivity = 1.0f;
    @SerialEntry public boolean invertCarouselScroll = false;

    @SerialEntry public SkinRenderStyle widgetSkinRenderStyle = SkinRenderStyle.CURSOR;
    @SerialEntry public SkinRenderStyle carouselSkinRenderStyle = SkinRenderStyle.ROTATION;
    @SerialEntry public SkinRenderStyle presetEditScreenRenderStyle = SkinRenderStyle.ROTATION;
    @SerialEntry public float rotationMultiplier = 1.0f;
    @SerialEntry public boolean showCapeInPreview = true;

    @SerialEntry public CarouselView carouselView = CarouselView.LARGE;

    public enum SkinRenderStyle {
        ROTATION,
        CURSOR
    }
}
