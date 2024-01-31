import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class VideoPlayerText extends Application {

    private final ArrayList<String> text = new ArrayList();
    private int lineNumber = 0;
    private boolean playing = true;
    private final Font font = javafx.scene.text.Font.font("Arial", 82);
    private GraphicsContext gc;
    private Canvas canvas;
    private MediaPlayer mediaPlayer;
    private final double myHeight = 600;
    private MediaView mediaView;
    private StackPane root;

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
        mediaPlayer = new MediaPlayer(media);

        // Create a MediaView, which displays the video
        mediaView = new MediaView(mediaPlayer);

        // Create a Canvas for drawing
        int w = media.getWidth();
        int h = media.getHeight();

        System.out.println("w: " + w + " h: " + h);
        canvas = new Canvas(1800, myHeight);

        double scaleX = 1.32; // Scale factor for X-axis
        double scaleY = 1.31; // Scale factor for Y-axis
        mediaView.setScaleX(scaleX);
        mediaView.setScaleY(scaleY);

        gc = canvas.getGraphicsContext2D();

        root = new StackPane();
        root.getChildren().addAll(mediaView, canvas);

        Scene scene = new Scene(root, 1800, myHeight);

        scene.setOnKeyPressed(this::handleKeys);

        // Set the title of the Stage
        primaryStage.setTitle("Video Player with lyrics.");

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> System.out.println(formatDuration(newValue)));

        mediaPlayer.play();

        gc.setFill(Color.RED.darker());
        gc.setFont(font);
    }

    private String formatDuration(Duration duration) {

        int hours = (int) duration.toHours();
        int minutes = (int) (duration.toMinutes() % 60);
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void handleKeys(KeyEvent event) {

        switch (event.getCode()) {
            case Q:
            case W:
                if (event.isMetaDown()) {
                    System.exit(1);
                }
                break;
            case DOWN:
                lineNumber++;
                setText();
                break;
            case UP:
                lineNumber--;
                setText();
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
            case T:
                mediaPlayer.seek(Duration.seconds(20));
                break;
            case Z:
                /// TODO: check
                mediaView.setScaleX(0.5); // Zoom in horizontally
                mediaView.setScaleY(0.5); // Zoom in vertically
                root = new StackPane();
                root.getChildren().addAll(mediaView, canvas);
                break;

        }
    }

    private void setText() {

        String strText;
        Text textNode;
        double textWidth;
        double xPos;
        strText = text.get(lineNumber);
        textNode = new Text(strText);
        textNode.setFont(font);
        textWidth = textNode.getLayoutBounds().getWidth();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        xPos = (canvas.getWidth() - textWidth) / 2.0;
        gc.fillText(strText, xPos, myHeight - 360);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
