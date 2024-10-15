package com.lifetools.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class QuickConnectScreen extends Screen {

    private final Screen parent;
    private final MinecraftClient client;

    // Map to store server name and address (with port if necessary)
    private final Map<String, String> servers = new HashMap<>();

    public QuickConnectScreen(Screen parent) {
        super(Text.literal("Quick Connect"));
        this.parent = parent;
        this.client = MinecraftClient.getInstance(); // Initialize the client instance

        // Add servers to the map (format: "address:port") or just address if the port is default (25565)
        servers.put("Hypixel", "mc.hypixel.net");
        servers.put("CubeCraft", "play.cubecraft.net");
        servers.put("SG", "play.survival-games.cz");
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 5;
        int yOffset = this.height / 4;
        int buttonMargin = 50;

        int index = 0; // For positioning the buttons vertically

        for (Map.Entry<String, String> entry : servers.entrySet()) {
            String serverName = entry.getKey();
            String serverAddressString = entry.getValue();
            ServerAddress serverAddress = ServerAddress.parse(serverAddressString);
            ServerInfo serverInfo = new ServerInfo(serverName, serverAddress.getAddress(), ServerInfo.ServerType.OTHER);

            this.addDrawableChild(new ButtonWidget.Builder(
                    Text.literal(serverName),
                    button -> connectToServer(serverAddress, serverInfo)
            ).position(this.width / 2 - buttonWidth / 2, yOffset + (buttonHeight + spacing) * index)
                    .size(buttonWidth, buttonHeight)
                    .build());

            index++;
        }

        // Add Back button with a margin
        this.addDrawableChild(new ButtonWidget.Builder(
                Text.literal("§cBack"),
                button -> this.client.setScreen(parent))
                .position(this.width / 2 - buttonWidth / 2, yOffset + (buttonHeight + spacing) * index + buttonMargin)
                .size(buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background and other elements
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Draw the main title text "Quick Join"
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("§5Quick Join"),
                this.width / 2,
                this.height / 30,
                0xFFFFFF
        );
    }


    private void connectToServer(ServerAddress serverAddress, ServerInfo serverInfo) {
        CookieStorage cookieStorage = new CookieStorage(new HashMap<>()); // Create an empty cookie storage
        ConnectScreen.connect(this, this.client, serverAddress, serverInfo, false, cookieStorage);
    }
}
