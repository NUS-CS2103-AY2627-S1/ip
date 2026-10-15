package happybot;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import happybot.task.DatedTask;
import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.Task;

/**
 * Runs the HappyBot chatbot application.
 *
 * <p>A HappyBot object owns the task list, the storage that saves it, and the user interface
 * that reads commands and prints replies. Ui builds the console text, Parser reads the typed
 * command and TaskList keeps the tasks, which leaves this class to decide what each command
 * asks of the three of them.
 */
public class HappyBot {
    private static final Path DATA_FILE_PATH = Path.of("data", "HappyBot.txt");

    /** User interface that reads the commands and prints every reply. */
    private final Ui ui;

    /** Storage that loads the saved tasks at startup and saves them after every change. */
    private final Storage storage;

    /** Clock used to record task completions, identify the current week and validate new tasks. */
    private final Clock clock;

    /** Tasks held by this HappyBot for the length of the session. */
    private TaskList tasks;

    /** Warning about saved data that should be shown after the welcome message, if any. */
    private final String startupNotice;

    /** Whether another HappyBot session has exclusive access to the data file. */
    private boolean isDataFileInUse;

    /**
     * Creates a HappyBot that uses the default data file.
     */
    public HappyBot() {
        this(DATA_FILE_PATH, Clock.systemDefaultZone());
    }

    /**
     * Creates a HappyBot that keeps its tasks in the specified data file.
     *
     * @param filePath The location of the task data file.
     */
    public HappyBot(Path filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates a HappyBot that uses the specified data file and clock.
     *
     * <p>The clock is supplied by tests to make the week boundaries predictable. Production
     * constructors use the system-default clock.
     *
     * @param filePath The location of the task data file.
     * @param clock The clock used for the current local date.
     */
    HappyBot(Path filePath, Clock clock) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.clock = clock;
        this.tasks = new TaskList();
        this.startupNotice = loadSavedTasks();
    }

    /**
     * Loads the saved tasks and returns a warning about data that could not be read.
     *
     * <p>A data file that cannot be read must not end the session, so the problem is reported
     * and HappyBot starts with the empty task list made by the constructor.
     */
    private String loadSavedTasks() {
        try {
            if (!storage.tryAcquireDataFileLock()) {
                isDataFileInUse = true;
                return ui.formatDataFileInUse(storage.getFilePath());
            }

            tasks = new TaskList(storage.loadTasks());
            if (storage.getSkippedLineCount() > 0) {
                return ui.formatSkippedLinesNotice(storage.getSkippedLineCount());
            }
        } catch (IOException e) {
            return ui.formatLoadingError(storage.getFilePath());
        }

        return null;
    }

    /**
     * Saves the tasks and returns a warning when the data file cannot be written.
     *
     * <p>A failed save must not end the session, so the problem is reported and the in-memory
     * task list is kept for the rest of the run.
     */
    private String saveTasks() {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (IOException e) {
            return ui.formatSavingError(storage.getFilePath()) + "\n";
        }

        return "";
    }

    /**
     * Adds a task, saves the updated list, and returns a confirmation.
     *
     * @param taskToAdd The task to add.
     * @throws HappyBotException If an equivalent task is already in the list.
     */
    private String addTask(Task taskToAdd) throws HappyBotException {
        tasks.add(taskToAdd);
        return saveTasks() + ui.formatAddedTask(taskToAdd, tasks.getSize());
    }

    /**
     * Adds the deadline described by a command after checking that it has not already passed.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If the command is invalid or the deadline has already passed.
     */
    private String addDeadline(String body) throws HappyBotException {
        Parser.DeadlineDetails deadlineDetails = Parser.parseDeadlineDetails(body);
        validateNotPast(deadlineDetails.getDueDateTime(), "A deadline cannot be due in the past.");

        Deadline deadline = new Deadline(deadlineDetails.getDescription(),
                deadlineDetails.getDueDateTime().getDateTime());
        return addTask(deadline);
    }

