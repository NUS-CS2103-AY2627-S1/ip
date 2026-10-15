package bob.gui;

import java.io.IOException;

import bob.Bob;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts Bob's JavaFX GUI using its FXML main window.
 */
public class Main extends Application {
    private final Bob bob = new Bob("data/bob.txt");

    /**
     * Loads the main window and injects the shared Bob application instance.
     *
     * @param stage primary application window
     * @throws IOException if the main-window FXML cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);
        scene.getStylesheets().add(Main.class.getResource("/view/bob.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Bob");
        fxmlLoader.<MainWindow>getController().setBob(bob);
        stage.show();
    }
}
