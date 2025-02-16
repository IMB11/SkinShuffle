package dev.imb11.skinshuffle.compat;

import traben.entity_texture_features.features.ETFRenderContext;

public class ETFCompat {
    public static void preventRenderLayerIssue() {
        ETFRenderContext.preventRenderLayerTextureModify();
    }
}
