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

package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselButton;
import com.mineblock11.skinshuffle.client.gui.widgets.WarningIndicatorButton;
import com.mineblock11.skinshuffle.mixin.accessor.GameMenuScreenAccessor;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.ToastHelper;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class GeneratedScreens {
    public static Screen getConfigScreen(Screen parent) {
        return SkinShuffleConfig.getInstance().generateScreen(parent);
    }

    public static ArrayList<ClickableWidget> createCarouselWidgets(Screen screen) {
        ArrayList<ClickableWidget> widgets = new ArrayList<>();
        int y = (screen.height / 4 + 48) + 84;
        int x = screen.width / 2 + 104 + 25;

        if(screen instanceof GameMenuScreen gameMenuScreen) {
            //? <1.21 {
            /*if(!gameMenuScreen.showMenu)
            *///?} else {
            if(!gameMenuScreen.shouldShowMenu())
            //?}
                return new ArrayList<>();

            if(!SkinShuffleConfig.get().displayInPauseMenu) return widgets;
            y = ((GameMenuScreenAccessor) gameMenuScreen).getExitButton().getY();
            x -= 25 / 2;

            widgets.add(new WarningIndicatorButton(x + 72, y, gameMenuScreen));
        }

        widgets.add(new OpenCarouselButton(x, y, 72, 20));

        return widgets;
    }

    public static Screen getReconnectScreen(Screen target) {
        MinecraftClient client = MinecraftClient.getInstance();
        return new ConfirmScreen((boolean result) -> {
            if(result) {
                NetworkingUtil.handleReconnect(client);
            } else {
                if (!ClientSkinHandling.isInstalledOnServer()) {
                    ToastHelper.showRefusedReconnectToast();
                    ClientSkinHandling.setReconnectRequired(true);
                }

                client.setScreen(target);
            }
        }, Text.translatable("skinshuffle.reconnect.title",
                client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.c_rejoin") : I18n.translate("skinshuffle.reconnect.c_reconnect")).formatted(Formatting.RED, Formatting.BOLD),
                Text.translatable("skinshuffle.reconnect.message",
                        client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.rejoin") : I18n.translate("skinshuffle.reconnect.reconnect_to"),
                        client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.world") : I18n.translate("skinshuffle.reconnect.server"),
                        client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.rejoin") : I18n.translate("skinshuffle.reconnect.reconnect")));
    }

    public static Screen getCarouselScreen(Screen parent) {
        return SkinShuffleConfig.get().carouselView.factory.apply(parent);
    }
}
