

package dev.imb11.skinshuffle.networking;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.util.SkinShufflePlayer;
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
     *
     * @param player   The player to refresh the entry for.
     * @param entityID The entity ID of the player.
     * @return Whether the refresh was successful.
     */
    public static boolean attemptPlayerListEntryRefresh(ServerPlayerEntity player, int entityID) {
        if (ServerPlayNetworking.canSend(player, RefreshPlayerListEntryPayload.PACKET_ID)) {
            ServerPlayNetworking.send(player, new RefreshPlayerListEntryPayload(entityID));
            return true;
        }
        return false;
    }

    public static void init() {
        // Send handshake packet to client.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(handler.getPlayer(), HandshakePayload.PACKET_ID)) {
                ServerPlayNetworking.send(handler.getPlayer(), new HandshakePayload());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SkinRefreshPayload.PACKET_ID, (payload, context) -> {
            try {
                ServerSkinHandling.handleSkinRefresh(context.server(), context.player(), payload.textureProperty());
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Failed to handle skin refresh packet from " + context.player().getName().getString() + "\n" + e.getMessage());
            }
        });
    }
}
