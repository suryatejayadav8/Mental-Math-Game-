package com.mentalmath.gui.screens;

import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * The initial landing screen presenting the game title, subtitle,
 * quick action buttons (Play Now, Login, Register), and an instructions card.
 */
public class WelcomePanel extends JPanel {

    private final MainFrame mainFrame;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initUI();
    }

    private void initUI() {
        // Center Container
        JPanel centerContainer = new JPanel();
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        // 1. Header Section
        JLabel iconLabel = new JLabel("⚡");
        iconLabel.setFont(UITheme.FONT_HERO_TITLE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("MENTAL MATH GAME");
        titleLabel.setFont(UITheme.FONT_HERO_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Improve Your Calculation Speed & Mental Agility");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.ACCENT_CYAN);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContainer.add(Box.createVerticalStrut(10));
        centerContainer.add(iconLabel);
        centerContainer.add(Box.createVerticalStrut(10));
        centerContainer.add(titleLabel);
        centerContainer.add(Box.createVerticalStrut(8));
        centerContainer.add(subtitleLabel);
        centerContainer.add(Box.createVerticalStrut(30));

        // 2. Action Buttons Card
        RoundedPanel buttonCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        buttonCard.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonCard.setMaximumSize(new Dimension(650, 85));

        RoundedButton playNowBtn = new RoundedButton("🚀  Play Now", RoundedButton.Style.SUCCESS);
        playNowBtn.setPreferredSize(new Dimension(170, 44));
        playNowBtn.addActionListener(e -> {
            if (mainFrame.getCurrentPlayer() != null) {
                mainFrame.showDashboard();
            } else {
                mainFrame.showLogin();
            }
        });

        RoundedButton loginBtn = new RoundedButton("🔑  Login", RoundedButton.Style.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(150, 44));
        loginBtn.addActionListener(e -> mainFrame.showLogin());

        RoundedButton registerBtn = new RoundedButton("✨  Register", RoundedButton.Style.SECONDARY);
        registerBtn.setPreferredSize(new Dimension(150, 44));
        registerBtn.addActionListener(e -> mainFrame.showRegister());

        buttonCard.add(playNowBtn);
        buttonCard.add(loginBtn);
        buttonCard.add(registerBtn);

        centerContainer.add(buttonCard);
        centerContainer.add(Box.createVerticalStrut(30));

        // 3. Instructions & Features Card
        RoundedPanel instructionsCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        instructionsCard.setLayout(new BorderLayout());
        instructionsCard.setBorder(new EmptyBorder(22, 28, 22, 28));
        instructionsCard.setMaximumSize(new Dimension(750, 260));

        JLabel instructionsHeader = new JLabel("🎮 How It Works & Game Rules");
        instructionsHeader.setFont(UITheme.FONT_SECTION_HEADER);
        instructionsHeader.setForeground(UITheme.PRIMARY_HOVER);

        JPanel rulesGrid = new JPanel(new GridLayout(2, 2, 20, 16));
        rulesGrid.setOpaque(false);
        rulesGrid.setBorder(new EmptyBorder(14, 0, 0, 0));

        rulesGrid.add(createRuleItem("⏱️ 10 Questions Round", "Each session challenges you with 10 random arithmetic problems."));
        rulesGrid.add(createRuleItem("🎯 3 Difficulty Levels", "Easy (single digits), Medium (double digits), Hard (triple digits & fast pace)."));
        rulesGrid.add(createRuleItem("⚡ Speed & Accuracy Scoring", "Earn base points per question plus speed bonus points for quick answers."));
        rulesGrid.add(createRuleItem("🏆 Persistent MySQL Leaderboard", "All scores are saved to MySQL. Compete for the Global Top 10 rank!"));

        instructionsCard.add(instructionsHeader, BorderLayout.NORTH);
        instructionsCard.add(rulesGrid, BorderLayout.CENTER);

        centerContainer.add(instructionsCard);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createRuleItem(String title, String description) {
        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BOLD_14);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel descLbl = new JLabel("<html><body style='width: 250px; color: #94A3B8;'>" + description + "</body></html>");
        descLbl.setFont(UITheme.FONT_SMALL);

        item.add(titleLbl);
        item.add(Box.createVerticalStrut(4));
        item.add(descLbl);
        return item;
    }
}
