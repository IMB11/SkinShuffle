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

package com.mineblock11.skinshuffle.networking;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerSkinHandling {
    private static final Identifier REFRESH_PLAYER_LIST_ENTRY_ID = SkinShuffle.id("refresh_player_list_entry");

    private static void handleSkinRefresh(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Property skinData = buf.readProperty();
        SkinShuffle.LOGGER.info("Recieved skin refresh packet from: " + player.getName().getString());

        server.execute(() -> {
            var properties = player.getGameProfile().getProperties();
            try {
                properties.removeAll("textures");
            } catch (Exception ignored) {
            }

            try {
                properties.put("textures", skinData);
            } catch (Error e) {
                SkinShuffle.LOGGER.error("Failed to refresh GameProfile for " + player.getName() + "\n" + e.getMessage());
            }

            SkinShufflePlayer skinShufflePlayer = (SkinShufflePlayer) player;
            skinShufflePlayer.skinShuffle$refreshSkin();
        });
    }

    public static PacketByteBuf createEntityIdPacket(int entityId) {
        return PacketByteBufs.create().writeVarInt(entityId);
    }

    public static boolean trySendRefreshPlayerListEntry(ServerPlayerEntity player, PacketByteBuf buf) {
        if (ServerPlayNetworking.canSend(player, REFRESH_PLAYER_LIST_ENTRY_ID)) {
            ServerPlayNetworking.send(player, REFRESH_PLAYER_LIST_ENTRY_ID, buf);
            return true;
        }
        return false;
    }

    public static void init() {
        // Send handshake packet to client.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(handler.getPlayer(), SkinShuffle.id("handshake"))) {
                ServerPlayNetworking.send(handler.getPlayer(), SkinShuffle.id("handshake"), PacketByteBufs.empty());
            }

        });

        ServerPlayNetworking.registerGlobalReceiver(SkinShuffle.id("refresh"), ServerSkinHandling::handleSkinRefresh);
    }
}
