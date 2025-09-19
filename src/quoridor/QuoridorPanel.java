package quoridor;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;


public class QuoridorPanel extends JPanel{
    private final List<Wall> walls;
    private Player[] players;
    private Player currentPlayer; // ผู้เล่นคนที่กำลังมีสิทธิ์เดิน
    private int currentPlayerIndex;
    private StatusPanel statusPanel;
    private int startingWalls;
    private int previewX = -1;
    private int previewY = -1;
    private boolean previewHorizontal = true;
    private boolean isPreviewing = false;
    private boolean playerSelected = false;
    private final List<Point> validMoves = new ArrayList<>();

    private static final int BOARD_SIZE = 9;
    private static final int CELL_SIZE = 50;
    private static final int WALL_THICKNESS = 8;
    private static final float GRID_LINE_THICKNESS = (float)2.3;
    private static final int BOARD_PIXEL_SIZE = BOARD_SIZE * CELL_SIZE;
    private static final Stroke GRID_STROKE = new BasicStroke(GRID_LINE_THICKNESS,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);
    private static final Stroke BOARD_BORDER_STROKE = new BasicStroke(3f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);

    private static final Color BOARD_GRADIENT_TOP = new Color(246, 229, 192);
    private static final Color BOARD_GRADIENT_BOTTOM = new Color(223, 190, 142);
    private static final Color BOARD_BORDER_COLOR = new Color(154, 123, 91);
    private static final Color GRID_LINE_COLOR = new Color(72, 59, 48);
    private static final Color VALID_MOVE_FILL = new Color(46, 204, 113, 160);
    private static final Color VALID_MOVE_STROKE = new Color(27, 156, 88, 200);
    private static final Color CURRENT_PLAYER_GLOW = new Color(255, 255, 255, 160);
    private static final Color PLAYER_OUTLINE_COLOR = new Color(45, 45, 45, 160);
    private static final Color WALL_BORDER_COLOR = new Color(54, 47, 43, 140);

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
        if (statusPanel != null) {
            statusPanel.updatePlayerPanel(currentPlayer);
            statusPanel.updateWallsLabel();
            statusPanel.setStatusMessage(String.format("%s's turn.", getPlayerDisplayName(currentPlayer)));
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public QuoridorPanel(String mode) {
        walls = new ArrayList<>();

        if ("Four Player".equals(mode)) {
            players = new Player[4];
            startingWalls = 5;
        } else {
            players = new Player[2];
            startingWalls = 10;
        }

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(initialPositions[i][0], initialPositions[i][1], startingWalls);
        }

        currentPlayerIndex = 0;
        currentPlayer = players[currentPlayerIndex];

        setPreferredSize(new Dimension(BOARD_PIXEL_SIZE + 1, BOARD_PIXEL_SIZE + 1));
        setBackground(new Color(240, 236, 227));
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

        paintBoardBackground(g2d);

        g2d.setStroke(GRID_STROKE);
        g2d.setColor(GRID_LINE_COLOR);
        for (int i = 0; i <= BOARD_SIZE; i++) {
            int position = i * CELL_SIZE;
            g2d.drawLine(position, 0, position, BOARD_PIXEL_SIZE);
            g2d.drawLine(0, position, BOARD_PIXEL_SIZE, position);
        }
    }

