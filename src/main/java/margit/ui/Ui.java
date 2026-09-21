package margit.ui;

import java.time.LocalDate;
import java.util.Scanner;

import margit.task.SortedTaskLists;
import margit.task.Task;
import margit.task.TaskDateTime;
import margit.task.TaskList;

/** Handles console input and output for the application. */
public class Ui {
    private static final String INDENT = "     ";
    private static final String HORIZONTAL_LINE = "-".repeat(96);

    private static final String GREETINGS = "Foul Tarnished. State thy burden.";

    private static final String FAREWELL = "'Tis well... put these foolish ambitions to rest.";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the application's greeting. */
    public void showWelcome() {
        System.out.println(INDENT + formatGreeting() + "\n");
        System.out.println(INDENT + HORIZONTAL_LINE + "\n");
    }

    /** Returns Margit's greeting for a newly opened interface. */
    public String formatGreeting() {
        return GREETINGS;
    }

    /** Reads and returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays all tasks currently in the task list. */
    public void showTaskList(TaskList tasks) {
        showFramed(formatTaskList(tasks));
    }

    /** Returns the message body for the current task list. */
    public String formatTaskList(TaskList tasks) {
        return formatTasks("Behold thy burdens:", tasks, "Thy ledger is empty.");
    }

    /** Returns the grouped message body for a permanently sorted task list. */
    public String formatSortedTaskLists(SortedTaskLists sortedTaskLists) {
        StringBuilder message = new StringBuilder();
        appendTaskSection(message, "Unfinished Burdens:", sortedTaskLists.getTodoTasks());
        appendTaskSection(message, "Burdens Without a Time:", sortedTaskLists.getUnscheduledTasks());
        appendTaskSection(message, "Burdens Yet to Come:", sortedTaskLists.getScheduledTasks());

        return message.isEmpty() ? "There are no burdens to order." : message.toString();
    }

    /** Displays all tasks whose descriptions match a find command. */
    public void showFindResults(TaskList matchingTasks) {
        showFramed(formatFindResults(matchingTasks));
    }

    /** Returns the message body for matching tasks. */
    public String formatFindResults(TaskList matchingTasks) {
        return formatTasks("These burdens answer thy search:", matchingTasks,
                "No burden answers that word.");
    }

    /** Displays tasks that occur on the specified date. */
    public void showTasksOnDate(LocalDate date, TaskList matchingTasks) {
        showFramed(formatTasksOnDate(date, matchingTasks));
    }

    /** Returns the message body for tasks that occur on a date. */
    public String formatTasksOnDate(LocalDate date, TaskList matchingTasks) {
        String heading = "On " + date.format(TaskDateTime.OUTPUT_DATE)
                + ", these burdens await thee:";
        return formatTasks(heading, matchingTasks, "Nothing awaits thee that day.");
    }

    /** Displays an error when a find command has no keyword. */
    public void showMissingFindKeyword() {
        showFramed(formatMissingFindKeyword());
    }

    /** Returns the error message for a find command without a keyword. */
    public String formatMissingFindKeyword() {
        return "Name the word thou wouldst seek, Tarnished.";
    }

    /** Displays an error when an on command has no date. */
    public void showMissingDate() {
        showFramed(formatMissingDate());
    }

    /** Returns the error message for an on command without a date. */
    public String formatMissingDate() {
        return "Name the day thou wouldst inspect, Tarnished.";
    }

    /** Displays an error when an on command contains an invalid date. */
    public void showInvalidDate() {
        showFramed(formatInvalidDate());
    }

    /** Returns the error message for an invalid date. */
    public String formatInvalidDate() {
        return "That date is beyond my reckoning, Tarnished.";
    }

    /** Displays an error when a task number is not a number. */
    public void showInvalidTaskNumber() {
        showFramed(formatInvalidTaskNumber());
    }

    /** Returns the error message for a nonnumeric task number. */
    public String formatInvalidTaskNumber() {
        return "That is no task number.";
    }

    /** Displays an error when a task number is outside the task list. */
    public void showTaskNotFound() {
        showFramed(formatTaskNotFound());
    }

    /** Returns the error message for a task number outside the list. */
    public String formatTaskNotFound() {
        return "No such burden rests upon thy ledger, Tarnished.";
    }

    /** Displays an unchanged completion status for a task. */
    public void showUnchangedTaskStatus(Task task, boolean isMarked) {
        showFramed(formatUnchangedTaskStatus(task, isMarked));
    }

