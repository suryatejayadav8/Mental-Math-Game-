package com.mentalmath.gui.screens;

import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;
import com.mentalmath.model.Player;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Main dashboard displayed to an authenticated player.
 * Allows launching games with chosen difficulty, reviewing score history,
 * viewing global leaderboard, and logging out.
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel welcomeLabel;
    private JLabel memberSinceLabel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 45, 30, 45));

        initUI();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel();
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // 1. Header Banner Card
        RoundedPanel headerCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(22, 28, 22, 28));
        headerCard.setMaximumSize(new Dimension(860, 100));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        welcomeLabel = new JLabel("Welcome, Player!");
        welcomeLabel.setFont(UITheme.FONT_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_PRIMARY);

        memberSinceLabel = new JLabel("Ready for today's mental calculation challenge?");
        memberSinceLabel.setFont(UITheme.FONT_REGULAR_14);
        memberSinceLabel.setForeground(UITheme.ACCENT_CYAN);

        headerText.add(welcomeLabel);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(memberSinceLabel);

        RoundedButton logoutBtn = new RoundedButton("🚪 Logout", RoundedButton.Style.SECONDARY);
        logoutBtn.setPreferredSize(new Dimension(110, 38));
        logoutBtn.addActionListener(e -> mainFrame.logout());

        headerCard.add(headerText, BorderLayout.WEST);
        headerCard.add(logoutBtn, BorderLayout.EAST);

        mainContainer.add(headerCard);
        mainContainer.add(Box.createVerticalStrut(25));

        // 2. Difficulty Selection & Play Card
        RoundedPanel playCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        playCard.setLayout(new BoxLayout(playCard, BoxLayout.Y_AXIS));
        playCard.setBorder(new EmptyBorder(22, 28, 22, 28));
        playCard.setMaximumSize(new Dimension(860, 230));

        JLabel playTitle = new JLabel("⚡ Choose Difficulty & Play (10 Questions)");
        playTitle.setFont(UITheme.FONT_SECTION_HEADER);
        playTitle.setForeground(UITheme.PRIMARY_HOVER);
        playCard.add(playTitle);
        playCard.add(Box.createVerticalStrut(6));

        JLabel playSubtitle = new JLabel("Select your level to start the real-time timed challenge:");
        playSubtitle.setFont(UITheme.FONT_REGULAR_14);
        playSubtitle.setForeground(UITheme.TEXT_SECONDARY);
        playCard.add(playSubtitle);
        playCard.add(Box.createVerticalStrut(18));

        // 3 Difficulty Options Grid
        JPanel diffGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        diffGrid.setOpaque(false);
        diffGrid.setMaximumSize(new Dimension(820, 110));

        diffGrid.add(createDifficultyBox("🌱 Easy", "Single digits (1-20)", "+, -, *, / with whole numbers",
                RoundedButton.Style.SUCCESS, QuestionGenerator.Difficulty.EASY));

        diffGrid.add(createDifficultyBox("⚡ Medium", "Double digits (10-100)", "Faster multipliers & clean division",
                RoundedButton.Style.PRIMARY, QuestionGenerator.Difficulty.MEDIUM));

        diffGrid.add(createDifficultyBox("🔥 Hard", "Triple digits (50-500)", "Advanced multipliers & speed challenge",
                RoundedButton.Style.DANGER, QuestionGenerator.Difficulty.HARD));

        playCard.add(diffGrid);
        mainContainer.add(playCard);
        mainContainer.add(Box.createVerticalStrut(25));

        // 3. Navigation Cards (History & Leaderboard)
        JPanel bottomGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomGrid.setOpaque(false);
        bottomGrid.setMaximumSize(new Dimension(860, 140));

        // History Card
        RoundedPanel historyCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        historyCard.setLayout(new BorderLayout());
        historyCard.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel histText = new JPanel();
        histText.setOpaque(false);
        histText.setLayout(new BoxLayout(histText, BoxLayout.Y_AXIS));

        JLabel histTitle = new JLabel("📊 My Score History");
        histTitle.setFont(UITheme.FONT_BOLD_16);
        histTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel histDesc = new JLabel("View your previous game scores and accuracy stats.");
        histDesc.setFont(UITheme.FONT_SMALL);
        histDesc.setForeground(UITheme.TEXT_SECONDARY);

        histText.add(histTitle);
        histText.add(Box.createVerticalStrut(4));
        histText.add(histDesc);

        RoundedButton viewHistoryBtn = new RoundedButton("View History", RoundedButton.Style.SECONDARY);
        viewHistoryBtn.setPreferredSize(new Dimension(130, 38));
        viewHistoryBtn.addActionListener(e -> mainFrame.showScoreHistory());

        historyCard.add(histText, BorderLayout.WEST);
        historyCard.add(viewHistoryBtn, BorderLayout.EAST);

        // Leaderboard Card
        RoundedPanel lbCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        lbCard.setLayout(new BorderLayout());
        lbCard.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel lbText = new JPanel();
        lbText.setOpaque(false);
        lbText.setLayout(new BoxLayout(lbText, BoxLayout.Y_AXIS));

        JLabel lbTitle = new JLabel("🏆 Global Leaderboard");
        lbTitle.setFont(UITheme.FONT_BOLD_16);
        lbTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lbDesc = new JLabel("See the Top 10 high scores across all registered players.");
        lbDesc.setFont(UITheme.FONT_SMALL);
        lbDesc.setForeground(UITheme.TEXT_SECONDARY);

        lbText.add(lbTitle);
        lbText.add(Box.createVerticalStrut(4));
        lbText.add(lbDesc);

        RoundedButton viewLbBtn = new RoundedButton("Leaderboard", RoundedButton.Style.OUTLINE);
        viewLbBtn.setPreferredSize(new Dimension(130, 38));
        viewLbBtn.addActionListener(e -> mainFrame.showLeaderboard());

        lbCard.add(lbText, BorderLayout.WEST);
        lbCard.add(viewLbBtn, BorderLayout.EAST);

        bottomGrid.add(historyCard);
        bottomGrid.add(lbCard);

        mainContainer.add(bottomGrid);
        mainContainer.add(Box.createVerticalGlue());

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createDifficultyBox(String name, String desc1, String desc2,
                                       RoundedButton.Style btnStyle, QuestionGenerator.Difficulty diff) {
        RoundedPanel box = new RoundedPanel(14, UITheme.CARD_BG_SOLID, UITheme.CARD_BORDER, 1);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel(name);
        title.setFont(UITheme.FONT_BOLD_14);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub1 = new JLabel(desc1);
        sub1.setFont(UITheme.FONT_SMALL);
        sub1.setForeground(UITheme.TEXT_MUTED);
        sub1.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton startBtn = new RoundedButton("Start " + diff.name(), btnStyle);
        startBtn.setFont(UITheme.FONT_BOLD_14);
        startBtn.setMaximumSize(new Dimension(160, 36));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> mainFrame.showGame(diff));

        box.add(title);
        box.add(Box.createVerticalStrut(2));
        box.add(sub1);
        box.add(Box.createVerticalStrut(10));
        box.add(startBtn);

        return box;
    }

    public void updatePlayerInfo(Player player) {
        if (player != null) {
            welcomeLabel.setText("Welcome back, " + player.getPlayerName() + "!");
            String dateStr = player.getCreatedAt() != null ? player.getCreatedAt().toString().substring(0, 10) : "Today";
            memberSinceLabel.setText("Player ID #" + player.getPlayerId() + " • Member since " + dateStr);
        }
    }
}
