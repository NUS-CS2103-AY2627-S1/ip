package nori;

import java.nio.file.Path;

import nori.parser.CommandType;
import nori.parser.Parser;
import nori.storage.Storage;
import nori.task.Task;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Coordinates Nori's user interface, command parsing, task list, and storage.
 */
public class Nori {
    private static final Path DATA_FILE_PATH = Path.of("data", "nori.txt");

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;

    /** Creates Nori with its command-line interface and default data file. */
    public Nori() {
        this(DATA_FILE_PATH);
    }

    /** Creates Nori with a command-line interface and the given data file. */
    Nori(Path dataFilePath) {
        this.ui = new Ui();
        this.storage = new Storage(dataFilePath);
        this.parser = new Parser();
    }

    /** Starts Nori and processes commands until the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = parser.parseCommandType(command);
            if (commandType == CommandType.BYE) {
                break;
            }
            ui.showResponse(getResponse(command));
            ui.showDivider();
        }

        ui.showGoodbye();
    }

    /**
     * Executes a command and returns Nori's response for display by any user interface.
     *
     * @param command complete user command.
     * @return response produced by the command
     */
    public String getResponse(String command) {
        return getReply(command).message();
    }

    /**
     * Executes a command, retaining its outcome for accessible GUI error feedback.
     * Failed commands discard cached tasks so an unsaved change cannot appear successful later.
     *
     * @param command complete user command.
     * @return response text and whether an error occurred
     */
    public Reply getReply(String command) {
        String trimmedCommand = command.trim();
        CommandType commandType = parser.parseCommandType(trimmedCommand);
        try {
            // Help and exit remain available even if the data file needs repair.
            if (tasks == null && needsTasks(commandType)) {
                tasks = storage.load();
            }
            return new Reply(executeCommand(trimmedCommand, commandType), false);
        } catch (NoriException exception) {
            tasks = null;
            return new Reply(ui.formatError(exception.getMessage()), true);
        }
    }

    /** Returns whether the command needs access to persisted tasks. */
    private boolean needsTasks(CommandType commandType) {
        return switch (commandType) {
            case HELP, BYE, EMPTY, UNKNOWN -> false;
            default -> true;
        };
    }

    /** Returns the welcome message for graphical interfaces. */
    public String getWelcomeMessage() {
        return ui.formatInlineWelcome();
    }

    /** Executes one parsed user command and returns its response. */
    private String executeCommand(String command, CommandType commandType) throws NoriException {
        return switch (commandType) {
            case EMPTY -> throw new NoriException("Please enter a command.");
            case LIST -> ui.formatTaskList(tasks);
            case MARK -> updateTaskStatus(command, true);
            case UNMARK -> updateTaskStatus(command, false);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case HELP -> ui.formatHelp();
            case TODO, DEADLINE, EVENT -> addTask(command);
            case UNKNOWN -> throw new NoriException("I don't know that command.");
            case BYE -> ui.formatGoodbye();
        };
    }

    /** Adds a task described by a user command and persists the updated list. */
    private String addTask(String command) throws NoriException {
        Task task = parser.parseTask(command);
        tasks.add(task);
        storage.save(tasks);
        return ui.formatTaskAdded(task, tasks.size());
    }

    /** Deletes the selected task and persists the updated list. */
    private String deleteTask(String command) throws NoriException {
        int taskIndex = parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks);
        return ui.formatTaskDeleted(removedTask, tasks.size());
    }

    /** Displays tasks whose descriptions contain the requested keyword. */
    private String findTasks(String command) throws NoriException {
        String keyword = parser.parseFindKeyword(command);
        return ui.formatMatchingTasks(tasks.find(keyword));
    }

    /** Changes a task's completion state and persists the updated list. */
    private String updateTaskStatus(String command, boolean isDone) throws NoriException {
        String commandWord = isDone ? "mark" : "unmark";
        int taskIndex = parser.parseTaskIndex(command, commandWord, tasks.size());
        Task task = isDone ? tasks.mark(taskIndex) : tasks.unmark(taskIndex);
        storage.save(tasks);
        return ui.formatTaskStatusChanged(task, isDone);
    }

    /**
     * Starts a command-line Nori session.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Nori().run();
    }
}
