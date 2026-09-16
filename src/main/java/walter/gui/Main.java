package walter.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import walter.Walter;

/**
 * Configures and displays Walter's JavaFX window.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        try {
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setWalter(new Walter());

            stage.setScene(new Scene(root));
            stage.setTitle("Walter");
            stage.setMinWidth(420);
            stage.setMinHeight(500);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Walter could not load its main window.", exception);
        }
    }
}
