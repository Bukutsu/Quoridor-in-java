package quoridor;

import java.awt.Color;
public class Wall {
    int x, y;
    boolean isHorizontal;
    Color color;

    Wall(int x, int y, boolean isHorizontal,Color color) {
        this.x = x;
        this.y = y;
        this.isHorizontal = isHorizontal;
        this.color = color;
    }
}