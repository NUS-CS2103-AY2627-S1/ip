package otaku;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import otaku.command.CommandType;
import otaku.exception.OtakuException;
import otaku.storage.Storage;
import otaku.task.Deadline;
import otaku.task.Event;
import otaku.task.Task;
import otaku.task.TaskSorter;
import otaku.task.Todo;

/** Provides Otaku's command-processing logic for both the CLI and JavaFX interfaces. */
public class Otaku {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path DATA_FILE = Path.of("data", "otaku.txt");
    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final String loadWarning;

    /** Creates an Otaku instance backed by the default data file. */
    public Otaku() {
        this(DATA_FILE);
    }

    /** Creates an instance backed by the given file, which is useful for isolated tests. */
    Otaku(Path dataFile) {
        storage = new Storage(dataFile);
        ArrayList<Task> loadedTasks;
        String warning = "";
        try {
            loadedTasks = storage.load();
        } catch (OtakuException e) {
            loadedTasks = new ArrayList<>();
            warning = " " + e.getMessage() + "\n Starting with an empty task list instead.";
        }
        tasks = loadedTasks;
        loadWarning = warning;
    }

    /** Runs the original command-line interface. */
    public static void main(String... args) {
        Otaku otaku = new Otaku();
        System.out.println(DIVIDER);
        System.out.println(otaku.getGreeting());
        System.out.println(DIVIDER);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(otaku.getResponse(command));
            System.out.println(DIVIDER);
            if (getCommandType(command) == CommandType.BYE) {
                break;
            }
        }
    }

    /** Returns the greeting shown when either interface starts. */
    public String getGreeting() {
        String banner = "  ___  _        _\n"
                + " / _ \\| |_ __ _| | ___   _\n"
                + "| | | | __/ _` | |/ / | | |\n"
                + "| |_| | || (_| |   <| |_| |\n"
                + " \\___/ \\__\\__,_|_|\\_\\\\__,_|";
        String greeting = banner + "\nHello! I'm Otaku, your questkeeper."
                + "\nWhat mission shall we tackle next?";
        return loadWarning.isEmpty() ? greeting : greeting + "\n" + loadWarning;
    }

    /** Processes one user command and returns the text to display. */
    public String getResponse(String command) {
        return getCommandResponse(command).message();
    }

    /** Processes one user command and returns its text together with its error status. */
    public CommandResponse getCommandResponse(String command) {
        String normalizedCommand = normalizeCommand(command);
        if (normalizedCommand.isEmpty()) {
            return new CommandResponse(" I need a command before I can update the quest log.", true);
        }
        CommandType commandType = getCommandType(normalizedCommand);
        if (commandType == CommandType.BYE) {
            return new CommandResponse("Quest log sealed. Mata ne!", false);
        }
        try {
            CommandResult result = processCommand(normalizedCommand, commandType, tasks);
            if (result.tasksChanged()) {
                storage.save(tasks);
            }
            return new CommandResponse(result.message(), false);
        } catch (OtakuException e) {
            return new CommandResponse(" " + e.getMessage(), true);
        }
    }

    /** Processes one non-exit command. */
    private static CommandResult processCommand(String command, CommandType type,
            ArrayList<Task> tasks) throws OtakuException {
        assert type != CommandType.BYE : "Exit commands must be handled before command processing";

        switch (type) {
        case LIST:
            return new CommandResult(formatTasks(tasks, null), false);
        case FIND:
            return findTasks(command, tasks);
        case TODO:
            return addTodo(command, tasks);
        case DEADLINE:
            return addDeadline(command, tasks);
        case EVENT:
            return addEvent(command, tasks);
        case MARK:
        case UNMARK:
            return updateTaskStatus(command, type, tasks);
        case DELETE:
            return deleteTask(command, tasks);
        case SORT:
            return sortTasks(command, tasks);
        default:
            throw new OtakuException(
                    "I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, "
                            + "delete, sort, or bye.");
        }
    }

    /** Returns tasks whose descriptions match the command keyword. */
    private static CommandResult findTasks(String command, ArrayList<Task> tasks) throws OtakuException {
        String keyword = getArguments(command, CommandType.FIND);
        requireNonEmpty(keyword, "I need a keyword after `find`.");
        return new CommandResult(formatTasks(tasks, keyword), false);
    }

    /** Adds a to-do task described by the command. */
    private static CommandResult addTodo(String command, ArrayList<Task> tasks) throws OtakuException {
        String description = getArguments(command, CommandType.TODO);
        requireNonEmpty(description, "I need a description after `todo`.");
        return addTask(tasks, new Todo(description));
    }

    /** Adds a deadline task described by the command. */
    private static CommandResult addDeadline(String command, ArrayList<Task> tasks) throws OtakuException {
        String[] parts = getArguments(command, CommandType.DEADLINE).split("\\s+/by(?:\\s+|$)", -1);
        if (parts.length > 2) {
            throw new OtakuException("A deadline accepts exactly one `/by` parameter.");
        }
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new OtakuException("A deadline needs a description and a time after `/by`.");
        }
        return addTask(tasks, new Deadline(parts[0].trim(), parseDate(parts[1].trim())));
    }

    /** Adds an event task described by the command. */
    private static CommandResult addEvent(String command, ArrayList<Task> tasks) throws OtakuException {
        String[] descriptionAndTimes = getArguments(command, CommandType.EVENT)
                .split("\\s+/from(?:\\s+|$)", -1);
        if (descriptionAndTimes.length > 2) {
            throw new OtakuException("An event accepts exactly one `/from` parameter.");
        }
        if (descriptionAndTimes.length != 2) {
            throw eventFormatException();
        }
        String[] times = descriptionAndTimes[1].split("\\s+/to(?:\\s+|$)", -1);
        if (times.length > 2) {
            throw new OtakuException("An event accepts exactly one `/to` parameter.");
        }
        if (descriptionAndTimes[0].trim().isEmpty() || times.length != 2
                || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            throw eventFormatException();
        }
        LocalDate from = parseDate(times[0].trim());
        LocalDate to = parseDate(times[1].trim());
        if (!to.isAfter(from)) {
            throw new OtakuException("An event's end date must be after its start date.");
        }
        return addTask(tasks, new Event(descriptionAndTimes[0].trim(), from, to));
    }

    /** Marks or unmarks the task selected by the command. */
    private static CommandResult updateTaskStatus(String command, CommandType type,
            ArrayList<Task> tasks) throws OtakuException {
        String word = type.name().toLowerCase(Locale.ROOT);
        int number = parseTaskNumber(getArguments(command, type), word, tasks.size());
        Task task = tasks.get(number - 1);
        if (type == CommandType.MARK) {
            task.markAsDone();
            return new CommandResult(" Quest cleared! This task is now complete:\n   " + task, true);
        }
        task.unmarkAsDone();
        return new CommandResult(" Quest reopened. This task is active again:\n   " + task, true);
    }

    /** Deletes the task selected by the command. */
    private static CommandResult deleteTask(String command, ArrayList<Task> tasks) throws OtakuException {
        int number = parseTaskNumber(getArguments(command, CommandType.DELETE), "delete", tasks.size());
        Task removed = tasks.remove(number - 1);
        return new CommandResult(" Quest retired. I've removed this task:\n   " + removed
                + "\n " + formatQuestCount(tasks.size()), true);
    }

    /** Sorts dated tasks chronologically and places undated tasks last. */
    private static CommandResult sortTasks(String command, ArrayList<Task> tasks) throws OtakuException {
        requireNoArguments(getArguments(command, CommandType.SORT), "sort");
        TaskSorter.sortByDate(tasks);
        return new CommandResult(formatTasks(tasks, null), true);
    }

    private static OtakuException eventFormatException() {
        return new OtakuException(
                "An event needs a description, a start time after `/from`, and an end time after `/to`.");
    }

    /** Returns the command word's enum value, or {@link CommandType#UNKNOWN}. */
    private static CommandType getCommandType(String command) {
        String normalizedCommand = normalizeCommand(command);
        return Arrays.stream(CommandType.values())
                .filter(type -> matchesCommandWord(normalizedCommand, type))
                .findFirst()
                .orElse(CommandType.UNKNOWN);
    }

    /** Returns whether the input starts with the word for the given command type. */
    private static boolean matchesCommandWord(String command, CommandType type) {
        String commandWord = type.name().toLowerCase(Locale.ROOT);
        return command.equals(commandWord)
                || (command.startsWith(commandWord)
                        && command.length() > commandWord.length()
                        && Character.isWhitespace(command.charAt(commandWord.length())));
    }

    /** Trims a command and collapses repeated whitespace to a single space. */
    private static String normalizeCommand(String command) {
        return command == null ? "" : command.trim().replaceAll("\\s+", " ");
    }

    /** Returns the text following the command word. */
    private static String getArguments(String command, CommandType type) {
        String commandWord = type.name().toLowerCase(Locale.ROOT);
        return command.substring(commandWord.length()).trim();
    }

    /** Formats either all tasks or those matching a keyword. */
    private static String formatTasks(ArrayList<Task> tasks, String keyword) {
        String heading = keyword == null ? " Your quest log:"
                : " Matching quests:";
        StringBuilder response = new StringBuilder(heading);
        int number = 1;
        for (Task task : tasks) {
            if (keyword == null || task.containsKeyword(keyword)) {
                response.append('\n').append(number++).append('.').append(task);
            }
        }
        return response.toString();
    }

    private static void requireNonEmpty(String value, String message) throws OtakuException {
        if (value.isEmpty()) {
            throw new OtakuException(message);
        }
    }

    private static void requireNoArguments(String arguments, String command) throws OtakuException {
        if (!arguments.isEmpty()) {
            throw new OtakuException("The `" + command + "` command does not accept arguments.");
        }
    }

    private static LocalDate parseDate(String input) throws OtakuException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new OtakuException("Please enter dates as yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    private static int parseTaskNumber(String input, String command,
            int taskCount) throws OtakuException {
        assert taskCount >= 0 : "A task list cannot contain a negative number of tasks";

        try {
            int number = Integer.parseInt(input);
            if (number < 1 || number > taskCount) {
                throw new OtakuException("Task " + number + " does not exist. Choose a number from 1 to "
                        + taskCount + ".");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new OtakuException("Please give a whole task number after `" + command + "`.");
        }
    }

    private static CommandResult addTask(ArrayList<Task> tasks, Task task) throws OtakuException {
        assert tasks != null : "A task must be added to an initialized task list";
        assert task != null : "Only a constructed task can be added to the task list";

        if (tasks.stream().anyMatch(existingTask -> hasSameDetails(existingTask, task))) {
            throw new OtakuException("That quest is already in your log.");
        }
        tasks.add(task);
        return new CommandResult(" Quest accepted! I've added this task:\n   " + task
                + "\n " + formatQuestCount(tasks.size()), true);
    }

    /** Formats a task count in Otaku's questkeeper voice. */
    private static String formatQuestCount(int taskCount) {
        String noun = taskCount == 1 ? "quest" : "quests";
        return "Your log now holds " + taskCount + " " + noun + ".";
    }

    /** Returns whether two tasks have the same type, description, and dates. */
    private static boolean hasSameDetails(Task first, Task second) {
        if (first.getClass() != second.getClass()
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.getFrom().equals(secondEvent.getFrom())
                    && firstEvent.getTo().equals(secondEvent.getTo());
        }
        return true;
    }

    /** Couples a command's display text with whether the task file needs saving. */
    private record CommandResult(String message, boolean tasksChanged) {
    }

    /** Contains display text and whether it represents an error. */
    public record CommandResponse(String message, boolean isError) {
    }
}
