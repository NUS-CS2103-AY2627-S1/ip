package sevensix;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Formats responses shared by the console and graphical interfaces.
 *
 * <p>Response wording is owned by this UI layer, so the console and the graphical
 * interface show the same wording. The class is a collection of static helpers because it
 * holds no state of its own.
 */
public class Ui {
    /** Defines the application name used by the graphical interface. */
    public static final String APPLICATION_NAME = "SevenSix";
    /** Defines the short product description shown beside the application name. */
    public static final String APPLICATION_SUBTITLE = "PRODUCTIVITY PARTNER";
    /** Defines the small label above the graphical interface introduction. */
    public static final String HEADER_EYEBROW = "COMMAND CENTER";
    /** Defines the graphical interface introduction title. */
    public static final String HEADER_TITLE = "Keep every deliverable moving.";
    /** Defines the graphical interface introduction subtitle. */
    public static final String HEADER_SUBTITLE = "Capture, review, and close out your work in one focused space.";
    /** Defines the readiness label shown in the graphical interface header. */
    public static final String READY_STATUS = "● READY";
    /** Defines the label shown above the graphical command field. */
    public static final String COMPOSER_LABEL = "NEW COMMAND";
    /** Defines the placeholder shown in the graphical command field. */
    public static final String INPUT_PROMPT = "Try: todo review proposal";
    /** Defines the hint shown below the graphical command field. */
    public static final String COMPOSER_HINT = "Press Enter to send  ·  Type help for commands and examples";
    /** Defines the label used by the graphical command button. */
    public static final String SEND_BUTTON_LABEL = "Send command  →";

    /** Explains why an empty command cannot be processed. */
    public static final String EMPTY_COMMAND = "enter a command, such as list or todo <description>.";
    /** Explains why a to-do command needs a description. */
    public static final String MISSING_TODO_DESCRIPTION =
            "a todo needs a description. Let us put some substance behind it.";
    /** Explains the required deadline details. */
    public static final String MISSING_DEADLINE_DETAILS =
            "a deadline needs both a description and a due time to be actionable.";
    /** Explains the required event details. */
    public static final String MISSING_EVENT_DETAILS =
            "an event needs a description, a start, and an end before I can calendar it.";
    /** Explains why a search needs a keyword. */
    public static final String MISSING_FIND_KEYWORD = "a find needs a keyword before I can surface anything.";
    /** Rejects a task number that is not a supported integer. */
    public static final String INVALID_TASK_NUMBER = "please reference a valid deliverable number.";
    /** Rejects a task number outside the current list. */
    public static final String TASK_NOT_FOUND = "that deliverable number is not in your pipeline.";
    /** Explains why undo cannot run before a task change has been recorded. */
    public static final String EMPTY_UNDO_HISTORY = "there is nothing in the rollback history yet.";
    /** Rejects characters that cannot safely round-trip through a one-line record. */
    public static final String INVALID_CHARACTERS = "commands cannot contain line breaks or control characters.";
    /** Explains that argument-free commands cannot accept extra text. */
    public static final String UNEXPECTED_ARGUMENTS = "list, undo, bye, and help do not accept extra parameters.";
    /** Explains why a repeated or misplaced date marker is rejected. */
    public static final String INVALID_PARAMETERS = "supply each date parameter exactly once, in the expected order.";
    /** Describes date input when its calendar values or precision are invalid. */
    public static final String INVALID_DATE = "use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm for dates and times.";
    /** Explains the required event ordering. */
    public static final String INVALID_EVENT_RANGE = "an event must end after it starts; omitted times mean midnight.";
    /** Explains why an existing task cannot be added again. */
    public static final String DUPLICATE_TASK =
            "a deliverable with the same type, description, and dates already exists.";
    /** Reports a blocked load without implying that the data file is empty. */
    public static final String LOAD_FAILURE = "the task file could not be read. Check its path and permissions,"
            + " then restart. Saving is disabled to protect existing data.";
    /** Reports invalid records and protects them from accidental replacement. */
    public static final String CORRUPT_STORAGE = "invalid or duplicate task records were skipped. Back up and repair"
            + " the task file, then restart. Saving is disabled to protect existing data.";
    /** Explains that a failed save leaves both tasks and undo history unchanged. */
    public static final String SAVE_FAILURE = "the change could not be saved. Check the task file and folder"
            + " permissions and available disk space. No tasks or undo history were changed.";

    /** Prefix used to make input errors recognizable in the user interface. */
    private static final String ERROR_PREFIX = "Flagging a blocker: ";

    /** Prevents instantiation of this helper class. */
    private Ui() {
    }

    /**
     * Returns the greeting shown when the chatbot starts.
     *
     * <p>The console and the graphical interface both read the greeting from here so that
     * the two interfaces always introduce the chatbot in the same words.
     *
     * @return the greeting, as one line per sentence.
     */
    public static String getGreeting() {
        return joinResponseLines(
                "Hello! I'm SevenSix, your productivity thought partner.",
                "Which deliverables are we unlocking today?");
    }

    /**
     * Marks a message as an input error so that the user can recognize it.
     *
     * @param message the error description.
     * @return the error message with its prefix.
     */
    public static String formatError(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Checks whether a chatbot response reports an input error, so that the graphical
     * interface can highlight it.
     *
     * @param response the response returned by the chatbot.
     * @return {@code true} when the response was produced by {@link #formatError(String)}.
     */
    public static boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_PREFIX);
    }

