package com.edgn.ui.core;

import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.renderer.FontRenderer;
import com.edgn.ui.css.CSSStyleApplier;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.css.values.Shadow;
import com.edgn.ui.layout.LayoutConstraints;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class UIElement implements IElement {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected final Set<StyleKey> classes = new HashSet<>();
    protected final UIStyleSystem styleSystem;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    protected LayoutConstraints constraints = new LayoutConstraints();
    protected ZIndex zIndex = ZIndex.CONTENT;
    protected FontRenderer fontRenderer = TextComponent.getDefaultFontRenderer();
    protected UIElement parent = null;

    protected int calculatedX;
    protected int calculatedY;
    protected int calculatedWidth;
    protected int calculatedHeight;
    protected boolean constraintsDirty = true;
    protected InteractionBounds interactionBounds;

    private boolean stylesComputed = false;
    private CSSStyleApplier.ComputedStyles cachedStyles;
    private boolean rendered = false;

    protected Runnable onClickHandler;
    protected Runnable onMouseEnterHandler;
    protected Runnable onMouseLeaveHandler;
    protected Runnable onFocusGainedHandler;
    protected Runnable onFocusLostHandler;

    protected boolean ignoreParentScroll = false;

    private String stateKey;


    protected UIElement(UIStyleSystem styleSystem, int x, int y, int width, int height) {
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

            calculatedWidth  = Math.clamp(calculatedWidth,  0, Math.max(0, availableWidth));
            calculatedHeight = Math.clamp(calculatedHeight, 0, Math.max(0, availableHeight));
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
        int ox = calculatedX;
        int oy = calculatedY;
        int ow = calculatedWidth;
        int oh = calculatedHeight;

        if (parent != null) {
            ox += parent.getChildInteractionOffsetX(this);
            oy += parent.getChildInteractionOffsetY(this);
        }

        int clipX = ox;
        int clipY = oy;
        int clipWidth = ow;
        int clipHeight = oh;

        if (parent != null) {
            InteractionBounds parentBounds = parent.interactionBounds;
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

    public boolean ignoresParentScroll() { return ignoreParentScroll; }
    public UIElement setIgnoreParentScroll(boolean value) { this.ignoreParentScroll = value; return this; }

    public boolean canInteract(double mouseX, double mouseY) {
        if (!visible || !enabled || !rendered) return false;
        updateConstraints();
        updateInteractionBounds();
        InteractionBounds b = interactionBounds;
        return b != null && b.contains(mouseX, mouseY);
    }

    public boolean contains(double mouseX, double mouseY) {
        updateConstraints();
        updateInteractionBounds();
        InteractionBounds b = interactionBounds;
        return b != null && b.contains(mouseX, mouseY);
    }

    public boolean isInInteractionZone(double mouseX, double mouseY) {
        if (!rendered) return false;
        updateConstraints();
        updateInteractionBounds();
        InteractionBounds bounds = interactionBounds;
        return bounds != null && bounds.contains(mouseX, mouseY);
    }

    protected void markAsRendered() { this.rendered = true; }

    public void markAsNotRendered() {
        this.rendered = false;
        if (hovered) onMouseLeave();
        if (focused) styleSystem.getEventManager().setFocus(null);
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

    public InteractionBounds getInteractionBounds() {
        updateConstraints();
        updateInteractionBounds();
        return interactionBounds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T addClass(StyleKey... keys) {
        Collections.addAll(classes, keys);
        markConstraintsDirty();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T removeClass(StyleKey key) {
        classes.remove(key);
        markConstraintsDirty();
        return (T) this;
    }

    public void setX(int x) { this.x = x; markConstraintsDirty(); }
    public void setY(int y) { this.y = y; markConstraintsDirty(); }
    public void setWidth(int width) { this.width = width; markConstraintsDirty(); }
    public void setHeight(int height) { this.height = height; markConstraintsDirty(); }
    public void setParent(UIElement parent) { this.parent = parent; markConstraintsDirty(); }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setZIndex(ZIndex zIndex) { this.zIndex = zIndex != null ? zIndex : ZIndex.CONTENT; return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setZIndex(ZIndex.Layer layer) { this.zIndex = new ZIndex(layer); return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setZIndex(ZIndex.Layer layer, int priority) { this.zIndex = new ZIndex(layer, priority); return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setZIndex(int intZIndex) {
        if (intZIndex < ZIndex.Layer.BACKGROUND.getBaseValue()) {
            this.zIndex = ZIndex.backgroundIndex(intZIndex - ZIndex.Layer.BACKGROUND.getBaseValue());
        } else if (intZIndex < ZIndex.Layer.CONTENT.getBaseValue()) {
            this.zIndex = ZIndex.contentIndex(intZIndex);
        } else if (intZIndex < ZIndex.Layer.OVERLAY.getBaseValue()) {
            this.zIndex = ZIndex.overlayIndex(intZIndex - ZIndex.Layer.OVERLAY.getBaseValue());
        } else if (intZIndex < ZIndex.Layer.MODAL.getBaseValue()) {
            this.zIndex = ZIndex.modalIndex(intZIndex - ZIndex.Layer.MODAL.getBaseValue());
        } else if (intZIndex < ZIndex.Layer.TOOLTIP.getBaseValue()) {
            this.zIndex = ZIndex.tooltipIndex(intZIndex - ZIndex.Layer.TOOLTIP.getBaseValue());
        } else {
            this.zIndex = ZIndex.debugIndex(intZIndex - ZIndex.Layer.DEBUG.getBaseValue());
        }
        return (T) this;
    }

    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;
        if (onClickHandler != null) { onClickHandler.run(); return true; }
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) { return false; }
    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) { return false; }
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) { return false; }

    public void onResize(MinecraftClient client, int width, int height) {}

    public boolean onCharTyped(char chr, int modifiers) { return false; }
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) { return false; }

    public void onMouseMove(double mouseX, double mouseY) {}

    public void onMouseEnter() {
        hovered = true;
        if (onMouseEnterHandler != null) onMouseEnterHandler.run();
    }

    public void onTick() {

    }

    public void onMouseLeave() {
        hovered = false;
        if (onMouseLeaveHandler != null) onMouseLeaveHandler.run();
    }

    public void onFocusGained() {
        if (!rendered) return;
        focused = true;
        if (onFocusGainedHandler != null) onFocusGainedHandler.run();
    }

    public void onFocusLost() {
        focused = false;
        if (onFocusLostHandler != null) onFocusLostHandler.run();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T onClick(Runnable handler) { this.onClickHandler = handler; return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T onMouseEnter(Runnable handler) { this.onMouseEnterHandler = handler; return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T onMouseLeave(Runnable handler) { this.onMouseLeaveHandler = handler; return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T onFocusGained(Runnable handler) { this.onFocusGainedHandler = handler; return (T) this; }
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T onFocusLost(Runnable handler) { this.onFocusLostHandler = handler; return (T) this; }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setConstraints(LayoutConstraints constraints) { this.constraints = constraints; markConstraintsDirty(); return (T) this; }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setVisible(boolean visible) {
        boolean wasVisible = this.visible;
        this.visible = visible;
        if (wasVisible && !visible) markAsNotRendered();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            if (hovered) onMouseLeave();
            if (focused) styleSystem.getEventManager().setFocus(null);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IElement> T setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer != null ? fontRenderer : TextComponent.getDefaultFontRenderer();
        return (T) this;
    }

    public CSSStyleApplier.ComputedStyles getComputedStyles() {
        if (!stylesComputed || constraintsDirty) {
            cachedStyles = CSSStyleApplier.computeStyles(this);
            stylesComputed = true;
        }
        return cachedStyles;
    }

    protected int getBgColor() { return getComputedStyles().getBackgroundColor(); }
    protected int getBorderRadius() { return getComputedStyles().getBorderRadius(); }
    protected Shadow getShadow() { return getComputedStyles().getShadow(); }

    public int getPaddingTop() { return getComputedStyles().getPaddingTop(); }
    public int getPaddingRight() { return getComputedStyles().getPaddingRight(); }
    public int getPaddingBottom() { return getComputedStyles().getPaddingBottom(); }
    public int getPaddingLeft() { return getComputedStyles().getPaddingLeft(); }
    public int getMarginTop() { return getComputedStyles().getMarginTop(); }
    public int getMarginRight() { return getComputedStyles().getMarginRight(); }
    public int getMarginBottom() { return getComputedStyles().getMarginBottom(); }
    public int getMarginLeft() { return getComputedStyles().getMarginLeft(); }

    protected int getGap() { return getComputedStyles().getGap(); }
    protected int getTextColor() { return getComputedStyles().getTextColor(); }
    protected boolean hasHoverEffect() { return getComputedStyles().isHasHoverEffect(); }
    protected boolean hasFocusRing() { return getComputedStyles().isHasFocusRing(); }

    public int getFlexGrow() { return getComputedStyles().getFlexGrow(); }
    protected int getFlexShrink() { return getComputedStyles().getFlexShrink(); }

    public boolean hasClass(StyleKey key) { return classes.contains(key); }
    public boolean isVisible() { return visible; }
    public boolean isEnabled() { return enabled; }
    public boolean isFocused() { return focused; }
    public boolean isHovered() { return hovered; }
    public void setHovered(boolean v) {
        if (hovered == v) return;
        hovered = v;
    }
    public boolean isRendered() { return rendered; }
    public UIStyleSystem getStyleSystem() { return styleSystem; }
    public UIElement getParent() { return parent; }
    public FontRenderer getFontRenderer() { return fontRenderer; }
    public LayoutConstraints getConstraints() { return constraints; }

    public ZIndex getZIndex() { return zIndex; }
    public int getZIndexValue() { return zIndex.getValue(); }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public final void renderElement(DrawContext context) {
        if (!visible) {
            markAsNotRendered();
            return;
        }
        markAsRendered();
        render(context);
    }

    public abstract void render(DrawContext context);

    public static class InteractionBounds {
        public final int minX;
        public final int minY;
        public final int maxX;
        public final int maxY;
        public final int width;
        public final int height;

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

        public boolean isValid() { return width > 0 && height > 0; }
    }

}
