package com.edgn.ui.core.item.items;

import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.models.scroll.ScrollbarModel;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

public class ScrollbarItem extends BaseItem {

    public enum Orientation { VERTICAL, HORIZONTAL }

    private final ScrollbarModel model;
    private final Orientation orientation;

    private int thickness = 8;
    private int padding = 2;
    private int trackColor = 0x40000000;
    private int thumbColor = 0x80FFFFFF;
    private int thumbHoverColor = 0xC0FFFFFF;

    private boolean dragging = false;
    private int dragStartMouse = 0;
    private int dragStartScroll = 0;

    public ScrollbarItem(UIStyleSystem styleSystem, int x, int y, int width, int height, ScrollbarModel model, Orientation orientation) {
        super(styleSystem, x, y, width, height);
        this.model = model;
        this.orientation = orientation;
        setIgnoreParentScroll(true);
    }

    public ScrollbarItem setThickness(int px) { this.thickness = Math.max(4, px); return this; }
    public ScrollbarItem setPadding(int px) { this.padding = Math.max(0, px); return this; }
    public ScrollbarItem setColors(int track, int thumb, int thumbHover) { this.trackColor = track; this.thumbColor = thumb; this.thumbHoverColor = thumbHover; return this; }

    @Override
    public void render(DrawContext context) {
        updateConstraints();

        boolean needV = model.isVerticalEnabled() && model.getContentHeight() > model.getViewportHeight();
        boolean needH = model.isHorizontalEnabled() && model.getContentWidth() > model.getViewportWidth();
        boolean need = orientation == Orientation.VERTICAL ? needV : needH;
        if (!need) return;

        int vx = getX();
        int vy = getY();
        int vw = getWidth();
        int vh = getHeight();

        int x, y, w, h;
        if (orientation == Orientation.VERTICAL) {
            x = vx + vw - thickness - padding;
            y = vy + padding;
            w = thickness;
            h = vh - 2 * padding;
        } else {
            x = vx + padding;
            y = vy + vh - thickness - padding;
            w = vw - 2 * padding;
            h = thickness;
        }
        setX(x); setY(y); setWidth(w); setHeight(h);
        updateConstraints();

        int content = orientation == Orientation.VERTICAL ? model.getContentHeight() : model.getContentWidth();
        int view = orientation == Orientation.VERTICAL ? model.getViewportHeight() : model.getViewportWidth();
        int scroll = orientation == Orientation.VERTICAL ? model.getScrollY() : model.getScrollX();
        if (content <= 0 || view <= 0) return;

        int trackX1 = x, trackY1 = y, trackX2 = x + w, trackY2 = y + h;
        context.fill(trackX1, trackY1, trackX2, trackY2, trackColor);

        double frac = Math.min(1.0, (double) view / (double) content);
        int span = orientation == Orientation.VERTICAL ? h : w;
        int thumbLen = Math.max(20, (int) Math.round(frac * span));
        int maxScroll = Math.max(1, content - view);
        double posFrac = Math.max(0.0, Math.min(1.0, (double) scroll / (double) maxScroll));

        int thumbX1, thumbY1, thumbX2, thumbY2;
        if (orientation == Orientation.VERTICAL) {
            int usable = span - thumbLen;
            int ty1 = y + (int) Math.round(posFrac * usable);
            thumbX1 = x; thumbX2 = x + w; thumbY1 = ty1; thumbY2 = ty1 + thumbLen;
        } else {
            int usable = span - thumbLen;
            int tx1 = x + (int) Math.round(posFrac * usable);
            thumbX1 = tx1; thumbX2 = tx1 + thumbLen; thumbY1 = y; thumbY2 = y + h;
        }

        int color = isHovered() ? thumbHoverColor : thumbColor;
        context.fill(thumbX1, thumbY1, thumbX2, thumbY2, color);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;

        int content = orientation == Orientation.VERTICAL ? model.getContentHeight() : model.getContentWidth();
        int view = orientation == Orientation.VERTICAL ? model.getViewportHeight() : model.getViewportWidth();
        if (content <= view) return false;

        int span = orientation == Orientation.VERTICAL ? getCalculatedHeight() : getCalculatedWidth();
        int scroll = orientation == Orientation.VERTICAL ? model.getScrollY() : model.getScrollX();
        int maxScroll = Math.max(1, content - view);

        double frac = Math.min(1.0, (double) view / (double) content);
        int thumbLen = Math.max(20, (int) Math.round(frac * span));
        int usable = Math.max(1, span - thumbLen);
        double posFrac = Math.max(0.0, Math.min(1.0, (double) scroll / (double) maxScroll));
        int thumbPos = (int) Math.round(posFrac * usable);

        int m = (int) (orientation == Orientation.VERTICAL ? mouseY - getCalculatedY() : mouseX - getCalculatedX());

        if (m >= thumbPos && m <= thumbPos + thumbLen) {
            dragging = true;
            dragStartMouse = m;
            dragStartScroll = scroll;
        } else {
            int page = (int) (0.8 * view);
            int newScroll = m < thumbPos ? Math.max(0, scroll - page) : Math.min(maxScroll, scroll + page);
            applyScroll(newScroll);
        }
        return true;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) return false;

        int content = orientation == Orientation.VERTICAL ? model.getContentHeight() : model.getContentWidth();
        int view = orientation == Orientation.VERTICAL ? model.getViewportHeight() : model.getViewportWidth();
        int span = orientation == Orientation.VERTICAL ? getCalculatedHeight() : getCalculatedWidth();
        double frac = Math.min(1.0, (double) view / (double) content);
        int thumbLen = Math.max(20, (int) Math.round(frac * span));
        int maxScroll = Math.max(1, content - view);
        int usable = Math.max(1, span - thumbLen);

        int currentMouse = (int) (orientation == Orientation.VERTICAL ? mouseY - getCalculatedY() : mouseX - getCalculatedX());
        int deltaMouse = currentMouse - dragStartMouse;
        int newScroll = dragStartScroll + (int) Math.round((deltaMouse / (double) usable) * maxScroll);

        applyScroll(newScroll);
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        boolean was = dragging;
        dragging = false;
        return was;
    }

    private void applyScroll(int value) {
        if (orientation == Orientation.VERTICAL) model.setScrollY(value);
        else model.setScrollX(value);
    }
}
