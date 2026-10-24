package orbit.gui;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import orbit.ChatSession;

/**
 * Loads Orbit's graphical interface and connects it to a persistent chat session.
 */
public class Main extends Application {
    /**
     * Creates the application instance that JavaFX launches.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow controller = loader.getController();
        controller.setSession(new ChatSession(Paths.get("data", "orbit.txt")), this::exitAfterGoodbye);

        stage.setTitle("Orbit | Task assistant");
        stage.setMinWidth(430);
        stage.setMinHeight(520);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void exitAfterGoodbye() {
        PauseTransition pause = new PauseTransition(Duration.millis(650));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
