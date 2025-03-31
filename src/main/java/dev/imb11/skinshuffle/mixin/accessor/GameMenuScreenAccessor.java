

package dev.imb11.skinshuffle.mixin.accessor;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameMenuScreen.class)
public interface GameMenuScreenAccessor {
    @Accessor()
    ButtonWidget getExitButton();
}
