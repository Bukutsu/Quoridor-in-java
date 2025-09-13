package quoridor;

public class GameState {
    private Board board;
    private Player[] players;
    private int currentPlayer;
    
    public GameState() {
        board = new Board();
        players = new Player[]{
            new Player(0, 4, 10, 8),  // Player 1 aims for bottom
            new Player(8, 4, 10, 0)   // Player 2 aims for top
        };
        currentPlayer = 0;
    }
    
    public boolean makeMove(int toX, int toY) {
        Player current = players[currentPlayer];
        if (board.isValidMove(current.getX(), current.getY(), toX, toY)) {
            current.moveTo(toX, toY);
            currentPlayer = (currentPlayer + 1) % 2;
            return true;
        }
        return false;
    }
    
    public boolean placeWall(int x, int y, boolean horizontal) {
        Player current = players[currentPlayer];
        if (!current.canPlaceWall()) {
            return false;
        }
        
        boolean success;
        if (horizontal) {
            success = board.placeHorizontalWall(x, y);
        } else {
            success = board.placeVerticalWall(x, y);
        }
        
        if (success) {
            // Check if both players still have a path to their goals
            if (!PathFinder.hasPathToGoal(board, players[0]) || 
                !PathFinder.hasPathToGoal(board, players[1])) {
                // If no path exists, undo the wall placement
                if (horizontal) {
                    board.placeHorizontalWall(x, y);
                } else {
                    board.placeVerticalWall(x, y);
                }
                return false;
            }
            
            current.placeWall();
            currentPlayer = (currentPlayer + 1) % 2;
            return true;
        }
        return false;
    }
    
    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }
    
    public Player[] getPlayers() {
        return players;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public boolean isGameOver() {
        return players[0].hasWon() || players[1].hasWon();
    }
    
    public Player getWinner() {
        if (players[0].hasWon()) return players[0];
        if (players[1].hasWon()) return players[1];
        return null;
    }
}