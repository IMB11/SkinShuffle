package dev.imb11.skinshuffle.mixin;

import com.mojang.authlib.GameProfile;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.imb11.skinshuffle.compat.CapesCompat;
import dev.imb11.skinshuffle.compat.MinecraftCapesCompat;
import dev.imb11.skinshuffle.util.SkinShuffleClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity implements SkinShuffleClientPlayer {
    @Shadow
    @Nullable
    private PlayerListEntry playerListEntry;

    private @Unique SkinTextures prevTextures;

    public PlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void modifySkinTextures(CallbackInfoReturnable<net.minecraft.client.util.SkinTextures> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && client.player != null) {
            if (this.getUuid().equals(client.player.getUuid())) {
                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
                var textures = currentPreset.getSkin().getSkinTextures();

                if (CapesCompat.IS_INSTALLED) {
                    textures = CapesCompat.loadTextures(this.getGameProfile(), textures);
                } else if (MinecraftCapesCompat.IS_INSTALLED) {
                    textures = MinecraftCapesCompat.loadTextures(uuid, textures);
                }

                if (currentPreset.getSkin().isLoading()) {
                    if (prevTextures != null)
                        cir.setReturnValue(prevTextures);
                    return;
                }
                prevTextures = textures;
                cir.setReturnValue(textures);
            }
        }
    }

    @Override
    public void skinShuffle$refreshPlayerListEntry() {
        this.playerListEntry = null;
    }
}
