package quoridor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusPanel extends JPanel {
    private final Timer timer;
    private final JLabel timeLabel;
    private final JLabel statusMessageLabel;
    private final JLabel[] playerWallsLabel;
    private final JLabel[] playerNameLabels;
    private final JPanel[] playerPanel;
    private final Player[] players;
    private final String[] playerNames;
    private final JButton themeToggleButton;
    private final JSeparator divider;
    private final JLabel titleLabel;
    private Runnable themeToggleListener;
    private ThemePalette themePalette;
    private Player highlightedPlayer;
    private int elapsedTime = 0;

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font TIME_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font NAME_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font WALL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font STATUS_FONT = new Font("SansSerif", Font.ITALIC, 13);

    public StatusPanel(Player[] players, Player currentPlayer, String[] playerNames, ThemePalette palette) {
        this.players = players;
        this.playerNames = playerNames.clone();
        this.themePalette = palette;

        setOpaque(true);
        setBackground(themePalette.statusPanelBackground());
        setPreferredSize(new Dimension(240, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themePalette.statusPanelBorder(), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        titleLabel = new JLabel("Game Status");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(themePalette.statusTextPrimary());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createRigidArea(new Dimension(0, 12)));

        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(TIME_FONT);
        timeLabel.setForeground(themePalette.statusTextPrimary());
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(timeLabel);

        add(Box.createRigidArea(new Dimension(0, 10)));
        divider = new JSeparator();
        divider.setForeground(themePalette.statusDividerColor());
        divider.setBackground(themePalette.statusDividerColor());
        add(divider);
        add(Box.createRigidArea(new Dimension(0, 14)));

        playerWallsLabel = new JLabel[players.length];
        playerNameLabels = new JLabel[players.length];
        playerPanel = new JPanel[players.length];

        for (int i = 0; i < players.length; i++) {
            JPanel panel = buildPlayerPanel(i);
            playerPanel[i] = panel;
            panel.setBorder(createPlayerBorder(players[i] == currentPlayer));
            add(panel);
            add(Box.createRigidArea(new Dimension(0, 12)));
        }

        add(Box.createVerticalGlue());

        statusMessageLabel = new JLabel("Ready to play.");
        statusMessageLabel.setFont(STATUS_FONT);
        statusMessageLabel.setForeground(themePalette.statusMessageColor());
        statusMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(statusMessageLabel);

        themeToggleButton = new JButton();
        themeToggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeToggleButton.addActionListener(e -> {
            if (themeToggleListener != null) {
                themeToggleListener.run();
            }
        });
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(themeToggleButton);

        timer = new Timer(1000, e -> {
            elapsedTime++;
            updateStatusPanel();
        });
        timer.start();

        updatePlayerPanel(currentPlayer);
        updateStatusPanel();
        refreshThemeDependentComponents();
    }

    private JPanel buildPlayerPanel(int index) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setOpaque(true);
        panel.setBackground(themePalette.statusSectionBackground());
        panel.setMaximumSize(new Dimension(260, 76));

        PlayerColorIndicator indicator = new PlayerColorIndicator(PlayerColorStore.getPlayerColor(index), 28);
        panel.add(indicator);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(playerNames[index]);
        nameLabel.setFont(NAME_FONT);
        nameLabel.setForeground(themePalette.statusTextPrimary());
        playerNameLabels[index] = nameLabel;
        textPanel.add(nameLabel);

        playerWallsLabel[index] = new JLabel("Walls: " + players[index].wall);
        playerWallsLabel[index].setFont(WALL_FONT);
        playerWallsLabel[index].setForeground(themePalette.statusTextSecondary());
        textPanel.add(playerWallsLabel[index]);

        panel.add(textPanel);
        panel.setBorder(createPlayerBorder(false));
        return panel;
    }

    private javax.swing.border.Border createPlayerBorder(boolean active) {
        Color borderColor = active ? themePalette.statusCurrentTurnBorder() : themePalette.statusPanelBorder();
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(8, 12, 8, 12)
        );
    }

    public Timer getTimer() {
        return timer;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = Math.max(0, elapsedTime);
        updateStatusPanel();
    }

    public void updateStatusPanel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        updateWallsLabel();
    }

    public void updateWallsLabel() {
        for (int i = 0; i < players.length; i++) {
            playerWallsLabel[i].setText("Walls: " + players[i].wall);
        }
    }

    public void updatePlayerPanel(Player currentPlayer) {
        highlightedPlayer = currentPlayer;
        for (int i = 0; i < players.length; i++) {
            boolean isCurrent = players[i] == currentPlayer;
            playerPanel[i].setBackground(isCurrent ? themePalette.statusCurrentTurnBackground() : themePalette.statusSectionBackground());
            playerPanel[i].setBorder(createPlayerBorder(isCurrent));
            playerNameLabels[i].setFont(isCurrent ? NAME_FONT.deriveFont(Font.BOLD) : NAME_FONT);
        }
    }

    public void setStatusMessage(String message) {
        statusMessageLabel.setText(message);
    }

    public void setThemeToggleListener(Runnable listener) {
        this.themeToggleListener = listener;
    }

    public void applyTheme(ThemePalette palette) {
        this.themePalette = palette;
        setBackground(palette.statusPanelBackground());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.statusPanelBorder(), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));
        refreshThemeDependentComponents();
    }

    private void refreshThemeDependentComponents() {
        titleLabel.setForeground(themePalette.statusTextPrimary());
        timeLabel.setForeground(themePalette.statusTextPrimary());
        statusMessageLabel.setForeground(themePalette.statusMessageColor());
        divider.setForeground(themePalette.statusDividerColor());
        divider.setBackground(themePalette.statusDividerColor());
        for (int i = 0; i < players.length; i++) {
            playerNameLabels[i].setForeground(themePalette.statusTextPrimary());
            playerWallsLabel[i].setForeground(themePalette.statusTextSecondary());
            playerPanel[i].setBackground(themePalette.statusSectionBackground());
        }
        themeToggleButton.setBackground(themePalette.themeToggleBackground());
        themeToggleButton.setForeground(themePalette.themeToggleForeground());
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setOpaque(true);
        themeToggleButton.setBorder(BorderFactory.createLineBorder(themePalette.statusPanelBorder()));
        themeToggleButton.setText(themePalette.isDark() ? "Switch to Light Theme" : "Switch to Dark Theme");

        if (highlightedPlayer != null) {
            updatePlayerPanel(highlightedPlayer);
        }
    }

    public String getPlayerName(int index) {
        if (index >= 0 && index < playerNames.length) {
            return playerNames[index];
        }
        return "";
    }

    public String getPlayerName(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return getPlayerName(i);
            }
        }
        return "";
    }

    private static class PlayerColorIndicator extends JComponent {
        private final Color color;
        private final int diameter;

        PlayerColorIndicator(Color color, int diameter) {
            this.color = color;
            this.diameter = diameter;
            setPreferredSize(new Dimension(diameter + 8, diameter + 8));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(3, 4, diameter, diameter);
            g2d.setColor(color);
            g2d.fillOval(2, 2, diameter, diameter);
        }
    }
}
