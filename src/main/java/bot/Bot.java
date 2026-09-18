package bot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bot.exception.BotException;
import bot.parser.Parser;
import bot.storage.Storage;
import bot.task.Task;
import bot.task.TaskList;
import bot.ui.Ui;

/**
 * Orchestrates the chatbot. Bot itself holds no command-handling logic:
 * it wires together a {@link Ui} (console interaction), a
 * {@link Storage} (loading/saving the data file), a {@link TaskList}
 * (the tasks and operations on them), and a {@link Parser} (interpreting
 * each command line), and its {@link #run()} loop just calls each of
 * them in turn.
 */
public class Bot {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /** The saved-task load failure to report once run() starts, or null if loading didn't fail outright. */
    private final String loadErrorMessage;

    /** Any saved-task lines that couldn't be parsed, to report once run() starts; empty if none. */
    private final List<String> loadWarnings;

    /**
     * Sets up a Bot backed by the data file at {@code filePath}, loading
     * whatever tasks are already saved there (or starting with an empty
     * list if the file doesn't exist yet). Building a Bot has no visible
     * effect on the console by itself - any load failure or corrupted
     * line found here is only reported once {@link #run()} starts, so
     * construction and the order things are printed in stay independent
     * of each other.
     */
    public Bot(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        List<Task> loaded;
        List<String> warnings = new ArrayList<>();
        String errorMessage = null;
        try {
            loaded = storage.load(warnings);
        } catch (BotException e) {
            errorMessage = e.getMessage();
            loaded = new ArrayList<>();
        }
        loadErrorMessage = errorMessage;
        loadWarnings = warnings;
        tasks = new TaskList(loaded);
    }

    /** Greets the user, reports any loading trouble, processes commands until "bye", then says goodbye. */
    public void run() {
        ui.showWelcome();
        if (loadErrorMessage != null) {
            ui.showLoadingError(loadErrorMessage);
        }
        ui.showLoadWarnings(loadWarnings);
        runCommandLoop();
        ui.showFarewell();
        ui.close();
    }

    /**
     * Reads one line of user input at a time and prints its response,
     * until the user types "bye".
     */
    private void runCommandLoop() {
        String input = ui.readCommand();
        while (!isExitCommand(input)) {
            ui.showLine();
            System.out.println(getResponse(input));
            ui.showLine();
            input = ui.readCommand();
        }
    }

    /**
     * Returns whether {@code input} is the command that ends the session,
     * so a caller (console loop or GUI) knows when to stop.
     */
    public boolean isExitCommand(String input) {
        return input != null && input.trim().equalsIgnoreCase("bye");
    }

    /** Returns the closing message shown for the {@code bye} command, for the GUI to display. */
    public String getFarewellMessage() {
        return ui.formatFarewell();
    }

    /** Returns the greeting shown at startup, for the GUI to display. */
    public String getGreetingMessage() {
        return ui.formatGreeting();
    }

    /**
     * Parses one line of user input into a command word and its argument
     * text (via {@link Parser}), carries out whichever command it names
     * against the task list, and returns the resulting response text. Any
     * problem with the command (unrecognized word, missing/malformed
     * argument, bad task number) is returned as an "OOPS!!!" message
     * instead of propagating further. Used by both the console loop and
     * the GUI, so the two show identical wording for the same input.
     */
    public String getResponse(String input) {
        // Both callers of getResponse - the console loop's Scanner.nextLine()
        // and the GUI's TextField.getText() - always return a non-null
        // String (empty at most, never null), and neither passes the "bye"
        // input through to here (the console loop stops before calling it;
        // the GUI checks isExitCommand first). A null input here would mean
        // a future caller broke that assumption.
        assert input != null : "input must not be null";
        Parser.ParsedCommand command = Parser.parseCommand(input);
        String commandWord = command.commandWord();
        String rest = command.arguments();

        try {
            switch (commandWord) {
                case "list":
                    return ui.formatTaskList(tasks.asList());
                case "mark": {
                    int index = Parser.parseTaskIndex(rest, "mark", tasks.size());
                    tasks.mark(index);
                    return withSaveResult(ui.formatMarked(tasks.get(index)));
                }
                case "unmark": {
                    int index = Parser.parseTaskIndex(rest, "unmark", tasks.size());
                    tasks.unmark(index);
                    return withSaveResult(ui.formatUnmarked(tasks.get(index)));
                }
                case "delete": {
                    int index = Parser.parseTaskIndex(rest, "delete", tasks.size());
                    Task removed = tasks.delete(index);
                    return withSaveResult(ui.formatRemoved(removed, tasks.size()));
                }
                case "todo":
                    return addTask(Parser.parseTodo(rest));
                case "deadline":
                    return addTask(Parser.parseDeadline(rest));
                case "event":
                    return addTask(Parser.parseEvent(rest));
                case "on": {
                    LocalDate date = Parser.parseOnDate(rest);
                    return ui.formatTasksOnDate(date, tasks.tasksOn(date));
                }
                case "find": {
                    String keyword = Parser.parseFindKeyword(rest);
                    return ui.formatMatchingTasks(tasks.findByKeyword(keyword));
                }
                case "sort":
                    tasks.sort();
                    return withSaveResult(ui.formatSorted(tasks.asList()));
                default:
                    throw new BotException(
                            "OOPS!!! I don't understand \"" + commandWord
                                    + "\" - try list, todo, deadline, event, mark, unmark, delete, on, find, sort, "
                                    + "or bye.");
            }
        } catch (BotException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Adds a newly-parsed task to the list, persists the change, and
     * returns the resulting confirmation message. Shared by the todo,
     * deadline, and event cases in {@link #getResponse}, which otherwise
     * differed only in which Parser method produced the task.
     */
    private String addTask(Task newTask) throws BotException {
        if (tasks.containsEquivalent(newTask)) {
            throw new BotException("OOPS!!! That task is already in your list.");
        }
        tasks.add(newTask);
        return withSaveResult(ui.formatAdded(newTask, tasks.size()));
    }

    /**
     * Persists the current task list to disk, so the change just made
     * survives a restart, and appends any save failure to {@code response}.
     * Saving is best-effort: if it fails (e.g. the disk is full or the
     * data folder isn't writable), the user is told but the in-memory task
     * list is left as-is rather than crashing.
     */
    private String withSaveResult(String response) {
        try {
            storage.save(tasks.asList());
            return response;
        } catch (BotException e) {
            return response + "\n" + ui.formatError(e.getMessage());
        }
    }

    /**
     * Starts Bot, backed by the data file at {@code data/bot.txt}.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Bot("data/bot.txt").run();
    }
}
