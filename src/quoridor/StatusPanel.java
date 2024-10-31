package quoridor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusPanel extends JPanel {
    private Timer timer;
    private JLabel timeLabel;
    private JLabel[] playerWallsLabel;
    private Player[] players;
    private JPanel[] playerPanel;
    private int elapsedTime = 0;

    private static final Color CURRENT_TURN_BACKGROUND_COLOR = Color.decode("#baede5");
    private static final Color CURRENT_TURN_BORDER_COLOR = Color.decode("#84b5ad");
    private static final Color BACKGROUND_COLOR = Color.decode("#b4aa99");
    private static final Color BORDER_COLOR = Color.decode("#41686f");

    public Timer getTimer() {
        return timer;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
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
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(currentPlayer)) {
                playerPanel[i].setBackground(CURRENT_TURN_BACKGROUND_COLOR); // Light red background for active player
                playerPanel[i].setBorder(BorderFactory.createLineBorder(CURRENT_TURN_BORDER_COLOR)); // Red border for active player section
            } else {
                playerPanel[i].setBackground(BACKGROUND_COLOR);
                playerPanel[i].setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            }
        }
    }

    public StatusPanel(Player[] players, Player currentPlayer, String[] playerNames) {
        this.players = players;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the entire panel

        // Time Label
        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(timeLabel);

        // Space below time label
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Player Wall Labels and Panels
        playerWallsLabel = new JLabel[players.length];
        playerPanel = new JPanel[players.length];
        for (int i = 0; i < players.length; i++) {
            playerPanel[i] = new JPanel(new FlowLayout(FlowLayout.LEFT)); // A panel for each player with FlowLayout
            playerPanel[i].setMaximumSize(new Dimension(250, 75)); // Fix the size to make it consistent

            if (players[i].equals(currentPlayer)) {
                playerPanel[i].setBackground(CURRENT_TURN_BACKGROUND_COLOR); // Light red background for active player
                playerPanel[i].setBorder(BorderFactory.createLineBorder(CURRENT_TURN_BORDER_COLOR)); // Red border for active player section
            } else {
                playerPanel[i].setBackground(BACKGROUND_COLOR);
                playerPanel[i].setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
            }

            // Player Color Indicator (Circle)
            JPanel playerIconPanel = new JPanel();
            playerIconPanel.setLayout(new BoxLayout(playerIconPanel, BoxLayout.Y_AXIS));
            playerIconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            PlayerColorIndicator colorIndicator = new PlayerColorIndicator(PlayerColorStore.getPlayerColor(i), 30); // Create a custom color indicator with 30px diameter

            JLabel playerNameLabel = new JLabel(playerNames[i]); // Use the passed player name
            playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            playerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            playerIconPanel.add(colorIndicator);
            playerIconPanel.add(playerNameLabel);
            playerIconPanel.setOpaque(false);

            // Player Wall Count Label
            playerWallsLabel[i] = new JLabel("Walls: " + players[i].wall);
            playerWallsLabel[i].setFont(new Font("Arial", Font.PLAIN, 14));

            // Add components to player panel
            playerPanel[i].add(playerIconPanel);
            playerPanel[i].add(playerWallsLabel[i]);

            // Add player panel to the main panel
            add(playerPanel[i]);

            // Space between player panels
            add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Timer Setup
        timer = new Timer(1000, e -> {
            elapsedTime++;
            updateStatusPanel();
        });
        timer.start();
    }

    // Custom component to draw a circle with a specified color
    private static class PlayerColorIndicator extends JComponent {
        private final Color color;
        private final int diameter;

        public PlayerColorIndicator(Color color, int diameter) {
            this.color = color;
            this.diameter = diameter;
            setPreferredSize(new Dimension(diameter + 10, diameter + 10)); // Add some padding around the circle
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(5, 5, diameter, diameter); // Draw the circle with a little padding
        }
    }
}
