package com.edgn.ui.core.container;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.layout.ClipBounds;
import com.edgn.ui.layout.LayoutBox;
import com.edgn.ui.layout.LayoutEngine;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseContainer extends UIElement implements IContainer {
    protected final List<UIElement> children = new CopyOnWriteArrayList<>();
    protected boolean clipChildren = true;
    private boolean updatingConstraints = false;
    protected ClipBounds clipBounds;

    public BaseContainer(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        updateClipBounds();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IContainer> T addChild(UIElement child) {
        if (child != null && !children.contains(child)) {
            children.add(child);
            child.setParent(this);
            markConstraintsDirty();
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IContainer> T removeChild(UIElement child) {
        if (children.remove(child)) {
            child.setParent(null);
            markConstraintsDirty();
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IContainer> T clearChildren() {
        for (UIElement child : children) {
            child.setParent(null);
        }
        children.clear();
        markConstraintsDirty();
        return (T) this;
    }

    @Override
    public List<UIElement> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public void updateChildrenLayout() {
        for (UIElement child : children) {
            LayoutEngine.applyElementStyles(child);
            if (child instanceof IContainer container) {
                container.updateChildrenLayout();
            }
        }
    }

    @Override
    public UIElement findElementAt(double mouseX, double mouseY) {
        if (!clipBounds.contains(mouseX, mouseY)) {
            return null;
        }

        List<UIElement> sortedChildren = LayoutEngine.sortByInteractionPriority(children, mouseX, mouseY);

        for (UIElement child : sortedChildren) {
            if (child instanceof IContainer container) {
                UIElement found = container.findElementAt(mouseX, mouseY);
                if (found != null) {
                    return found;
                }
            } else if (child.canInteract(mouseX, mouseY)) {
                return child;
            }
        }

        return this.canInteract(mouseX, mouseY) ? this : null;
    }

    @Override
    public ClipBounds getClipBounds() {
        updateClipBounds();
        return clipBounds;
    }

    protected void updateClipBounds() {
        LayoutBox contentBox = LayoutEngine.calculateContentBox(this);
        int borderRadius = clipChildren ? getBorderRadius() : 0;

        ClipBounds baseClip = ClipBounds.fromLayoutBox(contentBox, borderRadius);

        if (parent instanceof IContainer parentContainer && clipChildren) {
            ClipBounds parentClip = parentContainer.getClipBounds();
            clipBounds = baseClip.intersect(parentClip);
        } else {
            clipBounds = baseClip;
        }
    }

    @Override
    public boolean canInteract(double mouseX, double mouseY) {
        if (!visible || !enabled) return false;

        updateClipBounds();
        return clipBounds.contains(mouseX, mouseY);
    }

    @Override
    public void updateConstraints() {
        if (updatingConstraints) return;

        updatingConstraints = true;
        try {
            super.updateConstraints();
            updateClipBounds();
        } finally {
            updatingConstraints = false;
        }
    }

    @Override
    public void markConstraintsDirty() {
        super.markConstraintsDirty();
        for (UIElement child : children) {
            child.markConstraintsDirty();
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        UIElement targetElement = findElementAt(mouseX, mouseY);
        if (targetElement != null && targetElement != this) {
            return targetElement.onMouseClick(mouseX, mouseY, button);
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        for (UIElement child : children) {
            if (child.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.onMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        UIElement targetElement = findElementAt(mouseX, mouseY);
        if (targetElement != null && targetElement != this) {
            return targetElement.onMouseScroll(mouseX, mouseY, scrollDelta);
        }
        return super.onMouseScroll(mouseX, mouseY, scrollDelta);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (UIElement child : children) {
            child.onMouseMove(mouseX, mouseY);
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateClipBounds();
        if (!clipBounds.isValid()) return;

        renderBackground(context);

        if (clipChildren) {
            clipBounds.applyScissor(context);
        }

        List<UIElement> sortedChildren = LayoutEngine.sortByZIndex(children);
        for (UIElement child : sortedChildren) {
            child.render(context);
        }

        if (clipChildren) {
            context.disableScissor();
        }

        renderEffects(context);
    }

    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();
        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            if (borderRadius > 0) {
                renderRoundedRect(context, getCalculatedX(), getCalculatedY(),
                        getCalculatedWidth(), getCalculatedHeight(),
                        borderRadius, bgColor);
            } else {
                context.fill(getCalculatedX(), getCalculatedY(),
                        getCalculatedX() + getCalculatedWidth(),
                        getCalculatedY() + getCalculatedHeight(), bgColor);
            }
        }
    }

    protected void renderEffects(DrawContext context) {
        if (isFocused() && hasFocusRing()) {
            renderFocusRing(context);
        }

        if (isHovered() && hasHoverEffect()) {
            renderHoverEffect(context);
        }
    }

    protected void renderRoundedRect(DrawContext context, int x, int y, int width, int height,
                                     int radius, int color) {
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }

        int clampedRadius = Math.min(radius, Math.min(width / 2, height / 2));

        context.fill(x, y + clampedRadius, x + width, y + height - clampedRadius, color);
        context.fill(x + clampedRadius, y, x + width - clampedRadius, y + clampedRadius, color);
        context.fill(x + clampedRadius, y + height - clampedRadius, x + width - clampedRadius, y + height, color);

        renderRoundedCorners(context, x, y, width, height, clampedRadius, color);
    }

    protected void renderRoundedCorners(DrawContext context, int x, int y, int width, int height,
                                        int radius, int color) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                double distance = Math.sqrt(i * i + j * j);
                if (distance <= radius) {
                    context.fill(x + radius - i - 1, y + radius - j - 1,
                            x + radius - i, y + radius - j, color);
                    int x2 = x + width - radius + i + 1;
                    context.fill(x + width - radius + i, y + radius - j - 1,
                            x2, y + radius - j, color);
                    int y2 = y + height - radius + j + 1;
                    context.fill(x + radius - i - 1, y + height - radius + j,
                            x + radius - i, y2, color);
                    context.fill(x + width - radius + i, y + height - radius + j,
                            x2, y2, color);
                }
            }
        }
    }

    protected void renderFocusRing(DrawContext context) {
        int x = getCalculatedX() - 2;
        int y = getCalculatedY() - 2;
        int width = getCalculatedWidth() + 4;
        int height = getCalculatedHeight() + 4;
        int radius = getBorderRadius() + 2;

        renderRoundedRect(context, x, y, width, height, radius, 0x80007ACC);
    }

    protected void renderHoverEffect(DrawContext context) {
        int overlayColor = 0x20FFFFFF;
        int radius = getBorderRadius();

        renderRoundedRect(context, getCalculatedX(), getCalculatedY(),
                getCalculatedWidth(), getCalculatedHeight(),
                radius, overlayColor);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseContainer> T setClipChildren(boolean clipChildren) {
        this.clipChildren = clipChildren;
        updateClipBounds();
        return (T) this;
    }

    public boolean isClipChildren() {
        return clipChildren;
    }
}