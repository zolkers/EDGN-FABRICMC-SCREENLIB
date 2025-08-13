package com.edgn.ui.core.models.text;

public class DefaultTextInputModel implements TextInputModel {
    private final StringBuilder value = new StringBuilder();
    private int caret = 0;
    private int selAnchor = -1;
    private int maxLength = Integer.MAX_VALUE;
    private boolean password = false;
    private char passwordChar = '•';

    @Override public String getText() { return value.toString(); }
    @Override public void setText(String text) { value.setLength(0); if (text!=null) value.append(text); caret = Math.min(caret, value.length()); selAnchor = -1; }
    @Override public int length() { return value.length(); }
    @Override public int getCaret() { return caret; }
    @Override public void setCaret(int index) { caret = Math.max(0, Math.min(index, value.length())); }
    @Override public int getSelectionStart() { return hasSelection()? Math.min(selAnchor, caret): caret; }
    @Override public int getSelectionEnd() { return hasSelection()? Math.max(selAnchor, caret): caret; }
    @Override public boolean hasSelection() { return selAnchor >= 0 && selAnchor != caret; }
    @Override public void setSelection(int start, int end) { start = Math.max(0, Math.min(start, value.length())); end = Math.max(0, Math.min(end, value.length())); selAnchor = start; caret = end; }
    @Override public void clearSelection() { selAnchor = -1; }
    @Override public int getMaxLength() { return maxLength; }
    @Override public void setMaxLength(int max) { maxLength = Math.max(0, max); if (value.length() > maxLength) { value.setLength(maxLength); caret = Math.min(caret, maxLength); selAnchor = -1; } }
    @Override public boolean isPassword() { return password; }
    @Override public void setPassword(boolean enabled) { password = enabled; }
    @Override public char getPasswordChar() { return passwordChar; }
    @Override public void setPasswordChar(char c) { passwordChar = c; }

    @Override
    public void insert(String s) {
        if (s == null || s.isEmpty()) return;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (hasSelection()) { value.delete(start, end); caret = start; selAnchor = -1; }
        int can = Math.max(0, maxLength - value.length());
        if (can <= 0) return;
        String ins = s.length() > can ? s.substring(0, can) : s;
        value.insert(caret, ins);
        caret += ins.length();
    }

    @Override
    public void backspace(boolean byWord) {
        if (hasSelection()) { deleteSelection(); return; }
        if (caret <= 0) return;
        int start = byWord ? wordLeft() : caret - 1;
        value.delete(start, caret);
        caret = start;
    }

    @Override
    public void delete(boolean byWord) {
        if (hasSelection()) { deleteSelection(); return; }
        if (caret >= value.length()) return;
        int end = byWord ? wordRight() : caret + 1;
        value.delete(caret, end);
    }

    @Override
    public int wordLeft() {
        int i = Math.max(0, Math.min(caret - 1, value.length()));
        while (i > 0 && isSep(value.charAt(i))) i--;
        while (i > 0 && isWord(value.charAt(i - 1))) i--;
        return i;
    }

    @Override
    public int wordRight() {
        int i = Math.max(0, Math.min(caret, value.length()));
        int n = value.length();
        while (i < n && isSep(value.charAt(i))) i++;
        while (i < n && isWord(value.charAt(i))) i++;
        return i;
    }

    private void deleteSelection() {
        int s = getSelectionStart(), e = getSelectionEnd();
        value.delete(s, e);
        caret = s;
        selAnchor = -1;
    }

    private boolean isWord(char c) { return Character.isLetterOrDigit(c) || c=='_' || c=='-'; }
    private boolean isSep(char c) { return !isWord(c) && !Character.isWhitespace(c); }
}
