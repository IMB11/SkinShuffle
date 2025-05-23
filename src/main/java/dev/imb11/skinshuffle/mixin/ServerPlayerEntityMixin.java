package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.networking.ServerSkinHandling;
import dev.imb11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Most mixin methods in this class are credit to FabricTailor and various mods that are licensed under LGPL or GPL.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements SkinShufflePlayer {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;

    protected ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public abstract ServerWorld getServerWorld();

    @Shadow
    public abstract void sendAbilitiesUpdate();

    @Shadow
    public abstract boolean isDisconnected();

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
        if (this.isDisconnected()) return;

        // Refreshing in tablist for each player
        PlayerManager playerManager = this.getServer().getPlayerManager();
        playerManager.sendToAll(new PlayerRemoveS2CPacket(new ArrayList<>(Collections.singleton(this.getUuid()))));
        playerManager.sendToAll(PlayerListS2CPacket.entryFromPlayer(Collections.singleton((ServerPlayerEntity) (Object) this)));

        ServerChunkManager manager = this.getServerWorld().getChunkManager();

        /*? if <1.21 {*/
        /*var storage = manager.threadedAnvilChunkStorage;
        var trackerEntry = storage.entityTrackers.get(this.getId());
        *//*?} else {*/
        var storage = manager.chunkLoadingManager;
        var trackerEntry = storage.entityTrackers.get(this.getId());
        /*?}*/

        // Refreshing skin in world for all that see the player
        trackerEntry.listeners.forEach(tracking -> {
            if (!ServerSkinHandling.attemptPlayerListEntryRefresh(tracking.getPlayer(), this.getId())) {
                trackerEntry.entry.startTracking(tracking.getPlayer());
            }
        });

        if (!ServerSkinHandling.attemptPlayerListEntryRefresh((ServerPlayerEntity) (Object) this, this.getId())) {
            // If we could not send refresh packet, we change the player entity on the client
            ServerWorld level = this.getServerWorld();

            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(

                    // RegistryEntry<DimensionType> registryEntry,
                    // RegistryKey<World> registryKey,
                    // long l,
                    // GameMode gameMode,
                    // @Nullable GameMode gameMode2,
                    // boolean bl,
                    // boolean bl2,
                    // Optional<GlobalPos> optional,
                    // int i,
                    // int j

                    new CommonPlayerSpawnInfo(
                            level.getDimensionEntry(),
                            level.getRegistryKey(),
                            BiomeAccess.hashSeed(level.getSeed()),
                            this.interactionManager.getGameMode(),
                            this.interactionManager.getPreviousGameMode(),
                            level.isDebugWorld(),
                            level.isFlat(),
                            this.getLastDeathPos(),
                            this.getPortalCooldown()
                            //? if >=1.21.2 {
                            , level.getSeaLevel()
                            //?}
                    ), (byte) 3)
            );

            //? if <1.21.2 {
            /*this.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), Collections.emptySet(), 0));
             *///?} else {
            this.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0, net.minecraft.entity.player.PlayerPosition.fromEntity(this), Collections.emptySet()));
            //?}

            //? if <1.21.5 {
            /*this.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.getInventory().selectedSlot));
             *///?} else {
            this.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.getInventory().getSelectedSlot()));
            //?}

            this.networkHandler.sendPacket(new DifficultyS2CPacket(level.getDifficulty(), level.getLevelProperties().isDifficultyLocked()));
            this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            playerManager.sendWorldInfo((ServerPlayerEntity) (Object) this, level);
            playerManager.sendCommandTree((ServerPlayerEntity) (Object) this);

            this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.getHungerManager().getFoodLevel(), this.getHungerManager().getSaturationLevel()));

            for (StatusEffectInstance statusEffect : this.getStatusEffects()) {
                this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), statusEffect, false));
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