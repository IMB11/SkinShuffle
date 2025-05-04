package dev.imb11.skinshuffle.compat.api;

import dev.imb11.skinshuffle.compat.CapesCompat;
import dev.imb11.skinshuffle.compat.MinecraftCapesCompat;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;

public class CompatLoader {
    private static final ArrayList<CompatHandler> HELPERS = new ArrayList<>();

    static {
        HELPERS.add(new MinecraftCapesCompat());
        HELPERS.add(new CapesCompat());
    }

    public static void init() {
        for (CompatHandler helper : HELPERS) {
            if (FabricLoader.getInstance().isModLoaded(helper.getID()))
                helper.execute();
        }
    }
}
