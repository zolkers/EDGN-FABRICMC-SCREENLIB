package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.item.items.ScrollbarItem;
import com.edgn.ui.css.UIStyleSystem;

@SuppressWarnings("unused")
public class ListContainer extends ScrollContainer {

    public enum Orientation { VERTICAL, HORIZONTAL }

    private Orientation orientation = Orientation.VERTICAL;

    public ListContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public ListContainer setOrientation(Orientation orientation) {
        this.orientation = orientation != null ? orientation : Orientation.VERTICAL;
        return this;
    }

    @Override
    protected void layoutChildren() {
        var kids = getChildren();
        if (kids.isEmpty()) return;

        int contentX = getViewportX();
        int contentY = getViewportY();
        int vw = getViewportWidth();
        int vh = getViewportHeight();
        int gap = getGap();

        if (orientation == Orientation.VERTICAL) {
            layoutVertical(kids, contentX, contentY, vw, gap);
        } else {
            layoutHorizontal(kids, contentX, contentY, vh, gap);
        }
    }


    private boolean isNotLayoutCandidate(UIElement c) {
        return c == null || !c.isVisible() || c instanceof ScrollbarItem;
    }

    private void layoutVertical(java.util.List<UIElement> kids, int contentX, int contentY, int vw, int gap) {
        int yCursor = contentY;
        int prevMB = 0;

        for (UIElement child : kids) {
            if (isNotLayoutCandidate(child)) continue;

            int mt = child.getMarginTop();
            int mb = child.getMarginBottom();
            int ml = child.getMarginLeft();
            int mr = child.getMarginRight();

            yCursor += (yCursor == contentY ? 0 : gap) + prevMB + mt;

            int cx = contentX + ml;
            int cw = Math.clamp((long) vw - ml - mr, 0, Integer.MAX_VALUE);

            placeAndMeasureVertical(child, cx, yCursor, cw);

            yCursor += child.getCalculatedHeight();
            prevMB = mb;
        }
    }

    private void layoutHorizontal(java.util.List<UIElement> kids, int contentX, int contentY, int vh, int gap) {
        int xCursor = contentX;
        int prevMR = 0;

        for (UIElement child : kids) {
            if (isNotLayoutCandidate(child)) continue;

            int mt = child.getMarginTop();
            int mb = child.getMarginBottom();
            int ml = child.getMarginLeft();
            int mr = child.getMarginRight();

            xCursor += (xCursor == contentX ? 0 : gap) + prevMR + ml;

            int cy = contentY + mt;
            int ch = Math.clamp((long) vh - mt - mb, 0, Integer.MAX_VALUE);

            placeAndMeasureHorizontal(child, xCursor, cy, ch);

            xCursor += child.getCalculatedWidth();
            prevMR = mr;
        }
    }

    private void placeAndMeasureVertical(UIElement child, int x, int y, int width) {
        child.setX(x);
        child.setY(y);
        child.setWidth(width);
        child.updateConstraints();
        child.getInteractionBounds();
    }

    private void placeAndMeasureHorizontal(UIElement child, int x, int y, int height) {
        child.setX(x);
        child.setY(y);
        child.setHeight(height);
        child.updateConstraints();
        child.getInteractionBounds();
    }

    @Override
    public String toString() {
        return String.format("ListContainer{orientation=%s, children=%d, visibleChildren=%d, viewport=[%d,%d,%d,%d], gap=%d}",
                orientation,
                getChildren().size(),
                getChildren().stream().filter(UIElement::isVisible).count(),
                getViewportX(),
                getViewportY(),
                getViewportWidth(),
                getViewportHeight(),
                getGap()
        );
    }
}
