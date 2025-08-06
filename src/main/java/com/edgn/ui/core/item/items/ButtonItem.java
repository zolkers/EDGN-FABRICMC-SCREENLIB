package com.edgn.ui.core.item.items;

import com.edgn.ui.core.ElementHost;
import com.edgn.ui.core.component.components.TextComponent;
import com.edgn.ui.core.item.BaseItem;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

public class ButtonItem extends BaseItem implements ElementHost {
    private TextComponent textComponent;

    public ButtonItem(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
    }

    public ButtonItem setText(String text) {
        this.textComponent = new TextComponent(text, textRenderer);
        return this;
    }

    @Override
    public void initializeComponents() {}

    @Override
    public void updateComponents() {
        if (textComponent != null) {
            textComponent.update();
        }
    }

    @Override
    protected void renderContent(DrawContext context) {
        renderButtonBackground(context);

        if (textComponent != null) {
            int contentX = getCalculatedX() + getPaddingLeft();
            int contentY = getCalculatedY() + getPaddingTop();
            int contentWidth = getCalculatedWidth() - getPaddingLeft() - getPaddingRight();
            int contentHeight = getCalculatedHeight() - getPaddingTop() - getPaddingBottom();

            textComponent.render(context, contentX, contentY, contentWidth, contentHeight);
        }
    }

    private void renderButtonBackground(DrawContext context) {
        int bgColor = getBgColor();

        if (isHovered()) {
            bgColor = mixColor(bgColor, 0xFFFFFFFF, 0.1f);
        }
        if (isFocused()) {
            bgColor = mixColor(bgColor, 0xFF0D6EFD, 0.2f);
        }

        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            renderRoundedRect(context, getCalculatedX(), getCalculatedY(),
                    getCalculatedWidth(), getCalculatedHeight(),
                    borderRadius, bgColor);
        }
    }

    private int mixColor(int color1, int color2, float ratio) {
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
}