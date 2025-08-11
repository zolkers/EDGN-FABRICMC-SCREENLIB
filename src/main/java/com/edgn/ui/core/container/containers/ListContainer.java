package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.item.items.ScrollbarItem;
import com.edgn.ui.css.UIStyleSystem;

import java.util.List;

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
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        int contentX = getViewportX();
        int contentY = getViewportY();
        int vw = getViewportWidth();
        int vh = getViewportHeight();
        int gap = getGap();

        int xCursor = contentX;
        int yCursor = contentY;

        if (orientation == Orientation.VERTICAL) {
            int prevMB = 0;
            for (UIElement child : kids) {
                if (!child.isVisible()) continue;
                if (child instanceof ScrollbarItem) continue;

                int mt = child.getMarginTop();
                int mb = child.getMarginBottom();
                int ml = child.getMarginLeft();
                int mr = child.getMarginRight();

                yCursor += (yCursor == contentY ? 0 : gap) + prevMB + mt;

                int cx = contentX + ml;
                int cw = Math.max(0, vw - ml - mr);

                child.setX(cx);
                child.setY(yCursor);
                child.setWidth(cw);
                child.updateConstraints();
                child.getInteractionBounds();

                yCursor += child.getCalculatedHeight();
                prevMB = mb;
            }
        } else {
            int prevMR = 0;
            for (UIElement child : kids) {
                if (!child.isVisible()) continue;
                if (child instanceof ScrollbarItem) continue;

                int mt = child.getMarginTop();
                int mb = child.getMarginBottom();
                int ml = child.getMarginLeft();
                int mr = child.getMarginRight();

                xCursor += (xCursor == contentX ? 0 : gap) + prevMR + ml;

                int cy = contentY + mt;
                int ch = Math.max(0, vh - mt - mb);

                child.setX(xCursor);
                child.setY(cy);
                child.setHeight(ch);
                child.updateConstraints();
                child.getInteractionBounds();

                xCursor += child.getCalculatedWidth();
                prevMR = mr;
            }
        }
    }

}
