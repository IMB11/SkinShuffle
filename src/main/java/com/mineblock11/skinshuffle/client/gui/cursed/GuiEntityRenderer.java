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

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public class GuiEntityRenderer {
    /**
     * Render a player in the GUI.
     */
    public static void drawEntity(MatrixStack matrices, int x, int y, int size, float rotation, double mouseX, double mouseY, LivingEntity entity) {
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan((mouseY) / 40.0F);

        Quaternionf entityRotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf pitchRotation = new Quaternionf().rotateX(pitch * 20.0F * 0.017453292F);
        entityRotation.mul(pitchRotation);

        float oldBodyYaw = entity.bodyYaw;
        float oldYaw = entity.getYaw();
        float oldPitch = entity.getPitch();
        float oldPrevHeadYaw = entity.prevHeadYaw;
        float oldHeadYaw = entity.headYaw;
        entity.bodyYaw = 180.0F + yaw * 20.0F + rotation;
        entity.setYaw(180.0F + yaw * 40.0F + rotation);
        entity.setPitch(-pitch * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();

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
        matrices.multiply(entityRotation);
        matrices.translate(0, -1, 0);
        DiffuseLighting.method_34742();

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (pitchRotation != null) {
            pitchRotation.conjugate();
            dispatcher.setRotation(pitchRotation);
        }
        dispatcher.setRenderShadows(false);

        VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrices, vertexConsumers, 0xF000F0);
        vertexConsumers.draw();

        dispatcher.setRenderShadows(true);

        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();

        /*? if >=1.20.5 {*/
        modelViewStack.popMatrix();
        /*?} else {*/
        /*modelViewStack.pop();
        *//*?}*/
        RenderSystem.applyModelViewMatrix();

        entity.bodyYaw = oldBodyYaw;
        entity.setYaw(oldYaw);
        entity.setPitch(oldPitch);
        entity.prevHeadYaw = oldPrevHeadYaw;
        entity.headYaw = oldHeadYaw;
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
