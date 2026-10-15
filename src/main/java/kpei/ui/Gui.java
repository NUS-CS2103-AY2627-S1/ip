package kpei.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import kpei.CommandCenter;
import kpei.datatypes.TaskList;
import kpei.storage.Storage;
import kpei.ui.controllers.MainWindowController;

/**
 * JavaFX Application class bootstrapping the GUI for BERT Assistant.
 */
public class Gui extends Application {

    private static final String MAIN_WINDOW_FXML = "/kpei/views/MainWindow.fxml";
    private static final String APPLICATION_TITLE = "BERT Assistant";
    private static final double MIN_WIDTH = 850;
    private static final double MIN_HEIGHT = 580;

    private static String DEFAULT_STORAGE_PATH;

    /**
     * Sets the shared storage and task list dependencies initialized by Launcher.
     *
     * @param defaultStoragePath The file path of the task list save file.
     */
    public static void initDependencies(String defaultStoragePath) {
        DEFAULT_STORAGE_PATH = defaultStoragePath;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Parent root = fxmlLoader.load();
        MainWindowController mainWindowController = fxmlLoader.getController();

        Storage storage = new Storage(DEFAULT_STORAGE_PATH);
        TaskList taskList = new TaskList(storage.getFileName());
        Cli cli = new Cli(msg -> mainWindowController.getCliTerminalController().appendOutput(msg));
        CommandCenter commandCenter = new CommandCenter(storage, taskList, cli, mainWindowController);

        mainWindowController.setDependencies(commandCenter);

        Scene scene = new Scene(root);
        stage.setTitle(APPLICATION_TITLE);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
