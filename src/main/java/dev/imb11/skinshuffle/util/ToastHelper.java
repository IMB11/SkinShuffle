package dev.imb11.skinshuffle.util;

import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastHelper {
    public static void showToast(String id) {
        var client = MinecraftClient.getInstance();
        client.getToastManager().add(SystemToast.create(client,
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable(id + ".title"),
                Text.translatable(id + ".message")));
    }

    public static void showRefusedReconnectToast() {
        if (!SkinShuffleConfig.get().disableReconnectToast)
            showToast("skinshuffle.toasts.refused_reconnect");
    }

    public static void showOfflineModeToast() {
        showToast("skinshuffle.toasts.offline");
    }

    public static void showEditorFailToast() {
        showToast("skinshuffle.toasts.editor_failure");
    }
}
