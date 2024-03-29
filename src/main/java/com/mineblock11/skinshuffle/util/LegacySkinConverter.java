/*
 *
 *     Copyright (C) 2024 Calum (mineblock11), enjarai
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

package com.mineblock11.skinshuffle.util;

import net.minecraft.client.texture.NativeImage;

public class LegacySkinConverter {
    /**
     * Convert a legacy 64x32 skin to a 64x64 skin.
     * @param image The image to convert.
     * @return The converted image.
     */
    public static NativeImage processTexture(NativeImage image) {
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();

        if (imageWidth == 64 && (imageHeight == 32 || imageHeight == 64)) {
            boolean isImageHeight32 = imageHeight == 32;
            if (isImageHeight32) {
                NativeImage nativeImage = new NativeImage(64, 64, true);
                nativeImage.copyFrom(image);
                image.close();
                image = nativeImage;

                nativeImage.fillRect(0, 32, 64, 32, 0);
                nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
                nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
                nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
                nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
                nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
                nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
                nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
                nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
                nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
                nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
                nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
                nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);

            }

            for(int i2 = 0; i2 < 32; ++i2) {
                for(int j2 = 0; j2 < 16; ++j2) {
                    image.setColor(i2, j2, image.getColor(i2, j2) | -16777216);
                }
            }
            if (isImageHeight32) {
                boolean finished = false;
                int i = 0;
                int j = 0;
                for(i = 32; i < 64; ++i) {
                    for(j = 0; j < 32; ++j) {
                        int k = image.getColor(i, j);
                        if ((k >> 24 & 255) < 128) {
                            finished = true;
                            break;
                        }
                    }
                    if (finished) break;
                }
                if (!finished) {
                    for(i = 32; i < 64; ++i) {
                        for(j = 0; j < 32; ++j) {
                            image.setColor(i, j, image.getColor(i, j) & 16777215);
                        }
                    }
                }
            }

            for(int i1 = 0; i1 < 64; ++i1) {
                for(int j1 = 16; j1 < 32; ++j1) {
                    image.setColor(i1, j1, image.getColor(i1, j1) | -16777216);
                }
            }
            for(int i = 16; i < 48; ++i) {
                for(int j = 48; j < 64; ++j) {
                    image.setColor(i, j, image.getColor(i, j) | -16777216);
                }
            }
        }

        return image;
    }
}
