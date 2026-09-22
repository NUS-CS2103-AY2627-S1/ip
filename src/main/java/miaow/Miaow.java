package miaow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import miaow.gui.DialogBox;
import miaow.storage.Storage;
import miaow.storage.TaskList;
import miaow.task.Task;
import miaow.user.Parser;
import miaow.user.UI;
import miaow.MainWindow;




/**
 * Public class miaow
 */
public class Miaow extends Application {
    private Storage storage;
    private Parser parser;
    private TaskList tasks;
    private UI ui;



    /**
     * Constructor for class
     */
    public Miaow() {
        this.ui = new UI();
        this.parser = new Parser();
        this.storage = new Storage("./data/miaow.txt");
        ArrayList<Task> loadedTasks = storage.loadTasks();
        assert loadedTasks != null : "Storage must return a task list";

        this.tasks = new TaskList(loadedTasks);
        assert this.tasks != null : "TaskList must be initialised";

        ui.showLoadingSuccess(loadedTasks.size());
    }

    /**
     * lowkey idk, some javafx thing
     * @param stage starts this
     */

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Miaow.class.getResource("/view/MainWindow.fxml"));

            AnchorPane ap = fxmlLoader.load();

            MainWindow controller = fxmlLoader.getController();
            controller.setMiaow(this);

            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if command
     * @param s string to check
     */
    public boolean isExitCommand(String s) {
        return Objects.equals(s, "bye");
    }

    public String getResponse(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "Please enter a command.";
        }

        command = command.trim();

        Parser.CommandType commandType = parser.getCommandType(command);
        assert commandType != null : "Parser must return a command type";


        switch (commandType) {
            case MIAOW:
                return "Miaow :3";
            case BYE:
                storage.saveTasks(tasks.getTasks());
                return "Goodbye!";

            case LIST:
                if (tasks.isEmpty()) {
                    return "Your task list is empty.";
                }
                return tasks.toString();

            case TODO:
                Task todo = parser.parseTodo(command);

                if (todo == null) {
                    return "Invalid todo format.";
                }

                try {
                    tasks.addTask(todo);
                    storage.saveTasks(tasks.getTasks());
                } catch (IllegalArgumentException e) {
                    return "Error: " + e.getMessage();
                } catch (IllegalStateException e) {
                    return "Error: Unable to save the task.";
                }

                return "Got it. I've added this task:\n" + todo;

            case DEADLINE:
                Task deadline = parser.parseDeadline(command);

                if (deadline == null) {
                    return "Invalid deadline format.";
                }

                try {
                    tasks.addTask(deadline);
                    storage.saveTasks(tasks.getTasks());
                } catch (IllegalArgumentException e) {
                    return "Error: " + e.getMessage();
                } catch (IllegalStateException e) {
                    return "Error: Unable to save the task.";
                }

                return "Got it. I've added this task:\n" + deadline;

            case MARK:
                int markIndex = parser.parseTaskNumber(command, "mark ");

                if (!tasks.isValidIndex(markIndex)) {
                    return "Invalid task number.";
                }

                tasks.markTask(markIndex);
                storage.saveTasks(tasks.getTasks());

                return "Marked task:\n" + tasks.getTask(markIndex);

            case UNMARK:
                int unmarkIndex = parser.parseTaskNumber(command, "unmark ");

                if (!tasks.isValidIndex(unmarkIndex)) {
                    return "Invalid task number.";
                }

                tasks.unmarkTask(unmarkIndex);
                storage.saveTasks(tasks.getTasks());

                return "Unmarked task:\n" + tasks.getTask(unmarkIndex);
            case EVENT:
                Task event = parser.parseEvent(command);

                if (event == null) {
                    return "Invalid event format.";
                }

                try {
                    tasks.addTask(event);
                    storage.saveTasks(tasks.getTasks());
                } catch (IllegalArgumentException e) {
                    return "Error: " + e.getMessage();
                } catch (IllegalStateException e) {
                    return "Error: Unable to save the task.";
                }

                return "Got it. I've added this task:\n" + event;

            case FIND:
                String keyword = parser.parseFindKeyword(command);

                if (keyword == null) {
                    return "Please provide a keyword to search for.";
                }

                ArrayList<Task> matchingTasks = tasks.findTasksByKeyword(keyword);
                return matchingTasks.toString();
            case DELETE:
                int deleteIndex = parser.parseTaskNumber(command, "delete ");

                if (!tasks.isValidIndex(deleteIndex)) {
                    return "Invalid task number.";
                }

                Task deletedTask = tasks.deleteTask(deleteIndex);
                storage.saveTasks(tasks.getTasks());

                return "Deleted task:\n" + deletedTask;
            case SORT:
                tasks.sortChronologically();
                storage.saveTasks(tasks.getTasks());
                return "Tasks sorted chronologically.";
            default:
                return "Sorry, I don't understand that command.";
        }
    }

    /**
     * Runs the method
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                String response = getResponse(command);
                ui.showMessage(response);

                if (isExitCommand(command)) {
                    return;
                }
            } catch (Exception e) {
                ui.showError("Miaow: An error occurred -- " + e.getMessage());
            }

            ui.showLine();
        }
    }

    public static void main(String[] args) {
        new Miaow().run();
    }


}
