package lebron.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lebron.LeBron;

/**
 * JavaFX entry point for the LeBron chatbot GUI.
 * Loads the main window from FXML and wires it to a {@link LeBron} instance.
 */
public class Main extends Application {
    private final LeBron leBron = new LeBron();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("LeBron");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setLeBron(leBron);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load MainWindow.fxml", e);
        }
    }
}
