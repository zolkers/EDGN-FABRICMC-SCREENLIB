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
        int gap = getGap();

        int xCursor = contentX;
        int yCursor = contentY;

        if (orientation == Orientation.VERTICAL) {
            int vw = getViewportWidth();
            for (UIElement child : kids) {
                if (!child.isVisible()) continue;
                if (child instanceof ScrollbarItem) continue;
                child.setX(xCursor);
                child.setY(yCursor);
                child.setWidth(vw);
                child.updateConstraints();
                child.getInteractionBounds();
                yCursor += child.getCalculatedHeight() + gap;
            }
        } else {
            int vh = getViewportHeight();
            for (UIElement child : kids) {
                if (!child.isVisible()) continue;
                if (child instanceof ScrollbarItem) continue;
                child.setX(xCursor);
                child.setY(yCursor);
                child.setHeight(vh);
                child.updateConstraints();
                child.getInteractionBounds();
                xCursor += child.getCalculatedWidth() + gap;
            }
        }
    }
}
