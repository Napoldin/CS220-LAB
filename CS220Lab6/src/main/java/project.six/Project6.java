package project.six;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Project6 extends Application {

    public static void main(String[] args) {
        launch();
    }

    private int sliderValue = 1;
    private boolean movingForward = true;
    private boolean movingDown = true;
    private boolean increasingSize = true;
    private Scene scene;
    private Circle ball;
    private Button controlButton;
    private boolean paused = true;
    private Timeline movementLine;
    private Timeline sizingLine;
    private Pane ballArea;
    private int ballSize = 25;
    private int minBallSize;
    private int maxBallSize;
    private TextField minField;
    private TextField maxField;

    /**
     * Our Start Function for javaFX. Sets up our stage and initializes values such as our Ball and our Timelines
     *
     * @param stage the primary stage for this application
     * @throws Exception if any JavaFX component fails to initialize
     */
    public void start(Stage stage) throws Exception {
        BorderPane base = new BorderPane();

        HBox controlsArea = new HBox(10);
        controlsArea.setPadding(new Insets(10));
        controlsArea.setAlignment(Pos.CENTER_LEFT);
        controlsArea.setStyle("-fx-background-color: #c1c3c7");

        Text sliderLabel = new Text("Speed Slider:");
        Slider slider = new Slider(1, 4,0.2);
        slider.valueProperty().addListener((_, _, newValue) -> {
            sliderValue = newValue.intValue();
        });
        Text minLabel = new Text("Minimum ball Size:");
        Text maxLabel = new Text("Maximum ball Size:");
        minField = new TextField();
        maxField = new TextField();
        minField.setPrefSize(30, 10);
        maxField.setPrefSize(30, 10);

        controlButton = new Button("Play");
        controlButton.setOnAction(e -> {buttonControls();});

        controlsArea.getChildren().addAll(sliderLabel, slider, minLabel, minField, maxLabel, maxField,  controlButton);

        ballArea = new Pane();
        ballArea.setPrefSize(900, 600);
        ballArea.setStyle("-fx-background-color: #e1e4e8");

        base.setTop(controlsArea);
        base.setCenter(ballArea);

        scene = new Scene(base, 850, 650);
        stage.setScene(scene);
        stage.setTitle("Dynamic Size Ball");

        ball = new Circle(ballSize, Color.DARKSEAGREEN);
        ball.relocate(0, 0);
        ballArea.getChildren().add(ball);

        stage.show();

        movementLine = new Timeline(new KeyFrame(Duration.millis(1),
                ml -> move()));
        sizingLine = new Timeline(new KeyFrame(Duration.millis(100),
                sl -> changeSize(minBallSize, maxBallSize)));

        movementLine.setCycleCount(Timeline.INDEFINITE);
        sizingLine.setCycleCount(Timeline.INDEFINITE);

    }

    /**
     * Function to move our Ball.
     * Reverses Direction when ball hits a border
     */
    private void move(){
        if (ball.getLayoutX() <= 0+(ball.getRadius())) movingForward = true;
        if (ball.getLayoutX() >= scene.getWidth()-ball.getRadius()) movingForward = false;
        if (ball.getLayoutY() <= 0+(ball.getRadius())) movingDown = true;
        if (ball.getLayoutY() >= ballArea.getHeight()-(ball.getRadius())) movingDown = false;

        if (movingForward) ball.setLayoutX(ball.getLayoutX()+sliderValue);
        else ball.setLayoutX(ball.getLayoutX()-sliderValue);
        if (movingDown) ball.setLayoutY(ball.getLayoutY()+sliderValue);
        else ball.setLayoutY(ball.getLayoutY()-sliderValue);
    }

    /**
     * Function to change the size of our ball.
     * flips a switch to increasing or decreasing if at min/max Size
     * Increments the ball size between minSize and maxSize.
     *
     * @param minSize Minimum size of the ball it will shrink to
     * @param maxSize Maximum size of the ball it will grow to
     */
    private void changeSize(int minSize, int maxSize){

        if (ballSize <= minSize) increasingSize = true;
        else if (ballSize >= maxSize) increasingSize = false;
        if (increasingSize) ballSize++;
        else ballSize--;
        ball.setRadius(ballSize);
        }

    /**
     * Function to Control our Pause/Resume Button
     * Sets Min and Max Ball size and handles invalid entries
     * Starts and pauses our Timelines with the button
     */
    private void buttonControls(){
        if (paused){
            try{
                minBallSize = Integer.parseInt(minField.getText());
                maxBallSize = Integer.parseInt(maxField.getText());

                if (minBallSize > maxBallSize){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Min must be Greater than Max");
                    alert.show();
                    return;
                }

                if (minBallSize <0 || ballSize <0){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Both Numbers must be above 0!");
                    alert.show();
                    return;
                }

            } catch(NumberFormatException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid number");
                alert.show();
                return;
            }

            movementLine.play();
            sizingLine.play();
            controlButton.setText("Pause");
            paused = false;
        }
        else{
            movementLine.pause();
            sizingLine.pause();
            controlButton.setText("Play");
            paused = true;
        }
    }
    }



