package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FlexContainer extends BaseContainer {

    private record Line(List<UIElement> children, int crossSize) {}
    private record ItemBox(UIElement node, int basis, int withMargins, int mStart, int mEnd, int mCrossStart, int mCrossEnd) {}
    private record Justify(int leading, int between) {}

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

    @Override
    protected void layoutChildren() {
        updateConstraints();
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        int contentX = getCalculatedX() + getPaddingLeft();
        int contentY = getCalculatedY() + getPaddingTop();
        int contentW = Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
        int contentH = Math.max(0, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        int gap = getGap();

        boolean row = isRow();
        int maxMain = row ? contentW : contentH;
        int maxCross = row ? contentH : contentW;

        List<Line> lines = measureAndWrap(kids, maxMain, row, gap);
        List<Integer> fittedCross = fitCrossSizes(lines, maxCross, gap);

        int crossCursor = wrapReverse() ? (row ? contentY + maxCross : contentX + maxCross) : (row ? contentY : contentX);

        for (int li = 0; li < lines.size(); li++) {
            Line line = lines.get(li);
            int lineCrossSize = fittedCross.get(li);
            List<ItemBox> metrics = collectMetrics(line.children(), maxMain, row);
            int[] itemTotals = distributeMainSpace(metrics, maxMain, gap);
            Justify justify = computeJustify(itemTotals, maxMain, gap);
            int mainStart = row ? contentX : contentY;
            positionLineWithCrossSize(line, metrics, itemTotals, justify, mainStart, crossCursor, row, lineCrossSize);
            crossCursor = advanceCrossCursor(crossCursor, lineCrossSize);
        }
    }

    private List<Line> measureAndWrap(List<UIElement> kids, int maxMain, boolean row, int gap) {
        List<Line> lines = new ArrayList<>();
        List<UIElement> current = new ArrayList<>();
        int lineMainUsed = 0;
        int lineCross = 0;

        for (UIElement c : kids) {
            if (!c.isVisible()) continue;
            c.updateConstraints();

            int basis = resolveFlexBasis(c, maxMain, row);
            int mStart = row ? c.getMarginLeft() : c.getMarginTop();
            int mEnd = row ? c.getMarginRight() : c.getMarginBottom();
            int mCrsS = row ? c.getMarginTop() : c.getMarginLeft();
            int mCrsE = row ? c.getMarginBottom() : c.getMarginRight();

            int withMargins = basis + mStart + mEnd;
            int crossMin = (row ? c.getHeight() : c.getWidth()) + mCrsS + mCrsE;

            int prospective = current.isEmpty() ? withMargins : lineMainUsed + gap + withMargins;
            if (wrapEnabled() && prospective > maxMain && !current.isEmpty()) {
                lines.add(new Line(current, lineCross));
                current = new ArrayList<>();
                lineMainUsed = 0;
                lineCross = 0;
            }

            current.add(c);
            lineMainUsed = current.size() == 1 ? withMargins : lineMainUsed + gap + withMargins;
            lineCross = Math.max(lineCross, crossMin);
        }
        if (!current.isEmpty()) lines.add(new Line(current, lineCross));
        return lines;
    }

    private List<ItemBox> collectMetrics(List<UIElement> children, int maxMain, boolean row) {
        List<ItemBox> boxes = new ArrayList<ItemBox>(children.size());
        for (UIElement child : children) {
            int basis = resolveFlexBasis(child, maxMain, row);
            int mls = row ? child.getMarginLeft() : child.getMarginTop();
            int mle = row ? child.getMarginRight() : child.getMarginBottom();
            int mcs = row ? child.getMarginTop() : child.getMarginLeft();
            int mce = row ? child.getMarginBottom() : child.getMarginRight();
            boxes.add(new ItemBox(child, basis, basis + mls + mle, mls, mle, mcs, mce));
        }
        return boxes;
    }

    private int[] distributeMainSpace(List<ItemBox> metrics, int maxMain, int gap) {
        int used = 0;
        long totalGrow = 0L;
        double totalShrinkWeighted = 0.0;

        for (int i = 0; i < metrics.size(); i++) {
            used += (i == 0 ? metrics.get(i).withMargins() : gap + metrics.get(i).withMargins());
            UIElement n = metrics.get(i).node();
            totalGrow += Math.max(0, n.getFlexGrow());
            totalShrinkWeighted += Math.max(0, n.getComputedStyles().flexShrink) * (double) metrics.get(i).basis();
        }

        int remaining = maxMain - used;
        int[] totals = new int[metrics.size()];

        if (remaining > 0 && totalGrow > 0) {
            long grown = 0;
            for (int i = 0; i < metrics.size(); i++) {
                UIElement n = metrics.get(i).node();
                int grow = Math.max(0, n.getFlexGrow());
                int delta = (int) Math.floor(remaining * (double) grow / (double) totalGrow);
                totals[i] = metrics.get(i).withMargins() + delta;
                grown += delta;
            }
        } else if (remaining < 0 && totalShrinkWeighted > 0.0) {
            int shrunk = 0;
            for (int i = 0; i < metrics.size(); i++) {
                ItemBox m = metrics.get(i);
                int shrink = Math.max(0, m.node().getComputedStyles().flexShrink);
                int delta = (int) Math.floor(remaining * (shrink * m.basis()) / totalShrinkWeighted);
                int minAllowed = m.mStart() + m.mEnd();
                totals[i] = Math.max(minAllowed, m.withMargins() + delta);
                shrunk += delta;
            }
        } else {
            for (int i = 0; i < metrics.size(); i++) totals[i] = metrics.get(i).withMargins();
        }

        int correction = maxMain - sumWithGaps(totals, gap);
        for (int i = 0; correction != 0 && i < totals.length; i++) {
            int step = correction > 0 ? 1 : -1;
            int minAllowed = metrics.get(i).mStart() + metrics.get(i).mEnd();
            totals[i] = Math.max(minAllowed, totals[i] + step);
            correction -= step;
        }
        return totals;
    }

    private Justify computeJustify(int[] itemTotals, int maxMain, int gap) {
        int used = sumWithGaps(itemTotals, gap);
        int freeSpace = Math.max(0, maxMain - used);

        int leading = 0;
        int between = gap;
        int count = itemTotals.length;

        if (hasClass(StyleKey.JUSTIFY_CENTER)) {
            leading = freeSpace / 2;
        } else if (hasClass(StyleKey.JUSTIFY_END)) {
            leading = freeSpace;
        } else if (hasClass(StyleKey.JUSTIFY_BETWEEN) && count > 1) {
            between = gap + (freeSpace / (count - 1));
        } else if (hasClass(StyleKey.JUSTIFY_AROUND)) {
            between = gap + (freeSpace / count);
            leading = between / 2;
        } else if (hasClass(StyleKey.JUSTIFY_EVENLY)) {
            between = gap + (freeSpace / (count + 1));
            leading = between;
        }
        return new Justify(leading, between);
    }

    private void positionLineWithCrossSize(Line line, List<ItemBox> metrics, int[] totals, Justify justify, int mainStart, int crossCursor, boolean row, int lineCrossSize) {
        int sign = isReverse() ? -1 : 1;
        int mainCursor = isReverse() ? mainStart + getMainMax() - justify.leading() : mainStart + justify.leading();
        int lineCrossStart = wrapReverse() ? (crossCursor - lineCrossSize) : crossCursor;

        for (int i = 0; i < line.children().size(); i++) {
            ItemBox box = metrics.get(i);
            int total = totals[i];
            int childMain = Math.max(0, total - box.mStart() - box.mEnd());
            int naturalCross = isRow() ? box.node().getHeight() : box.node().getWidth();
            int stretched = hasClass(StyleKey.ITEMS_STRETCH) ? Math.max(0, lineCrossSize - box.mCrossStart() - box.mCrossEnd()) : naturalCross;
            int childCross = Math.max(0, stretched);
            int crossPos = computeCrossPos(lineCrossStart, lineCrossSize, childCross, box, isRow());
            int marginForStart = startMarginForDirection(box.node(), isRow(), isReverse());
            int itemStartPos = isReverse() ? (mainCursor - total + marginForStart) : (mainCursor + marginForStart);

            if (row) {
                box.node().setX(itemStartPos);
                box.node().setY(crossPos);
                box.node().setWidth(childMain);
                box.node().setHeight(childCross);
            } else {
                box.node().setX(crossPos);
                box.node().setY(itemStartPos);
                box.node().setWidth(childCross);
                box.node().setHeight(childMain);
            }
            box.node().updateConstraints();

            mainCursor += sign * (total + (i + 1 < line.children().size() ? justify.between() : 0));
        }
    }

    private int computeCrossStart(int contentStart, int maxCross) {
        return wrapReverse() ? contentStart + maxCross : contentStart;
    }

    private int advanceCrossCursor(int crossCursor, int lineCross) {
        return wrapReverse() ? (crossCursor - lineCross) : (crossCursor + lineCross);
    }

    private int computeCrossPos(int lineStart, int lineCrossSize, int childCross, ItemBox box, boolean row) {
        if (hasClass(StyleKey.ITEMS_CENTER)) {
            return lineStart + (lineCrossSize - childCross) / 2 + box.mCrossStart();
        } else if (hasClass(StyleKey.ITEMS_END)) {
            return lineStart + lineCrossSize - childCross - box.mCrossEnd();
        }
        return lineStart + box.mCrossStart();
    }

    private int startMarginForDirection(UIElement n, boolean row, boolean reverse) {
        if (row) return reverse ? n.getMarginRight() : n.getMarginLeft();
        else return reverse ? n.getMarginBottom() : n.getMarginTop();
    }

    private int sumWithGaps(int[] arr, int gap) {
        int s = 0;
        for (int i = 0; i < arr.length; i++) s += (i == 0 ? arr[i] : gap + arr[i]);
        return s;
    }

    private int resolveFlexBasis(UIElement child, int maxMain, boolean row) {
        int basis = child.getComputedStyles().flexBasis;
        if (basis > 0 && basis <= 100) {
            return Math.max(0, (int) Math.floor((basis / 100.0) * maxMain));
        }
        if (basis <= 0) {
            int raw = row ? child.getWidth() : child.getHeight();
            return Math.max(0, Math.min(raw, maxMain));
        }
        return Math.min(basis, Math.max(0, maxMain));
    }

    private List<Integer> fitCrossSizes(List<Line> lines, int maxCross, int lineGap) {
        List<Integer> sizes = new ArrayList<Integer>(lines.size());
        int rawTotal = 0;
        for (int i = 0; i < lines.size(); i++) {
            int size = lines.get(i).crossSize();
            sizes.add(size);
            rawTotal += (i == 0 ? size : lineGap + size);
        }
        if (rawTotal <= maxCross) return sizes;

        int deficit = rawTotal - maxCross;
        int sumCross = 0;
        for (Line line : lines) sumCross += line.crossSize();

        List<Integer> fitted = new ArrayList<>(lines.size());
        int absorbed = 0;
        for (Line line : lines) {
            int natural = line.crossSize();
            int cut = (int) Math.floor(deficit * (natural / (double) sumCross));
            int target = Math.max(0, natural - cut);
            fitted.add(target);
            absorbed += cut;
        }

        int fittedTotal = 0;
        for (int i = 0; i < fitted.size(); i++) {
            fittedTotal += (i == 0 ? fitted.get(i) : lineGap + fitted.get(i));
        }
        int correction = maxCross - fittedTotal;
        for (int i = 0; correction != 0 && i < fitted.size(); i++) {
            int step = correction > 0 ? 1 : -1;
            fitted.set(i, Math.max(0, fitted.get(i) + step));
            correction -= step;
        }
        return fitted;
    }

    private int getMainMax() {
        int contentW = Math.max(0, getCalculatedWidth() - getPaddingLeft() - getPaddingRight());
        int contentH = Math.max(0, getCalculatedHeight() - getPaddingTop() - getPaddingBottom());
        return isRow() ? contentW : contentH;
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
