package clsl;

import java.io.IOException;
import java.util.List;

import clsl.parser.ParsedCommand;
import clsl.parser.Parser;
import clsl.storage.Storage;
import clsl.task.Deadline;
import clsl.task.Event;
import clsl.task.Task;
import clsl.task.TaskList;
import clsl.task.ToDo;
import clsl.ui.console.Ui;

/**
 * Coordinates user interaction, command processing, task management, and storage.
 */
public class Clsl {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExitRequested;

    /**
     * Creates the application and loads its saved tasks.
     *
     * @param filePath Path of the task data file.
     */
    public Clsl(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            List<Task> savedTasks = storage.load();
            loadedTasks = new TaskList(savedTasks);
            if (loadedTasks.size() < savedTasks.size()) {
                try {
                    storage.save(loadedTasks.asList());
                } catch (IOException e) {
                    ui.showMessage(ui.formatLoadingError(
                            "Loaded tasks, but could not remove duplicate tasks from storage."));
                }
            }
        } catch (IOException e) {
            ui.showMessage(ui.formatLoadingError("Unable to load saved tasks. Starting with an empty task list."));
            loadedTasks = new TaskList();
        } catch (ClslException e) {
            ui.showMessage(ui.formatLoadingError(e.getMessage()));
            loadedTasks = new TaskList();
        }
        assert loadedTasks != null : "Application must always have a task list";
        tasks = loadedTasks;
    }

    /** Runs the application's command-processing loop. */
    public void run() {
        ui.showMessage(ui.formatWelcome());

        while (!isExitRequested) {
            ui.showMessage(getResponse(ui.readCommand()));
        }
    }

    /**
     * Processes one command and returns the corresponding user-facing response.
     *
     * @param userInput Command entered by the user.
     * @return Response produced after processing the command.
     */
    public String getResponse(String userInput) {
        try {
            ParsedCommand parsedCommand = Parser.parse(userInput);
            switch (parsedCommand.getType()) {
                case LIST:
                    return ui.formatTaskList(tasks.asList());
                case MARK:
                    return markTask(parsedCommand.getTaskIndex());
                case UNMARK:
                    return unmarkTask(parsedCommand.getTaskIndex());
                case TODO:
                    return addTask(new ToDo(parsedCommand.getDescription()));
                case DEADLINE:
                    return addTask(new Deadline(parsedCommand.getDescription(),
                            parsedCommand.getFirstDate().toString()));
                case EVENT:
                    return addTask(new Event(parsedCommand.getDescription(),
                            parsedCommand.getFirstDate().toString(),
                            parsedCommand.getSecondDate().toString()));
                case DELETE:
                    return deleteTask(parsedCommand.getTaskIndex());
                case ON:
                    return ui.formatTasksOn(parsedCommand.getFirstDate(),
                            tasks.getTasksOn(parsedCommand.getFirstDate()));
                case FIND:
                    return ui.formatMatchingTasks(tasks.findTasks(parsedCommand.getDescription()));
                case BYE:
                    isExitRequested = true;
                    return ui.formatGoodbye();
                default:
                    throw new ClslException("I don't understand");
            }
        } catch (ClslException e) {
            return ui.formatError(e.getMessage());
        } catch (IOException e) {
            return ui.formatError("Unable to save task data. Please try again.");
        }
    }

    private String markTask(int taskIndex) throws IOException, ClslException {
        requireValidTaskIndex(taskIndex);
        tasks.mark(taskIndex);
        storage.save(tasks.asList());
        return ui.formatTaskMarked(tasks.get(taskIndex));
    }

    private String unmarkTask(int taskIndex) throws IOException, ClslException {
        requireValidTaskIndex(taskIndex);
        tasks.unmark(taskIndex);
        storage.save(tasks.asList());
        return ui.formatTaskUnmarked(tasks.get(taskIndex));
    }

    private String addTask(Task task) throws IOException, ClslException {
        tasks.add(task);
        storage.save(tasks.asList());
        return ui.formatTaskAdded(task, tasks.size());
    }

    private String deleteTask(int taskIndex) throws IOException, ClslException {
        requireValidTaskIndex(taskIndex);
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks.asList());
        return ui.formatTaskDeleted(removedTask, tasks.size());
    }

    /** Checks that a task index refers to a task currently in the list. */
    private void requireValidTaskIndex(int taskIndex) throws ClslException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new ClslException("task number is out of range");
        }
    }

    /** Starts the application with its default task data file. */
    public static void main(String[] args) {
        new Clsl("data/csls.txt").run();
    }
}
