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
    private record ElementMetrics(int mainSize, int crossSize) {}
    private record Margins(int start, int end) {}
    private record FlexMetrics(int usedSpace, long totalGrow, double totalShrinkWeighted) {}
    private record ElementDimensions(int mainSize, int crossSize) {}
    private record ElementPosition(int mainPos, int crossPos, int spacingAfter) {}
    private record LayoutContext(int contentX, int contentY, int contentW, int contentH,
                                 int maxMain, int maxCross, int gap, boolean isRow, double scaleFactor) {}

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

        LayoutContext context = createLayoutContext();
        List<Line> lines = measureAndWrap(kids, context.maxMain, context.isRow, context.gap);

        if (uniformScaleEnabled) {
            context = applyUniformScaling(context, lines);
            lines = measureAndWrap(kids, context.maxMain, context.isRow, context.gap);
        }

        layoutLines(lines, context);
    }

    private LayoutContext createLayoutContext() {
        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        int gap = getGap();

        int contentX = getCalculatedX() + pl;
        int contentY = getCalculatedY() + pt;
        int contentW = Math.max(0, getCalculatedWidth() - pl - pr);
        int contentH = Math.max(0, getCalculatedHeight() - pt - pb);

        boolean isRow = isRow();
        int maxMain = isRow ? contentW : contentH;
        int maxCross = isRow ? contentH : contentW;

        return new LayoutContext(contentX, contentY, contentW, contentH, maxMain, maxCross, gap, isRow, 1.0);
    }

    private LayoutContext applyUniformScaling(LayoutContext original, List<Line> lines) {
        double k = computeUniformScaleFactor(lines, original.maxMain, original.maxCross, original.gap);

        int scaledGap = scaleRound(k, original.gap);
        int scaledPl = scaleRound(k, getPaddingLeft());
        int scaledPr = scaleRound(k, getPaddingRight());
        int scaledPt = scaleRound(k, getPaddingTop());
        int scaledPb = scaleRound(k, getPaddingBottom());

        int newContentX = getCalculatedX() + scaledPl;
        int newContentY = getCalculatedY() + scaledPt;
        int newContentW = Math.max(0, getCalculatedWidth() - scaledPl - scaledPr);
        int newContentH = Math.max(0, getCalculatedHeight() - scaledPt - scaledPb);

        int newMaxMain = original.isRow ? newContentW : newContentH;
        int newMaxCross = original.isRow ? newContentH : newContentW;

        return new LayoutContext(newContentX, newContentY, newContentW, newContentH,
                newMaxMain, newMaxCross, scaledGap, original.isRow, k);
    }

    private void layoutLines(List<Line> lines, LayoutContext context) {
        List<Line> scaledLines = uniformScaleEnabled ? scaleLinesForUniform(lines, context.scaleFactor, context.isRow) : lines;
        List<Integer> fittedCross = fitCrossSizes(scaledLines, context.maxCross, context.gap);

        int crossCursor = calculateInitialCrossCursor(context);

        for (int li = 0; li < scaledLines.size(); li++) {
            Line line = scaledLines.get(li);
            int lineCrossSize = fittedCross.get(li);

            layoutSingleLine(line, context, lineCrossSize, crossCursor);

            int lineGap = (li < scaledLines.size() - 1) ? context.gap : 0;
            crossCursor = advanceCrossCursor(crossCursor, lineCrossSize, lineGap);
        }
    }

    private int calculateInitialCrossCursor(LayoutContext context) {
        if (wrapReverse()) {
            return context.isRow ? context.contentY + context.maxCross : context.contentX + context.maxCross;
        } else {
            return context.isRow ? context.contentY : context.contentX;
        }
    }

    private void layoutSingleLine(Line line, LayoutContext context, int lineCrossSize, int crossCursor) {
        List<ItemBox> metrics = uniformScaleEnabled
                ? collectMetricsScaled(line.children(), context.maxMain, context.isRow, context.scaleFactor)
                : collectMetrics(line.children(), context.maxMain, context.isRow);

        int[] itemTotals = distributeMainSpace(metrics, context.maxMain, context.gap);
        snapFixSum(itemTotals, context.gap, context.maxMain);
        Justify justify = computeJustify(itemTotals, context.maxMain, context.gap);

        int mainStart = context.isRow ? context.contentX : context.contentY;

        if (uniformScaleEnabled) {
            positionLineScaled(line, metrics, itemTotals, justify, mainStart, crossCursor,
                    context.isRow, lineCrossSize, context.scaleFactor, context.maxMain);
        } else {
            positionLineWithCrossSize(line, metrics, itemTotals, justify, mainStart, crossCursor,
                    context.isRow, lineCrossSize, context.maxMain);
        }
    }

    private List<Line> measureAndWrap(List<UIElement> kids, int maxMain, boolean row, int gap) {
        List<Line> lines = new ArrayList<>();
        LineBuilder currentLine = new LineBuilder();

        for (UIElement child : kids) {
            if (!child.isVisible()) continue;

            child.updateConstraints();
            ElementMetrics metrics = calculateElementMetrics(child, row);

            if (shouldWrapToNewLine(currentLine, metrics, maxMain, gap)) {
                lines.add(currentLine.build());
                currentLine = new LineBuilder();
            }

            currentLine.addElement(child, metrics, gap);
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.build());
        }

        return lines;
    }

    private ElementMetrics calculateElementMetrics(UIElement element, boolean row) {
        int basis = resolveFlexBasis(element, Integer.MAX_VALUE, row);

        Margins mainMargins = row
                ? new Margins(element.getMarginLeft(), element.getMarginRight())
                : new Margins(element.getMarginTop(), element.getMarginBottom());

        Margins crossMargins = row
                ? new Margins(element.getMarginTop(), element.getMarginBottom())
                : new Margins(element.getMarginLeft(), element.getMarginRight());

        int mainSize = basis + mainMargins.start + mainMargins.end;
        int crossSize = (row ? element.getHeight() : element.getWidth()) + crossMargins.start + crossMargins.end;

        return new ElementMetrics(mainSize, crossSize);
    }

    private boolean shouldWrapToNewLine(LineBuilder currentLine, ElementMetrics metrics, int maxMain, int gap) {
        if (!wrapEnabled() || currentLine.isEmpty()) {
            return false;
        }

        int prospectiveSize = currentLine.getMainSize() + gap + metrics.mainSize;
        return prospectiveSize > maxMain;
    }

    private static class LineBuilder {
        private final List<UIElement> elements = new ArrayList<>();
        private int mainSize = 0;
        private int crossSize = 0;

        boolean isEmpty() {
            return elements.isEmpty();
        }

        int getMainSize() {
            return mainSize;
        }

        void addElement(UIElement element, ElementMetrics metrics, int gap) {
            elements.add(element);

            if (elements.size() == 1) {
                mainSize = metrics.mainSize;
            } else {
                mainSize += gap + metrics.mainSize;
            }

            crossSize = Math.max(crossSize, metrics.crossSize);
        }

        Line build() {
            return new Line(new ArrayList<>(elements), crossSize);
        }
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
        FlexMetrics flexMetrics = calculateFlexMetrics(metrics, gap);
        int remaining = maxMain - flexMetrics.usedSpace;

        int[] totals = new int[metrics.size()];

        if (remaining > 0) {
            distributeExtraSpace(metrics, totals, remaining, flexMetrics.totalGrow);
        } else if (remaining < 0) {
            shrinkOverflowingSpace(metrics, totals, remaining, flexMetrics.totalShrinkWeighted);
        } else {
            copyBaselineSizes(metrics, totals);
        }

        applyPixelPerfectCorrection(metrics, totals, maxMain, gap);
        return totals;
    }

    private FlexMetrics calculateFlexMetrics(List<ItemBox> metrics, int gap) {
        int usedSpace = 0;
        long totalGrow = 0L;
        double totalShrinkWeighted = 0.0;

        for (int i = 0; i < metrics.size(); i++) {
            ItemBox item = metrics.get(i);
            usedSpace += (i == 0 ? item.withMargins() : gap + item.withMargins());

            UIElement element = item.node();
            totalGrow += Math.max(0, element.getFlexGrow());
            totalShrinkWeighted += Math.max(0, element.getComputedStyles().getFlexShrink()) * item.basis();
        }

        return new FlexMetrics(usedSpace, totalGrow, totalShrinkWeighted);
    }

    private void distributeExtraSpace(List<ItemBox> metrics, int[] totals, int remaining, long totalGrow) {
        if (totalGrow == 0) {
            copyBaselineSizes(metrics, totals);
            return;
        }

        for (int i = 0; i < metrics.size(); i++) {
            ItemBox item = metrics.get(i);
            int grow = Math.max(0, item.node().getFlexGrow());
            int delta = (int) Math.floor((double) remaining * grow / totalGrow);
            totals[i] = item.withMargins() + delta;
        }
    }

    private void shrinkOverflowingSpace(List<ItemBox> metrics, int[] totals, int deficit, double totalShrinkWeighted) {
        if (totalShrinkWeighted == 0) {
            copyBaselineSizes(metrics, totals);
            return;
        }

        for (int i = 0; i < metrics.size(); i++) {
            ItemBox item = metrics.get(i);
            double shrink = Math.max(0, item.node().getComputedStyles().getFlexShrink());
            double shrinkWeight = shrink * item.basis();

            int delta = (int) Math.floor(deficit * shrinkWeight / totalShrinkWeighted);
            int minSize = item.mStart() + item.mEnd();
            totals[i] = Math.max(minSize, item.withMargins() + delta);
        }
    }

    private void copyBaselineSizes(List<ItemBox> metrics, int[] totals) {
        for (int i = 0; i < metrics.size(); i++) {
            totals[i] = metrics.get(i).withMargins();
        }
    }

    private void applyPixelPerfectCorrection(List<ItemBox> metrics, int[] totals, int maxMain, int gap) {
        int actualUsed = sumWithGaps(totals, gap);
        int correction = maxMain - actualUsed;

        for (int i = 0; correction != 0 && i < totals.length; i++) {
            int step = correction > 0 ? 1 : -1;
            int minSize = metrics.get(i).mStart() + metrics.get(i).mEnd();

            int newSize = totals[i] + step;
            if (newSize >= minSize) {
                totals[i] = newSize;
                correction -= step;
            }
        }
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

    private void positionLineScaled(Line line, List<ItemBox> metrics, int[] totals, Justify justify,
                                    int mainStart, int crossCursor, boolean row, int lineCrossSize, double k, int maxMain) {

        PositionContext context = createPositionContext(mainStart, crossCursor, lineCrossSize, justify, maxMain, row);

        for (int i = 0; i < line.children().size(); i++) {
            ItemBox item = metrics.get(i);
            int allocatedSpace = totals[i];

            ElementDimensions dimensions = calculateScaledDimensions(item, allocatedSpace, lineCrossSize, k, row);
            ElementPosition position = calculateElementPosition(item, dimensions, context, allocatedSpace, i < line.children().size() - 1);

            applyPositionAndSize(item.node(), position, dimensions, row);
            item.node().updateConstraints();

            context = context.advanceMainCursor(allocatedSpace, position.spacingAfter);
        }
    }

    private PositionContext createPositionContext(int mainStart, int crossCursor, int lineCrossSize,
                                                  Justify justify, int maxMain, boolean row) {
        int direction = isReverse() ? -1 : 1;
        int mainCursor = isReverse() ?
                mainStart + maxMain - justify.leading() :
                mainStart + justify.leading();
        int lineCrossStart = wrapReverse() ?
                crossCursor - lineCrossSize :
                crossCursor;

        return new PositionContext(mainCursor, lineCrossStart, lineCrossSize, direction, justify.between());
    }

    private ElementDimensions calculateScaledDimensions(ItemBox item, int allocatedSpace, int lineCrossSize, double k, boolean row) {
        int mainSize = Math.max(0, allocatedSpace - item.mStart() - item.mEnd());

        int naturalCrossSize = row ? item.node().getHeight() : item.node().getWidth();
        int scaledNaturalCross = scaleRound(k, naturalCrossSize);

        int crossSize;
        if (hasClass(StyleKey.ITEMS_STRETCH)) {
            int stretchedSize = Math.max(0, lineCrossSize - item.mCrossStart() - item.mCrossEnd());
            crossSize = enforceMinMaxCross(stretchedSize, item.node(), row);
        } else {
            crossSize = enforceMinMaxCross(scaledNaturalCross, item.node(), row);
        }

        return new ElementDimensions(mainSize, crossSize);
    }

    private ElementPosition calculateElementPosition(ItemBox item, ElementDimensions dimensions,
                                                     PositionContext context, int allocatedSpace, boolean hasNext) {
        int crossPos = calculateCrossPosition(item, dimensions.crossSize, context);
        int mainMargin = startMarginForDirection(item.node(), isRow(), isReverse());

        int mainPos = isReverse() ?
                context.mainCursor - allocatedSpace + mainMargin :
                context.mainCursor + mainMargin;

        int spacingAfter = hasNext ? context.betweenSpacing : 0;

        return new ElementPosition(mainPos, crossPos, spacingAfter);
    }

    private int calculateCrossPosition(ItemBox item, int childCrossSize, PositionContext context) {
        if (hasClass(StyleKey.ITEMS_CENTER)) {
            return context.lineCrossStart + (context.lineCrossSize - childCrossSize) / 2 + item.mCrossStart();
        } else if (hasClass(StyleKey.ITEMS_END)) {
            return context.lineCrossStart + context.lineCrossSize - childCrossSize - item.mCrossEnd();
        } else {
            return context.lineCrossStart + item.mCrossStart();
        }
    }

    private void applyPositionAndSize(UIElement element, ElementPosition position, ElementDimensions dimensions, boolean row) {
        if (row) {
            element.setX(position.mainPos);
            element.setY(position.crossPos);
            element.setWidth(dimensions.mainSize);
            element.setHeight(dimensions.crossSize);
        } else {
            element.setX(position.crossPos);
            element.setY(position.mainPos);
            element.setWidth(dimensions.crossSize);
            element.setHeight(dimensions.mainSize);
        }
    }

    private record PositionContext(int mainCursor, int lineCrossStart, int lineCrossSize,
                                   int direction, int betweenSpacing) {

        PositionContext advanceMainCursor(int allocatedSpace, int spacing) {
            int newCursor = mainCursor + direction * (allocatedSpace + spacing);
            return new PositionContext(newCursor, lineCrossStart, lineCrossSize, direction, betweenSpacing);
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
        int basis = child.getComputedStyles().getFlexBasis();
        if (basis > 0 && basis <= 100) {
            return Math.max(0, (int) Math.floor((basis / 100.0) * maxMain));
        }
        if (basis <= 0) {
            int raw = row ? child.getWidth() : child.getHeight();
            return Math.clamp(raw, 0, maxMain);
        }
        return Math.clamp(basis, 0, maxMain);
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
            if(sumCross == 0) continue;
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
        super.addChild(element);
        return this;
    }

    @Override
    public FlexContainer addClass(StyleKey... keys) {
        super.addClass(keys);
        return this;
    }

    @Override
    public FlexContainer removeClass(StyleKey key) {
        super.removeClass(key);
        return this;
    }

    @Override
    public FlexContainer removeChild(UIElement element) {
        super.removeChild(element);
        return this;
    }

    public FlexContainer setUniformScaleEnabled(boolean enabled) {
        this.uniformScaleEnabled = enabled;
        return this;
    }
}
