package dev.imb11.skinshuffle.client.gui.renderer;

import dev.imb11.skinshuffle.client.SkinShuffleClient;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.skin.Skin;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GuiEntityRenderer {

    public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size,
                                  float rotation, double mouseX, double mouseY, Skin skin,
                                  SkinShuffleConfig.SkinRenderStyle style) {
        
        // Calculate center position
        int centerX = (x1 + x2) / 2;
        int centerY = (y1 + y2) / 2;
        
        // Calculate head rotation based on style
        float headYaw = 0.0f;
        float headPitch = 0.0f;
        
        if (style == SkinShuffleConfig.SkinRenderStyle.CURSOR) {
            // Calculate mouse position relative to the render area center
            float deltaX = (float) (centerX - mouseX);
            float deltaY = (float) (mouseY - centerY);
            
            // Use atan2 for smooth, natural head rotation
            // Scale the input to create a reasonable sensitivity
            float sensitivity = 0.003f; // Adjust this value to control sensitivity
            headYaw = (float) Math.toDegrees(Math.atan(deltaX * sensitivity));
            headPitch = (float) Math.toDegrees(Math.atan(deltaY * sensitivity));
        }
        
        // Create player render state
        PlayerEntityRenderState renderState = createPlayerRenderState(skin, headYaw, headPitch, rotation);
        
        // Create base rotation quaternion (try Z-axis rotation if Y-axis doesn't work)
//        System.out.println(rotation);
        Quaternionf baseRotation = new Quaternionf();
        baseRotation.rotationZ((float) (Math.PI * 1.0f));
        
        // Calculate entity position (negative Y to fix upside down rendering)
        Vector3f entityPosition = new Vector3f(0.0F, 1.1F, 0.0F);
        
        // Render the entity within the scissor area
        context.enableScissor(x1, y1, x2, y2);
        context.addEntity(
                renderState,
                size,
                entityPosition,
                baseRotation,
                null,
                x1, y1, x2, y2
        );
        context.disableScissor();
    }

    private static PlayerEntityRenderState createPlayerRenderState(Skin skin, float headYaw, float headPitch, float rotation) {
        PlayerEntityRenderState state = new PlayerEntityRenderState();

        // Basic entity properties
        state.age = SkinShuffleClient.TOTAL_TICK_DELTA;
        state.width = 0.6F;
        state.height = 1.8F;
        state.standingEyeHeight = 1.62F;
        state.invisible = false;
        state.sneaking = false;
        state.onFire = false;

        // Body orientation - keep body facing forward
        state.bodyYaw = headYaw * 0.1f + rotation;
        state.relativeHeadYaw = headYaw; // Only the head turns with mouse
        state.pitch = headPitch; // Head pitch
        state.deathTime = 0.0F;

        // Gentle arm swaying animation
        float animationTime = SkinShuffleClient.TOTAL_TICK_DELTA * 0.067F;
        state.limbSwingAnimationProgress = MathHelper.sin(animationTime) * 0.05F;
        state.limbSwingAmplitude = 0.1F;

        // Standard entity state
        state.baseScale = 1.0F;
        state.ageScale = 1.0F;
        state.flipUpsideDown = false; // Ensure this is explicitly false
        state.shaking = false;
        state.baby = false;
        state.touchingWater = false;
        state.usingRiptide = false;
        state.hurt = false;
        state.invisibleToPlayer = false;
        state.hasOutline = false;
        state.sleepingDirection = null;
        state.customName = null;
        state.pose = EntityPose.STANDING;

        // Biped state - keep everything neutral
        state.leaningPitch = 0.0F;
        state.handSwingProgress = 0.0F;
        state.limbAmplitudeInverse = 1.0F;
        state.crossbowPullTime = 0.0F;
        state.itemUseTime = 0;
        state.isInSneakingPose = false;
        state.isGliding = false;
        state.isSwimming = false;
        state.hasVehicle = false;
        state.isUsingItem = false;
        state.leftWingPitch = 0.0F;
        state.leftWingYaw = 0.0F;
        state.leftWingRoll = 0.0F;

        // Player-specific properties
        state.skinTextures = skin.getSkinTextures();
        state.name = String.valueOf(skin.hashCode());
        state.spectator = false;
        state.stuckArrowCount = 0;
        state.stingerCount = 0;
        state.itemUseTimeLeft = 0;
        state.handSwinging = false;
        state.glidingTicks = 0.0F;
        state.applyFlyingRotation = false;
        state.flyingRotation = 0.0F;

        // Skin layer visibility
        state.hatVisible = true;
        state.jacketVisible = true;
        state.leftPantsLegVisible = true;
        state.rightPantsLegVisible = true;
        state.leftSleeveVisible = true;
        state.rightSleeveVisible = true;
        state.capeVisible = SkinShuffleConfig.get().showCapeInPreview;

        // Misc properties
        state.playerName = null;
        state.leftShoulderParrotVariant = null;
        state.rightShoulderParrotVariant = null;
        state.id = 0;

        return state;
    }
}