package com.mineblock11.skinshuffle.networking;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerSkinHandling {
    /**
     * Player UUIDs that currently have a cooldown.
     */
    private static final ArrayList<String> LOCKED_PLAYERS = new ArrayList<>();

    /**
     * Player UUIDs that are currently in the refresh process.
     */
    private static final ArrayList<String> CURRENTLY_REFRESHING = new ArrayList<>();

    /**
     * Player UUIDs that are currently waiting for the cooldown to expire before starting the refresh process.
     */
    private static final ArrayList<String> PLAYERS_WITH_SCHEDULERS = new ArrayList<>();


    private static void handlePresetChange(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String uuidAsString = player.getUuidAsString();
        if(CURRENTLY_REFRESHING.contains(uuidAsString)) return;
        if(LOCKED_PLAYERS.contains(uuidAsString)) {
            if(!PLAYERS_WITH_SCHEDULERS.contains(uuidAsString)) {
                CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS, server).execute(() -> {
                    LOCKED_PLAYERS.remove(uuidAsString);
                    PLAYERS_WITH_SCHEDULERS.remove(uuidAsString);
                    if(player.isDisconnected()) return;
                    handlePresetChange(server, player, handler, buf, responseSender);
                });
            }

            if(!PLAYERS_WITH_SCHEDULERS.contains(uuidAsString)) PLAYERS_WITH_SCHEDULERS.add(uuidAsString);

            return;
        }

        LOCKED_PLAYERS.add(uuidAsString);
        CURRENTLY_REFRESHING.add(uuidAsString);

        responseSender.sendPacket(SkinShuffle.id("reset_cooldown"), PacketByteBufs.empty());

        var result = MojangSkinAPI.getPlayerSkinTexture(uuidAsString);
        SkinShuffle.LOGGER.info("recieved packet");
        Property skinData = result.toProperty();

        server.execute(() -> {
            var properties = player.getGameProfile().getProperties();
            try {
                properties.removeAll("textures");
            } catch (Exception ignored) {
            }

            try {
                properties.put("textures", skinData);
            } catch (Error e) {
                SkinShuffle.LOGGER.error("Failed to refresh GameProfile for " + player.getName(), e.getMessage());
            }

            SkinShufflePlayer skinShufflePlayer = (SkinShufflePlayer) player;
            skinShufflePlayer.skinShuffle$refreshSkin();

            CURRENTLY_REFRESHING.remove(uuidAsString);
        });
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            ServerSkinHandling.PLAYERS_WITH_SCHEDULERS.clear();
            ServerSkinHandling.LOCKED_PLAYERS.clear();
            ServerSkinHandling.CURRENTLY_REFRESHING.clear();
        });
        ServerPlayNetworking.registerGlobalReceiver(SkinShuffle.id("preset_changed"), ServerSkinHandling::handlePresetChange);
    }
}
