package quoridor;
import javax.swing.*;
import java.awt.*;

public class QuoridorMain extends JFrame {
    private QuoridorPanel gamePanel;
    private StatusPanel statusPanel;
    private static GameSetupPanel setupPanel;


    private static void showGameSetupDialog() {
        JDialog setupDialog = new JDialog();
        setupDialog.setTitle("Quoridor Game Setup");
        setupDialog.setModal(true); // Makes it a modal dialog that blocks other windows until closed
        setupDialog.setLocationRelativeTo(null); // Centers the dialog
    
        setupPanel = new GameSetupPanel(setupDialog);
        setupDialog.add(setupPanel);
    
        // Action Listener for start button to close the setup dialog and start the main game
        setupPanel.setStartGameActionListener(e -> {
            setupDialog.dispose(); // Close the setup dialog
            QuoridorMain game = new QuoridorMain(setupPanel.getSelectedMode());
            game.setVisible(true);
        });
    
        setupDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0); // Terminate the entire program if the setup dialog is closed
            }
        });
    
        setupDialog.pack(); // Adjust the size of the dialog to fit its contents
        setupDialog.setVisible(true); // Display the dialog
    }

    public QuoridorMain(String mode) {
        setTitle("Quoridor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        gamePanel = new QuoridorPanel(mode);
        statusPanel = new StatusPanel(gamePanel.getPlayers(),gamePanel.getCurrentPlayer(),setupPanel.getPlayerNames());
        gamePanel.setStatusPanel(statusPanel);
        gamePanel.setOpaque(false);
        statusPanel.setOpaque(false);
        getContentPane().setBackground(Color.decode("#f4e1c1"));
        add(gamePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);
        pack();    
    }


    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     QuoridorMain game = new QuoridorMain();
        //     game.setVisible(true);
        // });

        SwingUtilities.invokeLater(() -> {
            // Show the game setup dialog first
            showGameSetupDialog();
        });

    }
}