package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.layout.LayoutEngine;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ScrollContainer extends BaseContainer {

    protected boolean scrollEnabled = true;
    protected boolean verticalScroll = true;
    protected boolean horizontalScroll = false;
    protected boolean dragScrollEnabled = true;
    protected int scrollX = 0;
    protected int scrollY = 0;
    protected int contentWidth = 0;
    protected int contentHeight = 0;
    protected int scrollStep = 40;
    protected int dragThreshold = 4;

    protected boolean pointerDown = false;
    protected boolean dragging = false;
    protected int dragButton = -1;
    protected double lastMouseX = 0.0;
    protected double lastMouseY = 0.0;

    public ScrollContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public ScrollContainer setScrollable(boolean enabled) {
        this.scrollEnabled = enabled;
        return this;
    }

    public ScrollContainer setScrollAxes(boolean vertical, boolean horizontal) {
        this.verticalScroll = vertical;
        this.horizontalScroll = horizontal;
        return this;
    }

    public ScrollContainer setDragScrollEnabled(boolean enabled) {
        this.dragScrollEnabled = enabled;
        return this;
    }

    public ScrollContainer setScrollStep(int step) {
        this.scrollStep = Math.max(1, step);
        return this;
    }

    protected int getViewportX() {
        return getCalculatedX() + getPaddingLeft();
    }

    protected int getViewportY() {
        return getCalculatedY() + getPaddingTop();
    }

    protected int getViewportWidth() {
        return Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
    }

    protected int getViewportHeight() {
        return Math.max(0, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
    }

    protected void computeContentSize() {
        List<UIElement> children = getChildren();
        if (children.isEmpty()) {
            contentWidth = 0;
            contentHeight = 0;
            return;
        }
        int originX = getViewportX();
        int originY = getViewportY();
        int minRX = Integer.MAX_VALUE;
        int minRY = Integer.MAX_VALUE;
        int maxRX = Integer.MIN_VALUE;
        int maxRY = Integer.MIN_VALUE;
        for (UIElement child : children) {
            if (!child.isVisible()) continue;
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
        if (minRX == Integer.MAX_VALUE) {
            contentWidth = 0;
            contentHeight = 0;
        } else {
            contentWidth = Math.max(0, maxRX - Math.min(0, minRX));
            contentHeight = Math.max(0, maxRY - Math.min(0, minRY));
        }
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

    @Override
    protected void layoutChildren() {}

    @Override
    public void render(DrawContext context) {
        if (!visible) {
            markAsNotRendered();
            List<UIElement> children = getChildren();
            for (UIElement child : children) child.markAsNotRendered();
            return;
        }

        markAsRendered();
        updateConstraints();
        renderBackground(context);

        InteractionBounds bounds = getInteractionBounds();
        if (bounds.isValid()) context.enableScissor(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
        try {
            layoutChildren();
            computeContentSize();
            clampScroll();

            List<UIElement> sortedChildren = LayoutEngine.sortByRenderOrder(new ArrayList<>(getChildren()));
            for (UIElement child : sortedChildren) {
                if (child == null || !child.isVisible()) continue;
                LayoutEngine.applyElementStyles(child);
                int ox = child.getX();
                int oy = child.getY();
                child.setX(ox - scrollX);
                child.setY(oy - scrollY);
                child.renderElement(context);
                child.setX(ox);
                child.setY(oy);
            }
        } finally {
            if (bounds.isValid()) context.disableScissor();
        }
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        if (!scrollEnabled) return false;
        if (!isInInteractionZone(mouseX, mouseY)) return false;
        boolean used = false;
        if (verticalScroll) {
            scrollY -= (int) Math.round(scrollDelta * scrollStep);
            used = true;
        }
        if (horizontalScroll) {
            scrollX -= (int) Math.round(scrollDelta * scrollStep);
            used = true;
        }
        clampScroll();
        return used;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!scrollEnabled || !dragScrollEnabled) return false;
        if (!isInInteractionZone(mouseX, mouseY)) return false;
        pointerDown = true;
        dragging = false;
        dragButton = button;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        return false;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!scrollEnabled || !dragScrollEnabled) return false;
        if (!isInInteractionZone(mouseX, mouseY)) return false;
        if (!pointerDown || button != dragButton) return false;

        double dx = mouseX - lastMouseX;
        double dy = mouseY - lastMouseY;

        if (!dragging) {
            if (Math.abs(dx) >= dragThreshold || Math.abs(dy) >= dragThreshold) dragging = true;
            else return false;
        }

        if (horizontalScroll) scrollX -= (int) Math.round(dx);
        if (verticalScroll) scrollY -= (int) Math.round(dy);
        clampScroll();

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        boolean wasDragging = dragging;
        pointerDown = false;
        dragging = false;
        dragButton = -1;
        return wasDragging;
    }
}
