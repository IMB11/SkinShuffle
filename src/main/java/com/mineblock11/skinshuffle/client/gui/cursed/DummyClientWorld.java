package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mineblock11.skinshuffle.SkinShuffle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

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
                new CursedRegistryEntry<>(new DimensionType(
                        OptionalLong.of(6000L),
                        true,
                        false,
                        false,
                        true,
                        1.0,
                        true,
                        false,
                        -64,
                        384,
                        384,
                        BlockTags.INFINIBURN_OVERWORLD,
                        DimensionTypes.OVERWORLD_ID,
                        0.0f,
                        new DimensionType.MonsterSettings(
                                false,
                                true,
                                UniformIntProvider.create(0, 7),
                                0
                        )
                ), RegistryKeys.DIMENSION_TYPE),
                0,
                0,
                () -> MinecraftClient.getInstance().getProfiler(),
                MinecraftClient.getInstance().worldRenderer,
                false,
                0L
        );
    }
}
