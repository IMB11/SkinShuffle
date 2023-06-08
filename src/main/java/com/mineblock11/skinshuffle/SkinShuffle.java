package com.mineblock11.skinshuffle;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class SkinShuffle implements ModInitializer {
    public static final String MOD_ID = "skinshuffle";

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
