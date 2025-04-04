package dev.imb11.skinshuffle.client.gui.cursed;

import dev.imb11.skinshuffle.client.SkinShuffleClient;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.skin.Skin;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.imb11.skinshuffle.compat.ETFCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public class GuiEntityRenderer {
    public static void drawEntity(MatrixStack matrices, int x, int y, int size, float rotation, double mouseX, double mouseY, Skin skin, SkinShuffleConfig.SkinRenderStyle style) {
        float yaw = (float) (Math.atan2(mouseX, 120.0F));
        float pitch = (float) (Math.atan2(-mouseY, 120.0F));

        Quaternionf entityRotation = new Quaternionf().rotateY(rotation * 0.025f);

        if (style == SkinShuffleConfig.SkinRenderStyle.CURSOR) {
            Quaternionf pitchRotation = new Quaternionf().rotateX(pitch * 10.0F * 0.017453292F);
            Quaternionf yawRotation = new Quaternionf().rotateY(yaw * 10.0F * 0.017453292F);
            entityRotation.mul(pitchRotation);
            entityRotation.mul(yawRotation);
        }

        setupModelViewStack();
        setupMatrices(matrices, x, y, size, entityRotation);

        renderEntity(matrices, yaw, pitch, skin, (long) SkinShuffleClient.TOTAL_TICK_DELTA);

        cleanupMatrices(matrices);
        cleanupModelViewStack();
    }

    private static void setupModelViewStack() {
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.translate(0.0f, 0.0f, 1000.0f);

        //? if <1.21.2 {
        /*RenderSystem.applyModelViewMatrix();
         *///?}
    }

    private static void setupMatrices(MatrixStack matrices, int x, int y, int size, Quaternionf entityRotation) {
        matrices.push();
        matrices.translate(x, y, 100.0);
        matrices.multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
        matrices.translate(0, -1, 0);
        matrices.multiply(entityRotation);
        matrices.translate(0, -1, 0);
        //? if <1.21.5 {
        DiffuseLighting.method_34742();
        //?} else {
        /*DiffuseLighting.enableGuiShaderLighting();
        *///?}
    }

    private static void renderEntity(MatrixStack matrices, float yaw, float pitch, Skin skin, long totalTickDelta) {
        var modelData = PlayerEntityModel.getTexturedModelData(Dilation.NONE, skin.getModel().equals("slim"));
        NoEntityPlayerModel model = new NoEntityPlayerModel(TexturedModelData.of(modelData, 64, 64).createModel(), skin.getModel().equals("slim"));

        model.swingArmsGently(totalTickDelta);
        model.setHeadPos(yaw, pitch);
        model.waveCapeGently(totalTickDelta);

        if (FabricLoader.getInstance().isModLoaded("entity_texture_features")) {
            ETFCompat.preventRenderLayerIssue();
        }

        VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        model.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin.getTexture())),
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
                OverlayTexture.DEFAULT_UV,
                0xFFFFFFFF
        );

        if (skin.getSkinTextures().capeTexture() != null && SkinShuffleConfig.get().showCapeInPreview) {
            matrices.push();
            matrices.translate(0.0F, 0.0F, 0.2F);

            // Get model for cape.
            //? >=1.21.2 {
            //?} else {
            /*model.renderCape(
                    matrices,
                    //? if !=1.20.1 {
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin.getSkinTextures().capeTexture())),
                    //?} else {
                    /^vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(client.getSkinProvider().loadSkin(client.getSkinProvider().getTextures(client.getSession().getProfile()).get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE))),
                    ^///?}
                    0,
                    OverlayTexture.DEFAULT_UV
            );
            *///?}

            matrices.pop();
        }
        vertexConsumers.draw();
    }

    private static void cleanupMatrices(MatrixStack matrices) {
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private static void cleanupModelViewStack() {
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.popMatrix();

        //? if <1.21.2 {
        /*RenderSystem.applyModelViewMatrix();
         *///?}
    }

    public static class NoEntityPlayerModel extends PlayerEntityModel {
        public NoEntityPlayerModel(ModelPart root, boolean thinArms) {
            super(root, thinArms);

            //? <1.21.2 {
            /*this.child = false;
             *///?}
        }

        public void swingArmsGently(long totalDeltaTick) {
            float f = MathHelper.sin(totalDeltaTick * 0.067F) * 0.05F;
            this.rightArm.roll = f + 0.06F;
            this.rightSleeve.roll = f + 0.06F;
            this.leftArm.roll = -f - 0.06F;
            this.leftSleeve.roll = -f - 0.06F;
        }

        public void setHeadPos(float headYaw, float headPitch) {
            this.head.yaw = headYaw;
            this.head.pitch = headPitch;

            //? if =1.21 {
            /*this.hat.yaw = headYaw;
            this.hat.pitch = headPitch;
            *///?}
        }

        public void waveCapeGently(long totalDeltaTick) {
            float f = MathHelper.sin(totalDeltaTick * 0.067F) * 0.05F;

            //? <1.21.2 {
            /*this.cloak.pitch = f;
             *///?}
        }
    }
}
