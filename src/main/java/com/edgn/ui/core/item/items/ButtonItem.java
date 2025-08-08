package com.edgn.ui.core.item.items;

import com.edgn.ui.core.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawContextUtils;
import net.minecraft.client.font.TextRenderer;
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
            this.textComponent = new TextComponent(text, textRenderer)
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

    public ButtonItem textBold() { if (textComponent != null) textComponent.bold(); return this; }
    public ButtonItem textItalic() { if (textComponent != null) textComponent.italic(); return this; }
    public ButtonItem textShadow() { if (textComponent != null) textComponent.shadow(); return this; }
    public ButtonItem textGlow() { if (textComponent != null) textComponent.glow(); return this; }
    public ButtonItem textGlow(int color) { if (textComponent != null) textComponent.glow(color); return this; }
    public ButtonItem textPulse() { if (textComponent != null) textComponent.pulse(); return this; }
    public ButtonItem textWave() { if (textComponent != null) textComponent.wave(); return this; }
    public ButtonItem textTypewriter() { if (textComponent != null) textComponent.typewriter(); return this; }
    public ButtonItem textRainbow() { if (textComponent != null) textComponent.rainbow(); return this; }

    public TextComponent getTextComponent() { return textComponent; }
    public String getText() { return textComponent != null ? textComponent.getText() : ""; }
    public boolean hasText() { return textComponent != null && !textComponent.getText().isEmpty(); }

    public ButtonItem asFlexPercent(int basisPercent, int grow, int shrink) {
        switch (Math.max(0, grow)) {
            case 3 -> addClass(StyleKey.FLEX_GROW_3);
            case 2 -> addClass(StyleKey.FLEX_GROW_2);
            case 1 -> addClass(StyleKey.FLEX_GROW_1);
            default -> addClass(StyleKey.FLEX_GROW_0);
        }
        if (shrink <= 0) addClass(StyleKey.FLEX_SHRINK_0); else addClass(StyleKey.FLEX_SHRINK_1);
        return this;
    }


    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (enabled && contains(mouseX, mouseY)) {
            setState(ItemState.PRESSED);
            return super.onMouseClick(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void onMouseEnter() {
        super.onMouseEnter();
        if (textComponent != null && !textComponent.getActiveAnimations().isEmpty()) {
            textComponent.startAnimation();
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        final int cx = getCalculatedX();
        final int cy = getCalculatedY();
        final int cw = getCalculatedWidth();
        final int ch = getCalculatedHeight();

        final int bgColor = getStateColor();
        final int borderRadius = getBorderRadius();
        final Shadow shadow = getShadow();
        final float anim = getAnimationProgress();

        if (state == ItemState.HOVERED && hasClass(StyleKey.HOVER_SCALE)) {
            float scale = 1.0f + (0.05f * anim);
            int sw = Math.max(0, Math.round(cw * scale));
            int sh = Math.max(0, Math.round(ch * scale));
            int ox = (sw - cw) / 2;
            int oy = (sh - ch) / 2;

            if (shadow != null) {
                DrawContextUtils.drawShadow(context, cx - ox, cy - oy, sw, sh, 3, 3, shadow.color);
            }
            DrawContextUtils.drawRoundedRect(context, cx - ox, cy - oy, sw, sh, borderRadius, bgColor);
        } else {
            if (shadow != null) {
                DrawContextUtils.drawShadow(context, cx, cy, cw, ch, 2, 2, shadow.color);
            }
            DrawContextUtils.drawRoundedRect(context, cx, cy, cw, ch, borderRadius, bgColor);
        }

        if (focused && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawContextUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, borderRadius + 2, focusColor, 2);
        }

        renderText(context, cx, cy, cw, ch);
    }

    private void renderText(DrawContext context, int cx, int cy, int cw, int ch) {
        if (textComponent == null) return;

        int contentX = cx + getPaddingLeft();
        int contentY = cy + getPaddingTop();
        int contentW = Math.max(0, cw - getPaddingLeft() - getPaddingRight());
        int contentH = Math.max(0, ch - getPaddingTop() - getPaddingBottom());

        if (!textComponent.hasCustomStyling()) {
            textComponent.color(getComputedStyles().textColor);
        }

        context.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        try {
            textComponent.render(context, contentX, contentY, contentW, contentH);
        } finally {
            context.disableScissor();
        }
    }

    public void startTextAnimation() { if (textComponent != null) textComponent.startAnimation(); }
    public void stopTextAnimation() { if (textComponent != null) textComponent.stopAnimation(); }

    public ButtonItem withFancyText() { if (textComponent != null) textComponent.rainbow().glow().pulse(); return this; }
    public ButtonItem withGlowingText() { if (textComponent != null) textComponent.glow().shadow(); return this; }
    public ButtonItem withAnimatedText() { if (textComponent != null) textComponent.wave().pulse(); return this; }

    public ButtonItem asPrimaryButton() { return addClass(StyleKey.PRIMARY, StyleKey.HOVER_SCALE, StyleKey.SHADOW_SM); }
    public ButtonItem asSecondaryButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.SECONDARY, StyleKey.HOVER_BRIGHTEN); }
    public ButtonItem asDangerButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.DANGER, StyleKey.HOVER_SCALE); }
    public ButtonItem asSuccessButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.SUCCESS, StyleKey.HOVER_SCALE); }
    public ButtonItem asWarningButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.WARNING, StyleKey.HOVER_SCALE); }
    public ButtonItem asInfoButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.INFO, StyleKey.HOVER_BRIGHTEN); }
    public ButtonItem asGhostButton() { return removeClass(StyleKey.PRIMARY).addClass(StyleKey.BG_OPACITY_0, StyleKey.HOVER_BRIGHTEN); }
    public ButtonItem asFancyButton() { return addClass(StyleKey.HOVER_SCALE, StyleKey.SHADOW_LG); }


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
    public ButtonItem setTextRenderer(TextRenderer textRenderer) {
        super.setTextRenderer(textRenderer);
        if (this.textComponent != null) this.textComponent.setTextRenderer(textRenderer);
        return this;
    }
}
