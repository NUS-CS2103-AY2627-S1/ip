package caesar.ui;

import caesar.Caesar;
import caesar.exception.CaesarException;
import caesar.parser.Parser;
import caesar.task.Task;
import caesar.task.TaskList;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;

/**
 * Handles Caesar's console input and user-facing output.
 *
 * <p>Ui knows how to present information, but it does not decide which task
 * operation should happen. That decision remains in {@link Caesar}.</p>
 */
public class Ui implements AutoCloseable {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a console user interface backed by standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Prints the greeting and available commands. */
    public void showWelcome() {
        String banner = "██████╗ █████╗ ███████╗███████╗ █████╗ ██████╗\n"
                + "██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗██╔══██╗\n"
                + "██║     ███████║█████╗  ███████╗███████║██████╔╝\n"
                + "██║     ██╔══██║██╔══╝  ╚════██║██╔══██║██╔══██╗\n"
                + "╚██████╗██║  ██║███████╗███████║██║  ██║██║  ██║\n"
                + " ╚═════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        showDivider();
        System.out.println();
        System.out.println(banner);
        System.out.println("Hello! I'm Caesar.\n"
                + "You look even brighter than the last time we spoke.\n"
                + "How may I ease your day today?");
        System.out.println("\nYou can enter the following commands: "
                + Parser.getCommandInstructions());
        showDivider();
    }

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command from standard input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the standard visual divider. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Reports that the task file had to be created. */
    public void showFileCreated(Path filePath) {
        System.out.println("Task file not found. A new task file has been created at: " + filePath);
    }

    /** Reports a failure while loading tasks. */
    public void showLoadingError(CaesarException exception) {
        System.out.println("Error loading tasks from file: " + exception.getMessage());
    }

    /** Reports a command-processing error and prints a divider afterwards. */
    public void showError(CaesarException exception) {
        System.out.println(exception.getMessage());
        showDivider();
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        System.out.println(formatGoodbyeResponse());
        showDivider();
    }

    /** Formats the farewell message shared by console and GUI interfaces. */
    protected String formatGoodbyeResponse() {
        return "You handled today wonderfully. \nUntil next time\u2014I'm always in your corner.";
    }

    /** Prints feedback after adding a task and the dynamic task-count comment. */
    public void showTaskAdded(Task task, TaskList tasks) {
        System.out.println(formatTaskAddedResponse(task, tasks));
        System.out.println(formatDynamicComment(tasks));
        showDivider();
    }

    /** Prints feedback after deleting a task. */
    public void showTaskDeleted(Task task, TaskList tasks) {
        System.out.println(formatTaskDeletedResponse(task, tasks));
        showDivider();
    }

    /** Prints feedback after marking a task complete. */
    public void showTaskMarked(Task task) {
        System.out.println(formatTaskMarkedResponse(task));
        showDivider();
    }

    /** Prints feedback after returning a task to pending. */
    public void showTaskUnmarked(Task task) {
        System.out.println(formatTaskUnmarkedResponse(task));
        showDivider();
    }

    /** Prints feedback after rescheduling a deadline or event. */
    public void showTaskRescheduled(Task task) {
        System.out.println(formatTaskRescheduledResponse(task));
        showDivider();
    }

    /** Formats the response shown after adding a task. */
    protected String formatTaskAddedResponse(Task task, TaskList tasks) {
        return "Got it. I've safely recorded this for you:\n" + task;
    }

    /** Formats the response shown after deleting a task. */
    protected String formatTaskDeletedResponse(Task task, TaskList tasks) {
        return "Noted. I've removed this task:\n" + task
                + "\nNow you have " + tasks.size() + " tasks in the list.\n"
                + "I'm glad that you got some of your own time";
    }

    /** Formats the response shown after marking a task complete. */
    protected String formatTaskMarkedResponse(Task task) {
        return "Well done, proud of your progress. I've marked this as complete:\n" + task;
    }

    /** Formats the response shown after returning a task to pending. */
    protected String formatTaskUnmarkedResponse(Task task) {
        return "No worries at all, no need to rush. I've set this back to pending:\n" + task;
    }

    /** Formats the response shown after rescheduling a task. */
    protected String formatTaskRescheduledResponse(Task task) {
        return "No worries, I've rescheduled this task for you:\n" + task;
    }

    /** Prints tasks including key words. */
    public void showTaskFound(TaskList foundTasks) throws CaesarException {
        System.out.println(formatTaskFoundResponse(foundTasks));
        showDivider();
    }

