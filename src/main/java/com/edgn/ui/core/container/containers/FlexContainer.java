package com.edgn.ui.core.container.containers;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.AlignItems;
import com.edgn.ui.css.rules.FlexDirection;
import com.edgn.ui.css.rules.FlexWrap;
import com.edgn.ui.css.rules.JustifyContent;
import com.edgn.ui.layout.LayoutBox;
import com.edgn.ui.layout.LayoutEngine;

import java.util.ArrayList;
import java.util.List;

public class FlexContainer extends BaseContainer {

    public FlexContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    @Override
    public void updateChildrenLayout() {
        if (children.isEmpty()) return;

        FlexDirection direction = getFlexDirection();
        JustifyContent justify = getJustifyContent();
        AlignItems alignItems = getAlignItems();
        FlexWrap wrap = getFlexWrap();
        int gap = getGap();

        LayoutBox contentBox = LayoutEngine.calculateContentBox(this);

        if (wrap == FlexWrap.NOWRAP) {
            layoutSingleLine(contentBox, direction, justify, alignItems, gap);
        } else {
            layoutMultiLine(contentBox, direction, justify, alignItems, wrap, gap);
        }

        for (UIElement child : children) {
            child.updateConstraints();
            if (child instanceof BaseContainer container) {
                container.updateChildrenLayout();
            }
        }
    }

    private void layoutSingleLine(LayoutBox contentBox, FlexDirection direction,
                                  JustifyContent justify, AlignItems alignItems, int gap) {
        boolean isRow = direction == FlexDirection.ROW || direction == FlexDirection.ROW_REVERSE;
        int mainSize = isRow ? contentBox.width() : contentBox.height();
        int crossSize = isRow ? contentBox.height() : contentBox.width();

        List<UIElement> visibleChildren = children.stream()
                .filter(UIElement::isVisible)
                .toList();

        if (visibleChildren.isEmpty()) return;

        int totalChildSize = 0;
        int totalGaps = Math.max(0, visibleChildren.size() - 1) * gap;
        int totalFlexGrow = 0;

        for (UIElement child : visibleChildren) {
            int childSize = isRow ? child.getWidth() : child.getHeight();
            totalChildSize += childSize;
            totalFlexGrow += child.getFlexGrow();
        }

        int availableSpace = mainSize - totalChildSize - totalGaps;
        int extraSpacePerFlexUnit = totalFlexGrow > 0 && availableSpace > 0 ?
                availableSpace / totalFlexGrow : 0;

        int startPos = calculateJustifyStart(justify, availableSpace, visibleChildren.size(), gap);

        List<UIElement> orderedChildren = direction == FlexDirection.ROW_REVERSE ||
                direction == FlexDirection.COLUMN_REVERSE ?
                new ArrayList<>(visibleChildren).reversed() : visibleChildren;

        int currentPos = startPos;
        for (UIElement child : orderedChildren) {
            int crossOffset = calculateAlignOffset(alignItems, crossSize,
                    isRow ? child.getHeight() : child.getWidth());

            if (isRow) {
                child.setX(contentBox.x() + currentPos);
                child.setY(contentBox.y() + crossOffset);
            } else {
                child.setX(contentBox.x() + crossOffset);
                child.setY(contentBox.y() + currentPos);
            }

            if (child.getFlexGrow() > 0 && extraSpacePerFlexUnit > 0) {
                int extraSize = child.getFlexGrow() * extraSpacePerFlexUnit;
                if (isRow) {
                    child.setWidth(child.getWidth() + extraSize);
                } else {
                    child.setHeight(child.getHeight() + extraSize);
                }
            }

            if (alignItems == AlignItems.STRETCH) {
                if (isRow && child.getHeight() < crossSize) {
                    child.setHeight(crossSize);
                } else if (!isRow && child.getWidth() < crossSize) {
                    child.setWidth(crossSize);
                }
            }

            int childSize = isRow ? child.getWidth() : child.getHeight();
            currentPos += childSize + gap;
        }
    }

