package com.lifetools;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClickableChatHelper {

    public static Text createClickableText(String displayText, String command) {
        Text commandText = Text.literal(displayText); // The command text without brackets


        Text icon = Text.literal(" [✎]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent.SuggestCommand(command))
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to type command")))
                        .withColor(Formatting.GRAY)
                );

        return Text.empty().append(commandText).append(icon);
    }
}
