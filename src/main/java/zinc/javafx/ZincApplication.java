package zinc.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zinc.Zinc;

/**
 * Starts Zinc's JavaFX user interface.
 */
public class ZincApplication extends Application {
    /** The minimum width of the main application window. */
    private static final double MINIMUM_WIDTH = 600;

    /** The minimum height of the main application window. */
    private static final double MINIMUM_HEIGHT = 500;

    /** The application instance that processes commands entered through the GUI. */
    private final Zinc zinc = new Zinc();

    /** Loads and displays Zinc's main window. */
    @Override
    public void start(Stage stage) throws IOException {
        URL layoutUrl = Objects.requireNonNull(ZincApplication.class.getResource("/view/MainWindow.fxml"),
                "Main-window layout is missing");
        URL stylesheetUrl = Objects.requireNonNull(ZincApplication.class.getResource("/view/main.css"),
                "Main-window stylesheet is missing");
        FXMLLoader fxmlLoader = new FXMLLoader(layoutUrl);
        Parent mainWindowRoot = fxmlLoader.load();
        Scene scene = new Scene(mainWindowRoot);
        scene.getStylesheets().add(stylesheetUrl.toExternalForm());
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Zinc");

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setZinc(zinc);
        stage.show();
    }
}
