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

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.util.Objects;

public class NetworkingUtil {
    public static boolean isLoggedIn() {
        MinecraftClient client = MinecraftClient.getInstance();
        return ((MinecraftClientAccessor) client).getUserApiService() instanceof YggdrasilUserApiService;
    }

    public static void handleReconnect(MinecraftClient client) {
        ClientSkinHandling.setReconnectRequired(false);
        SkinPresetManager.setApiPreset(null);

        boolean isSingleplayer = client.isInSingleplayer();
        String folderName, serverAddress;

        if (isSingleplayer) {
            serverAddress = null;
            folderName = client.getServer().getSavePath(WorldSavePath.ROOT).toFile().getName();
            client.world.disconnect();
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.rejoining")));
        } else {
            folderName = null;
            if(!client.getNetworkHandler().getConnection().isLocal()) {
                serverAddress = client.getNetworkHandler().getServerInfo().address;
            } else {
                serverAddress = null;
            }
            client.world.disconnect();
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.reconnecting")));
        }


        if(client.isInSingleplayer()) {
            client.executeTask(() -> {
                try {
                    Thread.sleep(250);
                    client.execute(() -> QuickPlay.startSingleplayer(client, folderName));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            client.executeTask(() -> {
                try {
                    Thread.sleep(250);
                    client.execute(() -> QuickPlay.startMultiplayer(client, serverAddress));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
