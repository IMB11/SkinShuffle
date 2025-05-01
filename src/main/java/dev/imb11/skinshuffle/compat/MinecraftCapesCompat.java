package dev.imb11.skinshuffle.compat;

import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraftcapes.config.MinecraftCapesConfig;
import net.minecraftcapes.player.PlayerHandler;

import java.util.UUID;

public class MinecraftCapesCompat implements CompatHandler {

    public static boolean IS_INSTALLED = false;

    @Override
    public String getID() {
        return "minecraftcapes";
    }

    @Override
    public void execute() {
        MinecraftCapesCompat.IS_INSTALLED = true;
    }

    public static SkinTextures loadTextures(UUID uuid, SkinTextures textures) {
        PlayerHandler playerHandler = PlayerHandler.get(uuid);

        Identifier capeTexture = textures.capeTexture();
        Identifier elytraTexture = textures.elytraTexture();

        if(MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
            capeTexture = playerHandler.getCapeLocation();
            elytraTexture = playerHandler.getCapeLocation();
        }

        textures = new SkinTextures(
                textures.texture(),
                textures.textureUrl(),
                capeTexture,
                elytraTexture,
                textures.model(),
                textures.secure()
        );

        return textures;
    }
}
