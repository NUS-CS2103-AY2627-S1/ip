package phin;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Configures and displays the Phin JavaFX window.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox root = loader.load();
        loader.<MainWindow>getController().setPhin(new Phin());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.setTitle("Phin");
        stage.setScene(scene);
        stage.show();
    }
}
