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

import com.mineblock11.skinshuffle.SkinShuffle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.nio.file.Files;
import java.nio.file.Path;

public class LegacySkinConverter {
    public static void handleLegacyChecks(Path filePath) {
        return;
//        try {
//            var image = ImageIO.read(Files.newInputStream(filePath));
//            if (image.getWidth() == 64 && image.getHeight() == 32) {
//                var newSkinTexture = convert(image);
//                ImageIO.write(newSkinTexture, "png", filePath.toFile());
//            }
//        } catch (Exception e) {
//            SkinShuffle.LOGGER.error("Unable to read image from file to handle conversion if nessecary: " + filePath, e);
//        }
    }

    public static BufferedImage convert(BufferedImage oldImage) {
        var newSkinTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        var graphics = newSkinTexture.createGraphics();

        graphics.drawImage(oldImage, 0, 0, 64, 32, null);
        graphics.drawImage(oldImage, 16, 48, 16, 16, 0, 16, 16, 32, null);
        graphics.drawImage(oldImage, 32, 48, 16, 16, 40, 16, 56, 32, null);

        return newSkinTexture;
    }
}
