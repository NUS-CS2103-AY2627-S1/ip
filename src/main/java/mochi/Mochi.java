package mochi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import mochi.command.Command;
import mochi.exception.MochiException;
import mochi.parser.Parser;
import mochi.storage.Storage;
import mochi.task.Task;
import mochi.task.TaskList;
import mochi.ui.Ui;

/**
 * Coordinates Mochi's user interface, task list, parser, and storage.
 */
public class Mochi {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private final ByteArrayOutputStream responseBuffer;
    private final String startupError;
    private boolean hasResponseError;

    /**
     * Creates Mochi and loads tasks from the specified data file.
     *
     * @param filePath relative path of the task data file
     */
    public Mochi(Path filePath) {
        this(filePath, new Ui(), null);
    }

    /** Creates a console session with supplied streams for repeatable interaction tests. */
    Mochi(Path filePath, Ui ui) {
        this(filePath, ui, null);
    }

    private Mochi(Path filePath, Ui ui, ByteArrayOutputStream responseBuffer) {
        this.ui = ui;
        this.responseBuffer = responseBuffer;
        this.storage = new Storage(filePath);
        TaskList loadedTasks;
        String loadError = "";
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MochiException e) {
            loadError = e.getMessage() + " Your saved file has been left untouched. "
                    + "Back it up, repair it or move it aside, then restart Mochi.";
            ui.showError(loadError);
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        this.startupError = loadError;
    }

    /**
     * Creates a Mochi instance that returns responses to a graphical interface.
     *
     * @param filePath relative path of the task data file
     * @return Mochi configured for GUI interaction
     */
    public static Mochi forGui(Path filePath) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Mochi mochi = new Mochi(filePath, new Ui(new Scanner(""), output), buffer);
        buffer.reset();
        return mochi;
    }

    /**
     * Returns any startup storage problem for display in the graphical interface.
     *
     * @return error text, or an empty string when loading succeeded
     */
    public String getStartupError() {
        return startupError;
    }

    /**
     * Indicates whether the last GUI command failed, independently of its wording.
     *
     * @return true when the last response reports an error
     */
    public boolean hasResponseError() {
        return hasResponseError;
    }

    /**
     * Starts Mochi with its default relative data path.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        new Mochi(Path.of("data", "mochi.txt")).run();
    }

    /**
     * Reads and executes commands until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand().trim();
            Command command = Parser.parseCommand(input);
            ui.showLine();
            if (command == Command.BYE && input.equals(command.getKeyword())) {
                ui.showGoodbye();
                ui.showLine();
                break;
            }
            try {
                executeCommand(input, command);
            } catch (MochiException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Executes one command and returns its response without console separators.
     * This instance must have been created using {@link #forGui(Path)}.
     *
     * @param input command entered by the user
     * @return generated response
     */
    public String getResponse(String input) {
        assert responseBuffer != null : "GUI responses require an instance created with forGui";
        responseBuffer.reset();
        hasResponseError = false;
        input = input == null ? "" : input.trim();
        Command command = Parser.parseCommand(input);
        if (command == Command.BYE && input.equals(command.getKeyword())) {
            ui.showGoodbye();
        } else {
            try {
                executeCommand(input, command);
            } catch (MochiException e) {
                hasResponseError = true;
                ui.showError(e.getMessage());
            }
        }
        return responseBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Executes one non-exit command and persists any resulting task changes.
     *
     * @param input complete input entered by the user
     * @param command parsed command type
     * @throws MochiException if the command is invalid or storage fails
     */
    private void executeCommand(String input, Command command) throws MochiException {
        if (!startupError.isEmpty() && changesTasks(command)) {
            throw new MochiException(startupError);
        }
        // Apply changes to a copy. The visible list changes only after a successful save.
        TaskList updatedTasks = tasks.copy();
        switch (command) {
            case LIST:
                if (!input.equals(command.getKeyword())) {
                    throw new MochiException("The list command does not take extra details.");
                }
                ui.showTaskList(tasks);
                break;
            case SORT:
                if (!input.equals(command.getKeyword())) {
                    throw new MochiException("The sort command does not take extra details.");
                }
                updatedTasks.sortByDescription();
                saveChanges(updatedTasks);
                ui.showTaskList(tasks);
                break;
            case MARK:
                int markIndex = Parser.parseTaskIndex(input, command, tasks.size());
                Task markedTask = updatedTasks.get(markIndex);
                markedTask.markAsDone();
                saveChanges(updatedTasks);
                ui.showMarked(markedTask);
                break;
            case UNMARK:
                int unmarkIndex = Parser.parseTaskIndex(input, command, tasks.size());
                Task unmarkedTask = updatedTasks.get(unmarkIndex);
                unmarkedTask.markAsNotDone();
                saveChanges(updatedTasks);
                ui.showUnmarked(unmarkedTask);
                break;
            case DELETE:
                int deleteIndex = Parser.parseTaskIndex(input, command, tasks.size());
                Task removedTask = updatedTasks.remove(deleteIndex);
                saveChanges(updatedTasks);
                ui.showDeleted(removedTask, tasks.size());
                break;
            case FIND:
                String keyword = Parser.parseFindKeyword(input);
                ui.showMatchingTasks(tasks.find(keyword));
                break;
            case TODO:
            case DEADLINE:
            case EVENT:
                Task task = Parser.parseTask(input, command);
                updatedTasks.add(task);
                saveChanges(updatedTasks);
                ui.showAdded(task, tasks.size());
                break;
            case UNKNOWN:
                throw new MochiException("I don't know that command yet. Try todo, deadline, event, list, sort, find, "
                        + "mark, unmark, delete, or bye.");
            case BYE:
                throw new MochiException("The bye command does not take extra details.");
            default:
                throw new MochiException("I couldn't process that command.");
        }
    }

    /** Saves a candidate list before making it the active list. */
    private void saveChanges(TaskList updatedTasks) throws MochiException {
        storage.save(updatedTasks);
        tasks = updatedTasks;
    }

    /** Identifies commands that must not overwrite a file that failed to load. */
    private boolean changesTasks(Command command) {
        return switch (command) {
            case TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, SORT -> true;
            default -> false;
        };
    }
}
