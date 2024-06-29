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

package com.mineblock11.skinshuffle.client.config;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.CarouselScreen;
import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.LargeCarouselScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public enum CarouselView {
    LARGE(LargeCarouselScreen::new, SkinShuffle.id("textures/gui/large-view-button.png"),
            Text.translatable("skinshuffle.carousel.view_type_button.large.tooltip")),
    COMPACT(CompactCarouselScreen::new, SkinShuffle.id("textures/gui/compact-view-button.png"),
            Text.translatable("skinshuffle.carousel.view_type_button.compact.tooltip"));

    public final Function<Screen, ? extends CarouselScreen> factory;
    public final Identifier iconTexture;
    public final Text tooltip;

    CarouselView(Function<Screen, ? extends CarouselScreen> factory, Identifier iconTexture, Text tooltip) {
        this.factory = factory;
        this.iconTexture = iconTexture;
        this.tooltip = tooltip;
    }
}
