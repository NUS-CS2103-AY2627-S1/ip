package bo.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts Bo's JavaFX interface using an FXML view and controller.
 */
public class Main extends Application {
    /**
     * Loads the main view and displays it in the primary stage.
     *
     * @param stage the primary JavaFX window
     * @throws IOException if the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
        stage.setTitle("Bo");
        stage.setMinWidth(417);
        stage.setMinHeight(220);
        stage.setScene(scene);
        stage.show();
    }
}
