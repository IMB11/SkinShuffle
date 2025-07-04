package dev.imb11.skinshuffle.client.skin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.imb11.skinshuffle.SkinShuffle;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Objects;

public class FileSkin extends FileBackedSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("file");

    public static final MapCodec<FileSkin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileSkin fileSkin = (FileSkin) o;

        if (!Objects.equals(file, fileSkin.file)) return false;
        return Objects.equals(model, fileSkin.model);
    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }
}
