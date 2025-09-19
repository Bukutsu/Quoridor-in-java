package quoridor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuoridorMain extends JFrame {
    private QuoridorPanel gamePanel;
    private StatusPanel statusPanel;
    private static GameSetupPanel setupPanel;
    private static final Color BACKGROUND_COLOR = Color.decode("#faf6ed");

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
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        gamePanel = new QuoridorPanel(mode);
        statusPanel = new StatusPanel(gamePanel.getPlayers(), gamePanel.getCurrentPlayer(), setupPanel.getPlayerNames());
        gamePanel.setStatusPanel(statusPanel);

        JPanel contentPanel = new JPanel(new BorderLayout(18, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPanel.add(gamePanel, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }


    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     QuoridorMain game = new QuoridorMain();
        //     game.setVisible(true);
        // });

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            showGameSetupDialog();
        });

    }
}
