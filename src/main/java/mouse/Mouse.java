package mouse;

import java.util.function.Function;

import mouse.parser.CommandType;
import mouse.parser.Parser;
import mouse.parser.PriorityCommand;
import mouse.storage.Storage;
import mouse.task.Task;
import mouse.task.TaskList;
import mouse.ui.Ui;

/**
 * Entry point for the Mouse chatbot.
 * Coordinates the UI, parser, storage, and task list.
 */
public class Mouse {
    public static final String DEFAULT_SAVE_PATH = "data/mouse.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a Mouse session that saves to {@link #DEFAULT_SAVE_PATH}.
     */
    public Mouse() {
        this(DEFAULT_SAVE_PATH);
    }

    /**
     * Creates a Mouse session that loads and saves at {@code filePath}.
     *
     * @param filePath Path to the save file.
     */
    public Mouse(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = storage.load();
    }

    /**
     * Starts the chatbot from the command line.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Mouse().run();
    }

    /**
     * Returns the GUI greeting.
     *
     * @return Greeting text for a chat bubble.
     */
    public String getGreeting() {
        String greeting = ui.formatGreeting();
        String warning = storage.getLoadWarning();
        if (warning == null) {
            return greeting;
        }
        return greeting + "\n" + warning;
    }

    /**
     * Returns whether {@code input} is the exit command.
     *
     * @param input Raw user input.
     * @return {@code true} if the session should end.
     */
    public boolean isExit(String input) {
        return input.trim().equalsIgnoreCase("bye");
    }

    /**
     * Returns whether {@code reply} is an error message for the GUI.
     *
     * @param reply Reply text from {@link #getResponse(String)}.
     * @return {@code true} if the reply should use the error bubble.
     */
    public boolean isErrorResponse(String reply) {
        return ui.isErrorMessage(reply);
    }

    /**
     * Returns Mouse's reply for one command, for the GUI.
     *
     * @param input Raw user input.
     * @return Reply text to show in a dialog bubble.
     */
    public String getResponse(String input) {
        return execute(input.trim());
    }

    /**
     * Greets the user, then handles commands until {@code bye}.
     */
    public void run() {
        ui.showGreeting();
        String warning = storage.getLoadWarning();
        if (warning != null) {
            ui.showReply(warning);
        }
        boolean isExit = false;
        while (!isExit) {
            isExit = handleCommand(ui.readCommand());
        }
    }

    /**
     * Handles one raw command line.
     * Returns {@code true} when the session should end.
     *
     * @param input Raw user input.
     * @return Whether the chatbot should exit.
     */
    private boolean handleCommand(String input) {
        String trimmed = input.trim();
        ui.showReply(execute(trimmed).split("\n", -1));
        return isExit(trimmed);
    }

    /**
     * Executes one command and returns the reply text.
     * Shared by the CLI loop and the GUI.
     *
     * @param input Trimmed user input.
     * @return Reply text to show the user.
     */
    private String execute(String input) {
        try {
            return executeCommand(input);
        } catch (MouseException exception) {
            return ui.formatError(exception.getMessage());
        } catch (IndexOutOfBoundsException exception) {
            return ui.formatError("That crumb is not in the stash GRR");
        }
    }

    /**
     * Routes {@code input} to the matching command.
     *
     * @param input Trimmed user input.
     * @return Reply text to show the user.
     * @throws MouseException If the command is invalid.
     */
    private String executeCommand(String input) throws MouseException {
        switch (CommandType.fromInput(input)) {
        case BYE:
            Parser.assertBareCommand(input, "bye");
            return ui.formatBye();
        case LIST:
            Parser.assertBareCommand(input, "list");
            return ui.formatList(tasks);
        case MARK:
            return changeTask(tasks.mark(Parser.parseIndex(input, "mark")), ui::formatMarked);
        case UNMARK:
            return changeTask(tasks.unmark(Parser.parseIndex(input, "unmark")), ui::formatUnmarked);
        case DELETE:
            Task deleted = tasks.delete(Parser.parseIndex(input, "delete"));
            storage.save(tasks);
            return ui.formatDeleted(deleted, tasks.size());
        case TODO:
            return addTask(Parser.parseTodo(input));
        case DEADLINE:
            return addTask(Parser.parseDeadline(input));
        case EVENT:
            return addTask(Parser.parseEvent(input));
        case FIND:
            return ui.formatFind(tasks, tasks.find(Parser.parseFind(input)));
        case HELP:
            Parser.assertBareCommand(input, "help");
            return ui.formatHelp();
        case PRIORITY:
            PriorityCommand priorityCommand = Parser.parsePriority(input);
            return changeTask(tasks.setPriority(
                    priorityCommand.getIndex(), priorityCommand.getPriority()),
                    ui::formatPriority);
        case UNKNOWN:
            throw new MouseException("Mouse no understand. Give cheese (or a real command).");
        default:
            throw new MouseException("Mouse no understand. Give cheese (or a real command).");
        }
    }

    /**
     * Adds {@code task} to the stash, saves, and returns the confirmation.
     *
     * @param task Task to add.
     * @return Confirmation text.
     * @throws MouseException If the crumb is a duplicate or cannot be saved.
     */
    private String addTask(Task task) throws MouseException {
        Task added = tasks.add(task);
        storage.save(tasks);
        return ui.formatAdded(added, tasks.size());
    }

    /**
     * Saves after a mark, unmark, or priority change and formats the reply.
     *
     * @param task Updated task.
     * @param formatter Builds the reply from {@code task}.
     * @return Formatted confirmation.
     * @throws MouseException If the stash cannot be saved.
     */
    private String changeTask(Task task, Function<Task, String> formatter) throws MouseException {
        storage.save(tasks);
        return formatter.apply(task);
    }
}
