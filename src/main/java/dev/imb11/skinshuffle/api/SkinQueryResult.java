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

package dev.imb11.skinshuffle.api;

import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.Nullable;

public record SkinQueryResult(boolean usesDefaultSkin, @Nullable String skinURL, @Nullable String modelType, @Nullable String textureSignature, @Nullable String textureValue) {
    public static final SkinQueryResult EMPTY_RESULT = new SkinQueryResult(true, null, null, null, null);

    public Property toProperty() {
        return new Property("textures", textureValue, textureSignature);
    }
}
