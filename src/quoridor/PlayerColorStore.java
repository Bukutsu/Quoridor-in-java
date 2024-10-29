package quoridor;

import java.awt.Color;
import java.util.HashMap;

public class PlayerColorStore {
    private static final HashMap<Integer, Color> playerColors = new HashMap<>();

    static {
        playerColors.put(0, Color.RED);
        playerColors.put(1, Color.BLUE);
        playerColors.put(2, Color.GREEN);
        playerColors.put(3, Color.YELLOW);
    }

    public static Color getPlayerColor(int playerIndex) {
        return playerColors.getOrDefault(playerIndex, Color.BLACK);
    }

    public static void setPlayerColor(int playerIndex, Color color) {
        playerColors.put(playerIndex, color);
    }
}
