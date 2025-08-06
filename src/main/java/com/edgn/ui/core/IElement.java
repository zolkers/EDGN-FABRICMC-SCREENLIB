package com.edgn.ui.core;

import com.edgn.ui.css.StyleKey;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.font.TextRenderer;

public interface IElement {
    <T extends UIElement> T addClass(StyleKey... keys);
    <T extends UIElement> T removeClass(StyleKey key);
    <T extends UIElement> T onClick(Runnable handler);
    <T extends UIElement> T onMouseEnter(Runnable handler);
    <T extends UIElement> T onMouseLeave(Runnable handler);
    <T extends UIElement> T onFocusGained(Runnable handler);
    <T extends UIElement> T onFocusLost(Runnable handler);
    <T extends UIElement> T setConstraints(LayoutConstraints constraints);
    <T extends UIElement> T setVisible(boolean visible);
    <T extends UIElement> T setEnabled(boolean enabled);
    <T extends UIElement> T setTextRenderer(TextRenderer textRenderer);
    <T extends UIElement> T setZIndex(ZIndex zIndex);
}
