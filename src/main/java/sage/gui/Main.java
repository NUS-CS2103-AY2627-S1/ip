package sage.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import sage.Sage;

/**
 * Starts the JavaFX user interface for Sage.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/sage.txt";
    private static final String MAIN_WINDOW_FXML_PATH = "/view/MainWindow.fxml";

    /**
     * Provides one backend instance whose task state is shared by every GUI command.
     */
    private final Sage sage = new Sage(DATA_FILE_PATH);

    /**
     * Creates the JavaFX application entry point.
     */
    public Main() {
    }

    /**
     * Loads the main window, connects it to Sage, and displays the primary stage.
     *
     * @param stage The primary stage supplied by JavaFX.
     * @throws IOException If the main-window FXML cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowUrl = Objects.requireNonNull(
                Main.class.getResource(MAIN_WINDOW_FXML_PATH),
                "Missing FXML resource: " + MAIN_WINDOW_FXML_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowUrl);
        Parent mainWindow = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setSage(sage);

        stage.setScene(new Scene(mainWindow));
        stage.setTitle("Sage");
        stage.setMinWidth(360.0);
        stage.setMinHeight(420.0);
        stage.setResizable(true);
        stage.show();
    }
}
