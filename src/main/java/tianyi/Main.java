package tianyi;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tianyi.ui.MainWindow;

/**
 * A GUI for Tianyi using FXML.
 */
public class Main extends Application {
    private static final double WINDOW_MIN_HEIGHT = 220;
    private static final double WINDOW_MIN_WIDTH = 500;

    private final Tianyi tianyi = new Tianyi();

    /**
     * Creates the Tianyi JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinHeight(WINDOW_MIN_HEIGHT);
            stage.setMinWidth(WINDOW_MIN_WIDTH);
            fxmlLoader.<MainWindow>getController().setTianyi(tianyi);
            stage.setTitle("Tianyi");
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window layout.", e);
        }
    }
}
