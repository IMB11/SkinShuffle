package dev.imb11.skinshuffle.client.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.PngMetadata;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Utility class for validation methods used in form inputs.
 */
public class ValidationUtils {
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});

    /**
     * Validates if a path is a valid PNG file with Minecraft skin dimensions.
     */
    public static boolean isValidPngFilePath(String pathStr) {
        // Trim leading and trailing spaces
        pathStr = pathStr.trim();

        // Remove surrounding quotation marks if present
        if (pathStr.startsWith("\"") && pathStr.endsWith("\"")) {
            pathStr = pathStr.substring(1, pathStr.length() - 1);
        }

        // Allow ~ as a shortcut for the user's home directory
        if (pathStr.startsWith("~")) {
            String home = System.getProperty("user.home");
            pathStr = home + pathStr.substring(1);
        }

        // Resolve the path
        Path path = Paths.get(pathStr);

        // Check if the file exists, follows symlinks, and is a regular file
        if (Files.exists(path) && Files.isRegularFile(path)) {
            // Check if the file has a .png extension (case-insensitive)
            String fileName = path.getFileName().toString().toLowerCase();

            if (!fileName.endsWith(".png")) {
                return false;
            }

            // Validate the png file's metadata.
            try {
                PngMetadata metadata = PngMetadata.fromStream(Files.newInputStream(path));

                // Width must be 64x64 or 64x32, and must be a png file.
                int width = metadata.width(), height = metadata.height();
                return (width == 64 && (height == 64 || height == 32));
            } catch (Exception ignored) {
                return false;
            }
        }

        return false;
    }

    /**
     * Validates if a string is a valid UUID.
     */
    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Validates if a string is a valid Minecraft username.
     */
    public static boolean isValidUsername(String username) {
        return username.matches("([a-zA-Z0-9]|_)*") && username.length() >= 3 && username.length() <= 16;
    }
    
    /**
     * Validates if a string is a valid URL.
     */
    public static boolean isValidUrl(String url) {
        return URL_VALIDATOR.isValid(url);
    }
    
    /**
     * Validates if a string is a valid resource identifier and exists in the resource manager.
     */
    public static boolean isValidResourceLocation(String location, net.minecraft.client.MinecraftClient client) {
        if (Identifier.validate(location).isSuccess()) {
            return client.getResourceManager().getResource(Identifier.tryParse(location)).isPresent();
        }
        return false;
    }
    
    /**
     * Normalizes a file path, expanding ~ to user home and removing quotes.
     */
    public static String normalizeFilePath(String path) {
        if (path == null) return null;
        
        // Trim leading and trailing spaces
        path = path.trim();
        
        // Remove surrounding quotes
        if (path.startsWith("\"") && path.endsWith("\"")) {
            path = path.substring(1, path.length() - 1);
        }
        
        // Expand ~ to user home
        if (path.startsWith("~")) {
            String home = System.getProperty("user.home");
            path = home + path.substring(1);
        }
        
        return path;
    }
}