package cookie.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import cookie.command.CookieException;
import cookie.task.SortCriterion;
import cookie.task.SortDirection;
import cookie.task.Task;
import cookie.task.TaskList;

/** Handles Cookie's messages and other interactions with the user. */
public class Ui {
    /** Formats dates shown in date-based task queries. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Receives messages after the UI formats them. */
    private final Output output;

    /** Creates a UI handler that writes messages to the console. */
    public Ui() {
        this(new ConsoleOutput());
    }

    /**
     * Creates a UI handler that sends messages to the supplied output.
     *
     * @param output The destination for formatted messages.
     */
    public Ui(Output output) {
        this.output = output;
    }

    /** Displays Cookie's greeting and the prompt for the first command. */
    public void greet() {
        String lineSeparator = System.lineSeparator();
        String banner =
                  " ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗" + lineSeparator
                + "██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝" + lineSeparator
                + "██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  " + lineSeparator
                + "██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  " + lineSeparator
                + "╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗" + lineSeparator
                + " ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝";

        show(banner + lineSeparator
                + "Hello! I'm your favourite chatbot Cookie." + lineSeparator
                + "What do you need today?");
    }

    /** Prints the message shown when the user ends the conversation. */
    public void exit() {
        show("Bye. I'm going to sleep.");
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task The added task.
     * @param taskCount The number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        String lineSeparator = System.lineSeparator();
        show("Ok. I've added this task:" + lineSeparator
                + "   " + task + lineSeparator
                + "You have " + taskCount + " task(s) now. Better start working.");
    }

    /**
     * Displays all tasks with their one-based positions.
     *
     * @param tasks The tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        Stream<TaskList.IndexedTask> indexedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> new TaskList.IndexedTask(index + 1, tasks.get(index)));
        show("Here are the task(s) in your list:" + formatTaskLines(indexedTasks));
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        show("Wow you actually got work done..." + System.lineSeparator() + "   " + task);
    }

    /**
     * Displays confirmation that a task was unmarked as done.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        show("I can't believe you lied to me..." + System.lineSeparator() + "   " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task The deleted task.
     * @param remainingTaskCount The number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        String lineSeparator = System.lineSeparator();
        show("You're welcome. I've gotten rid of this task for you:" + lineSeparator
                + "   " + task + lineSeparator
                + "Now you have " + remainingTaskCount + " task(s) in the list.");
    }

    /**
     * Displays a friendly error without terminating the application.
     *
     * @param exception The error to display.
     */
    public void showError(CookieException exception) {
        show("Bruh... " + exception.getMessage());
    }

    /**
     * Displays an error encountered while saving tasks.
     *
     * @param details The details of the saving error.
     */
    public void showSaveError(String details) {
        show("Oh no! I couldn't save your tasks: " + details);
    }

    /**
     * Displays an error encountered while loading tasks.
     *
     * @param details The details of the loading error.
     */
    public void showLoadError(String details) {
        show("Oh no! I couldn't load your tasks: " + details);
    }

    /**
     * Warns that malformed saved records were skipped while valid tasks were recovered.
     *
     * @param lineNumbers The one-based line numbers of malformed records.
     */
    public void showMalformedRecords(List<Integer> lineNumbers) {
        String noun = lineNumbers.size() == 1 ? "line" : "lines";
        String numbers = lineNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        show("Heads up! I skipped malformed saved task records on " + noun + " " + numbers
                + ". Your valid tasks were still loaded.");
    }

    /**
     * Displays deadlines and events that occur on the requested calendar date.
     *
     * @param date The date to search.
     * @param matchingTasks The matching tasks and their original task numbers.
     */
    public void showTasksOnDate(LocalDate date, List<TaskList.IndexedTask> matchingTasks) {
        show("Here are the task(s) on " + date.format(DISPLAY_DATE_FORMAT) + ":"
                + formatTaskLines(matchingTasks.stream()));
    }

    /** Displays tasks whose descriptions contain the requested keyword. */
    public void showMatchingTasks(List<TaskList.IndexedTask> matchingTasks) {
        show("Here are the matching tasks in your list:" + formatTaskLines(matchingTasks.stream()));
    }

    /**
     * Displays all tasks in a sorted view while retaining their original task numbers.
     *
     * @param sortedTasks The sorted tasks paired with their original task numbers.
     * @param criterion The task property used for sorting.
     * @param direction The direction used for sorting comparable values.
     */
    public void showSortedTasks(List<TaskList.IndexedTask> sortedTasks,
                                SortCriterion criterion, SortDirection direction) {
        String criterionLabel = criterion == SortCriterion.DESCRIPTION ? "description" : "date";
        String directionLabel = direction == SortDirection.ASCENDING ? "ascending" : "descending";
        show("Here are the task(s) sorted by " + criterionLabel + " (" + directionLabel + "):"
                + formatTaskLines(sortedTasks.stream()));
    }

    /**
     * Formats indexed tasks as display lines, including a leading line separator when nonempty.
     *
     * @param indexedTasks The indexed tasks to format.
     * @return The formatted task lines, or an empty string if there are no tasks.
     */
    private String formatTaskLines(Stream<TaskList.IndexedTask> indexedTasks) {
        String taskLines = indexedTasks
                .map(indexedTask -> indexedTask.taskNumber() + ". " + indexedTask.task())
                .collect(Collectors.joining(System.lineSeparator()));
        return taskLines.isEmpty() ? "" : System.lineSeparator() + taskLines;
    }

    /** Sends a formatted message to the configured output. */
    private void show(String message) {
        output.show(message);
    }
}
