package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class WaitScreen extends JPanel implements MouseListener, MouseMotionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mouseX, mouseY;
	private Rectangle playZone;
	private boolean leftClick = false;
	private BufferedImage banner1, banner2, play;
	private Screen screen;
	private Timer timer;
	
	
	public WaitScreen(Screen screen){
		try {
			banner1 = ImageIO.read(PlayScreen.class.getResource("/banner1.png"));
			banner2 = ImageIO.read(PlayScreen.class.getResource("/banner2.png"));
			play = ImageIO.read(PlayScreen.class.getResource("/play1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer = new Timer(1000/60, new ActionListener(){
  
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
			
		});
		timer.start();
		mouseX = 0;
		mouseY = 0;
		
		playZone = new Rectangle(Screen.WIDTH/2 - play.getWidth()/2, Screen.HEIGHT/2 - play.getHeight()/2 - 50  , 200, 133); // tọa độ và kích thước vùng "Play"
		this.screen = screen;
				
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if(leftClick && playZone.contains(mouseX, mouseY))
			screen.startTetris();
			
		g.setColor(Color.white); //Màu background title
		
		g.fillRect(0, 0, Screen.WIDTH, Screen.HEIGHT);
		
		g.drawImage(banner1, Screen.WIDTH/2 - banner1.getWidth()/2, 0, null);
		g.drawImage(banner2, Screen.WIDTH/2 - banner2.getWidth()/2,
				Screen.HEIGHT/2 - banner2.getHeight()/2 + 150, null);
		
		if(playZone.contains(mouseX, mouseY))
			g.drawImage(play, Screen.WIDTH/2 - play.getWidth()/2 + 5, Screen.HEIGHT/2 - play.getHeight()/2 - 45, null);
		else
			g.drawImage(play, Screen.WIDTH/2 - play.getWidth()/2, Screen.HEIGHT/2 - play.getHeight()/2 - 50, null);
		
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
}


