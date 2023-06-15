/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.util;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
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
        if(toastLock || SkinShuffleConfig.get().disableCooldownToast) return;
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
