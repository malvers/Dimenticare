import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;

public class VideoPlayer extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Path to the video file
        String pathToFile = "/Users/malvers/IdeaProjects/Dimenticare/MyWay.mp4";

        // Create a Media object
        Media media = new Media(new File(pathToFile).toURI().toString());

        // Create a MediaPlayer
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Create a MediaView, which displays the video
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a layout pane and add the MediaView to it
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        // Create a Scene
        Scene scene = new Scene(root, 600, 400);

        // Set the title of the Stage
        primaryStage.setTitle("Video Player");

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();

        // Play the video
        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
