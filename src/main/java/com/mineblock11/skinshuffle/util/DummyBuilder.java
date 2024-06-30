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

package com.mineblock11.skinshuffle.util;

import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import nl.enjarai.cicada.api.cursed.DummyClientPlayNetworkHandler;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import nl.enjarai.cicada.api.cursed.DummyClientWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyBuilder {
    @Nullable
    public static DummyClientPlayerEntity createDummy(@Nullable SkinPreset preset) {
        if(preset == null) {
            return null;
        }

        ClientWorld world = MinecraftClient.getInstance().world;
        
        if(world == null) {
            world = DummyClientWorld.getInstance();
        }

        /*? if <1.20.4 {*/
        /*return new DummyClientPlayerEntity(null, UUID.randomUUID(), preset.getSkin().getTexture(), preset.getSkin().getModel());
        *//*?} else {*/
        return new DummyClientPlayerEntity(null, UUID.randomUUID(), preset.getSkin().getSkinTextures(), world, DummyClientPlayNetworkHandler.getInstance());
        /*?}*/
    }
}
