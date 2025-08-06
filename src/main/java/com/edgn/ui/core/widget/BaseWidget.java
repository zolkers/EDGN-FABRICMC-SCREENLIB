package com.edgn.ui.core.widget;

import com.edgn.ui.core.container.BaseContainer;
import com.edgn.ui.css.UIStyleSystem;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class BaseWidget<T> extends BaseContainer implements IWidget {
    protected T value;
    protected T defaultValue;
    protected boolean valueChanged = false;
    protected boolean valid = true;
    protected boolean editable = true;
    protected String validationError = null;

    protected final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
    protected final List<Predicate<T>> validators = new ArrayList<>();

    public BaseWidget(UIStyleSystem styleSystem, int x, int y, int width, int height) {
        super(styleSystem, x, y, width, height);
        setupWidgetItems();
    }

    public BaseWidget(UIStyleSystem styleSystem, int x, int y, int width, int height, T defaultValue) {
        super(styleSystem, x, y, width, height);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        setupWidgetItems();
    }

    protected abstract void setupWidgetItems();

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public IWidget setValue(Object newValue) {
        try {
            @SuppressWarnings("unchecked")
            T typedValue = (T) newValue;
            return setValueTyped(typedValue);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid value type: " +
                    (newValue != null ? newValue.getClass().getSimpleName() : "null"));
        }
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U setValueTyped(T newValue) {
        if (!editable) {
            return (U) this;
        }

        T oldValue = this.value;

        if (!Objects.equals(oldValue, newValue)) {
            this.value = newValue;
            this.valueChanged = true;

            validateValue();
            onValueChangedInternal(oldValue, newValue);
            notifyValueChanged(oldValue, newValue);
            markConstraintsDirty();
        }

        return (U) this;
    }

    protected void onValueChangedInternal(T oldValue, T newValue) {}

    @Override
    public boolean hasValueChanged() {
        return valueChanged;
    }

    @Override
    public void resetValueChanged() {
        this.valueChanged = false;
    }

    @Override
    public IWidget onValueChanged(ValueChangeListener listener) {
        if (listener != null) {
            valueChangeListeners.add(listener);
        }
        return this;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public String getValidationError() {
        return validationError;
    }

    @Override
    public IWidget setEditable(boolean editable) {
        this.editable = editable;
        onEditableChanged();
        return this;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    protected void onEditableChanged() {
        markConstraintsDirty();
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U addValidator(Predicate<T> validator) {
        if (validator != null) {
            validators.add(validator);
            validateValue();
        }
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U clearValidators() {
        validators.clear();
        validateValue();
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U reset() {
        setValueTyped(defaultValue);
        return (U) this;
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (U) this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    protected void validateValue() {
        valid = true;
        validationError = null;

        if (value == null && isRequired()) {
            valid = false;
            validationError = "This field is required";
            return;
        }

        for (Predicate<T> validator : validators) {
            if (!validator.test(value)) {
                valid = false;
                validationError = getCustomValidationError();
                break;
            }
        }

        if (valid) {
            String customError = validateValueCustom(value);
            if (customError != null) {
                valid = false;
                validationError = customError;
            }
        }
    }

    protected String validateValueCustom(T value) {
        return null;
    }

    protected String getCustomValidationError() {
        return "Invalid value";
    }

    protected boolean isRequired() {
        return false;
    }

    protected void notifyValueChanged(T oldValue, T newValue) {
        for (ValueChangeListener listener : valueChangeListeners) {
            listener.onValueChanged(oldValue, newValue);
        }
    }

    @Override
    protected void renderBackground(DrawContext context) {
        int bgColor = getBgColor();

        if (!editable) {
            bgColor = mixColor(bgColor, 0xFF888888, 0.3f);
        } else if (!valid) {
            bgColor = mixColor(bgColor, 0xFFFF0000, 0.1f);
        }

        if (bgColor != 0) {
            int borderRadius = getBorderRadius();
            renderRoundedRect(context, getCalculatedX(), getCalculatedY(),
                    getCalculatedWidth(), getCalculatedHeight(),
                    borderRadius, bgColor);
        }
    }

    @Override
    protected void renderEffects(DrawContext context) {
        super.renderEffects(context);

        if (!valid) {
            renderValidationError(context);
        }

        if (!editable) {
            renderDisabledOverlay(context);
        }
    }

    protected void renderValidationError(DrawContext context) {
        int borderColor = 0xFFFF4444;
        int x = getCalculatedX();
        int y = getCalculatedY();
        int width = getCalculatedWidth();
        int height = getCalculatedHeight();

        context.fill(x - 1, y - 1, x + width + 1, y, borderColor); // Top
        context.fill(x - 1, y + height, x + width + 1, y + height + 1, borderColor); // Bottom
        context.fill(x - 1, y, x, y + height, borderColor); // Left
        context.fill(x + width, y, x + width + 1, y + height, borderColor); // Right
    }

    protected void renderDisabledOverlay(DrawContext context) {
        int overlayColor = 0x40888888;
        int radius = getBorderRadius();

        renderRoundedRect(context, getCalculatedX(), getCalculatedY(),
                getCalculatedWidth(), getCalculatedHeight(),
                radius, overlayColor);
    }

    protected int mixColor(int color1, int color2, float ratio) {
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

    public <U extends BaseWidget<T>> U required() {
        return addValidator(value -> value != null && !value.toString().trim().isEmpty());
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U min(T minValue) {
        return addValidator(value -> {
            if (value instanceof Comparable && minValue instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<T> comparableValue = (Comparable<T>) value;
                return comparableValue.compareTo(minValue) >= 0;
            }
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    public <U extends BaseWidget<T>> U max(T maxValue) {
        return addValidator(value -> {
            if (value instanceof Comparable && maxValue instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<T> comparableValue = (Comparable<T>) value;
                return comparableValue.compareTo(maxValue) <= 0;
            }
            return true;
        });
    }
}