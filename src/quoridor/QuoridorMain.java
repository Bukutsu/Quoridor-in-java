package quoridor;
import javax.swing.*;
import java.awt.*;

public class QuoridorMain extends JFrame {
    private QuoridorPanel gamePanel;
    private StatusPanel statusPanel;

    public QuoridorMain() {
        setTitle("Quoridor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        gamePanel = new QuoridorPanel();
        statusPanel = new StatusPanel(gamePanel.getPlayers());
        gamePanel.setStatusPanel(statusPanel);
        add(gamePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);
        pack();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuoridorMain game = new QuoridorMain();
            game.setVisible(true);
            
        });
    }
}