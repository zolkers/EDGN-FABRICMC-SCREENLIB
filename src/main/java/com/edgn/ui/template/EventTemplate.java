package com.edgn.ui.template;

import com.edgn.exceptions.ScreenCrashException;
import com.edgn.exceptions.safe.Crash;
import com.edgn.exceptions.safe.Safe;
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

    @Override
    protected final void init() {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.INIT, super::init);
            Safe.run(s, ScreenCrashException.Phase.INIT, () -> uiSystem.getEventManager().resetAllElements());
            Safe.run(s, ScreenCrashException.Phase.INIT, this::onInit);
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final void removed() {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.CLOSE, () -> uiSystem.getEventManager().cleanup());
            Safe.run(s, ScreenCrashException.Phase.CLOSE, this::onRemove);
            Safe.run(s, ScreenCrashException.Phase.CLOSE, super::removed);
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final void resize(MinecraftClient client, int width, int height) {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.RESIZE, () -> super.resize(client, width, height));
            Safe.run(s, ScreenCrashException.Phase.RESIZE, () -> uiSystem.getEventManager().resetAllElements());
            Safe.run(s, ScreenCrashException.Phase.RESIZE, () -> this.onResize(client, width, height));
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onMouseClick(mouseX, mouseY, button)) {
                    this.onMouseClicked(mouseX, mouseY, button);
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onMouseRelease(mouseX, mouseY, button)) {
                    this.onMouseReleased(mouseX, mouseY, button);
                    return true;
                }
                return super.mouseReleased(mouseX, mouseY, button);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onMouseScroll(mouseX, mouseY, verticalAmount)) {
                    this.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
                    return true;
                }
                return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.INPUT, () -> uiSystem.getEventManager().onMouseMove(mouseX, mouseY));
            Safe.run(s, ScreenCrashException.Phase.INPUT, () -> this.onMouseMoved(mouseX, mouseY));
            Safe.run(s, ScreenCrashException.Phase.INPUT, () -> super.mouseMoved(mouseX, mouseY));
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                    this.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
                    return true;
                }
                return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onKeyPress(keyCode, scanCode, modifiers)) {
                    this.onKeyPressed(keyCode, scanCode, modifiers);
                    return true;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final boolean charTyped(char chr, int modifiers) {
        String s = getClass().getSimpleName();
        try {
            return Safe.call(s, ScreenCrashException.Phase.INPUT, () -> {
                if (uiSystem.getEventManager().onCharTyped(chr, modifiers)) {
                    this.onCharTyped(chr, modifiers);
                    return true;
                }
                return super.charTyped(chr, modifiers);
            });
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
            return true;
        }
    }

    @Override
    public final void tick() {
        String s = getClass().getSimpleName();
        try {
            Safe.run(s, ScreenCrashException.Phase.TICK, super::tick);
            Safe.run(s, ScreenCrashException.Phase.TICK, () -> uiSystem.getEventManager().onTick());
            Safe.run(s, ScreenCrashException.Phase.TICK, this::onTick);
        } catch (ScreenCrashException e) {
            Crash.handle(this, e, this::close);
        }
    }
}
