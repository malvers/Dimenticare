import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class VideoPlayerText extends Application {

    private ArrayList<String> text = new ArrayList();
    private int lineNumber = 0;
    private boolean playing = true;

    public VideoPlayerText() {

        String filePath = "/Users/malvers/IdeaProjects/Dimenticare/Dimenticare.txt";

        try {
            // Create a FileReader object to read the file
            FileReader fileReader = new FileReader(filePath);

            // Wrap the FileReader in a BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read each line from the file until reaching the end of the file
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.add(line);
            }

            // Close the BufferedReader
            bufferedReader.close();
        } catch (IOException e) {
            // Handle exceptions, such as file not found or unable to read
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        // Path to the video file
        String pathToFile = "/Users/malvers/IdeaProjects/Dimenticare/Dimenticarti è poco.mp4";

        // Create a Media object
        Media media = new Media(new File(pathToFile).toURI().toString());

        // Create a MediaPlayer
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Create a MediaView, which displays the video
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a Canvas for drawing
        int w = media.getWidth();
        int h = media.getHeight();

        System.out.println("w: " + w + " h: " + h);
        Canvas canvas = new Canvas(1800, 600);

        double scaleX = 1.32; // Scale factor for X-axis
        double scaleY = 1.31; // Scale factor for Y-axis
        mediaView.setScaleX(scaleX);
        mediaView.setScaleY(scaleY);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().addAll(mediaView, canvas);

        Scene scene = new Scene(root, 1800, 600);

        scene.setOnKeyPressed(event -> {

            switch (event.getCode()) {
                case Q:
                case W:
                    if (event.isMetaDown()) {
                        System.exit(1);
                    }
                    break;
                case DOWN:
                    lineNumber++;
                    gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
                    gc.fillText(text.get(lineNumber), 200, 400);
                    break;
                case UP:
                    lineNumber--;
                    gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
                    gc.fillText(text.get(lineNumber), 200, 400);
                    break;
                case SPACE:
                    if (playing) {
                        mediaPlayer.pause();
                        playing = false;
                    } else {
                        mediaPlayer.play();
                        playing = true;
                    }
                    break;
            }
        });

        // Set the title of the Stage
        primaryStage.setTitle("Video Player with Drawing");

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();

        // Play the video
        mediaPlayer.play();

        gc.setFill(Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 36)); // Font size set to 36
    }

    public static void main(String[] args) {
        launch(args);
    }
}
