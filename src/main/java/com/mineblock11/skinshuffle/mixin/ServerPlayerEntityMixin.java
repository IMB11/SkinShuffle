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

import com.mineblock11.skinshuffle.networking.ServerSkinHandling;
import com.mineblock11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Most mixin methods in this class are credit to FabricTailor and various mods that are licesned under LGPL or GPL.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements SkinShufflePlayer {
    @Shadow public abstract ServerWorld getServerWorld();

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;

    @Shadow public abstract void sendAbilitiesUpdate();

    @Shadow public abstract boolean isDisconnected();

    protected ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    /**
     * @author Pyrofab
     * <p>
     * This method has been adapted from the Impersonate mod's <a href="https://github.com/Ladysnake/Impersonate/blob/1.16/src/main/java/io/github/ladysnake/impersonate/impl/ServerPlayerSkins.java">source code</a>
     * under GNU Lesser General Public License.
     * <p>
     * Reloads player's skin for all the players (including the one that has changed the skin)
     * </p>
     */
    @Override
    public void skinShuffle$refreshSkin() {
        if(this.isDisconnected()) return;

        // Refreshing in tablist for each player
        PlayerManager playerManager = this.getServer().getPlayerManager();
        playerManager.sendToAll(new PlayerRemoveS2CPacket(new ArrayList<>(Collections.singleton(this.getUuid()))));
        playerManager.sendToAll(PlayerListS2CPacket.entryFromPlayer(Collections.singleton((ServerPlayerEntity) (Object) this)));

        ServerChunkManager manager = this.getServerWorld().getChunkManager();
        ThreadedAnvilChunkStorage storage = manager.threadedAnvilChunkStorage;
        ThreadedAnvilChunkStorage.EntityTracker trackerEntry = storage.entityTrackers.get(this.getId());

        PacketByteBuf refreshPlayerListEntryPacket = ServerSkinHandling.createEntityIdPacket(getId());

        // Refreshing skin in world for all that see the player
        trackerEntry.listeners.forEach(tracking -> {
            if (!ServerSkinHandling.trySendRefreshPlayerListEntry(tracking.getPlayer(), refreshPlayerListEntryPacket)) {
                trackerEntry.entry.startTracking(tracking.getPlayer());
            }
        });

        if (!ServerSkinHandling.trySendRefreshPlayerListEntry((ServerPlayerEntity) (Object) this, refreshPlayerListEntryPacket)) {
            // If we could not send refresh packet, we change the player entity on the client
            ServerWorld level = this.getServerWorld();
//            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(
//                    new CommonPlayerSpawnInfo(
//                            level.getDimensionKey(),
//                            level.getRegistryKey(),
//                            BiomeAccess.hashSeed(level.getSeed()),
//                            this.interactionManager.getGameMode(),
//                            this.interactionManager.getPreviousGameMode(),
//                            level.isDebugWorld(),
//                            level.isFlat(),
//                            this.getLastDeathPos(),
//                            this.getPortalCooldown()), (byte) 3
//            ));
            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(level.getDimensionKey(), level.getRegistryKey(), BiomeAccess.hashSeed(level.getSeed()), this.interactionManager.getGameMode(), this.interactionManager.getPreviousGameMode(), level.isDebugWorld(), level.isFlat(), (byte) 3, this.getLastDeathPos(),this.getPortalCooldown()));

            this.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), Collections.emptySet(), 0));
            this.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.getInventory().selectedSlot));

            this.networkHandler.sendPacket(new DifficultyS2CPacket(level.getDifficulty(), level.getLevelProperties().isDifficultyLocked()));
            this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            playerManager.sendWorldInfo((ServerPlayerEntity) (Object) this, level);
            playerManager.sendCommandTree((ServerPlayerEntity) (Object) this);

            this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.getHungerManager().getFoodLevel(), this.getHungerManager().getSaturationLevel()));

            for (StatusEffectInstance statusEffect : this.getStatusEffects()) {
                this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), statusEffect));
            }

            var equipmentList = new ArrayList<Pair<EquipmentSlot, ItemStack>>();
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                if (!itemStack.isEmpty()) {
                    equipmentList.add(new Pair<>(equipmentSlot, itemStack.copy()));
                }
            }

            if (!equipmentList.isEmpty()) {
                this.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(this.getId(), equipmentList));
            }


            if (!this.getPassengerList().isEmpty()) {
                this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(this));
            }
            if (this.hasVehicle()) {
                this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(this.getVehicle()));
            }

            this.sendAbilitiesUpdate();
            playerManager.sendPlayerStatus((ServerPlayerEntity) (Object) this);
        }
    }
}
