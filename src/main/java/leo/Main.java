package leo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX GUI entry point. Builds the main window from
 * {@code MainWindow.fxml}, hands the window's controller a {@link Leo}
 * instance to talk to, and shows it. Started via {@link Launcher} rather
 * than directly (see its Javadoc for why).
 */
public class Main extends Application {
    private final Leo leo = new Leo();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            // A-BetterGui: theme.css replaces the plain default JavaFX
            // look with a warmer background and colour-coded, rounded
            // chat bubbles - see that file for the full rationale.
            scene.getStylesheets().add(
                    Main.class.getResource("/view/theme.css").toExternalForm()
            );

            stage.setTitle("Leo");
            // A-BetterGui: raised from 220 to make room for the new
            // quick-action button row in MainWindow.fxml, so the window
            // can never be resized small enough to clip it.
            stage.setMinHeight(260);
            stage.setMinWidth(417);
            stage.setScene(scene);

            MainWindow controller = fxmlLoader.getController();
            controller.setLeo(leo);

            stage.show();
        } catch (IOException e) {
            // Thrown if MainWindow.fxml is missing or malformed; there is
            // no sensible way to recover, so surface it on the console
            // and let the GUI fail to open rather than showing a broken
            // window.
            e.printStackTrace();
        }
    }
}
