import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Ball object creation for the Swing Timer animation
 * and method setup.
 *
 * @author Brian Mann, Michael, Aiden
 * @version 1.0
 */

public class Ball extends JComponent {
    private int x, y;
    private int diameter = 229;
    private Image ballImg;

    /**
     * Constructor for the Ball class with a default position and size
     * loads its visual representation from the specified image path
     *
     * @param imagePath the file path to the image used to represent the ball
     */

    public Ball(String imagePath){
        ballImg = new ImageIcon(imagePath).getImage();
        setRandomPosition();
        setSize(diameter,diameter);
        setOpaque(false);
    }

    /**
     * Paints the ball image
     * <p>
     * This method overrides to render the ball image
     * scaled to fit the current width and height of the component.
     * It uses {@code Graphics2D} for enhanced rendering capabilities.
     * </p>
     *
     * @param g the {@code Graphics} context used for painting
     */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(ballImg,0,0, getWidth(), getHeight(), this);
    }

    /**
     * Moves the ball with specified horizontal and vertical velocity values.
     *
     * @param xVel the horizontal velocity to apply to the ball's x-coordinate
     * @param yVel the vertical velocity to apply to the ball's y-coordinate
     */

    public void moveBall(int xVel, int yVel){
        x += xVel;
        y += yVel;
        super.setLocation(x,y);
    }

    /**
     * Sets the ball's position to a random location within the bounds of the frame.
     */

    public void setRandomPosition(){
        Random rand = new Random();

        x = rand.nextInt(Project4View.FRAMEWIDTH - diameter);
        y = rand.nextInt(Project4View.FRAMEHEIGHT - (diameter + 50));
        setLocation(x,y);
    }

    /**
     * Resets the ball's size and position to a new random location within the frame.
     * <p>
     * This method restores the ball to its default diameter, assigns it a random position
     * and repaints to update its visual state.
     * </p>
     */

    public void fullReset(){
        setSize(diameter,diameter);
        setRandomPosition();
        repaint();

    }
}
