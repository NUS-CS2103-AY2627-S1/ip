package rei;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import rei.command.Command;
import rei.exception.ReiException;
import rei.storage.Storage;
import rei.task.Deadlines;
import rei.task.Events;
import rei.task.Task;
import rei.ui.Ui;

/** Coordinates Rei command processing, task management, storage, and UI. */
public class Rei {
    private static final Path DATA_FILE = Path.of(System.getProperty(
            "rei.data.file", Path.of("data", "rei.txt").toString()));
    private static final List<DateTimeFormatter> INPUT_DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT));
    private static final List<DateTimeFormatter> FIND_DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT));

    private final Storage storage;
    private final List<Task> tasks;
    private final String startupMessage;

    /** Creates Rei using the default data file. */
    public Rei() {
        this(DATA_FILE);
    }

    /** Creates Rei using a specific data file, primarily for testing or alternate interfaces. */
    public Rei(Path dataFile) {
        storage = new Storage(dataFile);
        LoadState loadState = loadTasks(storage);
        tasks = loadState.tasks();
        startupMessage = loadState.warning();
    }

    /**
     * Starts Rei's command-line interface and processes commands until the user exits.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showGreeting();
        Rei rei = new Rei();
        if (!rei.startupMessage.isEmpty()) {
            ui.showResponse(rei.startupMessage);
            ui.divider();
        }

        while (ui.hasNextCommand()) {
            CommandResult result = rei.executeCommand(ui.readCommand(), ui);
            ui.divider();
            if (result.shouldExit()) {
                break;
            }
        }
        ui.close();
    }

    /** Returns any warning generated while loading saved tasks. */
    public String getStartupMessage() {
        return startupMessage;
    }

    /** Processes one command and returns its displayable response for a non-console interface. */
    public CommandResult getResponse(String input) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = Ui.forOutput(output);
            CommandResult result = executeCommand(input.trim(), responseUi);
            return new CommandResult(outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing(),
                    result.shouldExit(), result.isError());
        }
    }

    /** Loads saved tasks while keeping startup usable if the file cannot be read. */
    private static LoadState loadTasks(Storage storage) {
        try {
            Storage.LoadResult result = storage.load();
            if (result.skippedLines() > 0) {
                return new LoadState(result.tasks(), "Warning: I skipped " + result.skippedLines()
                        + " invalid line(s) in the data file.");
            }
            return new LoadState(result.tasks(), "");
        } catch (IOException exception) {
            return new LoadState(new ArrayList<>(),
                    "Warning: Oh man I could not read the data file. Gonna create an empty list.");
        }
    }

    /** Processes one command through a supplied renderer. */
    private CommandResult executeCommand(String userInput, Ui ui) {
        // Both interfaces must trim input before handing it to the shared parser.
        assert userInput != null && userInput.equals(userInput.trim()) : "Command input must be trimmed";
        if (userInput.isEmpty()) {
            ui.showError("Please enter a command. Try: todo read book");
            return new CommandResult("", false, true);
        }

        int firstSpace = userInput.indexOf(" ");
        String commandText = firstSpace == -1
                ? userInput
                : userInput.substring(0, firstSpace);
        String details = firstSpace == -1
                ? ""
                : userInput.substring(firstSpace).trim();
        Command command = Command.fromText(commandText);
        try {
            if (command == null) {
                throw new ReiException("I'm sorry, I don't know what is '" + commandText
                        + "'. Try todo, deadline, event, list, find, delete, mark, unmark, tag, or bye.");
            }
            return dispatchCommand(command, details, ui);
        } catch (ReiException exception) {
            ui.showError(exception.getMessage());
            return new CommandResult("", false, true);
        }
    }

    /** Routes a recognized command and reports whether it requests application exit. */
    private CommandResult dispatchCommand(Command command, String details, Ui ui) throws ReiException {
        switch (command) {
            case BYE -> {
                ensureNoDetails(command, details);
                ui.showExit();
                return new CommandResult("", true, false);
            }
            case LIST -> {
                ensureNoDetails(command, details);
                ui.showTasks(tasks);
            }
            case FIND -> printFoundTasks(details, tasks, ui);
            case TAG -> {
                tagTask(details, tasks, ui);
                saveTasks(storage, tasks);
            }
            case DELETE -> {
                Task deletedTask = deleteTask(details, tasks);
                saveTasks(storage, tasks);
                ui.showTaskDeleted(deletedTask, tasks.size());
            }
            case TODO, DEADLINE, EVENT, MARK, UNMARK -> executeTaskCommand(command, details, ui);
            default -> throw new IllegalStateException("Unhandled command: " + command);
        }
        return new CommandResult("", false, false);
    }

    /** Applies a task creation or status change and persists it before confirming additions. */
    private void executeTaskCommand(Command command, String details, Ui ui) throws ReiException {
        Task newTask = processTaskCommand(command, details, tasks, ui);
        if (newTask != null) {
            tasks.add(newTask);
            saveTasks(storage, tasks);
            ui.showTaskAdded(newTask, tasks.size());
        } else if (command == Command.MARK || command == Command.UNMARK) {
            saveTasks(storage, tasks);
        }
    }

    /** Saves tasks immediately after a successful list-changing command. */
    private static void saveTasks(Storage storage, List<Task> tasks) throws ReiException {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            throw new ReiException("I couldn't save your tasks. Please check that the data folder is writable.");
        }
    }

    /** Processes a task-creating or task-status command entered by the user. */
    private static Task processTaskCommand(Command command, String details, List<Task> tasks, Ui ui)
            throws ReiException {
        // The dispatcher must handle all non-task commands before reaching this helper.
        assert command == Command.TODO || command == Command.DEADLINE || command == Command.EVENT
                || command == Command.MARK || command == Command.UNMARK : "Expected a task command";
        return switch (command) {
            case TODO -> createTodo(details);
            case DEADLINE -> createDeadline(details);
            case EVENT -> createEvent(details);
            case MARK -> {
                updateTaskStatus(details, tasks, true, ui);
                yield null;
            }
            case UNMARK -> {
                updateTaskStatus(details, tasks, false, ui);
                yield null;
            }
            case LIST, FIND, DELETE, TAG, BYE -> throw new IllegalStateException("Command already handled: " + command);
        };
    }

    /** Adds a tag to one existing task and displays the updated task. */
    private static void tagTask(String details, List<Task> tasks, Ui ui) throws ReiException {
        String[] parts = details.split("\\s+", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new ReiException("Please provide a task number and tag. Try: tag 1 #school");
        }
        int taskIndex = getTaskIndex(parts[0], tasks.size(), Command.TAG.getKeyword());
        Task task = tasks.get(taskIndex);
        try {
            if (!task.addTag(parts[1])) {
                throw new ReiException("Task " + (taskIndex + 1) + " already has tag "
                        + parts[1].toLowerCase(Locale.ENGLISH) + ".");
            }
        } catch (IllegalArgumentException exception) {
            throw new ReiException("A tag must start with # and contain only letters, numbers, _ or -. "
                    + "Try: tag 1 #school");
        }
        String normalizedTag = task.getTags().get(task.getTags().size() - 1);
        ui.showTaskTagged(task, taskIndex + 1, normalizedTag);
    }

    /** Creates a todo task after validating that a description was provided. */
    private static Task createTodo(String details) throws ReiException {
        if (details.isEmpty()) {
            throw new ReiException("A todo needs a description. \nTry: todo {your task}");
        }
        return new Task(details);
    }

    /** Creates a deadline task from its description and typed due date. */
    private static Task createDeadline(String details) throws ReiException {
        String[] deadlineParts = details.split("\\s+/by\\s+", -1);
        if (deadlineParts.length != 2 || deadlineParts[0].isEmpty() || deadlineParts[1].isEmpty()) {
            throw new ReiException("A deadline needs a description and a by date. "
                    + "\nTry: deadline {your task} /by {deadline}");
        }
        return new Deadlines(deadlineParts[0], parseDateTime(deadlineParts[1]));
    }

    /** Creates an event task after validating its description and time range. */
    private static Task createEvent(String details) throws ReiException {
        String[] eventParts = details.split("\\s+/from\\s+", -1);
        if (eventParts.length != 2) {
            throw new ReiException("An event needs a description, start, and end time. "
                    + "\nTry: event {task} /from {start} /to {end}");
        }
        String[] timeParts = eventParts[1].split("\\s+/to\\s+", -1);
        if (eventParts[0].isEmpty() || timeParts.length != 2
                || timeParts[0].isEmpty() || timeParts[1].isEmpty()) {
            throw new ReiException("An event needs a description, start, and end time. "
                    + "\nTry: event {task} /from {start} /to {end}");
        }
        LocalDateTime start = parseDateTime(timeParts[0]);
        LocalDateTime end = parseDateTime(timeParts[1]);
        if (end.isBefore(start)) {
            throw new ReiException("An event's end time cannot be before its start time.");
        }
        return new Events(eventParts[0], start, end);
    }

    /** Converts a supported user-entered date and time into a typed value. */
    private static LocalDateTime parseDateTime(String text) throws ReiException {
        for (DateTimeFormatter formatter : INPUT_DATE_FORMATS) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new ReiException("Please use a valid date and time in yyyy-MM-dd, yyyy/MM/dd, "
                + "dd/MM/yyyy, or dd-MM-yyyy format, followed by a 24-hour time such as 1800.");
    }

    /** Updates a selected task's completion status and displays the result. */
    private static void updateTaskStatus(String details, List<Task> tasks, boolean isDone, Ui ui)
            throws ReiException {
        Command command = isDone ? Command.MARK : Command.UNMARK;
        if (details.isEmpty()) {
            throw new ReiException("Please provide a task number! Try: "
                    + command.getKeyword() + " {task no.}");
        }
        if (!details.matches("\\d+")) {
            throw new ReiException("The task number must be a whole number! Try: "
                    + command.getKeyword() + " {task no.}");
        }
        int taskIndex = getTaskIndex(details, tasks.size(), command.getKeyword());
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        // The displayed confirmation must agree with the state that will be saved.
        assert task.isDone() == isDone : "Task status must match the requested state";
        ui.showTaskStatusChanged(task, isDone);
    }

    /** Finds tasks by date for date-shaped input, or by description keyword otherwise. */
    private static void printFoundTasks(String details, List<Task> tasks, Ui ui) throws ReiException {
        if (details.isEmpty()) {
            throw new ReiException("Please provide a keyword or date. Try: find book");
        }
        if (isDateQuery(details)) {
            printTasksOnDate(details, tasks, ui);
            return;
        }
        printTasksMatchingKeyword(details, tasks, ui);
    }

    /** Returns whether the query has one of the supported date shapes. */
    private static boolean isDateQuery(String details) {
        return details.matches("\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}")
                || details.matches("\\d{1,2}[-/]\\d{1,2}[-/]\\d{4}");
    }

    /** Prints tasks whose descriptions contain the supplied keyword. */
    private static void printTasksMatchingKeyword(String keyword, List<Task> tasks, Ui ui) {
        // Keep original indexes so users can act on a match with mark or delete.
        List<Integer> matchingIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).hasKeyword(keyword))
                .boxed()
                .toList();
        ui.showMatchingTasksHeading();
        if (matchingIndexes.isEmpty()) {
            ui.showNoMatchingTasks();
            return;
        }
        matchingIndexes.forEach(index -> ui.showNumberedTask(index + 1, tasks.get(index)));
    }

    /** Prints deadlines and events that occur on a user-specified calendar date. */
    private static void printTasksOnDate(String details, List<Task> tasks, Ui ui) throws ReiException {
        LocalDate date = getDate(details);

        boolean foundTask = false;
        ui.showTasksOnDateHeading(date);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (occursOn(task, date)) {
                ui.showNumberedTask(i + 1, task);
                foundTask = true;
            }
        }
        if (!foundTask) {
            ui.showNoTasksOnDate();
        }
    }

    /** Converts a supported user-entered date into a typed calendar date. */
    private static LocalDate getDate(String details) throws ReiException {
        LocalDate date = null;
        for (DateTimeFormatter formatter : FIND_DATE_FORMATS) {
            try {
                date = LocalDate.parse(details, formatter);
                break;
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        if (date == null) {
            throw new ReiException("Please provide a valid date in yyyy-MM-dd, yyyy/MM/dd, "
                    + "dd/MM/yyyy, or dd-MM-yyyy format. Try: find 2019-12-02");
        }
        return date;
    }

    /** Returns whether a scheduled task falls on the supplied date. */
    private static boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadlines deadline) {
            return deadline.getBy().toLocalDate().equals(date);
        }
        if (task instanceof Events event) {
            LocalDate startDate = event.getStart().toLocalDate();
            LocalDate endDate = event.getEnd().toLocalDate();
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }
        return false;
    }

    /** Removes the requested task and keeps the remaining task indexes contiguous. */
    private static Task deleteTask(String details, List<Task> tasks) throws ReiException {
        if (details.isEmpty()) {
            throw new ReiException("Please provide a task number. Try: delete 1");
        } else if (!details.matches("\\d+")) {
            throw new ReiException("The task number must be a whole number. Try: delete 1");
        }
        int taskIndex = getTaskIndex(details, tasks.size(), "delete");
        return tasks.remove(taskIndex);
    }

    /** Rejects a command that has unexpected extra text. */
    private static void ensureNoDetails(Command command, String details) throws ReiException {
        if (!details.isEmpty()) {
            throw new ReiException("The " + command.getKeyword()
                    + " command does not take any extra text. Try: " + command.getKeyword());
        }
    }

    /** Converts a valid task number to an array index. */
    private static int getTaskIndex(String details, int taskCount, String command) throws ReiException {
        try {
            int taskNumber = Integer.parseInt(details);
            if (taskNumber < 1) {
                throw new ReiException("The task number must be at least 1. Try: " + command
                        + " 1\nLet's try that again!");
            }
            if (taskNumber > taskCount) {
                throw new ReiException("Task " + taskNumber + " does not exist.\nLet's try that again!");
            }
            int taskIndex = taskNumber - 1;
            // Callers rely on validation above to produce a safe zero-based list index.
            assert taskIndex >= 0 && taskIndex < taskCount : "Validated task index must be in bounds";
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new ReiException("Try a number from 1 to " + taskCount + "!");
        }
    }

    /** Contains a command's response text and whether the application should close. */
    public record CommandResult(String response, boolean shouldExit, boolean isError) {
    }

    /** Contains loaded tasks and an optional startup warning. */
    private record LoadState(List<Task> tasks, String warning) {
    }
}