    /** Formats matching tasks with one-based numbering. */
    protected String formatTaskFoundResponse(TaskList foundTasks) throws CaesarException {
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int taskNumber = 1; taskNumber <= foundTasks.size(); taskNumber++) {
            response.append(taskNumber).append(". ").append(foundTasks.get(taskNumber)).append("\n");
        }
        return response.toString();
    }

    /** Prints overdue and soon-due pending dated tasks. */
    public void showReminders(TaskList tasks) {
        System.out.println(formatReminderResponse(tasks));
        System.out.println(formatDynamicComment(tasks));
        showDivider();
    }

    /** Formats the two-part reminder report shared by console and GUI interfaces. */
    protected String formatReminderResponse(TaskList tasks) {
        TaskList.ReminderTasks reminderTasks = tasks.getReminderTasks(LocalDate.now());
        return "I'm right here. Take a breath and drink some water first—I "
                + "sorted through your schedule so you don't have to stress.\n\n"
                + "A few loose ends from earlier:\n"
                + formatReminderTasks(reminderTasks.getMissedTasks())
                + "\n\n"
                + "On the horizon (next 3 days):\n"
                + formatReminderTasks(reminderTasks.getUpcomingTasks());
    }

    /** Prints the result of clearing pending missed tasks. */
    public void showMissedTasksCleared(int clearedTaskCount) {
        System.out.println(formatMissedTasksClearedResponse(clearedTaskCount));
        showDivider();
    }

    /** Formats the result of clearing pending missed tasks. */
    protected String formatMissedTasksClearedResponse(int clearedTaskCount) {
        if (clearedTaskCount == 0) {
            return "There are no missed tasks to clear.";
        }
        String taskLabel = clearedTaskCount == 1 ? "task" : "tasks";
        return "Cleared " + clearedTaskCount + " missed " + taskLabel + ".";
    }

    /** Formats one reminder section and keeps empty sections explicit. */
    private String formatReminderTasks(List<Task> reminderTasks) {
        if (reminderTasks.isEmpty()) {
            return "None";
        }

        StringBuilder response = new StringBuilder();
        for (Task task : reminderTasks) {
            response.append("- ").append(task).append("\n");
        }
        return response.toString().stripTrailing();
    }

    /** Prints the available command formats. */
    public void showHelp() {
        System.out.println(formatHelpResponse());
        showDivider();
    }

    /** Formats the available command formats for console and GUI interfaces. */
    protected String formatHelpResponse() {
        StringBuilder response = new StringBuilder("Available commands:\n");
        for (String command : Parser.getHelpCommands()) {
            response.append("• ").append(command).append("\n");
        }
        return response.toString();
    }

    /** Prints tasks with one-based numbering and completion feedback. */
    public void showTaskList(Iterable<Task> tasks) throws CaesarException {
        System.out.println(formatTaskListResponse(tasks));
        showDivider();
    }

    /** Formats a task list with one-based numbering and completion feedback. */
    protected String formatTaskListResponse(Iterable<Task> tasks) throws CaesarException {
        List<Task> taskItems = StreamSupport.stream(tasks.spliterator(), false).toList();
        if (taskItems.isEmpty()) {
            throw new CaesarException(
                    "Your schedule is completely clear right now. "
                            + "Take this time to relax and recharge");
        }

        StringBuilder response = new StringBuilder("Here are the tasks in your list:\n\n");
        for (int i = 0; i < taskItems.size(); i++) {
            response.append(i + 1).append(". ").append(taskItems.get(i)).append("\n");
        }

        if (taskItems.stream().allMatch(Task::isDone)) {
            response.append("\nCongrats! You have completed all your tasks!");
        }
        return response.toString();
    }

    /** Formats encouragement based on pending dated tasks due within three days. */
    protected String formatDynamicComment(TaskList tasks) {
        int upcomingTaskCount = tasks.getReminderTasks(LocalDate.now()).getUpcomingTasks().size();
        if (upcomingTaskCount == 0) {
            return "You have no deadlines or events due within the next 3 days. Take some time to relax and recharge.";
        }
        String taskLabel = upcomingTaskCount == 1 ? "task" : "tasks";
        if (1 <= upcomingTaskCount && upcomingTaskCount < 4) {
            return "You have " + upcomingTaskCount + " " + taskLabel
                    + " due within the next 3 days. "
                    + "Take it one step at a time\u2014you've got this.";
        }
        if (4 <= upcomingTaskCount && upcomingTaskCount < 8) {
            return "You have " + upcomingTaskCount + " " + taskLabel
                    + " due within the next 3 days. "
                    + "Things are getting a bit crowded over the next few days."
                    + " Let's pace ourselves, eat properly, and tackle them one by one.";
        }
        return "You have " + upcomingTaskCount + " " + taskLabel
                + " due within the next 3 days. "
                + "Hey, that's way too much to juggle in three days!"
                + " Please don't push yourself to exhaustion—"
                + "let's prioritize, take breaks, and remember your health comes first.";
    }

    /** Closes the standard-input scanner. */
    @Override
    public void close() {
        scanner.close();
    }
}
