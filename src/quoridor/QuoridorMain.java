package quoridor;
import java.awt.*;       
import javax.swing.*;

public class QuoridorMain extends JFrame{
    QuoridorBoardPanel board = new QuoridorBoardPanel();

    public QuoridorMain() {
        Container cp = this.getContentPane();           // JFrame's content-pane
        cp.setLayout(new BorderLayout()); // in 10x10 GridLayout
  
        cp.add(board, BorderLayout.CENTER);
  
        pack();  // Pack the UI components, instead of setSize()
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // handle window-close button
        setTitle("Quoridor");
        setVisible(true);   // show it
     }

     public static void main(String[] args) {
        new QuoridorMain();
     }
}
