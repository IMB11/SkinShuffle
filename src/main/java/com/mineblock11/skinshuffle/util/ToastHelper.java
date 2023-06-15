package com.mineblock11.skinshuffle.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastHelper {
    private static void showToast(MinecraftClient client, String translationKeyTitle, String translationKeyMessage) {
        client.getToastManager().add(SystemToast.create(client,
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable(translationKeyTitle),
                Text.translatable(translationKeyMessage)));
    }

    public static void showHandshakeInitial(MinecraftClient client) {
        showToast(client, "skinshuffle.handshake.toast.title", "skinshuffle.handshake.toast.message_initial");
    }

    public static void showHandshakeOnChange(MinecraftClient client) {
        showToast(client, "skinshuffle.handshake.toast.title", "skinshuffle.handshake.toast.message_on_change");
    }
}
