package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.BaseContainer;
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

    private boolean uniformScaleEnabled = true;

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

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        int gap = getGap();

        int contentX = getCalculatedX() + pl;
        int contentY = getCalculatedY() + pt;
        int contentW = Math.max(0, getCalculatedWidth() - pl - pr);
        int contentH = Math.max(0, getCalculatedHeight() - pt - pb);

        boolean row = isRow();
        int maxMain = row ? contentW : contentH;
        int maxCross = row ? contentH : contentW;

        List<Line> lines = measureAndWrap(kids, maxMain, row, gap);

        double k = 1.0;
        if (uniformScaleEnabled) {
            k = computeUniformScaleFactor(lines, maxMain, maxCross, gap);

            pl = scaleRound(k, pl);
            pr = scaleRound(k, pr);
            pt = scaleRound(k, pt);
            pb = scaleRound(k, pb);
            gap = scaleRound(k, gap);

            contentX = getCalculatedX() + pl;
            contentY = getCalculatedY() + pt;
            contentW = Math.max(0, getCalculatedWidth() - pl - pr);
            contentH = Math.max(0, getCalculatedHeight() - pt - pb);

            maxMain = row ? contentW : contentH;
            maxCross = row ? contentH : contentW;

            lines = measureAndWrap(kids, maxMain, row, gap);
        }

        List<Line> scaledLines = uniformScaleEnabled ? scaleLinesForUniform(lines, k, row) : lines;
        List<Integer> fittedCross = fitCrossSizes(scaledLines, maxCross, gap);

        int crossCursor = wrapReverse() ? (row ? contentY + maxCross : contentX + maxCross) : (row ? contentY : contentX);

        for (int li = 0; li < scaledLines.size(); li++) {
            Line line = scaledLines.get(li);
            int lineCrossSize = fittedCross.get(li);

            List<ItemBox> metrics = uniformScaleEnabled
                    ? collectMetricsScaled(line.children(), maxMain, row, k)
                    : collectMetrics(line.children(), maxMain, row);

            int[] itemTotals = distributeMainSpace(metrics, maxMain, gap);
            snapFixSum(itemTotals, gap, maxMain);
            Justify justify = computeJustify(itemTotals, maxMain, gap);

            int mainStart = row ? contentX : contentY;

            if (uniformScaleEnabled) {
                positionLineScaled(line, metrics, itemTotals, justify, mainStart, crossCursor, row, lineCrossSize, k, maxMain);
            } else {
                positionLineWithCrossSize(line, metrics, itemTotals, justify, mainStart, crossCursor, row, lineCrossSize, maxMain);
            }

            int lineGap = (li < scaledLines.size() - 1) ? gap : 0;
            crossCursor = advanceCrossCursor(crossCursor, lineCrossSize, lineGap);
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
        List<ItemBox> boxes = new ArrayList<>(children.size());
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

    private List<ItemBox> collectMetricsScaled(List<UIElement> children, int maxMain, boolean row, double k) {
        List<ItemBox> boxes = new ArrayList<>(children.size());
        for (UIElement child : children) {
            int basis = resolveFlexBasis(child, maxMain, row);
            int mls = row ? child.getMarginLeft() : child.getMarginTop();
            int mle = row ? child.getMarginRight() : child.getMarginBottom();
            int mcs = row ? child.getMarginTop() : child.getMarginLeft();
            int mce = row ? child.getMarginBottom() : child.getMarginRight();
            int sb = scaleRound(k, basis);
            int sms = scaleRound(k, mls);
            int sme = scaleRound(k, mle);
            int smcs = scaleRound(k, mcs);
            int smce = scaleRound(k, mce);
            boxes.add(new ItemBox(child, sb, sb + sms + sme, sms, sme, smcs, smce));
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
            for (int i = 0; i < metrics.size(); i++) {
                UIElement n = metrics.get(i).node();
                int delta = (int) Math.floor((double) remaining * Math.max(0, n.getFlexGrow()) / (double) totalGrow);
                totals[i] = metrics.get(i).withMargins() + delta;
            }
        } else if (remaining < 0 && totalShrinkWeighted > 0) {
            for (int i = 0; i < metrics.size(); i++) {
                UIElement n = metrics.get(i).node();
                double shrink = Math.max(0, n.getComputedStyles().flexShrink);
                ItemBox m = metrics.get(i);
                int delta = (int) Math.floor(remaining * (shrink * m.basis()) / totalShrinkWeighted);
                int minAllowed = m.mStart() + m.mEnd();
                totals[i] = Math.max(minAllowed, m.withMargins() + delta);
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

    private void positionLineWithCrossSize(Line line, List<ItemBox> metrics, int[] totals, Justify justify, int mainStart, int crossCursor, boolean row, int lineCrossSize, int maxMain) {
        int sign = isReverse() ? -1 : 1;
        int mainCursor = isReverse() ? mainStart + maxMain - justify.leading() : mainStart + justify.leading();
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

    private void positionLineScaled(Line line, List<ItemBox> metrics, int[] totals, Justify justify, int mainStart, int crossCursor, boolean row, int lineCrossSize, double k, int maxMain) {
        int sign = isReverse() ? -1 : 1;
        int mainCursor = isReverse() ? mainStart + maxMain - justify.leading() : mainStart + justify.leading();
        int lineCrossStart = wrapReverse() ? (crossCursor - lineCrossSize) : crossCursor;

        for (int i = 0; i < line.children().size(); i++) {
            ItemBox box = metrics.get(i);
            int total = totals[i];
            int childMain = Math.max(0, total - box.mStart() - box.mEnd());
            int naturalCross = isRow() ? box.node().getHeight() : box.node().getWidth();
            int scaledNatural = scaleRound(k, naturalCross);
            int stretched = hasClass(StyleKey.ITEMS_STRETCH) ? Math.max(0, lineCrossSize - box.mCrossStart() - box.mCrossEnd()) : scaledNatural;
            int childCross = enforceMinMaxCross(stretched, box.node(), isRow());
            int crossPos;
            if (hasClass(StyleKey.ITEMS_CENTER)) {
                crossPos = lineCrossStart + (lineCrossSize - childCross) / 2 + box.mCrossStart();
            } else if (hasClass(StyleKey.ITEMS_END)) {
                crossPos = lineCrossStart + lineCrossSize - childCross - box.mCrossEnd();
            } else {
                crossPos = lineCrossStart + box.mCrossStart();
            }
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

    private int advanceCrossCursor(int crossCursor, int lineCross, int lineGap) {
        return wrapReverse() ? (crossCursor - lineCross - lineGap) : (crossCursor + lineCross + lineGap);
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
        List<Integer> sizes = new ArrayList<>(lines.size());
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
        for (Line line : lines) {
            int natural = line.crossSize();
            int cut = (int) Math.floor(deficit * (natural / (double) sumCross));
            int target = Math.max(0, natural - cut);
            fitted.add(target);
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

    private double computeUniformScaleFactor(List<Line> lines, int maxMain, int maxCross, int gap) {
        int neededMainMax = 0;
        for (Line line : lines) {
            int lineMainUsed = 0;
            for (int i = 0; i < line.children().size(); i++) {
                UIElement n = line.children().get(i);
                int basis = resolveFlexBasis(n, maxMain, isRow());
                int withMargins = basis + (isRow() ? n.getMarginLeft() + n.getMarginRight() : n.getMarginTop() + n.getMarginBottom());
                lineMainUsed += (i == 0 ? withMargins : gap + withMargins);
            }
            neededMainMax = Math.max(neededMainMax, lineMainUsed);
        }
        int rawCrossTotal = 0;
        for (int i = 0; i < lines.size(); i++) {
            rawCrossTotal += (i == 0 ? lines.get(i).crossSize() : gap + lines.get(i).crossSize());
        }
        double kMain = neededMainMax > 0 ? Math.min(1.0, (double) maxMain / (double) neededMainMax) : 1.0;
        double kCross = rawCrossTotal > 0 ? Math.min(1.0, (double) maxCross / (double) rawCrossTotal) : 1.0;
        return Math.min(kMain, kCross);
    }

    private int scaleRound(double k, int v) {
        return (int) Math.floor(k * v);
    }

    private List<Line> scaleLinesForUniform(List<Line> lines, double k, boolean row) {
        List<Line> out = new ArrayList<>(lines.size());
        for (Line line : lines) {
            int scaledCross = scaleRound(k, line.crossSize());
            out.add(new Line(line.children(), Math.max(0, scaledCross)));
        }
        return out;
    }

    private int enforceMinMaxCross(int value, UIElement n, boolean row) {
        return Math.max(0, value);
    }

    private int snapFixSum(int[] totals, int gap, int target) {
        int used = 0;
        for (int i = 0; i < totals.length; i++) used += (i == 0 ? totals[i] : gap + totals[i]);
        int correction = target - used;
        for (int i = 0; correction != 0 && i < totals.length; i++) {
            int step = correction > 0 ? 1 : -1;
            totals[i] = Math.max(0, totals[i] + step);
            correction -= step;
        }
        return correction;
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

    public FlexContainer setUniformScaleEnabled(boolean enabled) {
        this.uniformScaleEnabled = enabled;
        return this;
    }
}
