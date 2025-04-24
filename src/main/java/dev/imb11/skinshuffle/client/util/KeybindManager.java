package dev.imb11.skinshuffle.client.util;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

/**
 * Manages keybinds for quickly switching between skin presets.
 */
public class KeybindManager {
    
    private static final int MAX_KEYBIND_COUNT = 9;
    private static final String TRANSLATION_KEY_PREFIX = "key.skinshuffle.preset_";
    private static final String KEYBIND_CATEGORY = "category.skinshuffle.presets";
    
    private static KeyBinding[] presetKeybindings;
    
    /**
     * Initialize all keybindings for skin presets.
     * This should be called during mod initialization.
     */
    public static void init() {
        presetKeybindings = new KeyBinding[MAX_KEYBIND_COUNT];
        
        // Register keybindings for each preset slot (1-9)
        for (int i = 0; i < MAX_KEYBIND_COUNT; i++) {
            final int presetId = i + 1;
            
            // Create unbound keybinds for each preset slot
            presetKeybindings[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    TRANSLATION_KEY_PREFIX + presetId,
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN, // Initially unbound
                    KEYBIND_CATEGORY
            ));
        }
        
        // Register the tick event for checking keybinds
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                checkKeybindings(client);
            }
        });
    }
    
    /**
     * Check if any keybinds are pressed and handle them.
     * 
     * @param client The Minecraft client instance
     */
    private static void checkKeybindings(MinecraftClient client) {
        for (int i = 0; i < presetKeybindings.length; i++) {
            if (presetKeybindings[i].wasPressed()) {
                int presetId = i + 1;
                applyPreset(presetId, client);
            }
        }
    }
    
    /**
     * Apply a preset by its ID and handle reconnect behavior if necessary.
     *
     * @param presetId The preset ID to apply (1-9)
     * @param client The Minecraft client instance
     */
    private static void applyPreset(int presetId, MinecraftClient client) {
        if (SkinPresetManager.getChosenPreset().getKeybindId() == presetId) return;
        boolean success = SkinPresetManager.applyPresetByKeybindId(presetId);
        
        if (success) {
            SkinShuffle.LOGGER.info("Applied skin preset with keybind ID: " + presetId);
            
            // If the mod is not installed on server, prompt for reconnect
            if (client.world != null && !ClientSkinHandling.isInstalledOnServer()) {
                client.setScreen(GeneratedScreens.getReconnectScreen(client.currentScreen));
            } else {
                if (SkinShuffleConfig.get().playKeybindSoundEffect) {
                    if (client.player != null)
                        client.player.playSoundToPlayer(SoundEvents.UI_TOAST_IN, SoundCategory.MASTER, 0.46f, 2f);
                }
            }
        }
    }
}
