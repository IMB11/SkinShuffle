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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class FileSkin extends FileBackedSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("file");
    public static final Codec<FileSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(string -> {
                try {
                    return DataResult.success(Path.of(string));
                } catch (Exception e) {
                    return DataResult.error(() -> "Invalid path: " + string);
                }
            }, Path::toString).fieldOf("path").forGetter(skin -> skin.file),
            Codec.STRING.fieldOf("model").forGetter(FileSkin::getModel)
    ).apply(instance, FileSkin::new));

    private final Path file;
    private String model;

    public FileSkin(Path file, String model) {
        this.file = file;
        this.model = model;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public void setModel(String value) {
        this.model = value;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    public Path getFile() {
        return file;
    }
}