    /**
     * Returns the message shown when the user says goodbye.
     *
     * @return the farewell message.
     */
    public static String getFarewellMessage() {
        return "Great sync. Let's touch base again soon!";
    }

    /**
     * Returns the message shown when the task list is empty.
     *
     * @return the empty-list message.
     */
    public static String getEmptyListMessage() {
        return "Your pipeline is empty. Nothing to action right now.";
    }

    /**
     * Returns the message shown when a search finds nothing.
     *
     * @return the no-matches message.
     */
    public static String getNoMatchesMessage() {
        return "Nothing in your pipeline matches that search.";
    }

    /** Describes the reversed action and the affected task before that action.
     *
     * @param entry the change that was undone.
     * @return the undo confirmation.
     */
    public static String formatUndoMessage(UndoEntry entry) {
        return "Undid " + getChangeDescription(entry.getType()) + ": " + entry.getTask();
    }

    /** Chooses user-facing wording for a task action.
     *
     * @param type the action to describe.
     * @return the action description.
     */
    private static String getChangeDescription(ChangeType type) {
        return switch (type) {
        case ADD -> "addition";
        case MARK -> "mark as done";
        case UNMARK -> "mark as not done";
        case DELETE -> "deletion";
        };
    }

    /**
     * Returns the message shown when the command keyword is not recognized.
     *
     * @return the unknown-command message.
     */
    public static String getUnknownCommandMessage() {
        return "that one is outside my wheelhouse. Type help for available commands.";
    }

    /** Returns command syntax and examples shared by both interfaces.
     *
     * @return the help guide, with one instruction per line.
     */
    public static String getHelpMessage() {
        return HelpText.getMessage();
    }

    /**
     * Formats the response shown after a task is added.
     *
     * @param task the task that was added.
     * @param numberOfTasks the number of tasks now in the list.
     * @return the response for the added task.
     */
    public static String formatAddedTask(Task task, int numberOfTasks) {
        return joinResponseLines(
                "Circling back on your ask. I've actioned this deliverable:",
                "  " + task,
                formatTaskCount(numberOfTasks));
    }

    /**
     * Formats the response shown after a task is marked as done.
     *
     * @param task the task that was marked as done.
     * @return the response for the marked task.
     */
    public static String formatMarkedTask(Task task) {
        return joinResponseLines(
                "Love to see it. This deliverable has shipped:", "  " + task);
    }

    /**
     * Formats the response shown after a task is marked as not done.
     *
     * @param task the task that was marked as not done.
     * @return the response for the unmarked task.
     */
    public static String formatUnmarkedTask(Task task) {
        return joinResponseLines(
                "Understood. I've moved this deliverable back into the pipeline:", "  " + task);
    }

    /**
     * Formats the response shown after a task is deleted.
     *
     * @param removedTask the task that was deleted.
     * @param numberOfTasks the number of tasks left in the list.
     * @return the response for the deleted task.
     */
    public static String formatDeletedTask(Task removedTask, int numberOfTasks) {
        return joinResponseLines(
                "Noted. I've descoped this deliverable:",
                "  " + removedTask,
                formatTaskCount(numberOfTasks));
    }

    /** Formats the full pipeline under a heading.
     *
     * @param tasks the tasks in insertion order.
     * @return the heading followed by numbered tasks.
     */
    public static String formatTaskList(Iterable<Task> tasks) {
        return joinResponseLines("Here is your pipeline:", formatNumberedTasks(tasks));
    }

    /**
     * Formats every task with its one-based list number.
     *
     * @param taskCollection the tasks to format.
     * @return the numbered task list.
     */
    private static String formatNumberedTasks(Iterable<Task> taskCollection) {
        List<Task> taskSnapshot = StreamSupport.stream(taskCollection.spliterator(), false)
                .collect(Collectors.toList());
        return IntStream.range(0, taskSnapshot.size())
                .mapToObj(index -> (index + 1) + "." + taskSnapshot.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Formats the tasks that matched a search, under an introductory line.
     *
     * @param matchingTasks the tasks that matched the search keyword.
     * @return the introduction followed by the numbered matching tasks.
     */
    public static String formatMatchingTasks(List<Task> matchingTasks) {
        return joinResponseLines(
                "Here is what surfaced in your pipeline:",
                formatNumberedTasks(matchingTasks));
    }

    /**
     * Formats the sentence reporting how many tasks the list now holds.
     *
     * @param numberOfTasks the number of stored tasks.
     * @return the task-count sentence.
     */
    private static String formatTaskCount(int numberOfTasks) {
        assert numberOfTasks >= 0 : "A task count is reported only after the list size is read";
        return "Your pipeline now holds " + numberOfTasks + " "
                + getTaskCountDescription(numberOfTasks) + ".";
    }

    /**
     * Returns a grammatically correct description of a number of tasks.
     *
     * @param numberOfTasks the number of stored tasks.
     * @return {@code deliverable} for one task, or {@code deliverables} otherwise.
     */
    private static String getTaskCountDescription(int numberOfTasks) {
        return numberOfTasks == 1 ? "deliverable" : "deliverables";
    }

    /**
     * Joins response lines using the platform's line separator.
     *
     * @param lines the lines that make up the response.
     * @return the response with each line separated appropriately.
     */
    private static String joinResponseLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }
}
