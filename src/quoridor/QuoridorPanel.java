package quoridor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;


public class QuoridorPanel extends JPanel{
    private List<Wall> walls;
    private Player players[];
    private Player currentPlayer; // ผู้เล่นคนที่กำลังมีสิทธิ์เดิน
    private boolean Start = true;
    private StatusPanel statusPanel;
    
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

    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }


    public QuoridorPanel() {
        walls = new ArrayList<>();
        players = new Player[2];
        players[0] = new Player(4,0,10);
        players[1] = new Player(4,8,10); 
//*************************************************** 4 คน **********************************************
//      players = new Player[4];
//      players[0] = new Player(4, 0, 10);
//      players[1] = new Player(4, 8, 10);
//      players[2] = new Player(0, 4, 10); 
//      players[3] = new Player(8, 4, 10);
        currentPlayer = players[0]; // เริ่มต้นที่ player1
        
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

    private void resetGame(){
        walls.clear();

        players[1].x = 4;
        players[1].y = 8;
        players[0].x = 4;
        players[0].y = 0;
//*************************************************** 4 คน ***********************************************
//      players[1].x = 4;players[1].y = 8;
//      players[0].x = 4;players[0].y = 0;
//      players[2].x = 0;players[2].y = 4;
//      players[3].x = 8;players[3].y = 4;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                horizontalWalls[i][j] = false;
                verticalWalls[i][j] = false;
            }
        }

        statusPanel.setElapsedTime(-1);

        currentPlayer = players[0];
        Start = true;
        repaint();
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
//*************************************************** 4 คน ***********************************************
//      Color[] colors = {Color.BLUE, Color.GREEN, Color.RED, Color.PINK}; 
//      for (int i = 0; i < players.length; i++) {
//          g.setColor(colors[i]);
//          g.fillRect(
//              players[i].x * CELL_SIZE + CELL_SIZE / 4, 
//              players[i].y * CELL_SIZE + CELL_SIZE / 4, 
//              CELL_SIZE / 2, CELL_SIZE / 2
//          );
//      }
//  }
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
        

        if (e.getButton() == MouseEvent.BUTTON1) {
        	// Clicked near a vertical line
        	if (isCloseToVerticalLine(x)) {
                if(currentPlayer.wall < 1){
                    System.out.println("Player out of wall!");
                } 
        	    else if (canPlaceVerticalWall(cellX, cellY)) {
        	        placeVerticalWall(cellX, cellY);
        	        addWall(cellX, cellY, false);
                    currentPlayer.wall--;
        	        switchPlayer(); // สลับตา
        	        System.out.println("Clicked Vertical Wall" + "(" + cellX + "," + cellY + ")");
                    statusPanel.updateWallsLebel();
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
                    currentPlayer.wall--;
        	        switchPlayer(); // สลับตา
        	        System.out.println("Clicked Horizontal Wall" + "(" + cellX + "," + cellY + ")");
                    statusPanel.updateWallsLebel();
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
        	resetGame();
        } 
    	
        else if (players[1].y == 0) {
        	JOptionPane.showMessageDialog(this, "Player 2 Wins!");
        	resetGame();
        }
        //*************************************************** 4 คน ***********************************************
//      if (players[0].y == 8) {
//    	JOptionPane.showMessageDialog(this, "Player 1 Wins!");
//    	resetGame();
//    } 
//	
//    else if (players[1].y == 0) {
//    	JOptionPane.showMessageDialog(this, "Player 2 Wins!");
//    	resetGame();
//    }
//    else if (players[2].x == 8) {
//    	JOptionPane.showMessageDialog(this, "Player 3 Wins!");
//    	resetGame();
//    }
//    else if (players[3].x == 0) {
//    	JOptionPane.showMessageDialog(this, "Player 3 Wins!");
//    	resetGame();
//    }
    }

   
    private boolean isMoveValid(Player player, int x, int y) {
        // ตรวจสอบตำแหน่งปัจจุบันของผู้เล่น
        int dx = Math.abs(player.x - x);
        int dy = Math.abs(player.y - y);
        for (Player other : players) {
          if (other != player && x == other.x && y == other.y) {
              return false; // ไม่อนุญาตให้เดินไปทับตำแหน่งที่มีผู้เล่นคนอื่นอยู่
          }
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
       if (dy == 2 && dx == 0) {
            for (Player other : players) {
                if (other != player && player.y + 1 == other.y && player.x == other.x) { // ลง
                    for (Player behind : players) {
                        if (behind != player && behind != other && behind.y == player.y + 2 && behind.x == player.x) {
                            return false; // มีผู้เล่นอีกคนอยู่ข้างหลัง
                        }
                    }
                    if (y > player.y && !horizontalWalls[player.y + 2][player.x] && !horizontalWalls[player.y + 1][player.x]) return true;
                } else if (other != player && player.y - 1 == other.y && player.x == other.x) { // ขึ้น
                    for (Player behind : players) {
                        if (behind != player && behind != other && behind.y == player.y - 2 && behind.x == player.x) {
                            return false; // มีผู้เล่นอีกคนอยู่ข้างหลัง
                        }
                    }
                    if (y < player.y && !horizontalWalls[player.y - 1][player.x] && !horizontalWalls[player.y][player.x]) return true;
                }
            }
        }
     // การเดินทแยง
        if (dx == 1 && dy == 1) {
        	for (Player other : players) {
	// ตรวจสอบว่าามีผู้เล่นอยู่ตรงหน้าและมีกำแพงขวางหลังไหม
        		if (player.x + 1 == other.x && player.y == other.y) { // มีผู้เล่นอยู่ทางขวา
                	if (verticalWalls[player.y][player.x + 2]) { // เช็คว่ามีกำแพงข้างหลังผู้เล่นที่จะข้าม
                    	if (y > player.y && !horizontalWalls[player.y + 1][player.x + 1]) { // เดินทแยง-ลงขวา
                        	return true;
                    	} else if (y < player.y && !horizontalWalls[player.y][player.x + 1]) { // เดินทแยง-ขึ้นขวา
                        	return true;
                    	}
                	}
            	} else if (player.x - 1 == other.x && player.y == other.y) { //มีผู้เล่นอยู่ทางซ้าย
                	if (verticalWalls[player.y][player.x - 1]) {
                    	if (y > player.y && !horizontalWalls[player.y + 1][player.x - 1]) { // เดินทแยง-ลงซ้าย
                        	return true;
                    	} else if (y < player.y && !horizontalWalls[player.y][player.x - 1]) { // เดินทแยง-ขึ้นซ้าย
                        	return true;
                    	}
                	}
            	} else if (player.y + 1 == other.y && player.x == other.x) { // มีผู้เล่นอยู่ข้างล่าง
            	if (horizontalWalls[player.y + 2][player.x]) {
                    	if (x > player.x && !verticalWalls[player.y + 1][player.x + 1]) { // เดินทแยง-ลงขวา
                        	return true;
                    	} else if (x < player.x && !verticalWalls[player.y + 1][player.x]) { // เดินทแยง-ลงซ้าย
                    		return true;
                    	}
                	}
            	} else if (player.y - 1 == other.y && player.x == other.x) { // มีผู้เล่นอยู่ข้างบน
                	if (horizontalWalls[player.y - 1][player.x]) {
                    	if (x > player.x && !verticalWalls[player.y - 1][player.x + 1]) { // เดินทแยง-ขึ้นขวา
                        	return true;
                    	} else if (x < player.x && !verticalWalls[player.y - 1][player.x]) { // เดินทแยง-ขึ้นซ้าย
                    		return true;
                    	}
                	}
            	}
        	}
        }
        return false;  //เดินผิดตำเเหน่ง
    }
    private void switchPlayer() {
    	int currentIndex = Arrays.asList(players).indexOf(currentPlayer);
    	currentPlayer = players[(currentIndex + 1) % players.length];
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
//*************************************************** 4 คน ***********************************************
//          if (player == players[0] && current.y == BOARD_SIZE - 1) return true; // player1 ไปถึงเส้นชัย
//          if (player == players[1] && current.y == 0) return true; // player2 ไปถึงเส้นชัย
//          if (player == players[2] && current.x == BOARD_SIZE - 1) return true; // player3 ไปถึง X = BOARD_SIZE - 1
//          if (player == players[3] && current.x == 0) return true; // player4 ไปถึง X = 0

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
        boolean pathAvailableForBothPlayers = isPathAvailable(players[0]) && isPathAvailable(players[1]);
        //*************************************************** 4 คน ***********************************************
        //boolean pathAvailableForAllPlayers = isPathAvailable(players[0]) && isPathAvailable(players[1]) && isPathAvailable(players[2]) && isPathAvailable(players[3]);
        // Remove the temporarily placed wall
        horizontalWalls[y][x] = false;
        horizontalWalls[y][x+1] = false;
        
        return pathAvailableForBothPlayers;
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
        boolean pathAvailableForBothPlayers = isPathAvailable(players[0]) && isPathAvailable(players[1]);
        //*************************************************** 4 คน ***********************************************
        //boolean pathAvailableForAllPlayers = isPathAvailable(players[0]) && isPathAvailable(players[1]) && isPathAvailable(players[2]) && isPathAvailable(players[3]);

        // Remove the temporarily placed wall
        verticalWalls[y][x] = false;
        verticalWalls[y+1][x] = false;

        return pathAvailableForBothPlayers;
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
