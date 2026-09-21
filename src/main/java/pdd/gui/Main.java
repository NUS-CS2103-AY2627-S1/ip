package pdd.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pdd.PDD;

/**
 * JavaFX application class. Loads {@code MainWindow.fxml}, wires a
 * {@link PDD} chatbot instance into its controller, and shows the stage.
 * The chatbot shares the same save file as the console text UI
 * ({@code ./data/pdd.txt}), so either UI can be used interchangeably.
 */
public class Main extends Application {
    private final PDD pdd = new PDD("./data/pdd.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("PDD");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Pdd.png")));
            stage.setMinWidth(320.0);
            stage.setMinHeight(400.0);
            fxmlLoader.<MainWindow>getController().setPdd(pdd);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load MainWindow.fxml", e);
        }
    }
}
