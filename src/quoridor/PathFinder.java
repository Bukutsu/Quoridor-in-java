package quoridor;

import java.util.*;

public class PathFinder {
    public static boolean hasPathToGoal(Board board, Player player) {
        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{player.getX(), player.getY()});
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[1] == player.getTargetRow()) {
                return true;
            }
            
            // Try all possible moves
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int newX = current[0] + dir[0];
                int newY = current[1] + dir[1];
                
                if (board.isValidMove(current[0], current[1], newX, newY)) {
                    String pos = newX + "," + newY;
                    if (!visited.contains(pos)) {
                        visited.add(pos);
                        queue.offer(new int[]{newX, newY});
                    }
                }
            }
        }
        return false;
    }
}