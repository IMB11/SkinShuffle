package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.SkinShuffleClient;
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
    private final String model;

    public FileSkin(Path file, String model) {
        this.file = file;
        this.model = model;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    protected Path getFile() {
        return file;
    }
}
