package quoridor;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class QuoridorPanel extends JPanel{
    private List<Wall> walls;
    private Player players[];
    private Player currentPlayer; // ผู้เล่นคนที่กำลังมีสิทธิ์เดิน
    private StatusPanel statusPanel;
    int startingWalls = 0;
    private int previewX = -1;
    private int previewY = -1;
    private boolean previewHorizontal = true;
    private boolean isPreviewing = false;
    private boolean playerSelected = false;
    private List<Point> validMoves = new ArrayList<>();

    private static final int BOARD_SIZE = 9;
    private static final int CELL_SIZE = 50;
    private static final int WALL_THICKNESS = 8;
    private static final float GRID_LINE_THICKNESS = (float)2.3;

    //wall click sensitivity
    private static final int CLICK_TOLERANCE = 10;
    private static final int[][] initialPositions = {
        {4, 0}, // Player 0 position
        {4, 8}, // Player 1 position
        {0, 4}, // Player 2 position (for more than 2 players)
        {8, 4}  // Player 3 position (for more than 2 players)
    };


    private boolean[][] horizontalWalls = new boolean[BOARD_SIZE][BOARD_SIZE];
    private boolean[][] verticalWalls = new boolean[BOARD_SIZE][BOARD_SIZE];

    public Player[] getPlayers() {
        return players;
    }

    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public QuoridorPanel(String mode) {
        walls = new ArrayList<>();
        
        if(mode.equals("Two Player")){
            players = new Player[2];
            startingWalls = 10;
        }
        else if(mode.equals("Four Player")){
            players = new Player[4];
            startingWalls = 5;
        }

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(initialPositions[i][0], initialPositions[i][1], startingWalls);
        }

        currentPlayer = players[0]; // เริ่มต้นที่ player1
        
        setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE + 1, BOARD_SIZE * CELL_SIZE + 1));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseHover(e);
            }
        });
     
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawWalls(g);
        drawPlayers(g);
        drawWallPreview(g);
        drawMovePreview(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(GRID_LINE_THICKNESS));

        g2d.setColor(Color.decode("#2f4858"));
        for (int i = 0; i <= BOARD_SIZE; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
            g2d.drawLine(0, i * CELL_SIZE, BOARD_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawWallPreview(Graphics g) {
        if (isPreviewing) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 0, 0, 128)); // Semi-transparent red color for preview

            int x = previewX * CELL_SIZE;
            int y = previewY * CELL_SIZE;

            if (previewHorizontal) {
                g2d.fillRect(x, y - WALL_THICKNESS / 2, CELL_SIZE * 2, WALL_THICKNESS);
            } else {
                g2d.fillRect(x - WALL_THICKNESS / 2, y, WALL_THICKNESS, CELL_SIZE * 2);
            }
        }
    }

    private void drawWalls(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Wall wall : walls) {
            g2d.setColor(wall.color);
            int x = wall.x * CELL_SIZE;
            int y = wall.y * CELL_SIZE;
            if (wall.isHorizontal) {
                g2d.fillRect(x, y - WALL_THICKNESS / 2, CELL_SIZE * 2, WALL_THICKNESS);
            } else {
                g2d.fillRect(x - WALL_THICKNESS / 2, y, WALL_THICKNESS, CELL_SIZE * 2);
            }
        }
    }

    private void drawMovePreview(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 255, 0, 128)); // Light green with some transparency
    
        // Draw a preview for each valid move
        for (Point move : validMoves) {
            g2d.fillOval(
                move.x * CELL_SIZE + CELL_SIZE / 4,
                move.y * CELL_SIZE + CELL_SIZE / 4,
                CELL_SIZE / 2,
                CELL_SIZE / 2
            );
        }
    }

    private void drawPlayers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        for (int i = 0; i < players.length; i++) {
            g.setColor(PlayerColorStore.getPlayerColor(i));
            g.fillOval(
                players[i].x * CELL_SIZE + CELL_SIZE / 4, 
                players[i].y * CELL_SIZE + CELL_SIZE / 4, 
                CELL_SIZE / 2, CELL_SIZE / 2
            );
        }
    
    }

    private void resetGame(){
        walls.clear();

        // Assign positions to players
        for (int i = 0; i < players.length; i++) {
            players[i].x = initialPositions[i][0];
            players[i].y = initialPositions[i][1];
            players[i].wall = startingWalls;
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                horizontalWalls[i][j] = false;
                verticalWalls[i][j] = false;
            }
        }

        statusPanel.setElapsedTime(-1);

        currentPlayer = players[0];
        statusPanel.getTimer().start();
        statusPanel.updatePlayerPanel(currentPlayer);
        repaint();
    }

    public void addWall(int x, int y, boolean isHorizontal) {
        walls.add(new Wall(x, y, isHorizontal, PlayerColorStore.getPlayerColor(Arrays.asList(players).indexOf(currentPlayer))));
        repaint();
    }

    private void handleMouseHover(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int cellX = x / CELL_SIZE;
        int cellY = y / CELL_SIZE;

        // Determine if hovering near a wall placement
        if (isCloseToVerticalLine(x)) {
            if (canPlaceVerticalWall(cellX, cellY)) {
                previewX = cellX;
                previewY = cellY;
                previewHorizontal = false;
                isPreviewing = true;
            } else {
                isPreviewing = false;
            }
        } else if (isCloseToHorizontalLine(y)) {
            if (canPlaceHorizontalWall(cellX, cellY)) {
                previewX = cellX;
                previewY = cellY;
                previewHorizontal = true;
                isPreviewing = true;
            } else {
                isPreviewing = false;
            }
        } else {
            isPreviewing = false;
        }

        repaint(); // Repaint to show or hide the preview
    }

    private void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        // Check if click is close to a vertical line
        int cellX = x / CELL_SIZE;
        int cellY = y / CELL_SIZE;

       
        if (e.getButton() == MouseEvent.BUTTON1) {
            Point clickedCell = new Point(cellX, cellY);
            

             // Clicked near a vertical line
            if (isCloseToVerticalLine(x)) {
                playerSelected = false;
                validMoves.clear();
                if(currentPlayer.wall < 1){
                    System.out.println("Player out of wall!");
                } 
                else if (canPlaceVerticalWall(cellX, cellY)) {
                    placeVerticalWall(cellX, cellY);
                    addWall(cellX, cellY, false);
                    currentPlayer.wall--;
                    switchPlayer(); // สลับตา
                    System.out.println("Clicked Vertical Wall" + "(" + cellX + "," + cellY + ")");
                    statusPanel.updateWallsLabel();
                }
                else System.out.println("You Cannot Place Vertical Wall at" + "(" + cellX + "," + cellY + ")");
            }
            else if (isCloseToHorizontalLine(y)) {
                playerSelected = false;
                validMoves.clear();
                if(currentPlayer.wall < 1){
                    System.out.println("Player out of wall!");
                } 
                else if (canPlaceHorizontalWall(cellX, cellY)) {
                    placeHorizontalWall(cellX, cellY);
                    addWall(cellX, cellY, true);
                    currentPlayer.wall--;
                    switchPlayer(); // สลับตา
                    System.out.println("Clicked Horizontal Wall" + "(" + cellX + "," + cellY + ")");
                    statusPanel.updateWallsLabel();
                }
                else System.out.println("You Cannot Place Horizontal Wall at" + "(" + cellX + "," + cellY + ")");
            }

            if (!playerSelected) {
                // Select the player if clicked on the current player's position
                if (cellX == currentPlayer.x && cellY == currentPlayer.y) {
                    playerSelected = true;
                    validMoves.clear();

                    // Calculate valid moves around the current player
                    calculateValidMoves();
                    
                    repaint(); // Repaint to show move previews
                    System.out.println("Player selected at (" + cellX + ", " + cellY + ")");
                }
            } else {
                // If player is already selected, attempt to move to the clicked cell
                if (validMoves.contains(clickedCell)) {
                    currentPlayer.x = cellX;
                    currentPlayer.y = cellY;

                    // Clear selection and previews, and switch to the next player
                    playerSelected = false;
                    validMoves.clear();
                    switchPlayer();
                    repaint();

                    System.out.println("Player moved to (" + cellX + ", " + cellY + ")");
                } else {
                    System.out.println("Invalid move");
                }
            }
        	
        } 
        

        for (int i = 0; i < players.length; i++) {
            if (i == 0 && players[i].y == 8) {
                statusPanel.getTimer().stop();
                JOptionPane.showMessageDialog(this, "Player 1 Wins!");
                resetGame();
                break;
            } else if (i == 1 && players[i].y == 0) {
                statusPanel.getTimer().stop();
                JOptionPane.showMessageDialog(this, "Player 2 Wins!");
                resetGame();
                break;
            } else if (i == 2 && players[i].x == 8) {
                statusPanel.getTimer().stop();
                JOptionPane.showMessageDialog(this, "Player 3 Wins!");
                resetGame();
                break;
            } else if (i == 3 && players[i].x == 0) {
                statusPanel.getTimer().stop();
                JOptionPane.showMessageDialog(this, "Player 4 Wins!");
                resetGame();
                break;
            }
        }
    }

    private void calculateValidMoves() {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Up, down, right, left
    
        // Loop through each direction to find valid moves
        for (int[] dir : directions) {
            int newX = currentPlayer.x + dir[0];
            int newY = currentPlayer.y + dir[1];
    
            // Check if within board boundaries
            if (newX >= 0 && newY >= 0 && newX < BOARD_SIZE && newY < BOARD_SIZE) {
                if (isMoveValid(currentPlayer, newX, newY)) {
                    // If the move is valid, add it to validMoves
                    validMoves.add(new Point(newX, newY));
                } else {
                    // Check if there's a player in the adjacent cell and handle jump-over
                    Player blockingPlayer = null;
                    for (Player other : players) {
                        if (other != currentPlayer && other.x == newX && other.y == newY) {
                            blockingPlayer = other;
                            break;
                        }
                    }
    
                    if (blockingPlayer != null) {
                        // Calculate jump-over position
                        int jumpX = newX + dir[0];
                        int jumpY = newY + dir[1];
    
                        // Check if the jump-over move is valid
                        if (jumpX >= 0 && jumpY >= 0 && jumpX < BOARD_SIZE && jumpY < BOARD_SIZE) {
                            if (isMoveValid(currentPlayer, jumpX, jumpY)) {
                                validMoves.add(new Point(jumpX, jumpY));
                            }
                        } 
    
                        // If the jump is blocked or out of bounds, check diagonal moves
                        int[][] diagonalDirections;
    
                        if (dir[0] == 0) { // Moving vertically (up or down)
                            diagonalDirections = new int[][]{{1, 0}, {-1, 0}}; // Try moving left or right
                        } else { // Moving horizontally (left or right)
                            diagonalDirections = new int[][]{{0, 1}, {0, -1}}; // Try moving up or down
                        }
    
                        for (int[] diagDir : diagonalDirections) {
                            int diagX = blockingPlayer.x + diagDir[0];
                            int diagY = blockingPlayer.y + diagDir[1];
    
                            if (diagX >= 0 && diagY >= 0 && diagX < BOARD_SIZE && diagY < BOARD_SIZE) {
                                // Check if diagonal move is valid
                                if (isMoveValid(currentPlayer, diagX, diagY)) {
                                    validMoves.add(new Point(diagX, diagY));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    


    private boolean isMoveValid(Player player, int x, int y) {
        // Don't allow moving to spaces with other players
        for (Player other : players) {
            if (other != player && x == other.x && y == other.y) {
                return false;
            }
        }
    
        int dx = Math.abs(player.x - x);
        int dy = Math.abs(player.y - y);
    
        // Normal move (1 step horizontally or vertically)
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            // Moving right
            if (x > player.x) return !verticalWalls[player.y][player.x + 1];
            // Moving left
            if (x < player.x) return !verticalWalls[player.y][player.x];
            // Moving down
            if (y > player.y) return !horizontalWalls[player.y + 1][player.x];
            // Moving up
            if (y < player.y) return !horizontalWalls[player.y][player.x];
        }
    
        // Jump move (2 steps over another player)
        if (dx == 2 && dy == 0) {  // Horizontal jump
            for (Player other : players) {
                // Jump right
                if (other != player && player.x + 1 == other.x && player.y == other.y) {
                    if (x > player.x && !verticalWalls[player.y][player.x + 2] && 
                        !verticalWalls[player.y][player.x + 1]) {
                        return true;
                    }
                }
                // Jump left
                if (other != player && player.x - 1 == other.x && player.y == other.y) {
                    if (x < player.x && !verticalWalls[player.y][player.x - 1] && 
                        !verticalWalls[player.y][player.x]) {
                        return true;
                    }
                }
            }
        }
    
        if (dy == 2 && dx == 0) {  // Vertical jump
            for (Player other : players) {
                // Jump down
                if (other != player && player.y + 1 == other.y && player.x == other.x) {
                    if (y > player.y && !horizontalWalls[player.y + 2][player.x] && 
                        !horizontalWalls[player.y + 1][player.x]) {
                        return true;
                    }
                }
                // Jump up
                if (other != player && player.y - 1 == other.y && player.x == other.x) {
                    if (y < player.y && !horizontalWalls[player.y - 1][player.x] && 
                        !horizontalWalls[player.y][player.x]) {
                        return true;
                    }
                }
            }
        }
    
        // Diagonal move (when blocked by two players)
        if (dx == 1 && dy == 1) {
            boolean hasRightPlayer = false, hasLeftPlayer = false;
            boolean hasAbovePlayer = false, hasBelowPlayer = false;
    
            // Check for adjacent players
            for (Player other : players) {
                if (other != player) {
                    if (other.x == player.x + 1 && other.y == player.y) hasRightPlayer = true;
                    if (other.x == player.x - 1 && other.y == player.y) hasLeftPlayer = true;
                    if (other.x == player.x && other.y == player.y - 1) hasAbovePlayer = true;
                    if (other.x == player.x && other.y == player.y + 1) hasBelowPlayer = true;
                }
            }
    
            // Check each diagonal direction
            if (x > player.x && y < player.y && hasRightPlayer && hasAbovePlayer) {
                return !verticalWalls[player.y - 1][player.x + 1] && 
                       !horizontalWalls[player.y][player.x + 1];
            }
            if (x < player.x && y < player.y && hasLeftPlayer && hasAbovePlayer) {
                return !verticalWalls[player.y - 1][player.x] && 
                       !horizontalWalls[player.y][player.x - 1];
            }
            if (x > player.x && y > player.y && hasRightPlayer && hasBelowPlayer) {
                return !verticalWalls[player.y + 1][player.x + 1] && 
                       !horizontalWalls[player.y + 1][player.x + 1];
            }
            if (x < player.x && y > player.y && hasLeftPlayer && hasBelowPlayer) {
                return !verticalWalls[player.y + 1][player.x] && 
                       !horizontalWalls[player.y + 1][player.x - 1];
            }
        }
    
        return false;
    }

    private void switchPlayer() {
    	int currentIndex = Arrays.asList(players).indexOf(currentPlayer);
    	currentPlayer = players[(currentIndex + 1) % players.length];
        statusPanel.updatePlayerPanel(currentPlayer);
    }
    
    private boolean isPathAvailable(Player player) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        return bfs(player, visited);
    }

    public boolean hasPlayerWon(Player player, Point current) {
        int playerIndex = -1;
    
        // Find the player's index
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                playerIndex = i;
                break;
            }
        }
        
        // Check winning conditions based on the player's index
        switch (playerIndex) {
            case 0:
                return current.y == BOARD_SIZE - 1; // Player 1 wins if they reach y = 8
            case 1:
                return current.y == 0; // Player 2 wins if they reach y = 0
            case 2:
                return current.x == BOARD_SIZE - 1; // Player 3 wins if they reach x = 8
            case 3:
                return current.x == 0; // Player 4 wins if they reach x = 0
            default:
                return false;
        }
    }

    private boolean bfs(Player player, boolean[][] visited) {
        // ใช้ Queue สำหรับการ BFS
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(player.x, player.y));
        visited[player.y][player.x] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            //*************************************************** 4 คน ***********************************************
            if(hasPlayerWon(player, current)) return true;

            // ตรวจสอบช่องทางที่สามารถเดินไปได้ (ขึ้น, ลง, ซ้าย, ขวา)
            if (current.x > 0 && !verticalWalls[current.y][current.x] && !visited[current.y][current.x - 1]) { // ซ้าย
                queue.add(new Point(current.x - 1, current.y));
                visited[current.y][current.x - 1] = true;
            }
            if (current.x < BOARD_SIZE - 1 && !verticalWalls[current.y][current.x + 1] && !visited[current.y][current.x + 1]) { // ขวา
                queue.add(new Point(current.x + 1, current.y));
                visited[current.y][current.x + 1] = true;
            }
            if (current.y > 0 && !horizontalWalls[current.y][current.x] && !visited[current.y - 1][current.x]) { // ขึ้น
                queue.add(new Point(current.x, current.y - 1));
                visited[current.y - 1][current.x] = true;
            }
            if (current.y < BOARD_SIZE - 1 && !horizontalWalls[current.y + 1][current.x] && !visited[current.y + 1][current.x]) { // ลง
                queue.add(new Point(current.x, current.y + 1));
                visited[current.y + 1][current.x] = true;
            }
        }
        return false; // ไม่มีทางไปถึงเส้นชัย
    }
    
    private boolean isCloseToVerticalLine(int x) {
        return Math.abs(x % CELL_SIZE) < CLICK_TOLERANCE;
    }
    
    private boolean isCloseToHorizontalLine(int y) {
        return Math.abs(y % CELL_SIZE) < CLICK_TOLERANCE;
    }

    private boolean canPlaceHorizontalWall(int x, int y) {
        // Check board boundaries
        if (x < 0 || x >= BOARD_SIZE - 1 || y <= 0 || y >= BOARD_SIZE) {
            return false;
        }
        
        // Check for overlap with existing horizontal walls
        if (horizontalWalls[y][x] || horizontalWalls[y][x+1]) {
            return false;
        }

        int verticalWallDownward = 0;
        for (int i = y; i < BOARD_SIZE; i++){
            if(verticalWalls[i][x+1]){
                verticalWallDownward++;
            }
        }

        if(verticalWallDownward % 2 != 0){
            return false;
        }
        
        placeHorizontalWall(x, y);

        boolean pathAvailableForAllPlayers = true;
        for(Player player : players){
            if(!isPathAvailable(player)){
                pathAvailableForAllPlayers = false;
                break;
            }
        }

        horizontalWalls[y][x] = false;
        horizontalWalls[y][x+1] = false;
        
        return pathAvailableForAllPlayers;
    }

    private boolean canPlaceVerticalWall(int x, int y) {
        // Check board boundaries
        if (x <= 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE - 1) {
            return false;
        }
        
        // Check for overlap with existing vertical walls
        if (verticalWalls[y][x] || verticalWalls[y+1][x]) {
            return false;
        }
        
        int horizontalWallRightward = 0;
        for (int i = x; i < BOARD_SIZE; i++){
            if(horizontalWalls[y+1][i]){
                horizontalWallRightward++;
            }
        }

        if(horizontalWallRightward % 2 != 0){
            return false;
        }
        placeVerticalWall(x, y);//วางกำเเพง

        boolean pathAvailableForAllPlayers = true;
        for(Player player : players){
            if(!isPathAvailable(player)){
                pathAvailableForAllPlayers = false;
                break;
            }
        }

        verticalWalls[y][x] = false;
        verticalWalls[y+1][x] = false;

        return pathAvailableForAllPlayers;
    }

    private void placeHorizontalWall(int x,int y){
        horizontalWalls[y][x] = true;
        horizontalWalls[y][x+1] = true;
    }

    private void placeVerticalWall(int x,int y){
        verticalWalls[y][x] = true;
        verticalWalls[y+1][x] = true;
    }

}
