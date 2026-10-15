package chocolate.gui;

import java.io.IOException;

import chocolate.Chocolate;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for Chocolate's graphical user interface.
 */
public class Main extends Application {
    private final Chocolate chocolate = new Chocolate("data/duke.txt");

    /**
     * Creates and displays Chocolate's main window.
     *
     * @param stage Primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            MainWindow mainWindow = loader.getController();
            mainWindow.setChocolate(chocolate);
            stage.setTitle("Chocolate's Cocoa Corner");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Chocolate's main window.", e);
        }
    }

    /**
     * Starts the JavaFX runtime.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
