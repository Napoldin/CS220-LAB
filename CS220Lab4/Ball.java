import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Ball extends JComponent {
    private int x, y;
    private int diameter = 229;
    private Image ballImg;

    public Ball(String imagePath){
        ballImg = new ImageIcon(imagePath).getImage();
        setRandomPosition();
        setSize(diameter,diameter);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(ballImg,0,0, getWidth(), getHeight(), this);
    }

    public void moveBall(int xVel, int yVel){
        x += xVel;
        y += yVel;
        super.setLocation(x,y);
    }

    public void setRandomPosition(){
        Random rand = new Random();

        x = rand.nextInt(Project4View.FRAMEWIDTH - diameter);
        y = rand.nextInt(Project4View.FRAMEHEIGHT - (diameter + 50));
        setLocation(x,y);
    }

    public void fullReset(){
        setSize(diameter,diameter);
        setRandomPosition();
        repaint();

    }
}
