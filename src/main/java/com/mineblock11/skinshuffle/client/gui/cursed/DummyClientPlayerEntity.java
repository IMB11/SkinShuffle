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

package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static DummyClientPlayerEntity instance;
    private Supplier<Identifier> skinIdentifier = null;
    private Supplier<String> model = null;
    private PlayerEntity player = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity() {
            @Override
            public Text getName() {
                return Text.translatable("gui.showmeyourskin.armorScreen.global");
            }
        };
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        setUuid(UUID.randomUUID());
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                skinIdentifier = () -> identifier;
                var model = texture.getMetadata("model");
                if (model == null) {
                    model = "default";
                }
                var model1 = model;
                this.model = () -> model1;
            }
        }, true);
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, Supplier<Identifier> skinIdentifier, @Nullable Supplier<String> model) {
        this(player, uuid, skinIdentifier, model, DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance());
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, Supplier<Identifier> skinIdentifier, @Nullable Supplier<String> model, ClientWorld world, ClientPlayNetworkHandler networkHandler) {
        super(MinecraftClient.getInstance(), world, networkHandler, null, null,false, false);
        this.player = player;
        setUuid(uuid);
        this.skinIdentifier = skinIdentifier;
        this.model = model;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        if (skinIdentifier != null) {
            var identifier = skinIdentifier.get();
            if (identifier != null) {
                return identifier;
            }
        }
        return DefaultSkinHelper.getTexture(getUuid());
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
    public String getModel() {
        if (model != null) {
            var model = this.model.get();
            if (model != null) {
                return model;
            }
        }
        return DefaultSkinHelper.getModel(getUuid());
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
        if (player != null) {
            return player.getEquippedStack(slot);
        }
        return equippedStackSupplier.apply(slot);
    }
}
