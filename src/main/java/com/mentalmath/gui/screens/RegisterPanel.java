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
 * Screen allowing a new player to register.
 * Uses SwingWorker to execute SQL insertion and generated key retrieval.
 */
public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private ModernTextField usernameField;
    private JLabel feedbackLabel;
    private RoundedButton registerButton;

    public RegisterPanel(MainFrame mainFrame) {
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
        card.setPreferredSize(new Dimension(460, 440));

        // Title & Subtitle
        JLabel title = new JLabel("Register Player");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose a unique name (2-50 characters)");
        subtitle.setFont(UITheme.FONT_REGULAR_14);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feedback Banner
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(UITheme.FONT_BOLD_14);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Field Label
        JLabel inputLabel = new JLabel("Choose Your Player Name");
        inputLabel.setFont(UITheme.FONT_BOLD_14);
        inputLabel.setForeground(UITheme.TEXT_PRIMARY);
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text Field
        usernameField = new ModernTextField("e.g. BrainMaster, Surya99", 20);
        usernameField.setMaximumSize(new Dimension(360, 44));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.addActionListener(e -> executeRegistration());

        // Buttons
        registerButton = new RoundedButton("Create Account", RoundedButton.Style.SUCCESS);
        registerButton.setMaximumSize(new Dimension(360, 46));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> executeRegistration());

        RoundedButton toLoginBtn = new RoundedButton("Already registered? Login here", RoundedButton.Style.OUTLINE);
        toLoginBtn.setFont(UITheme.FONT_SMALL);
        toLoginBtn.setMaximumSize(new Dimension(360, 36));
        toLoginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toLoginBtn.addActionListener(e -> mainFrame.showLogin());

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
        card.add(registerButton);
        card.add(Box.createVerticalStrut(12));
        card.add(toLoginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(backBtn);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void executeRegistration() {
        String name = usernameField.getText().trim();
        String sanitized = PlayerManager.sanitizeName(name);

        if (sanitized == null) {
            showError("Invalid name. Must be 2-50 characters (letters, numbers, underscores).");
            return;
        }

        registerButton.setLoading(true);
        feedbackLabel.setForeground(UITheme.ACCENT_CYAN);
        feedbackLabel.setText("Registering in MySQL database...");

        new SwingWorker<Player, Void>() {
            @Override
            protected Player doInBackground() {
                return mainFrame.getPlayerManager().registerPlayer(sanitized);
            }

            @Override
            protected void done() {
                registerButton.setLoading(false);
                try {
                    Player player = get();
                    if (player != null) {
                        feedbackLabel.setForeground(UITheme.ACCENT_EMERALD);
                        feedbackLabel.setText("Registration successful! Welcome, " + player.getPlayerName());
                        mainFrame.setCurrentPlayer(player);
                        mainFrame.showDashboard();
                        usernameField.setText("");
                        feedbackLabel.setText(" ");
                    } else {
                        String errMsg = mainFrame.getPlayerManager().getLastErrorMessage();
                        showError(errMsg != null ? errMsg : "Registration failed. Username may already exist.");
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
