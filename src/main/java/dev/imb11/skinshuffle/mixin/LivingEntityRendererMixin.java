package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.MixinStatics;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @ModifyArg(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            ),
            index = 4
    )
    private int changeAlpha(int originalColour) {
        if (MixinStatics.RENDERING_STATE == null) return originalColour;

        int alpha = (int) (MathHelper.clamp(MixinStatics.RENDERING_STATE.getAlpha(), 0.0F, 1.0F) * 255.0F) & 0xFF;
        return (alpha << 24) | (originalColour & 0x00FFFFFF);
    }
}
