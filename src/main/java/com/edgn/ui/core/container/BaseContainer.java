package com.edgn.ui.core.container;

import com.edgn.ui.core.IElement;
import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutEngine;
import com.edgn.ui.utils.DrawContextUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public abstract class BaseContainer extends UIElement implements IContainer {
    protected final List<UIElement> children = new ArrayList<>();

    public BaseContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    @Override
    public <T extends IContainer> T addChild(UIElement element) {
        if (element != null && !children.contains(element)) {
            element.setParent(this);
            children.add(element);
            markConstraintsDirty();
        }
        return (T) this;
    }

    @Override
    public <T extends IContainer> T removeChild(UIElement element) {
        if (children.remove(element)) {
            if (element != null) {
                element.setParent(null);
                element.markAsNotRendered();
            }
            markConstraintsDirty();
        }
        return (T) this;
    }

    @Override
    public <T extends IContainer> T clearChildren() {
        for (UIElement child : children) {
            if (child != null) {
                child.setParent(null);
                child.markAsNotRendered();
            }
        }
        children.clear();
        markConstraintsDirty();
        return (T) this;
    }

    @Override
    public void markConstraintsDirty() {
        if (constraintsDirty) return;
        super.markConstraintsDirty();
        for (UIElement child : children) {
            child.markConstraintsDirty();
        }
    }

    @Override
    public void updateConstraints() {
        if (!constraintsDirty) return;

        calculateEffectiveBounds();
        updateInteractionBounds();
        constraintsDirty = false;

        for (UIElement child : children) {
            child.updateConstraints();
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;

        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseClick(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child.onMouseScroll(mouseX, mouseY, scrollDelta)) {
                return true;
            }
        }
        return super.onMouseScroll(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        List<UIElement> sortedChildren = LayoutEngine.sortByRenderOrder(children);

        for (int i = sortedChildren.size() - 1; i >= 0; i--) {
            UIElement child = sortedChildren.get(i);
            if (child.isVisible() && child.isEnabled() && child.isRendered() &&
                    child.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (UIElement child : children) {
            if (child.isVisible() && child.isRendered()) {
                child.onMouseMove(mouseX, mouseY);
            }
        }
    }

    public List<UIElement> getChildren() {
        return new ArrayList<>(children);
    }

    public List<UIElement> getVisibleChildren() {
        return LayoutEngine.sortByRenderOrder(children);
    }

    public UIElement getTopChildAt(double mouseX, double mouseY) {
        return LayoutEngine.getTopElementAt(children, mouseX, mouseY);
    }

    public boolean canChildInteractAt(UIElement child, double mouseX, double mouseY) {
        return LayoutEngine.canInteractAt(child, children, mouseX, mouseY);
    }

    @Override
    public <T extends IElement> T setVisible(boolean visible) {
        boolean wasVisible = this.visible;
        super.setVisible(visible);

        if (wasVisible && !visible) {
            for (UIElement child : children) {
                child.markAsNotRendered();
            }
        }

        return (T) this;
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) {
            markAsNotRendered();
            for (UIElement child : children) {
                child.markAsNotRendered();
            }
            return;
        }

        markAsRendered();
        updateConstraints();
        renderBackground(context);

        LayoutEngine.LayoutBox content = getContentArea();

        InteractionBounds bounds = getInteractionBounds();
        if (bounds.isValid()) {
            context.enableScissor(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
        }

        try {
            layoutChildren();

            List<UIElement> sortedChildren = LayoutEngine.sortByRenderOrder(children);

            for (UIElement child : sortedChildren) {
                if (child != null && child.isVisible()) {
                    LayoutEngine.applyElementStyles(child);
                    child.renderElement(context);
                }
            }
        } finally {
            if (bounds.isValid()) {
                context.disableScissor();
            }
        }
    }

    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            Shadow shadow = getShadow();

            if (shadow != null) {
                DrawContextUtils.drawShadow(context, getCalculatedX(), getCalculatedY(),
                        getCalculatedWidth(), getCalculatedHeight(), 2, 2, shadow.color);
            }

            DrawContextUtils.drawRoundedRect(context, getCalculatedX(), getCalculatedY(),
                    getCalculatedWidth(), getCalculatedHeight(), borderRadius, bgColor);
        }
    }

    protected LayoutEngine.LayoutBox getContentArea() {
        return LayoutEngine.calculateContentBox(this);
    }

    protected abstract void layoutChildren();

    protected List<UIElement> getSortedChildren() {
        return LayoutEngine.sortByZIndex(children);
    }


    public BaseContainer bringChildToFront(UIElement child) {
        if (children.contains(child)) {
            int maxZIndex = children.stream()
                    .mapToInt(UIElement::getZIndexValue)
                    .max()
                    .orElse(0);

            child.setZIndex(maxZIndex + 1);
        }
        return this;
    }

    public BaseContainer sendChildToBack(UIElement child) {
        if (children.contains(child)) {
            int minZIndex = children.stream()
                    .mapToInt(UIElement::getZIndexValue)
                    .min()
                    .orElse(0);

            child.setZIndex(minZIndex - 1);
        }
        return this;
    }

    public ContainerRenderStats getRenderStats() {
        long totalChildren = children.size();
        long visibleChildren = children.stream().filter(UIElement::isVisible).count();
        long renderedChildren = children.stream().filter(UIElement::isRendered).count();
        long interactiveChildren = children.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .count();

        return new ContainerRenderStats(totalChildren, visibleChildren, renderedChildren, interactiveChildren);
    }

    public record ContainerRenderStats(
            long totalChildren,
            long visibleChildren,
            long renderedChildren,
            long interactiveChildren
    ) {}
}