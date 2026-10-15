package nob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import nob.command.ClearCommand;
import nob.command.ExitCommand;
import nob.command.HelpCommand;
import nob.command.ListCommand;
import nob.exception.NobException;
import nob.parser.Parser;
import nob.storage.Storage;
import nob.task.Task;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Starts the Nob chatbot application.
 */
public class Nob {
    /** The maximum number of tasks Nob can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** The file used to store the current task list. */
    private static final Path DATA_FILE = Path.of("data", "nob.txt");

    /**
     * Reads and responds to commands until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which this application does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        Task[] loadedTasks = new Task[MAX_TASKS];
        int taskCount;
        try {
            taskCount = storage.loadTasks(loadedTasks);
        } catch (NobException exception) {
            ui.showLoadingError();
            taskCount = 0;
        }
        TaskList tasks = new TaskList(loadedTasks, taskCount);
        ui.showWelcome();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (true) {
                String command = reader.readLine();
                if (command == null) {
                    command = "";
                }
                System.out.println();
                ui.showDivider();

                try {
                    boolean shouldExit = executeCommand(command, tasks, storage, ui);
                    if (shouldExit) {
                        ui.showDivider();
                        break;
                    }
                } catch (NobException exception) {
                    ui.showError(exception.getMessage());
                }
                ui.showDivider();
                System.out.println();
            }
        } catch (IOException exception) {
            ui.showInputError();
            exception.printStackTrace();
        }
    }

    /**
     * Executes one command entered through the console.
     *
     * @param command The command entered by the user.
     * @param tasks The user's task list.
     * @param storage Persistent task storage.
     * @param ui The console user interface.
     * @return {@code true} if Nob should stop accepting commands.
     * @throws NobException If the command contains invalid task details.
     */
    private static boolean executeCommand(String command, TaskList tasks, Storage storage, Ui ui)
            throws NobException {
        if (command.equals("bye")) {
            new ExitCommand().execute(tasks, ui, storage);
            return true;
        }
        if (command.equals("help")) {
            new HelpCommand().execute(tasks, ui, storage);
        } else if (command.equals("clear")) {
            new ClearCommand().execute(tasks, ui, storage);
        } else if (command.equals("list")) {
            new ListCommand().execute(tasks, ui, storage);
        } else {
            executeTaskCommand(command, tasks, storage, ui);
        }
        return false;
    }

    /** Executes a command that queries or modifies tasks. */
    private static void executeTaskCommand(String command, TaskList tasks, Storage storage, Ui ui)
            throws NobException {
        if (command.equals("find") || command.startsWith("find ")) {
            findTasks(command, tasks, ui);
        } else if (command.startsWith("mark ")) {
            markTask(command, tasks, true, storage, ui);
        } else if (command.startsWith("unmark ")) {
            markTask(command, tasks, false, storage, ui);
        } else if (command.startsWith("delete ")) {
            deleteTask(command, tasks, storage, ui);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            addTask(Parser.parseTodo(command), tasks, storage, ui);
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            addTask(Parser.parseDeadline(command), tasks, storage, ui);
        } else if (command.equals("event") || command.startsWith("event ")) {
            addTask(Parser.parseEvent(command), tasks, storage, ui);
        } else {
            showInvalidCommandHint(command, ui);
        }
    }

    /** Shows the most relevant hint for an invalid command. */
    private static void showInvalidCommandHint(String command, Ui ui) {
        if (command.startsWith("todo")) {
            ui.showMissingSpaceHint("todo", command);
        } else if (command.startsWith("deadline")) {
            ui.showMissingSpaceHint("deadline", command);
        } else if (command.startsWith("event")) {
            ui.showMissingSpaceHint("event", command);
        } else {
            ui.showUnknownCommandHint();
        }
    }

    /**
     * Stores a task and reports the result to the user when storage is available.
     *
     * @param task The task to store.
     * @param tasks The user's task list.
     * @param storage Persistent task storage.
     */
    private static void addTask(Task task, TaskList tasks, Storage storage, Ui ui) {
        if (!tasks.addTask(task)) {
            ui.showTaskLimit(MAX_TASKS);
            return;
        }

        saveTasks(storage, tasks, ui);
        ui.showTaskAdded(task, tasks.getTaskCount());
    }

    /**
     * Marks a task as complete or incomplete using its position in the list.
     *
     * @param command The mark or unmark command entered by the user.
     * @param tasks The user's task list.
     * @param isDone Whether the task should be marked as completed.
     * @param storage Persistent task storage.
     */
    private static void markTask(String command, TaskList tasks, boolean isDone, Storage storage, Ui ui)
            throws NobException {
        Task task = tasks.markTask(Parser.parseTaskNumber(command), isDone);
        saveTasks(storage, tasks, ui);
        ui.showTaskMarked(task, isDone);
    }

    /**
     * Deletes a task from the list by its position.
     *
     * @param command The delete command entered by the user.
     * @param tasks The user's task list.
     * @param storage Persistent task storage.
     * @throws NobException If the task index is invalid.
     */
    private static void deleteTask(String command, TaskList tasks, Storage storage, Ui ui)
            throws NobException {
        Task removedTask = tasks.deleteTask(Parser.parseTaskNumber(command));
        saveTasks(storage, tasks, ui);
        ui.showTaskDeleted(removedTask, tasks.getTaskCount());
    }

    /**
     * Finds and displays tasks whose descriptions contain the requested keyword.
     *
     * @param command The find command entered by the user.
     * @param tasks The user's task list.
     * @param ui The console user interface.
     * @throws NobException If the keyword is missing.
     */
    private static void findTasks(String command, TaskList tasks, Ui ui) throws NobException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NobException("Use: find KEYWORD\n(eg., find book)");
        }
        ui.showMatchingTasks(tasks, keyword);
    }

    /** Saves tasks and reports if the file could not be updated. */
    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        if (!storage.saveTasks(tasks.getTasks(), tasks.getTaskCount())) {
            ui.showSavingError();
        }
    }
}
