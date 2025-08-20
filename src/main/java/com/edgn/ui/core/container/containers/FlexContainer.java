package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
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

    private static final class InsetsGap {
        int pl;
        int pr;
        int pt;
        int pb;
        int gap;
        InsetsGap(int pl, int pr, int pt, int pb, int gap) { this.pl=pl; this.pr=pr; this.pt=pt; this.pb=pb; this.gap=gap; }
    }
    private static final class ContentBox {
        int x;
        int y;
        int w;
        int h;
        ContentBox(int x, int y, int w, int h) { this.x=x; this.y=y; this.w=w; this.h=h; }
    }

    @Override
    protected void layoutChildren() {
        updateConstraints();
        List<UIElement> kids = getChildren();
        if (kids.isEmpty()) return;

        boolean row = isRow();

        InsetsGap ig = readInsetsAndGap();
        ContentBox cb = computeContentBox(ig);

        int maxMain  = row ? cb.w : cb.h;
        int maxCross = row ? cb.h : cb.w;

        List<Line> lines = measureAndWrap(kids, maxMain, row, ig.gap);

        double k = 1.0;
        if (uniformScaleEnabled) {
            k = computeUniformScaleFactor(lines, maxMain, maxCross, ig.gap);
            ig = scaleInsetsAndGap(ig, k);
            cb = computeContentBox(ig);
            maxMain  = row ? cb.w : cb.h;
            maxCross = row ? cb.h : cb.w;
            lines = measureAndWrap(kids, maxMain, row, ig.gap);
        }

        layoutAllLines(lines, cb, maxMain, maxCross, ig.gap, row, k);
    }

    private InsetsGap readInsetsAndGap() {
        int pl = Math.max(0, getPaddingLeft());
        int pr = Math.max(0, getPaddingRight());
        int pt = Math.max(0, getPaddingTop());
        int pb = Math.max(0, getPaddingBottom());
        int gap = Math.max(0, getGap());
        return new InsetsGap(pl, pr, pt, pb, gap);
    }

    private ContentBox computeContentBox(InsetsGap ig) {
        int x = getCalculatedX() + ig.pl;
        int y = getCalculatedY() + ig.pt;
        int w = Math.max(0, getCalculatedWidth()  - ig.pl - ig.pr);
        int h = Math.max(0, getCalculatedHeight() - ig.pt - ig.pb);
        return new ContentBox(x, y, w, h);
    }

    private InsetsGap scaleInsetsAndGap(InsetsGap ig, double k) {
        return new InsetsGap(
                scaleRound(k, ig.pl),
                scaleRound(k, ig.pr),
                scaleRound(k, ig.pt),
                scaleRound(k, ig.pb),
                Math.max(0, scaleRound(k, ig.gap))
        );
    }

    private List<Line> maybeScaleLines(List<Line> lines, double k, boolean row) {
        return uniformScaleEnabled ? scaleLinesForUniform(lines, k, row) : lines;
    }

    private List<ItemBox> collectMetricsAuto(Line line, int maxMain, boolean row, double k) {
        return uniformScaleEnabled
                ? collectMetricsScaled(line.children(), maxMain, row, k)
                : collectMetrics(line.children(), maxMain, row);
    }

    private void positionLineAuto(Line line, List<ItemBox> metrics, int[] totals, Justify justify,
                                  int mainStart, int crossCursor, boolean row, int lineCrossSize,
                                  double k, int maxMain) {
        if (uniformScaleEnabled) {
            positionLineScaled(line, metrics, totals, justify, mainStart, crossCursor, row, lineCrossSize, k, maxMain);
        } else {
            positionLineWithCrossSize(line, metrics, totals, justify, mainStart, crossCursor, row, lineCrossSize, maxMain);
        }
    }

    private void layoutAllLines(List<Line> lines, ContentBox cb, int maxMain, int maxCross, int gap, boolean row, double k) {
        List<Line> scaledLines = maybeScaleLines(lines, k, row);
        List<Integer> fittedCross = fitCrossSizes(scaledLines, maxCross, gap);

        int baseCross = row ? cb.y : cb.x;
        if (wrapReverse()) {
            baseCross += maxCross;
        }
        int crossCursor = baseCross;

        final int mainStart = row ? cb.x : cb.y;

        for (int li = 0; li < scaledLines.size(); li++) {
            Line line = scaledLines.get(li);
            int lineCrossSize = fittedCross.get(li);

            List<ItemBox> metrics = collectMetricsAuto(line, maxMain, row, k);

            int[] itemTotals = distributeMainSpace(metrics, maxMain, gap);
            snapFixSum(itemTotals, gap, maxMain);

            Justify justify = computeJustify(itemTotals, maxMain, gap);

            positionLineAuto(line, metrics, itemTotals, justify, mainStart, crossCursor, row, lineCrossSize, k, maxMain);

            int lineGap = (li < scaledLines.size() - 1) ? gap : 0;
            crossCursor = advanceCrossCursor(crossCursor, lineCrossSize, lineGap);
        }
    }

    private record ItemMeasure(int withMargins, int crossMin) {}

    private ItemMeasure measureForWrap(UIElement c, int maxMain, boolean row) {
        int basis = resolveFlexBasis(c, maxMain, row);
        int mStart = row ? c.getMarginLeft() : c.getMarginTop();
        int mEnd   = row ? c.getMarginRight() : c.getMarginBottom();
        int mCrsS  = row ? c.getMarginTop() : c.getMarginLeft();
        int mCrsE  = row ? c.getMarginBottom() : c.getMarginRight();

        int withMargins = basis + mStart + mEnd;
        int crossMin    = (row ? c.getHeight() : c.getWidth()) + mCrsS + mCrsE;
        return new ItemMeasure(withMargins, crossMin);
    }

    private boolean needLineBreak(boolean wrapping, boolean currentEmpty, int prospective, int maxMain) {
        return wrapping && !currentEmpty && prospective > maxMain;
    }

    private int accumulateMainUsed(int currentUsed, int gap, int withMargins, boolean firstInLine) {
        return firstInLine ? withMargins : currentUsed + gap + withMargins;
    }

    private void addLine(List<Line> lines, List<UIElement> current, int lineCross) {
        lines.add(new Line(current, lineCross));
    }

    private List<Line> measureAndWrap(List<UIElement> kids, int maxMain, boolean row, int gap) {
        List<Line> lines = new ArrayList<>();
        List<UIElement> current = new ArrayList<>();
        int lineMainUsed = 0;
        int lineCross = 0;

        final boolean wrapping = wrapEnabled();

        for (UIElement c : kids) {
            if (!c.isVisible()) continue;
            c.updateConstraints();

            ItemMeasure m = measureForWrap(c, maxMain, row);
            int prospective = current.isEmpty() ? m.withMargins : lineMainUsed + gap + m.withMargins;

            if (needLineBreak(wrapping, current.isEmpty(), prospective, maxMain)) {
                addLine(lines, current, lineCross);
                current = new ArrayList<>();
                lineMainUsed = 0;
                lineCross = 0;
            }

            current.add(c);
            lineMainUsed = accumulateMainUsed(lineMainUsed, gap, m.withMargins, current.size() == 1);
            lineCross = Math.max(lineCross, m.crossMin);
        }

        if (!current.isEmpty()) addLine(lines, current, lineCross);
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

    private static final class FlexAgg {
        int used;
        long totalGrow;
        double totalShrinkWeighted;
    }

    private FlexAgg computeAggregates(List<ItemBox> metrics, int gap) {
        FlexAgg a = new FlexAgg();
        for (int i = 0; i < metrics.size(); i++) {
            int withMargins = metrics.get(i).withMargins();
            a.used += (i == 0 ? withMargins : gap + withMargins);
            UIElement n = metrics.get(i).node();
            a.totalGrow += Math.max(0, n.getFlexGrow());
            a.totalShrinkWeighted += Math.max(0, n.getComputedStyles().getFlexShrink()) * (double) metrics.get(i).basis();
        }
        return a;
    }

    private void applyRoundRobinCorrection(int[] totals, int correction, List<ItemBox> metrics) {
        if (correction == 0 || totals.length == 0) return;
        int dir = correction > 0 ? 1 : -1;
        int remaining = Math.abs(correction);
        int i = 0;
        while (remaining > 0) {
            int idx = i % totals.length;
            int minAllowed = metrics.get(idx).mStart() + metrics.get(idx).mEnd();
            int candidate = totals[idx] + dir;
            if (dir > 0 || candidate >= minAllowed) {
                totals[idx] = Math.max(minAllowed, candidate);
                remaining--;
            }
            i++;
        }
    }

    private int[] distributeMainSpace(List<ItemBox> metrics, int maxMain, int gap) {
        int count = metrics.size();
        int[] totals = new int[count];
        if (count == 0) return totals;

        for (int i = 0; i < count; i++) totals[i] = metrics.get(i).withMargins();

        FlexAgg a = computeAggregates(metrics, gap);
        int remaining = maxMain - a.used;

        if (remaining > 0 && a.totalGrow > 0) {
            for (int i = 0; i < count; i++) {
                UIElement n = metrics.get(i).node();
                long grow = Math.max(0, n.getFlexGrow());
                int delta = (int) Math.floor((double) remaining * grow / a.totalGrow);
                totals[i] += delta;
            }
        } else if (remaining < 0 && a.totalShrinkWeighted > 0) {
            for (int i = 0; i < count; i++) {
                UIElement n = metrics.get(i).node();
                double shrink = Math.max(0, n.getComputedStyles().getFlexShrink());
                ItemBox m = metrics.get(i);
                int delta = (int) Math.floor(remaining * (shrink * m.basis()) / a.totalShrinkWeighted);
                int minAllowed = m.mStart() + m.mEnd();
                totals[i] = Math.max(minAllowed, m.withMargins() + delta);
            }
        }
        int correction = maxMain - sumWithGaps(totals, gap);
        applyRoundRobinCorrection(totals, correction, metrics);

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

    private void positionLineScaled(
            Line line, List<ItemBox> metrics, int[] totals, Justify justify,
            int mainStart, int crossCursor, boolean row, int lineCrossSize, double k, int maxMain) {

        final boolean reverse = isReverse();
        final boolean wrapRev = wrapReverse();
        final boolean itemsCenter = hasClass(StyleKey.ITEMS_CENTER);
        final boolean itemsEnd    = hasClass(StyleKey.ITEMS_END);
        final boolean itemsStretch= hasClass(StyleKey.ITEMS_STRETCH);

        final int sign = reverse ? -1 : 1;
        final int mainCursorBase = reverse ? mainStart + maxMain - justify.leading()
                : mainStart + justify.leading();
        final int lineCrossStart = wrapRev ? (crossCursor - lineCrossSize) : crossCursor;
        final int size = line.children().size();

        int mainCursor = mainCursorBase;

        for (int i = 0; i < size; i++) {
            ItemBox box = metrics.get(i);
            int total = totals[i];

            int childMain = Math.max(0, total - box.mStart() - box.mEnd());
            int childCross = getChildCrossSizeScaled(box, lineCrossSize, itemsStretch, k, row);
            int crossPos = computeCrossPosScaled(itemsCenter, itemsEnd, lineCrossStart, lineCrossSize, childCross, box);

            int startMargin = startMarginForDirection(box.node(), row, reverse);
            int itemStartPos = reverse ? (mainCursor - total + startMargin) : (mainCursor + startMargin);

            setNodeFrame(row, box.node(), itemStartPos, crossPos, childMain, childCross);
            box.node().updateConstraints();

            int spacing = (i + 1 < size) ? justify.between() : 0;
            mainCursor += sign * (total + spacing);
        }
    }


    private int getChildCrossSizeScaled(ItemBox box, int lineCrossSize, boolean itemsStretch, double k, boolean row) {
        int naturalCross = row ? box.node().getHeight() : box.node().getWidth();
        int scaledNatural = scaleRound(k, naturalCross);
        int stretched = itemsStretch ? Math.max(0, lineCrossSize - box.mCrossStart() - box.mCrossEnd()) : scaledNatural;
        return enforceMinMaxCross(stretched, box.node(), row);
    }

    private int computeCrossPosScaled(boolean itemsCenter, boolean itemsEnd,
                                      int lineCrossStart, int lineCrossSize, int childCross, ItemBox box) {
        if (itemsCenter) {
            return lineCrossStart + (lineCrossSize - childCross) / 2 + box.mCrossStart();
        }
        if (itemsEnd) {
            return lineCrossStart + lineCrossSize - childCross - box.mCrossEnd();
        }
        return lineCrossStart + box.mCrossStart();
    }

    private void setNodeFrame(boolean row, UIElement node, int mainPos, int crossPos, int mainSize, int crossSize) {
        if (row) {
            node.setX(mainPos);
            node.setY(crossPos);
            node.setWidth(mainSize);
            node.setHeight(crossSize);
        } else {
            node.setX(crossPos);
            node.setY(mainPos);
            node.setWidth(crossSize);
            node.setHeight(mainSize);
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
            int cut = (int) Math.floor(deficit * ((double) natural / sumCross));
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
