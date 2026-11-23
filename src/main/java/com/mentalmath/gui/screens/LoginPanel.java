package com.mentalmath.gui.screens;

import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.ModernTextField;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;
import com.mentalmath.manager.PlayerManager;
import com.mentalmath.model.Player;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Screen allowing an existing player to log in by username.
 * Executes database lookup on a SwingWorker background thread to avoid UI lag.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private ModernTextField usernameField;
    private JLabel feedbackLabel;
    private RoundedButton loginButton;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(40, 40, 40, 40));

        initUI();
    }

    private void initUI() {
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(20, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 45, 35, 45));
        card.setPreferredSize(new Dimension(460, 420));

        // Title & Subtitle
        JLabel title = new JLabel("Player Login");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your registered player name to continue");
        subtitle.setFont(UITheme.FONT_REGULAR_14);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feedback Banner
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(UITheme.FONT_BOLD_14);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Field Label
        JLabel inputLabel = new JLabel("Player Name");
        inputLabel.setFont(UITheme.FONT_BOLD_14);
        inputLabel.setForeground(UITheme.TEXT_PRIMARY);
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text Field
        usernameField = new ModernTextField("e.g. MathWizard, Surya99", 20);
        usernameField.setMaximumSize(new Dimension(360, 44));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.addActionListener(e -> executeLogin());

        // Buttons
        loginButton = new RoundedButton("Login to Game", RoundedButton.Style.PRIMARY);
        loginButton.setMaximumSize(new Dimension(360, 46));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> executeLogin());

        RoundedButton toRegisterBtn = new RoundedButton("Don't have an account? Register here", RoundedButton.Style.OUTLINE);
        toRegisterBtn.setFont(UITheme.FONT_SMALL);
        toRegisterBtn.setMaximumSize(new Dimension(360, 36));
        toRegisterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toRegisterBtn.addActionListener(e -> mainFrame.showRegister());

        RoundedButton backBtn = new RoundedButton("← Back to Home", RoundedButton.Style.SECONDARY);
        backBtn.setFont(UITheme.FONT_SMALL);
        backBtn.setMaximumSize(new Dimension(160, 34));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> mainFrame.showWelcome());

        // Assembly
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(15));
        card.add(feedbackLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(inputLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(12));
        card.add(toRegisterBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(backBtn);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void executeLogin() {
        String name = usernameField.getText().trim();
        String sanitized = PlayerManager.sanitizeName(name);

        if (sanitized == null) {
            showError("Please enter a valid player name (2-50 characters).");
            return;
        }

        // Run database operation asynchronously via SwingWorker
        loginButton.setLoading(true);
        feedbackLabel.setForeground(UITheme.ACCENT_CYAN);
        feedbackLabel.setText("Connecting to database...");

        new SwingWorker<Player, Void>() {
            @Override
            protected Player doInBackground() {
                return mainFrame.getPlayerManager().loginPlayer(sanitized);
            }

            @Override
            protected void done() {
                loginButton.setLoading(false);
                try {
                    Player player = get();
                    if (player != null) {
                        feedbackLabel.setForeground(UITheme.ACCENT_EMERALD);
                        feedbackLabel.setText("Login successful! Loading dashboard...");
                        mainFrame.setCurrentPlayer(player);
                        mainFrame.showDashboard();
                        usernameField.setText("");
                        feedbackLabel.setText(" ");
                    } else {
                        String errMsg = mainFrame.getPlayerManager().getLastErrorMessage();
                        showError(errMsg != null ? errMsg : "Player not found. Please register first!");
                    }
                } catch (Exception ex) {
                    showError("Database connection failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String msg) {
        feedbackLabel.setForeground(UITheme.DANGER);
        feedbackLabel.setText(msg);
    }

    public void resetForm() {
        usernameField.setText("");
        feedbackLabel.setText(" ");
    }
}
