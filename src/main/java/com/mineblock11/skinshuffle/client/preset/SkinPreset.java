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

package com.mineblock11.skinshuffle.client.preset;

import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.client.skin.ResourceSkin;
import com.mineblock11.skinshuffle.client.skin.Skin;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.util.Identifier;

public class SkinPreset {
    public static final Codec<SkinPreset> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Skin.CODEC.fieldOf("skin").forGetter(SkinPreset::getSkin),
                Codec.STRING.fieldOf("name").forGetter(SkinPreset::getName)
        ).apply(instance, SkinPreset::new));

    private String name;
    private Skin skin;

    public SkinPreset(Skin skin) {
        this(skin, "Unnamed Preset");
    }

    public SkinPreset(Skin skin, String name) {
        this.skin = skin;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public static SkinPreset generateDefaultPreset() {
        MinecraftClient client = MinecraftClient.getInstance();
        Session session = client.getSession();
        String name = session.getUsername();

        if(!AuthUtil.isLoggedIn()) {
            Identifier skinTexture = client.getSkinProvider().loadSkin(session.getProfile());
            Skin skin = new ResourceSkin(skinTexture, skinTexture.getPath().contains("/slim/") ? "slim" : "default");

            return new SkinPreset(skin, name);
        } else {
            var skinQueryResult = MojangSkinAPI.getPlayerSkinTexture(session.getUuid());

            if(skinQueryResult.usesDefaultSkin()) {
                Identifier skinTexture = client.getSkinProvider().loadSkin(session.getProfile());
                Skin skin = new ResourceSkin(skinTexture, skinTexture.getPath().contains("/slim/") ? "slim" : "default");

                return new SkinPreset(skin, name);
            }

            return new SkinPreset(new UrlSkin(skinQueryResult.skinURL(), skinQueryResult.skinURL()), name);
        }
    }

    public void copyFrom(SkinPreset other) {
        this.name = other.name;
        this.skin = other.skin;
    }

    public SkinPreset copy() {
        return new SkinPreset(this.skin, this.name);
    }
}
