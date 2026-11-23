package com.mentalmath.gui.components;

import com.mentalmath.gui.UITheme;

import javax.swing.JPanel;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

/**
 * A JPanel that renders a modern dark navy to purple linear gradient background.
 */
public class GradientBackgroundPanel extends JPanel {

    public GradientBackgroundPanel() {
        super();
        setOpaque(false);
    }

    public GradientBackgroundPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        // Diagonal gradient from top-left (Navy) to bottom-right (Purple)
        GradientPaint gp = new GradientPaint(
                0, 0, UITheme.BG_DARK_START,
                w, h, UITheme.BG_DARK_END
        );

        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}
