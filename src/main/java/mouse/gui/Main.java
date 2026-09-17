package mouse.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mouse.Mouse;

/**
 * JavaFX application that shows the Mouse chatbot GUI.
 */
public class Main extends Application {
    private final Mouse mouse = new Mouse();

    /**
     * Creates the JavaFX application with a default Mouse session.
     */
    public Main() {
    }

    /**
     * Loads the main window and shows the stage.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Mouse");
            stage.setResizable(true);
            stage.setMinHeight(280);
            stage.setMinWidth(360);
            fxmlLoader.<MainWindow>getController().setMouse(mouse);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load MainWindow.fxml", exception);
        }
    }
}
