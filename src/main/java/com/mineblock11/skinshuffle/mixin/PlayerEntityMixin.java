/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.SkinShuffleClientPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends PlayerEntity implements SkinShuffleClientPlayer {
    @Shadow @Nullable private PlayerListEntry playerListEntry;

    protected PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    /*? if <1.20.4 {*//*
    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    private void modifySkinTexture(CallbackInfoReturnable<net.minecraft.util.Identifier> cir) {
        if(MinecraftClient.getInstance().world != null) {
            if(this.getUuid().equals(MinecraftClient.getInstance().player.getUuid()) && (!NetworkingUtil.isLoggedIn() || SkinShuffleConfig.get().disableAPIUpload || ClientSkinHandling.isReconnectRequired())) {
                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
                cir.setReturnValue(java.util.Objects.requireNonNullElse(currentPreset.getSkin().getTexture(), new net.minecraft.util.Identifier("textures/skins/default/steve.png")));
            }
        }
    }
    /*? } else { */
    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    private void modifySkinModel(CallbackInfoReturnable<net.minecraft.client.util.SkinTextures> cir) {
        if(MinecraftClient.getInstance().world != null) {
            if(this.getUuid().equals(MinecraftClient.getInstance().player.getUuid()) && (!NetworkingUtil.isLoggedIn() || SkinShuffleConfig.get().disableAPIUpload || ClientSkinHandling.isReconnectRequired())) {
                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
                cir.setReturnValue(currentPreset.getSkin().getSkinTextures());
            }
        }
    }
    /*?}*/

    @Override
    public void skinShuffle$refreshPlayerListEntry() {
        playerListEntry = null;
    }
}
