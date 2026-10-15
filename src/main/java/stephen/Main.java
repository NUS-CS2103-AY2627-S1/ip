package stephen;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import stephen.gui.MainWindow;

/** Starts Orbit's JavaFX graphical interface. */
public class Main extends Application {
    private static final Path DATA_FILE = Path.of("data", "stephen.txt");

    /** Loads the FXML view, injects the chatbot, and displays the main window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            Pane root = loader.load();
            loader.<MainWindow>getController().setChatbot(new Chatbot(DATA_FILE));

            Scene scene = new Scene(root, 620, 720);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            stage.setTitle("Orbit - Task Mission Control");
            stage.setMinWidth(360);
            stage.setMinHeight(480);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Orbit could not start");
            alert.setHeaderText("The interface could not be loaded.");
            alert.setContentText("Please check that the application resources are present.");
            alert.showAndWait();
        }
    }
}
