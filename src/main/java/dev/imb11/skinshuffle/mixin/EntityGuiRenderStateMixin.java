package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.client.gui.renderer.InstancedGuiEntityElementRenderer;
import dev.imb11.skinshuffle.client.gui.renderer.InstancedGuiEntityRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityGuiElementRenderState.class)
public class EntityGuiRenderStateMixin implements InstancedGuiEntityRenderState {
    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public InstancedGuiEntityElementRenderer newRenderer(VertexConsumerProvider.Immediate vertexConsumers) {
        return new InstancedGuiEntityElementRenderer(vertexConsumers, MinecraftClient.getInstance().getEntityRenderDispatcher());
    }
}
