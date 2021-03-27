package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;



public class PlayScreen extends JPanel implements KeyListener, MouseListener, MouseMotionListener{
	
	private static final long serialVersionUID = 1L;

	private Clip music;
	
	private BufferedImage blocks, background, pause, refresh,nextShapeImg,scoreImg;
		
	private final int boardHeight = 20, boardWidth = 10;
	
	// block size
	
	private final int blockSize = 30;

	private int[][] board = new int[boardHeight][boardWidth];
	
	private Shape[] shapes = new Shape[7];
	
	// currentShape
	
	private static Shape currentShape, nextShape;
	
	// game loop
	
	private Timer looper;
	
	private int FPS = 60;
	
	private int delay = 1000/FPS;
	
	// mouse events variables
	
	private int mouseX, mouseY;
	
	private boolean leftClick = false;
	
	private Rectangle stopZone, refreshZone;
	
	private boolean gamePaused = false;
	
	private boolean gameOver = false;
	
	// buttons press lapse
	
	private Timer buttonLapse = new Timer(300, new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			buttonLapse.stop();
		}});
	
	// score
	
	private int score = 0;
	
	
	public PlayScreen(){
		
		blocks = LoadImg.loadImage("/tiles.png");
		background = LoadImg.loadImage("/background2.jpg");		
		nextShapeImg = LoadImg.loadImage("/nextShape.png");
		scoreImg = LoadImg.loadImage("/score.png");
		music = LoadImg.LoadSound("/bgmusic2.wav");
		
		pause = LoadImg.loadImage("/pause1.png");
		refresh = LoadImg.loadImage("/refresh1.png");
		
		music.loop(Clip.LOOP_CONTINUOUSLY);
		
		
		
		mouseX = 0;
		mouseY = 0;
		
		stopZone = new Rectangle(350, 500, pause.getWidth(), pause.getHeight() + pause.getHeight()/2); // tọa độ nút stop
		refreshZone = new Rectangle(350, 500 - refresh.getHeight() - 20,refresh.getWidth(),
				refresh.getHeight() + refresh.getHeight()/2); // tọa độ nút refresh
		
		
		looper = new Timer(delay, new GameLooper());
		
		// create shapes
		
		//thiết lập hình dạng 
				shapes[0] = new Shape(blocks.getSubimage(0, 0, blockSize, blockSize), new int[][] {
					{1,1,1,1} //thanh ngang
				},this, 1); 
				
				shapes[1] = new Shape(blocks.getSubimage(blockSize*1, 0, blockSize, blockSize), new int[][] {
					{1,1,0},
					{0,1,1} //Z xuôi
				},this, 2); 
				
				shapes[2] = new Shape(blocks.getSubimage(blockSize*2, 0, blockSize, blockSize), new int[][] {
					{0,1,1},
					{1,1,0} //Z ngược
				},this, 3); 
				
				shapes[3] = new Shape(blocks.getSubimage(blockSize*3, 0, blockSize, blockSize), new int[][] {
					{1,1,1},
					{0,1,0} //T
				},this, 4); 
				
				shapes[4] = new Shape(blocks.getSubimage(blockSize*4, 0, blockSize, blockSize), new int[][] {
					{1,1,1},
					{0,0,1}  //L1
				},this, 5); 
				
				shapes[5] = new Shape(blocks.getSubimage(blockSize*5, 0, blockSize, blockSize), new int[][] {
					{1,1,1},
					{1,0,0} //L2
				},this, 6);
				
				shapes[6] = new Shape(blocks.getSubimage(blockSize*6, 0, blockSize, blockSize), new int[][] {
					{1,1},
					{1,1} // ô vuông
				},this, 7);
		
		
	}
	
	private void update(){	
		if(stopZone.contains(mouseX, mouseY) && leftClick && !buttonLapse.isRunning() && !gameOver)
		{
			buttonLapse.start();
			gamePaused = !gamePaused;
		}
	
		if(refreshZone.contains(mouseX, mouseY) && leftClick)
			startGame();
		
		if(gamePaused || gameOver)
//		if(gameOver)
		{
			return;
		}
		currentShape.update();
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.drawImage(background, 0, 0, null);
		for(int row = 0; row < board.length; row++)
		{
			for(int col = 0; col < board[row].length; col ++)
			{
				
				if(board[row][col] != 0)
				{
					
					g.drawImage(blocks.getSubimage((board[row][col] - 1)*blockSize,
							0, blockSize, blockSize), col*blockSize, row*blockSize, null);
				}				
					
			}
		}
		
		for(int row = 0; row < nextShape.getCoords().length; row ++)
		{
			for(int col = 0; col < nextShape.getCoords()[0].length; col ++)
			{
				if(nextShape.getCoords()[row][col] != 0)
				{
					g.drawImage(nextShape.getBlock(), col*30 + 320, row*30 + 50, null);	
				}
			}		
		}
		currentShape.render(g);
		
		if(stopZone.contains(mouseX, mouseY))
			g.drawImage(pause.getScaledInstance(pause.getWidth() + 3, pause.getHeight() + 3, BufferedImage.SCALE_DEFAULT)
					, stopZone.x + 3, stopZone.y + 3, null);
		else
			g.drawImage(pause, stopZone.x, stopZone.y, null);
		
		if(refreshZone.contains(mouseX, mouseY))
			g.drawImage(refresh.getScaledInstance(refresh.getWidth() + 3, refresh.getHeight() + 3,
					BufferedImage.SCALE_DEFAULT), refreshZone.x + 3, refreshZone.y + 3, null);
		else
			g.drawImage(refresh, refreshZone.x, refreshZone.y, null);
		
		
		if(gamePaused)
		{
			String gamePausedString = "GAME PAUSED";
			g.setColor(Color.WHITE);
			g.setFont(new Font("Georgia", Font.BOLD, 30));
			g.drawString(gamePausedString, 35, Screen.HEIGHT/2);

		}
		if(gameOver)
		{
			String gameOverString = "GAME OVER";
			g.setColor(Color.WHITE);
			g.setFont(new Font("Georgia", Font.BOLD, 30));
			g.drawString(gameOverString, 50, Screen.HEIGHT/2);
		}	
		g.setColor(Color.BLACK);
		g.setFont(new Font("Georgia", Font.BOLD, 20));
//		g.drawString("NEXT SHAPE", Window.WIDTH - 125, Window.HEIGHT - 600);
//		g.drawString("SCORE", Window.WIDTH - 125, Window.HEIGHT/2);	
		g.drawImage(nextShapeImg, Screen.WIDTH - 145, Screen.HEIGHT - 640, null);
		g.drawImage(scoreImg, Screen.WIDTH - 150, Screen.HEIGHT/2 - 30, null);
		g.drawString(score+"", Screen.WIDTH - 90, Screen.HEIGHT/2 + 30);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(new Color(0, 0, 0, 100));
		
//		for(int i = 0; i <= boardHeight; i++)
//		{
//			g2d.drawLine(0, i*blockSize, boardWidth*blockSize, i*blockSize);
//		}
//		for(int j = 0; j <= boardWidth; j++)
//		{
//			g2d.drawLine(j*blockSize, 0, j*blockSize, boardHeight*30);
//		}
		g2d.drawLine(blockSize*10, 0, 10*blockSize, boardHeight*30);
	}
	
	public void setNextShape(){
		int index = (int)(Math.random()*shapes.length);
		nextShape = new Shape(shapes[index].getBlock(), shapes[index].getCoords(),this,shapes[index].getColor() );
	}
	
	public void setCurrentShape(){
		currentShape = nextShape;
		setNextShape();
		
		for(int row = 0; row < currentShape.getCoords().length; row ++)
		{
			for(int col = 0; col < currentShape.getCoords()[0].length; col ++)
			{
				if(currentShape.getCoords()[row][col] != 0)
				{
					if(board[currentShape.getY() + row][currentShape.getX() + col] != 0)
						gameOver = true;
				}
			}		
		}
		
	}
	
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			currentShape.setDeltaX(-1);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			currentShape.setDeltaX(1);
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			currentShape.speedDown(); 
		if(e.getKeyCode() == KeyEvent.VK_UP)
			currentShape.rotate(); 
		if(e.getKeyCode() == KeyEvent.VK_X) // xoa 5 dong
			currentShape.rotate(); 
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			currentShape.normalSpeed(); 
	}

	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void startGame(){
		stopGame();
		setNextShape();
		setCurrentShape();
		gameOver = false;
		looper.start();
		
	}
	public void stopGame(){
		score = 0;
		
		for(int row = 0; row < board.length; row++)
		{
			for(int col = 0; col < board[row].length; col ++)
			{
				board[row][col] = 0;
			}
		}
		looper.stop();
	}
	
	
	class GameLooper implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			update();
			repaint();
		}
		
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftClick = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftClick = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void addScore(){
		score += 5;
	}
	//hàm lấy kích thước khối
	public int getBlockSize() {
		return blockSize;
	}

	
	public int [][] getBoard(){
		return board;
	}
	
}
