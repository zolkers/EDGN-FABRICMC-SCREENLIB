package com.edgn.ui.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@SuppressWarnings("unused")
public class MessageUtils {
    public static final String MOD_TAG = Formatting.GOLD + "ESL" + Formatting.YELLOW + " >> " + Formatting.RESET;
    public static final String ERROR_TAG = MOD_TAG + Formatting.RED + "ERROR" + Formatting.GOLD + " >> " + Formatting.RED;
    public static final String INFO_TAG = MOD_TAG + Formatting.GOLD + "INFO" + Formatting.YELLOW + " >> " + Formatting.GOLD;
    public static final String SUCCESS_TAG = MOD_TAG + Formatting.GREEN + "SUCCESS" + Formatting.DARK_GREEN + " >> " + Formatting.GREEN;

    public static void sendErrorToPlayer(String error) {
        if(MinecraftClient.getInstance().player == null) return;
        MinecraftClient.getInstance().player.sendMessage(Text.literal(ERROR_TAG + error), false);
    }
}