    /** Returns the unchanged completion-status message for a task. */
    public String formatUnchangedTaskStatus(Task task, boolean isMarked) {
        String message = isMarked ? "This burden is already laid to rest:"
                : "This burden already remains before thee:";
        return message + "\n  " + task;
    }

    /** Displays a changed completion status for a task. */
    public void showChangedTaskStatus(Task task, boolean isMarked) {
        showFramed(formatChangedTaskStatus(task, isMarked));
    }

    /** Returns the changed completion-status message for a task. */
    public String formatChangedTaskStatus(Task task, boolean isMarked) {
        String message = isMarked ? "Marked. Let this burden trouble thee no longer:"
                : "Restored. This burden remains before thee:";
        return message + "\n  " + task;
    }

    /** Displays a confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        showFramed(formatTaskDeleted(task, taskCount));
    }

    /** Returns the deletion confirmation message for a task. */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "Cast aside. This burden is no more:\n  " + task
                + "\n" + taskCount + " burdens remain upon thy ledger.";
    }

    /** Displays an error when a todo has no description. */
    public void showMissingTodoDescription() {
        showFramed(formatMissingTodoDescription());
    }

    /** Returns the error message for a todo without a description. */
    public String formatMissingTodoDescription() {
        return "Name the burden thou wouldst record, Tarnished.";
    }

    /** Displays an error when no more tasks can be added. */
    public void showTaskListFull() {
        showFramed(formatTaskListFull());
    }

    /** Returns the error message for a full task list. */
    public String formatTaskListFull() {
        return "Thy task list can hold no more.";
    }

    /** Displays an error when a deadline omits its /by separator. */
    public void showMissingDeadlineSeparator() {
        showFramed(formatMissingDeadlineSeparator());
    }

    /** Returns the error message for a deadline without a /by separator. */
    public String formatMissingDeadlineSeparator() {
        return "A deadline requires a burden and a '/by' date, Tarnished.";
    }

    /** Displays an error when a deadline omits its description or /by date. */
    public void showMissingDeadlineComponent() {
        showFramed(formatMissingDeadlineComponent());
    }

    /** Returns the error message for a deadline missing a required component. */
    public String formatMissingDeadlineComponent() {
        return "Supply both the burden and its '/by' date.";
    }

    /** Displays an error when an event omits required information. */
    public void showInvalidEvent() {
        showFramed(formatInvalidEvent());
    }

    /** Returns the error message for an invalid event. */
    public String formatInvalidEvent() {
        return "An event requires a burden, a '/from' time, and a '/to' time.";
    }

    /** Returns the error message for an unsupported sort command. */
    public String formatInvalidSortCommand() {
        return "Do not embellish the command. Use 'sort' alone.";
    }

    /** Displays a confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showFramed(formatTaskAdded(task, taskCount));
    }

    /** Returns the addition confirmation message for a task. */
    public String formatTaskAdded(Task task, int taskCount) {
        return "So be it. I have recorded this burden:\n  " + task
                + "\n" + taskCount + " burdens remain upon thy ledger.";
    }

    /** Displays an error for an unrecognized command. */
    public void showUnknownCommand() {
        showFramed(formatUnknownCommand());
    }

    /** Returns the error message for an unrecognized command. */
    public String formatUnknownCommand() {
        return "Thy command is unknown to me, Tarnished.";
    }

    /** Displays the application's farewell message. */
    public void showFarewell() {
        showFramed(formatFarewell());
    }

    /** Returns the farewell message. */
    public String formatFarewell() {
        return FAREWELL;
    }

    /** Displays a response message between two horizontal separator lines. */
    public void showResponse(String response) {
        showFramed(response);
    }

    /** Releases the console input resource when the program ends. */
    public void close() {
        scanner.close();
    }

    /** Displays a message between two horizontal separator lines. */
    private void showFramed(String message) {
        System.out.println(INDENT + HORIZONTAL_LINE);
        System.out.println(INDENT + message.replace("\n", "\n" + INDENT));
        System.out.println(INDENT + HORIZONTAL_LINE + "\n");
    }

    /** Displays a heading followed by numbered tasks or an empty-list message. */
    private String formatTasks(String heading, TaskList tasks, String emptyMessage) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        if (tasks.size() == 0 && !emptyMessage.isEmpty()) {
            message.append("\n").append(emptyMessage);
        }
        return message.toString();
    }

    /** Appends a numbered task section when it contains at least one task. */
    private void appendTaskSection(StringBuilder message, String heading, TaskList tasks) {
        if (tasks.size() == 0) {
            return;
        }
        if (!message.isEmpty()) {
            message.append("\n\n");
        }
        message.append(formatTasks(heading, tasks, ""));
    }
}
