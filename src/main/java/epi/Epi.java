package epi;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import epi.exception.EpiException;
import epi.parser.Parser;
import epi.storage.Storage;
import epi.task.Task;
import epi.task.TaskList;
import epi.ui.ConsoleUi;

/** Coordinates Epi's user interface, command parsing, task list, and storage. */
public class Epi {
    private final ConsoleUi ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;

    /**
     * Loads tasks from the supplied path and displays the command-line greeting and any loading warnings.
     *
     * @param filePath Non-null task-file path, resolved against the working directory if relative.
     */
    public Epi(String filePath) {
        this(filePath, true);
    }

    /**
     * Loads tasks and creates an Epi instance, optionally displaying the command-line greeting.
     * Loading problems are retained as warnings rather than propagated as user-facing exceptions.
     *
     * @param filePath Non-null task-file path, resolved against the working directory if relative.
     * @param showBanner Whether to print the banner, greeting, and loading warnings to the console.
     * @see #getLoadingWarnings()
     */
    public Epi(String filePath, boolean showBanner) {
        ui = new ConsoleUi();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = storage.load();
        if (!showBanner) {
            return;
        }
        String banner = "  ______       _ \n"
                + " |  ____|     (_)\n"
                + " | |__   _ __  _ \n"
                + " |  __| | '_ \\| |\n"
                + " | |____| |_) | |\n"
                + " |______| .__/|_|\n"
                + "        | |      \n"
                + "        |_|      \n";
        ui.showLine(banner);
        ui.showLine("Meowdy! I'm Epi");
        ui.showLine("Are you ready to tackle some purr-fectly good tasks today?");
        getLoadingWarnings().forEach(ui::showLine);
    }

    /**
     * Processes one command and returns the lines that should be shown to a user interface.
     * Expected command and storage errors are returned as messages rather than thrown.
     *
     * @param input Non-null user command.
     * @return Immutable response lines in display order, without a separate error flag.
     * @see #processCommandResult(String)
     */
    public List<String> processCommand(String input) {
        return processCommandResult(input).lines();
    }

    /**
     * Returns startup warnings so either interface can display loading problems.
     *
     * @return An immutable list of loading warnings, empty if loading succeeded without warnings.
     */
    public List<String> getLoadingWarnings() {
        return storage.getWarnings();
    }

    /**
     * Processes a command and converts failures into responses shared by the CLI and GUI.
     *
     * @param input Non-null user command.
     * @return Immutable reply lines and an explicit error flag, independent of their wording.
     *     Empty search results and unchanged completion states are informational, not errors.
     * @throws AssertionError If the input is null and assertions are enabled.
     */
    public CommandResult processCommandResult(String input) {
        assert input != null : "A command must be provided to Epi";
        try {
            return new CommandResult(executeCommand(input), false);
        } catch (EpiException e) {
            return new CommandResult(List.of(e.getMessage()), true);
        } catch (NumberFormatException e) {
            return new CommandResult(List.of("That is not a valid number"), true);
        } catch (DateTimeException e) {
            return new CommandResult(List.of(
                    "Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)"), true);
        } catch (IllegalArgumentException e) {
            return new CommandResult(List.of(e.getMessage()), true);
        }
    }

    /**
     * Parses the command word and delegates to its handler without formatting replies here.
     *
     * @param input Non-null command text to parse and execute.
     * @return The selected handler's reply lines in display order.
     * @throws EpiException If the command is unknown, has invalid arguments, or cannot be safely saved.
     * @throws NumberFormatException If a task number cannot be parsed as an {@code int}.
     * @throws DateTimeException If a task date/time has an invalid format or value.
     * @throws IllegalArgumentException If a task description or event range is invalid.
     */
    private List<String> executeCommand(String input) throws EpiException {
        String[] commandParts = parser.parseInput(input);
        assert commandParts.length > 0 : "The parser must return a command part";
        String command = commandParts[0].toLowerCase(Locale.ROOT);
        String argument = commandParts.length > 1 ? commandParts[1] : "";
        return switch (command) {
            case "bye" -> handleBye(argument);
            case "list" -> handleList(argument);
            case "sort" -> handleSort(argument);
            case "find" -> handleFind(argument);
            case "delete" -> handleDelete(argument);
            case "mark" -> handleStatusChange(argument, true);
            case "unmark" -> handleStatusChange(argument, false);
            case "todo" -> handleTodo(argument);
            case "deadline" -> handleDeadline(argument);
            case "event" -> handleEvent(argument);
            default -> throw new EpiException("I do not understand what that means, Human.");
        };
    }

