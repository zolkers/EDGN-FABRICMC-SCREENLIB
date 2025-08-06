package com.edgn.ui.layout;


public record LayoutBox(int x, int y, int width, int height) {

    public boolean contains(int pointX, int pointY) {
        return pointX >= this.x && pointX < (this.x + this.width) && 
               pointY >= this.y && pointY < (this.y + this.height);
    }

    public boolean contains(double pointX, double pointY) {
        return pointX >= this.x && pointX < (this.x + this.width) && 
               pointY >= this.y && pointY < (this.y + this.height);
    }

    public boolean intersects(LayoutBox other) {
        return x < other.x + other.width && x + width > other.x &&
               y < other.y + other.height && y + height > other.y;
    }

    public LayoutBox intersection(LayoutBox other) {
        if (!intersects(other)) {
            return new LayoutBox(0, 0, 0, 0);
        }
        
        int newX = Math.max(x, other.x);
        int newY = Math.max(y, other.y);
        int newWidth = Math.min(x + width, other.x + other.width) - newX;
        int newHeight = Math.min(y + height, other.y + other.height) - newY;
        
        return new LayoutBox(newX, newY, Math.max(0, newWidth), Math.max(0, newHeight));
    }

    public LayoutBox expand(int deltaWidth, int deltaHeight) {
        return new LayoutBox(x, y, width + deltaWidth, height + deltaHeight);
    }

    public LayoutBox expand(int delta) {
        return expand(delta * 2, delta * 2);
    }

    public LayoutBox inset(int left, int top, int right, int bottom) {
        int newX = x + left;
        int newY = y + top;
        int newWidth = Math.max(0, width - left - right);
        int newHeight = Math.max(0, height - top - bottom);
        
        return new LayoutBox(newX, newY, newWidth, newHeight);
    }

    public LayoutBox inset(int inset) {
        return inset(inset, inset, inset, inset);
    }
    public LayoutBox inset(int horizontal, int vertical) {
        return inset(horizontal, vertical, horizontal, vertical);
    }
    public LayoutBox offset(int offsetX, int offsetY) {
        return new LayoutBox(x + offsetX, y + offsetY, width, height);
    }
    public LayoutBox withPosition(int newX, int newY) {
        return new LayoutBox(newX, newY, width, height);
    }
    public LayoutBox withSize(int newWidth, int newHeight) {
        return new LayoutBox(x, y, newWidth, newHeight);
    }
    public LayoutBox centerHorizontallyIn(LayoutBox container) {
        int centeredX = container.x + (container.width - width) / 2;
        return new LayoutBox(centeredX, y, width, height);
    }

    public LayoutBox centerVerticallyIn(LayoutBox container) {
        int centeredY = container.y + (container.height - height) / 2;
        return new LayoutBox(x, centeredY, width, height);
    }

    public LayoutBox centerIn(LayoutBox container) {
        int centeredX = container.x + (container.width - width) / 2;
        int centeredY = container.y + (container.height - height) / 2;
        return new LayoutBox(centeredX, centeredY, width, height);
    }

    public boolean isValid() {
        return width > 0 && height > 0;
    }

    public int area() {
        return width * height;
    }

    public int left() { return x; }
    public int top() { return y; }
    public int right() { return x + width; }
    public int bottom() { return y + height; }
    public int centerX() { return x + width / 2; }
    public int centerY() { return y + height / 2; }

    public static LayoutBox fromBounds(int minX, int minY, int maxX, int maxY) {
        return new LayoutBox(minX, minY, maxX - minX, maxY - minY);
    }

    public static LayoutBox empty() {
        return new LayoutBox(0, 0, 0, 0);
    }

    public static LayoutBox ofSize(int width, int height) {
        return new LayoutBox(0, 0, width, height);
    }
    
    @Override
    public String toString() {
        return String.format("LayoutBox{x=%d, y=%d, width=%d, height=%d}", x, y, width, height);
    }
}