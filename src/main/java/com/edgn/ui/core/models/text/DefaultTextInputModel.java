package com.edgn.ui.core.models.text;

import java.util.ArrayDeque;
import java.util.Deque;

public class DefaultTextInputModel implements TextInputModel {
    private final StringBuilder value = new StringBuilder();
    private int caret = 0;
    private int selAnchor = -1;
    private int maxLength = Integer.MAX_VALUE;
    private boolean password = false;
    private char passwordChar = '•';
    private record HistoryEntry(String text, int caret, int selAnchor) {}
    private final Deque<HistoryEntry> undoStack = new ArrayDeque<>();
    private final Deque<HistoryEntry> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private boolean isUndoingOrRedoing = false;

    @Override public String getText() { return value.toString(); }

    @Override public void setText(String text) {
        if (text == null) text = "";
        if (value.toString().equals(text)) return;

        pushUndoState();
        value.setLength(0);
        value.append(text);
        caret = Math.clamp(caret, 0, value.length());
        selAnchor = -1;
    }

    @Override public int length() { return value.length(); }
    @Override public int getCaret() { return caret; }
    @Override public void setCaret(int index) { caret = Math.clamp(index, 0, value.length()); }
    @Override public int getSelectionStart() { return hasSelection() ? Math.min(selAnchor, caret) : caret; }
    @Override public int getSelectionEnd() { return hasSelection() ? Math.max(selAnchor, caret) : caret; }
    @Override public int getSelectionAnchor() { return selAnchor; }
    @Override public boolean hasSelection() { return selAnchor >= 0 && selAnchor != caret; }

    @Override public void setSelection(int start, int end) {
        start = Math.clamp(start, 0, value.length());
        end   = Math.clamp(end,   0, value.length());
        selAnchor = start;
        caret = end;
    }

    @Override public void clearSelection() { selAnchor = -1; }
    @Override public int getMaxLength() { return maxLength; }

    @Override public void setMaxLength(int max) {
        maxLength = Math.clamp(max, 0, Integer.MAX_VALUE);
        if (value.length() > maxLength) {
            value.setLength(maxLength);
            caret = Math.clamp(caret, 0, maxLength);
            selAnchor = -1;
        }
    }

    @Override public boolean isPassword() { return password; }
    @Override public void setPassword(boolean enabled) { password = enabled; }
    @Override public char getPasswordChar() { return passwordChar; }
    @Override public void setPasswordChar(char c) { passwordChar = c; }

    @Override
    public void insert(String s) {
        if (s == null || s.isEmpty()) return;
        pushUndoState();

        if (hasSelection()) {
            deleteSelection();
        }

        int can = Math.clamp((long) maxLength - value.length(), 0, Integer.MAX_VALUE);
        if (can <= 0) return;
        String ins = s.length() > can ? s.substring(0, can) : s;
        value.insert(caret, ins);
        caret += ins.length();
    }

    @Override
    public void backspace(boolean byWord) {
        if (!hasSelection() && caret <= 0) return;
        pushUndoState();

        if (hasSelection()) {
            deleteSelection();
        } else {
            int start = byWord ? wordLeft() : caret - 1;
            value.delete(start, caret);
            caret = start;
        }
    }

    @Override
    public void delete(boolean byWord) {
        if (!hasSelection() && caret >= value.length()) return;
        pushUndoState();

        if (hasSelection()) {
            deleteSelection();
        } else {
            int end = byWord ? wordRight() : caret + 1;
            value.delete(caret, end);
        }
    }

    @Override
    public int wordLeft() {
        int i = Math.clamp((long) caret - 1, 0, value.length());
        while (i > 0 && isSep(value.charAt(i))) i--;
        while (i > 0 && !Character.isWhitespace(value.charAt(i - 1))) i--;
        return i;
    }

    @Override
    public int wordRight() {
        int i = Math.clamp(caret, 0, value.length());
        int n = value.length();
        while (i < n && !Character.isWhitespace(value.charAt(i))) i++;
        while (i < n && isSep(value.charAt(i))) i++;
        return i;
    }

    private void deleteSelection() {
        int s = getSelectionStart();
        int e = getSelectionEnd();
        value.delete(s, e);
        caret = s;
        selAnchor = -1;
    }

    private boolean isWord(char c) { return Character.isLetterOrDigit(c) || c == '_' || c == '-'; }
    private boolean isSep(char c) { return !isWord(c) && !Character.isWhitespace(c); }

    // --- Implémentation de l'historique ---

    private void pushUndoState() {
        if (isUndoingOrRedoing) return;

        redoStack.clear();
        if (undoStack.size() >= MAX_HISTORY_SIZE) {
            undoStack.removeFirst();
        }
        undoStack.addLast(new HistoryEntry(value.toString(), caret, selAnchor));
    }

    private void applyState(HistoryEntry entry) {
        value.setLength(0);
        value.append(entry.text());
        caret = entry.caret();
        selAnchor = entry.selAnchor();
    }

    @Override
    public void undo() {
        if (undoStack.isEmpty()) return;

        isUndoingOrRedoing = true;
        redoStack.addLast(new HistoryEntry(value.toString(), caret, selAnchor));
        HistoryEntry lastState = undoStack.removeLast();
        applyState(lastState);
        isUndoingOrRedoing = false;
    }

    @Override
    public void redo() {
        if (redoStack.isEmpty()) return;

        isUndoingOrRedoing = true;
        undoStack.addLast(new HistoryEntry(value.toString(), caret, selAnchor));
        HistoryEntry nextState = redoStack.removeLast();
        applyState(nextState);
        isUndoingOrRedoing = false;
    }
}