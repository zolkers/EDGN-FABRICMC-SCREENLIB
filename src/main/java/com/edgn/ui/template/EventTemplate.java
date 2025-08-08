package com.edgn.ui.template;

import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * The core of the framework and main entrypoint, if you want to use this framework,
 * you are going to be forced to use this one template.
 * The role of EventTemplate.java is to hook the events, those events are designed
 * to perfectly manage the {@link com.edgn.ui.core.UIElement} behaviors
 * Each event from Screen is final as we don't want the user to do a mistake and break
 * the whole mod by forgetting to call super.method()
 * @author EDGN
 */
@SuppressWarnings("unused")
public abstract class EventTemplate extends Screen {
    protected UIStyleSystem uiSystem;

    protected EventTemplate(Text title) {
        super(title);
        this.uiSystem = new UIStyleSystem();
    }

    //these are the methods to use if you ever want to do something with the events
    protected void onRemove() {}
    protected void onInit() {}
    protected void onResize(MinecraftClient client, int width, int height){}
    protected void onMouseClicked(double mouseX, double mouseY, int button) {}
    protected void onMouseReleased(double mouseX, double mouseY, int button) {}
    protected void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {}
    protected void onMouseMoved(double mouseX, double mouseY) {}
    protected void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){}
    protected void onKeyPressed(int keyCode, int scanCode, int modifiers) {}
    protected void onCharTyped(char chr, int modifiers) {}
    protected void onTick(){}

    //from this point, there's nothing to really see, those mainly are the hooks
    @Override
    protected final void init() {
        super.init();
        uiSystem.getEventManager().resetAllElements();
        this.onInit();
    }

    @Override
    public final void removed() {
        uiSystem.getEventManager().cleanup();
        this.onRemove();
        super.removed();
    }

    @Override
    public final void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        uiSystem.getEventManager().resetAllElements();
        this.onResize(client, width, height);
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (uiSystem.getEventManager().onMouseClick(mouseX, mouseY, button)) {
            this.onMouseClicked(mouseX, mouseY, button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (uiSystem.getEventManager().onMouseRelease(mouseX, mouseY, button)) {
            this.onMouseReleased(mouseX, mouseY, button);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (uiSystem.getEventManager().onMouseScroll(mouseX, mouseY, verticalAmount)) {
            this.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        uiSystem.getEventManager().onMouseMove(mouseX, mouseY);
        this.onMouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (uiSystem.getEventManager().onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
            this.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (uiSystem.getEventManager().onKeyPress(keyCode, scanCode, modifiers)) {
            this.onKeyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public final boolean charTyped(char chr, int modifiers) {
        if (uiSystem.getEventManager().onCharTyped(chr, modifiers)) {
            this.onCharTyped(chr, modifiers);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public final void tick() {
        super.tick();
        uiSystem.getEventManager().onTick();
        this.onTick();
    }
}
