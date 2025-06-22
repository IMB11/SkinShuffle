package dev.imb11.skinshuffle.util;

import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

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
//            client.world.disconnect(Text.literal("Rejoining world."));
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.rejoining")), false);
        } else {
            folderName = null;
            if (!client.getNetworkHandler().getConnection().isLocal()) {
                serverAddress = client.getNetworkHandler().getServerInfo().address;
            } else {
                serverAddress = null;
            }
//            client.world.disconnect(Text.literal("Rejoining world."));
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.reconnecting")), false);
        }


        if (client.isInSingleplayer()) {
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
