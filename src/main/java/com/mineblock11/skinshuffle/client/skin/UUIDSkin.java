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
import com.mineblock11.skinshuffle.api.SkinAPIs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UUIDSkin extends UrlSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("uuid");
    public static final Codec<UUIDSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("uuid").forGetter(skin -> skin.uuid.toString()),
            Codec.STRING.optionalFieldOf("model").forGetter(skin -> Optional.ofNullable(skin.model))
    ).apply(instance, (uuid, model) -> new UUIDSkin(UUID.fromString(uuid), model.orElse(null))));

    protected UUID uuid;

    public UUIDSkin(UUID uuid, @Nullable String model) {
        super(model);
        this.uuid = uuid;
    }

    protected UUIDSkin(@Nullable String model) {
        super(model);
    }

    @Override
    protected Object getTextureUniqueness() {
        return uuid;
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        var profile = SkinAPIs.getPlayerSkinTexture(uuid.toString());

        if (profile.skinURL() == null) {
            return null;
        }

        url = profile.skinURL();
        if (model == null) {
            model = profile.modelType();
            cacheModel();
        }

        return super.loadTexture(completionCallback);
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UUIDSkin uuidSkin = (UUIDSkin) o;

        return Objects.equals(uuid, uuidSkin.uuid) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
