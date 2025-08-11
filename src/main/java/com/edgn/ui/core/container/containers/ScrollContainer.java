package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.core.item.items.ScrollbarItem;
import com.edgn.ui.core.models.scroll.ContainerScrollbarModel;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

@SuppressWarnings("unused")
public class ScrollContainer extends BaseContainer {

    protected boolean scrollEnabled = true;
    protected boolean verticalScroll = true;
    protected boolean horizontalScroll = false;

    protected int scrollX = 0;
    protected int scrollY = 0;
    protected int contentWidth = 0;
    protected int contentHeight = 0;

    protected int scrollStep = 40;

    private ScrollbarItem vbar;
    private ScrollbarItem hbar;
    private boolean showScrollbars = true;

    private int scrollbarThickness = 8;
    private int scrollbarPadding = 2;
    private int reserveRight = 0;
    private int reserveBottom = 0;

    public ScrollContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public ScrollContainer setScrollable(boolean enabled) { this.scrollEnabled = enabled; return this; }
    public ScrollContainer setScrollAxes(boolean vertical, boolean horizontal) { this.verticalScroll = vertical; this.horizontalScroll = horizontal; return this; }
    public ScrollContainer setScrollStep(int step) { this.scrollStep = Math.max(1, step); return this; }
    public ScrollContainer setShowScrollbars(boolean show) { this.showScrollbars = show; return this; }
    public ScrollContainer setScrollbarStyle(int thickness, int padding) { this.scrollbarThickness = Math.max(4, thickness); this.scrollbarPadding = Math.max(0, padding); return this; }

    private int baseViewportWidth() { return Math.max(0, calculatedWidth - getPaddingLeft() - getPaddingRight()); }
    private int baseViewportHeight() { return Math.max(0, calculatedHeight - getPaddingTop() - getPaddingBottom()); }

    public int getViewportX() { return calculatedX + getPaddingLeft(); }
    public int getViewportY() { return calculatedY + getPaddingTop(); }
    public int getViewportWidth() { return Math.max(0, baseViewportWidth() - reserveRight); }
    public int getViewportHeight() { return Math.max(0, baseViewportHeight() - reserveBottom); }

    public int getContentWidth() { return contentWidth; }
    public int getContentHeight() { return contentHeight; }

    public int getScrollX() { return scrollX; }
    public int getScrollY() { return scrollY; }
    public void setScrollX(int v) { scrollX = v; clampScroll(); }
    public void setScrollY(int v) { scrollY = v; clampScroll(); }

    public boolean isVerticalScrollEnabled() { return verticalScroll; }
    public boolean isHorizontalScrollEnabled() { return horizontalScroll; }

    @Override
    protected void updateInteractionBounds() {
        int innerX = calculatedX + getPaddingLeft();
        int innerY = calculatedY + getPaddingTop();
        int innerW = baseViewportWidth();
        int innerH = baseViewportHeight();
        this.interactionBounds = new InteractionBounds(innerX, innerY, innerW, innerH);
    }

    @Override
    protected int getChildInteractionOffsetX(UIElement child) {
        if (child instanceof ScrollbarItem) return 0;
        if (child.ignoresParentScroll()) return 0;
        return -scrollX;
    }

    @Override
    protected int getChildInteractionOffsetY(UIElement child) {
        if (child instanceof ScrollbarItem) return 0;
        if (child.ignoresParentScroll()) return 0;
        return -scrollY;
    }

    protected void layoutChildren() {}

    protected void computeContentSize() {
        List<UIElement> children = getChildren();
        if (children.isEmpty()) { contentWidth = 0; contentHeight = 0; return; }
        int originX = getViewportX();
        int originY = getViewportY();
        int minRX = Integer.MAX_VALUE;
        int minRY = Integer.MAX_VALUE;
        int maxRX = Integer.MIN_VALUE;
        int maxRY = Integer.MIN_VALUE;
        for (UIElement child : children) {
            if (!child.isVisible()) continue;
            if (child instanceof ScrollbarItem) continue;
            child.updateConstraints();
            int cx = child.getCalculatedX();
            int cy = child.getCalculatedY();
            int cw = child.getCalculatedWidth();
            int ch = child.getCalculatedHeight();
            int rx = cx - originX;
            int ry = cy - originY;
            if (rx < minRX) minRX = rx;
            if (ry < minRY) minRY = ry;
            if (rx + cw > maxRX) maxRX = rx + cw;
            if (ry + ch > maxRY) maxRY = ry + ch;
        }
        if (minRX == Integer.MAX_VALUE) { contentWidth = 0; contentHeight = 0; }
        else { contentWidth = Math.max(0, maxRX - Math.min(0, minRX)); contentHeight = Math.max(0, maxRY - Math.min(0, minRY)); }
    }

    protected void clampScroll() {
        int maxX = Math.max(0, contentWidth - getViewportWidth());
        int maxY = Math.max(0, contentHeight - getViewportHeight());
        if (!horizontalScroll) scrollX = 0;
        if (!verticalScroll) scrollY = 0;
        if (scrollX < 0) scrollX = 0;
        if (scrollY < 0) scrollY = 0;
        if (scrollX > maxX) scrollX = maxX;
        if (scrollY > maxY) scrollY = maxY;
    }

