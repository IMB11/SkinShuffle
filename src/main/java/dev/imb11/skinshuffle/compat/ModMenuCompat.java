

package dev.imb11.skinshuffle.compat;

import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) GeneratedScreens::getCarouselScreen;
    }
}
