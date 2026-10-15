package jeff;

import java.util.Optional;
import java.util.function.IntFunction;

import jeff.parser.Parser;
import jeff.storage.Storage;
import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.TaskList;
import jeff.task.Todo;
import jeff.ui.Ui;

/**
 * The Jeff chatbot. Wires together {@link Ui}, {@link Storage}, and {@link TaskList},
 * and drives the read-parse-execute cycle that reads a command from the user, asks
 * {@link Parser} to make sense of it, and acts on it. An instance owns one
 * conversation's worth of state, so tests can create one against a throwaway
 * {@link Storage} instead of touching the real save file.
 */
public class Jeff {
    /** Where {@link #main} and the JavaFX GUI persist tasks between runs. */
    public static final String DEFAULT_SAVE_FILE_PATH = "./data/jeff.txt";

    /** Every task the user has added so far, in order. */
    private final TaskList taskList;

    /** Persists {@code taskList} to, and restores it from, disk. */
    private final Storage storage;

    /** Handles all printing to, and reading from, the user. */
    private final Ui ui = new Ui();

    /** Creates a Jeff that persists its tasks via the given {@link Storage}, loading any it already has. */
    public Jeff(Storage storage) {
        this.storage = storage;
        this.taskList = new TaskList(storage.load());
    }

    /** Stores the given task in {@code taskList} and returns the "added" confirmation. */
    private String addTask(Task task) {
        taskList.add(task);
        String response = ui.formatTaskAdded(task, taskList.size());
        return withSaveWarning(response);
    }

    /**
     * Saves {@code taskList} to disk and, if that fails (e.g. the disk is full or its
     * permissions changed), appends a warning to {@code response} so the user finds out
     * their change was not actually persisted instead of it only reaching a console
     * nobody may be looking at (e.g. when running the GUI).
     */
    private String withSaveWarning(String response) {
        Optional<String> saveError = storage.save(taskList.getTasks());
        return saveError.map(error -> response + "\n" + ui.formatError(error)).orElse(response);
    }

    /**
     * Resolves {@code arguments} to a zero-based task index and runs {@code command} on it,
     * translating the two ways resolution can fail (a malformed or an out-of-range task
     * number) into one of Jeff's usual "OOPS!!!"-prefixed error messages. Shared by the
     * "mark", "unmark", and "delete" commands, which differ only in what they do with the
     * resolved index and in {@code actionWord}, the word naming them in the
     * malformed-number error message.
     */
    private String executeTaskCommand(String arguments, String actionWord, IntFunction<String> command) {
        try {
            return command.apply(Parser.parseTaskIndex(arguments, taskList.size()));
        } catch (NumberFormatException e) {
            return ui.formatError("Please provide a valid task number to " + actionWord + ".");
        } catch (IndexOutOfBoundsException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Returns the greeting message shown when Jeff starts up, without the console's
     * ASCII banner. If {@code storage} had trouble loading the saved tasks (e.g. the
     * file was unreadable, or some of its lines were corrupted), a warning about that
     * is appended so the user isn't left wondering where their tasks went.
     */
    public String getWelcomeMessage() {
        String greeting = ui.formatGreeting();
        return storage.getLastLoadWarning()
                .map(warning -> greeting + "\n" + ui.formatError(warning))
                .orElse(greeting);
    }

    /**
     * Parses and executes a single line of user input, updating {@code taskList}
     * and {@code storage} as needed, and returns Jeff's response text. Shared by
     * both the console UI in {@link #main} and the JavaFX GUI.
     *
     * @param input a raw line of user input, e.g. {@code "todo read book"}.
     * @return the response text Jeff should show the user.
     */
    public String getResponse(String input) {
        Parser.Command command = Parser.parseCommandType(input);
        String arguments = Parser.getArguments(input);

        switch (command) {
            case BYE:
                return ui.formatBye();
            case LIST:
                return ui.formatTaskList(taskList.getTasks());
            case MARK:
                return handleMark(arguments);
            case UNMARK:
                return handleUnmark(arguments);
            case DELETE:
                return handleDelete(arguments);
            case TODO:
                return handleTodo(arguments);
            case DEADLINE:
                return handleDeadline(arguments);
            case EVENT:
                return handleEvent(arguments);
            case FIND:
                return handleFind(arguments);
            case SORT:
                return handleSort();
            default:
                return ui.formatError("I'm sorry, but I don't know what that command means.");
        }
    }

    /** Marks the task named by {@code arguments} as done and returns the confirmation. */
    private String handleMark(String arguments) {
        return executeTaskCommand(arguments, "mark", index -> {
            Task task = taskList.get(index);
            task.markAsDone();
            return withSaveWarning(ui.formatTaskMarked(task));
        });
    }

    /** Marks the task named by {@code arguments} as not done and returns the confirmation. */
    private String handleUnmark(String arguments) {
        return executeTaskCommand(arguments, "unmark", index -> {
            Task task = taskList.get(index);
            task.unmarkAsDone();
            return withSaveWarning(ui.formatTaskUnmarked(task));
        });
    }

    /** Removes the task named by {@code arguments} from {@code taskList} and returns the confirmation. */
    private String handleDelete(String arguments) {
        return executeTaskCommand(arguments, "delete", index -> {
            Task task = taskList.remove(index);
            return withSaveWarning(ui.formatTaskRemoved(task, taskList.size()));
        });
    }

    /** Adds a {@link Todo} built from {@code arguments} and returns the "added" confirmation. */
    private String handleTodo(String arguments) {
        try {
            return addTask(new Todo(arguments));
        } catch (IllegalArgumentException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /** Adds a {@link Deadline} built from {@code arguments} and returns the "added" confirmation. */
    private String handleDeadline(String arguments) {
        try {
            String[] parts = Parser.splitDeadlineArgs(arguments);
            return addTask(new Deadline(parts[0], parts[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            return ui.formatError("A deadline needs a description and a /by date/time.");
        } catch (IllegalArgumentException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /** Adds an {@link Event} built from {@code arguments} and returns the "added" confirmation. */
    private String handleEvent(String arguments) {
        try {
            String[] parts = Parser.splitEventArgs(arguments);
            return addTask(new Event(parts[0], parts[1], parts[2]));
        } catch (ArrayIndexOutOfBoundsException e) {
            return ui.formatError("An event needs a description, a /from and a /to date/time.");
        } catch (IllegalArgumentException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /** Returns the tasks whose description contains the keyword in {@code arguments}. */
    private String handleFind(String arguments) {
        if (arguments.isEmpty()) {
            return ui.formatError("Please provide a keyword to search for.");
        }
        return ui.formatMatchingTasks(taskList.find(arguments));
    }

    /** Sorts {@code taskList} chronologically by date and returns the resulting list. */
    private String handleSort() {
        taskList.sortByDate();
        return withSaveWarning(ui.formatSortedTasks(taskList.getTasks()));
    }

    /**
     * Greets the user, then repeatedly reads and executes commands until a
     * "bye" command or end of input is reached.
     */
    private void run() {
        ui.showWelcome(getWelcomeMessage());

        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                ui.showBye();
                ui.showLine();
                break;
            }
            ui.showLine();
            ui.showMessage(getResponse(input));
            ui.showLine();
            if (Parser.parseCommandType(input) == Parser.Command.BYE) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Starts a console-driven Jeff that persists to {@link #DEFAULT_SAVE_FILE_PATH}.
     *
     * @param args unused; Jeff takes no command-line arguments.
     */
    public static void main(String[] args) {
        new Jeff(new Storage(DEFAULT_SAVE_FILE_PATH)).run();
    }
}
