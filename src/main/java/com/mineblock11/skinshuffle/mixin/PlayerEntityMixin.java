/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
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
            if(this.getUuid().equals(MinecraftClient.getInstance().player.getUuid()) && (!NetworkingUtil.isLoggedIn() || SkinShuffleConfig.get().disableAPIUpload)) {
                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
                cir.setReturnValue(Objects.requireNonNullElse(currentPreset.getSkin().getTexture(), new Identifier("textures/skins/default/steve.png")));
            }
        }
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void modifySkinModel(CallbackInfoReturnable<String> cir) {
        if(MinecraftClient.getInstance().world != null) {
            if(this.getUuid().equals(MinecraftClient.getInstance().player.getUuid()) && (!NetworkingUtil.isLoggedIn() || SkinShuffleConfig.get().disableAPIUpload)) {
                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
                cir.setReturnValue(Objects.requireNonNullElse(currentPreset.getSkin().getModel(), "default"));
            }
        }
    }
}
