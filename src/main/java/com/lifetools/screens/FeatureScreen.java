package com.lifetools.screens;

import com.lifetools.FeatureRegistry;
import com.lifetools.annotations.Feature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FeatureScreen extends Screen {

    private final List<ButtonWidget> modeButtons = new ArrayList<>();
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_MARGIN_TOP = 30; // Margin above buttons
    private static final int TITLE_MARGIN_TOP = 40; // Margin for the title from the top

    public FeatureScreen() {
        super(Text.literal("Feature Screen"));
    }

    private void initButtons() {
        // Clear previous buttons to avoid duplicates
        modeButtons.clear();

        for (FeatureRegistry.FeatureDetails feature : FeatureRegistry.getFeatures()) {
            createButton(feature.featureName, feature.method);
        }
    }

    private void createButton(String featureName, Method method) {
        try {
            Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
            Feature annotation = method.getAnnotation(Feature.class);

            boolean isEnabled = method.getDeclaringClass()
                    .getDeclaredField(annotation.booleanField())
                    .getBoolean(instance);

            String initialState = isEnabled ? "§aON" : "§cOFF";
            ButtonWidget button = new ButtonWidget.Builder(
                    Text.literal(featureName + " §7[" + initialState + "§7]"),
                    buttonWidget -> {
                        try {
                            method.setAccessible(true);
                            // Toggle the state by invoking the method
                            method.invoke(instance);

                            // Check the new state after invoking the method
                            boolean newState = method.getDeclaringClass()
                                    .getDeclaredField(annotation.booleanField())
                                    .getBoolean(instance);

                            // Update button text based on the new state
                            String updatedState = newState ? "§aON" : "§cOFF";
                            buttonWidget.setMessage(Text.literal(featureName + " §7[" + updatedState + "§7]"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            ).dimensions(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT).build();

            // Add the button to the list
            modeButtons.add(button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStates() {
        for (ButtonWidget button : modeButtons) {
            try {
                Method method = FeatureRegistry.getFeatures()
                        .stream()
                        .filter(f -> button.getMessage().getString().startsWith(f.featureName))
                        .findFirst().orElseThrow()
                        .method;

                method.setAccessible(true);

                // Create a new instance of the class that declares the method
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();

                // Retrieve the boolean field to track the current state without invoking the method
                Feature annotation = method.getAnnotation(Feature.class);
                boolean isEnabled = method.getDeclaringClass()
                        .getDeclaredField(annotation.booleanField())
                        .getBoolean(instance);

                String featureName = annotation.featureName();
                String newState = isEnabled ? "§aON" : "§cOFF";
                button.setMessage(Text.literal(featureName + " §7[" + newState + "§7]"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        initButtons(); // Initialize buttons when the screen opens
        updateButtonStates(); // Update buttons based on their initial state

        // Position the buttons in two columns, centered on the screen
        int columns = 2; // Number of columns
        int spacingX = 10; // Horizontal spacing between buttons
        int spacingY = 5;  // Vertical spacing between buttons

        // Calculate total width and height of button area
        int totalButtonWidth = BUTTON_WIDTH * columns + spacingX * (columns - 1);
        int totalButtonHeight = BUTTON_HEIGHT * ((modeButtons.size() + columns - 1) / columns) + spacingY * ((modeButtons.size() + columns - 1) / columns - 1);

        // Calculate starting positions to center buttons with a top margin
        int startX = (this.width - totalButtonWidth) / 2; // Centered start X
        int startY = (this.height - totalButtonHeight) / 2 - BUTTON_MARGIN_TOP; // Centered start Y with top margin

        // Position and add buttons to the screen
        for (int i = 0; i < modeButtons.size(); i++) {
            ButtonWidget button = modeButtons.get(i);
            int columnIndex = i % columns; // Calculate the column index (0 or 1)
            int rowIndex = i / columns; // Calculate the row index

            int xPos = startX + (BUTTON_WIDTH + spacingX) * columnIndex; // X position for button
            int yPos = startY + (BUTTON_HEIGHT + spacingY) * rowIndex; // Y position for button

            button.setX(xPos);
            button.setY(yPos);
            this.addDrawableChild(button); // Add button to the screen
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000); // Dimmed background
        super.render(context, mouseX, mouseY, delta);

        // Draw title text at the top with a slight margin down
        String titleText = "§9LifeTools Features"; // Title text
        int titleY = TITLE_MARGIN_TOP; // Set Y position for title with margin from top
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(titleText), this.width / 2, titleY, 0xFFFFFF);
    }
}
