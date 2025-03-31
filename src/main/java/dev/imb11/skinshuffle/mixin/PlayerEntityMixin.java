package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.imb11.skinshuffle.util.SkinShuffleClientPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
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
    @Shadow
    @Nullable
    private PlayerListEntry playerListEntry;

    protected PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void modifySkinModel(CallbackInfoReturnable<net.minecraft.client.util.SkinTextures> cir) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.world != null) {
//            if (this.getUuid().equals(client.player.getUuid())) {
//                SkinPreset currentPreset = SkinPresetManager.getChosenPreset();
//                cir.setReturnValue(currentPreset.getSkin().getSkinTextures());
//            }
//        }
        cir.setReturnValue(new SkinTextures(SkinShuffle.id("textures/skins/the_rock.png"), null, null, null, SkinTextures.Model.WIDE, false));
    }

    @Override
    public void skinShuffle$refreshPlayerListEntry() {
        playerListEntry = null;
    }
}
