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

package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Objects;

public class ConfigSkin extends FileBackedSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("config");
    /*? if >=1.20.5 {*/
    public static final MapCodec<ConfigSkin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    /*?} else {*/
    /*public static final Codec<ConfigSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            *//*?}*/
            Codec.STRING.fieldOf("skin_name").forGetter(skin -> skin.skinName),
            Codec.STRING.fieldOf("model").forGetter(ConfigSkin::getModel)
    ).apply(instance, ConfigSkin::new));

    private final String skinName;
    private String model;

    public ConfigSkin(String skinName, String model) {
        this.skinName = skinName;
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

    @Override
    public ConfigSkin saveToConfig() {
        return this;
    }

    public Path getFile() {
        return SkinPresetManager.PERSISTENT_SKINS_DIR.resolve(skinName + ".png");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigSkin that = (ConfigSkin) o;

        if (!Objects.equals(skinName, that.skinName)) return false;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        int result = skinName != null ? skinName.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }
}
