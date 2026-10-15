package dave;

import java.io.IOException;

import dave.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main application class for the Dave GUI.
 */
public class Main extends Application {

    /** Minimum allowable window width in pixels. */
    private static final double MIN_WINDOW_WIDTH = 420.0;
    /** Minimum allowable window height in pixels. */
    private static final double MIN_WINDOW_HEIGHT = 550.0;

    private final Dave dave = new Dave();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Dave - The Knight Butler");
            stage.setResizable(true);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            fxmlLoader.<MainWindow>getController().setDave(this.dave);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
