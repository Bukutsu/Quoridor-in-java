package quoridor;

import java.awt.Color;
import java.util.HashMap;

public class PlayerColorStore {
    private static final HashMap<Integer, Color> playerColors = new HashMap<>();

    static {
        playerColors.put(0, Color.decode("#3498db"));
        playerColors.put(1, Color.decode("#e74c3c"));
        playerColors.put(2, Color.decode("#2ecc71"));
        playerColors.put(3, Color.decode("#9b59b6"));
    }

    public static Color getPlayerColor(int playerIndex) {
        return playerColors.getOrDefault(playerIndex, Color.BLACK);
    }

    public static void setPlayerColor(int playerIndex, Color color) {
        playerColors.put(playerIndex, color);
    }
}
