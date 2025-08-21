package com.edgn.ui.core.item.items;

import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.models.values.BooleanModel;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import com.edgn.ui.core.renderer.FontRenderer;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
public class SwitchItem extends BaseItem {
    private final BooleanModel model;

    private int trackW = 34;
    private int trackH = 18;
    private int trackRadius = 9;
    private int thumbSize = 14;
    private int thumbPadding = 2;

    private int onTrack  = 0xFF4F8EF7;
    private int offTrack = 0xFF9AA0A6;
    private int thumbColor = 0xFFFFFFFF;
    private int hoverOutline = 0x664F8EF7;

    public SwitchItem(UIStyleSystem styleSystem, int x, int y, int w, int h, BooleanModel model) {
        super(styleSystem, x, y, w, h);
        this.model = Objects.requireNonNull(model, "model");
        addClass(StyleKey.ROUNDED_FULL, StyleKey.P_1);
    }

    public BooleanModel getModel() { return model; }
    public boolean isOn() { return model.get(); }
    public SwitchItem setOn(boolean v) { model.set(v); return this; }

    public SwitchItem trackSize(int w, int h) {
        this.trackW = Math.max(thumbSize + thumbPadding * 2 + 2, w);
        this.trackH = Math.max(thumbSize + thumbPadding * 2, h);
        this.trackRadius = Math.clamp(trackRadius, 0, trackH / 2);
        return this;
    }
    public SwitchItem thumbSize(int px) {
        this.thumbSize = Math.max(8, px);
        this.trackH = Math.max(trackH, thumbSize + thumbPadding * 2);
        this.trackW = Math.max(trackW, thumbSize * 2 + thumbPadding * 4);
        return this;
    }
    public SwitchItem thumbPadding(int px) { this.thumbPadding = Math.max(0, px); return this; }

    public SwitchItem onTrack(int c)  { this.onTrack = c; return this; }
    public SwitchItem offTrack(int c) { this.offTrack = c; return this; }
    public SwitchItem thumbColor(int c){ this.thumbColor = c; return this; }
    public SwitchItem hoverOutline(int c){ this.hoverOutline = c; return this; }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int w = Math.min(trackW, cw - getPaddingLeft() - getPaddingRight());
        int h = Math.min(trackH, ch - getPaddingTop() - getPaddingBottom());
        w = Math.max(w, thumbSize * 2 + thumbPadding * 4);
        h = Math.max(h, thumbSize + thumbPadding * 2);

        int x = cx + getPaddingLeft() + (cw - getPaddingLeft() - getPaddingRight() - w) / 2;
        int y = cy + getPaddingTop() + (ch - getPaddingTop() - getPaddingBottom() - h) / 2;

        int trackColor = model.get() ? onTrack : offTrack;

        Shadow shadow = getShadow();
        if (shadow != null) {
            DrawingUtils.drawShadow(context, x, y, w, h, 2, 2, shadow.color);
        }
        DrawingUtils.drawRoundedRect(context, x, y, w, h, Math.max(trackRadius, h / 2), trackColor);

        if (isHovered()) {
            DrawingUtils.drawRoundedRectBorder(context, x - 2, y - 2, w + 4, h + 4, Math.max(trackRadius, h / 2) + 2, hoverOutline, 1);
        }

        int range = w - (thumbPadding * 2) - thumbSize;
        int thumbX = x + thumbPadding + (model.get() ? range : 0);
        int thumbY = y + (h - thumbSize) / 2;

        DrawingUtils.drawRoundedRect(context, thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2, thumbColor);

        if (isFocused() && hasClass(StyleKey.FOCUS_RING)) {
            int focusColor = styleSystem.getColor(StyleKey.PRIMARY_LIGHT);
            DrawingUtils.drawRoundedRectBorder(context, cx - 2, cy - 2, cw + 4, ch + 4, Math.max(trackRadius, h / 2) + 4, focusColor, 2);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        setState(ItemState.PRESSED);
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        boolean inside = canInteract(mouseX, mouseY);
        setState(inside ? ItemState.HOVERED : ItemState.NORMAL);
        if (inside) model.toggle();
        return inside;
    }

    @Override public SwitchItem addClass(StyleKey... keys) { super.addClass(keys); return this; }
    @Override public SwitchItem removeClass(StyleKey key) { super.removeClass(key); return this; }
    @Override public SwitchItem onClick(Runnable handler) { super.onClick(handler); return this; }
    @Override public SwitchItem onMouseEnter(Runnable handler) { super.onMouseEnter(handler); return this; }
    @Override public SwitchItem onMouseLeave(Runnable handler) { super.onMouseLeave(handler); return this; }
    @Override public SwitchItem onFocusGained(Runnable handler) { super.onFocusGained(handler); return this; }
    @Override public SwitchItem onFocusLost(Runnable handler) { super.onFocusLost(handler); return this; }
    @Override public SwitchItem setVisible(boolean visible) { super.setVisible(visible); return this; }
    @Override public SwitchItem setEnabled(boolean enabled) { super.setEnabled(enabled); return this; }
    @Override public SwitchItem setZIndex(int zIndex) { super.setZIndex(zIndex); return this; }
    @Override public SwitchItem setZIndex(ZIndex zIndex) { super.setZIndex(zIndex); return this; }
    @Override public SwitchItem setZIndex(ZIndex.Layer layer) { super.setZIndex(layer); return this; }
    @Override public SwitchItem setZIndex(ZIndex.Layer layer, int priority) { super.setZIndex(layer, priority); return this; }
    @Override public SwitchItem setConstraints(LayoutConstraints c) { super.setConstraints(c); return this; }

    @Override
    public SwitchItem setFontRenderer(FontRenderer fontRenderer) {
        super.setFontRenderer(fontRenderer);
        return this;
    }

    @Override
    public String toString() {
        return String.format("SwitchItem{state=%s, on=%b, enabled=%b, visible=%b, bounds=[%d,%d,%d,%d]}",
                getState(),
                isOn(),
                isEnabled(),
                isVisible(),
                getCalculatedX(),
                getCalculatedY(),
                getCalculatedWidth(),
                getCalculatedHeight()
        );
    }
}
