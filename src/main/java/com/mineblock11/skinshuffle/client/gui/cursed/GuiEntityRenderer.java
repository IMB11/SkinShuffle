/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.skin.Skin;
import com.mineblock11.skinshuffle.compat.ETFCompat;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public class GuiEntityRenderer {
    /**
     * Render a player in the GUI.
     */
    static float totalDeltaTicks = 0;
    public static void drawEntity(MatrixStack matrices, int x, int y, int size, float rotation, double mouseX, double mouseY, Skin skin) {
        totalDeltaTicks += MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan((mouseY) / 40.0F);

        /*? if >=1.20.5 {*/
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.translate(0.0f, 0.0f, 1000.0f);
        /*?} else {*/
        /*MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.translate(0.0, 0.0, 1000.0);
        *//*?}*/
        RenderSystem.applyModelViewMatrix();

        matrices.push();
        matrices.translate(x, y, -950.0);
        matrices.multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
        matrices.translate(0, -1, 0);
        matrices.multiply(new Quaternionf().rotateZ(rotation));
        matrices.translate(0, -1, 0);
        DiffuseLighting.method_34742();

        var modelData = PlayerEntityModel.getTexturedModelData(Dilation.NONE, false);
        NoEntityPlayerModel model = new NoEntityPlayerModel(TexturedModelData.of(modelData, 64, 64).createModel(), false);

        model.swingArmsGently(totalDeltaTicks);

        model.setHeadPos(yaw, pitch);

        if(FabricLoader.getInstance().isModLoaded("entity_texture_features")) {
            ETFCompat.preventRenderLayerIssue();
        }

        VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        model.render(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin.getTexture())),
                0,
                OverlayTexture.DEFAULT_UV,
                0xFFFFFFFF);
        vertexConsumers.draw();

        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();

        /*? if >=1.20.5 {*/
        modelViewStack.popMatrix();
        /*?} else {*/
        /*modelViewStack.pop();
        *//*?}*/
        RenderSystem.applyModelViewMatrix();
    }

    public static class NoEntityPlayerModel extends PlayerEntityModel {
        public NoEntityPlayerModel(ModelPart root, boolean thinArms) {
            super(root, thinArms);
            this.child = false;
        }

        public void swingArmsGently(float totalDeltaTick) {
            float f = MathHelper.sin(totalDeltaTick * 0.067F) * 0.05F;
            this.rightArm.roll = f + 0.06F;
            this.rightSleeve.roll = f + 0.06F;
            this.leftArm.roll = -f - 0.06F;
            this.leftSleeve.roll = -f - 0.06F;
        }

        public void setHeadPos(float headYaw, float headPitch) {
            this.head.yaw = headYaw;
            this.hat.yaw = headYaw;
            this.head.pitch = headPitch;
            this.hat.pitch = headPitch;
        }
    }

//    @SuppressWarnings("deprecation")
//    private static void drawEntity(MatrixStack matrices, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
//        if(entity == null) return;
//        /*? if <1.20.5 { *//*
//        MatrixStack matrixStack = RenderSystem.getModelViewStack();
//        matrixStack.push();
//        matrixStack.translate(0.0, 0.0, 1000.0);
//        *//*? } else { */
//        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
//        matrixStack.pushMatrix();
//        matrixStack.translate(0.0F, 0.0F, 1000.0F);
//        /*? }*/
//        RenderSystem.applyModelViewMatrix();
//        matrices.push();
//        matrices.translate(x, y, -950.0);
//        matrices.multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
//        matrices.translate(0, -1, 0);
//        matrices.multiply(quaternionf);
//        matrices.translate(0, -1, 0);
//        DiffuseLighting.method_34742();
//        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
//        if (quaternionf2 != null) {
//            quaternionf2.conjugate();
//            entityRenderDispatcher.setRotation(quaternionf2);
//        }
//        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//        entityRenderDispatcher.setRenderShadows(false);
//        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrices, immediate, 0xF000F0);
//        entityRenderDispatcher.setRenderShadows(true);
//        immediate.draw();
//        matrices.pop();
//        DiffuseLighting.enableGuiDepthLighting();
//
//        /*? if <1.20.5 { *//*
//        matrixStack.pop();
//        *//*? } else { */
//        matrixStack.popMatrix();
//        /*? }*/
//
//        RenderSystem.applyModelViewMatrix();
//    }
}
