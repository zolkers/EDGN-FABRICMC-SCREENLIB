package com.edgn.ui.core.item;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.css.CSSStyleApplier;
import com.edgn.ui.css.StyleKey;
import com.edgn.ui.css.UIStyleSystem;
import com.edgn.ui.utils.ColorUtils;

@SuppressWarnings("unused")
public abstract class BaseItem extends UIElement implements IItem {
    protected ItemState state = ItemState.NORMAL;
    protected long lastStateChange = 0;
    protected static final long ANIMATION_DURATION = 150;

    public enum ItemState {
        NORMAL, HOVERED, PRESSED, FOCUSED, DISABLED, ACTIVE
    }

    protected BaseItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    public ItemState getState() {
        return state;
    }

    protected void setState(ItemState newState) {
        if (state != newState) {
            state = newState;
            lastStateChange = System.currentTimeMillis();
            onStateChanged(state);
        }
    }

    protected void onStateChanged(ItemState newState) {}

    @Override
    public void onMouseEnter() {
        super.onMouseEnter();
        if (enabled && state != ItemState.PRESSED && state != ItemState.DISABLED) {
            setState(ItemState.HOVERED);
        }
    }

    @Override
    public void onMouseLeave() {
        super.onMouseLeave();
        if (enabled && state != ItemState.PRESSED && state != ItemState.DISABLED) {
            setState(ItemState.NORMAL);
        }
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        if (enabled && state != ItemState.DISABLED) {
            setState(ItemState.FOCUSED);
        }
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        if (enabled && state != ItemState.DISABLED) {
            setState(hovered ? ItemState.HOVERED : ItemState.NORMAL);
        }
    }

    protected float getAnimationProgress() {
        return Math.min(1.0f, (System.currentTimeMillis() - lastStateChange) / (float) ANIMATION_DURATION);
    }

    protected int getStateColor() {
        CSSStyleApplier.ComputedStyles styles = getComputedStyles();
        int baseColor = styles.getBackgroundColor();
        if (baseColor == 0) baseColor = ColorUtils.NamedColor.AQUAMARINE.toInt();

        return switch (state) {
            case HOVERED -> styles.isHasHoverEffect() ? brightenColor(baseColor, 20) : baseColor;
            case PRESSED -> darkenColor(baseColor);
            case FOCUSED -> styles.isHasFocusRing() ? ColorUtils.NamedColor.ALICEBLUE.toInt() : baseColor;
            case DISABLED -> fadeColor(baseColor);
            case ACTIVE -> brightenColor(baseColor, 30);
            default -> baseColor;
        };
    }

    protected int getAnimatedColor(int baseColor) {
        float progress = getAnimationProgress();

        if (hasHoverEffect() && state == ItemState.HOVERED) {
            int brightenAmount = (int) (20 * progress);
            return brightenColor(baseColor, brightenAmount);
        }

        return getStateColor();
    }

    protected float getAnimatedScale() {
        if (hasClass(StyleKey.HOVER_SCALE) && state == ItemState.HOVERED) {
            float progress = getAnimationProgress();
            float scaleValue = styleSystem.getValue(StyleKey.HOVER_SCALE) / 100.0f;
            return 1.0f + (scaleValue - 1.0f) * progress;
        }
        return 1.0f;
    }

    protected int brightenColor(int color, int amount) {
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        int a = (color >> 24) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected int darkenColor(int color) {
        int r = Math.max(0, ((color >> 16) & 0xFF) - 20);
        int g = Math.max(0, ((color >> 8) & 0xFF) - 20);
        int b = Math.max(0, (color & 0xFF) - 20);
        int a = (color >> 24) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected int fadeColor(int color) {
        int a = (int) (((color >> 24) & 0xFF) * (float) 0.5);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (state == ItemState.PRESSED) {
            setState(canInteract(mouseX, mouseY) ? ItemState.HOVERED : ItemState.NORMAL);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (!canInteract(mouseX, mouseY)) return false;

        if (enabled) {
            setState(ItemState.PRESSED);
        }

        return super.onMouseClick(mouseX, mouseY, button);
    }
}