    private int gutterV() { return scrollbarThickness + 2 * scrollbarPadding; }
    private int gutterH() { return scrollbarThickness + 2 * scrollbarPadding; }

    private boolean updateReservesOnce() {
        int oldRight = reserveRight;
        int oldBottom = reserveBottom;

        int bw = baseViewportWidth();
        int bh = baseViewportHeight();

        boolean needV = verticalScroll && contentHeight > bh;
        boolean needH = horizontalScroll && contentWidth > bw;

        int newRight = needV ? gutterV() : 0;
        int newBottom = needH ? gutterH() : 0;

        boolean needV2 = verticalScroll && contentHeight > (bh - newBottom);
        boolean needH2 = horizontalScroll && contentWidth > (bw - newRight);

        newRight = needV2 ? gutterV() : 0;
        newBottom = needH2 ? gutterH() : 0;

        reserveRight = newRight;
        reserveBottom = newBottom;

        return oldRight != reserveRight || oldBottom != reserveBottom;
    }

    private void ensureScrollbars() {
        if (!showScrollbars) {
            if (vbar != null) { removeChild(vbar); vbar = null; }
            if (hbar != null) { removeChild(hbar); hbar = null; }
            return;
        }

        boolean needV = verticalScroll && contentHeight > getViewportHeight();
        boolean needH = horizontalScroll && contentWidth > getViewportWidth();

        int baseX = calculatedX + getPaddingLeft();
        int baseY = calculatedY + getPaddingTop();
        int baseW = baseViewportWidth();
        int baseH = baseViewportHeight();

        ContainerScrollbarModel model = new ContainerScrollbarModel(this);

        if (needV) {
            if (vbar == null) {
                vbar = new ScrollbarItem(styleSystem, 0, 0, 1, 1, model, ScrollbarItem.Orientation.VERTICAL)
                        .setThickness(scrollbarThickness).setPadding(scrollbarPadding)
                        .setZIndex(ZIndex.Layer.OVERLAY);
                addChild(vbar);
            }
            int gx = baseX + baseW - gutterV();
            int gw = gutterV();
            int gh = baseH - reserveBottom;
            vbar.setX(gx);
            vbar.setY(baseY);
            vbar.setWidth(gw);
            vbar.setHeight(Math.max(0, gh));
        } else if (vbar != null) {
            removeChild(vbar);
            vbar = null;
        }

        if (needH) {
            if (hbar == null) {
                hbar = new ScrollbarItem(styleSystem, 0, 0, 1, 1, model, ScrollbarItem.Orientation.HORIZONTAL)
                        .setThickness(scrollbarThickness).setPadding(scrollbarPadding)
                        .setZIndex(ZIndex.Layer.OVERLAY);
                addChild(hbar);
            }
            int gy = baseY + baseH - gutterH();
            int gw = baseW - reserveRight;
            int gh = gutterH();
            hbar.setX(baseX);
            hbar.setY(gy);
            hbar.setWidth(Math.max(0, gw));
            hbar.setHeight(gh);
        } else if (hbar != null) {
            removeChild(hbar);
            hbar = null;
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) {
            markAsNotRendered();
            List<UIElement> cs = getChildren();
            for (UIElement c : cs) c.markAsNotRendered();
            return;
        }
        markAsRendered();
        updateConstraints();

        layoutChildren();
        computeContentSize();
        clampScroll();

        boolean changed = updateReservesOnce();
        if (changed) {
            layoutChildren();
            computeContentSize();
            clampScroll();
        }

        ensureScrollbars();
        updateInteractionBounds();

        int vpMinX = getViewportX();
        int vpMinY = getViewportY();
        int vpMaxX = vpMinX + getViewportWidth();
        int vpMaxY = vpMinY + getViewportHeight();

        int inMinX = calculatedX + getPaddingLeft();
        int inMinY = calculatedY + getPaddingTop();
        int inMaxX = inMinX + baseViewportWidth();
        int inMaxY = inMinY + baseViewportHeight();

        context.enableScissor(vpMinX, vpMinY, vpMaxX, vpMaxY);
        try {
            for (UIElement child : getChildren()) {
                if (child == null || !child.isVisible()) continue;
                if (child instanceof ScrollbarItem) continue;
                context.getMatrices().push();
                context.getMatrices().translate((float) -scrollX, (float) -scrollY, 0.0f);
                child.renderElement(context);
                context.getMatrices().pop();
            }
        } finally {
            context.disableScissor();
        }

        context.enableScissor(inMinX, inMinY, inMaxX, inMaxY);
        try {
            for (UIElement child : getChildren()) {
                if (child == null || !child.isVisible()) continue;
                if (!(child instanceof ScrollbarItem)) continue;
                child.renderElement(context);
            }
        } finally {
            context.disableScissor();
        }
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (!scrollEnabled) return false;
        if (!isInInteractionZone(mouseX, mouseY)) return false;
        boolean used = false;
        if (verticalScroll) { scrollY -= (int) Math.round(scrollDelta * scrollStep); used = true; }
        if (horizontalScroll) { scrollX -= (int) Math.round(scrollDelta * scrollStep); used = true; }
        clampScroll();
        return used;
    }
}
