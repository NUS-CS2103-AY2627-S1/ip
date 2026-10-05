package kaykay.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the initial JavaFX application window for Kaykay using FXML.
 */
public final class Main extends Application {
    private static final String APP_ICON_PATH = "/images/kaykay-avatar.png";
    private static final double MIN_WINDOW_WIDTH = 420.0;
    private static final double MIN_WINDOW_HEIGHT = 300.0;

    /**
     * Starts the JavaFX application window.
     *
     * @param stage primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);
            stage.setTitle("Kaykay - Mission Control");
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    Main.class.getResource(APP_ICON_PATH)).toExternalForm()));
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            scene.getStylesheets().add(Main.class.getResource("/css/dialog-box.css").toExternalForm());
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Kaykay GUI.", exception);
        }
    }
}
