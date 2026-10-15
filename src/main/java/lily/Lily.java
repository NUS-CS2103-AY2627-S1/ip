package lily;

import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lily.exception.LilyException;
import lily.parser.DateTimeParser;
import lily.parser.Parser;
import lily.parser.Parser.ParsedCommand;
import lily.storage.Storage;
import lily.storage.Storage.LoadResult;
import lily.task.Deadline;
import lily.task.Event;
import lily.task.Task;
import lily.task.TaskList;
import lily.ui.Ui;

/** Runs Lily's command-line task manager. */
public class Lily {
    /** Greeting shared by both the command-line and graphical interfaces. */
    public static final String WELCOME_MESSAGE = "Hello, I'm Lily. Let's give your day a little room to grow.";
    /** Farewell shared by both the command-line and graphical interfaces. */
    public static final String GOODBYE_MESSAGE = "Take care. I'll keep your plans safe until you return.";
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startupMessage;

    /**
     * Sets up Lily against the given save-file path, loading any tasks already
     * saved
     * there.
     *
     * @param filePath path to the save file, e.g. {@code "data/lily.txt"}
     */
    public Lily(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        LoadResult loadResult = storage.loadWithReport();
        tasks = new TaskList(loadResult.tasks());
        startupMessage = buildStartupMessage(loadResult.warnings());
    }

    /** Returns the greeting followed by any warnings produced while loading tasks. */
    public String getStartupMessage() {
        return startupMessage;
    }

    private String buildStartupMessage(List<String> warnings) {
        if (warnings.isEmpty()) {
            return WELCOME_MESSAGE;
        }
        StringBuilder message = new StringBuilder(WELCOME_MESSAGE)
                .append("\n\nStartup notice:");
        warnings.forEach(warning -> message.append("\n- ").append(warning));
        return message.toString();
    }

    /**
     * Processes one command and returns Lily's reply. Both the CLI and the GUI
     * display this exact reply, so command behaviour is defined in one place.
     *
     * @param input command typed by the user
     * @return a human-readable reply
     */
    public String getResponse(String input) {
        return getResponseResult(input).message();
    }

    /**
     * Processes one command and returns both Lily's reply and whether it reports
     * a user-facing error. The GUI uses this flag to present errors distinctly.
     *
     * @param input command typed by the user
     * @return the reply together with its error status
     */
    public Response getResponseResult(String input) {
        if (input == null || input.isBlank()) {
            return new Response("What would you like to tend to? Enter a command to get started.", true, false);
        }

        try {
            CommandResult result = executeCommand(input);
            if (result.changedTaskList()) {
                storage.save(tasks.toList());
            }
            return new Response(result.response(), result.isError(), result.shouldExit());
        } catch (LilyException | IOException e) {
            return new Response(e.getMessage(), true, false);
        } catch (RuntimeException e) {
            return new Response("Something went wrong handling that command: " + e.getMessage(), true, false);
        }
    }

    /** Executes a parsed command without performing persistence. */
    private CommandResult executeCommand(String userInput) throws LilyException {
        ParsedCommand parsedCommand = Parser.parseCommand(userInput);
        String command = parsedCommand.commandWord();
        String argument = parsedCommand.arguments();
        switch (command) {
        case "bye":
            requireNoArguments(command, argument);
            return exiting(GOODBYE_MESSAGE);
        case "list":
            requireNoArguments(command, argument);
            return unchanged(formatTaskList(tasks.toList(), "Here's your little garden of tasks:"));
        case "mark":
            return changeTaskStatus(argument, true);
        case "unmark":
            return changeTaskStatus(argument, false);
        case "todo":
        case "deadline":
        case "event":
            return addTask(command, userInput);
        case "delete":
            return deleteTask(argument);
        case "find":
            return findTasks(argument);
        case "schedule":
            requireAtMostOneArgument(command, argument);
            return showSchedule(argument);
        default:
            return error("I’m not sure how to tend to that yet. "
                    + "Try a command such as list, todo, deadline, or event.");
        }
    }

