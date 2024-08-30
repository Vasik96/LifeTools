package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static com.lifetools.LifeTools.ERROR_PREFIX;
import static com.lifetools.LifeTools.INFO_PREFIX;

public class NameChanger {

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 16;

    public void changeName(FabricClientCommandSource source, String newName) {
        if (newName.length() < MIN_NAME_LENGTH || newName.length() > MAX_NAME_LENGTH) {
            source.sendError(Text.literal(ERROR_PREFIX + "Nickname must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters"));
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null) {
            try {
                // Access the private session field and change the username
                Field sessionField = MinecraftClient.class.getDeclaredField("session");
                sessionField.setAccessible(true);

                Session session = (Session) sessionField.get(client);

                // Create a new session with the new username
                Session newSession = new Session(
                        newName,
                        UUID.randomUUID(), // Generate a new UUID for the new session
                        session.getAccessToken(),
                        Optional.of(String.valueOf(session.getClientId())),
                        Optional.of(String.valueOf(session.getXuid())),
                        session.getAccountType()
                );

                // Set the new session with the changed username
                sessionField.set(client, newSession);

                source.sendFeedback(Text.literal(INFO_PREFIX + "Nickname has been changed to: §r" + newName));

                // Disconnect after changing the name
                Disconnect.reason = "Your nickname has been changed successfully";
                new Disconnect().handleDisconnect(source);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                source.sendError(Text.literal(ERROR_PREFIX + "Failed to change nickname"));
            }
        }
    }
}
