package main;
import javax.swing.JFrame;


// tạo cửa sổ mở lên
public class Screen {
	public static final int WIDTH = 460, HEIGHT = 635; //tạo chiều dài + chiều rộng
	private JFrame screen;
	private PlayScreen playscreen;
	private WaitScreen waitscreen;
	
	public Screen() {
		screen = new JFrame("Tetris Game"); //tên cửa sổ
		screen.setSize(WIDTH,HEIGHT);		//đặt kích thước
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
		screen.setResizable(false);
		screen.setLocationRelativeTo(null);
		
		playscreen = new PlayScreen();
		waitscreen = new WaitScreen(this);
		
		screen.addKeyListener(playscreen);
		screen.addMouseMotionListener(waitscreen);
		screen.addMouseListener(waitscreen);
		screen.add(waitscreen);
		 
		
		
		screen.setVisible(true);
	}
	
	public void startTetris(){
		screen.remove(waitscreen);
		screen.addMouseMotionListener(playscreen);
		screen.addMouseListener(playscreen);
		screen.add(playscreen);
		playscreen.startGame();
		screen.revalidate();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Screen();
	}

}