    private void layoutMultiLine(LayoutBox contentBox, FlexDirection direction,
                                 JustifyContent justify, AlignItems alignItems,
                                 FlexWrap wrap, int gap) {
        boolean isRow = direction == FlexDirection.ROW || direction == FlexDirection.ROW_REVERSE;
        int mainSize = isRow ? contentBox.width() : contentBox.height();
        int crossSize = isRow ? contentBox.height() : contentBox.width();

        List<UIElement> visibleChildren = children.stream()
                .filter(UIElement::isVisible)
                .toList();

        if (visibleChildren.isEmpty()) return;

        List<List<UIElement>> lines = new ArrayList<>();
        List<UIElement> currentLine = new ArrayList<>();
        int currentLineSize = 0;

        for (UIElement child : visibleChildren) {
            int childSize = isRow ? child.getWidth() : child.getHeight();

            if (currentLine.isEmpty() || currentLineSize + gap + childSize <= mainSize) {
                currentLine.add(child);
                currentLineSize += (currentLine.size() > 1 ? gap : 0) + childSize;
            } else {
                lines.add(new ArrayList<>(currentLine));
                currentLine.clear();
                currentLine.add(child);
                currentLineSize = childSize;
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        int lineHeight = crossSize / lines.size();
        int currentCrossPos = 0;

        for (List<UIElement> line : lines) {
            layoutLineInMultiLine(contentBox, line, direction, justify, alignItems,
                    gap, mainSize, lineHeight, currentCrossPos, isRow);
            currentCrossPos += lineHeight;
        }
    }

    private void layoutLineInMultiLine(LayoutBox contentBox, List<UIElement> line,
                                       FlexDirection direction, JustifyContent justify,
                                       AlignItems alignItems, int gap, int mainSize,
                                       int lineHeight, int crossPos, boolean isRow) {

        int totalChildSize = line.stream()
                .mapToInt(child -> isRow ? child.getWidth() : child.getHeight())
                .sum();
        int totalGaps = Math.max(0, line.size() - 1) * gap;
        int availableSpace = mainSize - totalChildSize - totalGaps;

        int startPos = calculateJustifyStart(justify, availableSpace, line.size(), gap);
        int currentPos = startPos;

        for (UIElement child : line) {
            int crossOffset = calculateAlignOffset(alignItems, lineHeight,
                    isRow ? child.getHeight() : child.getWidth());

            if (isRow) {
                child.setX(contentBox.x() + currentPos);
                child.setY(contentBox.y() + crossPos + crossOffset);
            } else {
                child.setX(contentBox.x() + crossPos + crossOffset);
                child.setY(contentBox.y() + currentPos);
            }

            if (alignItems == AlignItems.STRETCH) {
                if (isRow) {
                    child.setHeight(lineHeight);
                } else {
                    child.setWidth(lineHeight);
                }
            }

            int childSize = isRow ? child.getWidth() : child.getHeight();
            currentPos += childSize + gap;
        }
    }

    private int calculateJustifyStart(JustifyContent justify, int availableSpace,
                                      int childCount, int gap) {
        return switch (justify) {
            case FLEX_START -> 0;
            case CENTER -> Math.max(0, availableSpace / 2);
            case FLEX_END -> Math.max(0, availableSpace);
            case SPACE_BETWEEN -> 0;
            case SPACE_AROUND -> childCount > 0 ? Math.max(0, availableSpace / (childCount * 2)) : 0;
            case SPACE_EVENLY -> childCount > 0 ? Math.max(0, availableSpace / (childCount + 1)) : 0;
        };
    }

    private int calculateAlignOffset(AlignItems align, int crossSize, int childCrossSize) {
        return switch (align) {
            case FLEX_START -> 0;
            case CENTER -> Math.max(0, (crossSize - childCrossSize) / 2);
            case FLEX_END -> Math.max(0, crossSize - childCrossSize);
            case STRETCH, BASELINE -> 0;
        };
    }

    private FlexDirection getFlexDirection() {
        if (hasClass(StyleKey.FLEX_ROW)) return FlexDirection.ROW;
        if (hasClass(StyleKey.FLEX_COLUMN)) return FlexDirection.COLUMN;
        if (hasClass(StyleKey.FLEX_ROW_REVERSE)) return FlexDirection.ROW_REVERSE;
        if (hasClass(StyleKey.FLEX_COLUMN_REVERSE)) return FlexDirection.COLUMN_REVERSE;
        return FlexDirection.ROW;
    }

    private JustifyContent getJustifyContent() {
        if (hasClass(StyleKey.JUSTIFY_START)) return JustifyContent.FLEX_START;
        if (hasClass(StyleKey.JUSTIFY_CENTER)) return JustifyContent.CENTER;
        if (hasClass(StyleKey.JUSTIFY_END)) return JustifyContent.FLEX_END;
        if (hasClass(StyleKey.JUSTIFY_BETWEEN)) return JustifyContent.SPACE_BETWEEN;
        if (hasClass(StyleKey.JUSTIFY_AROUND)) return JustifyContent.SPACE_AROUND;
        if (hasClass(StyleKey.JUSTIFY_EVENLY)) return JustifyContent.SPACE_EVENLY;
        return JustifyContent.FLEX_START;
    }

    private AlignItems getAlignItems() {
        if (hasClass(StyleKey.ITEMS_START)) return AlignItems.FLEX_START;
        if (hasClass(StyleKey.ITEMS_CENTER)) return AlignItems.CENTER;
        if (hasClass(StyleKey.ITEMS_END)) return AlignItems.FLEX_END;
        if (hasClass(StyleKey.ITEMS_STRETCH)) return AlignItems.STRETCH;
        if (hasClass(StyleKey.ITEMS_BASELINE)) return AlignItems.BASELINE;
        return AlignItems.FLEX_START;
    }

    private FlexWrap getFlexWrap() {
        if (hasClass(StyleKey.FLEX_WRAP)) return FlexWrap.WRAP;
        if (hasClass(StyleKey.FLEX_WRAP_REVERSE)) return FlexWrap.WRAP_REVERSE;
        return FlexWrap.NOWRAP;
    }

    // Méthodes de convenance pour configurer le flex
    @SuppressWarnings("unchecked")
    public <T extends FlexContainer> T flexDirection(FlexDirection direction) {
        removeClass(StyleKey.FLEX_ROW).removeClass(StyleKey.FLEX_COLUMN)
                .removeClass(StyleKey.FLEX_ROW_REVERSE).removeClass(StyleKey.FLEX_COLUMN_REVERSE);

        return switch (direction) {
            case ROW -> (T) addClass(StyleKey.FLEX_ROW);
            case COLUMN -> (T) addClass(StyleKey.FLEX_COLUMN);
            case ROW_REVERSE -> (T) addClass(StyleKey.FLEX_ROW_REVERSE);
            case COLUMN_REVERSE -> (T) addClass(StyleKey.FLEX_COLUMN_REVERSE);
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends FlexContainer> T justifyContent(JustifyContent justify) {
        removeClass(StyleKey.JUSTIFY_START).removeClass(StyleKey.JUSTIFY_CENTER)
                .removeClass(StyleKey.JUSTIFY_END).removeClass(StyleKey.JUSTIFY_BETWEEN)
                .removeClass(StyleKey.JUSTIFY_AROUND).removeClass(StyleKey.JUSTIFY_EVENLY);

        return switch (justify) {
            case FLEX_START -> (T) addClass(StyleKey.JUSTIFY_START);
            case CENTER -> (T) addClass(StyleKey.JUSTIFY_CENTER);
            case FLEX_END -> (T) addClass(StyleKey.JUSTIFY_END);
            case SPACE_BETWEEN -> (T) addClass(StyleKey.JUSTIFY_BETWEEN);
            case SPACE_AROUND -> (T) addClass(StyleKey.JUSTIFY_AROUND);
            case SPACE_EVENLY -> (T) addClass(StyleKey.JUSTIFY_EVENLY);
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends FlexContainer> T alignItems(AlignItems align) {
        removeClass(StyleKey.ITEMS_START).removeClass(StyleKey.ITEMS_CENTER)
                .removeClass(StyleKey.ITEMS_END).removeClass(StyleKey.ITEMS_STRETCH)
                .removeClass(StyleKey.ITEMS_BASELINE);

        return switch (align) {
            case FLEX_START -> (T) addClass(StyleKey.ITEMS_START);
            case CENTER -> (T) addClass(StyleKey.ITEMS_CENTER);
            case FLEX_END -> (T) addClass(StyleKey.ITEMS_END);
            case STRETCH -> (T) addClass(StyleKey.ITEMS_STRETCH);
            case BASELINE -> (T) addClass(StyleKey.ITEMS_BASELINE);
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends FlexContainer> T flexWrap(FlexWrap wrap) {
        removeClass(StyleKey.FLEX_WRAP).removeClass(StyleKey.FLEX_WRAP_REVERSE)
                .removeClass(StyleKey.FLEX_NOWRAP);

        return switch (wrap) {
            case WRAP -> (T) addClass(StyleKey.FLEX_WRAP);
            case WRAP_REVERSE -> (T) addClass(StyleKey.FLEX_WRAP_REVERSE);
            case NOWRAP -> (T) addClass(StyleKey.FLEX_NOWRAP);
        };
    }
}