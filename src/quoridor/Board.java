package quoridor;

public class Board {
    private static final int BOARD_SIZE = 9;
    private int[][] board;
    private boolean[][] horizontalWalls;
    private boolean[][] verticalWalls;
    
    public Board() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        horizontalWalls = new boolean[BOARD_SIZE-1][BOARD_SIZE-1];
        verticalWalls = new boolean[BOARD_SIZE-1][BOARD_SIZE-1];
    }
    
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Check if move is within bounds
        if (toX < 0 || toX >= BOARD_SIZE || toY < 0 || toY >= BOARD_SIZE) {
            return false;
        }
        
        // Check if wall blocks the path
        if (Math.abs(fromX - toX) == 1 && fromY == toY) {
            return !verticalWalls[Math.min(fromX, toX)][fromY];
        }
        if (Math.abs(fromY - toY) == 1 && fromX == toX) {
            return !horizontalWalls[fromX][Math.min(fromY, toY)];
        }
        
        return false;
    }
    
    public boolean placeHorizontalWall(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE-1 || y < 0 || y >= BOARD_SIZE-1) {
            return false;
        }
        if (horizontalWalls[x][y]) {
            return false;
        }
        horizontalWalls[x][y] = true;
        return true;
    }
    
    public boolean placeVerticalWall(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE-1 || y < 0 || y >= BOARD_SIZE-1) {
            return false;
        }
        if (verticalWalls[x][y]) {
            return false;
        }
        verticalWalls[x][y] = true;
        return true;
    }
    
    public int getBoardSize() {
        return BOARD_SIZE;
    }
    
    public boolean hasHorizontalWall(int x, int y) {
        return horizontalWalls[x][y];
    }
    
    public boolean hasVerticalWall(int x, int y) {
        return verticalWalls[x][y];
    }
}