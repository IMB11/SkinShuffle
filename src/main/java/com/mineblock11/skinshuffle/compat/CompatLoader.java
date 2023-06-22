package com.mineblock11.skinshuffle.compat;

import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;

public class CompatLoader {
    private static final ArrayList<CompatHandler> HELPERS = new ArrayList<>();

    static {
        HELPERS.add(new ETFCompatHandler());
    }

    public static void init() {
        for (CompatHandler helper : HELPERS) {
            if(FabricLoader.getInstance().isModLoaded(helper.getID()))
                helper.execute();
        }
    }
}
