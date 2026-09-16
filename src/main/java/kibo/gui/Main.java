package kibo.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kibo.Kibo;

/**
 * Displays Kibo's JavaFX user interface.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 420;
    private static final double MINIMUM_WINDOW_WIDTH = 420;

    private final Kibo kibo = new Kibo();

    /**
     * Creates the JavaFX application instance.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = Main.class.getResource("/view/MainWindow.fxml");
        if (mainWindowResource == null) {
            throw new IOException("MainWindow.fxml could not be found.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setKibo(kibo);

        stage.setTitle("Kibo - Small steps, brighter days");
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }
}
