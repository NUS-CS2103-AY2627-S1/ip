package bobby;

import java.io.IOException;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import bobby.command.Parser;
import bobby.exception.BobbyException;
import bobby.storage.Storage;
import bobby.task.Task;
import bobby.task.TaskList;
import bobby.ui.Ui;

/**
 * Executes task-list commands for Bobby's console and graphical interfaces.
 */
public class Bobby {
    private static final DateTimeFormatter STATISTICS_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final String FAREWELL_MESSAGE =
            "     I humbly take my leave. May good fortune attend thee until next we meet.";

    private final TaskList tasks;
    private final boolean hasLoadingError;
    private final Clock clock;

    /**
     * Describes text returned by Bobby and whether it represents an error.
     */
    public static final class CommandResult {
        private final String message;
        private final boolean isError;

        /**
         * Creates a command result for presentation by a user interface.
         *
         * @param message the text to display.
         * @param isError whether the command failed.
         */
        private CommandResult(String message, boolean isError) {
            this.message = message;
            this.isError = isError;
        }

        /**
         * Returns the text to display.
         *
         * @return the response text.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns whether the command failed.
         *
         * @return {@code true} when the response describes an error.
         */
        public boolean isError() {
            return isError;
        }
    }

    /**
     * Creates a Bobby instance backed by the task list saved on disk.
     */
    public Bobby() {
        TaskList loadedTasks;
        boolean didLoadingFail;
        try {
            loadedTasks = new TaskList(Storage.load());
            didLoadingFail = false;
        } catch (IOException e) {
            loadedTasks = new TaskList();
            didLoadingFail = true;
        }
        tasks = loadedTasks;
        hasLoadingError = didLoadingFail;
        clock = Clock.systemDefaultZone();
    }

    /**
     * Creates a Bobby instance with a supplied task list.
     *
     * @param tasks the initial task list.
     */
    Bobby(TaskList tasks) {
        this(tasks, Clock.systemDefaultZone());
    }

    /**
     * Creates a Bobby instance with a supplied task list and clock.
     *
     * @param tasks the initial task list.
     * @param clock the clock used to record and report task completion times.
     */
    Bobby(TaskList tasks, Clock clock) {
        assert tasks != null : "Initial task list must not be null";
        assert clock != null : "Clock must not be null";
        this.tasks = tasks;
        hasLoadingError = false;
        this.clock = clock;
    }

    /**
     * Starts the console application and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Bobby bobby = new Bobby();
        if (bobby.hasLoadingError) {
            ui.showLoadingError();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (Parser.isByeCommand(input)) {
                break;
            }
            ui.showLine();
            ui.showResponse(bobby.getResponse(input));
            ui.showLine();
        }
        ui.showGoodbye();
    }

    /**
     * Executes a user command and returns Bobby's response.
     *
     * @param input the user's raw command.
     * @return Bobby's response for the command.
     */
    public String getResponse(String input) {
        return getCommandResult(input).getMessage();
    }

    /**
     * Returns whether the saved task list could not be loaded at startup.
     *
     * @return {@code true} when Bobby started with an empty list after a loading failure.
     */
    public boolean hasLoadingError() {
        return hasLoadingError;
    }

    /**
     * Executes a user command and returns presentation metadata with Bobby's response.
     *
     * @param input the user's raw command.
     * @return the command result, including whether it represents an error.
     */
    public CommandResult getCommandResult(String input) {
        if (isExitCommand(input)) {
            return new CommandResult(FAREWELL_MESSAGE, false);
        }

        try {
            Parser.Command command = Parser.parse(input, tasks.size());
            return new CommandResult(execute(command), false);
        } catch (BobbyException e) {
            return new CommandResult("     " + e.getMessage(), true);
        }
    }

    /**
     * Returns whether a command should terminate Bobby.
     *
     * @param input the user's raw command.
     * @return {@code true} when the command is {@code bye}.
     */
    public boolean isExitCommand(String input) {
        return Parser.isByeCommand(input);
    }

    /**
     * Executes a parsed command and returns its success response.
     *
     * @param command the validated command to execute.
     * @return Bobby's success response.
     * @throws BobbyException if changed tasks cannot be saved.
     */
    private String execute(Parser.Command command) throws BobbyException {
        switch (command.getType()) {
            case LIST:
                return formatTasks("Behold, the full register of thy appointed duties:",
                        "The royal register standeth presently unburdened; no duty hath yet been inscribed.",
                        tasks.asList());
            case STATS:
                return formatStatistics();
            case FIND:
                return formatTasks("Behold, the duties answering thy inquiry:",
                        "Alas, no duty within the register answereth thy inquiry.",
                        tasks.findTasksContaining(command.getKeyword()));
            case ADD:
                return addTask(command.getTask());
            case MARK:
                return markTask(command.getTaskIndex(), true);
            case UNMARK:
                return markTask(command.getTaskIndex(), false);
            case DELETE:
                return deleteTask(command.getTaskIndex());
            default:
                throw new AssertionError("Unhandled command type: " + command.getType());
        }
    }

    /**
     * Adds and saves a task, then returns a confirmation.
     *
     * @param task the task to add.
     * @return the addition confirmation.
     * @throws BobbyException if the task list cannot be saved.
     */
    private String addTask(Task task) throws BobbyException {
        if (tasks.hasTaskWithSameDetails(task)) {
            throw new BobbyException(
                    "That very duty already standeth upon the royal register.");
        }

        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.add(task);
        saveTasks(updatedTasks);
        tasks.add(task);
        return "     It is done. By thy command, I have inscribed this duty upon the royal register:\n"
                + "       " + task + "\n"
                + "     " + formatAddedTaskCount(tasks.size());
    }

