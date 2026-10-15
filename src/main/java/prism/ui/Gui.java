package prism.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the Prism JavaFX application and loads its FXML view. */
public class Gui extends Application {
    /** Starts the JavaFX application window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Gui.class.getResource("/view/MainWindow.fxml"));

            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root, 650, 450));

            stage.setTitle("Prism");
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Prism GUI.", exception);
        }
    }

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
