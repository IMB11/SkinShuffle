package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private final SkinPreset skinPreset;
    private PlayerEntity player = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    public DummyClientPlayerEntity(SkinPreset skinPreset) {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        setUuid(UUID.randomUUID());
        this.skinPreset = skinPreset;
    }

    private String getUserUsername() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public SkinTextures getSkinTextures() {
        @Nullable Identifier presetTexture = skinPreset.getSkin().getTexture();
        if(presetTexture != null) {
            return new SkinTextures(presetTexture, null, null, null, SkinTextures.Model.fromName(skinPreset.getSkin().getModel()), true);
        }
        return DefaultSkinHelper.getSkinTextures(getUuid());
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        return false;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
    }


    @Override
    public void updateCapeAngles() {}

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return equippedStackSupplier.apply(slot);
    }
}
