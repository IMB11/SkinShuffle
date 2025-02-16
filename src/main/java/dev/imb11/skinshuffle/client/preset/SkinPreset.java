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

package dev.imb11.skinshuffle.client.preset;

import dev.imb11.skinshuffle.api.SkinAPIs;
import dev.imb11.skinshuffle.client.skin.ResourceSkin;
import dev.imb11.skinshuffle.client.skin.Skin;
import dev.imb11.skinshuffle.client.skin.UrlSkin;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.session.Session;

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

    public static SkinPreset generateDefaultPreset() {
        MinecraftClient client = MinecraftClient.getInstance();
        Session session = client.getSession();
        String name = session.getUsername();

        if(!NetworkingUtil.isLoggedIn()) {
            Skin skin = new ResourceSkin(Identifier.of("minecraft:textures/entity/player/wide/steve.png"), "default");
            return new SkinPreset(skin, name);
        } else {
            var skinQueryResult = SkinAPIs.getPlayerSkinTexture(String.valueOf(client.getGameProfile().getId()));

            if(skinQueryResult.usesDefaultSkin()) {
                SkinTextures skinTexture = client.getSkinProvider().getSkinTextures(client.getGameProfile());
                Skin skin = new ResourceSkin(skinTexture.texture(), skinTexture.texture().getPath().contains("/slim/") ? "slim" : "default");

                return new SkinPreset(skin, name);
            }

            try (var urlSkin = new UrlSkin(skinQueryResult.skinURL(), skinQueryResult.modelType())) {
                return new SkinPreset(urlSkin.saveToConfig(), name);
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

    public void copyFrom(SkinPreset other) {
        this.name = other.name;
        this.skin = other.skin;
    }

    public SkinPreset copy() {
        return new SkinPreset(this.skin, this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkinPreset that = (SkinPreset) o;

        if (!name.equals(that.name)) return false;
        return skin.equals(that.skin);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + skin.hashCode();
        return result;
    }
}
