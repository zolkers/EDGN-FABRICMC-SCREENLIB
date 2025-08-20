package com.edgn.ui.event;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.layout.LayoutEngine;
import com.edgn.ui.layout.ZIndex;
import net.minecraft.client.MinecraftClient;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class UIEventManager {
    private final Set<UIElement> elements = new HashSet<>();

    private UIElement focusedElement = null;
    private UIElement hoveredElement = null;

    private long lastInteractionTime = 0;

    private double lastMouseX = Double.NaN;
    private double lastMouseY = Double.NaN;


    public void registerElement(UIElement element) {
        if (element != null) {
            elements.add(element);
        }
    }

    public void unregisterElement(UIElement element) {
        if (element == null) return;

        elements.remove(element);

        if (focusedElement == element) {
            focusedElement.onFocusLost();
            focusedElement = null;
        }

        if (hoveredElement == element) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }
    }

    public void cleanup() {
        if (focusedElement != null) {
            focusedElement.onFocusLost();
            focusedElement = null;
        }
        if (hoveredElement != null) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }
        elements.clear();
        lastInteractionTime = 0;
        lastMouseX = Double.NaN;
        lastMouseY = Double.NaN;
    }

    private List<UIElement> getSortedInteractableElements(double mouseX, double mouseY) {
        return LayoutEngine.sortByInteractionPriority(new ArrayList<>(elements), mouseX, mouseY);
    }

    private UIElement pickTopElementAt(double mouseX, double mouseY) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        for (UIElement el : sorted) {
            if (canElementInteractAt(el, mouseX, mouseY)) {
                return el;
            }
        }
        return null;
    }

    private List<UIElement> getAllVisibleElements() {
        return elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex).reversed())
                .toList();
    }

    public List<UIElement> getElementsInLayer(ZIndex.Layer layer) {
        return LayoutEngine.filterByLayer(new ArrayList<>(elements), layer);
    }

    private void updateHoverUnderMouse() {
        UIElement newHover = pickTopElementAt(lastMouseX, lastMouseY);

        if (hoveredElement == newHover) return;

        if (hoveredElement != null) {
            hoveredElement.onMouseLeave();
        }
        hoveredElement = newHover;
        if (hoveredElement != null) {
            hoveredElement.onMouseEnter();
        }
    }

    public void refreshHover(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        updateHoverUnderMouse();
    }


    public void onMouseMove(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        updateHoverUnderMouse();

        for (UIElement element : elements) {
            if (element.isVisible() && element.isRendered()) {
                element.onMouseMove(mouseX, mouseY);
            }
        }
    }

    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        lastInteractionTime = System.currentTimeMillis();

        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        for (UIElement element : sorted) {
            if (!canElementInteractAt(element, mouseX, mouseY)) continue;
            if (element.onMouseClick(mouseX, mouseY, button)) {
                setFocus(element);
                return true;
            }
        }

        setFocus(null);
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        for (UIElement element : sorted) {
            if (!canElementInteractAt(element, mouseX, mouseY)) continue;
            if (element.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        for (UIElement element : sorted) {
            if (!canElementInteractAt(element, mouseX, mouseY)) continue;
            if (element.onMouseScroll(mouseX, mouseY, scrollDelta)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        for (UIElement element : sorted) {
            if (!element.isEnabled()) continue;
            if (!canElementInteractAt(element, mouseX, mouseY)) continue;
            if (element.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return focusedElement != null
                && focusedElement.isRendered()
                && focusedElement.onKeyPress(keyCode, scanCode, modifiers);
    }

    public boolean onCharTyped(char chr, int modifiers) {
        return focusedElement != null
                && focusedElement.isRendered()
                && focusedElement.onCharTyped(chr, modifiers);
    }

    public void onResize(MinecraftClient client, int width, int height) {
        for (UIElement element : elements) {
            element.onResize(client, width, height);
        }
        if (!Double.isNaN(lastMouseX) && !Double.isNaN(lastMouseY)) {
            updateHoverUnderMouse();
        }
    }

    public void onTick() {
        if (focusedElement != null && (!focusedElement.isVisible() || !focusedElement.isRendered())) {
            setFocus(null);
        }

        if (hoveredElement != null && (!hoveredElement.isVisible() || !hoveredElement.isRendered())) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }

        if (!Double.isNaN(lastMouseX) && !Double.isNaN(lastMouseY)) {
            updateHoverUnderMouse();
        }

        for (UIElement element : elements) {
            element.onTick();
        }
    }

    public void setFocus(UIElement element) {
        if (element != null && (!element.isVisible() || !element.isRendered() || !element.isEnabled())) {
            element = null;
        }

        if (focusedElement == element) return;

        if (focusedElement != null) {
            focusedElement.onFocusLost();
        }
        focusedElement = element;
        if (focusedElement != null) {
            focusedElement.onFocusGained();
        }
    }

    public void focusNext() {
        List<UIElement> focusable = elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex))
                .toList();

        if (focusable.isEmpty()) return;

        int idx = focusedElement != null ? focusable.indexOf(focusedElement) : -1;
        int next = (idx + 1) % focusable.size();
        setFocus(focusable.get(next));
    }

    public void focusPrevious() {
        List<UIElement> focusable = elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex))
                .toList();

        if (focusable.isEmpty()) return;

        int idx = focusedElement != null ? focusable.indexOf(focusedElement) : 0;
        int prev = (idx - 1 + focusable.size()) % focusable.size();
        setFocus(focusable.get(prev));
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
            element.markAsNotRendered();
            element.updateConstraints();
        }

        lastInteractionTime = 0;
        lastMouseX = Double.NaN;
        lastMouseY = Double.NaN;
    }

    public void updateAllConstraints() {
        for (UIElement element : elements) {
            element.updateConstraints();

            if (!element.isVisible() || !element.isEnabled()) {
                if (focusedElement == element) setFocus(null);
                if (hoveredElement == element) {
                    hoveredElement.onMouseLeave();
                    hoveredElement = null;
                }
            }
        }

        if (!Double.isNaN(lastMouseX) && !Double.isNaN(lastMouseY)) {
            updateHoverUnderMouse();
        }
    }


    public UIElement getTopElementAt(double mouseX, double mouseY) {
        return pickTopElementAt(mouseX, mouseY);
    }

    public List<UIElement> getAllElementsAt(double mouseX, double mouseY) {
        List<UIElement> sorted = getSortedInteractableElements(mouseX, mouseY);
        List<UIElement> hits = new ArrayList<>();
        for (UIElement el : sorted) {
            if (canElementInteractAt(el, mouseX, mouseY)) {
                hits.add(el);
            }
        }
        return hits;
    }

    public boolean canElementInteractAt(UIElement element, double mouseX, double mouseY) {
        return LayoutEngine.canInteractAt(element, new ArrayList<>(elements), mouseX, mouseY);
    }

    public UIElement getFocusedElement() { return focusedElement; }
    public UIElement getHoveredElement() { return hoveredElement; }
    public long getLastInteractionTime() { return lastInteractionTime; }

    public Set<UIElement> getAllElements() { return new HashSet<>(elements); }

    public Map<ZIndex.Layer, Long> getElementCountByLayer() {
        return elements.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getZIndex().getLayer(),
                        Collectors.counting()
                ));
    }

    public long getRenderedElementCount() {
        return elements.stream().filter(UIElement::isRendered).count();
    }

    public long getInteractiveElementCount() {
        return elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .count();
    }
}
