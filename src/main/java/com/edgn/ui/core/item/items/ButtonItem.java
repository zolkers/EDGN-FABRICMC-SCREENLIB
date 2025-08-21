package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.renderer.FontRenderer;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public class ButtonItem extends BaseItem {
    private TextComponent textComponent;
    private int textSafetyMargin = 8;

    public ButtonItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        addClass(StyleKey.PRIMARY, StyleKey.ROUNDED_MD, StyleKey.P_2, StyleKey.TEXT_WHITE);
    }

    public ButtonItem(UIStyleSystem styleSystem, int x, int y, int width, int height, String text) {
        this(styleSystem, x, y, width, height);
        setText(text);
    }

    public ButtonItem(UIStyleSystem styleSystem, int x, int y, int width, int height, TextComponent textComponent) {
        this(styleSystem, x, y, width, height);
        setText(textComponent);
    }

    public ButtonItem withText(String text) {
        if (text != null && !text.isEmpty()) {
            this.textComponent = new TextComponent(text, fontRenderer)
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE)
                    .truncate()
                    .setSafetyMargin(textSafetyMargin);
        }
        return this;
    }

    public ButtonItem withText(TextComponent textComponent) {
        if (textComponent != null) {
            this.textComponent = textComponent
                    .setOverflowMode(TextComponent.TextOverflowMode.TRUNCATE)
                    .setSafetyMargin(textSafetyMargin)
                    .align(TextComponent.TextAlign.CENTER)
                    .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
        }
        return this;
    }

    public ButtonItem setText(String text) { return withText(text); }
    public ButtonItem setText(TextComponent text) { return withText(text); }

    public ButtonItem setTextSafetyMargin(int margin) {
        this.textSafetyMargin = Math.max(0, margin);
        if (textComponent != null) textComponent.setSafetyMargin(this.textSafetyMargin);
        return this;
    }

    public ButtonItem setEllipsis(String ellipsis) {
        if (textComponent != null) textComponent.setEllipsis(ellipsis);
        return this;
    }

    public ButtonItem textColor(int color) {
        if (textComponent != null) textComponent.color(color);
        return this;
    }
    public ButtonItem textBold() {
        if (textComponent != null) textComponent.bold();
        return this;
    }
    public ButtonItem textItalic() {
        if (textComponent != null) textComponent.italic();
        return this;

    }
    public ButtonItem textShadow() {
        if (textComponent != null) textComponent.shadow();
        return this;

    }
    public ButtonItem textGlow() {
        if (textComponent != null) textComponent.glow();
        return this;

    }
    public ButtonItem textGlow(int color) {
        if (textComponent != null) textComponent.glow(color);
        return this;

    }
    public ButtonItem textPulse() {
        if (textComponent != null) textComponent.pulse();
        return this;
    }
    public ButtonItem textWave() {
        if (textComponent != null) textComponent.wave();
        return this;
    }

    public ButtonItem textTypewriter() {
        if (textComponent != null) textComponent.typewriter();
        return this;
    }

    public ButtonItem textRainbow() {
        if (textComponent != null) textComponent.rainbow();
        return this;
    }

    public TextComponent getTextComponent() { return textComponent; }
    public String getText() { return textComponent != null ? textComponent.getText() : ""; }
    public boolean hasText() { return textComponent != null && !textComponent.getText().isEmpty(); }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        this.setState(ItemState.PRESSED);
        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        boolean inside = canInteract(mouseX, mouseY);
        setState(inside ? ItemState.HOVERED : ItemState.NORMAL);
        return inside;
    }

    @Override
    public void onTick() {
        if(this.isHovered()) setState(ItemState.HOVERED);
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int baseBg = getBgColor();
        if (baseBg == 0) baseBg = styleSystem.getColor(StyleKey.PRIMARY);

        int bg = backgroundForState(baseBg);
        int radius = getBorderRadius();
        Shadow shadow = getShadow();

        float scale = (hasClass(StyleKey.HOVER_SCALE) && isHovered())
                ? getAnimatedScale()
                : 1.0f;

        if (scale != 1.0f) {
            int sw = Math.max(0, Math.round(cw * scale));
            int sh = Math.max(0, Math.round(ch * scale));
            int ox = (sw - cw) / 2;
            int oy = (sh - ch) / 2;
            if (shadow != null) DrawingUtils.drawShadow(context, cx - ox, cy - oy, sw, sh, 3, 3, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx - ox, cy - oy, sw, sh, radius, bg);
        } else {
            if (shadow != null) DrawingUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
            DrawingUtils.drawRoundedRect(context, cx, cy, cw, ch, radius, bg);
        }

        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawingUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, radius + 2, focusColor, 2);
        }

        renderText(context, cx, cy, cw, ch);
    }

    private int backgroundForState(int base) {
        if (getState() == ItemState.PRESSED) {
            return darken(base);
        }
        if (this.isHovered()) {
            float f = hasClass(StyleKey.HOVER_BRIGHTEN) ? 0.20f : 0.08f;
            return brighten(base, f);
        }
        return base;
    }

    private int brighten(int color, float ratio) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.min(255, Math.round(r + (255 - r) * ratio));
        g = Math.min(255, Math.round(g + (255 - g) * ratio));
        b = Math.min(255, Math.round(b + (255 - b) * ratio));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int darken(int color) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        r = Math.max(0, Math.round(r * (1.0f - 0.16f)));
        g = Math.max(0, Math.round(g * (1.0f - 0.16f)));
        b = Math.max(0, Math.round(b * (1.0f - 0.16f)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void renderText(DrawContext context, int cx, int cy, int cw, int ch) {
        if (textComponent == null) return;

        int contentX = cx + getPaddingLeft();
        int contentY = cy + getPaddingTop();
        int contentW = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int contentH = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        if (!textComponent.hasCustomStyling()) {
            textComponent.color(getComputedStyles().getTextColor());
        }

        textComponent.render(context, contentX, contentY, contentW, contentH);
    }

    @Override public ButtonItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public ButtonItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public ButtonItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public ButtonItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public ButtonItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public ButtonItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public ButtonItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public ButtonItem setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public ButtonItem setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public ButtonItem setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public ButtonItem setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public ButtonItem setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public ButtonItem setZIndex(ZIndex.Layer layer, int priority) { super.setZIndex(layer, priority); return this; }
    @Override public ButtonItem setConstraints(LayoutConstraints constraints) { super.setConstraints(constraints); return this; }

    @Override
    public ButtonItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        if (this.textComponent != null) {
            this.textComponent.setFontRenderer(fontRenderer);
        }
        return this;
    }
}
