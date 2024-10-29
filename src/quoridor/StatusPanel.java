package quoridor;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    // TODO: make it nicer GUI 
    private Timer timer;
    private JLabel timeLabel;
    private JLabel[] playerWallsLabel;
    private int elapsedTime = 0;
    private Player[] players;

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
            playerWallsLabel[i].setText("Player " + (i + 1) + " Walls: " + players[i].wall);
        }
    }

    public StatusPanel(Player[] players) {
        this.players = players;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Use BoxLayout to stack components vertically

        // Time Label
        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for BoxLayout
        add(timeLabel);

        // Player Wall Labels
        playerWallsLabel = new JLabel[players.length];
        for (int i = 0; i < players.length; i++) {
            playerWallsLabel[i] = new JLabel("Player " + (i + 1) + " Walls: " + players[i].wall);
            playerWallsLabel[i].setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for BoxLayout
            add(playerWallsLabel[i]);
        }

        // Timer Setup
        timer = new Timer(1000, e -> {
            elapsedTime++;
            updateStatusPanel();
        });
        timer.start();
    }
}
