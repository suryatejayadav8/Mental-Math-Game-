package com.mentalmath.gui.components;

import com.mentalmath.gui.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * A customized JTable styled for dark navy and purple themes, with alternating
 * row colors, generous cell height, and clean table headers.
 */
public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(38);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setOpaque(false);
        setBackground(UITheme.CARD_BG_SOLID);
        setForeground(UITheme.TEXT_PRIMARY);
        setSelectionBackground(new Color(109, 40, 217, 180)); // Vibrant purple selection
        setSelectionForeground(Color.WHITE);
        setFont(UITheme.FONT_REGULAR_14);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setBackground(new Color(30, 20, 60)); // Dark purple header
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setFont(UITheme.FONT_BOLD_14);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.PRIMARY));
        header.setReorderingAllowed(false);

        // Header cell renderer
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        // Cell Alignment & Alternating Row Colors
        setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                defaultRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(24, 30, 50));
                    } else {
                        c.setBackground(new Color(18, 24, 40));
                    }
                    c.setForeground(UITheme.TEXT_PRIMARY);
                } else {
                    c.setBackground(new Color(109, 40, 217));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
    }

    /**
     * Creates a styled JScrollPane wrapping this table.
     */
    public JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.BG_DARK_START);
        return scrollPane;
    }
}
