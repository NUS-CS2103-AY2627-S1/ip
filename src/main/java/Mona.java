import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
    // Relative to the working directory the program is run from, per the project's
    // requirement to avoid absolute, OS-specific paths.
    private static final String DATA_FILE_PATH = "./data/mona.txt";
    private static final String BANNER = " __  __  ___  _   _    _ \n"
            + "|  \\/  |/ _ \\| \\ | |  / \\\n"
            + "| |\\/| | | | |  \\| | / _ \\\n"
            + "| |  | | |_| | |\\  |/ ___ \\\n"
            + "|_|  |_|\\___/|_| \\_/_/   \\_\\\n";

    /**
     * Starts Mona and processes task commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        // The default console encoding on Windows cannot represent the emoji used in Mona's
        // messages, so stdout is switched to UTF-8 explicitly rather than relying on the
        // platform default.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Storage storage = new Storage(DATA_FILE_PATH);
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (MonaException exception) {
            // Loading failed (e.g. the data file could not be read), so start with an
            // empty list rather than crashing; the user's tasks for this session are
            // still tracked in memory even though the earlier save could not be recovered.
            printFormatted(exception.getMessage());
            tasks = new ArrayList<>();
        }

        printFormatted(BANNER
                + "\n✨ Hello, I'm Mona.\nThe constellations lie reflected in the water tonight. "
                + "What fate shall we divine?");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine().trim();

            try {
                if (userInput.isEmpty()) {
                    throw MonaException.withHint(
                            "❌ Silence carries no fate. Please enter a command.",
                            "list");
                }

                Optional<Command> parsedCommand = Command.from(userInput);
                if (parsedCommand.isEmpty()) {
                    throw MonaException.withHint(
                            "❌ That command is not written in the stars I can read. "
                                    + "Try a todo, deadline, or event.",
                            "list | todo <description> | deadline <description> /by <yyyy-mm-dd> | "
                                    + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd> | mark <number> | "
                                    + "unmark <number> | delete <number> | bye");
                }

                Command command = parsedCommand.get();
                switch (command) {
                    case BYE:
                        printFormatted("✨ Farewell. May the stars guide you until we meet again.");
                        return;
                    case LIST:
                        printTasks(tasks);
                        break;
                    case MARK:
                        markTask(tasks, userInput, true, storage);
                        break;
                    case UNMARK:
                        markTask(tasks, userInput, false, storage);
                        break;
                    case DELETE:
                        deleteTask(tasks, userInput, storage);
                        break;
                    case TODO:
                        addTodo(tasks, userInput, storage);
                        break;
                    case DEADLINE:
                        addDeadline(tasks, userInput, storage);
                        break;
                    case EVENT:
                        addEvent(tasks, userInput, storage);
                        break;
                    default:
                        throw new AssertionError("Unhandled command: " + command);
                }
            } catch (MonaException exception) {
                printFormatted(exception.getMessage());
            }
        }
    }

    private static void addTodo(ArrayList<Task> tasks, String userInput, Storage storage) throws MonaException {
        String description = Command.TODO.extractArguments(userInput);
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A todo needs a name before its fate can be charted.",
                    "todo read book");
        }

        addTask(tasks, new Todo(description), storage);
    }

    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage) throws MonaException {
        tasks.add(task);
        // Save before reporting success, so a failed save is reported as an error
        // instead of falsely telling the user the task was added.
        storage.save(tasks);
        printFormatted("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void addDeadline(ArrayList<Task> tasks, String userInput, Storage storage)
            throws MonaException {
        String arguments = Command.DEADLINE.extractArguments(userInput);
        int separatorIndex = arguments.indexOf(DEADLINE_SEPARATOR);
        if (separatorIndex < 0) {
            throw MonaException.withHint(
                    "❌ Even the stars need a fixed point. Specify the deadline using /by.",
                    "deadline return book /by 2019-10-15");
        }

        String description = arguments.substring(0, separatorIndex);
        String deadlineText = arguments.substring(separatorIndex + DEADLINE_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A deadline needs a name before its fate can be charted.",
                    "deadline return book /by 2019-10-15");
        }
        if (deadlineText.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A deadline needs a point in time. Tell me when it falls due after /by.",
                    "deadline return book /by 2019-10-15");
        }

        TaskDateTime deadline = parseDate(deadlineText.trim(), "deadline return book /by 2019-10-15");
        addTask(tasks, new Deadline(description, deadline), storage);
    }

    private static void addEvent(ArrayList<Task> tasks, String userInput, Storage storage) throws MonaException {
        String arguments = Command.EVENT.extractArguments(userInput);
        int startSeparatorIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        int endSeparatorIndex = arguments.indexOf(EVENT_END_SEPARATOR,
                startSeparatorIndex + EVENT_START_SEPARATOR.length());
        if (startSeparatorIndex < 0 || endSeparatorIndex < 0) {
            // /from and /to are both present, but /to comes before /from: point the user at the
            // ordering rather than reporting them as missing.
            if (startSeparatorIndex >= 0 && arguments.indexOf(EVENT_END_SEPARATOR) >= 0) {
                throw MonaException.withHint(
                        "❌ Fate flows only forward. Place /from before /to.",
                        "event project meeting /from 2019-10-15 /to 2019-10-16");
            }
            throw MonaException.withHint(
                    "❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }

        String description = arguments.substring(0, startSeparatorIndex);
        String startText = arguments.substring(
                startSeparatorIndex + EVENT_START_SEPARATOR.length(), endSeparatorIndex);
        String endText = arguments.substring(endSeparatorIndex + EVENT_END_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ An event needs a name before its fate can be charted.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }
        if (startText.trim().isEmpty() || endText.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ An event needs both a dawn and a dusk. Fill in /from and /to.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }

        TaskDateTime start = parseDate(startText.trim(), "event project meeting /from 2019-10-15 /to 2019-10-16");
        TaskDateTime end = parseDate(endText.trim(), "event project meeting /from 2019-10-15 /to 2019-10-16");
        addTask(tasks, new Event(description, start, end), storage);
    }

    /**
     * Parses user-entered text as a date, optionally with a time of day.
     *
     * @param dateText the text to parse, either {@code yyyy-mm-dd HHmm} or {@code yyyy-mm-dd}.
     * @param hint an example command demonstrating the correct usage, shown if parsing fails.
     * @return the parsed date.
     * @throws MonaException if the text matches neither accepted format.
     */
    private static TaskDateTime parseDate(String dateText, String hint) throws MonaException {
        try {
            return TaskDateTime.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw MonaException.withHint(
                    "❌ The stars only read dates as yyyy-mm-dd, optionally followed by a "
                            + "24-hour time, such as 2019-10-15 or 2019-10-15 1800.",
                    hint);
        }
    }

    private static void printFormatted(String text) {
        System.out.println(SEPARATOR);
        System.out.println(text);
        System.out.println(SEPARATOR);
    }

    private static void printTasks(ArrayList<Task> tasks) {
        StringBuilder taskList = new StringBuilder("✨ Here is what the stars reveal:");
        for (int index = 0; index < tasks.size(); index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(tasks.get(index));
        }
        printFormatted(taskList.toString());
    }

    private static void markTask(ArrayList<Task> tasks, String userInput, boolean shouldMarkAsDone,
            Storage storage) throws MonaException {
        Command command = shouldMarkAsDone ? Command.MARK : Command.UNMARK;
        String argument = command.extractArguments(userInput).trim();

        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which task's fate to alter.",
                    command + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    command + " 2");
        }

        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ There are no tasks yet whose fate can be altered.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task task = tasks.get(taskNumber - 1);

        if (shouldMarkAsDone) {
            task.markAsDone();
            storage.save(tasks);
            printFormatted("✅ The stars align. I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            storage.save(tasks);
            printFormatted("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
        }
    }

    private static void deleteTask(ArrayList<Task> tasks, String userInput, Storage storage)
            throws MonaException {
        String argument = Command.DELETE.extractArguments(userInput).trim();

        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which task should be deleted.",
                    "delete 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "delete 2");
        }

        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ The constellations remain still. There are no tasks to be deleted.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task deletedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks);
        printFormatted("✅ A fate fades from the constellations. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