    /**
     * Validates a farewell command; the calling interface decides whether to exit.
     *
     * @param argument Text following {@code bye}, which must be blank.
     * @return The farewell message as a single response line.
     * @throws EpiException If an argument was supplied.
     */
    private List<String> handleBye(String argument) throws EpiException {
        requireNoArgument("bye", argument);
        return List.of("Meow for now. See you later!");
    }

    /**
     * Lists every task in its original order after validating the command.
     *
     * @param argument Text following {@code list}, which must be blank.
     * @return A heading followed by tasks with their one-based full-list numbers.
     * @throws EpiException If an argument was supplied or the task list is empty.
     */
    private List<String> handleList(String argument) throws EpiException {
        requireNoArgument("list", argument);
        requireTasks();
        List<String> output = new ArrayList<>();
        output.add("Here is your pile of tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            output.add((i + 1) + ". " + tasks.get(i));
        }
        return output;
    }

    /**
     * Displays tasks chronologically without changing the saved order or task numbers.
     *
     * @param argument Sort key, which must be {@code date}, ignoring case and surrounding whitespace.
     * @return A heading followed by tasks in earliest-first order, with undated tasks last.
     * @throws EpiException If the sort key is unsupported or the task list is empty.
     */
    private List<String> handleSort(String argument) throws EpiException {
        if (!argument.trim().equalsIgnoreCase("date")) {
            throw new EpiException("Meow! Use: sort date");
        }
        requireTasks();
        return formatTaskView("Here is your pile of tasks, sorted by date (original task numbers):",
                tasks.getSortedByDate());
    }

