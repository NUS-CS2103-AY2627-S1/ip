package turtley.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import turtley.Turtley;

/**
 * Starts Turtley's JavaFX user interface.
 */
public class Main extends Application {

    private final Turtley turtley = new Turtley();

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    /**
     * Loads and displays the main Turtley window.
     *
     * @param stage the primary application stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            URL stylesheet = Main.class.getResource("/styles/gui.css");
            if (stylesheet == null) {
                throw new IllegalStateException("Missing GUI stylesheet resource.");
            }
            scene.getStylesheets().add(stylesheet.toExternalForm());
            stage.setTitle("Turtley");
            stage.setMinWidth(420.0);
            stage.setMinHeight(320.0);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            fxmlLoader.<MainWindow>getController().setTurtley(turtley);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Turtley interface.", exception);
        }
    }
}
