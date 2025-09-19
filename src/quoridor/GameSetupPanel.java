package quoridor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class GameSetupPanel extends JPanel {

    private final JRadioButton twoPlayerMode;
    private final JRadioButton fourPlayerMode;
    private final ButtonGroup modeGroup;
    private final JButton startGameButton;
    private final JButton themeToggleButton;
    private final JPanel playersPanel;
    private final ArrayList<PlayerSetupPanel> playerSetupPanels;
    private final JDialog parentDialog; // Reference to the parent dialog
    private final HashSet<Color> selectedColors = new HashSet<>();
    private final JLabel modeLabel;
    private final JPanel modePanel;
    private final JPanel buttonPanel;
    private ThemePalette themePalette;
    private final Consumer<ThemePalette> themeChangeListener;

    public GameSetupPanel(JDialog parentDialog, ThemePalette palette, Consumer<ThemePalette> themeChangeListener) {
        this.parentDialog = parentDialog;
        this.themePalette = palette;
        this.themeChangeListener = themeChangeListener;

        setLayout(new BorderLayout(12, 12));

        // Mode selection panel with radio buttons
        modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modeLabel = new JLabel("Select Game Mode:");

        twoPlayerMode = new JRadioButton("Two Player");
        fourPlayerMode = new JRadioButton("Four Player");
        modeGroup = new ButtonGroup();
        modeGroup.add(twoPlayerMode);
        modeGroup.add(fourPlayerMode);
        twoPlayerMode.setSelected(true); // Default to two players

        modePanel.add(modeLabel);
        modePanel.add(twoPlayerMode);
        modePanel.add(fourPlayerMode);

        add(modePanel, BorderLayout.NORTH);

        // Players setup panel (will change based on selected mode)
        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        add(playersPanel, BorderLayout.CENTER);

        playerSetupPanels = new ArrayList<>();

        // Theme toggle and start button
        startGameButton = new JButton("Start Game");
        themeToggleButton = new JButton();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.add(themeToggleButton);
        buttonPanel.add(startGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updatePlayerPanels(2); // Default to two players

        // Add action listeners for radio buttons to update player panels
        twoPlayerMode.addActionListener(e -> updatePlayerPanels(2));
        fourPlayerMode.addActionListener(e -> updatePlayerPanels(4));
        themeToggleButton.addActionListener(e -> toggleTheme());

        applyTheme(themePalette);
    }

    private void toggleTheme() {
        ThemePalette next = themePalette.isDark() ? ThemePalette.light() : ThemePalette.dark();
        applyTheme(next);
        if (themeChangeListener != null) {
            themeChangeListener.accept(next);
        }
    }

    private void applyTheme(ThemePalette palette) {
        this.themePalette = palette;

        setBackground(palette.frameBackground());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.statusPanelBorder(), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        modePanel.setOpaque(true);
        modePanel.setBackground(palette.statusPanelBackground());
        modeLabel.setForeground(palette.statusTextPrimary());

        configureRadioButton(twoPlayerMode, palette);
        configureRadioButton(fourPlayerMode, palette);

        playersPanel.setOpaque(false);

        buttonPanel.setOpaque(false);

        configurePrimaryButton(startGameButton, palette.statusSectionBackground(), palette.statusTextPrimary(), palette.statusPanelBorder());
        configurePrimaryButton(themeToggleButton, palette.themeToggleBackground(), palette.themeToggleForeground(), palette.statusPanelBorder());
        themeToggleButton.setText(palette.isDark() ? "Switch to Light Theme" : "Switch to Dark Theme");

        for (PlayerSetupPanel panel : playerSetupPanels) {
            panel.applyTheme(palette);
        }

        revalidate();
        repaint();
    }

    private void configureRadioButton(JRadioButton button, ThemePalette palette) {
        button.setOpaque(false);
        button.setForeground(palette.statusTextPrimary());
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
    }

    private void configurePrimaryButton(JButton button, Color background, Color foreground, Color borderColor) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(borderColor));
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
        playersPanel.removeAll();
        playerSetupPanels.clear();
        selectedColors.clear();

        for (int i = 0; i < numberOfPlayers; i++) {
            PlayerSetupPanel panel = new PlayerSetupPanel(i, "Player " + (i + 1));
            playerSetupPanels.add(panel);
            playersPanel.add(panel);
            playersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        adjustPanelSize(numberOfPlayers);

        playersPanel.revalidate();
        playersPanel.repaint();

        if (parentDialog != null) {
            parentDialog.pack();
        }

        applyTheme(themePalette);
    }

    // Adjust the preferred size of the panel based on the number of players
    private void adjustPanelSize(int numberOfPlayers) {
        int height = 100 + (numberOfPlayers * 85); // Adjust height based on player count
        Dimension newPreferredSize = new Dimension(420, height);
        setPreferredSize(newPreferredSize);
    }

    // Inner class for individual player setup panel
    private class PlayerSetupPanel extends JPanel {
        private final JTextField playerNameField;
        private final JButton chooseColorButton;
        private final PlayerColorIndicator colorIndicator;
        private final int playerIndex;
        private final String title;
        private Color selectedColor;

        PlayerSetupPanel(int playerIndex, String defaultName) {
            this.playerIndex = playerIndex;
            this.title = defaultName;

            setLayout(new FlowLayout(FlowLayout.LEFT));

            playerNameField = new JTextField(defaultName, 10);
            add(new JLabel("Name:"));
            add(playerNameField);

            chooseColorButton = new JButton("Choose Color");
            colorIndicator = new PlayerColorIndicator(PlayerColorStore.getPlayerColor(playerIndex), 30);

            chooseColorButton.addActionListener(e -> {
                Color color = JColorChooser.showDialog(this, "Select a Color", Color.WHITE);
                if (color != null) {
                    if (selectedColors.contains(color)) {
                        JOptionPane.showMessageDialog(this, "Color already selected by another player. Choose a different color.");
                        return;
                    }

                    if (selectedColor != null) {
                        selectedColors.remove(selectedColor);
                    }

                    selectedColor = color;
                    selectedColors.add(selectedColor);
                    PlayerColorStore.setPlayerColor(playerIndex, selectedColor);
                    colorIndicator.setColor(selectedColor);
                    colorIndicator.repaint();
                }
            });

            add(chooseColorButton);
            add(colorIndicator);

            applyTheme(themePalette);
        }

        void applyTheme(ThemePalette palette) {
            setOpaque(true);
            setBackground(palette.statusSectionBackground());

            TitledBorder border = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(palette.statusPanelBorder()),
                    title
            );
            border.setTitleColor(palette.statusTextPrimary());
            setBorder(border);

            Color inputBackground = palette.isDark() ? new Color(0x2d373f) : Color.WHITE;
            Color inputForeground = palette.statusTextPrimary();
            playerNameField.setBackground(inputBackground);
            playerNameField.setForeground(inputForeground);
            playerNameField.setCaretColor(inputForeground);
            playerNameField.setBorder(BorderFactory.createLineBorder(palette.statusPanelBorder()));

            configurePrimaryButton(chooseColorButton, palette.themeToggleBackground(), palette.themeToggleForeground(), palette.statusPanelBorder());
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

    private static class PlayerColorIndicator extends JComponent {
        private Color color;
        private final int diameter;

        PlayerColorIndicator(Color color, int diameter) {
            this.color = color;
            this.diameter = diameter;
            setPreferredSize(new Dimension(diameter + 10, diameter + 10)); // Add some padding around the circle
            setOpaque(false);
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
