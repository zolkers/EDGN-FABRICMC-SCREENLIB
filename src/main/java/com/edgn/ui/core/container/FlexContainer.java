package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.AlignItems;
import com.edgn.ui.css.rules.FlexDirection;
import com.edgn.ui.css.rules.FlexWrap;
import com.edgn.ui.css.rules.JustifyContent;
import com.edgn.ui.layout.LayoutEngine;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "unused"})
public class FlexContainer extends BaseContainer {
    private FlexDirection direction = FlexDirection.ROW;
    private JustifyContent justifyContent = JustifyContent.FLEX_START;
    private AlignItems alignItems = AlignItems.STRETCH;
    private FlexWrap wrap = FlexWrap.NOWRAP;

    public FlexContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    public FlexContainer add(UIElement element) {
        addChild(element);
        return this;
    }

    @Override
    protected void layoutChildren() {
        if (children.isEmpty()) return;

        updateConstraints();
        updateStylesFromClasses();
        LayoutEngine.LayoutBox content = getContentArea();
        int gap = getGap();

        if (direction == FlexDirection.ROW || direction == FlexDirection.ROW_REVERSE) {
            layoutAsRow(content, gap);
        } else {
            layoutAsColumn(content, gap);
        }
    }

    private void updateStylesFromClasses() {
        for (StyleKey key : classes) {
            switch (key) {
                case FLEX_ROW -> direction = FlexDirection.ROW;
                case FLEX_COLUMN -> direction = FlexDirection.COLUMN;
                case FLEX_ROW_REVERSE -> direction = FlexDirection.ROW_REVERSE;
                case FLEX_COLUMN_REVERSE -> direction = FlexDirection.COLUMN_REVERSE;

                case FLEX_WRAP -> wrap = FlexWrap.WRAP;
                case FLEX_NOWRAP -> wrap = FlexWrap.NOWRAP;
                case FLEX_WRAP_REVERSE -> wrap = FlexWrap.WRAP_REVERSE;

                case JUSTIFY_START -> justifyContent = JustifyContent.FLEX_START;
                case JUSTIFY_CENTER -> justifyContent = JustifyContent.CENTER;
                case JUSTIFY_END -> justifyContent = JustifyContent.FLEX_END;
                case JUSTIFY_BETWEEN -> justifyContent = JustifyContent.SPACE_BETWEEN;
                case JUSTIFY_AROUND -> justifyContent = JustifyContent.SPACE_AROUND;
                case JUSTIFY_EVENLY -> justifyContent = JustifyContent.SPACE_EVENLY;

                case ITEMS_START -> alignItems = AlignItems.FLEX_START;
                case ITEMS_CENTER -> alignItems = AlignItems.CENTER;
                case ITEMS_END -> alignItems = AlignItems.FLEX_END;
                case ITEMS_STRETCH -> alignItems = AlignItems.STRETCH;
                case ITEMS_BASELINE -> alignItems = AlignItems.BASELINE;
            }
        }
    }

    private void layoutAsRow(LayoutEngine.LayoutBox content, int gap) {
        if (wrap == FlexWrap.NOWRAP) {
            layoutRowNoWrap(content, gap);
        } else {
            layoutRowWithWrap(content, gap);
        }
    }

    private void layoutAsColumn(LayoutEngine.LayoutBox content, int gap) {
        if (wrap == FlexWrap.NOWRAP) {
            layoutColumnNoWrap(content, gap);
        } else {
            layoutColumnWithWrap(content, gap);
        }
    }