    /**
     * Changes and saves a task's completion state, then returns a confirmation.
     *
     * @param index the zero-based task index.
     * @param isDone whether the task should be marked as done.
     * @return the status-change confirmation.
     * @throws BobbyException if the task list cannot be saved.
     */
    private String markTask(int index, boolean isDone) throws BobbyException {
        Task task = tasks.get(index);
        boolean isStatusChanged = task.isDone() != isDone;
        if (isStatusChanged) {
            Optional<LocalDateTime> previousCompletionDateTime = task.getCompletionDateTime();
            if (isDone) {
                tasks.markAsDone(index, LocalDateTime.now(clock));
            } else {
                tasks.markAsNotDone(index);
            }
            try {
                saveTasks(tasks.asList());
            } catch (BobbyException e) {
                restoreTaskStatus(task, !isDone, previousCompletionDateTime);
                throw e;
            }
        }

        String message = isDone
                ? "     Most excellent. I have proclaimed this duty duly accomplished:"
                : "     As thou commandest. I have restored this duty to the ranks of unfinished business:";
        return message + "\n       " + tasks.get(index);
    }

    /**
     * Removes and saves a task, then returns a confirmation.
     *
     * @param index the zero-based task index.
     * @return the deletion confirmation.
     * @throws BobbyException if the task list cannot be saved.
     */
    private String deleteTask(int index) throws BobbyException {
        Task removedTask = tasks.get(index);
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.remove(index);
        saveTasks(updatedTasks);
        tasks.remove(index);
        return "     It is done. I have struck this duty from the royal register:\n"
                + "       " + removedTask + "\n"
                + "     " + formatRemainingTaskCount(tasks.size());
    }

    /**
     * Restores a task after its changed completion state could not be saved.
     *
     * @param task the task whose state must be restored.
     * @param wasDone whether the task was completed before the failed change.
     * @param completionDateTime the previous completion time, when known.
     */
    private static void restoreTaskStatus(Task task, boolean wasDone,
            Optional<LocalDateTime> completionDateTime) {
        if (!wasDone) {
            task.markAsNotDone();
        } else if (completionDateTime.isPresent()) {
            task.markAsDone(completionDateTime.get());
        } else {
            task.markAsDone();
        }
    }

    /**
     * Saves the task list and converts storage failures into a user-facing error.
     *
     * @throws BobbyException if the task list cannot be saved.
     */
    private void saveTasks(List<Task> tasksToSave) throws BobbyException {
        try {
            Storage.save(tasksToSave);
        } catch (IOException e) {
            throw new BobbyException(
                    "Grievous tidings: I was unable to commit thy duties unto the permanent archive.");
        }
    }

    /**
     * Formats the register size after a task is added.
     *
     * @param taskCount the number of tasks in the register.
     * @return a grammatically correct task-count sentence.
     */
    private static String formatAddedTaskCount(int taskCount) {
        return taskCount == 1
                ? "One duty now standeth upon the register."
                : "There now stand " + taskCount + " duties upon the register.";
    }

    /**
     * Formats the register size after a task is removed.
     *
     * @param taskCount the number of tasks remaining in the register.
     * @return a grammatically correct task-count sentence.
     */
    private static String formatRemainingTaskCount(int taskCount) {
        if (taskCount == 0) {
            return "The register now standeth empty.";
        }
        return taskCount == 1
                ? "One duty now remaineth upon the register."
                : "There now remain " + taskCount + " duties upon the register.";
    }

    /**
     * Formats a numbered task list for display.
     *
     * @param heading the text shown before the list.
     * @param emptyMessage the text shown when no tasks are present.
     * @param displayedTasks the tasks to display.
     * @return the formatted task-list response.
     */
    private static String formatTasks(String heading, String emptyMessage, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder(heading);
        if (displayedTasks.isEmpty()) {
            return response.append('\n').append(emptyMessage).toString();
        }
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append("\n     ")
                    .append(i + 1)
                    .append('.')
                    .append(displayedTasks.get(i));
        }
        return response.toString();
    }

    /**
     * Formats statistics for the current Monday-to-Sunday calendar week.
     *
     * @return the current task statistics.
     */
    private String formatStatistics() {
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        LocalDate weekStartDate = currentDateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = weekStartDate.plusDays(6);
        LocalDateTime weekStartDateTime = weekStartDate.atStartOfDay();
        LocalDateTime nextWeekStartDateTime = weekStartDate.plusWeeks(1).atStartOfDay();
        long completedThisWeek = tasks.countTasksCompletedBetween(
                weekStartDateTime, nextWeekStartDateTime);
        long completedOverall = tasks.countCompletedTasks();
        long pending = tasks.size() - completedOverall;

        return "Attend now to the formal reckoning of thy duties:\n"
                + "     Period under review: " + weekStartDate.format(STATISTICS_DATE_FORMAT)
                + " to " + weekEndDate.format(STATISTICS_DATE_FORMAT) + "\n"
                + "     Accomplished within the present week: " + completedThisWeek + "\n"
                + "     Accomplished across all recorded time: " + completedOverall + "\n"
                + "     Yet awaiting fulfilment: " + pending + "\n"
                + "     Total duties inscribed: " + tasks.size();
    }
}
