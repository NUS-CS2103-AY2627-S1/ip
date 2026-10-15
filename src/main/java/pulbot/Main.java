package pulbot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Pulbot using FXML.
 */
public class Main extends Application {
    private static final String PRODUCT_NAME = "Pulbot";
    private static final double MINIMUM_WINDOW_HEIGHT = 220;
    private static final double MINIMUM_WINDOW_WIDTH = 417;

    private final Pulbot pulbot = new Pulbot();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(PRODUCT_NAME);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setPulbot(pulbot);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window layout.", e);
        }
    }
}
