package toothless;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import toothless.ui.MainWindow;

/**
 * Displays the Toothless JavaFX application.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        URL mainWindowResource = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml must be available on the classpath");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);

        try {
            Parent root = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setToothless(new Toothless());

            Scene scene = new Scene(root, 560, 720);
            URL stylesheetResource = Main.class.getResource("/css/toothless.css");
            if (stylesheetResource != null) {
                scene.getStylesheets().add(stylesheetResource.toExternalForm());
            }
            stage.setTitle("Toothless");
            stage.setMinWidth(440);
            stage.setMinHeight(560);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Toothless window", exception);
        }
    }
}
