package vermithor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A command-line chatbot that records tasks and lets users update their status.
 */
public class Vermithor {
    private static final Path DATA_FILE = Path.of("data", "vermithor.txt");
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I don't know that command. Try todo, deadline, event, list, mark, unmark, delete, find, sort, or bye.";
    private static final Pattern BY_MARKER = Pattern.compile("\\s+/by\\s+");
    private static final Pattern FROM_MARKER = Pattern.compile("\\s+/from\\s+");
    private static final Pattern TO_MARKER = Pattern.compile("\\s+/to\\s+");
    /**
     * Starts Vermithor and processes commands until the user says goodbye.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = "__      __\n"
                + "\\ \\    / /__ _ __ _ __ ___ (_) |_| |__   ___  _ __\n"
                + " \\ \\  / / _ \\ '__| '_ ` _ \\| | __| '_ \\ / _ \\| '__|\n"
                + "  \\ \\/ /  __/ |  | | | | | | | |_| | | | | (_) | |\n"
                + "   \\__/ \\___|_|  |_| |_| |_|_|\\__|_| |_|\\___/|_|\n";
        Ui ui = new Ui();
        ui.show(banner, "Hello! I'm Vermithor.\nWhat can I do for you?");

        Storage storage = new Storage(DATA_FILE);
        List<Task> tasks = loadTasks(storage);
        Parser parser = new Parser();

        while (true) {
            if (!ui.hasNextCommand()) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            String input = ui.readCommand();
            try {
                if (input.equalsIgnoreCase("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    break;
                }
                processCommand(parser, input, tasks);
                storage.save(tasks);
            } catch (VermithorException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
        }
        ui.close();
    }

    /** Loads existing tasks while allowing the chatbot to continue after a storage problem. */
    private static List<Task> loadTasks(Storage storage) {
        try {
            return storage.load();
        } catch (VermithorException e) {
            System.out.println("OOPS!!! " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Processes one command.
     *
     * @param input the user's command
     * @param tasks the task storage list
     * @throws VermithorException if the command is invalid
     */
    public static void processCommand(Parser parser, String input, List<Task> tasks)
            throws VermithorException {
        assert parser != null : "A command parser is required";
        assert input != null : "A command input is required";
        assert tasks != null : "A task collection is required";
        Parser.ParsedCommand parsed = parser.parse(input);
        CommandType command = parsed.command();
        String details = parsed.details();

        switch (command) {
        case LIST:
            if (!details.isEmpty()) {
                throw new VermithorException("The list command does not take extra words.");
            }
            printTaskList(tasks);
            return;
        case MARK:
            updateTaskStatus(details, tasks, true);
            return;
        case UNMARK:
            updateTaskStatus(details, tasks, false);
            return;
        case TODO:
            addTask(new ToDo(requireDescription(details, "todo")), tasks);
            return;
        case DEADLINE:
            addDeadline(details, tasks);
            return;
        case EVENT:
            addEvent(details, tasks);
            return;
        case DELETE:
            deleteTask(details, tasks);
            return;
        case FIND:
            findTasks(details, tasks);
            return;
        case SORT:
            sortTasks(details, tasks);
            return;
        case UNKNOWN:
            throw new VermithorException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /** Prints all stored tasks with their one-based list numbers. */
    private static void printTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /** Adds a task and prints confirmation. */
    private static void addTask(Task task, List<Task> tasks) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Adds a deadline command after validating its description and deadline. */
    private static void addDeadline(String details, List<Task> tasks) throws VermithorException {
        Matcher byMarker = BY_MARKER.matcher(details);
        if (!byMarker.find() || byMarker.start() == 0) {
            throw new VermithorException("Use deadline DESCRIPTION /by DATE.");
        }
        String description = details.substring(0, byMarker.start()).trim();
        String dateText = details.substring(byMarker.end()).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new VermithorException("Use deadline DESCRIPTION /by DATE.");
        }
        try {
            LocalDate by = LocalDate.parse(dateText);
            addTask(new Deadline(description, by), tasks);
        } catch (DateTimeParseException e) {
            if (dateText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new VermithorException("The deadline date is not valid.");
            }
            throw new VermithorException("Use a deadline date in yyyy-MM-dd format.");
        }
    }

    /** Adds an event command after validating its description and time range. */
    private static void addEvent(String details, List<Task> tasks) throws VermithorException {
        Matcher fromMarker = FROM_MARKER.matcher(details);
        if (!fromMarker.find() || fromMarker.start() == 0) {
            throw new VermithorException("Use event DESCRIPTION /from START /to END.");
        }
        String description = details.substring(0, fromMarker.start()).trim();
        String afterFrom = details.substring(fromMarker.end());
        Matcher toMarker = TO_MARKER.matcher(afterFrom);
        if (!toMarker.find()) {
            throw new VermithorException("Use event DESCRIPTION /from START /to END.");
        }
        String from = afterFrom.substring(0, toMarker.start()).trim();
        String to = afterFrom.substring(toMarker.end()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new VermithorException("Use event DESCRIPTION /from START /to END.");
        }
        LocalDate fromDate = parseOptionalEventDate(from);
        LocalDate toDate = parseOptionalEventDate(to);
        if (fromDate != null && toDate != null && !toDate.isAfter(fromDate)) {
            throw new VermithorException("The event end date must be after the start date.");
        }
        addTask(new Event(description, from, to), tasks);
    }

    /** Validates ISO date-looking event values while retaining support for free-form times. */
    private static LocalDate parseOptionalEventDate(String value) throws VermithorException {
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new VermithorException("Use valid event dates in yyyy-MM-dd format.");
        }
    }

    /** Marks or unmarks a task after validating its one-based list number. */
    private static void updateTaskStatus(String details, List<Task> tasks, boolean isDone)
            throws VermithorException {
        int taskNumber = getTaskNumber(details, tasks.size());
        Task task = tasks.get(taskNumber - 1);
        if (isDone) {
            task.markAsDone();
            System.out.println("Nice! I've marked this task as done:");
        } else {
            task.markAsNotDone();
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    /** Removes a task after validating its one-based list number. */
    private static void deleteTask(String details, List<Task> tasks) throws VermithorException {
        int taskNumber = getTaskNumber(details, tasks.size());
        Task removedTask = tasks.remove(taskNumber - 1);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Prints tasks whose descriptions contain the given keyword, ignoring letter case. */
    private static void findTasks(String details, List<Task> tasks) throws VermithorException {
        String keyword = requireDescription(details, "find").toLowerCase(Locale.ROOT);
        System.out.println("Here are the matching tasks in your list:");
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + ". " + matchingTasks.get(i));
        }
    }

    /** Sorts tasks alphabetically by description. */
    private static void sortTasks(String details, List<Task> tasks) throws VermithorException {
        if (!details.isEmpty()) {
            throw new VermithorException("The sort command does not take extra words.");
        }
        tasks.sort(Comparator.comparing(task -> task.getDescription().toLowerCase(Locale.ROOT)));
        printTaskList(tasks);
    }

    /** Validates and converts a user-entered one-based task number. */
    private static int getTaskNumber(String details, int taskCount) throws VermithorException {
        assert taskCount >= 0 : "Task count cannot be negative";
        if (taskCount == 0) {
            throw new VermithorException("Your task list is empty, so there is no task to choose.");
        }
        try {
            int taskNumber = Integer.parseInt(details);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new VermithorException("Choose a task number from 1 to " + taskCount + ".");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new VermithorException("Please provide a valid task number.");
        }
    }

    /** Returns a non-empty task description for commands that require one. */
    private static String requireDescription(String details, String command) throws VermithorException {
        if (details.isEmpty()) {
            throw new VermithorException("The description of a " + command + " cannot be empty.");
        }
        return details;
    }
}
