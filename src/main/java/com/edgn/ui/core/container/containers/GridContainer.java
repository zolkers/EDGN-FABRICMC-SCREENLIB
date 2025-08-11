package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.item.items.ScrollbarItem;
import com.edgn.ui.css.UIStyleSystem;

import java.util.List;

@SuppressWarnings("unused")
public class GridContainer extends ScrollContainer {

    private int columns = 3;

    public GridContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
    }

    public GridContainer setColumns(int columns) {
        this.columns = Math.max(1, columns);
        return this;
    }

    @Override
    protected void layoutChildren() {
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        int contentX = getViewportX();
        int contentY = getViewportY();
        int vw = getViewportWidth();
        int gap = getGap();

        int colCount = Math.max(1, this.columns);
        int totalGap = gap * (colCount - 1);
        int cellW = Math.max(0, (vw - totalGap) / colCount);

        int xCursor = contentX;
        int yCursor = contentY;
        int rowMaxH = 0;
        int colIndex = 0;

        for (UIElement child : kids) {
            if (!child.isVisible()) continue;
            if (child instanceof ScrollbarItem) continue;

            child.setX(xCursor);
            child.setY(yCursor);
            child.setWidth(cellW);
            child.updateConstraints();
            child.getInteractionBounds();

            int ch = child.getCalculatedHeight();
            if (ch > rowMaxH) rowMaxH = ch;

            colIndex++;
            if (colIndex < colCount) {
                xCursor += cellW + gap;
            } else {
                xCursor = contentX;
                yCursor += rowMaxH + gap;
                rowMaxH = 0;
                colIndex = 0;
            }
        }
    }
}
