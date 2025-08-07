package com.edgn.ui.event;

import com.edgn.ui.core.UIElement;
import com.edgn.ui.layout.LayoutEngine;
import com.edgn.ui.layout.ZIndex;

import java.util.*;
import java.util.stream.Collectors;

public final class UIEventManager {
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

    private List<UIElement> getSortedInteractableElements(double mouseX, double mouseY) {
        return LayoutEngine.sortByInteractionPriority(new ArrayList<>(elements), mouseX, mouseY);
    }

    private List<UIElement> getAllVisibleElements() {
        return elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex).reversed())
                .collect(Collectors.toList());
    }

    private List<UIElement> getElementsInLayer(ZIndex.Layer layer) {
        return LayoutEngine.filterByLayer(new ArrayList<>(elements), layer);
    }

    public boolean onMouseClick(double mouseX, double mouseY, int button) {
        lastInteractionTime = System.currentTimeMillis();

        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseClick(mouseX, mouseY, button)) {
                setFocus(element);
                return true;
            }
        }

        setFocus(null);
        return false;
    }

    public boolean onMouseRelease(double mouseX, double mouseY, int button) {
        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseRelease(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double scrollDelta) {
        List<UIElement> sortedElements = getSortedInteractableElements(mouseX, mouseY);

        for (UIElement element : sortedElements) {
            if (element.onMouseScroll(mouseX, mouseY, scrollDelta)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        List<UIElement> sortedElements = getAllVisibleElements();

        for (UIElement element : sortedElements) {
            if (element.isEnabled() && element.onMouseDrag(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    public void onMouseMove(double mouseX, double mouseY) {
        UIElement newHovered = LayoutEngine.getTopElementAt(new ArrayList<>(elements), mouseX, mouseY);

        if (hoveredElement != newHovered) {
            if (hoveredElement != null) {
                hoveredElement.onMouseLeave();
            }
            if (newHovered != null) {
                newHovered.onMouseEnter();
            }
            hoveredElement = newHovered;
        }

        for (UIElement element : elements) {
            if (element.isVisible() && element.isRendered()) {
                element.onMouseMove(mouseX, mouseY);
            }
        }
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return focusedElement != null && focusedElement.isRendered() &&
                focusedElement.onKeyPress(keyCode, scanCode, modifiers);
    }

    public boolean onCharTyped(char chr, int modifiers) {
        return focusedElement != null && focusedElement.isRendered() && focusedElement.onCharTyped(chr, modifiers);
    }

    public void onTick() {
        if (focusedElement != null && (!focusedElement.isVisible() || !focusedElement.isRendered())) {
            setFocus(null);
        }

        if (hoveredElement != null && (!hoveredElement.isVisible() || !hoveredElement.isRendered())) {
            hoveredElement.onMouseLeave();
            hoveredElement = null;
        }
    }

    public void setFocus(UIElement element) {
        if (element != null && (!element.isVisible() || !element.isRendered() || !element.isEnabled())) {
            element = null;
        }

        if (focusedElement != element) {
            if (focusedElement != null) {
                focusedElement.onFocusLost();
            }
            focusedElement = element;
            if (element != null) {
                element.onFocusGained();
            }
        }
    }

    public void focusNext() {
        List<UIElement> focusableElements = elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex))
                .toList();

        if (focusableElements.isEmpty()) return;

        int currentIndex = focusedElement != null ? focusableElements.indexOf(focusedElement) : -1;
        int nextIndex = (currentIndex + 1) % focusableElements.size();
        setFocus(focusableElements.get(nextIndex));
    }

    public void focusPrevious() {
        List<UIElement> focusableElements = elements.stream()
                .filter(UIElement::isVisible)
                .filter(UIElement::isEnabled)
                .filter(UIElement::isRendered)
                .sorted(Comparator.comparing(UIElement::getZIndex))
                .toList();

        if (focusableElements.isEmpty()) return;

        int currentIndex = focusedElement != null ? focusableElements.indexOf(focusedElement) : 0;
        int prevIndex = (currentIndex - 1 + focusableElements.size()) % focusableElements.size();
        setFocus(focusableElements.get(prevIndex));
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
    }

    public void cleanup() {
        elements.clear();
        focusedElement = null;
        hoveredElement = null;
        lastInteractionTime = 0;
    }

    public void updateAllConstraints() {
        for (UIElement element : elements) {
            element.updateConstraints();

            if (!element.isVisible() || !element.isEnabled()) {
                if (focusedElement == element) {
                    setFocus(null);
                }
                if (hoveredElement == element) {
                    element.onMouseLeave();
                    hoveredElement = null;
                }
            }
        }
    }

    public UIElement getTopElementAt(double mouseX, double mouseY) {
        return LayoutEngine.getTopElementAt(new ArrayList<>(elements), mouseX, mouseY);
    }

    public List<UIElement> getAllElementsAt(double mouseX, double mouseY) {
        return LayoutEngine.getElementsAt(new ArrayList<>(elements), mouseX, mouseY);
    }

    public boolean canElementInteractAt(UIElement element, double mouseX, double mouseY) {
        return LayoutEngine.canInteractAt(element, new ArrayList<>(elements), mouseX, mouseY);
    }

    public void refreshHover(double mouseX, double mouseY) {
        UIElement newHovered = getTopElementAt(mouseX, mouseY);

        if (hoveredElement != newHovered) {
            if (hoveredElement != null) {
                hoveredElement.onMouseLeave();
            }
            hoveredElement = newHovered;
            if (newHovered != null) {
                newHovered.onMouseEnter();
            }
        }
    }

    // Getters
    public UIElement getFocusedElement() {
        return focusedElement;
    }

    public UIElement getHoveredElement() {
        return hoveredElement;
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public Set<UIElement> getAllElements() {
        return new HashSet<>(elements);
    }

    public Map<ZIndex.Layer, Long> getElementCountByLayer() {
        return elements.stream()
                .collect(Collectors.groupingBy(
                        element -> element.getZIndex().getLayer(),
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