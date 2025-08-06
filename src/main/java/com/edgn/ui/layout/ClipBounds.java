package com.edgn.ui.layout;

public class ClipBounds {
    private final int x, y, width, height;
    private final int borderRadius;
    private final boolean isValid;
    
    public static final ClipBounds INVALID = new ClipBounds(0, 0, 0, 0, 0);
    
    public ClipBounds(int x, int y, int width, int height, int borderRadius) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
        this.borderRadius = Math.max(0, borderRadius);
        this.isValid = this.width > 0 && this.height > 0;
    }
    
    public ClipBounds(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }

    public static ClipBounds fromLayoutBox(LayoutBox box, int borderRadius) {
        return new ClipBounds(box.x(), box.y(), box.width(), box.height(), borderRadius);
    }
    
    public static ClipBounds fromLayoutBox(LayoutBox box) {
        return fromLayoutBox(box, 0);
    }

    public boolean contains(double pointX, double pointY) {
        if (!isValid || pointX < x || pointX >= x + width || pointY < y || pointY >= y + height) {
            return false;
        }
        
        if (borderRadius <= 0) {
            return true;
        }
        
        return isInsideRoundedRect(pointX, pointY);
    }

    public ClipBounds intersect(ClipBounds other) {
        if (!isValid || !other.isValid) {
            return INVALID;
        }
        
        int newX = Math.max(x, other.x);
        int newY = Math.max(y, other.y);
        int newWidth = Math.min(x + width, other.x + other.width) - newX;
        int newHeight = Math.min(y + height, other.y + other.height) - newY;
        
        if (newWidth <= 0 || newHeight <= 0) {
            return INVALID;
        }
        
        int newBorderRadius = Math.min(borderRadius, other.borderRadius);
        
        return new ClipBounds(newX, newY, newWidth, newHeight, newBorderRadius);
    }

    public boolean intersects(ClipBounds other) {
        if (!isValid || !other.isValid) {
            return false;
        }
        
        return x < other.x + other.width && x + width > other.x &&
               y < other.y + other.height && y + height > other.y;
    }

    public void applyScissor(net.minecraft.client.gui.DrawContext context) {
        if (isValid) {
            context.enableScissor(x, y, x + width, y + height);
        }
    }

    private boolean isInsideRoundedRect(double pointX, double pointY) {
        double relX = pointX - x;
        double relY = pointY - y;
        
        boolean inLeftRadius = relX < borderRadius;
        boolean inRightRadius = relX > width - borderRadius;
        boolean inTopRadius = relY < borderRadius;
        boolean inBottomRadius = relY > height - borderRadius;
        
        if (!inLeftRadius && !inRightRadius || !inTopRadius && !inBottomRadius) {
            return true;
        }
        
        double centerX, centerY;
        
        if (inLeftRadius && inTopRadius) {
            centerX = borderRadius;
            centerY = borderRadius;
        } else if (inRightRadius && inTopRadius) {
            centerX = width - borderRadius;
            centerY = borderRadius;
        } else if (inLeftRadius) {
            centerX = borderRadius;
            centerY = height - borderRadius;
        } else {
            centerX = width - borderRadius;
            centerY = height - borderRadius;
        }
        
        double dx = relX - centerX;
        double dy = relY - centerY;
        return dx * dx + dy * dy <= borderRadius * borderRadius;
    }

    public ClipBounds inset(int inset) {
        return inset(inset, inset, inset, inset);
    }
    
    public ClipBounds inset(int left, int top, int right, int bottom) {
        int newX = x + left;
        int newY = y + top;
        int newWidth = width - left - right;
        int newHeight = height - top - bottom;
        
        if (newWidth <= 0 || newHeight <= 0) {
            return INVALID;
        }
        
        // Ajuste le border radius si nécessaire
        int maxInset = Math.max(Math.max(left, right), Math.max(top, bottom));
        int adjustedRadius = Math.max(0, borderRadius - maxInset);
        
        return new ClipBounds(newX, newY, newWidth, newHeight, adjustedRadius);
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBorderRadius() { return borderRadius; }
    public boolean isValid() { return isValid; }
    
    public int getMinX() { return x; }
    public int getMinY() { return y; }
    public int getMaxX() { return x + width; }
    public int getMaxY() { return y + height; }
    
    @Override
    public String toString() {
        return String.format("ClipBounds{x=%d, y=%d, width=%d, height=%d, radius=%d, valid=%s}", 
                           x, y, width, height, borderRadius, isValid);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClipBounds that = (ClipBounds) obj;
        return x == that.x && y == that.y && width == that.width && 
               height == that.height && borderRadius == that.borderRadius;
    }
    
    @Override
    public int hashCode() {
        return x * 31 + y * 31 + width * 31 + height * 31 + borderRadius;
    }
}