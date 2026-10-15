package whimsybot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import whimsybot.WhimsyBot;

/** Starts the JavaFX GUI for Whimsy Bot. */
public class Main extends Application {
    /** Loads and displays the main window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MainWindow controller = fxmlLoader.getController();
            controller.setWhimsyBot(new WhimsyBot());
            stage.setTitle("Whimsy Bot | Quest Desk");
            stage.setScene(scene);
            stage.setMinWidth(360);
            stage.setMinHeight(480);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}
