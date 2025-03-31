

package dev.imb11.skinshuffle.client.skin;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.api.MojangSkinAPI;
import dev.imb11.skinshuffle.api.data.SkinQueryResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UsernameSkin extends UUIDSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("username");

    public static final MapCodec<UsernameSkin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("username").forGetter(skin -> skin.username),
            Codec.STRING.optionalFieldOf("model").forGetter(skin -> Optional.ofNullable(skin.model))
    ).apply(instance, UsernameSkin::new));

    private final String username;

    public UsernameSkin(String username, @Nullable String model) {
        super(model);
        this.username = username;
    }

    private UsernameSkin(String username, Optional<String> model) {
        this(username, model.orElse(null));
    }

    @Override
    public ConfigSkin saveToConfig() {
        Optional<UUID> uuid = MojangSkinAPI.getUUIDFromUsername(this.username);
        if (uuid.isEmpty()) throw new RuntimeException("UUID is not a valid player UUID.");
        SkinQueryResult queryResult = MojangSkinAPI.getPlayerSkinTexture(uuid.get().toString());
        this.url = queryResult.skinURL();
        return super.saveToConfig();
    }

    @Override
    protected Object getTextureUniqueness() {
        return username;
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        var uuid = MojangSkinAPI.getUUIDFromUsername(username);

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
