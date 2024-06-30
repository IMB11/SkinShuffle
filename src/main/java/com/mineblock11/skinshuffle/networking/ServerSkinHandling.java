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
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerSkinHandling {

    private static void handleSkinRefresh(MinecraftServer server, ServerPlayerEntity player, Property skinData) {
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

    /**
     * Attempt to refresh the player list entry for a player.
     * @param player The player to refresh the entry for.
     * @param entityID The entity ID of the player.
     * @return Whether the refresh was successful.
     */
    public static boolean attemptPlayerListEntryRefresh(ServerPlayerEntity player, int entityID) {
        /*? if <1.20.5 {*/
        /*if (ServerPlayNetworking.canSend(player, SkinShuffle.id("refresh_player_list_entry"))) {
            ServerPlayNetworking.send(player, SkinShuffle.id("refresh_player_list_entry"), net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create().writeVarInt(entityID));
            return true;
        }
        *//*?} else {*/
        if (ServerPlayNetworking.canSend(player, RefreshPlayerListEntryPayload.PACKET_ID)) {
            ServerPlayNetworking.send(player, new RefreshPlayerListEntryPayload(entityID));
            return true;
        }
        /*?}*/
        return false;
    }

    public static void init() {
        // Send handshake packet to client.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

            /*? if <1.20.5 {*/
            /*if (ServerPlayNetworking.canSend(handler.getPlayer(), SkinShuffle.id("handshake"))) {
                ServerPlayNetworking.send(handler.getPlayer(), SkinShuffle.id("handshake"), net.fabricmc.fabric.api.networking.v1.PacketByteBufs.empty());
            }
            *//*?} else {*/
            if (ServerPlayNetworking.canSend(handler.getPlayer(), HandshakePayload.PACKET_ID)) {
                ServerPlayNetworking.send(handler.getPlayer(), new HandshakePayload());
            }
            /*?}*/

        });

        /*? if <1.20.5 {*/
        /*ServerPlayNetworking.registerGlobalReceiver(SkinShuffle.id("refresh"), (server, player, handler, buf, responseSender) -> {
            try {
                handleSkinRefresh(server, player, buf.readProperty());
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Failed to handle skin refresh packet from " + player.getName().getString() + "\n" + e.getMessage());
            }
        });
        *//*?} else {*/
        ServerPlayNetworking.registerGlobalReceiver(SkinRefreshPayload.PACKET_ID, (payload, context) -> {
            try {
                ServerSkinHandling.handleSkinRefresh(context.server(), context.player(), payload.textureProperty());
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Failed to handle skin refresh packet from " + context.player().getName().getString() + "\n" + e.getMessage());
            }
        });
        /*?}*/
    }
}
