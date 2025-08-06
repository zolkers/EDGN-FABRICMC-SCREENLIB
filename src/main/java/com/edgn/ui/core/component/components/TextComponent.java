package com.edgn.ui.core.component.components;

import com.edgn.ui.core.component.BaseComponent;
import com.edgn.ui.core.component.IComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TextComponent extends BaseComponent {

    public enum TextAlign { LEFT, CENTER, RIGHT }
    public enum VerticalAlign { TOP, MIDDLE, BOTTOM }
    public enum AnimationType { NONE, WAVE, TYPEWRITER, GLOW, PULSE, SHAKE }
    public enum EffectType { NONE, SOLID, GRADIENT, RAINBOW }
    public enum EffectMode { PULSE, HORIZONTAL_LTR, HORIZONTAL_RTL }
    public enum TextOverflowMode { NONE, TRUNCATE, WRAP, SCALE }
    private final String text;
    private TextRenderer textRenderer;
    private TextOverflowMode overflowMode = TextOverflowMode.NONE;
    private String ellipsis = "...";
    private int maxLines = 1;
    private float minScale = 0.5f;
    private int safetyMargin = 0;
    private EffectType effectType = EffectType.SOLID;
    private EffectMode effectMode = EffectMode.PULSE;
    private int startColor = 0xFFFFFFFF;
    private int endColor = 0xFF000000;
    private float effectSpeed = 1.0f;
    private TextAlign textAlign = TextAlign.LEFT;
    private VerticalAlign verticalAlign = VerticalAlign.MIDDLE;
    private boolean hasShadow = false;
    private int shadowColor = 0xFF000000;
    private int shadowOffsetX = 1;
    private int shadowOffsetY = 1;
    private final Set<AnimationType> activeAnimations = EnumSet.noneOf(AnimationType.class);
    private float animationSpeed = 1.0f;
    private long animationStartTime = System.currentTimeMillis();
    private float waveAmplitude = 5.0f;
    private float waveFrequency = 2.0f;
    private int typewriterCharCount = 0;
    private long lastTypewriterUpdate = 0;
    private int typewriterDelay = 100;
    private boolean hasGlow = false;
    private int glowColor = 0x80FFFFFF;
    private float glowRadius = 3.0f;
    private float glowIntensity = 1.0f;
    private float pulseMin = 0.8f;
    private float pulseMax = 1.2f;
    private float shakeIntensity = 2.0f;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderlined = false;
    private boolean isStrikethrough = false;

    private final List<TextEffect> customEffects = new ArrayList<>();

    public TextComponent(String text, TextRenderer textRenderer) {
        this.text = text;
        this.textRenderer = textRenderer;
        this.startColor = super.color; 
    }

    @Override
    protected void renderComponent(DrawContext context, int x, int y, int width, int height) {
        if (text == null || text.isEmpty()) return;

        String displayText = getProcessedText();
        if (displayText.isEmpty()) return;

        switch (overflowMode) {
            case WRAP -> renderWrapped(context, x, y, width, height);
            case SCALE -> renderScaled(context, x, y, width, height, displayText);
            default -> renderSingle(context, x, y, width, height, displayText);
        }
    }

    @Override
    protected void updateComponent(long deltaTime) {
        
        if (activeAnimations.contains(AnimationType.TYPEWRITER)) {
            updateTypewriterAnimation();
        }
    }

    @Override
    protected long getUpdateInterval() {
        
        if (!activeAnimations.isEmpty()) {
            return 16; 
        }
        return 100; 
    }

    private void renderSingle(DrawContext context, int x, int y, int width, int height, String displayText) {
        int renderX = calculateX(x, width, displayText);
        int renderY = calculateY(y, height);
        renderTextInternal(context, displayText, renderX, renderY, 0);
    }

    private void renderScaled(DrawContext context, int x, int y, int width, int height, String displayText) {
        int textWidth = textRenderer.getWidth(displayText);
        if (textWidth <= width - safetyMargin) {
            renderSingle(context, x, y, width, height, displayText);
            return;
        }

        float scale = Math.max(minScale, (float) (width - safetyMargin) / textWidth);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);
        int scaledWidth = (int) (width / scale);
        int scaledHeight = (int) (height / scale);

        renderSingle(context, scaledX, scaledY, scaledWidth, scaledHeight, displayText);

        context.getMatrices().pop();
    }

    private void renderWrapped(DrawContext context, int x, int y, int width, int height) {
        if (textRenderer == null) return;

        List<OrderedText> orderedLines = textRenderer.wrapLines(Text.literal(this.text), width - safetyMargin);
        List<String> stringLines = orderedLines.stream().map(orderedText -> {
            StringBuilder sb = new StringBuilder();
            orderedText.accept((index, style, codePoint) -> {
                sb.append(Character.toChars(codePoint));
                return true;
            });
            return sb.toString();
        }).toList();

        int yOffset = 0;
        int charOffset = 0;
        int lineHeight = textRenderer.fontHeight + 2;

        for (int i = 0; i < Math.min(stringLines.size(), maxLines); i++) {
            String line = stringLines.get(i);

            if (i == maxLines - 1 && stringLines.size() > maxLines) {
                line = truncateText(line, width - safetyMargin);
            }

            int renderX = calculateX(x, width, line);
            renderTextInternal(context, line, renderX, y + yOffset, charOffset);

            yOffset += lineHeight;
            charOffset += stringLines.get(i).length();
        }
    }

    private void renderTextInternal(DrawContext context, String textToRender, int x, int y, int charOffset) {
        if (textToRender.isEmpty()) return;

        int renderX = x;
        int renderY = y;

        
        if (activeAnimations.contains(AnimationType.SHAKE)) {
            float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f * animationSpeed;
            renderX += (int) (Math.sin(time * 20) * shakeIntensity);
            renderY += (int) (Math.cos(time * 25) * shakeIntensity);
        }

        
        for (TextEffect effect : customEffects) {
            effect.apply(this, context, renderX, renderY);
        }

        
        if (hasGlow && !activeAnimations.contains(AnimationType.TYPEWRITER)) {
            renderGlow(context, textToRender, renderX, renderY);
        }

        
        if (hasShadow) {
            renderTextWithFormatting(context, textToRender, renderX + shadowOffsetX,
                    renderY + shadowOffsetY, applyOpacity(shadowColor), charOffset);
        }

        
        boolean needsPerCharRender = activeAnimations.contains(AnimationType.WAVE) ||
                effectMode != EffectMode.PULSE;

        if (needsPerCharRender) {
            renderPerChar(context, textToRender, renderX, renderY, charOffset);
        } else {
            int textColor = getCurrentColor(charOffset);
            if (activeAnimations.contains(AnimationType.PULSE)) {
                textColor = applyPulseEffect(textColor);
            }
            renderTextWithFormatting(context, textToRender, renderX, renderY, textColor, charOffset);
        }

        
        if (isUnderlined || isStrikethrough) {
            renderTextDecorations(context, textToRender, renderX, renderY, charOffset);
        }
    }

    private void renderPerChar(DrawContext context, String displayText, int x, int y, int charOffset) {
        for (int i = 0; i < displayText.length(); i++) {
            char c = displayText.charAt(i);
            String charStr = String.valueOf(c);

            int charX = x + textRenderer.getWidth(displayText.substring(0, i));
            int charY = y;
            int charColor = getCurrentColor(i + charOffset);

            
            if (activeAnimations.contains(AnimationType.WAVE)) {
                float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
                charY += (int) (Math.sin(time * animationSpeed * waveFrequency + (i + charOffset) * 0.5f) * waveAmplitude);
            }

            
            if (activeAnimations.contains(AnimationType.PULSE)) {
                charColor = applyPulseEffect(charColor);
            }

            renderTextWithFormatting(context, charStr, charX, charY, charColor, i + charOffset);
        }
    }

    private int getCurrentColor(int charIndex) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
        float positionFactor = (float) charIndex / 15.0f;

        int baseColor = switch (effectType) {
            case SOLID -> this.startColor;
            case GRADIENT -> getGradientColorAt(time, positionFactor);
            case RAINBOW -> getRainbowColorAt(time, positionFactor);
            default -> this.color;
        };

        return applyOpacity(baseColor);
    }

    private int getGradientColorAt(float time, float position) {
        float factor = switch (effectMode) {
            case PULSE -> (float) (Math.sin(time * effectSpeed) * 0.5 + 0.5);
            case HORIZONTAL_LTR -> robustModulo(time * effectSpeed - position);
            case HORIZONTAL_RTL -> robustModulo(time * effectSpeed + position);
        };
        return interpolateColor(startColor, endColor, factor);
    }

    private int getRainbowColorAt(float time, float position) {
        float hue = switch (effectMode) {
            case PULSE -> robustModulo(time * effectSpeed);
            case HORIZONTAL_LTR -> robustModulo(time * effectSpeed - position);
            case HORIZONTAL_RTL -> robustModulo(time * effectSpeed + position);
        };
        return Color.HSBtoRGB(hue, 1.0f, 1.0f) | 0xFF000000;
    }

    private void renderTextWithFormatting(DrawContext context, String text, int x, int y, int color, int charOffset) {
        if (isBold && isItalic) {
            renderBoldItalicText(context, text, x, y, color, charOffset);
        } else if (isBold) {
            renderBoldText(context, text, x, y, color);
        } else if (isItalic) {
            renderItalicText(context, text, x, y, color, charOffset);
        } else {
            context.drawText(textRenderer, text, x, y, color, false);
        }
    }

    private void renderBoldText(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text, x, y, color, false);
        context.drawText(textRenderer, text, x + 1, y, color, false);
        context.drawText(textRenderer, text, x, y + 1, color, false);
        context.drawText(textRenderer, text, x + 1, y + 1, color, false);
    }

    private void renderItalicText(DrawContext context, String text, int x, int y, int color, int charOffset) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charX = x + textRenderer.getWidth(text.substring(0, i));
            int italicOffset = (int) (Math.sin((i + charOffset) * 0.3f) * 1.5f);
            context.drawText(textRenderer, charStr, charX + italicOffset, y, color, false);
        }
    }

    private void renderBoldItalicText(DrawContext context, String text, int x, int y, int color, int charOffset) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charX = x + textRenderer.getWidth(text.substring(0, i));
            int italicOffset = (int) (Math.sin((i + charOffset) * 0.3f) * 1.5f);
            renderBoldText(context, charStr, charX + italicOffset, y, color);
        }
    }

    private void renderTextDecorations(DrawContext context, String displayText, int x, int y, int charOffset) {
        int textWidth = textRenderer.getWidth(displayText);
        int decorationColor = applyOpacity(getCurrentColor(charOffset));

        if (isUnderlined) {
            int underlineY = y + textRenderer.fontHeight;
            context.fill(x, underlineY, x + textWidth, underlineY + 1, decorationColor);
        }
        if (isStrikethrough) {
            int strikeY = y + textRenderer.fontHeight / 2;
            context.fill(x, strikeY, x + textWidth, strikeY + 1, decorationColor);
        }
    }

    private void renderGlow(DrawContext context, String displayText, int x, int y) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f;
        float glowAlpha = (float) (Math.sin(time * 3) * 0.3 + 0.7) * glowIntensity;

        for (int offsetX = -(int) glowRadius; offsetX <= glowRadius; offsetX++) {
            for (int offsetY = -(int) glowRadius; offsetY <= glowRadius; offsetY++) {
                if (offsetX == 0 && offsetY == 0) continue;
                float distance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
                if (distance <= glowRadius) {
                    float alpha = (1.0f - distance / glowRadius) * glowAlpha * 0.3f;
                    int currentGlowColor = (glowColor & 0x00FFFFFF) | ((int) (255 * alpha) << 24);
                    renderTextWithFormatting(context, displayText, x + offsetX, y + offsetY,
                            applyOpacity(currentGlowColor), 0);
                }
            }
        }
    }

    private int applyPulseEffect(int color) {
        float time = (System.currentTimeMillis() - animationStartTime) / 1000.0f * animationSpeed;
        float scale = (float) (Math.sin(time * 3) * 0.5 + 0.5);
        float alpha = pulseMin + (pulseMax - pulseMin) * scale;
        int a1 = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int newAlpha = (int) (a1 * alpha);
        return (Math.min(255, newAlpha) << 24) | (r << 16) | (g << 8) | b;
    }

    
    private float robustModulo(float value) {
        return (value % 1.0f + 1.0f) % 1.0f;
    }

    private int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        int r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF, a1 = (color1 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF, a2 = (color2 >> 24) & 0xFF;
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        int a = (int) (a1 + (a2 - a1) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private String getProcessedText() {
        String displayText = getDisplayText();

        if (overflowMode == TextOverflowMode.TRUNCATE) {
            displayText = truncateText(displayText, getPreferredWidth() - safetyMargin);
        }

        return displayText;
    }

    private String getDisplayText() {
        if (activeAnimations.contains(AnimationType.TYPEWRITER)) {
            return text.substring(0, Math.min(typewriterCharCount, text.length()));
        }
        return text;
    }

    private void updateTypewriterAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTypewriterUpdate > typewriterDelay && typewriterCharCount < text.length()) {
            typewriterCharCount++;
            lastTypewriterUpdate = currentTime;
        }
    }

    private String truncateText(String text, int availableWidth) {
        if (text == null || text.isEmpty() || availableWidth <= 0 || textRenderer == null) {
            return text != null ? text : "";
        }

        int fullTextWidth = textRenderer.getWidth(text);
        if (fullTextWidth <= availableWidth) {
            return text;
        }

        int ellipsisWidth = textRenderer.getWidth(ellipsis);
        if (ellipsisWidth >= availableWidth) {
            return "";
        }

        int maxTextWidth = availableWidth - ellipsisWidth;
        int left = 0;
        int right = text.length();
        int bestLength = 0;

        while (left <= right) {
            int mid = (left + right) / 2;
            String substring = text.substring(0, mid);
            int substringWidth = textRenderer.getWidth(substring);

            if (substringWidth <= maxTextWidth) {
                bestLength = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        String result = text.substring(0, bestLength) + ellipsis;

        while (textRenderer.getWidth(result) > availableWidth && bestLength > 0) {
            bestLength--;
            result = text.substring(0, bestLength) + ellipsis;
        }

        return result;
    }

    private int calculateX(int baseX, int maxWidth, String displayText) {
        if (textRenderer == null) return baseX;

        int textWidth = textRenderer.getWidth(displayText);
        return switch (textAlign) {
            case LEFT -> baseX;
            case CENTER -> baseX + (maxWidth - textWidth) / 2;
            case RIGHT -> baseX + maxWidth - textWidth;
        };
    }

    private int calculateY(int baseY, int maxHeight) {
        if (textRenderer == null) return baseY;

        int textHeight = textRenderer.fontHeight;
        return switch (verticalAlign) {
            case TOP -> baseY;
            case MIDDLE -> baseY + (maxHeight - textHeight) / 2;
            case BOTTOM -> baseY + maxHeight - textHeight;
        };
    }

    @Override
    protected int calculatePreferredWidth() {
        if (textRenderer == null || text == null) return 0;
        return textRenderer.getWidth(text);
    }

    @Override
    protected int calculatePreferredHeight() {
        if (textRenderer == null) return 12; 
        return textRenderer.fontHeight;
    }

    @Override
    public IComponent clone() {
        TextComponent clone = new TextComponent(this.text, this.textRenderer);
        copyBaseTo(clone);

        
        clone.overflowMode = this.overflowMode;
        clone.ellipsis = this.ellipsis;
        clone.maxLines = this.maxLines;
        clone.minScale = this.minScale;
        clone.safetyMargin = this.safetyMargin;
        clone.effectType = this.effectType;
        clone.effectMode = this.effectMode;
        clone.startColor = this.startColor;
        clone.endColor = this.endColor;
        clone.effectSpeed = this.effectSpeed;
        clone.textAlign = this.textAlign;
        clone.verticalAlign = this.verticalAlign;
        clone.hasShadow = this.hasShadow;
        clone.shadowColor = this.shadowColor;
        clone.shadowOffsetX = this.shadowOffsetX;
        clone.shadowOffsetY = this.shadowOffsetY;
        clone.animationSpeed = this.animationSpeed;
        clone.waveAmplitude = this.waveAmplitude;
        clone.waveFrequency = this.waveFrequency;
        clone.typewriterDelay = this.typewriterDelay;
        clone.hasGlow = this.hasGlow;
        clone.glowColor = this.glowColor;
        clone.glowRadius = this.glowRadius;
        clone.glowIntensity = this.glowIntensity;
        clone.pulseMin = this.pulseMin;
        clone.pulseMax = this.pulseMax;
        clone.shakeIntensity = this.shakeIntensity;
        clone.isBold = this.isBold;
        clone.isItalic = this.isItalic;
        clone.isUnderlined = this.isUnderlined;
        clone.isStrikethrough = this.isStrikethrough;
        clone.activeAnimations.addAll(this.activeAnimations);
        clone.customEffects.addAll(this.customEffects);

        return clone;
    }

    
    public TextComponent setOverflowMode(TextOverflowMode mode) {
        this.overflowMode = mode;
        return this;
    }

    public TextComponent setMaxLines(int maxLines) {
        this.maxLines = Math.max(1, maxLines);
        return this;
    }

    public TextComponent setEllipsis(String ellipsis) {
        this.ellipsis = ellipsis != null ? ellipsis : "...";
        return this;
    }

    public TextComponent setSafetyMargin(int margin) {
        this.safetyMargin = Math.max(0, margin);
        return this;
    }

    public TextComponent setMinScale(float minScale) {
        this.minScale = Math.max(0.1f, Math.min(1.0f, minScale));
        return this;
    }

    public TextComponent truncate() {
        return setOverflowMode(TextOverflowMode.TRUNCATE);
    }

    public TextComponent wrap(int maxLines) {
        return setOverflowMode(TextOverflowMode.WRAP).setMaxLines(maxLines);
    }

    public TextComponent autoScale() {
        return setOverflowMode(TextOverflowMode.SCALE);
    }

    public TextComponent align(TextAlign align) {
        this.textAlign = align;
        return this;
    }

    public TextComponent verticalAlign(VerticalAlign align) {
        this.verticalAlign = align;
        return this;
    }

    public TextComponent shadow(int color, int offsetX, int offsetY) {
        this.hasShadow = true;
        this.shadowColor = color;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        return this;
    }

    public TextComponent shadow() {
        return shadow(0xFF000000, 1, 1);
    }

    public TextComponent gradient(int startColor, int endColor, EffectMode mode, float speed) {
        this.effectType = EffectType.GRADIENT;
        this.startColor = startColor;
        this.endColor = endColor;
        this.effectMode = mode;
        this.effectSpeed = speed;
        return this;
    }

    public TextComponent rainbow(EffectMode mode, float speed) {
        this.effectType = EffectType.RAINBOW;
        this.effectMode = mode;
        this.effectSpeed = speed;
        return this;
    }

    public TextComponent rainbow(EffectMode mode) { return rainbow(mode, 1.0f); }
    public TextComponent rainbow() { return rainbow(EffectMode.HORIZONTAL_LTR, 1.0f); }


    public TextComponent glow(int color) { return glow(color, 3.0f, 1.0f); }

    public TextComponent wave(float amplitude, float frequency, float speed) {
        this.activeAnimations.add(AnimationType.WAVE);
        this.waveAmplitude = amplitude;
        this.waveFrequency = frequency;
        this.animationSpeed = speed;
        markNeedsUpdate();
        return this;
    }

    public TextComponent wave() {
        return wave(5.0f, 2.0f, 1.0f);
    }

    public TextComponent typewriter(int delayMs) {
        this.activeAnimations.add(AnimationType.TYPEWRITER);
        this.typewriterDelay = delayMs;
        this.typewriterCharCount = 0;
        this.lastTypewriterUpdate = System.currentTimeMillis();
        markNeedsUpdate();
        return this;
    }

    public TextComponent typewriter() {
        return typewriter(100);
    }

    public TextComponent glow(int color, float radius, float intensity) {
        this.hasGlow = true;
        this.glowColor = color;
        this.glowRadius = radius;
        this.glowIntensity = intensity;
        return this;
    }

    public TextComponent glow() {
        return glow(0x80FFFFFF, 3.0f, 1.0f);
    }

    public TextComponent pulse(float min, float max, float speed) {
        this.activeAnimations.add(AnimationType.PULSE);
        this.pulseMin = min;
        this.pulseMax = max;
        this.animationSpeed = speed;
        markNeedsUpdate();
        return this;
    }

    public TextComponent pulse() {
        return pulse(0.8f, 1.2f, 1.0f);
    }

    public TextComponent shake(float intensity, float speed) {
        this.activeAnimations.add(AnimationType.SHAKE);
        this.shakeIntensity = intensity;
        this.animationSpeed = speed;
        markNeedsUpdate();
        return this;
    }

    public TextComponent shake() {
        return shake(2.0f, 1.0f);
    }

    public TextComponent bold() {
        this.isBold = true;
        return this;
    }

    public TextComponent italic() {
        this.isItalic = true;
        return this;
    }

    public TextComponent underlined() {
        this.isUnderlined = true;
        return this;
    }

    public TextComponent strikethrough() {
        this.isStrikethrough = true;
        return this;
    }

    public TextComponent addEffect(TextEffect effect) {
        this.customEffects.add(effect);
        return this;
    }

    public TextComponent asTitle() {
        return setColor(0xFF0D6EFD);
    }

    public TextComponent asSubtitle() {
        return setColor(0xFF888888).italic();
    }

    public TextComponent asError() {
        return setColor(0xFFDC3545).shake();
    }

    public TextComponent asSuccess() {
        return setColor(0xFF198754).glow(0xFF198754, 3.0f, 1.0f);
    }

    public TextComponent asWarning() {
        return setColor(0xFFFFC107).pulse();
    }

    public TextComponent asHighlight() {
        return gradient(0xFF0D6EFD, 0xFF6EA8FE, EffectMode.HORIZONTAL_LTR, 1.0f);
    }

    public TextComponent asFancy() {
        return rainbow(EffectMode.HORIZONTAL_LTR, 1.0f).glow().shadow().wave().pulse().bold();
    }

    
    public String getText() { return text; }
    public TextAlign getTextAlign() { return textAlign; }
    public VerticalAlign getVerticalAlign() { return verticalAlign; }
    public Set<AnimationType> getActiveAnimations() { return EnumSet.copyOf(activeAnimations); }
    public TextOverflowMode getOverflowMode() { return overflowMode; }
    public int getMaxLines() { return maxLines; }
    public String getEllipsis() { return ellipsis; }
    public int getSafetyMargin() { return safetyMargin; }
    public float getMinScale() { return minScale; }

    public void startAnimation() {
        this.animationStartTime = System.currentTimeMillis();
        if (activeAnimations.contains(AnimationType.TYPEWRITER)) {
            this.typewriterCharCount = 0;
            this.lastTypewriterUpdate = System.currentTimeMillis();
        }
        markNeedsUpdate();
    }

    public void stopAnimation() {
        this.activeAnimations.clear();
    }

    public void resetAnimation() {
        startAnimation();
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public interface TextEffect {
        void apply(TextComponent textComponent, DrawContext context, int x, int y);
    }

    @Override
    public TextComponent setColor(int color) {
        super.setColor(color);
        this.startColor = color;
        return this;
    }

}