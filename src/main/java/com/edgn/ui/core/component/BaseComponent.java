package com.edgn.ui.core.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;

public abstract class BaseComponent implements IComponent {
    protected boolean visible = true;
    protected float opacity = 1.0f;
    protected int color = 0xFFFFFFFF;
    protected int preferredWidth = -1;
    protected int preferredHeight = -1;
    protected long lastUpdateTime = 0;
    protected boolean needsUpdate = false;

    @Override
    public void render(DrawContext context, int x, int y, int width, int height) {
        if (!visible || opacity <= 0.0f) return;

        boolean hasTransparency = opacity < 1.0f;
        if (hasTransparency && opacity < 0.95f) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

            renderComponent(context, x, y, width, height);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        } else {
            renderComponent(context, x, y, width, height);
        }
    }
    
    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (needsUpdate || currentTime - lastUpdateTime > getUpdateInterval()) {
            updateComponent(currentTime - lastUpdateTime);
            lastUpdateTime = currentTime;
            needsUpdate = false;
        }
    }

    protected abstract void renderComponent(DrawContext context, int x, int y, int width, int height);

    protected void updateComponent(long deltaTime) {}

    protected long getUpdateInterval() {
        return 16;
    }
    
    @Override
    public int getPreferredWidth() {
        if (preferredWidth >= 0) {
            return preferredWidth;
        }
        return calculatePreferredWidth();
    }
    
    @Override
    public int getPreferredHeight() {
        if (preferredHeight >= 0) {
            return preferredHeight;
        }
        return calculatePreferredHeight();
    }

    protected abstract int calculatePreferredWidth();
    protected abstract int calculatePreferredHeight();
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IComponent> T setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            onVisibilityChanged();
        }
        return (T) this;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IComponent> T setOpacity(float opacity) {
        float newOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        if (this.opacity != newOpacity) {
            this.opacity = newOpacity;
            onOpacityChanged();
        }
        return (T) this;
    }
    
    @Override
    public float getOpacity() {
        return opacity;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IComponent> T setColor(int color) {
        if (this.color != color) {
            this.color = color;
            onColorChanged();
        }
        return (T) this;
    }
    
    @Override
    public int getColor() {
        return color;
    }

    protected void onVisibilityChanged() {
        markNeedsUpdate();
    }
    protected void onOpacityChanged() {
        markNeedsUpdate();
    }
    protected void onColorChanged() {
        markNeedsUpdate();
    }
    protected void markNeedsUpdate() {
        this.needsUpdate = true;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseComponent> T setPreferredSize(int width, int height) {
        this.preferredWidth = Math.max(-1, width);
        this.preferredHeight = Math.max(-1, height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseComponent> T setPreferredWidth(int width) {
        this.preferredWidth = Math.max(-1, width);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseComponent> T setPreferredHeight(int height) {
        this.preferredHeight = Math.max(-1, height);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseComponent> T autoSize() {
        this.preferredWidth = -1;
        this.preferredHeight = -1;
        return (T) this;
    }

    protected int applyOpacity(int baseColor) {
        if (opacity >= 1.0f) return baseColor;
        
        int alpha = (baseColor >> 24) & 0xFF;
        int newAlpha = (int) (alpha * opacity);
        
        return (baseColor & 0x00FFFFFF) | (newAlpha << 24);
    }

    protected int mixColor(int color1, int color2, float ratio) {
        if (ratio <= 0) return color1;
        if (ratio >= 1) return color2;
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected void renderRoundedRect(DrawContext context, int x, int y, int width, int height, 
                                   int radius, int color) {
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, applyOpacity(color));
            return;
        }
        
        int finalColor = applyOpacity(color);
        int clampedRadius = Math.min(radius, Math.min(width / 2, height / 2));
        
        context.fill(x, y + clampedRadius, x + width, y + height - clampedRadius, finalColor);
        context.fill(x + clampedRadius, y, x + width - clampedRadius, y + clampedRadius, finalColor);
        context.fill(x + clampedRadius, y + height - clampedRadius, x + width - clampedRadius, y + height, finalColor);
        
        renderRoundedCorners(context, x, y, width, height, clampedRadius, finalColor);
    }
    

    protected void renderRoundedCorners(DrawContext context, int x, int y, int width, int height, 
                                      int radius, int color) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                double distance = Math.sqrt(i * i + j * j);
                if (distance <= radius) {
                    context.fill(x + radius - i - 1, y + radius - j - 1,
                               x + radius - i, y + radius - j, color);
                    context.fill(x + width - radius + i, y + radius - j - 1,
                               x + width - radius + i + 1, y + radius - j, color);
                    context.fill(x + radius - i - 1, y + height - radius + j,
                               x + radius - i, y + height - radius + j + 1, color);
                    context.fill(x + width - radius + i, y + height - radius + j,
                               x + width - radius + i + 1, y + height - radius + j + 1, color);
                }
            }
        }
    }

    protected void renderCircle(DrawContext context, int centerX, int centerY, int radius, int color) {
        int finalColor = applyOpacity(color);
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, finalColor);
                }
            }
        }
    }

    protected void renderLine(DrawContext context, int x1, int y1, int x2, int y2, int thickness, int color) {
        int finalColor = applyOpacity(color);
        
        if (y1 == y2) {
            int startX = Math.min(x1, x2);
            int endX = Math.max(x1, x2);
            context.fill(startX, y1, endX + 1, y1 + thickness, finalColor);
            return;
        }
        
        if (x1 == x2) {
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            context.fill(x1, startY, x1 + thickness, endY + 1, finalColor);
            return;
        }
        
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        int x = x1, y = y1;
        
        while (true) {
            context.fill(x, y, x + thickness, y + thickness, finalColor);
            
            if (x == x2 && y == y2) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
    
    @Override
    public abstract IComponent clone();

    protected void copyBaseTo(BaseComponent target) {
        target.visible = this.visible;
        target.opacity = this.opacity;
        target.color = this.color;
        target.preferredWidth = this.preferredWidth;
        target.preferredHeight = this.preferredHeight;
        target.lastUpdateTime = this.lastUpdateTime;
        target.needsUpdate = this.needsUpdate;
    }
}