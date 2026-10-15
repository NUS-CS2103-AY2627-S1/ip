package greenchonk.gui;

import java.io.IOException;

import greenchonk.GreenChonk;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Green Chonk's JavaFX interface.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Green Chonk — Task Companion";
    private static final double MINIMUM_WINDOW_WIDTH = 440.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 580.0;

    private final GreenChonk greenChonk = new GreenChonk();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setGreenChonk(greenChonk);

        stage.setTitle(WINDOW_TITLE);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
