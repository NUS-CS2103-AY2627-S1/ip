package megia.ui;

import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import megia.exception.StorageException;
import megia.model.TaskStorage;
import megia.service.CommandExecutor;
import megia.service.LocalStorageService;
import megia.service.LocalizationService;
import megia.service.PropertiesService;
import megia.service.TaskService;

/**
 * Configures and displays the Megia JavaFX chatbot.
 */
public final class MegiaGuiApplication extends Application {
    private final LocalStorageService configuredStorageService;
    private LocalStorageService localStorageService;
    private TaskStorage taskStorage;
    private MainController mainController;
    private Stage primaryStage;
    private boolean isStorageBlocked;

    /** Creates an application that uses the configured task storage path. */
    public MegiaGuiApplication() {
        this.configuredStorageService = null;
    }

    MegiaGuiApplication(LocalStorageService localStorageService) {
        this.configuredStorageService = localStorageService;
    }

    /**
     * Loads the FXML shell and displays the chatbot window.
     *
     * @param stage Primary JavaFX stage.
     * @throws IOException If the FXML or stylesheet cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Properties properties = PropertiesService.getProperties();
        localStorageService = configuredStorageService == null
                ? new LocalStorageService(properties.getProperty("storage.task.path"))
                : configuredStorageService;

        String startupError = null;
        try {
            taskStorage = localStorageService.loadTaskData().orElse(new TaskStorage());
        } catch (StorageException exception) {
            taskStorage = new TaskStorage();
            isStorageBlocked = true;
            startupError = LocalizationService.getException(
                    exception.getErrorCode(), exception.getMessageArguments());
        }

        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));
        mainController = new MainController(
                commandExecutor, startupError, new ProfileImageService(), this::requestExit);

        FXMLLoader loader = new FXMLLoader(
                MegiaGuiApplication.class.getResource("/megia/ui/MainView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == MainController.class) {
                return mainController;
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Cannot create FXML controller", exception);
            }
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                MegiaGuiApplication.class.getResource("/megia/ui/chat.css").toExternalForm());
        stage.setTitle("Megia");
        stage.setMinWidth(540);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            requestExit();
        });
        stage.show();
    }

    private boolean requestExit() {
        if (!isStorageBlocked && !saveTasks()) {
            return false;
        }
        mainController.completeExit();
        mainController.dispose();
        primaryStage.hide();
        return true;
    }

    private boolean saveTasks() {
        try {
            localStorageService.saveTaskData(taskStorage);
            return true;
        } catch (StorageException exception) {
            if (mainController != null) {
                mainController.showErrorMessage(LocalizationService.getException(
                        exception.getErrorCode(), exception.getMessageArguments()));
            }
            return false;
        }
    }
}
