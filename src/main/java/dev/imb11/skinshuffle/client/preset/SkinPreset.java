package dev.imb11.skinshuffle.client.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.imb11.skinshuffle.api.MojangSkinAPI;
import dev.imb11.skinshuffle.client.skin.ResourceSkin;
import dev.imb11.skinshuffle.client.skin.Skin;
import dev.imb11.skinshuffle.client.skin.UrlSkin;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

public class SkinPreset {
    public static final Codec<SkinPreset> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Skin.CODEC.fieldOf("skin").forGetter(SkinPreset::getSkin),
                    Codec.STRING.fieldOf("name").forGetter(SkinPreset::getName),
                    Codec.INT.optionalFieldOf("keybindId", -1).forGetter(SkinPreset::getKeybindId)
            ).apply(instance, SkinPreset::new));

    private String name;
    private Skin skin;
    private int keybindId;

    public SkinPreset(Skin skin) {
        this(skin, "Unnamed Preset", -1);
    }

    public SkinPreset(Skin skin, String name) {
        this(skin, name, -1);
    }

    public SkinPreset(Skin skin, String name, int keybindId) {
        this.skin = skin;
        this.name = name;
        this.keybindId = keybindId;
    }

    public static SkinPreset generateDefaultPreset() {
        MinecraftClient client = MinecraftClient.getInstance();
        Session session = client.getSession();
        String name = session.getUsername();

        if (!NetworkingUtil.isLoggedIn()) {
            Skin skin = new ResourceSkin(Identifier.of("minecraft:textures/entity/player/wide/steve.png"), "default");
            return new SkinPreset(skin, name, -1);
        } else {
            var skinQueryResult = MojangSkinAPI.getPlayerSkinTexture(String.valueOf(client.getGameProfile().getId()));

            if (skinQueryResult.usesDefaultSkin()) {
                SkinTextures skinTexture = client.getSkinProvider().getSkinTextures(client.getGameProfile());
                Skin skin = new ResourceSkin(skinTexture.texture(), skinTexture.texture().getPath().contains("/slim/") ? "slim" : "default");

                return new SkinPreset(skin, name, -1);
            }

            try (var urlSkin = new UrlSkin(skinQueryResult.skinURL(), skinQueryResult.modelType())) {
                return new SkinPreset(urlSkin.saveToConfig(), name, -1);
            }
        }
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeybindId() {
        return keybindId;
    }

    public void setKeybindId(int keybindId) {
        this.keybindId = keybindId;
    }

    public void copyFrom(SkinPreset other) {
        this.name = other.name;
        this.skin = other.skin;
        this.keybindId = other.keybindId;
    }

    public SkinPreset copy() {
        return new SkinPreset(this.skin, this.name, this.keybindId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkinPreset that = (SkinPreset) o;

        if (keybindId != that.keybindId) return false;
        if (!name.equals(that.name)) return false;
        return skin.equals(that.skin);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + skin.hashCode();
        result = 31 * result + keybindId;
        return result;
    }
}
