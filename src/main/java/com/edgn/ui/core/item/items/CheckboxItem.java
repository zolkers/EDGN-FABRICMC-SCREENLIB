package com.edgn.ui.core.item.items;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.models.values.BooleanModel;
import com.edgn.ui.core.renderer.FontRenderer;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public class CheckboxItem extends BaseItem {

    public enum LabelPosition { RIGHT, LEFT, TOP, BOTTOM }
    public enum CheckType { TICK, CROSS, MINUS, DOT, FILL }

    private final BooleanModel model;

    private int boxSize   = 16;
    private int boxRadius = 0;
    private int gap       = 6;

    private int borderColor  = 0xFF9AA0A6;
    private int onColor      = 0xFF4F8EF7;
    private int offColor     = 0x00000000;
    private int checkColor   = 0xFFFFFFFF;

    private TextComponent label;
    private LabelPosition labelPosition = LabelPosition.RIGHT;
    private Integer labelColor = null;

    private CheckType checkType = CheckType.TICK;

    private int lastBoxX;
    private int lastBoxY;
    private int lastBoxS;
    private boolean pressedInBox = false;

    public CheckboxItem(UIStyleSystem styleSystem, int x, int y, int w, int h, BooleanModel model) {
        super(styleSystem, x, y, w, h);
        this.model = Objects.requireNonNull(model, "model");
        addClass(StyleKey.ROUNDED_SM, StyleKey.P_1);
    }

    public CheckboxItem(UIStyleSystem styleSystem, int x, int y, int w, int h,
                        BooleanModel model, String text) {
        this(styleSystem, x, y, w, h, model);
        withLabel(text);
    }

    public CheckboxItem(UIStyleSystem styleSystem, int x, int y, int w, int h,
                        BooleanModel model, TextComponent label) {
        this(styleSystem, x, y, w, h, model);
        withLabel(label);
    }

    public CheckboxItem(UIStyleSystem styleSystem, int x, int y, int w, int h,
                        BooleanModel model, TextComponent label, LabelPosition position) {
        this(styleSystem, x, y, w, h, model);
        withLabel(label).labelPosition(position);
    }

    public CheckboxItem withLabel(String text) {
        if (text != null) {
            this.label = new TextComponent(text, fontRenderer).truncate();
        }
        return this;
    }

    public CheckboxItem withLabel(TextComponent tc) { this.label = tc; return this; }

    public CheckboxItem labelPosition(LabelPosition pos) { this.labelPosition = pos; return this; }
    public CheckboxItem labelColor(Integer color)        { this.labelColor = color; return this; }

    public CheckboxItem boxSize(int px)   { this.boxSize   = Math.max(10, px); return this; }
    public CheckboxItem boxRadius(int px) { this.boxRadius = Math.max(0, px);  return this; }
    public CheckboxItem gap(int px)       { this.gap       = Math.max(0, px);  return this; }

    public CheckboxItem borderColor(int c)  { this.borderColor  = c; return this; }
    public CheckboxItem checkColor(int c)   { this.checkColor   = c; return this; }
    public CheckboxItem onColor(int c)      { this.onColor      = c; return this; }
    public CheckboxItem offColor(int c)     { this.offColor     = c; return this; }

    public CheckboxItem checkType(CheckType t) { this.checkType = t; return this; }

    public boolean isChecked()                 { return model.get(); }
    public CheckboxItem setChecked(boolean v)  { model.set(v); return this; }
    public BooleanModel getModel()             { return model; }

    @Override
    public void render(DrawContext ctx) {
        if (!visible) return;

        updateConstraints();

        Layout lay = computeLayout();
        lastBoxX = lay.bx; lastBoxY = lay.by; lastBoxS = lay.s;
        renderBox(ctx, lay);
        if (model.get()) renderCheck(ctx, lay);

        renderLabel(ctx, lay);
        renderFocusRing(ctx);
    }

    private Layout computeLayout() {
        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int contentLeft   = cx + getPaddingLeft();
        int contentTop    = cy + getPaddingTop();
        int contentRight  = cx + cw - getPaddingRight();
        int contentBottom = cy + ch - getPaddingBottom();

        int contentW = Math.max(0, contentRight - contentLeft);
        int contentH = Math.max(0, contentBottom - contentTop);

        int s = Math.clamp(Math.min(contentW, contentH), 12, boxSize);

        return switch (labelPosition) {
            case RIGHT -> layoutRight(contentLeft, contentTop, contentRight, contentH, s);
            case LEFT -> layoutLeft(contentLeft, contentTop, contentRight, contentH, s);
            case TOP -> layoutTop(contentLeft, contentTop, contentW, contentBottom, s);
            case BOTTOM -> layoutBottom(contentLeft, contentTop, contentW, contentBottom, s);
        };
    }

    private Layout layoutRight(int left, int top, int right, int h, int s) {
        Layout l = new Layout();
        l.s = s;
        l.bx = left;
        l.by = top + (h - s) / 2;
        if (label != null) {
            l.lx = l.bx + s + gap;
            l.ly = top;
            l.lw = Math.max(0, right - l.lx);
            l.lh = Math.max(0, h);
        }
        return l;
    }

    private Layout layoutLeft(int left, int top, int right, int h, int s) {
        Layout l = new Layout();
        l.s = s;
        l.bx = right - s;
        l.by = top + (h - s) / 2;
        if (label != null) {
            l.lx = left;
            l.ly = top;
            l.lw = Math.max(0, l.bx - gap - left);
            l.lh = Math.max(0, h);
        }
        return l;
    }

    private Layout layoutTop(int left, int top, int w, int bottom, int s) {
        Layout l = new Layout();
        l.s = s;
        l.by = bottom - s;
        l.bx = left + (w - s) / 2;
        if (label != null) {
            l.lx = left;
            l.ly = top;
            l.lw = Math.max(0, w);
            l.lh = Math.max(0, (bottom - top) - s - gap);
        }
        return l;
    }

    private Layout layoutBottom(int left, int top, int w, int bottom, int s) {
        Layout l = new Layout();
        l.s = s;
        l.by = top;
        l.bx = left + (w - s) / 2;
        if (label != null) {
            l.lx = left;
            l.ly = l.by + s + gap;
            l.lw = Math.max(0, w);
            l.lh = Math.max(0, bottom - l.ly);
        }
        return l;
    }

    private void renderBox(DrawContext ctx, Layout l) {
        int fill = model.get() ? onColor : offColor;

        Shadow shadow = getShadow();
        if (shadow != null) {
            DrawingUtils.drawShadow(ctx, l.bx, l.by, l.s, l.s, 2, 2, shadow.color);
        }
        DrawingUtils.drawRoundedRect(ctx, l.bx, l.by, l.s, l.s, boxRadius, fill);
        DrawingUtils.drawRoundedRectBorder(ctx, l.bx, l.by, l.s, l.s, boxRadius, borderColor, 1);
    }

    private void renderCheck(DrawContext ctx, Layout l) {
        switch (checkType) {
            case FILL:  drawFill (ctx, l, checkColor); break;
            case DOT:   drawDot  (ctx, l, checkColor); break;
            case MINUS: drawMinus(ctx, l, checkColor); break;
            case TICK:  drawTick (ctx, l, checkColor); break;
            case CROSS: drawCross(ctx, l, checkColor); break;
        }
    }

    private void renderLabel(DrawContext ctx, Layout l) {
        if (label == null) return;

        if (!label.hasCustomStyling()) {
            label.color(labelColor != null ? labelColor : getComputedStyles().getTextColor());
        }
        switch (labelPosition) {
            case RIGHT:
                label.align(TextComponent.TextAlign.LEFT)
                        .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
                break;
            case LEFT:
                label.align(TextComponent.TextAlign.RIGHT)
                        .verticalAlign(TextComponent.VerticalAlign.MIDDLE);
                break;
            case TOP:
                label.align(TextComponent.TextAlign.CENTER)
                        .verticalAlign(TextComponent.VerticalAlign.BOTTOM);
                break;
            case BOTTOM:
                label.align(TextComponent.TextAlign.CENTER)
                        .verticalAlign(TextComponent.VerticalAlign.TOP);
                break;
        }
        label.render(ctx, l.lx, l.ly, l.lw, l.lh);
    }

    private void renderFocusRing(DrawContext ctx) {
        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            int cx = getCalculatedX();
            int cy = getCalculatedY();
            int cw = getCalculatedWidth();
            int ch = getCalculatedHeight();
            DrawingUtils.drawRoundedRectBorder(ctx, cx - 2, cy - 2, cw + 4, ch + 4,
                    Math.max(4, boxRadius + 4), focusColor, 2);
        }
    }


    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        pressedInBox = isInBox(mouseX, mouseY);
        if (pressedInBox) {
            setState(ItemState.PRESSED);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        boolean inside = isInBox(mouseX, mouseY);
        setState(isHovered() ? ItemState.HOVERED : ItemState.NORMAL);
        if (pressedInBox && inside) {
            model.toggle();
            if (onClickHandler != null) onClickHandler.run();
            pressedInBox = false;
            return true;
        }
        pressedInBox = false;
        return false;
    }

    private boolean isInBox(double x, double y) {
        if (lastBoxS <= 0) {
            updateConstraints();
            int cx = getCalculatedX();
            int cy = getCalculatedY();
            int cw = getCalculatedWidth();
            int ch = getCalculatedHeight();
            int s = Math.min(boxSize, Math.clamp(Math.min(cw, ch), 12, Integer.MAX_VALUE));
            int bx = cx + getPaddingLeft();
            int by = cy + (ch - s) / 2;
            lastBoxX = bx; lastBoxY = by; lastBoxS = s;
        }
        return x >= lastBoxX && x <= lastBoxX + lastBoxS &&
                y >= lastBoxY && y <= lastBoxY + lastBoxS;
    }

    private void drawFill(DrawContext ctx, Layout l, int color) {
        int inset = Math.max(3, l.s / 5);
        int ix = l.bx + inset;
        int iy = l.by + inset;
        int isz = l.s - inset * 2;
        DrawingUtils.drawRoundedRect(ctx, ix, iy, isz, isz, Math.max(2, isz / 6), color);
    }

    private void drawDot(DrawContext ctx, Layout l, int color) {
        int d = Math.max(4, l.s / 3);
        int ix = l.bx + (l.s - d) / 2;
        int iy = l.by + (l.s - d) / 2;
        DrawingUtils.drawRoundedRect(ctx, ix, iy, d, d, d / 2, color);
    }

    private void drawMinus(DrawContext ctx, Layout l, int color) {
        int h = Math.max(2, l.s / 6);
        int ix = l.bx + l.s / 6;
        int iy = l.by + (l.s - h) / 2;
        int w  = l.s - (l.s / 3);
        DrawingUtils.drawRoundedRect(ctx, ix, iy, w, h, h / 2, color);
    }

    private void drawTick(DrawContext ctx, Layout l, int color) {
        int t  = Math.max(2, l.s / 8);
        int x1 = l.bx + (int)(0.22f * l.s);
        int y1 = l.by + (int)(0.55f * l.s);
        int x2 = l.bx + (int)(0.44f * l.s);
        int y2 = l.by + (int)(0.78f * l.s);
        int x3 = l.bx + (int)(0.80f * l.s);
        int y3 = l.by + (int)(0.24f * l.s);
        drawLineDDA(ctx, x1, y1, x2, y2, t, color);
        drawLineDDA(ctx, x2, y2, x3, y3, t, color);
    }

    private void drawCross(DrawContext ctx, Layout l, int color) {
        int t = Math.max(2, l.s / 8);
        int m = (int)(0.22f * l.s);
        int p = (int)(0.78f * l.s);
        drawLineDDA(ctx, l.bx + m, l.by + m, l.bx + p, l.by + p, t, color);
        drawLineDDA(ctx, l.bx + m, l.by + p, l.bx + p, l.by + m, t, color);
    }

    private void drawLineDDA(DrawContext ctx, int x0, int y0, int x1, int y1, int thickness, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int steps = Math.max(dx, dy);
        if (steps == 0) {
            DrawingUtils.drawRoundedRect(ctx, x0 - thickness / 2, y0 - thickness / 2, thickness, thickness, 0, color);
            return;
        }
        float sx = (x1 - x0) / (float) steps;
        float sy = (y1 - y0) / (float) steps;
        float x = x0;
        int y = y0;
        for (int i = 0; i <= steps; i++) {
            int ix = Math.round(x);
            int iy = y;
            DrawingUtils.drawRoundedRect(ctx, ix - thickness / 2, iy - thickness / 2,
                    thickness, thickness, Math.max(0, thickness / 2), color);
            x += sx; y += (int) sy;
        }
    }

    @Override public CheckboxItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public CheckboxItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public CheckboxItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public CheckboxItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public CheckboxItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public CheckboxItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public CheckboxItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public CheckboxItem setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public CheckboxItem setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public CheckboxItem setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public CheckboxItem setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public CheckboxItem setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public CheckboxItem setZIndex(ZIndex.Layer layer, int priority) { super.setZIndex(layer, priority); return this; }
    @Override public CheckboxItem setConstraints(LayoutConstraints c) { super.setConstraints(c); return this; }

    @Override
    public CheckboxItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        if (this.label != null) this.label.setFontRenderer(fontRenderer);
        return this;
    }

    private static final class Layout {
        int bx;
        int by;
        int s;
        int lx;
        int ly;
        int lw;
        int lh;
    }

    @Override
    public String toString() {
        return String.format("CheckboxItem{checked=%b, label='%s', state=%s, enabled=%b, visible=%b, bounds=[%d,%d,%d,%d]}",
                isChecked(),
                label != null ? label.getText() : "null",
                getState(),
                isEnabled(),
                isVisible(),
                getCalculatedX(),
                getCalculatedY(),
                getCalculatedWidth(),
                getCalculatedHeight()
        );
    }
}
