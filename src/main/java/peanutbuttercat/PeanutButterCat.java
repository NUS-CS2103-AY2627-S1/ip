package peanutbuttercat;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Starts the PeanutButterCat chatbot.
 */
public class PeanutButterCat {
    /** Relative, OS-independent location used for the application's saved tasks. */
    private static final String DEFAULT_STORAGE_PATH = "data/peanutbuttercat.txt";

    private final Storage storage;
    private final Parser parser;
    private final Ui ui;
    private final TaskList tasks;
    private final Clock clock;
    private final String startupWarning;

    /** Creates the chatbot backed by its default task storage file. */
    public PeanutButterCat() {
        this(new Storage(DEFAULT_STORAGE_PATH), new Parser(), Clock.systemDefaultZone());
    }

    /**
     * Creates the chatbot with collaborators supplied for application setup and testing.
     *
     * @param storage Storage used to load and save tasks.
     * @param parser Parser used to interpret commands.
     */
    PeanutButterCat(Storage storage, Parser parser) {
        this(storage, parser, Clock.systemDefaultZone());
    }

    /**
     * Creates the chatbot with collaborators and a clock supplied for deterministic testing.
     *
     * @param storage Storage used to load and save tasks.
     * @param parser Parser used to interpret commands.
     * @param clock Clock used to obtain completion and statistics dates.
     */
    PeanutButterCat(Storage storage, Parser parser, Clock clock) {
        this.storage = storage;
        this.parser = parser;
        this.ui = new Ui();
        this.tasks = new TaskList(storage.load());
        this.clock = clock;
        this.startupWarning = storage.getLoadWarning();
    }

    /**
     * Runs the chatbot and responds to commands read from standard input.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        PeanutButterCat peanutButterCat = new PeanutButterCat();
        Ui ui = new Ui();
        ui.showWelcome(peanutButterCat.getWelcomeMessage());

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            ui.showLine();
            System.out.println(peanutButterCat.getResponse(command));
            ui.showLine();
            if (peanutButterCat.isExitCommand(command)) {
                break;
            }
        }
    }

    /**
     * Returns the chatbot's reply to a command and updates its stored task list when needed.
     *
     * @param input Raw command entered by the user.
     * @return The reply for the user interface to display.
     */
    public String getResponse(String input) {
        String command = input == null ? "" : input.trim();
        CommandType commandType = parser.parseCommandType(command);

        try {
            return switch (commandType) {
                case BYE -> ui.getFarewellMessage();
                case LIST -> ui.getTaskListMessage(tasks);
                case FIND -> {
                    String keyword = parser.getDescription(command, commandType.getCommandWord());
                    yield ui.getMatchingTasksMessage(tasks.findByDescription(keyword));
                }
                case MARK -> updateTaskStatus(command, commandType, true);
                case UNMARK -> updateTaskStatus(command, commandType, false);
                case DELETE -> deleteTask(command, commandType);
                case TODO -> addTodo(command, commandType);
                case DEADLINE -> addDeadline(command);
                case EVENT -> addEvent(command);
                case ON -> ui.getTasksOnDateMessage(parser.parseDate(command), tasks);
                case STATISTICS -> getStatistics(command, commandType);
                case UNKNOWN -> {
                    if (command.isEmpty()) {
                        throw new PeanutButterCatException(
                                "I didn't catch a command. Type something for me to do, purr-lease!");
                    }
                    throw new PeanutButterCatException(
                            "My whiskers can't sort that command yet. Try another scoop, purr-lease!");
                }
            };
        } catch (PeanutButterCatException | StorageException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    /** Returns the greeting used when a user first meets the chatbot. */
    public String getWelcomeMessage() {
        String welcomeMessage = ui.getWelcomeMessage();
        return startupWarning == null
                ? welcomeMessage
                : welcomeMessage + System.lineSeparator() + startupWarning;
    }

    /**
     * Returns the type of command represented by the supplied input.
     *
     * @param input Raw command entered by the user.
     * @return The command type, or {@link CommandType#UNKNOWN} when it is not recognized.
     */
    public CommandType getCommandType(String input) {
        return parser.parseCommandType(input);
    }

    /** Returns whether the supplied input requests that the application exit. */
    public boolean isExitCommand(String input) {
        return parser.parseCommandType(input) == CommandType.BYE;
    }

    private String deleteTask(String command, CommandType commandType)
            throws PeanutButterCatException, StorageException {
        assert commandType == CommandType.DELETE : "Delete handler must receive a delete command";

        int taskIndex = parser.parseTaskIndex(command, commandType.getCommandWord(), tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Parsed task index must exist before deletion";
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.insert(taskIndex, removedTask);
            throw exception;
        }
        return ui.getTaskDeletedMessage(removedTask, tasks.size());
    }

    private String addTodo(String command, CommandType commandType)
            throws PeanutButterCatException, StorageException {
        assert commandType == CommandType.TODO : "Todo handler must receive a todo command";

        String description = parser.getDescription(command, commandType.getCommandWord());
        Task todo = new Todo(description);
        return addTask(todo);
    }

    private String addDeadline(String command) throws PeanutButterCatException, StorageException {
        Task deadline = parser.parseDeadline(command);
        return addTask(deadline);
    }

    private String addEvent(String command) throws PeanutButterCatException, StorageException {
        Task event = parser.parseEvent(command);
        return addTask(event);
    }

    private String addTask(Task task) throws PeanutButterCatException, StorageException {
        if (tasks.containsSameDetails(task)) {
            throw new PeanutButterCatException(
                    "That task is already in the jar. Try adding something different!");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        return ui.getTaskAddedMessage(task, tasks.size());
    }

    private String getStatistics(String command, CommandType commandType) throws PeanutButterCatException {
        assert commandType == CommandType.STATISTICS
                : "Statistics handler must receive a statistics command";

        parser.validateNoArguments(command, commandType.getCommandWord());
        LocalDate endDate = LocalDate.now(clock);
        LocalDate startDate = endDate.minusDays(6);
        long completedTaskCount = tasks.countCompletedBetween(startDate, endDate);
        long unknownDateTaskCount = tasks.countCompletedWithUnknownDate();
        return ui.getStatisticsMessage(completedTaskCount, unknownDateTaskCount);
    }

    /**
     * Marks or unmarks the task selected by the command.
     *
     * @param command Full mark or unmark command entered by the user.
     * @param commandType Type of status-changing command.
     * @param isDone Whether the selected task should be marked as done.
     * @throws PeanutButterCatException If the task number is invalid.
     * @throws StorageException If the updated task list cannot be saved.
     */
    private String updateTaskStatus(String command, CommandType commandType, boolean isDone)
            throws PeanutButterCatException, StorageException {
        assert commandType == CommandType.MARK || commandType == CommandType.UNMARK
                : "Status handler must receive a mark or unmark command";

        int taskIndex = parser.parseTaskIndex(command, commandType.getCommandWord(), tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Parsed task index must exist before status update";
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        LocalDate previousCompletionDate = task.getCompletionDate();
        if (isDone == wasDone) {
            String status = isDone ? "already marked as done" : "already active";
            throw new PeanutButterCatException("That task is " + status + ". No change needed!");
        }
        if (isDone) {
            task.markAsDone(LocalDate.now(clock));
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            task.restoreCompletion(wasDone, previousCompletionDate);
            throw exception;
        }
        return ui.getTaskStatusMessage(task, isDone);
    }
}
