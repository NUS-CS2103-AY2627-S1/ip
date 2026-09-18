package rene;

import java.nio.file.Path;

import rene.command.CommandType;
import rene.command.ParsedCommand;
import rene.command.Parser;
import rene.exception.ReneException;
import rene.storage.Storage;
import rene.task.Task;
import rene.task.TaskList;
import rene.ui.Ui;

/**
 * Starts and coordinates the Rene chatbot application.
 */
public class Rene {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "rene.txt");
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "That falls outside my scope of operations. Standard procedures are: todo, deadline, "
                    + "event, list, mark, unmark, delete, find, help, or bye.";

    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private final String loadingError;
    private final boolean isStorageWriteBlocked;

    /**
     * Creates a Rene application backed by the default task data file.
     */
    public Rene() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a Rene application backed by the specified task data file.
     *
     * @param dataFile the path of the task data file.
     */
    public Rene(Path dataFile) {
        parser = new Parser();
        storage = new Storage(dataFile);
        ui = new Ui();

        TaskList loadedTasks;
        String encounteredLoadingError = null;
        boolean shouldBlockStorageWrites = false;
        try {
            Storage.LoadResult loadResult = storage.loadTasks();
            loadedTasks = new TaskList(loadResult.tasks());
            if (!loadResult.warnings().isEmpty()) {
                encounteredLoadingError = String.join("\n Apologies — ", loadResult.warnings())
                        + "\nChanges are disabled to protect the original data file. Fix or move the file, "
                        + "then restart Rene.";
                shouldBlockStorageWrites = true;
            }
        } catch (ReneException exception) {
            encounteredLoadingError = exception.getMessage();
            loadedTasks = new TaskList();
            shouldBlockStorageWrites = true;
        }
        tasks = loadedTasks;
        loadingError = encounteredLoadingError;
        isStorageWriteBlocked = shouldBlockStorageWrites;
    }

    /**
     * Processes commands until the user exits or standard input ends.
     */
    public void run() {
        ui.showWelcome(loadingError);
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showDivider();
            try {
                ParsedCommand command = parser.parse(input);
                if (command.type() == CommandType.BYE && command.argument().isEmpty()) {
                    ui.showGoodbye();
                    ui.showDivider();
                    break;
                }
                ui.showResponse(execute(command));
            } catch (ReneException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showDivider();
        }
    }

    /**
     * Starts Rene using the default data file or an optional override.
     *
     * @param args an optional first argument that overrides the task data file path.
     */
    public static void main(String[] args) {
        Path dataFile = args.length == 0 ? DEFAULT_DATA_FILE : Path.of(args[0]);
        new Rene(dataFile).run();
    }

    /**
     * Processes one command and returns Rene's response for a graphical UI.
     *
     * @param input the complete command entered by the user.
     * @return Rene's response, including a friendly error for invalid input.
     */
    public String getResponse(String input) {
        try {
            ParsedCommand command = parser.parse(input);
            if (command.type() == CommandType.BYE && command.argument().isEmpty()) {
                return ui.formatGoodbye();
            }
            return execute(command);
        } catch (ReneException exception) {
            return ui.formatError(exception.getMessage());
        }
    }

    /**
     * Returns Rene's greeting and any error encountered while loading saved tasks.
     *
     * @return the greeting to show when a graphical UI opens.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcome(loadingError);
    }

    /**
     * Applies a parsed command to the task list.
     */
    private String execute(ParsedCommand command) throws ReneException {
        // Only successfully parsed commands reach the command dispatcher.
        assert command != null : "Parsed command must not be null";

        return switch (command.type()) {
            case LIST -> ui.formatTasks(tasks.getTasks());
            case MARK -> markTask(command);
            case UNMARK -> unmarkTask(command);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case HELP -> ui.formatHelp();
            case TODO, DEADLINE, EVENT -> addTask(parser.parseTask(command));
            case BYE -> throw new ReneException(UNKNOWN_COMMAND_MESSAGE);
            default -> throw new ReneException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    /**
     * Adds and persists a task before displaying confirmation.
     */
    private String addTask(Task task) throws ReneException {
        TaskList updatedTasks = tasks.copy();
        updatedTasks.add(task);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.formatTaskAdded(task, updatedTasks.size());
    }

    /**
     * Marks and persists a task before displaying confirmation.
     */
    private String markTask(ParsedCommand command) throws ReneException {
        TaskList updatedTasks = tasks.copy();
        Task task = updatedTasks.mark(parser.parseTaskNumber(command));
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.formatTaskMarked(task);
    }

    /**
     * Unmarks and persists a task before displaying confirmation.
     */
    private String unmarkTask(ParsedCommand command) throws ReneException {
        TaskList updatedTasks = tasks.copy();
        Task task = updatedTasks.unmark(parser.parseTaskNumber(command));
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.formatTaskUnmarked(task);
    }

    /**
     * Deletes and persists a task before displaying confirmation.
     */
    private String deleteTask(ParsedCommand command) throws ReneException {
        TaskList updatedTasks = tasks.copy();
        Task task = updatedTasks.remove(parser.parseTaskNumber(command));
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.formatTaskDeleted(task, updatedTasks.size(), !updatedTasks.isEmpty());
    }

    /**
     * Displays tasks whose descriptions contain the supplied keyword.
     *
     * @param command the find command containing the search keyword.
     * @throws ReneException if the command does not contain a keyword.
     */
    private String findTasks(ParsedCommand command) throws ReneException {
        if (command.argument().isEmpty()) {
            throw new ReneException("A find directive requires a keyword. Try: find book");
        }
        return ui.formatMatchingTasks(tasks.find(command.argument()));
    }

    /**
     * Saves a snapshot of the current task list.
     */
    private void saveTasks(TaskList updatedTasks) throws ReneException {
        if (isStorageWriteBlocked) {
            throw new ReneException("Changes are disabled because the data file did not load completely. "
                    + "Fix or move it, then restart Rene.");
        }
        storage.saveTasks(updatedTasks.getTasks());
    }
}
