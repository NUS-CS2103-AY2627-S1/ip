package gongrilla.gui;

import java.io.IOException;

import gongrilla.Gongrilla;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Provides the JavaFX entry point for Gongrilla. */
public class Main extends Application {
    private final Gongrilla gongrilla = new Gongrilla();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();

            mainWindow.setGongrilla(gongrilla);
            stage.setTitle("Gongrilla");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load the main window", exception);
        }
    }
}
