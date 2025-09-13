package quoridor;

public class Player {
    // Make these package-private so QuoridorPanel can access them
    int x, y, wall; // Keep 'wall' for compatibility
    private final int targetRow;
    
    public Player(int startX, int startY, int startWall) {
        this.x = startX;
        this.y = startY;
        this.wall = startWall;
        this.targetRow = (startY == 0) ? 8 : 0; // If starting at top, target is bottom, and vice versa
    }
    
    // Support for the new constructor with target row
    public Player(int startX, int startY, int startWall, int targetRow) {
        this.x = startX;
        this.y = startY;
        this.wall = startWall;
        this.targetRow = targetRow;
    }
    
    public boolean hasWon() {
        return y == targetRow;
    }
    
    public int getTargetRow() {
        return targetRow;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public boolean canPlaceWall() {
        return wall > 0;
    }
    
    public void placeWall() {
        if (canPlaceWall()) {
            wall--;
        }
    }
    
    public int getWalls() {
        return wall;
    }
}