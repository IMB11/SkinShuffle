package dev.imb11.skinshuffle;

import dev.imb11.skinshuffle.compat.CompatLoader;
import dev.imb11.skinshuffle.networking.HandshakePayload;
import dev.imb11.skinshuffle.networking.RefreshPlayerListEntryPayload;
import dev.imb11.skinshuffle.networking.ServerSkinHandling;
import dev.imb11.skinshuffle.networking.SkinRefreshPayload;
import dev.imb11.skinshuffle.util.SkinCacheRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SkinShuffle implements ModInitializer {
    public static final String MOD_ID = "skinshuffle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path DATA_DIR = FabricLoader.getInstance().getConfigDir().resolve("skinshuffle");
    public static SoundEvent ITS_ABOUT_DRIVE_ITS_ABOUT_POWER_WE_STAY_HUNGRY_WE_DEVOUR_PUT_IN_THE_WORK_PUT_IN_THE_HOURS_AND_TAKE_WHATS_OURS;

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

        ITS_ABOUT_DRIVE_ITS_ABOUT_POWER_WE_STAY_HUNGRY_WE_DEVOUR_PUT_IN_THE_WORK_PUT_IN_THE_HOURS_AND_TAKE_WHATS_OURS = Registry.register(Registries.SOUND_EVENT, SkinShuffle.id("its_about_drive"), SoundEvent.of(SkinShuffle.id("its_about_drive")));
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