    private void layoutRowNoWrap(LayoutEngine.LayoutBox content, int gap) {
        int totalGap = gap * Math.max(0, children.size() - 1);
        int availableWidth = content.width() - totalGap;

        int totalFlexGrow = children.stream().mapToInt(UIElement::getFlexGrow).sum();

        int reservedWidth = 0;
        for (UIElement child : children) {
            child.updateConstraints();
            if (child.getFlexGrow() == 0) {
                reservedWidth += child.getCalculatedWidth();
            }
        }

        int flexibleWidth = Math.max(0, availableWidth - reservedWidth);
        int currentX = content.x();

        for (UIElement child : children) {
            int finalChildWidth;
            if (totalFlexGrow > 0 && child.getFlexGrow() > 0) {
                finalChildWidth = (flexibleWidth * child.getFlexGrow()) / totalFlexGrow;
            } else {
                finalChildWidth = child.getCalculatedWidth();
            }

            int childY = getAlignY(content, child);

            LayoutEngine.applyMargins(child, currentX, childY, finalChildWidth, content.height());

            int availableChildWidth = finalChildWidth - child.getMarginLeft() - child.getMarginRight();
            child.setWidth(Math.max(0, availableChildWidth));

            if (alignItems == AlignItems.STRETCH) {
                int availableChildHeight = content.height() - child.getMarginTop() - child.getMarginBottom();
                child.setHeight(Math.max(0, availableChildHeight));
            }

            currentX += finalChildWidth + gap;
        }
    }

    private void layoutColumnNoWrap(LayoutEngine.LayoutBox content, int gap) {
        int totalGap = gap * Math.max(0, children.size() - 1);
        int availableHeight = content.height() - totalGap;

        int totalFlexGrow = children.stream().mapToInt(UIElement::getFlexGrow).sum();

        int reservedHeight = 0;
        for (UIElement child : children) {
            child.updateConstraints();
            if (child.getFlexGrow() == 0) {
                reservedHeight += child.getCalculatedHeight();
            }
        }

        int flexibleHeight = Math.max(0, availableHeight - reservedHeight);
        int currentY = content.y();

        for (UIElement child : children) {
            int childHeight;

            if (totalFlexGrow > 0 && child.getFlexGrow() > 0) {
                childHeight = (flexibleHeight * child.getFlexGrow()) / totalFlexGrow;
            } else {
                childHeight = child.getCalculatedHeight();
            }

            int alignX = getAlignX(content, child);
            LayoutEngine.applyMargins(child, alignX, currentY, content.width(), childHeight);

            if (alignItems == AlignItems.STRETCH) {
                int availableChildWidth = content.width() - child.getMarginLeft() - child.getMarginRight();
                int availableChildHeight = childHeight - child.getMarginTop() - child.getMarginBottom();

                child.setWidth(Math.max(0, availableChildWidth));
                child.setHeight(Math.max(0, availableChildHeight));
            }

            currentY += childHeight + gap;
        }
    }

    private void layoutRowWithWrap(LayoutEngine.LayoutBox content, int gap) {
        List<List<UIElement>> lines = wrapChildrenInRows(content, gap);
        if (lines.isEmpty()) return;

        int lineHeight = content.height() / lines.size();
        int currentY = content.y();

        for (List<UIElement> line : lines) {
            layoutLineHorizontally(line, content, currentY, lineHeight, gap);
            currentY += lineHeight;
        }
    }

    private void layoutColumnWithWrap(LayoutEngine.LayoutBox content, int gap) {
        List<List<UIElement>> columns = wrapChildrenInColumns(content, gap);
        if (columns.isEmpty()) return;

        int columnWidth = content.width() / columns.size();
        int currentX = content.x();

        for (List<UIElement> column : columns) {
            layoutLineVertically(column, content, currentX, columnWidth, gap);
            currentX += columnWidth;
        }
    }

