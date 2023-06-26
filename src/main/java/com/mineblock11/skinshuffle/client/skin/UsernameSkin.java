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
import com.mineblock11.skinshuffle.api.SkinQueryResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UsernameSkin extends UUIDSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("username");
    public static final Codec<UsernameSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("username").forGetter(skin -> skin.username),
            Codec.STRING.fieldOf("model").forGetter(UsernameSkin::getModel)
    ).apply(instance, UsernameSkin::new));

    private final String username;

    public UsernameSkin(String username, String model) {
        super(model);
        this.username = username;
    }

    @Override
    public ConfigSkin saveToConfig() {
        Optional<UUID> uuid = SkinAPIs.getUUIDFromUsername(this.username);
        if(uuid.isEmpty()) throw new RuntimeException("UUID is not a valid player UUID.");
        SkinQueryResult queryResult = SkinAPIs.getPlayerSkinTexture(uuid.get().toString());
        this.url = queryResult.skinURL();
        return super.saveToConfig();
    }

    @Override
    protected Object getTextureUniqueness() {
        return username;
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        var uuid = SkinAPIs.getUUIDFromUsername(username);

        if (uuid.isPresent()) {
            this.uuid = uuid.get();
        } else {
            return null;
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
        if (!super.equals(o)) return false;

        UsernameSkin that = (UsernameSkin) o;

        return Objects.equals(username, that.username) && super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
