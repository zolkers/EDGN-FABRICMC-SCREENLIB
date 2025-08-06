package com.edgn.ui.core;

import com.edgn.ui.css.CSSStyleApplier;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.rules.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class UIElement implements IElement {
    protected int x, y, width, height;
    protected final Set<StyleKey> classes = new HashSet<>();
    protected final UIStyleSystem styleSystem;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    protected LayoutConstraints constraints = new LayoutConstraints();
    protected ZIndex zIndex = ZIndex.CONTENT;
    protected TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    protected UIElement parent = null;

    protected int calculatedX, calculatedY, calculatedWidth, calculatedHeight;
    protected boolean constraintsDirty = true;
    protected InteractionBounds interactionBounds;

    private boolean stylesComputed = false;
    private CSSStyleApplier.ComputedStyles cachedStyles;

    protected Runnable onClickHandler;
    protected Runnable onMouseEnterHandler;
    protected Runnable onMouseLeaveHandler;
    protected Runnable onFocusGainedHandler;
    protected Runnable onFocusLostHandler;

    public UIElement(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        this.styleSystem = styleSystem;
        this.x = x; this.y = y; this.width = width; this.height = height;
        this.calculatedX = x; this.calculatedY = y;
        this.calculatedWidth = width; this.calculatedHeight = height;
        this.interactionBounds = new InteractionBounds(x, y, width, height);
        styleSystem.getEventManager().registerElement(this);
    }

    public void markConstraintsDirty() {
        if (constraintsDirty) return;
        this.constraintsDirty = true;
        this.stylesComputed = false;
    }

    public void updateConstraints() {
        if (!constraintsDirty) return;

        calculatedX = x + (parent != null ? getMarginLeft() : 0);
        calculatedY = y + (parent != null ? getMarginTop() : 0);
        calculatedWidth = width;
        calculatedHeight = height;

        interactionBounds = new InteractionBounds(calculatedX, calculatedY, calculatedWidth, calculatedHeight);

        constraintsDirty = false;
    }

    protected void calculateEffectiveBounds() {
        calculatedX = x;
        calculatedY = y;
        calculatedWidth = width;
        calculatedHeight = height;

        if (parent != null) {
            calculatedX += getMarginLeft();
            calculatedY += getMarginTop();

            int availableWidth = parent.getCalculatedWidth() - getMarginLeft() - getMarginRight();
            int availableHeight = parent.getCalculatedHeight() - getMarginTop() - getMarginBottom();

            calculatedWidth = Math.min(calculatedWidth, Math.max(0, availableWidth));
            calculatedHeight = Math.min(calculatedHeight, Math.max(0, availableHeight));
        }

        if (constraints != null) {
            if (constraints.getMinWidth() != null) {
                calculatedWidth = Math.max(calculatedWidth, constraints.getMinWidth());
            }
            if (constraints.getMaxWidth() != null) {
                calculatedWidth = Math.min(calculatedWidth, constraints.getMaxWidth());
            }
            if (constraints.getMinHeight() != null) {
                calculatedHeight = Math.max(calculatedHeight, constraints.getMinHeight());
            }
            if (constraints.getMaxHeight() != null) {
                calculatedHeight = Math.min(calculatedHeight, constraints.getMaxHeight());
            }
        }
    }

    protected void updateInteractionBounds() {
        int clipX = calculatedX;
        int clipY = calculatedY;
        int clipWidth = calculatedWidth;
        int clipHeight = calculatedHeight;

        if (parent != null) {
            InteractionBounds parentBounds = parent.getInteractionBounds();
            if (parentBounds != null) {
                int rightEdge = Math.min(clipX + clipWidth, parentBounds.maxX);
                int bottomEdge = Math.min(clipY + clipHeight, parentBounds.maxY);

                clipX = Math.max(clipX, parentBounds.minX);
                clipY = Math.max(clipY, parentBounds.minY);
                clipWidth = Math.max(0, rightEdge - clipX);
                clipHeight = Math.max(0, bottomEdge - clipY);
            }
        }

        interactionBounds = new InteractionBounds(clipX, clipY, clipWidth, clipHeight);
    }

    public boolean canInteract(double mouseX, double mouseY) {
        if (!visible || !enabled) return false;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        updateConstraints();
        return mouseX >= calculatedX && mouseX <= calculatedX + calculatedWidth &&
                mouseY >= calculatedY && mouseY <= calculatedY + calculatedHeight;
    }

    public int getCalculatedX() {
        if (constraintsDirty) updateConstraints();
        return calculatedX;
    }

    public int getCalculatedY() {
        if (constraintsDirty) updateConstraints();
        return calculatedY;
    }

    public int getCalculatedWidth() {
        if (constraintsDirty) updateConstraints();
        return calculatedWidth;
    }

    public int getCalculatedHeight() {
        if (constraintsDirty) updateConstraints();
        return calculatedHeight;
    }

    public InteractionBounds getInteractionBounds() { updateConstraints(); return interactionBounds; }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T addClass(StyleKey... keys) {
        Collections.addAll(classes, keys);
        markConstraintsDirty();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T removeClass(StyleKey key) {
        classes.remove(key);
        markConstraintsDirty();
        return (T) this;
    }

    public void setX(int x) {
        this.x = x;
        markConstraintsDirty();
    }

    public void setY(int y) {
        this.y = y;
        markConstraintsDirty();
    }

    public void setWidth(int width) {
        this.width = width;
        markConstraintsDirty();
    }

    public void setHeight(int height) {
        this.height = height;
        markConstraintsDirty();
    }

    public void setParent(UIElement parent) {
        this.parent = parent;
        markConstraintsDirty();
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setZIndex(ZIndex zIndex) {
        this.zIndex = zIndex != null ? zIndex : ZIndex.CONTENT;
        return (T) this;
    }

    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;
        if (onClickHandler != null) {
            onClickHandler.run();
            return true;
        }
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        return false;
    }

    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean onCharTyped(char chr, int modifiers) { return false; }
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) { return false; }

    public void onMouseMove(double mouseX, double mouseY) {}

    public void onMouseEnter() {
        hovered = true;
        if (onMouseEnterHandler != null) onMouseEnterHandler.run();
    }

    public void onMouseLeave() {
        hovered = false;
        if (onMouseLeaveHandler != null) onMouseLeaveHandler.run();
    }

    public void onFocusGained() {
        focused = true;
        if (onFocusGainedHandler != null) onFocusGainedHandler.run();
    }

    public void onFocusLost() {
        focused = false;
        if (onFocusLostHandler != null) onFocusLostHandler.run();
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onClick(Runnable handler) {
        this.onClickHandler = handler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onMouseEnter(Runnable handler) {
        this.onMouseEnterHandler = handler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onMouseLeave(Runnable handler) {
        this.onMouseLeaveHandler = handler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onFocusGained(Runnable handler) {
        this.onFocusGainedHandler = handler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T onFocusLost(Runnable handler) {
        this.onFocusLostHandler = handler;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setConstraints(LayoutConstraints constraints) {
        this.constraints = constraints;
        markConstraintsDirty();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIElement> T setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer != null ? textRenderer : MinecraftClient.getInstance().textRenderer;
        return (T) this;
    }

    public CSSStyleApplier.ComputedStyles getComputedStyles() {
        if (!stylesComputed || constraintsDirty) {
            cachedStyles = CSSStyleApplier.computeStyles(this);
            stylesComputed = true;
        }
        return cachedStyles;
    }
    protected int getBgColor() {
        return getComputedStyles().backgroundColor;
    }

    protected int getBorderRadius() {
        return getComputedStyles().borderRadius;
    }

    protected Shadow getShadow() {
        return getComputedStyles().shadow;
    }

    public int getPaddingTop() {
        return getComputedStyles().paddingTop;
    }

    public int getPaddingRight() {
        return getComputedStyles().paddingRight;
    }

    public int getPaddingBottom() {
        return getComputedStyles().paddingBottom;
    }

    public int getPaddingLeft() {
        return getComputedStyles().paddingLeft;
    }

    public int getMarginTop() {
        return getComputedStyles().marginTop;
    }

    public int getMarginRight() {
        return getComputedStyles().marginRight;
    }

    public int getMarginBottom() {
        return getComputedStyles().marginBottom;
    }

    public int getMarginLeft() {
        return getComputedStyles().marginLeft;
    }

    protected int getGap() {
        return getComputedStyles().gap;
    }

    protected int getTextColor() {
        return getComputedStyles().textColor;
    }

    protected boolean hasHoverEffect() {
        return getComputedStyles().hasHoverEffect;
    }

    protected boolean hasFocusRing() {
        return getComputedStyles().hasFocusRing;
    }

    public int getFlexGrow() {
        return getComputedStyles().flexGrow;
    }

    protected int getFlexShrink() {
        return getComputedStyles().flexShrink;
    }

    public boolean hasClass(StyleKey key) { return classes.contains(key); }
    public boolean isVisible() { return visible; }
    public boolean isEnabled() { return enabled; }
    public boolean isFocused() { return focused; }
    public boolean isHovered() { return hovered; }
    public UIStyleSystem getStyleSystem() { return styleSystem; }
    public UIElement getParent() { return parent; }
    public TextRenderer getTextRenderer() { return textRenderer; }
    public LayoutConstraints getConstraints() { return constraints; }

    public ZIndex getZIndex() {
        return zIndex;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getZIndexValue() {
        return zIndex.getValue();
    }

    public abstract void render(DrawContext context);

    public static class InteractionBounds {
        public final int minX, minY, maxX, maxY;
        public final int width, height;

        public InteractionBounds(int x, int y, int width, int height) {
            this.minX = x;
            this.minY = y;
            this.maxX = x + width;
            this.maxY = y + height;
            this.width = width;
            this.height = height;
        }

        public boolean contains(double x, double y) {
            return x >= minX && x < maxX && y >= minY && y < maxY;
        }

        public boolean isValid() {
            return width > 0 && height > 0;
        }
    }
}