package com.edgn.ui.utils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused"})
public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([A-Fa-f0-9]{3,8})$");
    private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)$");
    private static final Pattern RGBA_PATTERN = Pattern.compile("^rgba\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*([0-9]*\\.?[0-9]+)\\s*\\)$");
    private static final Pattern HSL_PATTERN = Pattern.compile("^hsl\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*\\)$");
    private static final Pattern HSLA_PATTERN = Pattern.compile("^hsla\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)%\\s*,\\s*(\\d+)%\\s*,\\s*([0-9]*\\.?[0-9]+)\\s*\\)$");

    private static final Map<String, Integer> NAMED_COLORS = new HashMap<>();
    static {
        NAMED_COLORS.put("aliceblue", 0xFFF0F8FF);
        NAMED_COLORS.put("antiquewhite", 0xFFFAEBD7);
        NAMED_COLORS.put("aqua", 0xFF00FFFF);
        NAMED_COLORS.put("aquamarine", 0xFF7FFFD4);
        NAMED_COLORS.put("azure", 0xFFF0FFFF);
        NAMED_COLORS.put("beige", 0xFFF5F5DC);
        NAMED_COLORS.put("bisque", 0xFFFFE4C4);
        NAMED_COLORS.put("black", 0xFF000000);
        NAMED_COLORS.put("blanchedalmond", 0xFFFFEBCD);
        NAMED_COLORS.put("blue", 0xFF0000FF);
        NAMED_COLORS.put("blueviolet", 0xFF8A2BE2);
        NAMED_COLORS.put("brown", 0xFFA52A2A);
        NAMED_COLORS.put("burlywood", 0xFFDEB887);
        NAMED_COLORS.put("cadetblue", 0xFF5F9EA0);
        NAMED_COLORS.put("chartreuse", 0xFF7FFF00);
        NAMED_COLORS.put("chocolate", 0xFFD2691E);
        NAMED_COLORS.put("coral", 0xFFFF7F50);
        NAMED_COLORS.put("cornflowerblue", 0xFF6495ED);
        NAMED_COLORS.put("cornsilk", 0xFFFFF8DC);
        NAMED_COLORS.put("crimson", 0xFFDC143C);
        NAMED_COLORS.put("cyan", 0xFF00FFFF);
        NAMED_COLORS.put("darkblue", 0xFF00008B);
        NAMED_COLORS.put("darkcyan", 0xFF008B8B);
        NAMED_COLORS.put("darkgoldenrod", 0xFFB8860B);
        NAMED_COLORS.put("darkgray", 0xFFA9A9A9);
        NAMED_COLORS.put("darkgreen", 0xFF006400);
        NAMED_COLORS.put("darkkhaki", 0xFFBDB76B);
        NAMED_COLORS.put("darkmagenta", 0xFF8B008B);
        NAMED_COLORS.put("darkolivegreen", 0xFF556B2F);
        NAMED_COLORS.put("darkorange", 0xFFFF8C00);
        NAMED_COLORS.put("darkorchid", 0xFF9932CC);
        NAMED_COLORS.put("darkred", 0xFF8B0000);
        NAMED_COLORS.put("darksalmon", 0xFFE9967A);
        NAMED_COLORS.put("darkseagreen", 0xFF8FBC8F);
        NAMED_COLORS.put("darkslateblue", 0xFF483D8B);
        NAMED_COLORS.put("darkslategray", 0xFF2F4F4F);
        NAMED_COLORS.put("darkturquoise", 0xFF00CED1);
        NAMED_COLORS.put("darkviolet", 0xFF9400D3);
        NAMED_COLORS.put("deeppink", 0xFFFF1493);
        NAMED_COLORS.put("deepskyblue", 0xFF00BFFF);
        NAMED_COLORS.put("dimgray", 0xFF696969);
        NAMED_COLORS.put("dodgerblue", 0xFF1E90FF);
        NAMED_COLORS.put("firebrick", 0xFFB22222);
        NAMED_COLORS.put("floralwhite", 0xFFFFFAF0);
        NAMED_COLORS.put("forestgreen", 0xFF228B22);
        NAMED_COLORS.put("fuchsia", 0xFFFF00FF);
        NAMED_COLORS.put("gainsboro", 0xFFDCDCDC);
        NAMED_COLORS.put("ghostwhite", 0xFFF8F8FF);
        NAMED_COLORS.put("gold", 0xFFFFD700);
        NAMED_COLORS.put("goldenrod", 0xFFDAA520);
        NAMED_COLORS.put("gray", 0xFF808080);
        NAMED_COLORS.put("green", 0xFF008000);
        NAMED_COLORS.put("greenyellow", 0xFFADFF2F);
        NAMED_COLORS.put("honeydew", 0xFFF0FFF0);
        NAMED_COLORS.put("hotpink", 0xFFFF69B4);
        NAMED_COLORS.put("indianred", 0xFFCD5C5C);
        NAMED_COLORS.put("indigo", 0xFF4B0082);
        NAMED_COLORS.put("ivory", 0xFFFFFFF0);
        NAMED_COLORS.put("khaki", 0xFFF0E68C);
        NAMED_COLORS.put("lavender", 0xFFE6E6FA);
        NAMED_COLORS.put("lavenderblush", 0xFFFFF0F5);
        NAMED_COLORS.put("lawngreen", 0xFF7CFC00);
        NAMED_COLORS.put("lemonchiffon", 0xFFFFFACD);
        NAMED_COLORS.put("lightblue", 0xFFADD8E6);
        NAMED_COLORS.put("lightcoral", 0xFFF08080);
        NAMED_COLORS.put("lightcyan", 0xFFE0FFFF);
        NAMED_COLORS.put("lightgoldenrodyellow", 0xFFFAFAD2);
        NAMED_COLORS.put("lightgray", 0xFFD3D3D3);
        NAMED_COLORS.put("lightgreen", 0xFF90EE90);
        NAMED_COLORS.put("lightpink", 0xFFFFB6C1);
        NAMED_COLORS.put("lightsalmon", 0xFFFFA07A);
        NAMED_COLORS.put("lightseagreen", 0xFF20B2AA);
        NAMED_COLORS.put("lightskyblue", 0xFF87CEFA);
        NAMED_COLORS.put("lightslategray", 0xFF778899);
        NAMED_COLORS.put("lightsteelblue", 0xFFB0C4DE);
        NAMED_COLORS.put("lightyellow", 0xFFFFFFE0);
        NAMED_COLORS.put("lime", 0xFF00FF00);
        NAMED_COLORS.put("limegreen", 0xFF32CD32);
        NAMED_COLORS.put("linen", 0xFFFAF0E6);
        NAMED_COLORS.put("magenta", 0xFFFF00FF);
        NAMED_COLORS.put("maroon", 0xFF800000);
        NAMED_COLORS.put("mediumaquamarine", 0xFF66CDAA);
        NAMED_COLORS.put("mediumblue", 0xFF0000CD);
        NAMED_COLORS.put("mediumorchid", 0xFFBA55D3);
        NAMED_COLORS.put("mediumpurple", 0xFF9370DB);
        NAMED_COLORS.put("mediumseagreen", 0xFF3CB371);
        NAMED_COLORS.put("mediumslateblue", 0xFF7B68EE);
        NAMED_COLORS.put("mediumspringgreen", 0xFF00FA9A);
        NAMED_COLORS.put("mediumturquoise", 0xFF48D1CC);
        NAMED_COLORS.put("mediumvioletred", 0xFFC71585);
        NAMED_COLORS.put("midnightblue", 0xFF191970);
        NAMED_COLORS.put("mintcream", 0xFFF5FFFA);
        NAMED_COLORS.put("mistyrose", 0xFFFFE4E1);
        NAMED_COLORS.put("moccasin", 0xFFFFE4B5);
        NAMED_COLORS.put("navajowhite", 0xFFFFDEAD);
        NAMED_COLORS.put("navy", 0xFF000080);
        NAMED_COLORS.put("oldlace", 0xFFFDF5E6);
        NAMED_COLORS.put("olive", 0xFF808000);
        NAMED_COLORS.put("olivedrab", 0xFF6B8E23);
        NAMED_COLORS.put("orange", 0xFFFFA500);
        NAMED_COLORS.put("orangered", 0xFFFF4500);
        NAMED_COLORS.put("orchid", 0xFFDA70D6);
        NAMED_COLORS.put("palegoldenrod", 0xFFEEE8AA);
        NAMED_COLORS.put("palegreen", 0xFF98FB98);
        NAMED_COLORS.put("paleturquoise", 0xFFAFEEEE);
        NAMED_COLORS.put("palevioletred", 0xFFDB7093);
        NAMED_COLORS.put("papayawhip", 0xFFFFEFD5);
        NAMED_COLORS.put("peachpuff", 0xFFFFDAB9);
        NAMED_COLORS.put("peru", 0xFFCD853F);
        NAMED_COLORS.put("pink", 0xFFFFC0CB);
        NAMED_COLORS.put("plum", 0xFFDDA0DD);
        NAMED_COLORS.put("powderblue", 0xFFB0E0E6);
        NAMED_COLORS.put("purple", 0xFF800080);
        NAMED_COLORS.put("red", 0xFFFF0000);
        NAMED_COLORS.put("rosybrown", 0xFFBC8F8F);
        NAMED_COLORS.put("royalblue", 0xFF4169E1);
        NAMED_COLORS.put("saddlebrown", 0xFF8B4513);
        NAMED_COLORS.put("salmon", 0xFFFA8072);
        NAMED_COLORS.put("sandybrown", 0xFFF4A460);
        NAMED_COLORS.put("seagreen", 0xFF2E8B57);
        NAMED_COLORS.put("seashell", 0xFFFFF5EE);
        NAMED_COLORS.put("sienna", 0xFFA0522D);
        NAMED_COLORS.put("silver", 0xFFC0C0C0);
        NAMED_COLORS.put("skyblue", 0xFF87CEEB);
        NAMED_COLORS.put("slateblue", 0xFF6A5ACD);
        NAMED_COLORS.put("slategray", 0xFF708090);
        NAMED_COLORS.put("snow", 0xFFFFFAFA);
        NAMED_COLORS.put("springgreen", 0xFF00FF7F);
        NAMED_COLORS.put("steelblue", 0xFF4682B4);
        NAMED_COLORS.put("tan", 0xFFD2B48C);
        NAMED_COLORS.put("teal", 0xFF008080);
        NAMED_COLORS.put("thistle", 0xFFD8BFD8);
        NAMED_COLORS.put("tomato", 0xFFFF6347);
        NAMED_COLORS.put("turquoise", 0xFF40E0D0);
        NAMED_COLORS.put("violet", 0xFFEE82EE);
        NAMED_COLORS.put("wheat", 0xFFF5DEB3);
        NAMED_COLORS.put("white", 0xFFFFFFFF);
        NAMED_COLORS.put("whitesmoke", 0xFFF5F5F5);
        NAMED_COLORS.put("yellow", 0xFFFFFF00);
        NAMED_COLORS.put("yellowgreen", 0xFF9ACD32);
        NAMED_COLORS.put("transparent", 0x00000000);
    }

    public static class RGBA {
        public final int r, g, b, a;

        public RGBA(int r, int g, int b) { this(r, g, b, 255); }
        public RGBA(int r, int g, int b, int a) {
            this.r = clamp(r, 0, 255);
            this.g = clamp(g, 0, 255);
            this.b = clamp(b, 0, 255);
            this.a = clamp(a, 0, 255);
        }

        public int toInt() { return (a << 24) | (r << 16) | (g << 8) | b; }
        public String toHex() { return String.format("#%02X%02X%02X%02X", r, g, b, a); }
        public String toHexNoAlpha() { return String.format("#%02X%02X%02X", r, g, b); }
        public String toRgb() { return String.format("rgb(%d, %d, %d)", r, g, b); }
        public String toRgba() { return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, a / 255.0f); }

        @Override
        public String toString() { return toRgba(); }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof RGBA other)) return false;
            return r == other.r && g == other.g && b == other.b && a == other.a;
        }

        @Override
        public int hashCode() { return toInt(); }
    }

    public static class HSV {
        public final float h, s, v, a;

        public HSV(float h, float s, float v) { this(h, s, v, 1.0f); }
        public HSV(float h, float s, float v, float a) {
            this.h = h % 360.0f;
            this.s = clamp(s, 0.0f, 1.0f);
            this.v = clamp(v, 0.0f, 1.0f);
            this.a = clamp(a, 0.0f, 1.0f);
        }

        public RGBA toRGBA() { return ColorUtils.hsvToRgba(this); }
        public String toString() { return String.format("hsv(%.1f, %.1f%%, %.1f%%)", h, s * 100, v * 100); }
    }

    public static class HSL {
        public final float h, s, l, a;

        public HSL(float h, float s, float l) { this(h, s, l, 1.0f); }
        public HSL(float h, float s, float l, float a) {
            this.h = h % 360.0f;
            this.s = clamp(s, 0.0f, 1.0f);
            this.l = clamp(l, 0.0f, 1.0f);
            this.a = clamp(a, 0.0f, 1.0f);
        }

        public RGBA toRGBA() { return ColorUtils.hslToRgba(this); }
        public String toString() { return String.format("hsl(%.1f, %.1f%%, %.1f%%)", h, s * 100, l * 100); }
    }

    public static RGBA parse(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            throw new IllegalArgumentException("Color string cannot be null or empty");
        }

        String color = colorString.trim().toLowerCase();

        if (NAMED_COLORS.containsKey(color)) {
            return fromInt(NAMED_COLORS.get(color));
        }

        Matcher hexMatcher = HEX_PATTERN.matcher(color);
        if (hexMatcher.matches()) {
            return parseHex(hexMatcher.group(1));
        }

        Matcher rgbMatcher = RGB_PATTERN.matcher(color);
        if (rgbMatcher.matches()) {
            return new RGBA(
                Integer.parseInt(rgbMatcher.group(1)),
                Integer.parseInt(rgbMatcher.group(2)),
                Integer.parseInt(rgbMatcher.group(3))
            );
        }

        Matcher rgbaMatcher = RGBA_PATTERN.matcher(color);
        if (rgbaMatcher.matches()) {
            return new RGBA(
                Integer.parseInt(rgbaMatcher.group(1)),
                Integer.parseInt(rgbaMatcher.group(2)),
                Integer.parseInt(rgbaMatcher.group(3)),
                (int)(Float.parseFloat(rgbaMatcher.group(4)) * 255)
            );
        }

        Matcher hslMatcher = HSL_PATTERN.matcher(color);
        if (hslMatcher.matches()) {
            return hslToRgba(new HSL(
                Float.parseFloat(hslMatcher.group(1)),
                Float.parseFloat(hslMatcher.group(2)) / 100.0f,
                Float.parseFloat(hslMatcher.group(3)) / 100.0f
            ));
        }

        Matcher hslaMatcher = HSLA_PATTERN.matcher(color);
        if (hslaMatcher.matches()) {
            return hslToRgba(new HSL(
                Float.parseFloat(hslaMatcher.group(1)),
                Float.parseFloat(hslaMatcher.group(2)) / 100.0f,
                Float.parseFloat(hslaMatcher.group(3)) / 100.0f,
                Float.parseFloat(hslaMatcher.group(4))
            ));
        }

        throw new IllegalArgumentException("Invalid color format: " + colorString);
    }

    private static RGBA parseHex(String hex) {
        return switch (hex.length()) {
            case 3 -> {
                int r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
                int g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
                int b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
                yield new RGBA(r, g, b);
            }
            case 4 -> {
                int r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
                int g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
                int b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
                int a = Integer.parseInt(hex.substring(3, 4), 16) * 17;
                yield new RGBA(r, g, b, a);
            }
            case 6 -> {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                yield new RGBA(r, g, b);
            }
            case 8 -> {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                int a = Integer.parseInt(hex.substring(6, 8), 16);
                yield new RGBA(r, g, b, a);
            }
            default -> throw new IllegalArgumentException("Invalid hex color length: " + hex);
        };
    }

    public static RGBA fromInt(int color) {
        return new RGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF);
    }

    public static RGBA fromIntNoAlpha(int color) {
        return new RGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255);
    }

    public static RGBA fromColor(Color color) {
        return new RGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color toColor(RGBA rgba) {
        return new Color(rgba.r, rgba.g, rgba.b, rgba.a);
    }

    public static HSV rgbaToHsv(RGBA rgba) {
        float r = rgba.r / 255.0f;
        float g = rgba.g / 255.0f;
        float b = rgba.b / 255.0f;
        float a = rgba.a / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * (((g - b) / delta) % 6);
            } else if (max == g) {
                h = 60 * (((b - r) / delta) + 2);
            } else {
                h = 60 * (((r - g) / delta) + 4);
            }
        }
        if (h < 0) h += 360;

        float s = max == 0 ? 0 : delta / max;
        float v = max;

        return new HSV(h, s, v, a);
    }

    public static RGBA hsvToRgba(HSV hsv) {
        float c = hsv.v * hsv.s;
        float x = c * (1 - Math.abs(((hsv.h / 60) % 2) - 1));
        float m = hsv.v - c;

        float r, g, b;
        if (hsv.h < 60) {
            r = c; g = x; b = 0;
        } else if (hsv.h < 120) {
            r = x; g = c; b = 0;
        } else if (hsv.h < 180) {
            r = 0; g = c; b = x;
        } else if (hsv.h < 240) {
            r = 0; g = x; b = c;
        } else if (hsv.h < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }

        return new RGBA(
            Math.round((r + m) * 255),
            Math.round((g + m) * 255),
            Math.round((b + m) * 255),
            Math.round(hsv.a * 255)
        );
    }

    public static HSL rgbaToHsl(RGBA rgba) {
        float r = rgba.r / 255.0f;
        float g = rgba.g / 255.0f;
        float b = rgba.b / 255.0f;
        float a = rgba.a / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * (((g - b) / delta) % 6);
            } else if (max == g) {
                h = 60 * (((b - r) / delta) + 2);
            } else {
                h = 60 * (((r - g) / delta) + 4);
            }
        }
        if (h < 0) h += 360;

        float l = (max + min) / 2;
        float s = l == 0 || l == 1 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        return new HSL(h, s, l, a);
    }

    public static RGBA hslToRgba(HSL hsl) {
        float c = (1 - Math.abs(2 * hsl.l - 1)) * hsl.s;
        float x = c * (1 - Math.abs(((hsl.h / 60) % 2) - 1));
        float m = hsl.l - c / 2;

        float r, g, b;
        if (hsl.h < 60) {
            r = c; g = x; b = 0;
        } else if (hsl.h < 120) {
            r = x; g = c; b = 0;
        } else if (hsl.h < 180) {
            r = 0; g = c; b = x;
        } else if (hsl.h < 240) {
            r = 0; g = x; b = c;
        } else if (hsl.h < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }

        return new RGBA(
            Math.round((r + m) * 255),
            Math.round((g + m) * 255),
            Math.round((b + m) * 255),
            Math.round(hsl.a * 255)
        );
    }

    public static RGBA lighten(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, hsl.s, Math.min(1.0f, hsl.l + amount), hsl.a));
    }

    public static RGBA darken(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, hsl.s, Math.max(0.0f, hsl.l - amount), hsl.a));
    }

    public static RGBA saturate(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, Math.min(1.0f, hsl.s + amount), hsl.l, hsl.a));
    }

    public static RGBA desaturate(RGBA color, float amount) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h, Math.max(0.0f, hsl.s - amount), hsl.l, hsl.a));
    }

    public static RGBA adjustHue(RGBA color, float degrees) {
        HSL hsl = rgbaToHsl(color);
        return hslToRgba(new HSL(hsl.h + degrees, hsl.s, hsl.l, hsl.a));
    }

    public static RGBA withAlpha(RGBA color, int alpha) {
        return new RGBA(color.r, color.g, color.b, alpha);
    }

    public static RGBA withAlpha(RGBA color, float alpha) {
        return new RGBA(color.r, color.g, color.b, Math.round(alpha * 255));
    }

    public static RGBA invert(RGBA color) {
        return new RGBA(255 - color.r, 255 - color.g, 255 - color.b, color.a);
    }

    public static RGBA complement(RGBA color) {
        return adjustHue(color, 180);
    }

    public static RGBA grayscale(RGBA color) {
        int gray = (int)(0.299 * color.r + 0.587 * color.g + 0.114 * color.b);
        return new RGBA(gray, gray, gray, color.a);
    }

    public static RGBA mix(RGBA color1, RGBA color2, float ratio) {
        ratio = clamp(ratio, 0.0f, 1.0f);
        return new RGBA(
            Math.round(color1.r + (color2.r - color1.r) * ratio),
            Math.round(color1.g + (color2.g - color1.g) * ratio),
            Math.round(color1.b + (color2.b - color1.b) * ratio),
            Math.round(color1.a + (color2.a - color1.a) * ratio)
        );
    }

    public static RGBA overlay(RGBA base, RGBA overlay) {
        float alpha = overlay.a / 255.0f;
        float invAlpha = 1.0f - alpha;
        
        return new RGBA(
            Math.round(base.r * invAlpha + overlay.r * alpha),
            Math.round(base.g * invAlpha + overlay.g * alpha),
            Math.round(base.b * invAlpha + overlay.b * alpha),
            Math.max(base.a, overlay.a)
        );
    }

    public static RGBA multiply(RGBA color1, RGBA color2) {
        return new RGBA(
            (color1.r * color2.r) / 255,
            (color1.g * color2.g) / 255,
            (color1.b * color2.b) / 255,
            (color1.a * color2.a) / 255
        );
    }

    public static RGBA screen(RGBA color1, RGBA color2) {
        return new RGBA(
            255 - ((255 - color1.r) * (255 - color2.r)) / 255,
            255 - ((255 - color1.g) * (255 - color2.g)) / 255,
            255 - ((255 - color1.b) * (255 - color2.b)) / 255,
            255 - ((255 - color1.a) * (255 - color2.a)) / 255
        );
    }

    public static RGBA additive(RGBA color1, RGBA color2) {
        return new RGBA(
            Math.min(255, color1.r + color2.r),
            Math.min(255, color1.g + color2.g),
            Math.min(255, color1.b + color2.b),
            Math.min(255, color1.a + color2.a)
        );
    }

    public static RGBA subtractive(RGBA color1, RGBA color2) {
        return new RGBA(
            Math.max(0, color1.r - color2.r),
            Math.max(0, color1.g - color2.g),
            Math.max(0, color1.b - color2.b),
            Math.max(0, color1.a - color2.a)
        );
    }

    public static RGBA[] complementaryPalette(RGBA baseColor) {
        return new RGBA[]{
            baseColor,
            complement(baseColor)
        };
    }

    public static RGBA[] triadicPalette(RGBA baseColor) {
        return new RGBA[]{
            baseColor,
            adjustHue(baseColor, 120),
            adjustHue(baseColor, 240)
        };
    }

    public static RGBA[] tetradicPalette(RGBA baseColor) {
        return new RGBA[]{
            baseColor,
            adjustHue(baseColor, 90),
            adjustHue(baseColor, 180),
            adjustHue(baseColor, 270)
        };
    }

    public static RGBA[] analogousPalette(RGBA baseColor) {
        return new RGBA[]{
            adjustHue(baseColor, -60),
            adjustHue(baseColor, -30),
            baseColor,
            adjustHue(baseColor, 30),
            adjustHue(baseColor, 60)
        };
    }

    public static RGBA[] splitComplementaryPalette(RGBA baseColor) {
        return new RGBA[]{
            baseColor,
            adjustHue(baseColor, 150),
            adjustHue(baseColor, 210)
        };
    }

    public static RGBA[] monochromaticPalette(RGBA baseColor, int count) {
        RGBA[] palette = new RGBA[count];
        HSL baseHsl = rgbaToHsl(baseColor);
        
        for (int i = 0; i < count; i++) {
            float lightness = (float) i / (count - 1);
            palette[i] = hslToRgba(new HSL(baseHsl.h, baseHsl.s, lightness, baseHsl.a));
        }
        
        return palette;
    }

    public static RGBA[] gradientPalette(RGBA startColor, RGBA endColor, int steps) {
        RGBA[] palette = new RGBA[steps];
        
        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / (steps - 1);
            palette[i] = mix(startColor, endColor, ratio);
        }
        
        return palette;
    }

    public static float getLuminance(RGBA color) {
        float r = color.r / 255.0f;
        float g = color.g / 255.0f;
        float b = color.b / 255.0f;

        r = r <= 0.03928f ? r / 12.92f : (float) Math.pow((r + 0.055f) / 1.055f, 2.4f);
        g = g <= 0.03928f ? g / 12.92f : (float) Math.pow((g + 0.055f) / 1.055f, 2.4f);
        b = b <= 0.03928f ? b / 12.92f : (float) Math.pow((b + 0.055f) / 1.055f, 2.4f);

        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    public static float getContrastRatio(RGBA color1, RGBA color2) {
        float lum1 = getLuminance(color1);
        float lum2 = getLuminance(color2);
        
        float brightest = Math.max(lum1, lum2);
        float darkest = Math.min(lum1, lum2);
        
        return (brightest + 0.05f) / (darkest + 0.05f);
    }

    public static boolean isAccessible(RGBA foreground, RGBA background, boolean largeText) {
        float ratio = getContrastRatio(foreground, background);
        return largeText ? ratio >= 3.0f : ratio >= 4.5f;
    }

    public static boolean isAccessibleAAA(RGBA foreground, RGBA background, boolean largeText) {
        float ratio = getContrastRatio(foreground, background);
        return largeText ? ratio >= 4.5f : ratio >= 7.0f;
    }

    public static boolean isDark(RGBA color) {
        return getLuminance(color) < 0.5f;
    }

    public static boolean isLight(RGBA color) {
        return !isDark(color);
    }

    public static RGBA getBestTextColor(RGBA backgroundColor) {
        return isDark(backgroundColor) ? 
            new RGBA(255, 255, 255, 255) : 
            new RGBA(0, 0, 0, 255);
    }

    public static float getDistance(RGBA color1, RGBA color2) {
        int dr = color1.r - color2.r;
        int dg = color1.g - color2.g;
        int db = color1.b - color2.b;
        return (float) Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static float getPerceptualDistance(RGBA color1, RGBA color2) {
        HSL hsl1 = rgbaToHsl(color1);
        HSL hsl2 = rgbaToHsl(color2);
        
        float dh = Math.min(Math.abs(hsl1.h - hsl2.h), 360 - Math.abs(hsl1.h - hsl2.h));
        float ds = Math.abs(hsl1.s - hsl2.s);
        float dl = Math.abs(hsl1.l - hsl2.l);
        
        return (float) Math.sqrt(dh * dh + ds * ds * 100 + dl * dl * 100);
    }

    public static RGBA fromTemperature(int kelvin) {
        float temp = kelvin / 100.0f;
        float red, green, blue;

        if (temp <= 66) {
            red = 255;
        } else {
            red = temp - 60;
            red = (float) (329.698727446 * Math.pow(red, -0.1332047592));
            red = clamp(red, 0, 255);
        }

        if (temp <= 66) {
            green = temp;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
        } else {
            green = temp - 60;
            green = (float) (288.1221695283 * Math.pow(green, -0.0755148492));
        }
        green = clamp(green, 0, 255);

        if (temp >= 66) {
            blue = 255;
        } else if (temp <= 19) {
            blue = 0;
        } else {
            blue = temp - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            blue = clamp(blue, 0, 255);
        }

        return new RGBA(Math.round(red), Math.round(green), Math.round(blue));
    }

    public static RGBA warmColor(RGBA color, float intensity) {
        intensity = clamp(intensity, 0.0f, 1.0f);
        return mix(color, new RGBA(255, 200, 100), intensity * 0.3f);
    }

    public static RGBA coolColor(RGBA color, float intensity) {
        intensity = clamp(intensity, 0.0f, 1.0f);
        return mix(color, new RGBA(100, 200, 255), intensity * 0.3f);
    }

    public static RGBA interpolate(RGBA from, RGBA to, float progress) {
        return interpolate(from, to, progress, InterpolationMode.LINEAR);
    }

    public static RGBA interpolate(RGBA from, RGBA to, float progress, InterpolationMode mode) {
        progress = clamp(progress, 0.0f, 1.0f);
        
        switch (mode) {
            case EASE_IN -> progress = progress * progress;
            case EASE_OUT -> progress = 1 - (1 - progress) * (1 - progress);
            case EASE_IN_OUT -> progress = progress < 0.5f ? 
                2 * progress * progress : 
                1 - 2 * (1 - progress) * (1 - progress);
            case HSV -> {
                HSV fromHsv = rgbaToHsv(from);
                HSV toHsv = rgbaToHsv(to);
                
                float h = interpolateHue(fromHsv.h, toHsv.h, progress);
                float s = fromHsv.s + (toHsv.s - fromHsv.s) * progress;
                float v = fromHsv.v + (toHsv.v - fromHsv.v) * progress;
                float a = fromHsv.a + (toHsv.a - fromHsv.a) * progress;
                
                return hsvToRgba(new HSV(h, s, v, a));
            }
            case HSL -> {
                HSL fromHsl = rgbaToHsl(from);
                HSL toHsl = rgbaToHsl(to);
                
                float h = interpolateHue(fromHsl.h, toHsl.h, progress);
                float s = fromHsl.s + (toHsl.s - fromHsl.s) * progress;
                float l = fromHsl.l + (toHsl.l - fromHsl.l) * progress;
                float a = fromHsl.a + (toHsl.a - fromHsl.a) * progress;
                
                return hslToRgba(new HSL(h, s, l, a));
            }
        }
        
        return mix(from, to, progress);
    }

    private static float interpolateHue(float from, float to, float progress) {
        float diff = to - from;
        if (Math.abs(diff) > 180) {
            if (diff > 0) {
                from += 360;
            } else {
                to += 360;
            }
        }
        float result = from + (to - from) * progress;
        return result < 0 ? result + 360 : result % 360;
    }

    public enum InterpolationMode {
        LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT, HSV, HSL
    }

    public static RGBA randomColor() {
        return new RGBA(
            (int)(Math.random() * 256),
            (int)(Math.random() * 256),
            (int)(Math.random() * 256)
        );
    }

    public static RGBA randomColor(int seed) {
        java.util.Random random = new java.util.Random(seed);
        return new RGBA(
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        );
    }

    public static RGBA randomPastelColor() {
        HSL hsl = new HSL(
            (float)(Math.random() * 360),
            0.3f + (float)(Math.random() * 0.4f),
            0.7f + (float)(Math.random() * 0.2f)
        );
        return hslToRgba(hsl);
    }

    public static RGBA randomVibrantColor() {
        HSL hsl = new HSL(
            (float)(Math.random() * 360),
            0.7f + (float)(Math.random() * 0.3f),
            0.4f + (float)(Math.random() * 0.4f)
        );
        return hslToRgba(hsl);
    }

    public static RGBA[] generatePalette(int count, PaletteType type) {
        return generatePalette(count, type, (int)(Math.random() * 1000));
    }

    public static RGBA[] generatePalette(int count, PaletteType type, int seed) {
        java.util.Random random = new java.util.Random(seed);
        RGBA[] palette = new RGBA[count];
        
        switch (type) {
            case RANDOM -> {
                for (int i = 0; i < count; i++) {
                    palette[i] = randomColor(seed + i);
                }
            }
            case PASTEL -> {
                for (int i = 0; i < count; i++) {
                    random.setSeed(seed + i);
                    HSL hsl = new HSL(
                        random.nextFloat() * 360,
                        0.3f + random.nextFloat() * 0.4f,
                        0.7f + random.nextFloat() * 0.2f
                    );
                    palette[i] = hslToRgba(hsl);
                }
            }
            case VIBRANT -> {
                for (int i = 0; i < count; i++) {
                    random.setSeed(seed + i);
                    HSL hsl = new HSL(
                        random.nextFloat() * 360,
                        0.7f + random.nextFloat() * 0.3f,
                        0.4f + random.nextFloat() * 0.4f
                    );
                    palette[i] = hslToRgba(hsl);
                }
            }
            case MONOCHROMATIC -> {
                float baseHue = random.nextFloat() * 360;
                for (int i = 0; i < count; i++) {
                    float lightness = (float) i / (count - 1);
                    HSL hsl = new HSL(baseHue, 0.7f, lightness);
                    palette[i] = hslToRgba(hsl);
                }
            }
            case COMPLEMENTARY -> {
                float baseHue = random.nextFloat() * 360;
                for (int i = 0; i < count; i++) {
                    float hue = i % 2 == 0 ? baseHue : (baseHue + 180) % 360;
                    HSL hsl = new HSL(hue, 0.7f, 0.5f + (i / (float)count) * 0.3f);
                    palette[i] = hslToRgba(hsl);
                }
            }
        }
        
        return palette;
    }

    public enum PaletteType {
        RANDOM, PASTEL, VIBRANT, MONOCHROMATIC, COMPLEMENTARY
    }

    public static final class Colors {
        public static final RGBA TRANSPARENT = new RGBA(0, 0, 0, 0);
        public static final RGBA BLACK = new RGBA(0, 0, 0);
        public static final RGBA WHITE = new RGBA(255, 255, 255);
        public static final RGBA RED = new RGBA(255, 0, 0);
        public static final RGBA GREEN = new RGBA(0, 255, 0);
        public static final RGBA BLUE = new RGBA(0, 0, 255);
        public static final RGBA YELLOW = new RGBA(255, 255, 0);
        public static final RGBA CYAN = new RGBA(0, 255, 255);
        public static final RGBA MAGENTA = new RGBA(255, 0, 255);
        public static final RGBA ORANGE = new RGBA(255, 165, 0);
        public static final RGBA PURPLE = new RGBA(128, 0, 128);
        public static final RGBA PINK = new RGBA(255, 192, 203);
        public static final RGBA GRAY = new RGBA(128, 128, 128);
        public static final RGBA LIGHT_GRAY = new RGBA(211, 211, 211);
        public static final RGBA DARK_GRAY = new RGBA(64, 64, 64);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int parseToInt(String colorString) {
        return parse(colorString).toInt();
    }

    public static String parseToHex(String colorString) {
        return parse(colorString).toHex();
    }

    public static boolean isValidColor(String colorString) {
        try {
            parse(colorString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] getNamedColors() {
        return NAMED_COLORS.keySet().toArray(new String[0]);
    }

    public static String findClosestNamedColor(RGBA color) {
        String closestName = "black";
        float closestDistance = Float.MAX_VALUE;
        
        for (Map.Entry<String, Integer> entry : NAMED_COLORS.entrySet()) {
            RGBA namedColor = fromInt(entry.getValue());
            float distance = getDistance(color, namedColor);
            
            if (distance < closestDistance) {
                closestDistance = distance;
                closestName = entry.getKey();
            }
        }
        
        return closestName;
    }
}