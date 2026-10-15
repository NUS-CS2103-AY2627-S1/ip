package bos;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX GUI for Bos using an FXML layout.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_WIDTH = 380;
    private static final double MINIMUM_WINDOW_HEIGHT = 520;

    private final Bos bos = new Bos();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);

        fxmlLoader.<MainWindow>getController().setBos(bos);
        stage.setTitle("Bos");
        stage.setScene(scene);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.show();
    }
}
