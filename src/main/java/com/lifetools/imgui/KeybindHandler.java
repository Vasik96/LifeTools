package com.lifetools.imgui;

import com.lifetools.LifeTools;
import com.lifetools.util.Launch;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    private static boolean insertPressedLastFrame = false; // Prevents spam toggling

    public static void checkKeybinds() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();

        // Check if the Insert key is currently pressed
        boolean insertPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_INSERT) == GLFW.GLFW_PRESS;

        // Toggle only if it was NOT pressed last frame
        if (insertPressed && !insertPressedLastFrame) {
            LifeTools.menu_shown = !LifeTools.menu_shown;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            LifeTools.menu_shown = false;
        }


        // Update last frame state
        insertPressedLastFrame = insertPressed;
    }
}