    private CommandResult changeTaskStatus(String argument, boolean shouldMarkDone) throws LilyException {
        String command = shouldMarkDone ? "mark" : "unmark";
        int index = parseRequiredTaskIndex(argument, command);
        if (!tasks.containsIndex(index)) {
            return error("I couldn’t find that task in your garden. Check list and try again.");
        }

        Task task = shouldMarkDone ? markTask(index) : unmarkTask(index);
        String response = shouldMarkDone ? "Nice! I've marked this task as done:\n  " + task
                : "OK, I've marked this task as not done yet:\n  " + task;
        return changed(response);
    }

    private CommandResult addTask(String command, String userInput) throws LilyException {
        Task task = switch (command) {
        case "todo" -> Parser.parseTodo(userInput);
        case "deadline" -> Parser.parseDeadline(userInput);
        case "event" -> Parser.parseEvent(userInput);
        default -> throw new IllegalArgumentException("Unsupported task command: " + command);
        };
        tasks.add(task);
        return changed("Planted it on your list:\n  " + task + "\nYou now have "
                + tasks.size() + " tasks to tend.");
    }

    private CommandResult deleteTask(String argument) throws LilyException {
        int index = parseRequiredTaskIndex(argument, "delete");
        if (!tasks.containsIndex(index)) {
            return error("I couldn’t find that task in your garden. Check list and try again.");
        }
        Task removed = tasks.remove(index);
        return changed("All cleared away:\n  " + removed + "\nYou now have "
                + tasks.size() + " tasks to tend.");
    }

    private CommandResult findTasks(String argument) throws LilyException {
        if (argument.isEmpty()) {
            throw new LilyException("What should I look for? Add one keyword after find.");
        }
        requireSingleArgument("find", argument);
        return unchanged(formatTaskList(tasks.findTasks(argument), "Here are the matching tasks in your list:"));
    }

    /** Builds a read-only chronological view of dated tasks for one day. */
    private CommandResult showSchedule(String argument) throws LilyException {
        LocalDate selectedDate = Parser.parseScheduleDate(argument, LocalDate.now());
        List<ScheduleEntry> entries = findScheduleEntries(selectedDate);
        String displayedDate = DateTimeParser.formatForDisplay(selectedDate.atStartOfDay());
        if (entries.isEmpty()) {
            return unchanged("There are no scheduled tasks for " + displayedDate + ".");
        }

        StringBuilder response = new StringBuilder("Here is your schedule for ")
                .append(displayedDate).append(":");
        appendScheduleSection(response, "Not completed:", entries, false);
        appendScheduleSection(response, "Completed:", entries, true);
        return unchanged(response.toString());
    }

    /** Returns matching deadlines and events while retaining their list numbers. */
    private List<ScheduleEntry> findScheduleEntries(LocalDate selectedDate) {
        List<Task> taskSnapshot = tasks.toList();
        List<ScheduleEntry> entries = new ArrayList<>();
        for (int i = 0; i < taskSnapshot.size(); i++) {
            Task task = taskSnapshot.get(i);
            if (task instanceof Deadline deadline
                    && deadline.getBy().toLocalDate().equals(selectedDate)) {
                entries.add(new ScheduleEntry(i + 1, task,
                        !deadline.hasExplicitTime(),
                        deadline.getBy().toLocalTime()));
            } else if (task instanceof Event event
                    && !selectedDate.isBefore(event.getFrom().toLocalDate())
                    && !selectedDate.isAfter(event.getTo().toLocalDate())) {
                boolean hasNoDisplayedTime = !event.hasExplicitFromTime() && !event.hasExplicitToTime();
                LocalTime effectiveTime = event.getFrom().toLocalDate().isBefore(selectedDate)
                        ? LocalTime.MIDNIGHT : event.getFrom().toLocalTime();
                entries.add(new ScheduleEntry(i + 1, task, hasNoDisplayedTime, effectiveTime));
            }
        }
        entries.sort(Comparator.comparingInt((ScheduleEntry entry) -> entry.hasNoDisplayedTime() ? 0 : 1)
                .thenComparing(ScheduleEntry::effectiveTime)
                .thenComparingInt(ScheduleEntry::taskNumber));
        return entries;
    }

