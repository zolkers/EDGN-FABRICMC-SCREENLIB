package com.edgn.ui.core.container;

import com.edgn.ui.core.IElement;
import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutEngine;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked"})
public abstract class BaseContainer extends UIElement implements IContainer {
    protected final List<UIElement> children = new ArrayList<>();
    private UIElement capturedElement = null;
    private int capturedButton = -1;

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

        List<UIElement> sorted = LayoutEngine.sortByRenderOrder(children);
        for (int i = sorted.size() - 1; i >= 0; i--) {
            UIElement child = sorted.get(i);
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;

            int offX = getChildInteractionOffsetX(child);
            int offY = getChildInteractionOffsetY(child);
            double mx = mouseX - offX;
            double my = mouseY - offY;

            if (!child.canInteract(mx, my)) continue;
            if (child.onMouseClick(mx, my, button)) {
                capturedElement = child;
                capturedButton = button;
                return true;
            }
        }
        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        try {
            if (capturedElement != null && button == capturedButton) {
                int offX = getChildInteractionOffsetX(capturedElement);
                int offY = getChildInteractionOffsetY(capturedElement);
                double mx = mouseX - offX;
                double my = mouseY - offY;
                return capturedElement.onMouseRelease(mx, my, button);
            }
        } finally {
            if (button == capturedButton) {
                capturedElement = null;
                capturedButton = -1;
            }
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        List<UIElement> sorted = LayoutEngine.sortByRenderOrder(children);
        for (int i = sorted.size() - 1; i >= 0; i--) {
            UIElement child = sorted.get(i);
            if (!child.isVisible() || !child.isEnabled() || !child.isRendered()) continue;

            int offX = getChildInteractionOffsetX(child);
            int offY = getChildInteractionOffsetY(child);
            double mx = mouseX - offX;
            double my = mouseY - offY;

            if (!child.canInteract(mx, my)) continue;
            if (child.onMouseScroll(mx, my, scrollDelta)) return true;
        }
        return super.onMouseScroll(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (capturedElement != null && button == capturedButton) {
            int offX = getChildInteractionOffsetX(capturedElement);
            int offY = getChildInteractionOffsetY(capturedElement);
            double mx = mouseX - offX;
            double my = mouseY - offY;
            return capturedElement.onMouseDrag(mx, my, button, deltaX, deltaY);
        }
        return super.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (UIElement child : children) {
            if (!child.isVisible() || !child.isRendered()) continue;

            int offX = getChildInteractionOffsetX(child);
            int offY = getChildInteractionOffsetY(child);
            double mx = mouseX - offX;
            double my = mouseY - offY;

            child.onMouseMove(mx, my);
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
                DrawingUtils.drawShadow(context, getCalculatedX(), getCalculatedY(),
                        getCalculatedWidth(), getCalculatedHeight(), 2, 2, shadow.color);
            }

            DrawingUtils.drawRoundedRect(context, getCalculatedX(), getCalculatedY(),
                    getCalculatedWidth(), getCalculatedHeight(), borderRadius, bgColor);
        }
    }

    private boolean containsInOwnInteractionBounds(double worldX, double worldY) {
        InteractionBounds b = getInteractionBounds();
        if (!b.isValid()) return true;
        return worldX >= b.minX && worldX < b.maxX && worldY >= b.minY && worldY < b.maxY;
    }

    private UIElement pickTopChildAt(double worldX, double worldY) {
        if (!isVisible() || !isRendered()) return null;
        if (!containsInOwnInteractionBounds(worldX, worldY)) return null;

        List<UIElement> sorted = LayoutEngine.sortByRenderOrder(children);
        for (int i = sorted.size() - 1; i >= 0; i--) {
            UIElement child = sorted.get(i);
            if (child == null || !child.isVisible() || !child.isRendered()) continue;

            int offX = getChildInteractionOffsetX(child);
            int offY = getChildInteractionOffsetY(child);
            double localX = worldX - offX;
            double localY = worldY - offY;

            if (child instanceof BaseContainer bc) {
                UIElement deep = bc.pickTopChildAt(localX, localY);
                if (deep != null) return deep;
            }

            if (child.canInteract(localX, localY)) {
                return child;
            }
        }
        return this.canInteract(worldX, worldY) ? this : null;
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