    private void paintBoardBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(0, 0, BOARD_GRADIENT_TOP, 0, BOARD_PIXEL_SIZE, BOARD_GRADIENT_BOTTOM);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, BOARD_PIXEL_SIZE, BOARD_PIXEL_SIZE);

        g2d.setPaint(BOARD_BORDER_COLOR);
        g2d.setStroke(BOARD_BORDER_STROKE);
        g2d.drawRoundRect(0, 0, BOARD_PIXEL_SIZE - 1, BOARD_PIXEL_SIZE - 1, 18, 18);
    }

    private void drawWallPreview(Graphics g) {
        if (!isPreviewing) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color baseColor = PlayerColorStore.getPlayerColor(currentPlayerIndex);
        Color previewColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150);
        g2d.setColor(previewColor);

        int x = previewX * CELL_SIZE;
        int y = previewY * CELL_SIZE;
        if (previewHorizontal) {
            Shape shape = new RoundRectangle2D.Float(
                    x,
                    y - WALL_THICKNESS / 2f,
                    CELL_SIZE * 2,
                    WALL_THICKNESS,
                    WALL_THICKNESS,
                    WALL_THICKNESS);
            g2d.fill(shape);
        } else {
            Shape shape = new RoundRectangle2D.Float(
                    x - WALL_THICKNESS / 2f,
                    y,
                    WALL_THICKNESS,
                    CELL_SIZE * 2,
                    WALL_THICKNESS,
                    WALL_THICKNESS);
            g2d.fill(shape);
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
                Shape shape = new RoundRectangle2D.Float(
                        x,
                        y - WALL_THICKNESS / 2f,
                        CELL_SIZE * 2,
                        WALL_THICKNESS,
                        WALL_THICKNESS,
                        WALL_THICKNESS);
                g2d.fill(shape);
                g2d.setColor(WALL_BORDER_COLOR);
                g2d.draw(shape);
            } else {
                Shape shape = new RoundRectangle2D.Float(
                        x - WALL_THICKNESS / 2f,
                        y,
                        WALL_THICKNESS,
                        CELL_SIZE * 2,
                        WALL_THICKNESS,
                        WALL_THICKNESS);
                g2d.fill(shape);
                g2d.setColor(WALL_BORDER_COLOR);
                g2d.draw(shape);
            }
        }
    }

    private void drawMovePreview(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2f));

        for (Point move : validMoves) {
            int centerX = move.x * CELL_SIZE + CELL_SIZE / 2;
            int centerY = move.y * CELL_SIZE + CELL_SIZE / 2;
            int radius = CELL_SIZE / 4;
            int diameter = radius * 2;

            g2d.setColor(VALID_MOVE_FILL);
            g2d.fillOval(centerX - radius, centerY - radius, diameter, diameter);

            g2d.setColor(VALID_MOVE_STROKE);
            g2d.drawOval(centerX - radius, centerY - radius, diameter, diameter);
        }
    }

    private void drawPlayers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            int pieceX = player.x * CELL_SIZE + CELL_SIZE / 4;
            int pieceY = player.y * CELL_SIZE + CELL_SIZE / 4;
            int pieceDiameter = CELL_SIZE / 2;

            if (player == currentPlayer) {
                int centerX = player.x * CELL_SIZE + CELL_SIZE / 2;
                int centerY = player.y * CELL_SIZE + CELL_SIZE / 2;
                int glowDiameter = pieceDiameter + 12;
                g2d.setColor(CURRENT_PLAYER_GLOW);
                g2d.fillOval(centerX - glowDiameter / 2, centerY - glowDiameter / 2, glowDiameter, glowDiameter);
            }

            g2d.setColor(PlayerColorStore.getPlayerColor(i));
            g2d.fillOval(pieceX, pieceY, pieceDiameter, pieceDiameter);

            g2d.setColor(PLAYER_OUTLINE_COLOR);
            g2d.setStroke(new BasicStroke(2.2f));
            g2d.drawOval(pieceX, pieceY, pieceDiameter, pieceDiameter);
        }

    }

    private void resetGame(){
        walls.clear();

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

        playerSelected = false;
        validMoves.clear();
        isPreviewing = false;
        previewX = -1;
        previewY = -1;

        currentPlayerIndex = 0;
        currentPlayer = players[currentPlayerIndex];

        if (statusPanel != null) {
            statusPanel.setElapsedTime(0);
            statusPanel.getTimer().restart();
            statusPanel.updatePlayerPanel(currentPlayer);
            statusPanel.updateWallsLabel();
            statusPanel.updateStatusPanel();
        }

        setStatusMessage(String.format("New game ready. %s's turn.", getPlayerDisplayName(currentPlayer)));
        repaint();
    }

    public void addWall(int x, int y, boolean isHorizontal) {
        walls.add(new Wall(x, y, isHorizontal, PlayerColorStore.getPlayerColor(currentPlayerIndex)));
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
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        int pixelX = e.getX();
        int pixelY = e.getY();
        int cellX = pixelX / CELL_SIZE;
        int cellY = pixelY / CELL_SIZE;

        if (handleWallInteraction(pixelX, pixelY, cellX, cellY)) {
            checkForWinner();
            return;
        }

        if (!playerSelected) {
            selectCurrentPlayer(cellX, cellY);
        } else {
            attemptPlayerMove(cellX, cellY);
        }

        checkForWinner();
    }

    private boolean handleWallInteraction(int pixelX, int pixelY, int cellX, int cellY) {
        if (isCloseToVerticalLine(pixelX)) {
            tryPlaceWall(cellX, cellY, false);
            return true;
        }

        if (isCloseToHorizontalLine(pixelY)) {
            tryPlaceWall(cellX, cellY, true);
            return true;
        }

        return false;
    }

    private void selectCurrentPlayer(int cellX, int cellY) {
        if (cellX == currentPlayer.x && cellY == currentPlayer.y) {
            playerSelected = true;
            validMoves.clear();
            calculateValidMoves();
            repaint();
            setStatusMessage(String.format("Select a destination for %s.", getPlayerDisplayName(currentPlayer)));
        } else {
            Toolkit.getDefaultToolkit().beep();
            setStatusMessage(String.format("Click %s to begin their move.", getPlayerDisplayName(currentPlayer)));
        }
    }

    private void attemptPlayerMove(int cellX, int cellY) {
        Point targetCell = new Point(cellX, cellY);
        if (!validMoves.contains(targetCell)) {
            Toolkit.getDefaultToolkit().beep();
            setStatusMessage("That square is not a valid move. Choose one of the highlighted circles.");
            return;
        }

        String moverName = getPlayerDisplayName(currentPlayer);
        currentPlayer.x = cellX;
        currentPlayer.y = cellY;
        playerSelected = false;
        validMoves.clear();
        switchPlayer();
        setStatusMessage(String.format("%s moved to (%d, %d). %s's turn.",
                moverName,
                cellX + 1,
                cellY + 1,
                getPlayerDisplayName(currentPlayer)));
        repaint();
    }

    private void checkForWinner() {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (hasPlayerWon(player, new Point(player.x, player.y))) {
                String winnerName = getPlayerDisplayName(player);
                if (statusPanel != null) {
                    statusPanel.getTimer().stop();
                }
                JOptionPane.showMessageDialog(this, winnerName + " wins!");
                setStatusMessage(String.format("%s wins! Starting a new round...", winnerName));
                resetGame();
                break;
            }
        }
    }

    private boolean tryPlaceWall(int cellX, int cellY, boolean horizontal) {
        playerSelected = false;
        validMoves.clear();

        if (!currentPlayer.canPlaceWall()) {
            Toolkit.getDefaultToolkit().beep();
            setStatusMessage(String.format("%s has no walls remaining.", getPlayerDisplayName(currentPlayer)));
            repaint();
            return false;
        }

        boolean placementAllowed = horizontal ? canPlaceHorizontalWall(cellX, cellY)
                                              : canPlaceVerticalWall(cellX, cellY);

        if (!placementAllowed) {
            Toolkit.getDefaultToolkit().beep();
            setStatusMessage("That wall cannot be placed there.");
            repaint();
            return false;
        }

        String placingPlayerName = getPlayerDisplayName(currentPlayer);

        if (horizontal) {
            placeHorizontalWall(cellX, cellY);
        } else {
            placeVerticalWall(cellX, cellY);
        }

        addWall(cellX, cellY, horizontal);
        currentPlayer.placeWall();
        if (statusPanel != null) {
            statusPanel.updateWallsLabel();
        }
        switchPlayer();
        setStatusMessage(String.format("%s placed a wall. %s's turn.",
                placingPlayerName,
                getPlayerDisplayName(currentPlayer)));
        repaint();
        return true;
    }

    private void calculateValidMoves() {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Up, down, right, left

        for (int[] dir : directions) {
            int adjacentX = currentPlayer.x + dir[0];
            int adjacentY = currentPlayer.y + dir[1];

            if (!isInsideBoard(adjacentX, adjacentY)) {
                continue;
            }

            if (isMoveValid(currentPlayer, adjacentX, adjacentY)) {
                addValidMove(adjacentX, adjacentY);
                continue;
            }

            Player blockingPlayer = playerAt(adjacentX, adjacentY, currentPlayer);
            if (blockingPlayer == null) {
                continue;
            }

            int jumpX = adjacentX + dir[0];
            int jumpY = adjacentY + dir[1];

            if (isInsideBoard(jumpX, jumpY) && isMoveValid(currentPlayer, jumpX, jumpY)) {
                addValidMove(jumpX, jumpY);
                continue;
            }

            int[][] diagonalDirections = (dir[0] == 0)
                    ? new int[][]{{1, 0}, {-1, 0}}
                    : new int[][]{{0, 1}, {0, -1}};

            for (int[] diagonal : diagonalDirections) {
                int diagX = blockingPlayer.x + diagonal[0];
                int diagY = blockingPlayer.y + diagonal[1];

                if (isInsideBoard(diagX, diagY) && isMoveValid(currentPlayer, diagX, diagY)) {
                    addValidMove(diagX, diagY);
                }
            }
        }
    }

    private void addValidMove(int x, int y) {
        Point candidate = new Point(x, y);
        if (!validMoves.contains(candidate)) {
            validMoves.add(candidate);
        }
    }




    private boolean isMoveValid(Player player, int targetX, int targetY) {
        if (!isInsideBoard(targetX, targetY)) {
            return false;
        }

        if (playerAt(targetX, targetY) != null) {
            return false;
        }

        int dx = targetX - player.x;
        int dy = targetY - player.y;
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        if ((absDx == 1 && absDy == 0) || (absDx == 0 && absDy == 1)) {
            return isStepClear(player.x, player.y, targetX, targetY);
        }

        if (absDx == 0 && absDy == 2) {
            int direction = Integer.signum(dy);
            Player blocker = playerAt(player.x, player.y + direction, player);
            if (blocker == null) {
                return false;
            }
            if (!isStepClear(player.x, player.y, blocker.x, blocker.y)) {
                return false;
            }
            return isStepClear(blocker.x, blocker.y, targetX, targetY);
        }

        if (absDy == 0 && absDx == 2) {
            int direction = Integer.signum(dx);
            Player blocker = playerAt(player.x + direction, player.y, player);
            if (blocker == null) {
                return false;
            }
            if (!isStepClear(player.x, player.y, blocker.x, blocker.y)) {
                return false;
            }
            return isStepClear(blocker.x, blocker.y, targetX, targetY);
        }

        if (absDx == 1 && absDy == 1) {
            int horizontalDir = Integer.signum(dx);
            int verticalDir = Integer.signum(dy);

            Player horizontalNeighbor = playerAt(player.x + horizontalDir, player.y, player);
            if (horizontalNeighbor != null && isStepClear(player.x, player.y, horizontalNeighbor.x, horizontalNeighbor.y)) {
                if (isStraightBlocked(horizontalNeighbor, horizontalDir, 0)) {
                    return canMoveAroundBlock(player, horizontalNeighbor, horizontalDir, verticalDir);
                }
            }

            Player verticalNeighbor = playerAt(player.x, player.y + verticalDir, player);
            if (verticalNeighbor != null && isStepClear(player.x, player.y, verticalNeighbor.x, verticalNeighbor.y)) {
                if (isStraightBlocked(verticalNeighbor, 0, verticalDir)) {
                    return canMoveAroundBlock(player, verticalNeighbor, horizontalDir, verticalDir);
                }
            }
        }

        return false;
    }

    private boolean isInsideBoard(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    private Player playerAt(int x, int y) {
        return playerAt(x, y, null);
    }

    private Player playerAt(int x, int y, Player exclude) {
        for (Player player : players) {
            if (player == exclude) {
                continue;
            }
            if (player.x == x && player.y == y) {
                return player;
            }
        }
        return null;
    }

    private boolean isStepClear(int fromX, int fromY, int toX, int toY) {
        if (!isInsideBoard(toX, toY)) {
            return false;
        }

        if (fromX == toX && fromY == toY) {
            return true;
        }

        if (fromX == toX) { // vertical movement
            if (toY > fromY) {
                return !horizontalWalls[fromY + 1][fromX];
            }
            return !horizontalWalls[fromY][fromX];
        }

        if (fromY == toY) { // horizontal movement
            if (toX > fromX) {
                return !verticalWalls[fromY][fromX + 1];
            }
            return !verticalWalls[fromY][fromX];
        }

        return false;
    }

    private boolean isStraightBlocked(Player blocker, int dirX, int dirY) {
        int nextX = blocker.x + dirX;
        int nextY = blocker.y + dirY;

        if (!isInsideBoard(nextX, nextY)) {
            return true;
        }

        if (!isStepClear(blocker.x, blocker.y, nextX, nextY)) {
            return true;
        }

        return playerAt(nextX, nextY) != null;
    }

    private boolean canMoveAroundBlock(Player player, Player blocker, int horizontalDir, int verticalDir) {
        int targetX = player.x + horizontalDir;
        int targetY = player.y + verticalDir;

        if (!isInsideBoard(targetX, targetY) || playerAt(targetX, targetY) != null) {
            return false;
        }

        if (blocker.y == player.y) { // blocker is horizontal neighbour
            int verticalStepX = player.x;
            int verticalStepY = player.y + verticalDir;

            if (!isInsideBoard(verticalStepX, verticalStepY)) {
                return false;
            }

            if (playerAt(verticalStepX, verticalStepY) != null) {
                return false;
            }

            return isStepClear(player.x, player.y, verticalStepX, verticalStepY)
                    && isStepClear(blocker.x, blocker.y, blocker.x, blocker.y + verticalDir)
                    && isStepClear(verticalStepX, verticalStepY, targetX, targetY);
        }

        if (blocker.x == player.x) { // blocker is vertical neighbour
            int horizontalStepX = player.x + horizontalDir;
            int horizontalStepY = player.y;

            if (!isInsideBoard(horizontalStepX, horizontalStepY)) {
                return false;
            }

            if (playerAt(horizontalStepX, horizontalStepY) != null) {
                return false;
            }

            return isStepClear(player.x, player.y, horizontalStepX, horizontalStepY)
                    && isStepClear(blocker.x, blocker.y, blocker.x + horizontalDir, blocker.y)
                    && isStepClear(horizontalStepX, horizontalStepY, targetX, targetY);
        }

        return false;
    }

    private void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        currentPlayer = players[currentPlayerIndex];
        if (statusPanel != null) {
            statusPanel.updatePlayerPanel(currentPlayer);
        }
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

    private void setStatusMessage(String message) {
        if (statusPanel != null) {
            statusPanel.setStatusMessage(message);
        }
    }

    private String getPlayerDisplayName(Player player) {
        int index = indexOfPlayer(player);
        if (index >= 0) {
            if (statusPanel != null) {
                String name = statusPanel.getPlayerName(index);
                if (name != null && !name.isBlank()) {
                    return name;
                }
            }
            return "Player " + (index + 1);
        }
        return "Player";
    }

    private int indexOfPlayer(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

}