    /**
     * Searches descriptions and reports a normal informational reply when nothing matches.
     *
     * @param argument Nonblank search text, trimmed before matching.
     * @return Matching tasks with full-list numbers, or a no-matches message.
     * @throws EpiException If the search text is blank.
     */
    private List<String> handleFind(String argument) throws EpiException {
        String keyword = argument.trim();
        if (keyword.isEmpty()) {
            throw new EpiException("Please provide a keyword to search for.");
        }
        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return List.of("Meow! I couldn't find any tasks matching \"" + keyword + "\".");
        }
        return formatTaskView("Here are the matching tasks in your list:", matches);
    }

    /**
     * Formats a filtered or sorted view using each task's original full-list number.
     *
     * @param heading Introductory line for the view.
     * @param displayedTasks Existing task objects from the full list, in the desired display order.
     * @return A new list containing the heading and numbered task descriptions.
     */
    private List<String> formatTaskView(String heading, List<Task> displayedTasks) {
        List<String> output = new ArrayList<>();
        output.add(heading);
        for (Task task : displayedTasks) {
            output.add((tasks.getIndex(task) + 1) + ". " + task);
        }
        return output;
    }

    /**
     * Removes a task and confirms the new count only after saving succeeds.
     *
     * @param argument One-based task number, optionally surrounded by whitespace.
     * @return Removal confirmation and the remaining task count.
     * @throws EpiException If the number is missing, the task does not exist, or saving fails.
     * @throws NumberFormatException If the nonblank argument is not a valid {@code int}.
     */
    private List<String> handleDelete(String argument) throws EpiException {
        int taskIdx = parseTaskIndex(argument);
        applyChange(updated -> updated.delete(taskIdx));
        return List.of("Noted, I'll remove that from the task pile",
                "Now you have " + tasks.size() + " tasks in the list");
    }

    /**
     * Applies the requested completion status, avoiding a save when it is already set.
     *
     * @param argument One-based task number, optionally surrounded by whitespace.
     * @param isDone {@code true} to mark the task completed; {@code false} to mark it incomplete.
     * @return A confirmation or already-set message, followed by the task's current representation.
     * @throws EpiException If the number is missing, the task does not exist, or saving fails.
     * @throws NumberFormatException If the nonblank argument is not a valid {@code int}.
     */
    private List<String> handleStatusChange(String argument, boolean isDone) throws EpiException {
        int taskIdx = parseTaskIndex(argument);
        if (tasks.get(taskIdx).isDone() == isDone) {
            String message = isDone
                    ? "Purr! Task " + (taskIdx + 1) + " is already marked as done."
                    : "Meow! Task " + (taskIdx + 1) + " is already marked as not done.";
            return List.of(message, tasks.get(taskIdx).toString());
        }
        applyChange(updated -> {
            if (isDone) {
                updated.get(taskIdx).markAsDone();
            } else {
                updated.get(taskIdx).markAsUndone();
            }
        });
        String message = isDone
                ? "About time you finished something. I've marked it as done:"
                : "Slacking off, are we? I've marked this as not done:";
        return List.of(message, tasks.get(taskIdx).toString());
    }

    /**
     * Parses and saves a todo, supplying its task-specific confirmation.
     *
     * @param argument Todo description following the command word.
     * @return Confirmation, the added task, and the new task count.
     * @throws EpiException If the description is blank or saving fails.
     * @throws IllegalArgumentException If the description contains characters forbidden in task records.
     */
    private List<String> handleTodo(String argument) throws EpiException {
        return addTask(parser.parseTodo(argument), "More work? Fine. I have added this task:");
    }

    /**
     * Parses and saves a deadline, supplying its task-specific confirmation.
     *
     * @param argument Description and {@code /by} date/time following the command word.
     * @return Confirmation, the added deadline, and the new task count.
     * @throws EpiException If required fields are missing, parameters are invalid, or saving fails.
     * @throws DateTimeException If the due date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description contains characters forbidden in task records.
     */
    private List<String> handleDeadline(String argument) throws EpiException {
        return addTask(parser.parseDeadline(argument), "A deadline? Better not miss it. I have added this task:");
    }

    /**
     * Parses and saves an event, supplying its task-specific confirmation.
     *
     * @param argument Description, {@code /from} start, and {@code /to} end following the command word.
     * @return Confirmation, the added event, and the new task count.
     * @throws EpiException If required fields are missing, parameters are invalid, or saving fails.
     * @throws DateTimeException If either date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description contains forbidden characters or the event range is invalid.
     */
    private List<String> handleEvent(String argument) throws EpiException {
        return addTask(parser.parseEvent(argument), "An event? I hope there will be treats. I have added this task:");
    }

    /**
     * Saves a new task before returning the shared confirmation lines for either interface.
     *
     * @param task Valid non-null task to append to the current list.
     * @param message Task-type-specific introductory confirmation.
     * @return The supplied message, task representation, and new task count.
     * @throws EpiException If the updated task list cannot be safely saved.
     */
    private List<String> addTask(Task task, String message) throws EpiException {
        applyChange(updated -> updated.add(task));
        return List.of(message, task.toString(), "Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Checks the parser's index contract before a task is changed or removed.
     *
     * @param argument One-based task number, optionally surrounded by whitespace.
     * @return The corresponding zero-based index in the current task list.
     * @throws EpiException If the number is missing or does not refer to an existing task.
     * @throws NumberFormatException If the nonblank argument is not a valid {@code int}.
     */
    private int parseTaskIndex(String argument) throws EpiException {
        int taskIdx = parser.parseTaskIndex(argument, tasks);
        assert taskIdx >= 0 && taskIdx < tasks.size() : "Parser returned an invalid task index";
        return taskIdx;
    }

    /**
     * Rejects list views that require at least one task.
     *
     * @throws EpiException If the current task list is empty.
     */
    private void requireTasks() throws EpiException {
        if (tasks.size() == 0) {
            throw new EpiException("Purr! There is no task in your list");
        }
    }

    /**
     * Publishes a task change in memory only after the complete new snapshot has been saved.
     * The current task list remains untouched if applying or saving the change fails.
     *
     * @param change Operation to apply only to an independent copy of the current task list.
     * @throws EpiException If the updated snapshot cannot be safely saved.
     */
    private void applyChange(Consumer<TaskList> change) throws EpiException {
        TaskList updated = tasks.copy();
        change.accept(updated);
        storage.save(updated);
        tasks = updated;
    }

    /**
     * Rejects ignored trailing text so malformed commands do not look successful.
     *
     * @param command Command word to include in the usage message.
     * @param argument Non-null trailing text, which must be blank.
     * @throws EpiException If nonblank argument text was supplied.
     */
    private void requireNoArgument(String command, String argument) throws EpiException {
        if (!argument.isBlank()) {
            throw new EpiException("Meow! Use: " + command);
        }
    }

    /**
     * Runs the console command loop until a valid {@code bye} command or end of input.
     * Closes the console scanner and its standard-input stream when the loop finishes.
     */
    public void run() {
        while (ui.hasNextLine()) {
            String input = ui.readLine();
            processCommand(input).forEach(ui::showLine);
            if (isExitCommand(input)) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Returns whether the user input requests termination of the command loop.
     *
     * @param input Non-null user command to check.
     * @return {@code true} only for {@code bye} without arguments, ignoring case and surrounding whitespace.
     */
    private boolean isExitCommand(String input) {
        String[] commandParts = parser.parseInput(input);
        return commandParts.length == 1 && commandParts[0].equalsIgnoreCase("bye");
    }

    /**
     * Starts the console interface using {@code ./data/epi.txt} as the task-file location.
     *
     * @param args Command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        new Epi("./data/epi.txt").run();
    }
}
