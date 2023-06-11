package com.mineblock11.skinshuffle.mixin.render;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity {
    protected PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    private void modifySkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if(MinecraftClient.getInstance().world != null) {
            if(!AuthUtil.isLoggedIn() && this.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                SkinPreset currentPreset = SkinShuffleConfig.getChosenPreset();
                cir.setReturnValue(Objects.requireNonNullElse(currentPreset.getSkin().getTexture(), new Identifier("textures/skins/default/steve.png")));
            }
        }
    }
}
