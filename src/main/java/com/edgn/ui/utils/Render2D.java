package com.edgn.ui.utils;

import net.minecraft.client.gui.DrawContext;

public class Render2D {

    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        context.fill(x, y, x + radius, y + radius, color);
        context.fill(x + width - radius, y, x + width, y + radius, color);
        context.fill(x, y + height - radius, x + radius, y + height, color);
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color);
    }

    public static void drawRoundedRectBorder(DrawContext context, int x, int y, int width, int height, int radius, int color, int thickness) {
        for (int i = 0; i < thickness; i++) {

            context.fill(x + radius, y + i, x + width - radius, y + i + 1, color);
            context.fill(x + radius, y + height - i - 1, x + width - radius, y + height - i, color);
            context.fill(x + i, y + radius, x + i + 1, y + height - radius, color);
            context.fill(x + width - i - 1, y + radius, x + width - i, y + height - radius, color);
        }
    }

    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int offsetX, int offsetY, int shadowColor) {
        context.fill(x + offsetX, y + offsetY, x + width + offsetX, y + height + offsetY, shadowColor);
    }

    public static void drawPanel(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                 int backgroundColor, int borderColor, int borderThickness) {
        drawRoundedRect(context, x, y, width, height, cornerRadius, backgroundColor);
        drawRoundedRectBorder(context, x, y, width, height, cornerRadius, borderColor, borderThickness);
    }

    public static void drawPanelWithShadow(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                           int backgroundColor, int borderColor, int borderThickness, int shadowColor) {

        drawShadow(context, x, y, width, height, 2, 2, shadowColor);
        drawPanel(context, x, y, width, height, cornerRadius, backgroundColor, borderColor, borderThickness);
    }

    public static boolean isPointInRect(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static void drawGradient(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }

    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            context.fill(x + i, y + i, x + width - i, y + i + 1, color);
            context.fill(x + i, y + height - i - 1, x + width - i, y + height - i, color);
            context.fill(x + i, y + i, x + i + 1, y + height - i, color);
            context.fill(x + width - i - 1, y + i, x + width - i, y + height - i, color);
        }
    }

    public static void enableClipping(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    public static void disableClipping(DrawContext context) {
        context.disableScissor();
    }
}
