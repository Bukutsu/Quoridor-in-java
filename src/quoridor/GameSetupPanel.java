package quoridor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameSetupPanel extends JPanel {

    
    private JComboBox<String> modeSelector;
    private JComboBox<String> playerColorSelector;
    private JButton startGameButton;

    public GameSetupPanel() {
        setLayout(new BorderLayout(10, 10));

        // Main panel for options (mode and player color)
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // Add label and dropdown for selecting game mode
        optionsPanel.add(new JLabel("Select Game Mode:"));
        String[] gameModes = {"Two Player", "Four Player"};
        modeSelector = new JComboBox<>(gameModes);
        optionsPanel.add(modeSelector);

        // Add label and dropdown for selecting player color
        // optionsPanel.add(new JLabel("Select Player Color:"));
        // String[] playerColors = {"Red", "Blue", "Green", "Yellow"};
        // playerColorSelector = new JComboBox<>(playerColors);
        // optionsPanel.add(playerColorSelector);

        // Add the options panel to the top of the main panel
        add(optionsPanel, BorderLayout.NORTH);

        // Add button to start the game at the center bottom
        startGameButton = new JButton("Start Game");
        JPanel buttonPanel = new JPanel(); // A panel to center the button
        buttonPanel.add(startGameButton);
        add(buttonPanel, BorderLayout.CENTER);
    }

    // Method to set the action listener for the start game button
    public void setStartGameActionListener(ActionListener actionListener) {
        startGameButton.addActionListener(actionListener);
    }

    // Get the selected mode
    public String getSelectedMode() {
        return (String) modeSelector.getSelectedItem();
    }

    // Get the selected color
    public String getSelectedColor() {
        return (String) playerColorSelector.getSelectedItem();
    }
}
