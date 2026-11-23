package com.mentalmath.gui.components;

import com.mentalmath.gui.UITheme;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A modern flat button with anti-aliased rounded corners, smooth hover effects,
 * and support for various color themes (Primary, Success, Danger, Secondary).
 */
public class RoundedButton extends JButton {

    public enum Style {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        DANGER,
        OUTLINE
    }

    private final Style style;
    private int cornerRadius = 14;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private boolean isLoading = false;

    private Color normalBg;
    private Color hoverBg;
    private Color pressedBg;
    private Color textColor;
    private Color borderColor;

    public RoundedButton(String text) {
        this(text, Style.PRIMARY);
    }

    public RoundedButton(String text, Style style) {
        super(text);
        this.style = style;
        initStyle();

        setFont(UITheme.FONT_BOLD_14);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private void initStyle() {
        switch (style) {
            case SUCCESS:
                normalBg = UITheme.ACCENT_EMERALD;
                hoverBg = UITheme.EMERALD_HOVER;
                pressedBg = UITheme.ACCENT_EMERALD.darker();
                textColor = Color.WHITE;
                borderColor = null;
                break;
            case DANGER:
                normalBg = UITheme.DANGER;
                hoverBg = UITheme.DANGER_HOVER;
                pressedBg = UITheme.DANGER.darker();
                textColor = Color.WHITE;
                borderColor = null;
                break;
            case SECONDARY:
                normalBg = UITheme.SECONDARY;
                hoverBg = UITheme.SECONDARY_HOVER;
                pressedBg = UITheme.SECONDARY.darker();
                textColor = UITheme.TEXT_PRIMARY;
                borderColor = UITheme.INPUT_BORDER;
                break;
            case OUTLINE:
                normalBg = new Color(0, 0, 0, 0);
                hoverBg = new Color(139, 92, 246, 30);
                pressedBg = new Color(139, 92, 246, 60);
                textColor = UITheme.PRIMARY_HOVER;
                borderColor = UITheme.PRIMARY;
                break;
            case PRIMARY:
            default:
                normalBg = UITheme.PRIMARY;
                hoverBg = UITheme.PRIMARY_HOVER;
                pressedBg = UITheme.PRIMARY_DARK;
                textColor = Color.WHITE;
                borderColor = null;
                break;
        }
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        setEnabled(!loading);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        Color currentBg;
        if (!isEnabled()) {
            currentBg = UITheme.SECONDARY;
        } else if (isPressed) {
            currentBg = pressedBg;
        } else if (isHovered) {
            currentBg = hoverBg;
        } else {
            currentBg = normalBg;
        }

        // Paint background
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Paint border
        if (borderColor != null) {
            g2.setColor(isHovered ? UITheme.PRIMARY : borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // Paint text
        String displayText = isLoading ? "Loading..." : getText();
        g2.setColor(isEnabled() ? textColor : UITheme.TEXT_MUTED);
        g2.setFont(getFont());

        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(displayText)) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        g2.drawString(displayText, textX, textY);
        g2.dispose();
    }
}
