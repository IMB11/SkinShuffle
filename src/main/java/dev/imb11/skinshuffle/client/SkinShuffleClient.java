package dev.imb11.skinshuffle.client;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.util.KeybindManager;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import net.fabricmc.api.ClientModInitializer;

public class SkinShuffleClient implements ClientModInitializer {
    public static float TOTAL_TICK_DELTA = 0;

    @Override
    public void onInitializeClient() {
        SkinShuffleConfig.load();

        SkinPresetManager.setup();
        SkinPresetManager.loadPresets();
        KeybindManager.init();
        
        ClientSkinHandling.init();
    }
}
