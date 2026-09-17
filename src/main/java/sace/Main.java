package sace;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Creates and displays Sace's JavaFX user interface.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String THEME_STYLESHEET = "/css/theme.css";

    private final Sace sace = new Sace();

    /**
     * Loads the main view, connects it to Sace, and displays the application window.
     *
     * @param stage primary window supplied by JavaFX.
     * @throws IOException if the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = Objects.requireNonNull(
                Main.class.getResource(MAIN_WINDOW_FXML), "MainWindow.fxml is missing");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        URL stylesheetResource = Objects.requireNonNull(
                Main.class.getResource(THEME_STYLESHEET), "theme.css is missing");
        scene.getStylesheets().add(stylesheetResource.toExternalForm());

        MainWindow controller = fxmlLoader.getController();
        controller.setSace(sace);

        stage.setTitle("Sace - Moonlit Task Oracle");
        stage.setMinWidth(640);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }
}
