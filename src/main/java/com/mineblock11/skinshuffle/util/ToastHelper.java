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
        if(!SkinShuffleConfig.get().disableReconnectToast)
            showToast("skinshuffle.toasts.refused_reconnect");
    }

    public static void showOfflineModeToast() {
        showToast("skinshuffle.toasts.offline");
    }

    public static void showEditorFailToast() {
        showToast("skinshuffle.toasts.editor_failure");
    }
}
