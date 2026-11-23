package com.mentalmath.gui.components;

import com.mentalmath.gui.UITheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

/**
 * A modern card container with smooth rounded corners, translucent dark background,
 * and an optional subtle border stroke.
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderWidth;

    public RoundedPanel() {
        this(20, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
    }

    public RoundedPanel(int radius) {
        this(radius, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
    }

    public RoundedPanel(int radius, Color backgroundColor, Color borderColor, int borderWidth) {
        this.cornerRadius = radius;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        this.cornerRadius = 20;
        this.backgroundColor = UITheme.CARD_BG;
        this.borderColor = UITheme.CARD_BORDER;
        this.borderWidth = 1;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        // Fill rounded card background
        g2.setColor(backgroundColor != null ? backgroundColor : UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw subtle border if specified
        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.drawRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
