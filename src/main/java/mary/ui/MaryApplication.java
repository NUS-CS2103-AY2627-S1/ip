package mary.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Creates MARY's desktop window and lets JavaFX manage its lifecycle.
 */
public class MaryApplication extends Application {
    /**
     * Loads the shared task file and displays a resizable chat window.
     *
     * @param stage primary application window.
     */
    @Override
    public void start(Stage stage) {
        ChatWindow chat = new ChatWindow("mary-data.txt");
        stage.setTitle(Personality.NAME + " | " + Personality.TAGLINE);
        stage.setScene(new Scene(chat, 760, 780));
        stage.setMinWidth(480);
        stage.setMinHeight(600);
        stage.show();
        Platform.runLater(chat::focusInput);
    }
}
