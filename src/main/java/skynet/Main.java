package skynet;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * Main entry point for the Skynet GUI.
 */
public class Main extends Application {

    private final Skynet skynet = new Skynet();
    private MediaPlayer backgroundMusic;

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("SkyNET");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSkynet(skynet);
            stage.show();
            playBackgroundMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the optional background music when the runtime supports audio.
     *
     * <p>Some environments, such as WSL without a working audio sink, support
     * JavaFX windows but cannot create a media player. Music should not prevent
     * the rest of the application from launching in those environments.</p>
     */
    private void playBackgroundMusic() {
        URL musicResource = Main.class.getResource("/sounds/terminator_ost.mp3");
        if (musicResource == null) {
            System.err.println("Background music resource not found; continuing without music.");
            return;
        }

        try {
            Media music = new Media(musicResource.toExternalForm());
            MediaPlayer player = new MediaPlayer(music);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setVolume(0.2);
            player.setOnError(() -> System.err.println(
                    "Background music playback failed; continuing without music."));
            player.play();
            backgroundMusic = player;
        } catch (MediaException e) {
            System.err.println("Background music unavailable; continuing without music: "
                    + e.getMessage());
        }
    }

    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
