package com.lifetools.mixins;

import com.lifetools.*;
import com.lifetools.commandsystem.LifeToolsCmd;
import com.lifetools.imgui.ImGuiImpl;
import com.lifetools.imgui.KeybindHandler;
import com.lifetools.util.Fullbright;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.*;
import imgui.type.ImInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    ImVec4 subTitleColor = new ImVec4(0.0f, 0.5f, 1.0f, 1.0f);
    @Unique
    private int[] speedValue = {1};

    @Unique
    private int[] boatFlySpeedValue = {1};
    @Unique
    private boolean isInWorld = false;

    @Unique
    private float[] _slipperinessValue = {BlockSlipperiness.slipperinessValue};

    @Unique
    imgui.type.ImInt inputTeleport = new imgui.type.ImInt(0);

    private Screen invisibleScreen = new InvisibleScreen();


    @Inject(method = "render", at = @At("RETURN"))
    private void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        KeybindHandler.checkKeybinds();

        if (LifeTools.menu_shown) {
            ImGuiImpl.draw(io -> RenderMenu());

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.currentScreen == null) {
                client.setScreen(invisibleScreen);
            }
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen == invisibleScreen) {
                client.setScreen(null);
            }
        }
    }

    @Unique
    private void RenderMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        isInWorld = client.player != null && client.world != null;

        ImGui.begin("LifeTools Menu");

        if (isInWorld) {
            // LEFT COLUMN
            ImGui.beginGroup();

            // VISUALS
            if (ImGui.beginChild("VISUAL", new ImVec2(160, 160), true)) {
                try {
                    ImGui.textColored(subTitleColor, "Visuals");
                    if (ImGui.checkbox("ESP", ESP.isEspEnabled)) ESP.isEspEnabled = !ESP.isEspEnabled;
                    if (ImGui.checkbox("Fullbright", Fullbright.isFullbright)) Fullbright.isFullbright = !Fullbright.isFullbright;
                    if (ImGui.checkbox("Xray", Xray.xrayEnabled)) Xray.xrayEnabled = !Xray.xrayEnabled;
                } finally {
                    ImGui.endChild();
                }
            }

            // MOVEMENT
            if (ImGui.beginChild("MOVEMENT", new ImVec2(160, 360), true)) {
                try {
                    ImGui.textColored(subTitleColor, "Movement");

                    if (ImGui.checkbox("Fly", Fly.isFlying)) {
                        Fly.isFlying = !Fly.isFlying;
                        if (client.player != null) Fly.setFlying(client.player, Fly.isFlying);
                    }
                    if (ImGui.checkbox("Boat Fly", BoatFly.boatFlyEnabled)) BoatFly.boatFlyEnabled = !BoatFly.boatFlyEnabled;
                    if (BoatFly.boatFlyEnabled) {
                        if (ImGui.sliderInt("Boat Fly Speed", boatFlySpeedValue, 1, 10)) {
                            BoatFly.boatFlySpeed = boatFlySpeedValue[0];
                        }
                    }
                    if (ImGui.checkbox("Jesus", Jesus.jesusModeEnabled)) Jesus.jesusModeEnabled = !Jesus.jesusModeEnabled;
                    if (ImGui.checkbox("Strafe", Strafe.strafeEnabled)) Strafe.strafeEnabled = !Strafe.strafeEnabled;
                    if (ImGui.checkbox("Jetpack", Jetpack.JetpackEnabled)) Jetpack.JetpackEnabled = !Jetpack.JetpackEnabled;
                    if (ImGui.checkbox("No Fall", NoFall.noFallEnabled)) NoFall.noFallEnabled = !NoFall.noFallEnabled;
                    if (ImGui.checkbox("Instant Climb", InstantClimb.instantClimbEnabled)) InstantClimb.instantClimbEnabled = !InstantClimb.instantClimbEnabled;
                    if (ImGui.checkbox("Airjump", AirJump.airJumpEnabled)) AirJump.airJumpEnabled = !AirJump.airJumpEnabled;
                    if (ImGui.checkbox("Block slipperiness", BlockSlipperiness.isSlipperinessEnabled)) BlockSlipperiness.isSlipperinessEnabled = !BlockSlipperiness.isSlipperinessEnabled;
                    if (BlockSlipperiness.isSlipperinessEnabled) {
                        if (ImGui.sliderFloat("Slipperiness", _slipperinessValue, 1, 10)) {
                            BlockSlipperiness.slipperinessValue = _slipperinessValue[0];
                        }
                    }

                    if (ImGui.sliderInt("Speed Value", speedValue, 1, 100)) sendSpeedCommand(speedValue[0]);
                    if (ImGui.button("Reset")) {
                        sendSpeedCommand("reset");
                        speedValue[0] = 1;
                    }
                    if (ImGui.button("Launch")) LifeToolsCmd.executeCommand("!util launch");
                    if (ImGui.isItemHovered()) ImGui.setTooltip("Launches you into the air");

                    ImGui.inputInt("Value", inputTeleport);
                    if (ImGui.button("Teleport")) Teleport.executeTeleportForward(inputTeleport.get());
                    if (ImGui.isItemHovered()) ImGui.setTooltip("Teleports you where you are looking.");

                } finally {
                    ImGui.endChild();
                }
            }

            ImGui.endGroup(); // end left column

            // RIGHT COLUMN
            ImGui.sameLine();
            ImGui.beginGroup();

            // COMBAT
            if (ImGui.beginChild("COMBAT", new ImVec2(160, 120), true)) {
                try {
                    ImGui.textColored(subTitleColor, "Combat");

                    if (ImGui.checkbox("Enable KillAura", KillAura.killauraEnabled)) KillAura.killauraEnabled = !KillAura.killauraEnabled;

                    String[] displayModes = {"1.9+ Combat", "Anti Kick", "Default", "TP Aura"};
                    String[] internalModes = {"newcombat", "avoid_too_much_packets", "default", "tpaura"};

                    ImInt selectedMode = new ImInt(getSelectedModeIndex(KillAura.mode));
                    if (ImGui.combo("KillAura Mode", selectedMode, displayModes)) {
                        KillAura.setModeFromExternal(internalModes[selectedMode.get()]);
                    }

                    if (ImGui.checkbox("Reach", Reach.reachToggled)) Reach.reachToggled = !Reach.reachToggled;
                    if (ImGui.checkbox("Criticals", Criticals.enabled)) Criticals.enabled = !Criticals.enabled;

                } finally {
                    ImGui.endChild();
                }
            }

            // MISC
            if (ImGui.beginChild("MISC", new ImVec2(160, 120), true)) {
                try {
                    ImGui.textColored(subTitleColor, "Misc");
                    if (ImGui.checkbox("Scaffold", Scaffold.scaffoldEnabled)) Scaffold.scaffoldEnabled = !Scaffold.scaffoldEnabled;
                } finally {
                    ImGui.endChild();
                }
            }

            ImGui.endGroup(); // end right column

        } else {
            ImGui.text("Player is not in a world, cannot show features.");
        }

        ImGui.end(); // end LifeTools Menu
    }



    @Unique
    private void sendSpeedCommand(Object speedValue) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Prepare the command string
            String command = "!speed " + speedValue.toString();

            // Use the executeCommand method from your LifeToolsCmd system to run the command
            LifeToolsCmd.executeCommand(command);
        }
    }

    @Unique
    private static int getSelectedModeIndex(String currentMode) {
        switch (currentMode) {
            case "newcombat": return 0;
            case "avoid_too_much_packets": return 1;
            case "default": return 2;
            case "tpaura": return 3;
            default: return 2; // Default mode index
        }
    }



    @Inject(at = @At("RETURN"), method = "render")
    private void afterRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (LifeTools.menu_shown) {
            // When menu is open, show the invisible screen
            if (client.currentScreen == null) {
                client.setScreen(invisibleScreen);
            }
        } else {
            // When menu is closed, remove the invisible screen
            if (client.currentScreen == invisibleScreen) {
                client.setScreen(null);
            }
        }
    }
}
