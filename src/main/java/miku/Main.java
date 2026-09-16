package miku;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import miku.ui.MainWindow;

/** Provides the JavaFX application shell for Miku. */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 220;
    private static final double MINIMUM_WINDOW_WIDTH = 417;

    private final Miku miku = new Miku();

    /** Loads Miku's FXML interface into the primary application stage. */
    @Override
    public void start(Stage stage) {
        try {
            URL mainWindowResource = Main.class.getResource("/view/MainWindow.fxml");
            assert mainWindowResource != null : "Main window FXML must be packaged.";
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            Parent mainLayout = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setMiku(miku);
            mainWindow.setExitHandler(stage::close);

            Scene scene = new Scene(mainLayout);
            URL dialogStyles = Main.class.getResource("/css/dialog-box.css");
            assert dialogStyles != null : "Dialog styling must be packaged.";
            scene.getStylesheets().add(dialogStyles.toExternalForm());
            stage.setTitle("Miku");
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Miku's interface could not be loaded.", exception);
        }
    }
}
