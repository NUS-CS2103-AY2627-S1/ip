package bob.gui;

import java.io.IOException;
import java.nio.file.Path;

import bob.Bob;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Bob's JavaFX user interface.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 380;
    private static final double MIN_WINDOW_HEIGHT = 560;
    private static final double WINDOW_WIDTH = 430;
    private static final double WINDOW_HEIGHT = 700;

    private final Bob bob = new Bob(Path.of("data", "bob.txt"));

    /**
     * Creates and displays Bob's main window.
     *
     * @param stage Primary stage supplied by JavaFX.
     * @throws IOException If the main-window FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane mainWindow = loadMainWindow();
        showMainWindow(stage, mainWindow);
    }

    /**
     * Loads the main-window layout and supplies its controller with Bob.
     *
     * @return Loaded main-window layout.
     * @throws IOException If the main-window FXML resource cannot be loaded.
     */
    private AnchorPane loadMainWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setBob(bob);
        return mainWindow;
    }

    /**
     * Configures and displays the stage containing Bob's main window.
     *
     * @param stage Primary stage supplied by JavaFX.
     * @param mainWindow Main-window layout to display.
     */
    private void showMainWindow(Stage stage, AnchorPane mainWindow) {
        Scene scene = new Scene(mainWindow);
        stage.setTitle("Bob");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
