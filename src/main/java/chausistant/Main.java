package chausistant;

import java.io.IOException;
import java.net.URL;

import chausistant.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Launches the FXML-based Chausistant application.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final double WINDOW_WIDTH = 460.0;
    private static final double WINDOW_HEIGHT = 680.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 520.0;

    private final Chausistant chausistant = new Chausistant();

    @Override
    public void start(Stage stage) {
        URL fxmlLocation = Main.class.getResource(MAIN_WINDOW_FXML);
        if (fxmlLocation == null) {
            throw new IllegalStateException("Missing FXML resource: " + MAIN_WINDOW_FXML);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        try {
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setChausistant(chausistant);

            stage.setTitle("chausistant");
            stage.setResizable(true);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setScene(new Scene(mainWindow));
            stage.show();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the main window.", error);
        }
    }
}
