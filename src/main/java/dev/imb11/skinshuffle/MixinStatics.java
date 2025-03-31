package dev.imb11.skinshuffle;

import net.minecraft.client.util.SkinTextures;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MixinStatics {
    public static boolean APPLIED_SKIN_MANAGER_CONFIGURATION = false;
    public static CompletableFuture<Optional<SkinTextures>> INITIAL_SKIN_TEXTURES = null;
}
