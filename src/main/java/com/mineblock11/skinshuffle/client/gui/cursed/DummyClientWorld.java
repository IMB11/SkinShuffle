package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mineblock11.skinshuffle.SkinShuffle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.Difficulty;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(
                DummyClientPlayNetworkHandler.getInstance(),
                new Properties(Difficulty.EASY, false, true),
                RegistryKey.of(RegistryKeys.WORLD, SkinShuffle.id("dummy")),
                DummyClientPlayNetworkHandler.CURSED_DIMENSION_TYPE_REGISTRY.entryOf(
                        RegistryKey.of(RegistryKeys.DIMENSION_TYPE, SkinShuffle.id("dummy"))),
                0,
                0,
                () -> MinecraftClient.getInstance().getProfiler(),
                MinecraftClient.getInstance().worldRenderer,
                false,
                0L
        );
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return super.getRegistryManager();
    }
}