    /** Appends one completion-status section, omitting it when there are no entries. */
    private void appendScheduleSection(StringBuilder response, String heading,
            List<ScheduleEntry> entries, boolean isDone) {
        boolean hasMatchingEntry = entries.stream().anyMatch(entry -> entry.task().isDone() == isDone);
        if (!hasMatchingEntry) {
            return;
        }
        response.append("\n").append(heading);
        entries.stream()
                .filter(entry -> entry.task().isDone() == isDone)
                .forEach(entry -> response.append("\n").append(entry.taskNumber())
                        .append(". ").append(entry.task()));
    }

    private CommandResult changed(String response) {
        return new CommandResult(response, true, false, false);
    }

    private CommandResult unchanged(String response) {
        return new CommandResult(response, false, false, false);
    }

    /** Builds a successful response that asks the active interface to close. */
    private CommandResult exiting(String response) {
        return new CommandResult(response, false, false, true);
    }

    /** Builds an unchanged response that explains an invalid user command. */
    private CommandResult error(String response) {
        return new CommandResult(response, false, true, false);
    }

    private Task markTask(int index) {
        tasks.mark(index);
        return tasks.get(index);
    }

    private Task unmarkTask(int index) {
        tasks.unmark(index);
        return tasks.get(index);
    }

    private int parseRequiredTaskIndex(String argument, String command) throws LilyException {
        if (argument.isEmpty()) {
            throw new LilyException("Which task should I " + command
                    + "? Give me its number, e.g. " + command + " 2.");
        }
        return Parser.parseTaskIndex(argument);
    }

    /** Rejects arguments supplied to a command that accepts none. */
    private void requireNoArguments(String command, String argument) throws LilyException {
        if (!argument.isEmpty()) {
            throw new LilyException("The " + command
                    + " command is ready as it is—it doesn’t need anything after it.");
        }
    }

    /** Rejects commands whose optional/required argument contains another token. */
    private void requireAtMostOneArgument(String command, String argument) throws LilyException {
        if (argument.contains(" ")) {
            throw new LilyException("I can show one day at a time. "
                    + "Give schedule one date, today, or tomorrow.");
        }
    }

    /** Rejects a command argument containing more than one token. */
    private void requireSingleArgument(String command, String argument) throws LilyException {
        if (argument.contains(" ")) {
            throw new LilyException("The '" + command + "' command accepts exactly one argument.");
        }
    }

    private String formatTaskList(List<Task> taskList, String heading) {
        if (taskList.isEmpty()) {
            return "Your list is clear—a peaceful patch of soil.";
        }
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(taskList.get(i));
        }
        return response.toString();
    }

    /** Represents a command reply and whether it needs to be saved. */
    /** A reply together with the presentation and application-lifecycle status needed by an interface. */
    public record Response(String message, boolean isError, boolean shouldExit) {
    }

    /** Represents a command reply and its persistence, error, and exit effects. */
    private record CommandResult(String response, boolean changedTaskList, boolean isError, boolean shouldExit) {
    }

    /** A dated task together with its original number and per-day sorting data. */
    private record ScheduleEntry(int taskNumber, Task task, boolean hasNoDisplayedTime,
            LocalTime effectiveTime) {
    }

    /** Runs the read-command/act/respond loop until the user says {@code bye}. */
    public void run() {
        ui.showWelcome(getStartupMessage());

        while (true) {
            String userInput = ui.readCommand();
            if (userInput == null) {
                ui.showResponse(GOODBYE_MESSAGE);
                break;
            }
            Response response = getResponseResult(userInput);
            ui.showResponse(response.message());
            if (response.shouldExit()) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Lily("data/lily.txt").run();
    }
}
