package grug.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The main GUI for the Grug chatbot.
 */
public class GrugApp extends Application {
    @Override
    public void start(Stage stage) {
        URL mainWindowResource = GrugApp.class.getResource("/view/MainWindow.fxml");
        if (mainWindowResource == null) {
            throw new IllegalStateException("Could not initialize GUI: MainWindow.fxml was not found");
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            AnchorPane mainWindowRoot = fxmlLoader.load();
            Scene scene = new Scene(mainWindowRoot);
            stage.setScene(scene);

            stage.setTitle("Grug");

            stage.setMinHeight(220);
            stage.setMinWidth(417);

            MainWindow mainWindowController = fxmlLoader.getController();
            if (mainWindowController == null) {
                throw new IllegalStateException("Could not initialize GUI: MainWindow controller was not created");
            }
            mainWindowController.initializeGrugBackend();

            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize GUI from MainWindow.fxml", e);
        }
    }
}
