package dev.imb11.skinshuffle.client.gui;

import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.widgets.OpenCarouselButton;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.WarningIndicatorButton;
import dev.imb11.skinshuffle.mixin.accessor.GameMenuScreenAccessor;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import dev.imb11.skinshuffle.util.ToastHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
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

        if (screen instanceof GameMenuScreen gameMenuScreen) {
            if (!gameMenuScreen.shouldShowMenu())
                return new ArrayList<>();

            if (!SkinShuffleConfig.get().displayInPauseMenu) return widgets;
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
            if (result) {
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
        var factoryValue = SkinShuffleConfig.get().carouselView.factory.apply(parent);

        if (!SkinShuffleConfig.get().welcomeGuideShown) {
            SkinShuffleConfig.get().welcomeGuideShown = true;
            SkinShuffleConfig.save();
            return new WelcomeGuideScreen(factoryValue);
        }

        return factoryValue;
    }
}
