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
