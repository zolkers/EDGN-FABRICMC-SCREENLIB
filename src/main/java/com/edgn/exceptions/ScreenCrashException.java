package com.edgn.exceptions;

import java.io.Serial;

@SuppressWarnings("unused")
public final class ScreenCrashException extends RuntimeException {
    @Serial private static final long serialVersionUID = 1L;

    public enum Phase { INIT, RENDER, RESIZE, TICK, INPUT, CLOSE, OTHER }

    private final String screen;
    private final Phase phase;
    private final boolean fatal;

    public ScreenCrashException(String message) {
        this(message, null, Phase.OTHER, true, null);
    }

    public ScreenCrashException(String message, Throwable cause) {
        this(message, null, Phase.OTHER, true, cause);
    }

    public ScreenCrashException(Throwable cause) {
        this(cause == null ? null : cause.getMessage(), null, Phase.OTHER, true, cause);
    }

    public ScreenCrashException(String message, String screen, Phase phase, boolean fatal) {
        this(message, screen, phase, fatal, null);
    }

    public ScreenCrashException(String message, String screen, Phase phase, boolean fatal, Throwable cause) {
        super(message, cause);
        this.screen = screen;
        this.phase = phase == null ? Phase.OTHER : phase;
        this.fatal = fatal;
    }

    public String getScreen() {
        return screen;
    }

    public Phase getPhase() {
        return phase;
    }

    public boolean isFatal() {
        return fatal;
    }

    @Override
    public String toString() {
        String base = super.toString();
        String s = screen != null ? screen : "unknown";
        return base + " [screen=" + s + ", phase=" + phase + ", fatal=" + fatal + "]";
    }
}
