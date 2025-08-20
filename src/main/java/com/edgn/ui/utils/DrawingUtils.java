package com.edgn.ui.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Utility class providing convenience drawing helpers.
 * <p><b>Notes</b></p>
 * <ul>
 *   <li>Rounded corners are approximated with fills for performance. (might change later) </li>
 *   <li>Algorithms for lines/circles use integer math (Bresenham) to avoid allocations.</li>
 * </ul>
 *
 * @author EDGN
 */
@SuppressWarnings("unused")
public class DrawingUtils {


    /** Thread-local stack of active clipping rectangles for push/pop semantics. */
    private static final ThreadLocal<Deque<Rectangle>> CLIP_STACK =
            ThreadLocal.withInitial(ArrayDeque::new);


    private DrawingUtils() {/* utility class */}

    public static void clearClipStack() {
        CLIP_STACK.remove();
    }

    /**
     * Fills a rectangle (thin wrapper around {@link DrawContext#fill}).
     *
     * @param context the {@link DrawContext} to draw on
     * @param x       left coordinate
     * @param y       top coordinate
     * @param width   width in pixels
     * @param height  height in pixels
     * @param color   fill color (ARGB)
     */
    public static void fillRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    /**
     * Alias for {@link #fillRect(DrawContext, int, int, int, int, int)} (clearer name in some contexts).
     */
    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        fillRect(context, x, y, width, height, color);
    }

    /**
     * Draws a horizontal 1px-thick line between (x1, y) and (x2, y).
     *
     * @param color ARGB color
     */
    public static void drawHLine(DrawContext context, int x1, int x2, int y, int color) {
        if (x2 < x1) { int t = x1; x1 = x2; x2 = t; }
        context.fill(x1, y, x2 + 1, y + 1, color);
    }

    /**
     * Draws a vertical 1px-thick line between (x, y1) and (x, y2).
     *
     * @param color ARGB color
     */
    public static void drawVLine(DrawContext context, int x, int y1, int y2, int color) {
        if (y2 < y1) { int t = y1; y1 = y2; y2 = t; }
        context.fill(x, y1, x + 1, y2 + 1, color);
    }

    /**
     * Draws a general 1px-thick line using Bresenham's algorithm.
     *
     * @param color ARGB color
     */
    public static void drawLine(DrawContext context, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            context.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx)  { err += dx; y0 += sy; }
        }
    }

    /**
     * Draws a filled rounded rectangle (fast, approximate corners).
     *
     * @param radius corner radius in pixels (clamped to half of min(width, height))
     * @param color  fill color (ARGB)
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        radius = Math.clamp(radius, 0, Math.min(width, height) / 2);
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        context.fill(x, y, x + radius, y + radius, color);
        context.fill(x + width - radius, y, x + width, y + radius, color);
        context.fill(x, y + height - radius, x + radius, y + height, color);
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color);
    }

    /**
     * Draws a rounded rectangle border (fast, approximate corners).
     *
     * @param thickness border thickness in pixels (>=1)
     */
    public static void drawRoundedRectBorder(DrawContext context, int x, int y, int width, int height, int radius, int color, int thickness) {
        radius = Math.clamp(radius, 0, Math.min(width, height) / 2);
        thickness = Math.max(1, thickness);
        for (int i = 0; i < thickness; i++) {
            context.fill(x + radius, y + i, x + width - radius, y + i + 1, color);
            context.fill(x + radius, y + height - i - 1, x + width - radius, y + height - i, color);
            context.fill(x + i, y + radius, x + i + 1, y + height - radius, color);
            context.fill(x + width - i - 1, y + radius, x + width - i, y + height - radius, color);
        }
    }

    /**
     * Draws a dashed border around a rectangle (non-rounded corners).
     *
     * @param thickness line thickness (>=1)
     * @param dash      dash length in pixels (>=1)
     * @param gap       gap length in pixels (>=1)
     * @param color     ARGB color
     */
    public static void drawDashedBorder(DrawContext context, int x, int y, int w, int h, int color, int thickness, int dash, int gap) {
        thickness = Math.max(1, thickness);
        dash = Math.max(1, dash);
        gap = Math.max(1, gap);
        int period = dash + gap;

        for (int tx = 0; tx < w; tx += period) {
            int seg = Math.min(dash, w - tx);
            fillRect(context, x + tx, y, seg, thickness, color);
            fillRect(context, x + tx, y + h - thickness, seg, thickness, color);
        }

        for (int ty = 0; ty < h; ty += period) {
            int seg = Math.min(dash, h - ty);
            fillRect(context, x, y + ty, thickness, seg, color);
            fillRect(context, x + w - thickness, y + ty, thickness, seg, color);
        }
    }

    /**
     * Draws a rectangular shadow behind an element (simple offset fill).
     *
     * @param offsetX horizontal shadow offset
     * @param offsetY vertical shadow offset
     * @param shadowColor ARGB color (usually semi-transparent)
     */
    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int offsetX, int offsetY, int shadowColor) {
        context.fill(x + offsetX, y + offsetY, x + width + offsetX, y + height + offsetY, shadowColor);
    }

    /**
     * Draws a rounded panel with a background fill and a border.
     */
    public static void drawPanel(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                 int backgroundColor, int borderColor, int borderThickness) {
        drawRoundedRect(context, x, y, width, height, cornerRadius, backgroundColor);
        drawRoundedRectBorder(context, x, y, width, height, cornerRadius, borderColor, borderThickness);
    }

    /**
     * Draws a rounded panel with a drop shadow, then border.
     */
    public static void drawPanelWithShadow(DrawContext context, int x, int y, int width, int height, int cornerRadius,
                                           int backgroundColor, int borderColor, int borderThickness, int shadowColor) {
        drawShadow(context, x, y, width, height, 2, 2, shadowColor);
        drawPanel(context, x, y, width, height, cornerRadius, backgroundColor, borderColor, borderThickness);
    }

    /**
     * Fills a rectangle with a vertical gradient from top to bottom.
     *
     * @param startColor top color (ARGB)
     * @param endColor   bottom color (ARGB)
     */
    public static void drawGradient(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }

    /**
     * Fills a rounded rectangle with a vertical gradient (fast approximation).
     * <p>Uses a central gradient + side bands + flat-color corners for speed.</p>
     */
    public static void drawRoundedRectGradientV(DrawContext context, int x, int y, int w, int h, int radius, int startColor, int endColor) {
        radius = Math.clamp(radius, 0, Math.min(w, h) / 2);
        context.fillGradient(x + radius, y, x + w - radius, y + h, startColor, endColor);
        context.fillGradient(x, y + radius, x + radius, y + h - radius, startColor, endColor);
        context.fillGradient(x + w - radius, y + radius, x + w, y + h - radius, startColor, endColor);
        fillRect(context, x, y, radius, radius, startColor);
        fillRect(context, x + w - radius, y, radius, radius, startColor);
        fillRect(context, x, y + h - radius, radius, radius, endColor);
        fillRect(context, x + w - radius, y + h - radius, radius, radius, endColor);
    }

    /**
     * Draws a solid rectangular border.
     *
     * @param thickness border thickness in pixels (>=1)
     */
    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color, int thickness) {
        thickness = Math.max(1, thickness);
        for (int i = 0; i < thickness; i++) {
            context.fill(x + i, y + i, x + width - i, y + i + 1, color);
            context.fill(x + i, y + height - i - 1, x + width - i, y + height - i, color);
            context.fill(x + i, y + i, x + i + 1, y + height - i, color);
            context.fill(x + width - i - 1, y + i, x + width - i, y + height - i, color);
        }
    }

    /**
     * Checks if a point lies inside a rectangle [x, x+width] × [y, y+height].
     *
     * @return {@code true} if inside, {@code false} otherwise
     */
    public static boolean isPointInRect(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    /**
     * Draws a filled circle using horizontal scanlines (fast, no allocations).
     *
     * @param cx    center X
     * @param cy    center Y
     * @param r     radius (>=0)
     * @param color ARGB fill color
     */
    public static void drawCircleFilled(DrawContext context, int cx, int cy, int r, int color) {
        if (r <= 0) return;
        int r2 = r * r;
        for (int dy = -r; dy <= r; dy++) {
            int dx = (int) Math.floor(Math.sqrt((double) r2 - dy * dy));
            context.fill(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
    }

    /**
     * Draws a circle outline using Bresenham's algorithm.
     *
     * @param thickness outline thickness (>=1)
     * @param color     ARGB color
     */
    public static void drawCircleBorder(DrawContext context, int cx, int cy, int r, int thickness, int color) {
        if (r <= 0 || thickness <= 0) return;
        for (int t = 0; t < thickness; t++) {
            int rr = r - t;
            if (rr <= 0) break;
            int x = rr;
            int y = 0;
            int err = 1 - x;
            while (x >= y) {
                putPixel(context, cx + x, cy + y, color);
                putPixel(context, cx + y, cy + x, color);
                putPixel(context, cx - y, cy + x, color);
                putPixel(context, cx - x, cy + y, color);
                putPixel(context, cx - x, cy - y, color);
                putPixel(context, cx - y, cy - x, color);
                putPixel(context, cx + y, cy - x, color);
                putPixel(context, cx + x, cy - y, color);
                y++;
                if (err < 0) err += 2 * y + 1;
                else { x--; err += 2 * (y - x + 1); }
            }
        }
    }

    /** Writes a single pixel using {@link DrawContext#fill}. */
    private static void putPixel(DrawContext context, int x, int y, int color) {
        context.fill(x, y, x + 1, y + 1, color);
    }

    /**
     * Draws a filled triangle using the POSITION_COLOR shader.
     *
     * @param color ARGB fill color
     */
    public static void drawTriangleFilled(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        float a = ((color >>> 24) & 0xFF) / 255f;
        float r = ((color >>> 16) & 0xFF) / 255f;
        float g = ((color >>> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buf.vertex(x1, y1, 0).color(r, g, b, a);
        buf.vertex(x2, y2, 0).color(r, g, b, a);
        buf.vertex(x3, y3, 0).color(r, g, b, a);
        BuiltBuffer built = buf.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        RenderSystem.disableBlend();
    }

    /**
     * Draws text centered on the provided (cx, cy).
     *
     * @param shadow if {@code true}, uses Minecraft's text shadow
     */
    public static void drawCenteredText(DrawContext context, TextRenderer tr, String text, int cx, int cy, int color, boolean shadow) {
        if (text == null) text = "";
        int w = tr.getWidth(text);
        int x = cx - w / 2;
        int y = cy - tr.fontHeight / 2;
        context.drawText(tr, text, x, y, color, shadow);
    }

    /**
     * Draws text clipped to {@code maxWidth}, appending an ellipsis if it overflows.
     *
     * @param ellipsis text to append when clipped (e.g., "…"); if {@code null}, defaults to "…"
     */
    public static void drawTextClipped(DrawContext context, TextRenderer tr, String text, int x, int y, int maxWidth, int color, boolean shadow, String ellipsis) {
        if (text == null) text = "";
        if (ellipsis == null) ellipsis = "…";
        int w = tr.getWidth(text);
        if (w <= maxWidth) {
            context.drawText(tr, text, x, y, color, shadow);
            return;
        }
        int ellW = tr.getWidth(ellipsis);
        int target = Math.max(0, maxWidth - ellW);
        int cut = text.length();
        while (cut > 0 && tr.getWidth(text.substring(0, cut)) > target) cut--;
        String out = (cut <= 0) ? ellipsis : text.substring(0, cut) + ellipsis;
        context.drawText(tr, out, x, y, color, shadow);
    }

    /**
     * Enables scissor clipping for a region.
     *
     * @param x left
     * @param y top
     * @param width width in pixels
     * @param height height in pixels
     */
    public static void enableClipping(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    /**
     * Disables scissor clipping.
     */
    public static void disableClipping(DrawContext context) {
        context.disableScissor();
    }

    /**
     * Pushes a clipping rectangle onto the stack. The new region is intersected with the current top.
     * Call {@link #popClip(DrawContext)} to restore the previous region.
     */
    public static void pushClip(DrawContext context, int x, int y, int width, int height) {
        Rectangle next = new Rectangle(x, y, width, height);
        Deque<Rectangle> stack = CLIP_STACK.get();
        if (!stack.isEmpty()) next = next.intersection(stack.peek());
        stack.push(next);
        enableClipping(context, next.x, next.y, next.width, next.height);
    }

    /**
     * Pops the current clipping rectangle and restores the previous one (if any).
     */
    public static void popClip(DrawContext context) {
        Deque<Rectangle> stack = CLIP_STACK.get();
        if (stack.isEmpty()) return;
        stack.pop();
        disableClipping(context);
        if (!stack.isEmpty()) {
            Rectangle prev = stack.peek();
            enableClipping(context, prev.x, prev.y, prev.width, prev.height);
        }
    }

    /**
     * Draws a textured quad with optional 90-degree rotation and optional horizontal mirroring.
     *
     * @param id       the texture {@link Identifier}
     * @param x1       left screen coordinate
     * @param y1       top screen coordinate
     * @param x2       right screen coordinate
     * @param y2       bottom screen coordinate
     * @param rotation rotation in 90° steps (0..3)
     * @param parity   {@code true} to flip horizontally
     * @param color    tint color (ARGB). Use fully opaque white for no tint.
     */
    public static void drawImage(Identifier id, int x1, int y1, int x2, int y2, int rotation, boolean parity, Color color) {
        int[][] texCoords = { {0,1},{1,1},{1,0},{0,0} };
        rotation = ((rotation % 4) + 4) % 4;
        for (int i = 0; i < rotation; i++) {
            int t0 = texCoords[3][0];
            int t1 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0]; texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0]; texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0]; texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = t0; texCoords[0][1] = t1;
        }
        if (parity) {
            int t = texCoords[1][0]; texCoords[1][0] = texCoords[0][0]; texCoords[0][0] = t;
            t = texCoords[3][0];     texCoords[3][0] = texCoords[2][0]; texCoords[2][0] = t;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, id);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        RenderSystem.enableBlend();
        buf.vertex(x1, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[0][0], texCoords[0][1]);
        buf.vertex(x2, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[1][0], texCoords[1][1]);
        buf.vertex(x2, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[2][0], texCoords[2][1]);
        buf.vertex(x1, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[3][0], texCoords[3][1]);
        BuiltBuffer built = buf.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        RenderSystem.disableBlend();
    }

    /**
     * Draws a sub-rectangle region of a texture (UVs specified in pixels).
     *
     * @param id   texture {@link Identifier}
     * @param x    left on screen
     * @param y    top on screen
     * @param w    quad width on screen
     * @param h    quad height on screen
     * @param u    source left in pixels
     * @param v    source top in pixels
     * @param rw   source width in pixels
     * @param rh   source height in pixels
     * @param texW full texture width in pixels
     * @param texH full texture height in pixels
     * @param argb tint color (ARGB). Use 0xFFFFFFFF for no tint.
     */
    public static void drawImageRegion(Identifier id, int x, int y, int w, int h,
                                       int u, int v, int rw, int rh,
                                       int texW, int texH, int argb) {
        float a = ((argb >>> 24) & 0xFF) / 255f;
        float r = ((argb >>> 16) & 0xFF) / 255f;
        float g = ((argb >>> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        float u0 = u / (float) texW;
        float v0 = v / (float) texH;
        float u1 = (u + rw) / (float) texW;
        float v1 = (v + rh) / (float) texH;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, id);
        RenderSystem.enableBlend();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buf.vertex(x,     (float) y + h, 0).color(r, g, b, a).texture(u0, v1);
        buf.vertex((float) x + w, (float) y + h, 0).color(r, g, b, a).texture(u1, v1);
        buf.vertex((float) x + w, y,     0).color(r, g, b, a).texture(u1, v0);
        buf.vertex(x,     y,     0).color(r, g, b, a).texture(u0, v0);
        BuiltBuffer built = buf.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        RenderSystem.disableBlend();
    }

    /**
     * Builds an ARGB color from individual channels.
     *
     * @param a alpha   (0..255)
     * @param r red     (0..255)
     * @param g green   (0..255)
     * @param b blue    (0..255)
     */
    public static int argb(int a, int r, int g, int b) {
        a = clamp255(a); r = clamp255(r); g = clamp255(g); b = clamp255(b);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Returns a color with the alpha channel replaced.
     *
     * @param color base ARGB color
     * @param alpha new alpha (0..255)
     */
    public static int withAlpha(int color, int alpha) {
        return (clamp255(alpha) << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Multiplies the alpha by {@code factor} and returns the new color.
     *
     * @param factor 0..1
     */
    public static int multiplyAlpha(int color, float factor) {
        int a = (int) (((color >>> 24) & 0xFF) * factor);
        return withAlpha(color, a);
    }

    /**
     * Lightens the given ARGB color toward white by {@code ratio}.
     *
     * @param ratio 0..1
     */
    public static int lighten(int color, float ratio) {
        ratio = clamp01(ratio);
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = (int) (r + (255 - r) * ratio);
        g = (int) (g + (255 - g) * ratio);
        b = (int) (b + (255 - b) * ratio);
        return argb(a, r, g, b);
    }

    /**
     * Darkens the given ARGB color toward black by {@code ratio}.
     *
     * @param ratio 0..1
     */
    public static int darken(int color, float ratio) {
        ratio = clamp01(ratio);
        int a = (color >>> 24) & 0xFF;
        int r = (int) (((color >>> 16) & 0xFF) * (1f - ratio));
        int g = (int) (((color >>> 8) & 0xFF) * (1f - ratio));
        int b = (int) ((color & 0xFF) * (1f - ratio));
        return argb(a, r, g, b);
    }

    /**
     * Linearly interpolates between two ARGB colors.
     *
     * @param c1 first color
     * @param c2 second color
     * @param t  interpolation factor in [0, 1]
     */
    public static int lerpColor(int c1, int c2, float t) {
        t = clamp01(t);
        int a = (int) (((c1 >>> 24) & 0xFF) * (1 - t) + ((c2 >>> 24) & 0xFF) * t);
        int r = (int) (((c1 >>> 16) & 0xFF) * (1 - t) + ((c2 >>> 16) & 0xFF) * t);
        int g = (int) (((c1 >>> 8) & 0xFF) * (1 - t) + ((c2 >>> 8) & 0xFF) * t);
        int b = (int) (((c1) & 0xFF) * (1 - t) + ((c2) & 0xFF) * t);
        return argb(a, r, g, b);
    }

    private static int clamp255(int v) {
        return Math.clamp(v, 0, 255);
    }

    private static float clamp01(float v) {
        return Math.clamp(v, 0f, 1f);
    }
}
