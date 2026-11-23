package com.mentalmath.gui.components;

import com.mentalmath.gui.UITheme;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A sleek, dark-themed text field with rounded corners, focus glow border,
 * and built-in placeholder text support.
 */
public class ModernTextField extends JTextField {

    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 12;

    public ModernTextField() {
        this("", 20);
    }

    public ModernTextField(String placeholder) {
        this(placeholder, 20);
    }

    public ModernTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;

        setOpaque(false);
        setBackground(UITheme.INPUT_BG);
        setForeground(UITheme.TEXT_PRIMARY);
        setCaretColor(UITheme.PRIMARY_HOVER);
        setFont(UITheme.FONT_REGULAR_15);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        // Fill background
        g2.setColor(UITheme.INPUT_BG);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw border (purple if focused, slate if idle)
        if (isFocused) {
            g2.setColor(UITheme.INPUT_BORDER_FOCUS);
            g2.setStroke(new BasicStroke(2f));
        } else {
            g2.setColor(UITheme.INPUT_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
        }
        g2.drawRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Render placeholder if text is empty and not focused
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
