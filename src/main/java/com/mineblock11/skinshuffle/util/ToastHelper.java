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
    public static void showToast(MinecraftClient client, String translationKeyTitle, String translationKeyMessage) {
        client.getToastManager().add(SystemToast.create(client,
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable(translationKeyTitle),
                Text.translatable(translationKeyMessage)));
    }

    public static void showHandshakeInitial(MinecraftClient client) {
        if(!SkinShuffleConfig.get().disableInstalledToast)
            showToast(client, "skinshuffle.handshake.toast.title", "skinshuffle.handshake.toast.message_initial");
    }

    public static void showHandshakeOnChange(MinecraftClient client) {
        if(!SkinShuffleConfig.get().disableInstalledToast)
            showToast(client, "skinshuffle.handshake.toast.title", "skinshuffle.handshake.toast.message_on_change");
    }

    public static void showErrorEdit() {
        showToast(MinecraftClient.getInstance(), "skinshuffle.edit.fail.title", "skinshuffle.edit.fail.message");
    }
}
