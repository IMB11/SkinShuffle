package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.MixinStatics;
import dev.imb11.skinshuffle.client.gui.renderer.InstancedGuiEntityRenderState;
import net.minecraft.client.gui.render.EntityGuiElementRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityGuiElementRenderer.class)
public abstract class EntityGuiRendererMixin extends SpecialGuiElementRenderer<EntityGuiElementRenderState> {
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    protected EntityGuiRendererMixin(VertexConsumerProvider.Immediate vertexConsumers) {
        super(vertexConsumers);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/state/special/EntityGuiElementRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setRenderShadows(Z)V", ordinal = 0))
    public void beforeRenderDispatcher(EntityGuiElementRenderState entityGuiElementRenderState, MatrixStack matrixStack, CallbackInfo ci) {
        MixinStatics.RENDERING_STATE = (InstancedGuiEntityRenderState) (Object) entityGuiElementRenderState;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/state/special/EntityGuiElementRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setRenderShadows(Z)V", ordinal = 1))
    public void afterRenderDispatcher(EntityGuiElementRenderState entityGuiElementRenderState, MatrixStack matrixStack, CallbackInfo ci) {
        MixinStatics.RENDERING_STATE = null;
    }
}
