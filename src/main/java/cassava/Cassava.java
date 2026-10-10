package cassava;

import static cassava.data.TaskData.getTasks;
import static cassava.data.TaskData.putTasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cassava.data.FileFormatException;
import cassava.gui.MainWindow;
import cassava.task.Task;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Prompts the user to add, update or delete tasks.
 * Tasks can be marked as completed.
 * Tasks are saved in a text file after each update, and when the program exits.
 */
public class Cassava extends Application {
    private static final String name = "Cassava";

    private static final List<Task> tasks = new ArrayList<Task>();

    private static String loadWarning = null;

    private static void loadTasks() {
        List<Task> savedTasks = new ArrayList<>();

        try {
            savedTasks = getTasks();
        } catch (FileFormatException e) {
            loadWarning = "Your data file appears to be corrupted, so I've started you off with "
                    + "an empty task list. Your previous tasks were not lost, but adding new tasks "
                    + "may overwrite the corrupted file.";
        } catch (IOException e) {
            loadWarning = "I couldn't read your saved tasks (" + e.getMessage() + "), "
                    + "so I've started you off with an empty task list.";
        }

        tasks.addAll(savedTasks);
    }

    private static void saveTasks() {
        try {
            putTasks(tasks);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void start(Stage stage) {
        loadTasks();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Cassava.class.getResource("/views/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            MainWindow controller = fxmlLoader.<MainWindow>getController();
            controller.setTasks(tasks);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle(name);
            stage.setOnCloseRequest(event -> saveTasks());
            stage.show();

            if (loadWarning != null) {
                controller.addErrorDialog(loadWarning);
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
