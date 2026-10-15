package min;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import min.exception.MinException;
import min.ui.Messages;
import min.ui.gui.MainWindow;

/**
 * Starts Min's JavaFX user interface.
 */
public class Main extends Application {
    private static final String STYLESHEET_PATH = "/view/min.css";
    private static final double MIN_WINDOW_WIDTH = 360.0;
    private static final double MIN_WINDOW_HEIGHT = 420.0;

    @Override
    public void start(Stage stage) {
        try {
            Min min = new Min();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();

            MainWindow controller = fxmlLoader.getController();
            controller.setMin(min);

            Scene scene = new Scene(mainLayout);
            scene.getStylesheets().add(loadStylesheet());

            stage.setScene(scene);
            stage.setTitle(Messages.APP_NAME);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.show();
        } catch (MinException e) {
            showStartupError(e.getMessage());
        } catch (IOException e) {
            showStartupError(Messages.STARTUP_ERROR);
        } catch (RuntimeException e) {
            e.printStackTrace();
            showStartupError(Messages.UNEXPECTED_STARTUP_ERROR);
        }
    }

    /**
     * Returns the location of Min's stylesheet.
     *
     * @return The stylesheet location in the form JavaFX expects.
     * @throws IllegalStateException If the stylesheet is missing from the build.
     */
    private static String loadStylesheet() {
        URL stylesheet = Main.class.getResource(STYLESHEET_PATH);
        if (stylesheet == null) {
            throw new IllegalStateException("Missing stylesheet: " + STYLESHEET_PATH);
        }

        return stylesheet.toExternalForm();
    }

    /**
     * Shows an error dialog when Min cannot start.
     *
     * @param message The error message to display.
     */
    private void showStartupError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Messages.APP_NAME);
        alert.setHeaderText(Messages.STARTUP_ERROR_HEADER);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
