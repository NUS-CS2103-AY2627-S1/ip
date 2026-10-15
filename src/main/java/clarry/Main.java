package clarry;

import java.io.IOException;
import java.net.URL;

import clarry.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Loads and displays Clarry's JavaFX interface.
 */
public class Main extends Application {
    private final Clarry clarry = new Clarry();

    /**
     * Creates the main scene and shows the application window.
     *
     * @param stage primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            URL mainWindow = Main.class.getResource("/view/MainWindow.fxml");
            URL stylesheet = Main.class.getResource("/styles/main.css");
            assert mainWindow != null : "MainWindow.fxml must be packaged with the application";
            assert stylesheet != null : "main.css must be packaged with the application";
            FXMLLoader loader = new FXMLLoader(mainWindow);
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setClarry(clarry);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(stylesheet.toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Clarry");
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new AssertionError("Unable to load Clarry's interface", e);
        }
    }
}
