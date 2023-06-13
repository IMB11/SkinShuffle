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

package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class BackedSkin implements Skin {
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

        if (textureManager.getOrDefault(id, null) == null) {
            // Texture doesn't exist, we need to fetch it.
            fetching = true;

            new Thread(() -> {
                var texture = loadTexture(() -> {
                    fetching = false;
                    fetched = true;
                    setTexture(id);
                });

                if (texture != null) {
                    textureManager.registerTexture(id, texture);
                }
            }, getClass().getName() + "Fetcher").start();
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

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() throws Throwable {
        try {
            if (textureId != null) {
                decrementInstanceCountAndCleanup(textureId);
            }
        } finally {
            super.finalize();
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
