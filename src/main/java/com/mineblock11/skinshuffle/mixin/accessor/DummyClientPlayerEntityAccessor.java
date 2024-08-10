package com.mineblock11.skinshuffle.mixin.accessor;

import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DummyClientPlayerEntity.class, remap = false)
public interface DummyClientPlayerEntityAccessor {
    //? if >=1.20.4 {
    @Accessor(value = "skinTextures", remap = false)
    void setSkinTextures(net.minecraft.client.util.SkinTexturesSkinTextures skinTextures);
    //?} else {
    /*@Accessor(value = "skinIdentifier", remap = false)
    void setSkinIdentifier(Identifier skinIdentifier);
    *///?}
}
