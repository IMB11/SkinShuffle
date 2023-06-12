package com.mineblock11.skinshuffle.util;

import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.ToastManagerAccessor;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

public class AuthUtil {
    public static boolean isLoggedIn() {
        MinecraftClient client = MinecraftClient.getInstance();
        return ((MinecraftClientAccessor) client).getUserApiService() instanceof YggdrasilUserApiService;
    }

    private static boolean toastLock = false;
    public static void warnNotAuthed() {
        if(toastLock) return;
        toastLock = true;
        MinecraftClient client = MinecraftClient.getInstance();

        ToastManager manager = client.getToastManager();

        var toast = SystemToast.create(MinecraftClient.getInstance(), SystemToast.Type.PACK_LOAD_FAILURE, Text.translatable("skinshuffle.offline.toast.title"), Text.translatable("skinshuffle.offline.toast.message"));

        boolean canShowToast = true;

        // There is a high chance that the player changes in quick succession.
        for (ToastManager.Entry<?> visibleEntry : ((ToastManagerAccessor) manager).getVisibleEntries()) {
            if(visibleEntry.getInstance() instanceof SystemToast systemToast) {
                if(systemToast.title.equals(Text.translatable("skinshuffle.offline.toast.title"))) {
                    canShowToast = false;
                }
            }
        }

        if(canShowToast && toastLock) {
            manager.add(toast);
        }
        toastLock = false;
    }
}
