package com.edgn.ui.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This class isn't aimed to be used by the user, it's just utilities for the template to send messages client side
 * @author EDGN
 */
@SuppressWarnings("unused")
public class MessageUtils {
    public static final String MOD_TAG = Formatting.GOLD + "ESL" + Formatting.YELLOW + " >> " + Formatting.RESET;
    public static final String ERROR_TAG = MOD_TAG + Formatting.RED + "ERROR" + Formatting.GOLD + " >> " + Formatting.RED;
    public static final String INFO_TAG = MOD_TAG + Formatting.GOLD + "INFO" + Formatting.YELLOW + " >> " + Formatting.GOLD;
    public static final String SUCCESS_TAG = MOD_TAG + Formatting.GREEN + "SUCCESS" + Formatting.DARK_GREEN + " >> " + Formatting.GREEN;

    public enum Level {
        SUCCESS,
        INFO,
        ERROR
    }

    public static void sendMessageToPlayer(String message, Level level) {
        if(MinecraftClient.getInstance().player == null) return;
        String tag = "";
        switch (level) {
            case SUCCESS -> tag = SUCCESS_TAG;
            case INFO -> tag = INFO_TAG;
            case ERROR -> tag = ERROR_TAG;
        }
        MinecraftClient.getInstance().player.sendMessage(Text.literal(tag + message), false);
    }
}
