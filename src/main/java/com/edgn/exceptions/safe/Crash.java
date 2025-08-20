package com.edgn.exceptions.safe;

import com.edgn.EdgnScreenLib;
import com.edgn.exceptions.ScreenCrashException;
import com.edgn.ui.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;

public final class Crash {
    private Crash() {}

    public static void handle(Screen current, ScreenCrashException e, Runnable closeAction) {
        var log = EdgnScreenLib.LOGGER;
        if (log != null && log.isErrorEnabled()) {
            log.error("Screen crashed: {}", current != null ? current.getTitle().getString() : "<unknown>", e);
        }

        MessageUtils.sendMessageToPlayer(
                "An error occurred. Closing the screen: " +
                        Formatting.GOLD + (current != null ? current.getTitle().getString() : "<unknown>"),
                MessageUtils.Level.ERROR
        );

        if (e.isFatal() && closeAction != null) {
            try {
                closeAction.run();
            } catch (Exception ex) {
                try { MinecraftClient.getInstance().setScreen(null); } catch (Exception ignored) { /* handling has already been done to this entry point */}
            }
        }
    }
}
