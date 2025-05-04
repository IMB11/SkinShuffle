package dev.imb11.skinshuffle;

import com.mojang.authlib.GameProfile;
import dev.imb11.skinshuffle.api.MojangSkinAPI;
import dev.imb11.skinshuffle.compat.api.CompatLoader;
import dev.imb11.skinshuffle.networking.HandshakePayload;
import dev.imb11.skinshuffle.networking.RefreshPlayerListEntryPayload;
import dev.imb11.skinshuffle.networking.ServerSkinHandling;
import dev.imb11.skinshuffle.networking.SkinRefreshPayload;
import dev.imb11.skinshuffle.util.SkinCacheRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinShuffle implements ModInitializer {
    public static final String MOD_ID = "skinshuffle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path DATA_DIR = FabricLoader.getInstance().getConfigDir().resolve("skinshuffle");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(
                SkinRefreshPayload.PACKET_ID,
                SkinRefreshPayload.PACKET_CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                HandshakePayload.PACKET_ID,
                PacketCodec.unit(HandshakePayload.INSTANCE)
        );
        PayloadTypeRegistry.playS2C().register(
                RefreshPlayerListEntryPayload.PACKET_ID,
                RefreshPlayerListEntryPayload.PACKET_CODEC
        );

        ensureDataDir();
        SkinCacheRegistry.initialize();
        ServerSkinHandling.init();
        CompatLoader.init();

        MixinStatics.INITIAL_SKIN_TEXTURES = CompletableFuture.supplyAsync(this::getInitialSkinTextures);
    }

    private Optional<SkinTextures> getInitialSkinTextures() {
        while (MinecraftClient.getInstance() == null) {
            Thread.onSpinWait();
        }
        while (MinecraftClient.getInstance().getSkinProvider() == null) {
            Thread.onSpinWait();
        }

        MinecraftClient client = MinecraftClient.getInstance();

        try {
            assert client != null;
            var tex = MojangSkinAPI.getPlayerSkinTexture(String.valueOf(client.getGameProfile().getId()));
            var texProperty = tex.toProperty();
            var dummyProfile = new GameProfile(UUID.randomUUID(), "dummyname");
            dummyProfile.getProperties().put("textures", texProperty);
            //? if <1.21.4 {
            /*return client.getSkinProvider().fetchSkinTextures(dummyProfile).thenApply(Optional::of).get();
             *///?} else {
            return client.getSkinProvider().fetchSkinTextures(dummyProfile).get();
            //?}
        } catch (Exception error) {
            LOGGER.error("Failed to fetch initial skin textures from Mojang's API.", error);
            return Optional.of(client.getSkinProvider().getSkinTextures(client.getGameProfile()));
        }
    }

    private void ensureDataDir() {
        if (!DATA_DIR.toFile().exists()) {
            try {
                Files.createDirectories(DATA_DIR);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + DATA_DIR, e);
            }
        }
    }
}
