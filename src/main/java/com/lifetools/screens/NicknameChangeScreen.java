package com.lifetools.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.session.Session;
import net.minecraft.client.gui.DrawContext;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class NicknameChangeScreen extends Screen {

    private TextFieldWidget nicknameField;
    private final MinecraftClient client;
    private final Screen previousScreen;
    private ButtonWidget confirmButton;

    public NicknameChangeScreen(Screen previousScreen) {
        super(Text.literal("Change Nickname"));
        this.client = MinecraftClient.getInstance();
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();

        nicknameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Text.literal("Enter new nickname"));
        nicknameField.setMaxLength(16);
        this.addDrawableChild(nicknameField);

        confirmButton = new ButtonWidget.Builder(Text.literal("Confirm"), button -> {
            String newName = nicknameField.getText();
            if (newName.length() >= 3) {
                changeNickname(newName);
                this.client.setScreen(previousScreen);
            }
        })
                .dimensions(this.width / 2 - 50, this.height / 2 + 20, 100, 20)
                .build();
        confirmButton.active = false;
        this.addDrawableChild(confirmButton);

        ButtonWidget backButton = new ButtonWidget.Builder(Text.literal("Back"), button -> this.client.setScreen(previousScreen))
                .dimensions(this.width / 2 - 50, this.height / 2 + 50, 100, 20)
                .build();
        this.addDrawableChild(backButton);

        nicknameField.setChangedListener(text -> confirmButton.active = text.length() >= 3);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§5Change Nickname"), this.width / 2, this.height / 2 - 80, 0xFFFFFF);
    }



    private void changeNickname(String newName) {
        if (!isValidNickname(newName)) {
            return;
        }

        try {
            Field sessionField = getSessionField();
            if (sessionField == null) {
                return;
            }

            Session session = (Session) sessionField.get(this.client);
            Session newSession = new Session(
                    newName,
                    UUID.randomUUID(),
                    session.getAccessToken(),
                    Optional.of(session.getClientId().toString()),
                    Optional.of(session.getXuid().toString()),
                    session.getAccountType()
            );
            sessionField.set(this.client, newSession);

        } catch (IllegalAccessException e) {
            // Handle the exception appropriately if needed
        }
    }

    private boolean isValidNickname(String newName) {
        return newName.length() >= 3 && newName.length() <= 16;
    }

    private Field getSessionField() {
        for (Field field : MinecraftClient.class.getDeclaredFields()) {
            if (field.getType().equals(Session.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
