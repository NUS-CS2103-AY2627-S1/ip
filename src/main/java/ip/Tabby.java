
package ip;

import java.util.List;

import ip.collection.TaskList;
import ip.exception.TabbyException;
import ip.model.Task;
import ip.parser.Parser;
import ip.storage.Storage;
import ip.ui.Ui;

/** Coordinates the command-line task manager. */
public class Tabby {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates Tabby using the supplied task-storage path. */
    public Tabby(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TabbyException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Runs the command-line interaction loop until the user exits. */
    public void run() {
        ui.showWelcome();

        while (true) {
            String fullCommand = ui.readCommand();
            if (fullCommand.equals("bye")) {
                ui.showBye();
                break;
            }

            try {
                executeCommand(fullCommand);
            } catch (TabbyException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /** Executes one already-read command. */
    private void executeCommand(String input) throws TabbyException {
        dispatchCommand(input);
    }

    /** Routes a command to the operation that handles it. */
    private void dispatchCommand(String input) throws TabbyException {
        if (input.equals("help")) {
            ui.showHelp();
        } else if (input.equals("list")) {
            ui.showTaskList(tasks);
        } else if (isCommand(input, "find")) {
            String keyword = input.substring(4).trim();
            if (keyword.isEmpty()) {
                throw new TabbyException("Please specify a keyword to find.");
            }
            List<Task> matchingTasks = tasks.find(keyword);
            ui.showMatchingTasks(matchingTasks);
        } else if (isCommand(input, "mark")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task task = tasks.get(index);
            task.markAsDone();
            storage.save(tasks);
            ui.showMarkedDone(task);
        } else if (isCommand(input, "unmark")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task task = tasks.get(index);
            task.markAsNotDone();
            storage.save(tasks);
            ui.showMarkedNotDone(task);
        } else if (isCommand(input, "delete")) {
            int index = Parser.parseTaskIndex(input, tasks.size());
            Task removedTask = tasks.delete(index);
            storage.save(tasks);
            ui.showTaskDeleted(removedTask, tasks.size());
        } else if (isCommand(input, "todo")) {
            Task task = Parser.parseTodo(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else if (isCommand(input, "deadline")) {
            Task task = Parser.parseDeadline(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else if (isCommand(input, "event")) {
            Task task = Parser.parseEvent(input);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        } else {
            throw new TabbyException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Returns whether the input is the command itself or the command followed by whitespace. */
    private boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /** Starts the command-line application. */
    public static void main(String[] args) {
        new Tabby("data/tabby.txt").run();
    }
}
