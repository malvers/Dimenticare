import javafx.application.Application;
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

import java.io.*;
import java.util.ArrayList;

public class VideoPlayerText extends Application {

    private final ArrayList<String> text = new ArrayList();
    private final int song = 0;
    private int lineNumber = 0;
    private boolean playing = true;
    private final Font font = javafx.scene.text.Font.font("Arial", 82);
    private GraphicsContext gc;
    private Canvas canvas;
    private MediaPlayer mediaPlayer;
    private final double myHeight = 600;
    private MediaView mediaView;
    private StackPane root;
    private long runningTime = 0;
    private BufferedWriter karaokeWriter = null;

    public VideoPlayerText() {

        init(song);
    }

    private void init(int song) {

        String theSong = "ThatWay";

        String filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".karaoke";
        File file = new File(filePath);

        if (!file.exists()) {
            filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".txt";
            System.out.println("The song " + theSong + ".karaoke DOES NOT exist!");
            initKaraokeWriter();
        } else {
            System.out.println("The song " + theSong + ".karaoke exists!");
        }

        try {

            FileReader fileReader = new FileReader(filePath);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        // Path to the video file

        String theSong = "ThatWay";

        String pathToFile = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".mp4";

        // Create a Media object
        File theFile = new File(pathToFile);
        Media media = new Media(theFile.toURI().toString());

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

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> runningTime = (long) newValue.toMillis());

        mediaPlayer.play();

        gc.setFill(Color.RED.darker());
        gc.setFont(font);
    }

    private void initKaraokeWriter() {

        try {

            File file = new File("/Users/malvers/IdeaProjects/Dimenticare/ThatWay.karaoke");

            FileWriter fileWriter = new FileWriter(file);

            karaokeWriter = new BufferedWriter(fileWriter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                    try {
                        if (karaokeWriter != null) {
                            karaokeWriter.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.exit(1);
                }
                break;
            case DOWN:
                lineNumber++;
                System.out.println("run: " + runningTime);
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

        if (karaokeWriter == null) {
            return;
        }

        try {
            karaokeWriter.write(runningTime + "-#-" + strText);
            karaokeWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
