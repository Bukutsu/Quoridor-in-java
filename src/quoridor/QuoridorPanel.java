package quoridor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class QuoridorPanel extends JPanel{
    private List<Wall> walls;
    private Player players[];
    private Player currentPlayer; // ผู้เล่นคนที่กำลังมีสิทธิ์เดิน
    private boolean gameEnded = false;
    private boolean Start = true;
    private StatusPanel statusPanel;
    
    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    private static final int BOARD_SIZE = 9;
    private static final int CELL_SIZE = 50;
    private static final int WALL_THICKNESS = 8;

    //wall click sensitivity
    private static final int CLICK_TOLERANCE = 10;

    private boolean[][] horizontalWalls = new boolean[BOARD_SIZE][BOARD_SIZE];
    private boolean[][] verticalWalls = new boolean[BOARD_SIZE][BOARD_SIZE];

    public Player[] getPlayers() {
        return players;
    }

    public QuoridorPanel() {
        walls = new ArrayList<>();
        
        players = new Player[2];
        players[0] = new Player(4,0,10);
        players[1] = new Player(4,8,10); 

        currentPlayer = players[1]; // เริ่มต้นที่ player1
        
        setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE + 1, BOARD_SIZE * CELL_SIZE + 1));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
     
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawWalls(g);
        drawPlayers(g);
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i <= BOARD_SIZE; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE, BOARD_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawWalls(Graphics g) {
        g.setColor(Color.RED);
        for (Wall wall : walls) {
            int x = wall.x * CELL_SIZE;
            int y = wall.y * CELL_SIZE;
            if (wall.isHorizontal) {
                g.fillRect(x, y - WALL_THICKNESS / 2, CELL_SIZE * 2, WALL_THICKNESS);
            } else {
                g.fillRect(x - WALL_THICKNESS / 2, y, WALL_THICKNESS, CELL_SIZE * 2);
            }
        }
    }
    private void drawPlayers(Graphics g) {
    	// g.fillOval วงกลม
    	//(ตำเเหน่ง x,ตำเเหน่ง y,wihth,height)   
    	//(ตำเเหน่ง x,y ไว้เช็คตรงกลางของช่องเดินเช่น(x=4,cell_size=50 ===> 4*50/50/4 = 212.5คือตรงกลางเเกนxที่ตัวเดินวางอยู่))
        if(currentPlayer == players[0] || Start){
		g.setColor(Color.BLUE);
	} else g.setColor(Color.decode("#5d8aa8"));
        g.fillRect(players[0].x * CELL_SIZE + CELL_SIZE / 4 , players[0].y * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
        if(currentPlayer == players[1] || Start){
		g.setColor(Color.GREEN);
	} else g.setColor(Color.decode("#679267"));
        g.fillRect(players[1].x * CELL_SIZE + CELL_SIZE / 4, players[1].y * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
  
}

    public void addWall(int x, int y, boolean isHorizontal) {
        walls.add(new Wall(x, y, isHorizontal));
        repaint();
    }

    private void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        // Check if click is close to a vertical line
        int cellX = x / CELL_SIZE;
        int cellY = y / CELL_SIZE;
        
        if (gameEnded) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
        	// Clicked near a vertical line
        	if (isCloseToVerticalLine(x)) {
                if(currentPlayer.wall < 1){
                    System.out.println("Player out of wall!");
                } 
        	    else if (canPlaceVerticalWall(cellX, cellY)) {
        	        placeVerticalWall(cellX, cellY);
        	        addWall(cellX, cellY, false);
                      	        
        	        if (!isPathAvailable(players[0]) || !isPathAvailable(players[1])) {
        	            JOptionPane.showMessageDialog(this, "Player " + (currentPlayer == players[0] ? "2" : "1") + " Wins! (Path Blocked)");
        	            gameEnded = true;
        	        } else {
        	            switchPlayer(); // สลับตา
        	            System.out.println("Clicked Vertical Wall" + "(" + cellX + "," + cellY + ")");
                        currentPlayer.wall--;
                        statusPanel.updateWallsLebel();
        	        }
        	    }
        	    else System.out.println("You Cannot Place Vertical Wall at" + "(" + cellX + "," + cellY + ")");
        	}
        	else if (isCloseToHorizontalLine(y)) {
                if(currentPlayer.wall < 1){
                    System.out.println("Player out of wall!");
                } 
        	    else if (canPlaceHorizontalWall(cellX, cellY)) {
        	        placeHorizontalWall(cellX, cellY);
        	        addWall(cellX, cellY, true);
        	        
        	        if (!isPathAvailable(players[0]) || !isPathAvailable(players[1])) {
        	            JOptionPane.showMessageDialog(this, "Player " + (currentPlayer == players[0] ? "2" : "1") + " Wins! (Path Blocked)");
        	            gameEnded = true;
        	        } else {
        	            switchPlayer(); // สลับตา
        	            System.out.println("Clicked Horizontal Wall" + "(" + cellX + "," + cellY + ")");
                        currentPlayer.wall--;
                        statusPanel.updateWallsLebel();
        	        }
        	    }
        	    else System.out.println("You Cannot Place Horizontal Wall at" + "(" + cellX + "," + cellY + ")");
        	}
         
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // คลิกขวา: เดินผู้เล่น
        	if (isMoveValid(currentPlayer, cellX, cellY)) {
                currentPlayer.x = cellX;
                currentPlayer.y = cellY;
                switchPlayer(); // สลับตา
                repaint(); // วาดใหม่หลังเดิน
		        
                System.out.println("Player moved to " + "(" + cellX + "," + cellY + ")");
            } else {
                System.out.println("Invalid move");
            }
	    Start = false;
        }

        if (players[0].y == 8) {
        	JOptionPane.showMessageDialog(this, "Player 1 Wins!");
        	gameEnded = true;
        } 
    	
        else if (players[1].y == 0) {
        	JOptionPane.showMessageDialog(this, "Player 2 Wins!");
        	gameEnded = true;
        }
    }

    private Player otherPlayer() {
        if (currentPlayer == players[0]) {
            return players[1];
        } else {
            return players[0];
        }
    }
   
    private boolean isMoveValid(Player player, int x, int y) {
        // ตรวจสอบตำแหน่งปัจจุบันของผู้เล่น
        int dx = Math.abs(player.x - x);
        int dy = Math.abs(player.y - y);
        if (x == otherPlayer().x && y == otherPlayer().y) {
            return false; // ไม่อนุญาตให้เดินไปทับตำแหน่งที่มีผู้เล่นคนอื่นอยู่
        }
        // ตรวจสอบว่ากำลังเดินในแนวนอนหรือแนวตั้งที่ห่างกัน 1 ช่อง
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            if (dx == 1) { // การเคลื่อนที่ในแนวนอน
                if (x > player.x) { // เดินขวา
                    if (!verticalWalls[player.y][player.x + 1]) return true; //ไม่มีกำเเพง
                    else return false; // มีกำแพงขวางทาง
                } else { // เดินซ้าย
                    if (!verticalWalls[player.y][player.x]) return true; //ไม่มีกำเเพง
                    else return false; // มีกำแพง
                }
            } else if (dy == 1) { // แนวตั้ง
                if (y > player.y) { // เดินลง
                    if (!horizontalWalls[player.y + 1][player.x]) return true; //ไม่มีกำเเพง
                    else return false; // มีกำแพงขวางทาง
                } else { // เดินขึ้น
                    if (!horizontalWalls[player.y][player.x]) return true; //ไม่มีกำเเพง
                    else return false; // มีกำแพง
                }
            }
        }
	// เดินข้ามผู้เล่นอื่น
        if (dx == 2 && dy == 0) {
            if (x > player.x) { // ขวา
                if (player.x + 1 == otherPlayer().x && player.y == otherPlayer().y && !verticalWalls[player.y][player.x + 2] && !verticalWalls[player.y][player.x + 1]) return true;
            } else { // ซ้าย
                if (player.x - 1 == otherPlayer().x && player.y == otherPlayer().y && !verticalWalls[player.y][player.x - 1] && !verticalWalls[player.y][player.x]) return true;
            }
        }

        if (dy == 2 && dx == 0) {
            if (y > player.y) { // ลง
                if (player.y + 1 == otherPlayer().y && player.x == otherPlayer().x && !horizontalWalls[player.y + 2][player.x] && !horizontalWalls[player.y + 1][player.x]) return true;
            } else { // ขึ้น
                if (player.y - 1 == otherPlayer().y && player.x == otherPlayer().x && !horizontalWalls[player.y - 1][player.x] && !horizontalWalls[player.y][player.x]) return true;
            }
        }
     // การเดินทแยง
        if (dx == 1 && dy == 1) {
	// ตรวจสอบว่าามีผู้เล่นอยู่ตรงหน้าและมีกำแพงขวางหลังไหม
            if (player.x + 1 == otherPlayer().x && player.y == otherPlayer().y) { // มีผู้เล่นอยู่ทางขวา
                if (verticalWalls[player.y][player.x + 2]) { // เช็คว่ามีกำแพงข้างหลังผู้เล่นที่จะข้าม
                    if (y > player.y && !horizontalWalls[player.y + 1][player.x + 1]) { // เดินทแยง-ลงขวา
                        return true;
                    } else if (y < player.y && !horizontalWalls[player.y][player.x + 1]) { // เดินทแยง-ขึ้นขวา
                        return true;
                    }
                }
            } else if (player.x - 1 == otherPlayer().x && player.y == otherPlayer().y) { //มีผู้เล่นอยู่ทางซ้าย
                if (verticalWalls[player.y][player.x - 1]) {
                    if (y > player.y && !horizontalWalls[player.y + 1][player.x]) { // เดินทแยง-ลงซ้าย
                        return true;
                    } else if (y < player.y && !horizontalWalls[player.y][player.x]) { // เดินทแยง-ขึ้นซ้าย
                        return true;
                    }
                }
            } else if (player.y + 1 == otherPlayer().y && player.x == otherPlayer().x) { // มีผู้เล่นอยู่ข้างล่าง
        	if (horizontalWalls[player.y + 2][player.x]) {
                    if (x > player.x && !verticalWalls[player.y + 1][player.x + 1]) { // เดินทแยง-ลงขวา
                        return true;
                    } else if (x < player.x && !verticalWalls[player.y + 1][player.x]) { // เดินทแยง-ลงซ้าย
                        return true;
                    }
                }
            } else if (player.y - 1 == otherPlayer().y && player.x == otherPlayer().x) { // มีผู้เล่นอยู่ข้างบน
                if (horizontalWalls[player.y - 1][player.x]) {
                    if (x > player.x && !verticalWalls[player.y - 1][player.x + 1]) { // เดินทแยง-ขึ้นขวา
                        return true;
                    } else if (x < player.x && !verticalWalls[player.y - 1][player.x]) { // เดินทแยง-ขึ้นซ้าย
                        return true;
                    }
                }
            }
        }
        return false;  //เดินผิดตำเเหน่ง
    }

    private void switchPlayer() {
        if (currentPlayer == players[0]) {
            currentPlayer = players[1];
        } else {
            currentPlayer = players[0];
        }
    }
    
    private boolean isPathAvailable(Player player) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        return bfs(player, visited);
    }

    private boolean bfs(Player player, boolean[][] visited) {
        // ใช้ Queue สำหรับการ BFS
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(player.x, player.y));
        visited[player.y][player.x] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // ตรวจสอบว่า player ถึงเส้นชัยหรือยัง
            if (player == players[0] && current.y == BOARD_SIZE - 1) {
                return true; // player1 ไปถึงเส้นชัย
            }
            if (player == players[1] && current.y == 0) {
                return true; // player2 ไปถึงเส้นชัย
            }

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
        
        return true;
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

        
        return true;
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
