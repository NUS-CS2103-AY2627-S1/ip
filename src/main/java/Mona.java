import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    private static final int MAXIMUM_TASKS = 100;
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
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

        Task[] tasks = new Task[MAXIMUM_TASKS];
        int taskCount = 0;

        printFormatted(BANNER
                + "\n✨ Hello, I'm Mona.\nThe constellations lie reflected in the water tonight. "
                + "What fate shall we divine?");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine().trim();

            try {
                if (userInput.equals("bye")) {
                    printFormatted("✨ Farewell. May the stars guide you until we meet again.");
                    break;
                } else if (userInput.equals("list")) {
                    printTasks(tasks, taskCount);
                } else if (isCommand(userInput, MARK_COMMAND)) {
                    markTask(tasks, taskCount, userInput, true);
                } else if (isCommand(userInput, UNMARK_COMMAND)) {
                    markTask(tasks, taskCount, userInput, false);
                } else if (isCommand(userInput, TODO_COMMAND)) {
                    taskCount = addTodo(tasks, taskCount, userInput);
                } else if (isCommand(userInput, DEADLINE_COMMAND)) {
                    taskCount = addDeadline(tasks, taskCount, userInput);
                } else if (isCommand(userInput, EVENT_COMMAND)) {
                    taskCount = addEvent(tasks, taskCount, userInput);
                } else {
                    throw new MonaException(
                            "❌ That command is not written in the stars I can read. Try a todo, deadline, or event.");
                }
            } catch (MonaException exception) {
                printFormatted(exception.getMessage());
            }
        }
    }

    /**
     * Returns whether the given input invokes the given command word, either on its own
     * (e.g. {@code "list"}) or followed by a space and further arguments (e.g. {@code "todo "}).
     *
     * @param userInput the trimmed line the user entered.
     * @param commandWord the command word to check for, such as {@code "todo"}.
     * @return {@code true} if {@code userInput} invokes {@code commandWord}.
     */
    private static boolean isCommand(String userInput, String commandWord) {
        return userInput.equals(commandWord) || userInput.startsWith(commandWord + " ");
    }

    /**
     * Returns the text following a command word, or an empty string if the command word was
     * entered with no arguments at all.
     *
     * @param userInput the trimmed line the user entered.
     * @param commandWord the command word the input starts with.
     * @return the remaining text after the command word and its separating space.
     */
    private static String extractArguments(String userInput, String commandWord) {
        return userInput.length() == commandWord.length() ? "" : userInput.substring(commandWord.length() + 1);
    }

    private static int addTodo(Task[] tasks, int taskCount, String userInput) throws MonaException {
        String description = extractArguments(userInput, TODO_COMMAND);
        if (description.trim().isEmpty()) {
            throw new MonaException("❌ A todo needs a name before its fate can be charted.");
        }

        return addTask(tasks, taskCount, new Todo(description));
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) throws MonaException {
        if (taskCount >= tasks.length) {
            throw new MonaException("❌ The list of fates overflows. No more tasks can be added.");
        }

        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        printFormatted("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    private static int addDeadline(Task[] tasks, int taskCount, String userInput) throws MonaException {
        String arguments = extractArguments(userInput, DEADLINE_COMMAND);
        int separatorIndex = arguments.indexOf(DEADLINE_SEPARATOR);
        if (separatorIndex < 0) {
            throw new MonaException("❌ Even the stars need a fixed point. Specify the deadline using /by.");
        }

        String description = arguments.substring(0, separatorIndex);
        String deadline = arguments.substring(separatorIndex + DEADLINE_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw new MonaException("❌ A deadline needs a name before its fate can be charted.");
        }
        if (deadline.trim().isEmpty()) {
            throw new MonaException("❌ A deadline needs a point in time. Tell me when it falls due after /by.");
        }

        return addTask(tasks, taskCount, new Deadline(description, deadline));
    }

    private static int addEvent(Task[] tasks, int taskCount, String userInput) throws MonaException {
        String arguments = extractArguments(userInput, EVENT_COMMAND);
        int startSeparatorIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        int endSeparatorIndex = arguments.indexOf(EVENT_END_SEPARATOR,
                startSeparatorIndex + EVENT_START_SEPARATOR.length());
        if (startSeparatorIndex < 0 || endSeparatorIndex < 0) {
            throw new MonaException("❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.");
        }

        String description = arguments.substring(0, startSeparatorIndex);
        String start = arguments.substring(startSeparatorIndex + EVENT_START_SEPARATOR.length(), endSeparatorIndex);
        String end = arguments.substring(endSeparatorIndex + EVENT_END_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw new MonaException("❌ An event needs a name before its fate can be charted.");
        }
        if (start.trim().isEmpty() || end.trim().isEmpty()) {
            throw new MonaException("❌ An event needs both a dawn and a dusk. Fill in /from and /to.");
        }

        return addTask(tasks, taskCount, new Event(description, start, end));
    }

    private static void printFormatted(String text) {
        System.out.println(SEPARATOR);
        System.out.println(text);
        System.out.println(SEPARATOR);
    }

    private static void printTasks(Task[] tasks, int taskCount) {
        StringBuilder taskList = new StringBuilder("✨ Here is what the stars reveal:");
        for (int index = 0; index < taskCount; index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(tasks[index]);
        }
        printFormatted(taskList.toString());
    }

    private static void markTask(Task[] tasks, int taskCount, String userInput, boolean shouldMarkAsDone)
            throws MonaException {
        String commandWord = shouldMarkAsDone ? MARK_COMMAND : UNMARK_COMMAND;
        String argument = extractArguments(userInput, commandWord).trim();

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new MonaException(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new MonaException(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.");
        }

        Task task = tasks[taskNumber - 1];

        if (shouldMarkAsDone) {
            task.markAsDone();
            printFormatted("✅ The stars align. I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            printFormatted("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
        }
    }
}
