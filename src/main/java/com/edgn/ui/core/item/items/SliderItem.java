package com.edgn.ui.core.item.items;

import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.core.container.components.TextComponent;
import com.edgn.ui.core.models.slider.SliderModel;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.utils.DrawingUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.Objects;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SliderItem<N extends Number> extends BaseItem {

    public enum ValuePosition { TOP, BOTTOM, LEFT, RIGHT }

    public interface ValueFormatter<T extends Number> {
        String format(T value);
    }

    private final SliderModel<N> model;
    private TextComponent valueText;
    private ValuePosition valuePosition = ValuePosition.TOP;
    private ValueFormatter<N> formatter = Objects::toString;

    private int trackHeight = 6;
    private int trackRadius = 3;
    private int thumbSize = 10;
    private int gapBetweenTrackAndLabel = 4;

    private int trackColor = 0xFF3A3A3A;
    private int fillColor  = 0xFF6AA7FF;
    private int thumbColor = 0xFFFFFFFF;

    private boolean dragging = false;

    public SliderItem(UIStyleSystem styleSystem,
                      int x, int y, int width, int height,
                      SliderModel<N> model) {
        super(styleSystem, x, y, width, height);
        this.model = Objects.requireNonNull(model, "model");
        this.valueText = new TextComponent(formatter.format(model.get()));
    }

    public SliderItem<N> valuePosition(ValuePosition pos) { this.valuePosition = pos; return this; }
    public SliderItem<N> formatter(ValueFormatter<N> f) { this.formatter = Objects.requireNonNull(f); refreshLabel(); return this; }

    public SliderItem<N> trackHeight(int px) { this.trackHeight = Math.max(2, px); return this; }
    public SliderItem<N> trackRadius(int px) { this.trackRadius = Math.max(0, px); return this; }
    public SliderItem<N> thumbSize(int px) { this.thumbSize = Math.max(6, px); return this; }
    public SliderItem<N> gap(int px) { this.gapBetweenTrackAndLabel = Math.max(0, px); return this; }

    public SliderItem<N> trackColor(int argb) { this.trackColor = argb; return this; }
    public SliderItem<N> fillColor(int argb) { this.fillColor = argb; return this; }
    public SliderItem<N> thumbColor(int argb) { this.thumbColor = argb; return this; }

    public SliderModel<N> getModel() { return model; }
    public N getValue() { return model.get(); }
    public SliderItem<N> setValue(N v) { model.set(v); refreshLabel(); return this; }

    public ValuePosition getValuePosition() { return valuePosition; }
    public TextComponent getValueTextComponent() { return valueText; }

    public SliderItem<N> textColor(int color) { valueText.color(color); return this; }
    public SliderItem<N> textBold() { valueText.bold(); return this; }
    public SliderItem<N> textItalic() { valueText.italic(); return this; }
    public SliderItem<N> textShadow() { valueText.shadow(); return this; }
    public SliderItem<N> textGlow() { valueText.glow(); return this; }
    public SliderItem<N> textGlow(int color) { valueText.glow(color); return this; }
    public SliderItem<N> textPulse() { valueText.pulse(); return this; }
    public SliderItem<N> textWave() { valueText.wave(); return this; }
    public SliderItem<N> textTypewriter() { valueText.typewriter(); return this; }
    public SliderItem<N> textRainbow() { valueText.rainbow(); return this; }

    @Override
    public void render(DrawContext context) {
        if (!visible) return;

        updateConstraints();
        int cx = getCalculatedX();
        int cy = getCalculatedY();
        int cw = getCalculatedWidth();
        int ch = getCalculatedHeight();

        int labelW = cw;
        int labelH = ch;
        int trackX = cx;
        int trackY = cy;
        int trackW = cw;
        int trackH = trackHeight;

        switch (valuePosition) {
            case TOP -> {
                labelH = Math.max(0, ch - trackHeight - gapBetweenTrackAndLabel);
                trackY = cy + labelH + gapBetweenTrackAndLabel;
            }
            case BOTTOM -> labelH = Math.max(0, ch - trackHeight - gapBetweenTrackAndLabel);
            case LEFT -> {
                labelW = Math.max(40, (int)(cw * 0.35f));
                trackX = cx + labelW + gapBetweenTrackAndLabel;
                trackW = Math.max(1, cw - labelW - gapBetweenTrackAndLabel);
                trackY = cy + (ch - trackHeight) / 2;
            }
            case RIGHT -> {
                labelW = Math.max(40, (int)(cw * 0.35f));
                trackW = Math.max(1, cw - labelW - gapBetweenTrackAndLabel);
                trackY = cy + (ch - trackHeight) / 2;
            }
        }

        DrawingUtils.drawRoundedRect(context, trackX, trackY, trackW, trackH, trackRadius, trackColor);

        double t = model.valueToRatio(model.get());
        t = Math.clamp(t, 0.0, 1.0);

        int filledW = (int) Math.round(t * trackW);
        if (filledW > 0) {
            DrawingUtils.drawRoundedRect(context, trackX, trackY, filledW, trackH, trackRadius, fillColor);
        }

        int thumbX = trackX + filledW - thumbSize / 2;
        int thumbY = trackY + (trackH - thumbSize) / 2;
        int thumbR = Math.max(2, thumbSize / 2);
        DrawingUtils.drawRoundedRect(context, thumbX, thumbY, thumbSize, thumbSize, thumbR, thumbColor);

        refreshLabel();
        int labelX = cx;
        int labelY = cy;
        int labelMaxW = (valuePosition == ValuePosition.LEFT || valuePosition == ValuePosition.RIGHT) ? labelW : cw;
        int labelMaxH = (valuePosition == ValuePosition.TOP || valuePosition == ValuePosition.BOTTOM) ? labelH : ch;

        switch (valuePosition) {
            case BOTTOM -> labelY = cy + trackHeight + gapBetweenTrackAndLabel;
            case RIGHT -> labelX = cx + cw - labelW;
            default -> {/* other cases simply dont need recalc */}
        }
        valueText.render(context, labelX, labelY, labelMaxW, labelMaxH);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!enabled || !canInteract(mouseX, mouseY)) return false;
        dragging = true;
        updateFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!enabled) return false;
        boolean wasDragging = dragging;
        dragging = false;
        return wasDragging;
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!enabled || !dragging) return false;
        updateFromMouse(mouseX);
        return true;
    }

    private void updateFromMouse(double mouseX) {
        int cx = getCalculatedX();
        int cw = getCalculatedWidth();

        int trackX = cx;
        int trackW = cw;

        if (valuePosition == ValuePosition.LEFT) {
            int labelW = Math.max(40, (int)(cw * 0.35f));
            trackX = cx + labelW + gapBetweenTrackAndLabel;
            trackW = Math.max(1, cw - labelW - gapBetweenTrackAndLabel);
        } else if (valuePosition == ValuePosition.RIGHT) {
            int labelW = Math.max(40, (int)(cw * 0.35f));
            trackW = Math.max(1, cw - labelW - gapBetweenTrackAndLabel);
        }

        double t = (mouseX - trackX) / trackW;
        t = Math.clamp(t, 0.0, 1.0);
        model.setFromRatio(t);
        refreshLabel();
    }

    private void refreshLabel() {
        String s = formatter.format(model.get());
        this.valueText = this.valueText.cloneWithNewText(s);
    }

    @Override
    public String toString() {
        return String.format("SliderItem{value=%s, min=%s, max=%s, position=%s, bounds=[%d,%d,%d,%d], visible=%b, enabled=%b}",
                model.get(),
                model.min(),
                model.max(),
                valuePosition,
                getCalculatedX(),
                getCalculatedY(),
                getCalculatedWidth(),
                getCalculatedHeight(),
                isVisible(),
                isEnabled()
        );
    }
}
