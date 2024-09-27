package quoridor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QuoridorBoardPanel extends JPanel{
   public static final int ROWS = 17;
   public static final int COLS = 17;

   public static final int CELL_SIZE = 50;  // Cell width and height, in pixels
   public static final int CANVAS_WIDTH  = CELL_SIZE * COLS; // Game board width/height
   public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;

   Cell cells[][] = new Cell[ROWS][COLS];
   
    public QuoridorBoardPanel() {
        super.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0); // No spacing between cells
        
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                // Create wall and player cells
                cells[row][col] = new Cell(row, col);
                
                // Check if the cell should be a player space or a wall space
                if (row % 2 == 0 && col % 2 == 0) {
                    // Player cell: Set a larger size
                    cells[row][col].setPreferredSize(new Dimension(50, 50));
                    cells[row][col].setBackground(Color.GREEN);
                } else if (row % 2 == 0 && col % 2 != 0) {
                    // Horizontal wall space: Thinner width, and continuous wall effect
                    cells[row][col].setPreferredSize(new Dimension(10, 50));
                    cells[row][col].setBackground(new Color(139, 69, 19)); // Brown-like color for wood
                    cells[row][col].setBorder(new EmptyBorder(0, 0, 0, 0)); // No borders
                } else if (row % 2 != 0 && col % 2 == 0) {
                    // Vertical wall space: Thinner height, and continuous wall effect
                    cells[row][col].setPreferredSize(new Dimension(50, 10));
                    cells[row][col].setBackground(new Color(139, 69, 19)); // Brown-like color for wood
                    cells[row][col].setBorder(new EmptyBorder(0, 0, 0, 0)); // No borders
                } else {
                    // Wall intersection point (between two walls)
                    cells[row][col].setPreferredSize(new Dimension(10, 10));
                    cells[row][col].setBackground(new Color(139, 69, 19)); // Darker brown for intersection
                    cells[row][col].setBorder(new EmptyBorder(0, 0, 0, 0)); // No borders
                }

                gbc.gridx = col;
                gbc.gridy = row;
                add(cells[row][col], gbc);
            }
        }
    }

}
