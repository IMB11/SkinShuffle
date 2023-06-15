package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;

public class SkinShuffleConfigScreen {
    public static Screen get(Screen parent) {
        return SkinShuffleConfig.getInstance().generateScreen(parent);
    }
}
