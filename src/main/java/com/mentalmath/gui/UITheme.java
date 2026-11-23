package com.mentalmath.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Central design tokens, color palette, fonts, and rendering utilities
 * for the Mental Math Game dark navy and purple gradient theme.
 */
public class UITheme {

    // Background Gradient Colors
    public static final Color BG_DARK_START = new Color(11, 15, 25);    // Deep Dark Navy
    public static final Color BG_DARK_END   = new Color(30, 16, 58);    // Deep Velvet Purple

    // Card & Surface Colors
    public static final Color CARD_BG       = new Color(21, 28, 48, 220); // Translucent Dark Slate
    public static final Color CARD_BG_SOLID = new Color(23, 31, 54);
    public static final Color CARD_BORDER   = new Color(91, 33, 182, 140); // Purple glow border
    public static final Color CARD_BORDER_HOVER = new Color(139, 92, 246, 200);

    // Accent Colors
    public static final Color PRIMARY       = new Color(139, 92, 246);  // Vibrant Purple
    public static final Color PRIMARY_HOVER = new Color(167, 139, 250);  // Lighter Purple
    public static final Color PRIMARY_DARK  = new Color(109, 40, 217);  // Deep Purple
    public static final Color SECONDARY     = new Color(30, 41, 59);    // Slate Dark
    public static final Color SECONDARY_HOVER = new Color(51, 65, 85);
    public static final Color ACCENT_CYAN   = new Color(56, 189, 248);  // Cyan
    public static final Color ACCENT_EMERALD= new Color(16, 185, 129);  // Success Emerald Green
    public static final Color EMERALD_HOVER = new Color(52, 211, 153);
    public static final Color DANGER        = new Color(239, 68, 68);   // Error Red
    public static final Color DANGER_HOVER  = new Color(248, 113, 113);
    public static final Color WARNING       = new Color(245, 158, 11);  // Amber Yellow

    // Text Colors
    public static final Color TEXT_PRIMARY   = new Color(248, 250, 252); // White
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184); // Muted slate gray
    public static final Color TEXT_MUTED     = new Color(100, 116, 139); // Dim gray

    // Input Colors
    public static final Color INPUT_BG           = new Color(15, 23, 42);
    public static final Color INPUT_BORDER       = new Color(51, 65, 85);
    public static final Color INPUT_BORDER_FOCUS = new Color(139, 92, 246);

    // Typography
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_HERO_TITLE     = new Font(FONT_FAMILY, Font.BOLD, 34);
    public static final Font FONT_TITLE          = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_SUBTITLE       = new Font(FONT_FAMILY, Font.PLAIN, 15);
    public static final Font FONT_SECTION_HEADER = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_BOLD_16        = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_REGULAR_15     = new Font(FONT_FAMILY, Font.PLAIN, 15);
    public static final Font FONT_REGULAR_14     = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BOLD_14        = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL          = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_MATH_QUESTION  = new Font(FONT_FAMILY, Font.BOLD, 44);
    public static final Font FONT_BIG_METRIC     = new Font(FONT_FAMILY, Font.BOLD, 36);

    /**
     * Enables high-quality anti-aliasing for both shapes and text.
     */
    public static void enableAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
