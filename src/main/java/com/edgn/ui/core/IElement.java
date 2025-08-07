package com.edgn.ui.core;

import com.edgn.ui.css.StyleKey;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.font.TextRenderer;

public interface IElement {
    <T extends IElement> T addClass(StyleKey... keys);
    <T extends IElement> T removeClass(StyleKey key);
    <T extends IElement> T setZIndex(ZIndex zIndex);
    <T extends IElement> T setZIndex(ZIndex.Layer layer);
    <T extends IElement> T setZIndex(ZIndex.Layer layer, int priority);
    <T extends IElement> T setZIndex(int intZIndex);
    <T extends IElement> T onClick(Runnable handler);
    <T extends IElement> T onMouseEnter(Runnable handler);
    <T extends IElement> T onMouseLeave(Runnable handler);
    <T extends IElement> T onFocusGained(Runnable handler);
    <T extends IElement> T onFocusLost(Runnable handler);
    <T extends IElement> T setConstraints(LayoutConstraints constraints);
    <T extends IElement> T setVisible(boolean visible);
    <T extends IElement> T setEnabled(boolean enabled);
    <T extends IElement> T setTextRenderer(TextRenderer textRenderer);
}