    /**
     * Adds the event described by a command after checking that its end has not already passed.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If the command is invalid, its range is invalid or it has ended.
     */
    private String addEvent(String body) throws HappyBotException {
        Parser.EventDetails eventDetails = Parser.parseEventDetails(body);
        Event event = createEvent(eventDetails);
        validateNotPast(eventDetails.getEndTime(), "An event cannot end in the past.");
        return addTask(event);
    }

    /**
     * Creates an event and turns an invalid event range into a command error.
     *
     * @param eventDetails The parsed description and dates of the event.
     * @return The event described by the supplied details.
     * @throws HappyBotException If the event does not start before it ends.
     */
    private Event createEvent(Parser.EventDetails eventDetails) throws HappyBotException {
        try {
            return new Event(eventDetails.getStartTime().getDateTime(),
                    eventDetails.getEndTime().getDateTime(), eventDetails.getDescription());
        } catch (IllegalArgumentException e) {
            throw new HappyBotException(e.getMessage());
        }
    }

    /**
     * Rejects a parsed date and time that has already passed.
     *
     * <p>A supplied time is compared to the current time. A date without a supplied time is
     * compared only to the current date, so it remains valid throughout that date.
     *
     * @param taskDateTime The date and optional time to validate.
     * @param errorMessage The message to show when the date and time has already passed.
     * @throws HappyBotException If the supplied date and time has already passed.
     */
    private void validateNotPast(Parser.ParsedDateTime taskDateTime, String errorMessage)
            throws HappyBotException {
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        if (taskDateTime.hasTime() && !taskDateTime.getDateTime().isAfter(currentDateTime)) {
            throw new HappyBotException(errorMessage);
        }

        if (!taskDateTime.hasTime()
                && taskDateTime.getDateTime().toLocalDate().isBefore(currentDateTime.toLocalDate())) {
            throw new HappyBotException(errorMessage);
        }
    }

    /**
     * Marks a selected task as completed and returns a confirmation.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private String markTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to mark.");
        }

        Task taskToMark = tasks.getTask(Parser.parseTaskNumber(body));
        taskToMark.markAsDone(LocalDate.now(clock));
        return saveTasks() + ui.formatMarkedTask(taskToMark);
    }

    /**
     * Marks a selected task as not completed and returns a confirmation.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private String unmarkTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to unmark.");
        }

        Task taskToUnmark = tasks.getTask(Parser.parseTaskNumber(body));
        taskToUnmark.unmarkAsDone();
        return saveTasks() + ui.formatUnmarkedTask(taskToUnmark);
    }

    /**
     * Deletes a selected task from the list and returns a confirmation.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If there are no tasks or the task number is invalid.
     */
    private String deleteTask(String body) throws HappyBotException {
        if (tasks.isEmpty()) {
            throw new HappyBotException("There are no tasks to delete.");
        }

        Task taskToDelete = tasks.delete(Parser.parseTaskNumber(body));
        return saveTasks() + ui.formatDeletedTask(taskToDelete, tasks.getSize());
    }

    /**
     * Returns the deadlines due on the specified date.
     *
     * <p>Each deadline is collected under the number it has in the full list, so one shown here
     * can be marked or deleted with the number displayed beside it.
     *
     * @param date The date the user asked about.
     */
    private String getDeadlinesDueOn(LocalDate date) {
        // Reusing the task display keeps one source of truth for how a date is written.
        String displayedDate = DatedTask.formatDate(date);
        return ui.formatDeadlinesDueOn(displayedDate, tasks.findDeadlinesDueOn(date));
    }

    /**
     * Returns the tasks whose description holds the specified keyword.
     *
     * @param keyword The keyword the user asked about.
     */
    private String getMatchingTasks(String keyword) {
        return ui.formatMatchingTasks(keyword, tasks.findTasksContaining(keyword));
    }

