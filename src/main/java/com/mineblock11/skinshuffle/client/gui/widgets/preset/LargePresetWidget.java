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

package com.mineblock11.skinshuffle.client.gui.widgets.preset;

import com.mineblock11.skinshuffle.client.gui.LargeCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;

public class LargePresetWidget extends PresetWidget<LargeCarouselScreen> {
    protected CarouselMoveButton moveLeftButton;
    protected CarouselMoveButton moveRightButton;

    public LargePresetWidget(LargeCarouselScreen parent, SkinPreset skinPreset) {
        super(parent, skinPreset);

        editButton.overridePosition((getWidth() / 8) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() - (this.getWidth() / 4) - 8, 20);

        copyButton.overridePosition(3, getHeight() - 24);
        copyButton.overrideDimensions(getWidth() / 2 - 5, 20);

        deleteButton.overridePosition(getWidth() / 2 + 2, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - 5, 20);

        this.moveLeftButton = new CarouselMoveButton(Position.of(2, getHeight() - 46), false);
        this.moveRightButton = new CarouselMoveButton(Position.of(getWidth() - 2, getHeight() - 46), true);

        this.moveLeftButton.setCallback(() -> {
            var i = parent.carouselWidgets.indexOf(this);
            parent.swapPresets(i, i - 1);
            parent.scrollCarousel(-1, false);
        });
        this.moveRightButton.setCallback(() -> {
            var i = parent.carouselWidgets.indexOf(this);
            parent.swapPresets(i, i + 1);
            parent.scrollCarousel(1, false);
        });

        addChild(moveLeftButton);
        addChild(moveRightButton);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        int i = this.parent.carouselWidgets.indexOf(this);
        moveLeftButton.setVisible(active && this.isMovable() && i > 0 && this.parent.carouselWidgets.get(i - 1).isMovable());
        moveRightButton.setVisible(active && this.isMovable() && i < this.parent.carouselWidgets.size() - 1 && this.parent.carouselWidgets.get(i + 1).isMovable());
    }
}
