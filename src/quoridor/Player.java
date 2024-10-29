package quoridor;

public class Player {
    int x, y, wall;
    public Player(int startX, int startY ,int startwall) {
        this.x = startX; 
        this.y = startY; 
        this.wall = startwall;
    }
}