package duke;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX application for the geen chatbot.
 */
public class Main extends Application {
    /**
     * Loads the main window layout and shows the chatbot GUI.
     *
     * @param stage Main JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            stage.setTitle("geen");
            stage.setMinWidth(420);
            stage.setMinHeight(360);
            stage.setScene(new Scene(root));
            stage.show();
            loader.<MainWindow>getController().focusInput();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the chatbot window.", e);
        }
    }
}
