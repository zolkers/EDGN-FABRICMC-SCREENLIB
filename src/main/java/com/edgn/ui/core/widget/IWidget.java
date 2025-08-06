package com.edgn.ui.core.widget;

import com.edgn.ui.core.container.IContainer;

public interface IWidget extends IContainer {
    Object getValue();
    IWidget setValue(Object value);
    boolean hasValueChanged();
    void resetValueChanged();
    IWidget onValueChanged(ValueChangeListener listener);
    boolean isValid();
    String getValidationError();
    IWidget setEditable(boolean editable);
    boolean isEditable();

    @FunctionalInterface
    interface ValueChangeListener {
        void onValueChanged(Object oldValue, Object newValue);
    }
}