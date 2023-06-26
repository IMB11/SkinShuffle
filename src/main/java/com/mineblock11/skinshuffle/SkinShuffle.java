/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle;

import com.mineblock11.skinshuffle.compat.CompatLoader;
import com.mineblock11.skinshuffle.networking.ServerSkinHandling;
import com.mineblock11.skinshuffle.util.SkinCacheRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SkinShuffle implements ModInitializer {
    public static final String MOD_ID = "skinshuffle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path DATA_DIR = FabricLoader.getInstance().getConfigDir().resolve("skinshuffle");

    @Override
    public void onInitialize() {
        ensureDataDir();
        SkinCacheRegistry.initialize();
        ServerSkinHandling.init();
        CompatLoader.init();
    }

    private void ensureDataDir() {
        if(!DATA_DIR.toFile().exists()) {
            try {
                Files.createDirectories(DATA_DIR);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + DATA_DIR, e);
            }
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
