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

package com.mineblock11.skinshuffle.mixin.screen;

import com.mineblock11.skinshuffle.MixinStatics;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselButton;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.ToastHelper;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Shadow
    @Final
    private boolean doBackgroundFade;
    @Unique
    private ArrayList<ClickableWidget> openCarouselWidgets;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void refreshConfig(CallbackInfo ci) {
        if (!MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION && this.doBackgroundFade) {
            MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION = true;
            SkinPresetManager.apply();

            if(!NetworkingUtil.isLoggedIn()) {
                ToastHelper.showOfflineModeToast();
            }
        }
    }

    @Override
    public void close() {
        this.openCarouselWidgets = null;
    }

    @Inject(method = "onDisplayed", at = @At("TAIL"), cancellable = false)
    public void updateVisibility(CallbackInfo ci) {
        if (!SkinShuffleConfig.get().displayInTitleScreen && this.openCarouselWidgets != null) {
            for (ClickableWidget openCarouselWidget : this.openCarouselWidgets) {
                this.remove(openCarouselWidget);
            }
            this.openCarouselWidgets = null;
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addButton(CallbackInfo ci) {
        /*
            TODO: Maybe different types of buttons?
             - Small icon button
             - Bedrock-style skin preview
         */

        this.openCarouselWidgets = GeneratedScreens.createCarouselWidgets(this);

        for (ClickableWidget carouselWidget : this.openCarouselWidgets) {
            this.addDrawableChild(carouselWidget);
        }
    }
}
