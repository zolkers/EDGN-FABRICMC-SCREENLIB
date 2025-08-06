package com.edgn.ui.event;


import com.edgn.ui.core.UIElement;
import com.edgn.ui.layout.LayoutEngine;
import net.minecraft.client.MinecraftClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UIEventManager {
    private final Set<UIElement> elements = new HashSet<>();
    private UIElement focusedElement = null;
    private UIElement hoveredElement = null;
    private long lastInteractionTime = 0;

    public void registerElement(UIElement element) {
        elements.add(element);
    }

    public void unregisterElement(UIElement element) {
        elements.remove(element);
        if (focusedElement == element) focusedElement = null;
        if (hoveredElement == element) hoveredElement = null;
    }


    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        lastInteractionTime = System.currentTimeMillis();

        List<UIElement> interactableElements = LayoutEngine.sortByInteractionPriority(
                elements.stream().toList(), mouseX, mouseY);

        for (UIElement element : interactableElements) {
            if (element.onMouseClick(mouseX, mouseY, button)) {
                setFocus(element);
                return true;
            }
        }

        setFocus(null);
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        return false;
    }

    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public void onMouseMove(double mouseX, double mouseY) {
        UIElement newHovered = null;

        List<UIElement> interactableElements = LayoutEngine.sortByInteractionPriority(
                elements.stream().toList(), mouseX, mouseY);

        if (!interactableElements.isEmpty()) {
            newHovered = interactableElements.getFirst();
        }

        if (hoveredElement != newHovered) {
            if (hoveredElement != null) {
                hoveredElement.onMouseLeave();
            }
            if (newHovered != null) {
                newHovered.onMouseEnter();
            }
            hoveredElement = newHovered;
        }
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean onCharTyped(char chr, int modifiers) {
        return false;
    }

    public void onTick() {

    }

    public void onResize(MinecraftClient client, int width, int height) {

    }

    public void setFocus(UIElement element) {
        if (focusedElement != element) {
            if (focusedElement != null) {
                focusedElement.onFocusLost();
            }
            if (element != null) {
                element.onFocusGained();
            }
            focusedElement = element;
        }
    }

    public void resetAllElements() {
        if (focusedElement != null) {
            focusedElement.onFocusLost();
            focusedElement = null;
        }

        if (hoveredElement != null) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }

        for (UIElement element : elements) {
            element.updateConstraints();
        }

        lastInteractionTime = 0;
    }

    public void cleanup() {
        elements.clear();
        focusedElement = null;
        hoveredElement = null;
        lastInteractionTime = 0;
    }

    public UIElement getFocusedElement() { return focusedElement; }
    public UIElement getHoveredElement() { return hoveredElement; }
    public long getLastInteractionTime() { return lastInteractionTime; }
    public Set<UIElement> getAllElements() { return new HashSet<>(elements); }
}