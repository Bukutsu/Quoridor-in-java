package quoridor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;

public class GameSetupPanel extends JPanel {

    private JRadioButton twoPlayerMode;
    private JRadioButton fourPlayerMode;
    private ButtonGroup modeGroup;
    private JButton startGameButton;
    private JPanel playersPanel;
    private ArrayList<PlayerSetupPanel> playerSetupPanels;
    private JDialog parentDialog; // Reference to the parent dialog
    private HashSet<Color> selectedColors = new HashSet<>();
    


    // Constructor that takes the parent dialog
    public GameSetupPanel(JDialog parentDialog) {
        this.parentDialog = parentDialog;
        setLayout(new BorderLayout(10, 10));

        // Mode selection panel with radio buttons
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(new JLabel("Select Game Mode:"));

        twoPlayerMode = new JRadioButton("Two Player");
        fourPlayerMode = new JRadioButton("Four Player");
        modeGroup = new ButtonGroup();
        modeGroup.add(twoPlayerMode);
        modeGroup.add(fourPlayerMode);
        twoPlayerMode.setSelected(true); // Default to two players

        modePanel.add(twoPlayerMode);
        modePanel.add(fourPlayerMode);

        add(modePanel, BorderLayout.NORTH);

        // Players setup panel (will change based on selected mode)
        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        add(playersPanel, BorderLayout.CENTER);

        playerSetupPanels = new ArrayList<>();
        updatePlayerPanels(2); // Default to two players

        // Add button to start the game at the bottom
        startGameButton = new JButton("Start Game");
        JPanel buttonPanel = new JPanel(); // A panel to center the button
        buttonPanel.add(startGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for radio buttons to update player panels
        twoPlayerMode.addActionListener(e -> updatePlayerPanels(2));
        fourPlayerMode.addActionListener(e -> updatePlayerPanels(4));
    }

    // Method to set the action listener for the start game button
    public void setStartGameActionListener(ActionListener actionListener) {
        startGameButton.addActionListener(actionListener);
    }

    // Get the selected mode
    public String getSelectedMode() {
        return twoPlayerMode.isSelected() ? "Two Player" : "Four Player";
    }

    // Update player setup panels based on selected game mode
    private void updatePlayerPanels(int numberOfPlayers) {
        // Clear previous player panels
        playersPanel.removeAll();
        playerSetupPanels.clear();

        // Add new player panels based on the selected mode
        for (int i = 0; i < numberOfPlayers; i++) {
            PlayerSetupPanel panel = new PlayerSetupPanel(i, "Player " + (i + 1));
            playerSetupPanels.add(panel);
            playersPanel.add(panel);
        }

        // Adjust the preferred size of the main panel based on the number of players
        adjustPanelSize(numberOfPlayers);

        // Revalidate and repaint to ensure the changes are visible
        playersPanel.revalidate();
        playersPanel.repaint();

        // Repack the dialog to resize properly
        if (parentDialog != null) {
            parentDialog.pack();
        }
    }

    // Adjust the preferred size of the panel based on the number of players
    private void adjustPanelSize(int numberOfPlayers) {
        int height = 100 + (numberOfPlayers * 75); // Adjust height based on player count
        Dimension newPreferredSize = new Dimension(400, height);
        setPreferredSize(newPreferredSize);
    }

    // Inner class for individual player setup panel
    private class PlayerSetupPanel extends JPanel {
        private JTextField playerNameField;
        private JButton chooseColorButton;
        private PlayerColorIndicator colorIndicator;
        private Color selectedColor;
    
        public PlayerSetupPanel(int playerIndex, String defaultName) {
    
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBorder(BorderFactory.createTitledBorder(defaultName));
    
            // Player name input
            playerNameField = new JTextField(defaultName, 10);
            add(new JLabel("Name:"));
            add(playerNameField);
    
            // Color selection button
            chooseColorButton = new JButton("Choose Color");
            colorIndicator = new PlayerColorIndicator(Color.LIGHT_GRAY, 30); // Initially set to gray indicating no color selected
    
            chooseColorButton.addActionListener(e -> {
                // Open color picker dialog
                Color color = JColorChooser.showDialog(this, "Select a Color", Color.WHITE);
                if (color != null) {
                    // If the color is already taken, show a message and do nothing
                    if (selectedColors.contains(color)) {
                        JOptionPane.showMessageDialog(this, "Color already selected by another player. Choose a different color.");
                        return;
                    }
    
                    // If a color was previously selected, remove it from the set
                    if (selectedColor != null) {
                        selectedColors.remove(selectedColor);
                    }
    
                    // Update selected color
                    selectedColor = color;
                    selectedColors.add(selectedColor);
                    PlayerColorStore.setPlayerColor(playerIndex, selectedColor);
    
                    // Update the color indicator
                    colorIndicator.setColor(selectedColor);
                    colorIndicator.repaint();
                }
            });
    
            add(chooseColorButton);
            add(colorIndicator);
        }
    
        public String getPlayerName() {
            return playerNameField.getText();
        }
    }
    
    // Getter method to return all player names
    public String[] getPlayerNames() {
        String[] playerNames = new String[playerSetupPanels.size()];
        for (int i = 0; i < playerSetupPanels.size(); i++) {
            playerNames[i] = playerSetupPanels.get(i).getPlayerName();
        }
        return playerNames;
    }

    // Custom component to draw a circle with a specified color
    private static class PlayerColorIndicator extends JComponent {
        private Color color;
        private final int diameter;

        public PlayerColorIndicator(Color color, int diameter) {
            this.color = color;
            this.diameter = diameter;
            setPreferredSize(new Dimension(diameter + 10, diameter + 10)); // Add some padding around the circle
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(5, 5, diameter, diameter); // Draw the circle with a little padding
        }
    }
}
