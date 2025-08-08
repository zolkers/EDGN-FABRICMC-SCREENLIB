package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FlexContainer extends BaseContainer {

    public FlexContainer(UIStyleSystem styleSystem, int x, int y, int w, int h) {
        super(styleSystem, x, y, w, h);
        addClass(StyleKey.FLEX_ROW, StyleKey.FLEX_WRAP, StyleKey.JUSTIFY_START, StyleKey.ITEMS_START);
    }

    @Override
    public void render(DrawContext context) {
        layoutChildren();
        for (UIElement child : getChildren()) {
            if (child.isVisible()) child.renderElement(context);
        }
    }

    private boolean isRow() {
        return hasClass(StyleKey.FLEX_ROW) || hasClass(StyleKey.FLEX_ROW_REVERSE)
                || (!hasClass(StyleKey.FLEX_COLUMN) && !hasClass(StyleKey.FLEX_COLUMN_REVERSE));
    }

    private boolean isReverse() {
        return hasClass(isRow() ? StyleKey.FLEX_ROW_REVERSE : StyleKey.FLEX_COLUMN_REVERSE);
    }

    private boolean wrapEnabled() {
        return hasClass(StyleKey.FLEX_WRAP) || hasClass(StyleKey.FLEX_WRAP_REVERSE);
    }

    private boolean wrapReverse() {
        return hasClass(StyleKey.FLEX_WRAP_REVERSE);
    }

    protected void layoutChildren() {
        updateConstraints();
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        final int contentX = getCalculatedX() + getPaddingLeft();
        final int contentY = getCalculatedY() + getPaddingTop();
        final int contentW = Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
        final int contentH = Math.max(0, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        final int gap = getGap();

        final boolean row = isRow();
        final int maxMain = row ? contentW : contentH;
        final int maxCross = row ? contentH : contentW;

        List<List<UIElement>> lines = new ArrayList<>();
        List<Integer> lineCrossSizes = new ArrayList<>();
        List<UIElement> current = new ArrayList<>();
        int lineMainUsed = 0;
        int lineCross = 0;

        for (UIElement child : kids) {
            if (!child.isVisible()) continue;
            child.updateConstraints();

            int basis = resolveFlexBasis(child, maxMain, row);
            int mMainStart = row ? child.getMarginLeft() : child.getMarginTop();
            int mMainEnd   = row ? child.getMarginRight() : child.getMarginBottom();
            int mCrossStart= row ? child.getMarginTop() : child.getMarginLeft();
            int mCrossEnd  = row ? child.getMarginBottom() : child.getMarginRight();

            int itemMain = basis + mMainStart + mMainEnd;
            int itemCrossMin = (row ? child.getHeight() : child.getWidth()) + mCrossStart + mCrossEnd;

            int prospective = current.isEmpty() ? itemMain : lineMainUsed + gap + itemMain;
            if (wrapEnabled() && prospective > maxMain && !current.isEmpty()) {
                lines.add(current);
                lineCrossSizes.add(lineCross);
                current = new ArrayList<>();
                lineMainUsed = 0;
                lineCross = 0;
            }

            current.add(child);
            lineMainUsed = current.size() == 1 ? itemMain : lineMainUsed + gap + itemMain;
            lineCross = Math.max(lineCross, itemCrossMin);
        }
        if (!current.isEmpty()) {
            lines.add(current);
            lineCrossSizes.add(lineCross);
        }

        int crossCursor = wrapReverse() ? (row ? contentY + maxCross : contentX + maxCross)
                : (row ? contentY : contentX);

        for (int li = 0; li < lines.size(); li++) {
            List<UIElement> line = lines.get(li);

            int usedMain = 0;
            int totalGrow = 0;
            int totalShrink = 0;

            int[] basisPx = new int[line.size()];
            int[] baseWithMargins = new int[line.size()];
            int[] mMainStartArr = new int[line.size()];
            int[] mCrossStartArr = new int[line.size()];
            int[] mCrossEndArr = new int[line.size()];

            for (int i = 0; i < line.size(); i++) {
                UIElement child = line.get(i);
                int basis = resolveFlexBasis(child, maxMain, row);

                int mMainStart = row ? child.getMarginLeft() : child.getMarginTop();
                int mMainEnd   = row ? child.getMarginRight() : child.getMarginBottom();
                int mCrossStart= row ? child.getMarginTop() : child.getMarginLeft();
                int mCrossEnd  = row ? child.getMarginBottom() : child.getMarginRight();

                mMainStartArr[i] = mMainStart;
                mCrossStartArr[i] = mCrossStart;
                mCrossEndArr[i] = mCrossEnd;

                int itemMain = basis + mMainStart + mMainEnd;
                basisPx[i] = itemMain - mMainStart - mMainEnd;
                baseWithMargins[i] = itemMain;

                usedMain += (i == 0 ? itemMain : gap + itemMain);
                totalGrow += Math.max(0, child.getFlexGrow());
                totalShrink += Math.max(0, child.getComputedStyles().flexShrink);
            }

            int remaining = maxMain - usedMain;

            int[] withGrowShrink = new int[line.size()];
            for (int i = 0; i < line.size(); i++) {
                UIElement child = line.get(i);
                int grow = Math.max(0, child.getFlexGrow());
                int shrink = Math.max(0, child.getComputedStyles().flexShrink);

                int delta = 0;
                if (remaining > 0 && totalGrow > 0) {
                    delta = (int) Math.floor((remaining * (double) grow) / totalGrow);
                } else if (remaining < 0 && totalShrink > 0) {
                    delta = (int) Math.floor((remaining * (double) shrink) / totalShrink);
                }
                withGrowShrink[i] = Math.max(0, baseWithMargins[i] + delta);
            }

            int usedMainAfter = 0;
            for (int i = 0; i < withGrowShrink.length; i++) {
                usedMainAfter += (i == 0 ? withGrowShrink[i] : gap + withGrowShrink[i]);
            }
            int freeSpace = Math.max(0, maxMain - usedMainAfter);

            int leading = 0;
            int between = gap;
            if (hasClass(StyleKey.JUSTIFY_CENTER)) {
                leading = freeSpace / 2;
            } else if (hasClass(StyleKey.JUSTIFY_END)) {
                leading = freeSpace;
            } else if (hasClass(StyleKey.JUSTIFY_BETWEEN) && line.size() > 1) {
                between = gap + (freeSpace / (line.size() - 1));
            } else if (hasClass(StyleKey.JUSTIFY_AROUND)) {
                between = gap + (freeSpace / line.size());
                leading = between / 2;
            } else if (hasClass(StyleKey.JUSTIFY_EVENLY)) {
                between = gap + (freeSpace / (line.size() + 1));
                leading = between;
            }

            int lineCrossSize = lineCrossSizes.get(li);
            int lineCrossStart = wrapReverse() ? (crossCursor - lineCrossSize) : crossCursor;

            int mainCursor = (row ? contentX : contentY) + (isReverse() ? (maxMain - leading) : leading);
            int sign = isReverse() ? -1 : 1;

            for (int i = 0; i < line.size(); i++) {
                UIElement child = line.get(i);

                int deltaFromBasis = withGrowShrink[i] - baseWithMargins[i];
                int childMain = Math.max(0, basisPx[i] + deltaFromBasis);

                int mCrossStart = mCrossStartArr[i];
                int mCrossEnd   = mCrossEndArr[i];

                int childCross = hasClass(StyleKey.ITEMS_STRETCH)
                        ? Math.max(0, lineCrossSize - mCrossStart - mCrossEnd)
                        : (row ? child.getHeight() : child.getWidth());

                int cx, cy, cw, ch;

                if (row) {
                    int crossPos;
                    if (hasClass(StyleKey.ITEMS_CENTER)) {
                        crossPos = lineCrossStart + (lineCrossSize - childCross) / 2 + mCrossStart;
                    } else if (hasClass(StyleKey.ITEMS_END)) {
                        crossPos = lineCrossStart + lineCrossSize - childCross - mCrossEnd;
                    } else {
                        crossPos = lineCrossStart + mCrossStart;
                    }

                    int itemMainTotal = withGrowShrink[i];

                    cx = isReverse()
                            ? (mainCursor - itemMainTotal + child.getMarginLeft())
                            : (mainCursor + child.getMarginLeft());
                    cy = crossPos;
                    cw = childMain;
                    ch = childCross;

                    mainCursor += sign * (itemMainTotal + (i + 1 < line.size() ? between : 0));
                } else {
                    int crossPos;
                    if (hasClass(StyleKey.ITEMS_CENTER)) {
                        crossPos = lineCrossStart + (lineCrossSize - childCross) / 2 + mCrossStart;
                    } else if (hasClass(StyleKey.ITEMS_END)) {
                        crossPos = lineCrossStart + lineCrossSize - childCross - mCrossEnd;
                    } else {
                        crossPos = lineCrossStart + mCrossStart;
                    }

                    int itemMainTotal = withGrowShrink[i];
                    int childY = isReverse()
                            ? (mainCursor - itemMainTotal + child.getMarginTop())
                            : (mainCursor + child.getMarginTop());

                    cx = crossPos;
                    cy = childY;
                    cw = childCross;
                    ch = childMain;

                    mainCursor += sign * (itemMainTotal + (i + 1 < line.size() ? between : 0));
                }

                child.setX(cx);
                child.setY(cy);
                child.setWidth(cw);
                child.setHeight(ch);
                child.updateConstraints();
            }

            crossCursor = wrapReverse() ? (crossCursor - lineCrossSize) : (crossCursor + lineCrossSize);
        }
    }

    private int resolveFlexBasis(UIElement child, int maxMain, boolean row) {
        int basis = child.getComputedStyles().flexBasis;
        if (basis > 0 && basis <= 100) {
            return Math.max(0, (int) Math.floor((basis / 100.0) * maxMain));
        }
        if (basis <= 0) {
            return row ? Math.max(0, child.getWidth()) : Math.max(0, child.getHeight());
        }
        return basis;
    }

    @Override
    public FlexContainer addChild(UIElement element) {
        return super.addChild(element);
    }

    @Override
    public FlexContainer addClass(StyleKey... keys) {
        return super.addClass(keys);
    }

    @Override
    public FlexContainer removeClass(StyleKey key) {
        return super.removeClass(key);
    }

    @Override
    public FlexContainer removeChild(UIElement element) {
        return super.removeChild(element);
    }
}
