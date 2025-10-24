import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/*
 * Graphical User Interface for the SpaceShip example
 * creates and loads the frame using the Ship class
 *
 * @author Chase
 * @version 1.0
 */


public class View {
	
	//constants to represent the frame width and height
	//notice that they are public
	public static final int FRAMEHEIGHT = 700;
	public static final int FRAMEWIDTH = 700;
//	private static final int STEPS = 500;
//	private static final int DELAY = 20;
	
	//graphical objects declared at class level
	private JFrame frame;
	private JPanel actionPanel;
	private JPanel playArea;
		
	//a single spaceship for use in the game 
	private Ball ball;
//	private ImageIcon ballImage;
	
	//buttons for starting and stopping the timer
	private JButton startButton;
	private JButton pauseResumeButton;
	private JButton resetButton;

	private JLabel shrinkLabel = new JLabel("Shrink Time Seconds:");
	private JTextField input = new JTextField();

	private JLabel timeLeftLabel = new JLabel("Time Left: --");

	private Timer ballTimer;
	private Timer shrinkTimer;
		
	int dx = 2;
	int dy = 3;

	
	/**
	 * Constructor for the SpaceShip class loads and displays the frame
	 */
	public View(){
		frame = new JFrame();
		actionPanel = new JPanel();
		playArea = new JPanel();
		startButton = new JButton("start");
		startButton.addActionListener(new startButtonHandler());
		pauseResumeButton = new JButton("pause");
		pauseResumeButton.addActionListener(new pauseResumeButtonHandler());
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new resetButtonHandler());

		
		frame.setLayout( null);
		
		actionPanel.setSize(FRAMEWIDTH, 40);
		actionPanel.setLocation(0, 0);
		actionPanel.add(startButton);
		actionPanel.add(pauseResumeButton);
		actionPanel.add(resetButton);
		actionPanel.add(input);
		actionPanel.add(shrinkLabel);
		input.setColumns(7); // sets entry width
		actionPanel.add(timeLeftLabel);

		playArea.setSize(FRAMEWIDTH, FRAMEHEIGHT - actionPanel.getHeight());
		playArea.setLocation(0, actionPanel.getHeight());
		playArea.setLayout(null);
		
		frame.add(actionPanel);
		frame.add(playArea);
		frame.setSize(FRAMEWIDTH, FRAMEHEIGHT);
		frame.setResizable(false);
		frame.setVisible(true); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		playArea.setBackground(Color.black);

		String ballImage = ("C:\\Users\\brian_9sbw6de\\Documents\\Fall 2025\\CS 220\\lab4\\src\\Resources\\pixil-frame-0.png");
		ball = new Ball(ballImage);

		playArea.add(ball);
		ballTimer = new Timer(10, new TimerListener());
	}
	
		/**
		 * This is a private helper method to determine if the ship needs to reverse course in the
		 * X dimension
		 *
		 * @return boolean representing whether or not the ship needs to bounce
		 */
		private boolean bounceX(){
			if (ball.getX() <= 0) {
				return true;
			}
			else if ((ball.getX() + ball.getWidth()) >= playArea.getWidth()) {
				return true;
			}
			return false;
		}
		
		/**
		 * This is a private helper method to determine if the ship needs to reverse course in the
		 * Y dimension
		 *
		 * @return boolean representing whether or not the ship needs to bounce
		 */
		private boolean bounceY(){
			if (ball.getY() <= 0) {
				return true;
			}
			else if ((ball.getY() + ball.getHeight() + 20) >= playArea.getHeight()) {
				return true;
			}
			return false;
		}
		
			
	/**
	 * private inner class startButtonHandler provides the action listener for the start button
	 */

	private class startButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ballTimer.start();
			try {
				int shrinkSeconds = Integer.parseInt(input.getText());
				if (shrinkSeconds <= 0) {
					JOptionPane.showMessageDialog(frame, "Please enter a positive number.");
					return;
				}

				int shrinkDelay = 50; // ms between shrink steps
				int totalSteps = shrinkSeconds * 1000 / shrinkDelay;
				int startDiameter = ball.getWidth();
				double shrinkAmountPerStep = (double) startDiameter / totalSteps;

				if (shrinkTimer != null && shrinkTimer.isRunning()) {
					shrinkTimer.stop();
				}

				timeLeftLabel.setText(String.format("Time Left: %d s", shrinkSeconds));
				shrinkTimer = new Timer(shrinkDelay, new ActionListener() {
					int steps = 0;

					public void actionPerformed(ActionEvent evt) {
						steps++;
						int newSize = (int) Math.max(startDiameter - shrinkAmountPerStep * steps, 0);
						ball.setSize(newSize, newSize);
						ball.repaint();

						// Update countdown display
						double timeLeft = shrinkSeconds - (steps * shrinkDelay / 1000.0);
						timeLeftLabel.setText(String.format("Time Left: %.1f s", Math.max(timeLeft, 0)));

						// Once fully shrunk, stop timer and remove ball
						if (newSize <= 0) {
							playArea.remove(ball);
							playArea.repaint();
							shrinkTimer.stop();
							timeLeftLabel.setText("Time Left: 0.0 s");
						}
					}
				});
				shrinkTimer.start();

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
			}
		}
	}

	/**
	 * private inner class stopButtonHandler provides the action listener for the stop button
	 */
	private class pauseResumeButtonHandler implements ActionListener
	{
	    /**
	     * This method stops the timer 
	     * 
	     * @param e the action event handled by this method
	     */
	    public void actionPerformed(ActionEvent e)
	    {
			if (ballTimer.isRunning()) {
				ballTimer.stop();
				pauseResumeButton.setText("Resume");
				shrinkTimer.stop();
			} else {
				ballTimer.start();
				pauseResumeButton.setText("Pause");
				shrinkTimer.start();
			}

		}
	}

		private class resetButtonHandler implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				// Stop any running timers
				if (ballTimer.isRunning()) {
					ballTimer.stop();
				}
				if (shrinkTimer != null && shrinkTimer.isRunning()) {
					shrinkTimer.stop();
				}

				// Reset ball size and position
				ball.resetSizeAndPosition();

				// Ensure the ball is visible again
				if (ball.getParent() == null) {
					playArea.add(ball);
				}
				playArea.repaint();

				// Reset label text
				timeLeftLabel.setText("Time Left: --");
				pauseResumeButton.setText("Pause");  // restore default button text
			}
		}

		/**
		 * private inner class TimerListener provides the action listener for the swing Timer
		 */
		private class TimerListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				ball.bounceBall(dx, dy);
				playArea.paintImmediately(playArea.getVisibleRect());
						
				//sleep the thread to slow down the movement
				if (bounceX()) {
					dx = dx * -1;
				}
				if (bounceY()) {
					dy = dy * -1;
				}
			}
		}
	}