    private List<List<UIElement>> wrapChildrenInRows(LayoutEngine.LayoutBox content, int gap) {
        List<List<UIElement>> lines = new ArrayList<>();
        List<UIElement> currentLine = new ArrayList<>();
        int currentLineWidth = 0;
        int availableWidth = content.width();

        for (UIElement child : children) {
            child.updateConstraints();
            int childTotalWidth = child.getCalculatedWidth() + child.getMarginLeft() + child.getMarginRight();
            boolean needsNewLine = !currentLine.isEmpty() && (currentLineWidth + gap + childTotalWidth > availableWidth);

            if (needsNewLine) {
                lines.add(new ArrayList<>(currentLine));
                currentLine.clear();
                currentLineWidth = 0;
            }

            currentLine.add(child);
            currentLineWidth += childTotalWidth + (currentLine.size() > 1 ? gap : 0);
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        return lines;
    }

    private List<List<UIElement>> wrapChildrenInColumns(LayoutEngine.LayoutBox content, int gap) {
        List<List<UIElement>> columns = new ArrayList<>();
        List<UIElement> currentColumn = new ArrayList<>();
        int currentColumnHeight = 0;
        int availableHeight = content.height();

        for (UIElement child : children) {
            child.updateConstraints();
            int childTotalHeight = child.getCalculatedHeight() + child.getMarginTop() + child.getMarginBottom();
            boolean needsNewColumn = !currentColumn.isEmpty() && (currentColumnHeight + gap + childTotalHeight > availableHeight);

            if (needsNewColumn) {
                columns.add(new ArrayList<>(currentColumn));
                currentColumn.clear();
                currentColumnHeight = 0;
            }

            currentColumn.add(child);
            currentColumnHeight += childTotalHeight + (currentColumn.size() > 1 ? gap : 0);
        }

        if (!currentColumn.isEmpty()) {
            columns.add(currentColumn);
        }

        return columns;
    }

    private void layoutLineHorizontally(List<UIElement> line, LayoutEngine.LayoutBox content, int y, int lineHeight, int gap) {
        if (line.isEmpty()) return;

        int totalGap = gap * Math.max(0, line.size() - 1);
        int availableWidth = content.width() - totalGap;
        int childWidth = availableWidth / line.size();
        int currentX = content.x();

        for (UIElement child : line) {
            LayoutEngine.applyMargins(child, currentX, y, childWidth, lineHeight);

            if (alignItems == AlignItems.STRETCH) {
                int availableChildWidth = childWidth - child.getMarginLeft() - child.getMarginRight();
                int availableChildHeight = lineHeight - child.getMarginTop() - child.getMarginBottom();

                child.setWidth(Math.max(0, availableChildWidth));
                child.setHeight(Math.max(0, availableChildHeight));
            }

            currentX += childWidth + gap;
        }
    }

    private void layoutLineVertically(List<UIElement> column, LayoutEngine.LayoutBox content, int x, int columnWidth, int gap) {
        if (column.isEmpty()) return;

        int totalGap = gap * Math.max(0, column.size() - 1);
        int availableHeight = content.height() - totalGap;
        int childHeight = availableHeight / column.size();
        int currentY = content.y();

        for (UIElement child : column) {
            LayoutEngine.applyMargins(child, x, currentY, columnWidth, childHeight);

            if (alignItems == AlignItems.STRETCH) {
                int availableChildWidth = columnWidth - child.getMarginLeft() - child.getMarginRight();
                int availableChildHeight = childHeight - child.getMarginTop() - child.getMarginBottom();

                child.setWidth(Math.max(0, availableChildWidth));
                child.setHeight(Math.max(0, availableChildHeight));
            }

            currentY += childHeight + gap;
        }
    }

    private int getAlignY(LayoutEngine.LayoutBox content, UIElement child) {
        child.updateConstraints();
        return switch (alignItems) {
            case CENTER -> content.y() + (content.height() - child.getCalculatedHeight()) / 2;
            case FLEX_END -> content.y() + content.height() - child.getCalculatedHeight();
            default -> content.y();
        };
    }

    private int getAlignX(LayoutEngine.LayoutBox content, UIElement child) {
        child.updateConstraints();
        return switch (alignItems) {
            case CENTER -> content.x() + (content.width() - child.getCalculatedWidth()) / 2;
            case FLEX_END -> content.x() + content.width() - child.getCalculatedWidth();
            default -> content.x();
        };
    }
}