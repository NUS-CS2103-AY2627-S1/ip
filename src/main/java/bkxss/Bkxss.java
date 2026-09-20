package bkxss;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Starts the Bkxss chatbot and displays its initial greeting.
 */
public class Bkxss {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd yyyy HH:mm", Locale.ENGLISH);
    private static final String BOT_PREFIX = "     ";
    private static final String DIVIDER = "    ____________________________________________________________";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/(\\S+)");
    private static final int FIND_COMMAND_LENGTH = 4;
    private static final int FIND_FREE_COMMAND_LENGTH = 8;
    private static final int TODO_COMMAND_LENGTH = 4;
    private static final int DEADLINE_COMMAND_LENGTH = 8;
    private static final int EVENT_COMMAND_LENGTH = 5;
    private static final int MARK_COMMAND_LENGTH = 4;
    private static final int UNMARK_COMMAND_LENGTH = 6;
    private static final int DELETE_COMMAND_LENGTH = 6;
    private static final int TASK_NUMBER_OFFSET = 1;

    /** Processes one command for a graphical client and returns the bot's response. */
    public static String processCommand(String command, ArrayList<Task> tasks, Storage storage) {
        return processCommandResult(command, tasks, storage).message();
    }

    /**
     * Processes a GUI command and reports whether it failed validation.
     * Keeping the error flag separate from the text prevents task descriptions from triggering error styling.
     *
     * @param command command supplied by the user
     * @param tasks current task list
     * @param storage destination for task changes
     * @return response text and its validation status
     */
    public static CommandResult processCommandResult(String command, ArrayList<Task> tasks, Storage storage) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        ArrayList<Boolean> originalStatuses = tasks.stream().map(Task::isDone)
                .collect(Collectors.toCollection(ArrayList::new));
        boolean isError = false;
        try (PrintStream capturedOutput = new PrintStream(output)) {
            System.setOut(capturedOutput);
            command = normalizeCommand(command);
            if (command.equals("bye")) {
                return new CommandResult("Bye. Hope to see you again soon!", false);
            }
            boolean changed = handleCommand(command, tasks);
            if (changed) {
                storage.save(tasks);
            }
        } catch (BkxssException exception) {
            isError = true;
            tasks.clear();
            tasks.addAll(originalTasks);
            for (int index = 0; index < tasks.size(); index++) {
                if (originalStatuses.get(index)) {
                    tasks.get(index).markAsDone();
                } else {
                    tasks.get(index).markAsNotDone();
                }
            }
            // Discard success messages when validation or persistence fails.
            output.reset();
            return new CommandResult("OhNo!! ERROR :( --> " + exception.getMessage(), isError);
        } finally {
            System.setOut(originalOutput);
        }
        String response = output.toString().lines()
                .map(line -> line.startsWith(BOT_PREFIX) ? line.substring(BOT_PREFIX.length()) : line)
                .collect(Collectors.joining("\n"));
        return new CommandResult(response, isError);
    }

    /**
     * Greets the user, stores task descriptions, lists stored tasks, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used by this application
     */
    public static void main(String... args) {
        Storage storage = new Storage("data/bkxss.txt");
        ArrayList<Task> tasks = storage.load();
        String banner = "____  _                   \n"
                + "| __ )| | ____  _____ ___ \n"
                + "|  _ \\| |/ /\\ \\/ / __/ __|\n"
                + "| |_) |   <  >  <\\__ \\__ \\\n"
                + "|____/|_|\\_\\/_/\\_\\___/___/\n";

        System.out.println(DIVIDER);
        System.out.print(BOT_PREFIX + banner.replace("\n", "\n" + BOT_PREFIX).stripTrailing());
        System.out.println();
        System.out.println(BOT_PREFIX + "Hello hello ~ This is Bkxss here ;)");
        System.out.println(BOT_PREFIX + "What can I do for you?");
        System.out.println(DIVIDER);

        try (Scanner scanner = new Scanner(System.in, System.getProperty("stdin.encoding"))) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();

                System.out.println(DIVIDER);
                CommandResult result = processCommandResult(command, tasks, storage);
                result.message().lines().forEach(line -> System.out.println(BOT_PREFIX + line));
                System.out.println(DIVIDER);
                if (!result.isError() && isExitCommand(command)) {
                    return;
                }
            }
        }
    }

    /** Normalizes harmless spacing while rejecting control characters and empty input. */
    private static String normalizeCommand(String command) throws BkxssException {
        if (command == null || command.isBlank()) {
            throw new BkxssException("please enter a command, e.g. list or todo DESCRIPTION.");
        }
        if (command.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new BkxssException("commands cannot contain line breaks or control characters.");
        }
        return command.replaceAll("[\\p{Zs}\\t]+", " ").strip();
    }

    /** Returns whether the input is the exit command after applying standard command normalization. */
    static boolean isExitCommand(String command) {
        try {
            return normalizeCommand(command).equals("bye");
        } catch (BkxssException exception) {
            return false;
        }
    }

    /**
     * Processes one command against the given task list.
     *
     * @param command command supplied by the user
     * @param tasks task storage
     * @throws BkxssException if the command is invalid
     */
    private static boolean handleCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String commandWord = command.split(" ", 2)[0];
        return switch (commandWord) {
        case "list" -> handleListCommand(command, tasks);
        case "find" -> handleFindCommand(command, tasks);
        case "findfree" -> handleFindFreeCommand(command, tasks);
        case "todo" -> handleTodoCommand(command, tasks);
        case "deadline" -> handleDeadlineCommand(command, tasks);
        case "event" -> handleEventCommand(command, tasks);
        case "mark" -> handleMarkCommand(command, tasks);
        case "unmark" -> handleUnmarkCommand(command, tasks);
        case "delete" -> handleDeleteCommand(command, tasks);
        case "bye" -> throw new BkxssException("list and bye do not accept extra arguments.");
        default -> throw new BkxssException("I'm sorry, but I don't know what that means :-(");
        };
    }

    /** Lists every task after rejecting arguments that the list command does not accept. */
    private static boolean handleListCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        if (!command.equals("list")) {
            throw new BkxssException("list and bye do not accept extra arguments.");
        }
        System.out.println(BOT_PREFIX + "Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(BOT_PREFIX + (index + TASK_NUMBER_OFFSET) + "." + tasks.get(index));
        }
        return false;
    }

    /** Adds a todo from the description following the command word. */
    private static boolean handleTodoCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String description = requireDescription(command.substring(TODO_COMMAND_LENGTH), "todo");
        addTask(new Todo(description), tasks);
        return true;
    }

    /** Adds a deadline after parsing its description and due date. */
    private static boolean handleDeadlineCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String[] parts = parseParameters(command.substring(DEADLINE_COMMAND_LENGTH),
                "deadline DESCRIPTION /by DATE", "by");
        addTask(new Deadline(parts[0], parseDeadline(parts[1])), tasks);
        return true;
    }

    /** Adds an event after validating its description and time range. */
    private static boolean handleEventCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String[] parts = parseParameters(command.substring(EVENT_COMMAND_LENGTH),
                "event DESCRIPTION /from START /to END", "from", "to");
        Event event = new Event(parts[0], parts[1], parts[2]);
        validateEventTimes(event);
        addTask(event, tasks);
        return true;
    }

    /** Marks one incomplete task as completed. */
    private static boolean handleMarkCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        Task task = getTask(command.substring(MARK_COMMAND_LENGTH), tasks);
        if (task.isDone()) {
            throw new BkxssException("this task is already marked as done!");
        }
        task.markAsDone();
        System.out.println(BOT_PREFIX + "Nice! I've marked this task as done:");
        System.out.println(BOT_PREFIX + "  " + task);
        return true;
    }

    /** Marks one completed task as incomplete. */
    private static boolean handleUnmarkCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        Task task = getTask(command.substring(UNMARK_COMMAND_LENGTH), tasks);
        if (!task.isDone()) {
            throw new BkxssException("this task is already unmarked!");
        }
        task.markAsNotDone();
        System.out.println(BOT_PREFIX + "OK, I've marked this task as not done yet:");
        System.out.println(BOT_PREFIX + "  " + task);
        return true;
    }

    /** Deletes one task and reports the new list size. */
    private static boolean handleDeleteCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        Task task = getTask(command.substring(DELETE_COMMAND_LENGTH), tasks);
        tasks.remove(task);
        System.out.println(BOT_PREFIX + "Noted. I've removed this task:");
        System.out.println(BOT_PREFIX + "  " + task);
        System.out.println(BOT_PREFIX + "Now you have " + tasks.size() + " tasks in the list.");
        return true;
    }

    /** Requires every named parameter exactly once, in order, with a nonempty value. */
    private static String[] parseParameters(String arguments, String usage, String... names) throws BkxssException {
        Matcher matcher = PARAMETER_PATTERN.matcher(arguments);
        String[] parts = new String[names.length + 1];
        int parameterIndex = 0;
        int valueStart = 0;
        String error = "invalid or repeated parameters. Use: " + usage;
        while (matcher.find()) {
            if (parameterIndex >= names.length || !matcher.group(1).equals(names[parameterIndex])) {
                throw new BkxssException(error);
            }
            parts[parameterIndex] = arguments.substring(valueStart, matcher.start()).strip();
            valueStart = matcher.end();
            parameterIndex++;
        }
        if (parameterIndex != names.length) {
            throw new BkxssException(error);
        }
        parts[parameterIndex] = arguments.substring(valueStart).strip();
        for (String part : parts) {
            if (part.isEmpty()) {
                throw new BkxssException(error);
            }
        }
        return parts;
    }

    /** Searches the task list and prints tasks matching the supplied keyword. */
    private static boolean handleFindCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String keyword = command.substring(FIND_COMMAND_LENGTH).trim();
        if (keyword.isBlank()) {
            throw new BkxssException("please provide a keyword to search for. Use: find KEYWORD");
        }
        System.out.println(BOT_PREFIX + "Here are the matching tasks in your list:");
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.matchesKeyword(keyword))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        IntStream.range(0, matchingTasks.size())
                .forEach(index -> System.out.println(BOT_PREFIX + (index + 1) + "." + matchingTasks.get(index)));
        return false;
    }

    /** Finds and prints the earliest free period in the user-supplied search range. */
    private static boolean handleFindFreeCommand(String command, ArrayList<Task> tasks) throws BkxssException {
        String[] parts = parseParameters(command.substring(FIND_FREE_COMMAND_LENGTH),
                "findfree HOURS /from START /to END", "from", "to");

        int durationHours = parseDurationHours(parts[0]);
        LocalDateTime searchStart = parseSearchDateTime(parts[1]);
        LocalDateTime searchEnd = parseSearchDateTime(parts[2]);
        if (!searchStart.isBefore(searchEnd)) {
            throw new BkxssException("the free-time search start must be before its end.");
        }

        ArrayList<Event> events = getScheduledEvents(tasks);
        Duration requiredDuration = Duration.ofHours(durationHours);
        Optional<LocalDateTime> freeStart = FreeTimeFinder.findEarliestStart(
                searchStart, searchEnd, requiredDuration, events);
        printFreeTimeResult(durationHours, searchStart, searchEnd, freeStart);
        return false;
    }

    /** Returns a positive whole-number duration in hours. */
    private static int parseDurationHours(String text) throws BkxssException {
        try {
            if (!text.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int durationHours = Integer.parseInt(text.trim());
            if (durationHours <= 0) {
                throw new NumberFormatException();
            }
            return durationHours;
        } catch (NumberFormatException exception) {
            throw new BkxssException("please provide the duration as a positive whole number of hours.");
        }
    }

    /** Parses a search boundary and gives the user a useful error for invalid dates. */
    private static LocalDateTime parseSearchDateTime(String text) throws BkxssException {
        try {
            return LocalDateTime.parse(text.trim(), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new BkxssException("please provide valid search dates in yyyy-MM-dd HHmm format, "
                    + "e.g. 2026-09-12 0900");
        }
    }

    /** Returns scheduled events, rejecting legacy events whose times cannot be placed on a calendar. */
    private static ArrayList<Event> getScheduledEvents(ArrayList<Task> tasks) throws BkxssException {
        ArrayList<Event> events = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task instanceof Event event) {
                if (!event.hasScheduledTimes()) {
                    throw new BkxssException("event " + (index + TASK_NUMBER_OFFSET)
                            + " does not use yyyy-MM-dd HHmm dates. Re-add it with dated /from and /to values.");
                }
                events.add(event);
            }
        }
        return events;
    }

    /** Prints either the earliest free period or a message explaining that none was found. */
    private static void printFreeTimeResult(int durationHours, LocalDateTime searchStart,
            LocalDateTime searchEnd, Optional<LocalDateTime> freeStart) {
        String durationLabel = durationHours + "-hour";
        if (freeStart.isPresent()) {
            LocalDateTime freeEnd = freeStart.get().plusHours(durationHours);
            System.out.println(BOT_PREFIX + "The earliest " + durationLabel + " free slot is:");
            System.out.println(BOT_PREFIX + "  " + freeStart.get().format(OUTPUT_DATE_FORMAT)
                    + " to " + freeEnd.format(OUTPUT_DATE_FORMAT));
        } else {
            System.out.println(BOT_PREFIX + "I couldn't find a " + durationLabel + " free slot between "
                    + searchStart.format(OUTPUT_DATE_FORMAT) + " and " + searchEnd.format(OUTPUT_DATE_FORMAT) + ".");
        }
    }

    /** Requires valid calendar dates and a positive duration for newly entered events. */
    private static void validateEventTimes(Event event) throws BkxssException {
        Optional<LocalDateTime> eventStart = event.getFromDateTime();
        Optional<LocalDateTime> eventEnd = event.getToDateTime();
        if (eventStart.isEmpty() || eventEnd.isEmpty()) {
            throw new BkxssException("please provide valid event dates in yyyy-MM-dd HHmm format, "
                    + "e.g. 2026-09-12 0900");
        }
        if (!eventStart.get().isBefore(eventEnd.get())) {
            throw new BkxssException("an event's start must be before its end.");
        }
    }

    /** Parses a deadline and gives the user a useful error for invalid dates. */
    private static LocalDateTime parseDeadline(String text) throws BkxssException {
        try {
            return LocalDateTime.parse(text.trim(), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new BkxssException("please provide a valid deadline in yyyy-MM-dd HHmm format, e.g. 2019-12-02 1800");
        }
    }

    /** Adds a task to the list and prints a confirmation. */
    private static void addTask(Task task, ArrayList<Task> tasks) throws BkxssException {
        Task.validateDescription(task.description);
        if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
            throw new BkxssException("this task already exists in your list.");
        }
        tasks.add(task);
        assert tasks.contains(task) : "Added task must be present in the task list";
        System.out.println(BOT_PREFIX + "Got it. I've added this task:");
        System.out.println(BOT_PREFIX + task);
        System.out.println(BOT_PREFIX + "Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Returns a non-empty task description for the specified command type. */
    private static String requireDescription(String description, String commandName) throws BkxssException {
        if (description.isBlank()) {
            throw new BkxssException("The description of a " + commandName + " cannot be empty.");
        }
        return description.trim();
    }

    /** Returns the requested task after validating that its number is in the task list. */
    private static Task getTask(String numberText, ArrayList<Task> tasks) throws BkxssException {
        try {
            if (!numberText.trim().matches("-?[0-9]+")) {
                throw new NumberFormatException();
            }
            int taskNumber = Integer.parseInt(numberText.trim());
            if (taskNumber < TASK_NUMBER_OFFSET || taskNumber > tasks.size()) {
                throw new BkxssException("there is no task numbered " + taskNumber + ".");
            }
            return tasks.get(taskNumber - TASK_NUMBER_OFFSET);
        } catch (NumberFormatException exception) {
            throw new BkxssException("please provide a task number. Use: mark/unmark/delete NUMBER");
        }
    }

}
