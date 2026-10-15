package serina;

import java.time.LocalDate;
import java.util.List;

import serina.exception.SerinaError;
import serina.exception.SerinaException;
import serina.parser.CommandParser;
import serina.parser.CommandType;
import serina.parser.DateParser;
import serina.parser.ParsedCommand;
import serina.storage.Storage;
import serina.task.Deadline;
import serina.task.Event;
import serina.task.Task;
import serina.task.TaskList;
import serina.task.Todo;
import serina.ui.Ui;

/** Processes Serina commands for console and graphical user interfaces. */
public class Serina {
    private final Storage storage;
    private final TaskList tasks;
    private final List<ResponseMessage> startupResponses;
    private final boolean isStorageAvailable;

    /** Creates Serina using the default save-file location. */
    public Serina() {
        this(new Storage());
    }

    /**
     * Creates Serina using the supplied storage location.
     *
     * @param storage Storage used to load and save tasks.
     */
    public Serina(Storage storage) {
        this.storage = storage;
        TaskList loadedTasks;
        List<ResponseMessage> loadingResponses;
        boolean canUseStorage;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
            loadingResponses = List.of();
            canUseStorage = true;
        } catch (SerinaException e) {
            loadedTasks = new TaskList();
            loadingResponses = List.of(ResponseMessage.warning(e.getMessage()));
            canUseStorage = false;
        }
        tasks = loadedTasks;
        startupResponses = loadingResponses;
        isStorageAvailable = canUseStorage;
    }

    /**
     * Starts Serina's command-line interface.
     *
     * @param args Command line arguments, which are not used.
     */
    public static void main(String[] args) {
        Serina serina = new Serina();
        try (Ui ui = new Ui()) {
            ui.showMessage(serina.getGreeting());
            serina.getStartupMessages().forEach(ui::showMessage);
            while (ui.hasNextCommand()) {
                CommandResult result = serina.executeCommand(ui.readCommand());
                result.getResponses().forEach(ui::showMessage);
                if (result.shouldExit()) {
                    break;
                }
            }
        }
    }

    /**
     * Returns Serina's greeting.
     *
     * @return Greeting shown when a conversation starts.
     */
    public String getGreeting() {
        return ResponseFormatter.formatGreeting(startupResponses.isEmpty());
    }

    /**
     * Returns messages produced while loading saved tasks.
     *
     * @return Loading warnings in display order.
     */
    public List<String> getStartupMessages() {
        return startupResponses.stream().map(ResponseMessage::getText).toList();
    }

    /**
     * Returns typed messages produced while loading saved tasks.
     *
     * @return Loading warnings in display order.
     */
    public List<ResponseMessage> getStartupResponses() {
        return startupResponses;
    }

    /**
     * Processes one command and returns the responses to display.
     *
     * @param input Command entered by the user.
     * @return Responses and exit behavior produced by the command.
     */
    public CommandResult executeCommand(String input) {
        try {
            ParsedCommand command = CommandParser.parse(input);
            if (command.type() == CommandType.BYE) {
                return new CommandResult(true, ResponseFormatter.formatGoodbye());
            }
            return new CommandResult(false, processCommand(command));
        } catch (SerinaException e) {
            return CommandResult.ofMessages(false, ResponseMessage.error(e.getMessage()));
        }
    }

    private String processCommand(ParsedCommand command) throws SerinaException {
        if (!isStorageAvailable && command.type() != CommandType.HELP) {
            throw new SerinaException(SerinaError.STORAGE_UNAVAILABLE);
        }
        return switch (command.type()) {
            case HELP -> ResponseFormatter.formatHelp();
            case LIST -> ResponseFormatter.formatTaskList(tasks.asList());
            case MARK -> markTask(command.arguments().get(0));
            case UNMARK -> unmarkTask(command.arguments().get(0));
            case DELETE -> deleteTask(command.arguments().get(0));
            case FIND -> ResponseFormatter.formatMatchingTasks(tasks.find(command.arguments().get(0)));
            case TODO, DEADLINE, EVENT -> addTask(createTask(command));
            case BYE -> throw new IllegalStateException("Bye should be handled before command dispatch.");
        };
    }

    private String markTask(String taskNumber) throws SerinaException {
        Task currentTask = tasks.getTask(taskNumber);
        if (currentTask.isDone()) {
            return ResponseFormatter.formatAlreadyMarkedTask(currentTask);
        }
        TaskList candidate = tasks.copy();
        Task task = candidate.getTask(taskNumber);
        task.markAsDone();
        saveAndPublish(candidate);
        return ResponseFormatter.formatMarkedTask(task);
    }

    private String unmarkTask(String taskNumber) throws SerinaException {
        Task currentTask = tasks.getTask(taskNumber);
        if (!currentTask.isDone()) {
            return ResponseFormatter.formatAlreadyUnmarkedTask(currentTask);
        }
        TaskList candidate = tasks.copy();
        Task task = candidate.getTask(taskNumber);
        task.markAsNotDone();
        saveAndPublish(candidate);
        return ResponseFormatter.formatUnmarkedTask(task);
    }

    private String deleteTask(String taskNumber) throws SerinaException {
        TaskList candidate = tasks.copy();
        Task task = candidate.delete(taskNumber);
        saveAndPublish(candidate);
        return ResponseFormatter.formatDeletedTask(task, candidate.size());
    }

    private String addTask(Task task) throws SerinaException {
        TaskList candidate = tasks.copy();
        candidate.add(task);
        saveAndPublish(candidate);
        return ResponseFormatter.formatAddedTask(task, candidate.size());
    }

    private static Task createTask(ParsedCommand command) throws SerinaException {
        List<String> arguments = command.arguments();
        return switch (command.type()) {
            case TODO -> new Todo(arguments.get(0));
            case DEADLINE -> new Deadline(arguments.get(0), DateParser.parseInputDate(arguments.get(1)));
            case EVENT -> createEvent(arguments);
            default -> throw new IllegalArgumentException("Command does not create a task: " + command.type());
        };
    }

    private static Event createEvent(List<String> arguments) throws SerinaException {
        LocalDate startDate = DateParser.parseInputDate(arguments.get(1));
        LocalDate endDate = DateParser.parseInputDate(arguments.get(2));
        if (!endDate.isAfter(startDate)) {
            throw new SerinaException(SerinaError.INVALID_EVENT_DATE_RANGE);
        }
        return new Event(arguments.get(0), startDate, endDate);
    }

    private void saveAndPublish(TaskList candidate) throws SerinaException {
        storage.saveTasks(candidate.asList());
        tasks.replaceWith(candidate);
    }
}
