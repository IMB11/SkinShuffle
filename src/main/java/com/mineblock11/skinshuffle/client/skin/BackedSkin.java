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

package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class BackedSkin implements Skin, AutoCloseable {
    // Keep track of how many instances exist for each texture id, so we can clean them up when they're no longer used
    // It should be fine to use the same map for all subclasses, since the texture ids will be unique anyway
    public static final Object2IntMap<Identifier> INSTANCE_COUNTS = new Object2IntOpenHashMap<>();

    private boolean fetching = false;
    private boolean fetched = false;
    @Nullable
    private Identifier textureId;

    public void fetchSkin() {
        var id = SkinShuffle.id("skin/" + getSerializationId().getPath() + "/" + Math.abs(getTextureUniqueness().hashCode()));
        var textureManager = MinecraftClient.getInstance().getTextureManager();

        //? if <1.21.4 {
        /*if (textureManager.getOrDefault(id, null) == null) {
        *///?} else {
        if (textureManager.textures.get(id) == null) {
        //?}
            // Texture doesn't exist, we need to fetch it.
            fetching = true;

            CompletableFuture.runAsync(() -> {
                try {
                    var texture = loadTexture(() -> {
                        fetching = false;
                        fetched = true;
                        setTexture(id);
                    });

                    if (texture != null) {
                        textureManager.registerTexture(id, texture);
                    } else {
                        fetching = false;
                        fetched = true;
                        setTexture(null);
                    }
                } catch (Exception e) {
                    SkinShuffle.LOGGER.warn("Failed to load skin texture", e);
                    fetching = false;
                    fetched = true;
                    setTexture(null);
                }
            }, Util.getMainWorkerExecutor());
        } else {
            // Texture already exists, we assume it hasn't changed
            fetched = true;
            setTexture(id);
        }
    }

    @Override
    public @Nullable Identifier getTexture() {
        // Fetch the skin if it hasn't been fetched yet and isn't being fetched
        if (textureId == null && !fetching && !fetched) {
            fetchSkin();
        }

        return textureId;
    }

    @Override
    public boolean isLoading() {
        return !fetched || fetching;
    }

    protected void setTexture(Identifier textureId) {
        // Manages instance counts when the texture id changes
        if (this.textureId != null) {
            decrementInstanceCountAndCleanup(this.textureId);
        }
        this.textureId = textureId;
        incrementInstanceCount(textureId);
    }

    protected abstract Object getTextureUniqueness();

    protected abstract @Nullable AbstractTexture loadTexture(Runnable completionCallback);

    @Override
    public void close() {
        if (textureId != null) {
            decrementInstanceCountAndCleanup(textureId);
        }
    }

    private static void incrementInstanceCount(Identifier id) {
        INSTANCE_COUNTS.compute(id, (k, v) -> v == null ? 1 : v + 1);
    }

    public static void decrementInstanceCountAndCleanup(Identifier id) {
        INSTANCE_COUNTS.compute(id, (k, v) -> v == null ? 0 : v - 1);
        if (INSTANCE_COUNTS.getInt(id) == 0) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
            INSTANCE_COUNTS.removeInt(id);
        }
    }
}
