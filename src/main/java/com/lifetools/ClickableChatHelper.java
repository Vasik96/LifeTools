package com.lifetools;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClickableChatHelper {

    public static Text createClickableText(String displayText, String command) {
        Text commandText = Text.literal(displayText); // The command text without brackets

        Text icon = Text.literal(" [✎]") // Left-pointing pencil icon with brackets
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                        .withHoverEvent(new net.minecraft.text.HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal("Click to type command")))
                        .withColor(Formatting.GRAY));

        return Text.empty().append(commandText).append(icon);
    }
}
