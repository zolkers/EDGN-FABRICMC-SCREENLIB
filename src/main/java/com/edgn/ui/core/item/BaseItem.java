package com.edgn.ui.core.item;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.core.container.IContainer;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.layout.ClipBounds;
import net.minecraft.client.gui.DrawContext;

public abstract class BaseItem extends UIElement implements IItem {
    protected ClipBounds visibleBounds;
    
    public BaseItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        updateInteractionBounds();
    }
    
    @Override
    public boolean canInteract(double mouseX, double mouseY) {
        return canInteractAt(mouseX, mouseY);
    }
    
    @Override
    public boolean canInteractAt(double mouseX, double mouseY) {
        if (!visible || !enabled) return false;
        
        updateInteractionBounds();
        return visibleBounds != null && visibleBounds.contains(mouseX, mouseY);
    }
    
    @Override
    public ClipBounds getVisibleBounds() {
        updateInteractionBounds();
        return visibleBounds;
    }
    
    @Override
    public void updateInteractionBounds() {
        int elementX = getCalculatedX();
        int elementY = getCalculatedY();
        int elementWidth = getCalculatedWidth();
        int elementHeight = getCalculatedHeight();
        int borderRadius = getBorderRadius();
        
        ClipBounds elementBounds = new ClipBounds(elementX, elementY, elementWidth, elementHeight, borderRadius);
        
        if (parent == null) {
            visibleBounds = elementBounds;
            return;
        }
        
        ClipBounds parentBounds;
        if (parent instanceof IContainer container) {
            parentBounds = container.getClipBounds();
        } else {
            parentBounds = new ClipBounds(parent.getCalculatedX(), parent.getCalculatedY(),
                                        parent.getCalculatedWidth(), parent.getCalculatedHeight());
        }
        
        if (parentBounds == null || !parentBounds.isValid()) {
            visibleBounds = ClipBounds.INVALID;
            return;
        }
        
        visibleBounds = elementBounds.intersect(parentBounds);
    }
    
    @Override
    public float getVisibilityRatio() {
        updateInteractionBounds();
        
        if (visibleBounds == null || !visibleBounds.isValid()) {
            return 0.0f;
        }
        
        int totalArea = getCalculatedWidth() * getCalculatedHeight();
        if (totalArea <= 0) return 0.0f;
        
        int visibleArea = visibleBounds.getWidth() * visibleBounds.getHeight();
        return (float) visibleArea / totalArea;
    }
    
    @Override
    public boolean isFullyVisible() {
        return getVisibilityRatio() >= 0.99f;
    }
    
    @Override
    public boolean isPartiallyVisible() {
        float ratio = getVisibilityRatio();
        return ratio > 0.0f && ratio < 1.0f;
    }
    
    @Override
    public void updateConstraints() {
        super.updateConstraints();
        updateInteractionBounds();
    }
    
    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteractAt(mouseX, mouseY)) return false;
        
        if (onClickHandler != null) {
            onClickHandler.run();
            return true;
        }
        return false;
    }
    
    @Override
    public void onMouseEnter() {
        if (visibleBounds != null && visibleBounds.isValid()) {
            super.onMouseEnter();
        }
    }
    
    @Override
    public void render(DrawContext context) {
        if (!visible) return;
        
        updateInteractionBounds();
        
        if (visibleBounds == null || !visibleBounds.isValid()) {
            return;
        }
        
        visibleBounds.applyScissor(context);
        
        renderContent(context);
        
        renderEffects(context);
        
        context.disableScissor();
    }
    
    /**
     * Méthode abstraite pour le rendu du contenu spécifique de l'élément
     */
    protected abstract void renderContent(DrawContext context);
    
    /**
     * Render les effets visuels (focus, hover, etc.)
     */
    protected void renderEffects(DrawContext context) {
        if (isFocused() && hasFocusRing()) {
            renderFocusRing(context);
        }
        
        if (isHovered() && hasHoverEffect()) {
            renderHoverEffect(context);
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
    
    protected void renderRoundedRect(DrawContext context, int x, int y, int width, int height, 
                                   int radius, int color) {
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }
        
        int clampedRadius = Math.min(radius, Math.min(width / 2, height / 2));
        
        // Corps principal
        context.fill(x, y + clampedRadius, x + width, y + height - clampedRadius, color);
        context.fill(x + clampedRadius, y, x + width - clampedRadius, y + clampedRadius, color);
        context.fill(x + clampedRadius, y + height - clampedRadius, x + width - clampedRadius, y + height, color);
        
        // Coins arrondis
        renderRoundedCorners(context, x, y, width, height, clampedRadius, color);
    }
    
    protected void renderRoundedCorners(DrawContext context, int x, int y, int width, int height, 
                                      int radius, int color) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                double distance = Math.sqrt(i * i + j * j);
                if (distance <= radius) {
                    // Coin supérieur gauche
                    context.fill(x + radius - i - 1, y + radius - j - 1, 
                               x + radius - i, y + radius - j, color);
                    // Coin supérieur droit
                    context.fill(x + width - radius + i, y + radius - j - 1, 
                               x + width - radius + i + 1, y + radius - j, color);
                    // Coin inférieur gauche
                    context.fill(x + radius - i - 1, y + height - radius + j, 
                               x + radius - i, y + height - radius + j + 1, color);
                    // Coin inférieur droit
                    context.fill(x + width - radius + i, y + height - radius + j, 
                               x + width - radius + i + 1, y + height - radius + j + 1, color);
                }
            }
        }
    }
}