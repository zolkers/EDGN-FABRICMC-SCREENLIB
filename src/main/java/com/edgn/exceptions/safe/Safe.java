package com.edgn.exceptions.safe;

import com.edgn.exceptions.ScreenCrashException;

import java.util.function.Supplier;

public final class Safe {
    private Safe() {}

    public static void run(String screen, ScreenCrashException.Phase phase, Runnable r) {
        try {
            r.run();
        } catch (ScreenCrashException e) {
            throw e;
        } catch (Exception e) {
            throw new ScreenCrashException("Failure", screen, phase, true, e);
        }
    }

    public static <T> T call(String screen, ScreenCrashException.Phase phase, Supplier<T> s) {
        try {
            return s.get();
        } catch (ScreenCrashException e) {
            throw e;
        } catch (Exception e) {
            throw new ScreenCrashException("Failure", screen, phase, true, e);
        }
    }
}
