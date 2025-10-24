import java.awt.*;
import java.util.Random;
import javax.swing.*;

class Ball extends JComponent{
		
	private int x, y;
	private int diameter = 229;
	private Image ballImage;
	/**
	 * Constructor for the Ball class
	 */
	public Ball(String imagePath){
		x = 100;
		y = 100;
		ballImage = new ImageIcon(imagePath).getImage();
		setLocation(x,y);
		setSize(diameter, diameter);
		setOpaque(false);
}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(ballImage, 0, 0, getWidth(), getHeight(), this);

	}
	
	public void bounceBall(int dx, int dy){
		x += dx;
		y += dy;
		super.setLocation(x, y);
	}
	
	/**
	 * Method to move the ship to a random location on the screen
	 */
	public void moveBall(){
		Random rand = new Random();
		
		x = rand.nextInt(View.FRAMEWIDTH - this.getWidth());
		y = rand.nextInt(View.FRAMEHEIGHT - this.getHeight());
		setLocation(x, y);
	}
	public void resetSizeAndPosition() {
		setSize(diameter, diameter);
		setLocation(100, 100);
		repaint();
	}
}