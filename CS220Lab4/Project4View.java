import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Project4View {

    public static final int FRAMEHEIGHT = 700;
    public static final int FRAMEWIDTH = 800;

    private JFrame frame;
    private JPanel interactPanel;
    private JPanel ballArea;

    private Ball ball;

    private JButton startButton;
    private JButton pauseResumeButton;
    private JButton resetButton;

    private JLabel shrinkLabel = new JLabel("Shrink Time Seconds:");
    private JTextField shrinkInput = new JTextField();

    private JLabel timeLeftLabel = new JLabel("Time Left: --");
    private JProgressBar timeLeftBar = new JProgressBar();

    private Timer drawTimer;
    private Timer shrinkTimer;

    int xVelocity = 2;
    int yVelocity = 3;

    public Project4View(){
        frame = new JFrame();
        interactPanel = new JPanel();
        ballArea = new JPanel();

        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonHandler());
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.addActionListener(new PauseResumeButtonHandler());
        pauseResumeButton.setVisible(false);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new resetButtonHandler());

        frame.setLayout(null);

        interactPanel.setSize(FRAMEWIDTH, 40);
        interactPanel.setLocation(0, 0);
        interactPanel.add(startButton);
        interactPanel.add(pauseResumeButton);
        interactPanel.add(resetButton);
        interactPanel.add(shrinkLabel);
        interactPanel.add(shrinkInput);
        shrinkInput.setColumns(7);
        interactPanel.add(timeLeftLabel);
        interactPanel.add(timeLeftBar);

        ballArea.setSize(FRAMEWIDTH, FRAMEHEIGHT-interactPanel.getHeight());
        ballArea.setLocation(0, interactPanel.getHeight());
        ballArea.setLayout(null);

        frame.add(interactPanel);
        frame.add(ballArea);
        frame.setSize(FRAMEWIDTH, FRAMEHEIGHT);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ballArea.setBackground(Color.black);

        String ballPath ="CS220Lab4/Resources/pixil-frame-0.png";
        ball = new Ball(ballPath);

        ballArea.add(ball);
        drawTimer = new Timer(10, new drawTimerListener());
    }


    // Helpers
    private boolean bounceX(){
        if (ball.getX() <= 0) return true;
        else return (ball.getX() + ball.getWidth()) >= ballArea.getWidth();
    }
    private boolean bounceY(){
        if (ball.getY() <= 0) return true;
        else return ((ball.getY() + ball.getHeight() + 20) >= ballArea.getHeight());
    }




    // Handlers

    class StartButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int shrinkSeconds;

            try {
                shrinkSeconds = Integer.parseInt(shrinkInput.getText());
                if (shrinkSeconds <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a positive number");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number");
                return;
            }



            try {
                ballArea.add(ball);
                drawTimer.start();
                int shrinkDelay = 50;
                int shrinkSteps = shrinkSeconds * 1000 / shrinkDelay;
                int startDiameter = ball.getWidth();
                double shrinkPerStep = (double) startDiameter / shrinkSteps;

                if (shrinkTimer != null && shrinkTimer.isRunning()) return;

                timeLeftLabel.setText(String.format("Time Left: %d s", shrinkSeconds));
                timeLeftBar.setMaximum(startDiameter);
                timeLeftBar.setMinimum(0);
                shrinkTimer = new Timer(shrinkDelay, new ActionListener() {
                    int steps = 0;

                    public void actionPerformed(ActionEvent e) {
                        steps++;
                        int newSize = (int) Math.max(startDiameter - shrinkPerStep * steps, 0);
                        ball.setSize(newSize, newSize);
                        ball.repaint();

                        double timeLeft = shrinkSeconds - (steps * shrinkDelay /1000.0);
                        timeLeftLabel.setText(String.format("Time Left: %.1f s", Math.max(timeLeft, 0)));
                        timeLeftBar.setValue((int) (shrinkPerStep * steps));

                        if (newSize <= 0) {
                            ballArea.remove(ball);
                            ballArea.repaint();
                            shrinkTimer.stop();
                            drawTimer.stop();
                            timeLeftLabel.setText("Time Left: 0.0 s");
                            pauseResumeButton.setVisible(false);
                            ball.fullReset();
                        }
                    }
                });
                shrinkTimer.start();
                startButton.setVisible(false);
                pauseResumeButton.setVisible(true);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        }
    }

    class PauseResumeButtonHandler implements ActionListener {
        boolean paused = false;
        public void actionPerformed(ActionEvent e) {
            if (shrinkTimer.isRunning()){
                drawTimer.stop();
                pauseResumeButton.setText("Resume");
                shrinkTimer.stop();
                paused = true;
            }else if (!shrinkTimer.isRunning()) {
                drawTimer.start();
                pauseResumeButton.setText("Pause");
                shrinkTimer.start();
                paused = false;
            }
        }
    }

    class resetButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            if (drawTimer.isRunning()) drawTimer.stop();
            if (shrinkTimer != null && shrinkTimer.isRunning()) shrinkTimer.stop();

            ball.fullReset();
            ballArea.repaint();
            ballArea.add(ball);
            timeLeftLabel.setText("Time Left: --");
            timeLeftBar.setValue(0);
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setVisible(false);
            startButton.setVisible(true);
        }
    }

    class drawTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            ball.moveBall(xVelocity, yVelocity);
            ballArea.paintImmediately(ballArea.getVisibleRect());

            if (bounceX()) xVelocity = -xVelocity;
            if (bounceY()) yVelocity = -yVelocity;

        }
    }
	}