package com.edgn.ui.layout;

public class ZIndex implements Comparable<ZIndex> {
    public static final ZIndex BACKGROUND = new ZIndex(Layer.BACKGROUND, 0);
    public static final ZIndex CONTENT = new ZIndex(Layer.CONTENT, 0);
    public static final ZIndex OVERLAY = new ZIndex(Layer.OVERLAY, 0);
    public static final ZIndex MODAL = new ZIndex(Layer.MODAL, 0);
    public static final ZIndex TOOLTIP = new ZIndex(Layer.TOOLTIP, 0);
    public static final ZIndex DEBUG = new ZIndex(Layer.DEBUG, 0);

    public enum Layer {
        BACKGROUND(-1000),
        CONTENT(0),
        OVERLAY(1000),
        MODAL(2000),
        TOOLTIP(3000),
        DEBUG(9000);

        private final int baseValue;

        Layer(int baseValue) {
            this.baseValue = baseValue;
        }

        public int getBaseValue() {
            return baseValue;
        }
    }

    private final Layer layer;
    private final int priority;

    public ZIndex(Layer layer, int priority) {
        this.layer = layer;
        this.priority = priority;
    }

    public ZIndex(Layer layer) {
        this(layer, 0);
    }

    public ZIndex above(int delta) {
        return new ZIndex(this.layer, this.priority + delta);
    }
    public ZIndex below(int delta) {
        return new ZIndex(this.layer, this.priority - delta);
    }

    public int getValue() {
        return layer.getBaseValue() + priority;
    }

    @Override
    public int compareTo(ZIndex other) {
        return Integer.compare(this.getValue(), other.getValue());
    }

    public Layer getLayer() {
        return layer;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZIndex zIndex = (ZIndex) obj;
        return priority == zIndex.priority && layer == zIndex.layer;
    }

    @Override
    public int hashCode() {
        return layer.hashCode() * 31 + priority;
    }

    @Override
    public String toString() {
        return String.format("ZIndex{layer=%s, priority=%d, value=%d}", 
                           layer, priority, getValue());
    }

    public static ZIndex background(int priority) {
        return new ZIndex(Layer.BACKGROUND, priority);
    }

    public static ZIndex content(int priority) {
        return new ZIndex(Layer.CONTENT, priority);
    }

    public static ZIndex overlay(int priority) {
        return new ZIndex(Layer.OVERLAY, priority);
    }

    public static ZIndex modal(int priority) {
        return new ZIndex(Layer.MODAL, priority);
    }

    public static ZIndex tooltip(int priority) {
        return new ZIndex(Layer.TOOLTIP, priority);
    }

    public static ZIndex debug(int priority) {
        return new ZIndex(Layer.DEBUG, priority);
    }
}