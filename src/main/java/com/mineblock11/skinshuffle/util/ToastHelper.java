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
