package project.five;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Project5 extends Application {

    public static void main(String[] args) {
        launch();
    }

    private int sliderValue = 1;
    private boolean movingForward = true;
    private Scene scene;
    private Circle ball;
    private int timer = 0;
    private double distanceMoved = 0;
    private Button controlButton;
    private boolean paused = true;
    private Timeline movementLine;

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

        controlButton = new Button("Play");
        controlButton.setOnAction(e -> {buttonControls();});

        controlsArea.getChildren().addAll(sliderLabel, slider,  controlButton);

        Pane ballArea = new Pane();
        ballArea.setPrefSize(900, 300);
        ballArea.setStyle("-fx-background-color: #e1e4e8");

        base.setTop(controlsArea);
        base.setCenter(ballArea);

        scene = new Scene(base, 850, 350);
        stage.setScene(scene);
        stage.setTitle("Color Changing Ball");

        ball = new  Circle(50, Color.AQUAMARINE);
        ball.relocate(0, 150);
        ballArea.getChildren().add(ball);

        stage.show();


        movementLine = new Timeline(new KeyFrame(Duration.millis(1),
                ml -> move()));

        movementLine.setCycleCount(Timeline.INDEFINITE);


    }

    private void move(){
        if (ball.getLayoutX() <= 0+(ball.getRadius())) movingForward = true;
        if (ball.getLayoutX() >= scene.getWidth()-(ball.getRadius())) movingForward = false;

        if  (movingForward) ball.setLayoutX(ball.getLayoutX()+sliderValue);
        else ball.setLayoutX(ball.getLayoutX()-sliderValue);

        distanceMoved += sliderValue;

        if (distanceMoved >= 100) {
            changeColor();
            distanceMoved = 0;
        }
    }

    private void changeColor(){
        timer = (timer + 1) % 3;
        switch (timer){
            case 0:
                ball.setFill(Color.RED);
                break;
            case 1:
                ball.setFill(Color.YELLOW);
                break;
            default: ball.setFill(Color.GREEN);
        }
    }

    private void buttonControls(){
        if (paused){
            movementLine.play();
            controlButton.setText("Pause");
            paused = false;
        }
        else{
            movementLine.pause();
            controlButton.setText("Play");
            paused = true;
        }
    }

}
