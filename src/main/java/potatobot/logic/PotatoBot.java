package potatobot.logic;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import potatobot.exception.PotatoBotException;
import potatobot.logic.command.Command;
import potatobot.logic.command.UndoCommand;
import potatobot.logic.command.UndoableCommand;
import potatobot.logic.parser.Parser;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Provides PotatoBot's application operations to user interfaces.
 */
public class PotatoBot {
    private static final String SAVE_FILE_NAME = "./data/potatabot.txt";
    private static final String SAVE_FILE_ENVIRONMENT_VARIABLE = "POTATOBOT_SAVE_FILE";
    private static final int MAX_UNDO_HISTORY_SIZE = 5;

    private final TaskList tasks;
    private final Parser parser;
    private final Storage storage;
    private final String startupErrorMessage;
    private final Deque<UndoHistoryEntry> undoHistory = new ArrayDeque<>(MAX_UNDO_HISTORY_SIZE);
    private boolean isShutdown;

    /**
     * Creates a PotatoBot with its default parser, storage, and task list.
     */
    public PotatoBot() {
        this(new Parser(), new TaskList(), new Storage(
                System.getenv().getOrDefault(SAVE_FILE_ENVIRONMENT_VARIABLE, SAVE_FILE_NAME),
                SAVE_FILE_NAME));
    }

    /**
     * Creates a PotatoBot using the specified application collaborators.
     * This constructor supports deterministic tests and alternative front ends.
     *
     * @param parser  Parser used to interpret user input.
     * @param tasks   Task list managed by the application.
     * @param storage Storage used to load and save tasks.
     */
    public PotatoBot(Parser parser, TaskList tasks, Storage storage) {
        this.parser = parser;
        this.tasks = tasks;
        this.storage = storage;
        this.startupErrorMessage = loadTasks();
    }

    /**
     * Processes one line of user input without performing console input or output.
     *
     * @param input User command to process.
     * @return Result containing the response, whether it is an error, and whether
     *         the application should exit.
     */
    public CommandResult respondTo(String input) {
        try {
            Command command = parser.parse(input);

            // A parser must either return an executable command or throw for invalid input.
            assert command != null : "Successful parsing must produce a command";

            if (command instanceof UndoCommand) {
                return undoLastCommand();
            }

            CommandResult result = command.execute(tasks, storage);
            if (command instanceof UndoableCommand undoableCommand) {
                rememberCommand(input, undoableCommand);
            }
            if (result.isExit()) {
                shutdown();
            }
            return result;
        } catch (PotatoBotException exception) {
            return CommandResult.error(exception.getMessage());
        }
    }

    /**
     * Saves the current task list once before the application closes.
     * A failed save can be retried by calling this method again.
     *
     */
    public void shutdown() {
        if (isShutdown) {
            return;
        }

        try {
            storage.save(tasks);
            isShutdown = true;
        } catch (IOException exception) {
            // Leave the application active so a later shutdown call can retry the save.
        }
    }

    /**
     * Returns a message describing a startup loading failure, if one occurred.
     *
     * @return Startup error message, or {@code null} when loading succeeded.
     */
    public String getStartupErrorMessage() {
        return startupErrorMessage;
    }

    /**
     * Loads the saved task list when the application is created.
     * A missing save file represents a new user with an empty task list.
     *
     * @return A user-visible error message, or {@code null} when loading succeeds.
     */
    private String loadTasks() {
        try {
            tasks.add(storage.load().toArray(Task[]::new));
            return null;
        } catch (IOException | PotatoBotException exception) {
            return "I couldn't load your saved tasks: " + exception.getMessage();
        }
    }

    /**
     * Stores a successfully executed command, discarding the oldest entry when
     * the five-command limit is reached.
     *
     * @param input   Original user input that describes the command.
     * @param command Command that can reverse its task-list change.
     */
    private void rememberCommand(String input, UndoableCommand command) {
        if (undoHistory.size() == MAX_UNDO_HISTORY_SIZE) {
            undoHistory.removeFirst();
        }
        undoHistory.addLast(new UndoHistoryEntry(input, command));
    }

    /**
     * Reverses and removes the newest command in the session history.
     *
     * @return Result describing the reversed command or the empty history.
     * @throws PotatoBotException If the command's inverse cannot be completed.
     */
    private CommandResult undoLastCommand() throws PotatoBotException {
        if (undoHistory.isEmpty()) {
            return CommandResult.error(UndoCommand.EMPTY_HISTORY_MESSAGE);
        }

        UndoHistoryEntry historyEntry = undoHistory.getLast();
        historyEntry.command().undo(tasks);
        undoHistory.removeLast();
        return CommandResult.reply(UndoCommand.formatSuccessMessage(historyEntry.input()));
    }

    /**
     * Associates the original command input with the inverse operation needed
     * to undo it.
     *
     * @param input   Original command input.
     * @param command Reversible command that was executed.
     */
    private record UndoHistoryEntry(String input, UndoableCommand command) {
    }

}
