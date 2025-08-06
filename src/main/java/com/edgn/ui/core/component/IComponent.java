package com.edgn.ui.core.component;

import net.minecraft.client.gui.DrawContext;

public interface IComponent {
    void render(DrawContext context, int x, int y, int width, int height);
    int getPreferredWidth();
    int getPreferredHeight();
    boolean isVisible();
    <T extends IComponent> T setVisible(boolean visible);
    <T extends IComponent> T setOpacity(float opacity);
    float getOpacity();
    <T extends IComponent> T setColor(int color);
    int getColor();
    IComponent clone();

    void update();
}