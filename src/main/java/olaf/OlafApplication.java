package olaf;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Configures and displays Olaf's JavaFX user interface.
 */
public class OlafApplication extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String STYLESHEET = "/view/Olaf.css";
    private static final double WINDOW_WIDTH = 520.0;
    private static final double WINDOW_HEIGHT = 640.0;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;
    private static final long EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 2L;

    private ExecutorService commandExecutor;
    private Olaf olaf;
    private String startupError;

    /**
     * Creates an application whose resources are initialized by the JavaFX lifecycle.
     */
    public OlafApplication() {
    }

    /** {@inheritDoc} */
    @Override
    public void init() {
        commandExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "olaf-command-worker");
            thread.setDaemon(true);
            return thread;
        });

        try {
            olaf = Olaf.createDefault();
        } catch (StorageException exception) {
            startupError = ResponseFormatter.formatError(exception.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = loadMainWindow();
        MainWindow mainWindow = loader.getController();
        configureController(mainWindow);
        configureStage(stage, loader.getRoot());
        stage.show();
        Platform.runLater(mainWindow::requestInputFocus);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (commandExecutor == null) {
            return;
        }

        commandExecutor.shutdown();
        try {
            if (!commandExecutor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                commandExecutor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            commandExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /** Loads the view and retains its controller for dependency injection before the stage is shown. */
    private FXMLLoader loadMainWindow() {
        FXMLLoader loader = new FXMLLoader(requireResource(MAIN_WINDOW_FXML));
        try {
            loader.load();
            return loader;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Olaf's main window.", exception);
        }
    }

    private void configureController(MainWindow mainWindow) {
        mainWindow.setCommandExecutor(commandExecutor);
        if (olaf == null) {
            mainWindow.showStartupError(startupError);
        } else {
            mainWindow.setOlaf(olaf);
        }
    }

    private void configureStage(Stage stage, Parent root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(requireResource(STYLESHEET).toExternalForm());
        stage.setTitle("Olaf");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private URL requireResource(String path) {
        URL resource = OlafApplication.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Required GUI resource is missing: " + path);
        }
        return resource;
    }
}
