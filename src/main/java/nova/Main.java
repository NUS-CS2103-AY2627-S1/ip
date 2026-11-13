package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nova.ui.MainWindow;

/**
 * JavaFX GUI for Nova.
 */
public class Main extends Application {
    private final Nova nova = new Nova("data/nova.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            stage.setTitle("Nova");
            stage.setScene(scene);
            stage.setMinHeight(320);
            stage.setMinWidth(420);
            loader.<MainWindow>getController().setNova(nova);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load the Nova interface.", exception);
        }
    }
}
