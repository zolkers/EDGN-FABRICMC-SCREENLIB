package com.edgn.ui.core.container.components.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class MinecraftFontRenderer implements FontRenderer {
    private final TextRenderer tr;

    public MinecraftFontRenderer(TextRenderer tr) { this.tr = tr; }
    public MinecraftFontRenderer() { this(MinecraftClient.getInstance().textRenderer); }

    @Override public int width(String text) { return tr.getWidth(text); }
    @Override public int lineHeight() { return tr.fontHeight; }

    @Override
    public void draw(DrawContext ctx, String text, int x, int y, int argb, boolean shadow) {
        ctx.drawText(tr, text, x, y, argb, shadow);
    }

    @Override
    public List<String> wrap(String text, int maxWidth) {
        var ordered = tr.wrapLines(Text.literal(text), maxWidth);
        var out = new ArrayList<String>(ordered.size());
        for (var ot : ordered) {
            var sb = new StringBuilder();
            ot.accept((i, style, cp) -> { sb.append(Character.toChars(cp)); return true; });
            out.add(sb.toString());
        }
        return out;
    }

    @Override
    public int advance(int codePoint) {
        return tr.getWidth(new String(Character.toChars(codePoint)));
    }
}