    /**
     * Returns the current week's task statistics.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If the command holds an unexpected argument.
     */
    private String getStatistics(String body) throws HappyBotException {
        Parser.validateStatsCommand(body);
        return ui.formatStatistics(tasks.getStatistics(LocalDate.now(clock)));
    }

    /**
     * Returns the welcome message and any warning raised while loading saved tasks.
     *
     * @return The message to show when a graphical session starts.
     */
    public String getWelcomeMessage() {
        if (startupNotice == null) {
            return ui.getWelcomeMessage();
        }

        return ui.getWelcomeMessage() + "\n\n" + startupNotice.stripLeading();
    }

    /**
     * Executes one command and returns HappyBot's response.
     *
     * @param userInput The command entered by the user.
     * @return HappyBot's response to the command.
     */
    public String getResponse(String userInput) {
        try {
            Parser.validateCommandInput(userInput);
            String command = Parser.parseCommandWord(userInput);
            String body = Parser.parseCommandBody(userInput);

            if (command.equals("bye")) {
                Parser.validateByeCommand(body);
                return ui.getGoodbyeMessage();
            }

            if (isDataFileInUse) {
                throw new HappyBotException(ui.getDataFileInUseMessage(storage.getFilePath()));
            }

            return switch (command) {
                case "list" -> getTaskList(body);
                case "mark" -> markTask(body);
                case "unmark" -> unmarkTask(body);
                case "delete" -> deleteTask(body);
                case "due" -> getDeadlinesDueOn(Parser.parseDueDate(body));
                case "find" -> getMatchingTasks(Parser.parseKeyword(body));
                case "stats" -> getStatistics(body);
                case "todo" -> addTask(Parser.parseToDo(body));
                case "deadline" -> addDeadline(body);
                case "event" -> addEvent(body);
                default -> throw new HappyBotException("I don't know what that means :-(");
            };
        } catch (HappyBotException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Returns the full task list after checking that list has no arguments.
     *
     * @param body The command text after the command word.
     * @return The formatted task list.
     * @throws HappyBotException If the command holds an unexpected argument.
     */
    private String getTaskList(String body) throws HappyBotException {
        Parser.validateListCommand(body);
        return ui.formatTaskList(tasks.getTasks());
    }

    /**
     * Returns whether the supplied command is a valid request to end the session.
     *
     * @param userInput The command entered by the user.
     * @return True only for a bye command with no arguments.
     */
    public boolean isExitCommand(String userInput) {
        try {
            Parser.validateCommandInput(userInput);
            String command = Parser.parseCommandWord(userInput);
            String body = Parser.parseCommandBody(userInput);
            Parser.validateByeCommand(body);
            return command.equals("bye");
        } catch (HappyBotException e) {
            return false;
        }
    }

    /**
     * Releases resources held for this HappyBot session.
     *
     * <p>Failing to release a lock during shutdown must not turn a normal program exit into an
     * error. The operating system releases the lock when the process ends in that rare case.
     */
    public void close() {
        try {
            storage.releaseDataFileLock();
        } catch (IOException e) {
            // The operating system releases any remaining file lock when HappyBot exits.
        }
    }

    /**
     * Greets the user, then reads and carries out commands until the session ends.
     */
    public void run() {
        boolean isRunning = true;

        ui.showWelcome();
        if (startupNotice != null) {
            ui.showNotice(startupNotice);
        }

        // Stopping at the end of the input also ends the session cleanly when no bye is typed.
        while (isRunning && ui.hasNextCommand()) {
            String userInput = ui.readCommand();
            ui.showDivider();

            if (isExitCommand(userInput)) {
                isRunning = false;
            } else {
                ui.showResponse(getResponse(userInput));
            }
        }

        ui.showGoodbye();
    }

    /**
     * Starts HappyBot.
     *
     * @param args Command-line arguments, which HappyBot does not use.
     */
    public static void main(String[] args) {
        HappyBot happyBot = new HappyBot(DATA_FILE_PATH);
        try {
            happyBot.run();
        } finally {
            happyBot.close();
        }
    }
}
