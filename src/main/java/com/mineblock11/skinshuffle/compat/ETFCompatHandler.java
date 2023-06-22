package com.mineblock11.skinshuffle.compat;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;

/**
 * Compatability for Entity Texture Features.
 * <p>
 * <a href="https://modrinth.com/mod/entitytexturefeatures">Modrinth</a>
 */
public class ETFCompatHandler implements CompatHandler {
    // Disable the config option in the screen.
    public static boolean DISABLE_RENDER_DESYNC = false;

    @Override
    public String getID() {
        return "entity_texture_features";
    }

    @Override
    public void execute() {
        // ETF's blink and other model features do not work when render desync is enabled.
        DISABLE_RENDER_DESYNC = true;
        SkinShuffleConfig.get().renderClientSkinRegardless = false;
        SkinShuffleConfig.GSON.save();
    }
}
