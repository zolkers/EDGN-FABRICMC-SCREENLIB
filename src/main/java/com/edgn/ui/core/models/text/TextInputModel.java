package com.edgn.ui.core.models.text;

import com.edgn.ui.core.models.Model;

public interface TextInputModel extends Model {
    String getText();
    void setText(String text);
    int length();
    int getCaret();
    void setCaret(int index);
    int getSelectionStart();
    int getSelectionEnd();
    int getSelectionAnchor();
    boolean hasSelection();
    void setSelection(int start, int end);
    void clearSelection();
    int getMaxLength();
    void setMaxLength(int max);
    boolean isPassword();
    void setPassword(boolean enabled);
    char getPasswordChar();
    void setPasswordChar(char c);
    void insert(String s);
    void backspace(boolean byWord);
    void delete(boolean byWord);
    int wordLeft();
    int wordRight();
}