package com.edgn.ui.core.container.components.font;

import net.minecraft.client.gui.DrawContext;
import java.util.List;

public interface FontRenderer {
    int width(String text);
    int lineHeight();
    void draw(DrawContext ctx, String text, int x, int y, int argb, boolean shadow);
    List<String> wrap(String text, int maxWidth);
    int advance(int codePoint);
}
