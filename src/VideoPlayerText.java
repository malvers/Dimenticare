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

    private final ArrayList<String> lyrics = new ArrayList();
    private int lineNumber = 0;
    private boolean playing = false;
    private final Font font = javafx.scene.text.Font.font("Arial", 82);
    private GraphicsContext gc;
    private Canvas canvas;
    private MediaPlayer mediaPlayer;
    private final double myHeight = 1200;
    private MediaView mediaView;
    private StackPane root;
    private long runTime = 0;
    private BufferedWriter karaokeWriter = null;
    private boolean recordingPossible = false;
    private Stage primaryStage;
    private String theSong = "ThatWay";

    public VideoPlayerText() {

        theSong = "Dimeticarti";
        initLyricsOrKaraoke();
    }

    private void initLyricsOrKaraoke() {

        String filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".karaoke";
        File file = new File(filePath);

        /// Karaoke file does not exist
        if (!file.exists()) {

            System.out.println("The karaoke file for the file " + file + " DOES NOT exist!");
            recordingPossible = true;

            /// create karaoke file
            createKaraokeFile(filePath);

            /// read the lyrics file
            filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".lyrics";
            readLyricsOrKaraokeFile(filePath);

        } else {
            /// read the karaoke file
            filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".karaoke";
            System.out.println("The file " + filePath + " exists!");
            readLyricsOrKaraokeFile(filePath);
        }
    }

    private void readLyricsOrKaraokeFile(String filePath) {

        try {

            System.out.println("readLyricsOrKaraokeFile - filePath: " + filePath);
            FileReader fileReader = new FileReader(filePath);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            System.out.println("buffered: " + bufferedReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("line: " + line);
                lyrics.add(line);
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStageIn) {

        primaryStage = primaryStageIn;
        // Path to the video file

        String pathToFile = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".mp4";

        // Create a Media object
        File theFile = new File(pathToFile);
        Media media = new Media(theFile.toURI().toString());

        // Create a MediaPlayer
        mediaPlayer = new MediaPlayer(media);

        // Create a MediaView, which displays the video
        mediaView = new MediaView(mediaPlayer);

        // Create a Canvas for drawing
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

        primaryStage.setTitle("Karaoke recorder and player ... press space to start");

        primaryStage.setScene(scene);

        primaryStage.show();

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            handlePlayKaraoke(newValue);
        });

        gc.setFill(Color.RED.darker());
        gc.setFont(font);
    }

    private void handlePlayKaraoke(Duration newValue) {

        runTime = (long) newValue.toMillis();

        if (recordingPossible) {
            primaryStage.setTitle("RECORDING - runtime: " + runTime + " ms");
            return;
        }
        primaryStage.setTitle("KARAOKE - runtime: " + runTime + " ms");

        if (lineNumber >= lyrics.size()) {
            return;
        }

        String theText = lyrics.get(lineNumber);
        long timeStamp = Long.parseLong(theText.substring(0, theText.indexOf("-#-")));
        System.out.println(lineNumber + " run: " + runTime + " time stamp: " + timeStamp + " - Text: " + theText);

        if (runTime >= timeStamp) {
            setText();
            lineNumber++;
        }
    }

    private void createKaraokeFile(String theSong) {

        String filePath = "/Users/malvers/IdeaProjects/Dimenticare/" + theSong + ".karaoke";

        try {

            File file = new File(theSong);

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
                if (recordingPossible) {
                    setText();
                    lineNumber++;
                }
                break;
            case UP:
                if (recordingPossible) {
                    lineNumber--;
                    setText();
                }
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

        Text textNode;
        double textWidth;
        double xPos;
        String strText = lyrics.get(lineNumber);
        if (!recordingPossible) {
            strText = strText.substring(strText.indexOf("-#-") + 3);
        }
        textNode = new Text(strText);
        textNode.setFont(font);
        textWidth = textNode.getLayoutBounds().getWidth();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        xPos = (canvas.getWidth() - textWidth) / 2.0;
        gc.fillText(strText, xPos, myHeight - (myHeight/4));

        if (karaokeWriter == null) {
            return;
        }

        try {
            karaokeWriter.write(runTime + "-#-" + strText);
            karaokeWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
