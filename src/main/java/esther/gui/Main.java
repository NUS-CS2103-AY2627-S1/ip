package esther.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import esther.command.CommandProcessor;
import esther.exception.EstherException;
import esther.storage.Storage;
import esther.task.TaskList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for Esther.
 */
public class Main extends Application {
    /**
     * Starts the initial Esther JavaFX window.
     *
     * @param stage Primary application window.
     */
    @Override
    public void start(Stage stage) {
        Storage storage = new Storage(Path.of("data", "esther.txt"));
        TaskList tasks;

        try {
            tasks = storage.load();
        } catch (IOException | EstherException exception) {
            tasks = new TaskList();
        }

        CommandProcessor commandProcessor = new CommandProcessor(storage, tasks);

        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            MainWindow mainWindow = loader.getController();
            mainWindow.setCommandProcessor(commandProcessor);

            Scene scene = new Scene(root, 400, 600);

            stage.setTitle("Esther");
            stage.setScene(scene);
            stage.show();
            mainWindow.showEstherMessages(List.of(
                    "Heyyyy! I'm your favourite assistant Esther.",
                    "What can I do for you today?"));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load the main window view.", exception);
        }
    }
}
