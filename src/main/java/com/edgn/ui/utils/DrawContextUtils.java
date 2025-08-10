package com.edgn.ui.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

import java.awt.*;

/**
 * Utility class providing helper methods for drawing
 * @author EDGN
 */
public class DrawContextUtils {

    /**
     * Draws a filled rounded rectangle.
     *
     * @param context the {@link DrawContext} to draw on
     * @param x       the X coordinate of the rectangle
     * @param y       the Y coordinate of the rectangle
     * @param width   the width of the rectangle
     * @param height  the height of the rectangle
     * @param radius  the corner radius in pixels
     * @param color   the fill color (ARGB)
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        context.fill(x, y, x + radius, y + radius, color);
        context.fill(x + width - radius, y, x + width, y + radius, color);
        context.fill(x, y + height - radius, x + radius, y + height, color);
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color);
    }

    /**
     * Draws a rounded rectangle border.
     *
     * @param context   the {@link DrawContext} to draw on
     * @param x         the X coordinate of the rectangle
     * @param y         the Y coordinate of the rectangle
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param radius    the corner radius in pixels
     * @param color     the border color (ARGB)
     * @param thickness the thickness of the border in pixels
     */
    public static void drawRoundedRectBorder(DrawContext context, int x, int y, int width, int height, int radius, int color, int thickness) {
        for (int i = 0; i < thickness; i++) {

            context.fill(x + radius, y + i, x + width - radius, y + i + 1, color);
            context.fill(x + radius, y + height - i - 1, x + width - radius, y + height - i, color);
            context.fill(x + i, y + radius, x + i + 1, y + height - radius, color);
            context.fill(x + width - i - 1, y + radius, x + width - i, y + height - radius, color);
        }
    }

    /**
     * Draws a rectangular shadow behind an element.
     *
     * @param context     the {@link DrawContext} to draw on
     * @param x           the X coordinate of the rectangle
     * @param y           the Y coordinate of the rectangle
     * @param width       the width of the shadow
     * @param height      the height of the shadow
     * @param offsetX     the horizontal shadow offset
     * @param offsetY     the vertical shadow offset
     * @param shadowColor the shadow color (ARGB)
     */
    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int offsetX, int offsetY, int shadowColor) {
        context.fill(x + offsetX, y + offsetY, x + width + offsetX, y + height + offsetY, shadowColor);
    }

    /**
     * Draws a rounded panel with a background and border.
     *
     * @param context        the {@link DrawContext} to draw on
     * @param x              the X coordinate
     * @param y              the Y coordinate
     * @param width          the width of the panel
     * @param height         the height of the panel
     * @param cornerRadius   the corner radius in pixels
     * @param backgroundColor the background fill color (ARGB)
     * @param borderColor    the border color (ARGB)
     * @param borderThickness the border thickness in pixels
     */
    public static void drawPanel(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                 int backgroundColor, int borderColor, int borderThickness) {
        drawRoundedRect(context, x, y, width, height, cornerRadius, backgroundColor);
        drawRoundedRectBorder(context, x, y, width, height, cornerRadius, borderColor, borderThickness);
    }

    /**
     * Draws a rounded panel with a drop shadow.
     *
     * @param context        the {@link DrawContext} to draw on
     * @param x              the X coordinate
     * @param y              the Y coordinate
     * @param width          the width of the panel
     * @param height         the height of the panel
     * @param cornerRadius   the corner radius in pixels
     * @param backgroundColor the background fill color (ARGB)
     * @param borderColor    the border color (ARGB)
     * @param borderThickness the border thickness in pixels
     * @param shadowColor    the shadow color (ARGB)
     */
    public static void drawPanelWithShadow(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                           int backgroundColor, int borderColor, int borderThickness, int shadowColor) {

        drawShadow(context, x, y, width, height, 2, 2, shadowColor);
        drawPanel(context, x, y, width, height, cornerRadius, backgroundColor, borderColor, borderThickness);
    }

    /**
     * Checks if a point is inside a rectangle.
     *
     * @param mouseX the X coordinate of the point
     * @param mouseY the Y coordinate of the point
     * @param x      the rectangle X coordinate
     * @param y      the rectangle Y coordinate
     * @param width  the rectangle width
     * @param height the rectangle height
     * @return {@code true} if the point is inside, {@code false} otherwise
     */
    public static boolean isPointInRect(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    /**
     * Draws a vertical gradient fill.
     *
     * @param context    the {@link DrawContext} to draw on
     * @param x          the X coordinate
     * @param y          the Y coordinate
     * @param width      the width of the gradient
     * @param height     the height of the gradient
     * @param startColor the start color (ARGB)
     * @param endColor   the end color (ARGB)
     */
    public static void drawGradient(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }

    /**
     * Draws a rectangular border.
     *
     * @param context   the {@link DrawContext} to draw on
     * @param x         the X coordinate
     * @param y         the Y coordinate
     * @param width     the width of the border
     * @param height    the height of the border
     * @param color     the border color (ARGB)
     * @param thickness the border thickness in pixels
     */
    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            context.fill(x + i, y + i, x + width - i, y + i + 1, color);
            context.fill(x + i, y + height - i - 1, x + width - i, y + height - i, color);
            context.fill(x + i, y + i, x + i + 1, y + height - i, color);
            context.fill(x + width - i - 1, y + i, x + width - i, y + height - i, color);
        }
    }

    /**
     * Enables clipping (scissor test) for a region.
     *
     * @param context the {@link DrawContext} to draw on
     * @param x       the X coordinate of the clip area
     * @param y       the Y coordinate of the clip area
     * @param width   the width of the clip area
     * @param height  the height of the clip area
     */
    public static void enableClipping(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    /**
     * Disables clipping (scissor test).
     *
     * @param context the {@link DrawContext} to draw on
     */
    public static void disableClipping(DrawContext context) {
        context.disableScissor();
    }

    /**
     * Draws an image (texture) with optional rotation and mirroring.
     *
     * @param id       the {@link Identifier} of the texture
     * @param x1       the left coordinate
     * @param y1       the top coordinate
     * @param x2       the right coordinate
     * @param y2       the bottom coordinate
     * @param rotation rotation in 90° steps (0-3)
     * @param parity   {@code true} to flip horizontally
     * @param color    the tint color (ARGB)
     */
    public static void drawImage(Identifier id, int x1, int y1, int x2, int y2, int rotation, boolean parity, Color color) {
        int[][] texCoords = {
                {0, 1},
                {1, 1},
                {1, 0},
                {0, 0}
        };

        for (int i = 0; i < rotation % 4; i++) {
            int temp1 = texCoords[3][0], temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0];
            texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0];
            texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
        }
        if (parity) {
            int temp1 = texCoords[1][0];
            texCoords[1][0] = texCoords[0][0];
            texCoords[0][0] = temp1;
            temp1 = texCoords[3][0];
            texCoords[3][0] = texCoords[2][0];
            texCoords[2][0] = temp1;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, id);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        RenderSystem.enableBlend();
        bufferbuilder.vertex(x1, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[0][0], texCoords[0][1]);
        bufferbuilder.vertex(x2, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[1][0], texCoords[1][1]);
        bufferbuilder.vertex(x2, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[2][0], texCoords[2][1]);
        bufferbuilder.vertex(x1, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[3][0], texCoords[3][1]);
        BuiltBuffer builtBuffer = bufferbuilder.endNullable();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }        RenderSystem.disableBlend();
    }
}
