package discitrack.ui;

import java.util.List;
import java.util.Scanner;

import discitrack.task.Task;

/**
 * Handles all interactions between DisciTrack and the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String GREETING = "I'm DisciTrack, your discipline coach.\n"
            + "Are you ready to crush today's tasks?\n"
            + "Type help or click Commands if you need examples.";
    private static final String HELP_MESSAGE = "Here are the commands you can use:\n\n"
            + "todo DESCRIPTION\n  Example: todo exercise\n\n"
            + "deadline DESCRIPTION /by yyyy-MM-dd\n  Example: deadline pay bills /by 2026-09-10\n\n"
            + "event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd\n"
            + "  Example: event holiday /from 2026-09-15 /to 2026-09-18\n\n"
            + "list\n  Shows all tasks.\n\n"
            + "mark NUMBER\n  Example: mark 1\n\n"
            + "unmark NUMBER\n  Example: unmark 1\n\n"
            + "delete NUMBER\n  Example: delete 1\n\n"
            + "find KEYWORD\n  Example: find exercise\n\n"
            + "checkdate yyyy-MM-dd\n  Shows deadlines and events occurring on that date.\n"
            + "  Example: checkdate 2026-09-10\n\n"
            + "/tag TAG\n  Optional: append to any new task; repeat for more tags.\n"
            + "  Example: todo slides /tag CS2103T /tag school\n\n"
            + "tag NUMBER TAG\n  Example: tag 2 school\n\n"
            + "untag NUMBER TAG\n  Example: untag 2 school\n\n"
            + "help\n  Shows this command guide.\n\n"
            + "bye\n  Ends the conversation.";

    private final Scanner scanner;
    private final boolean shouldPrintResponses;
    private String lastResponse = "";

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI that can optionally print responses to standard output.
     *
     * @param shouldPrintResponses whether responses should be printed to the terminal.
     */
    public Ui(boolean shouldPrintResponses) {
        this.scanner = new Scanner(System.in);
        this.shouldPrintResponses = shouldPrintResponses;
    }

    /**
     * Displays the welcome message.
     */
    public void showGreeting() {
        String greeting = LINE + "\n"
                + " ____    _             _   _____                 _       \n"
                + "|  _ \\  (_) ___   ___ (_) |_   _| _ __   __ _  ___ | | __ \n"
                + "| | | | | |/ __| / __|| |   | |  | '__| / _` |/ __|| |/ / \n"
                + "| |_| | | |\\__ \\| (__ | |   | |  | |   | (_| | (__ |   <  \n"
                + "|____/  |_||___/ \\___||_|   |_|  |_|    \\__,_|\\___||_|\\_\\ \n"
                + LINE + "\n"
                + GREETING + "\n"
                + LINE;

        lastResponse = GREETING;
        if (shouldPrintResponses) {
            System.out.println(greeting);
        }
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the farewell message.
     */
    public void showBye() {
        showResponse("Coach out! Rest up, champ. We'll chase the next win together.");
    }

    /**
     * Displays the supported commands and examples.
     */
    public void showHelp() {
        showResponse(HELP_MESSAGE);
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("All clear, champ! Nothing on the board. Enjoy the breather - coach approves.");
        } else {
            showResponse(formatNumberedTasks(tasks));
        }
    }

    /**
     * Displays a message for a task that has been marked as done.
     *
     * @param task the task that was marked.
     */
    public void showTaskMarked(Task task) {
        showResponse(
                "Task completed! You finished:",
                "",
                task.toString(),
                "",
                "Another commitment kept. Be proud of yourself, champ - keep that momentum going!");
    }

    /**
     * Displays a message for a task that has been marked as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showResponse(
                "Back on the board! A little early with the victory dance? It's okay - we go again. Let's lock in!",
                task.toString());
    }

    /**
     * Displays tasks that match a searched date.
     *
     * @param tasksMatchedDate tasks that match the searched date.
     * @param allTasks the full task list used for numbering.
     */
    public void showDateMatches(List<Task> tasksMatchedDate, List<Task> allTasks) {
        if (tasksMatchedDate.isEmpty()) {
            showResponse("There are no deadlines or events on this date.");
        } else {
            showResponse("Here are your tasks for this date:", formatNumberedTasks(tasksMatchedDate, allTasks));
        }
    }

    /**
     * Displays tasks that match a searched keyword.
     *
     * @param matchingTasks tasks that match the searched keyword.
     * @param allTasks the full task list used for numbering.
     */
    public void showFoundTasks(List<Task> matchingTasks, List<Task> allTasks) {
        if (matchingTasks.isEmpty()) {
            showResponse("Nothing on the radar, champ. Try another keyword or hit Show all.");
        } else {
            showResponse(
                    "Scouting report is in! Here's what matches:",
                    formatNumberedTasks(matchingTasks, allTasks));
        }
    }

    /**
     * Displays a message for a newly added task.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks after adding the task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showResponse(
                "Your next challenge:",
                "",
                task.toString(),
                String.format("You now have %d %s.", taskCount, taskWord),
                "",
                "Planning done. Now comes the most important part: doing it!");
    }

    /**
     * Displays a message for a deleted task.
     *
     * @param removedTask the task that was deleted.
     * @param taskCount the number of tasks after deleting the task.
     */
    public void showTaskDeleted(Task removedTask, int taskCount) {
        showResponse(
                "Off the board! Game plan updated.",
                removedTask.toString(),
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    /**
     * Displays an error message.
     *
     * @param message the error message to display.
     */
    public void showError(String message) {
        showResponse(message);
    }

    /**
     * Displays a successfully saved tag change and the updated task.
     *
     * @param number the full-list task number.
     * @param tag the stored spelling of the tag.
     * @param task the updated task.
     * @param isRemoval whether the tag was removed.
     */
    public void showTagChanged(int number, String tag, Task task, boolean isRemoval) {
        String message = isRemoval ? "\"%s\" badge removed from task %d. Task stays in the game."
                : "Task %2$d is now wearing the \"%1$s\" badge!";
        showResponse(String.format(message, tag, number), task.toString());
    }

    /**
     * Explains why a tagging command made no change.
     *
     * @param number the full-list task number.
     * @param tag the tag spelling to display.
     * @param isPresent whether the task already has the tag.
     */
    public void showTagUnchanged(int number, String tag, boolean isPresent) {
        String message = isPresent ? "Task %d already has tag \"%s\"." : "Task %d does not have tag \"%s\".";
        showResponse(String.format(message, number, tag));
    }

    /**
     * Displays an error message for a failed save operation.
     */
    public void showSaveError() {
        showError("UHOH! I could not save your tasks.");
    }

    /**
     * Returns the latest response produced by this UI.
     *
     * @return the latest response text.
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Returns the welcome message used by both interfaces.
     *
     * @return the DisciTrack welcome message.
     */
    public String getGreeting() {
        return GREETING;
    }

    private static String formatNumberedTasks(List<Task> tasks) {
        return formatNumberedTasks(tasks, tasks);
    }

    private static String formatNumberedTasks(List<Task> tasks, List<Task> allTasks) {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            int taskNumber = allTasks.indexOf(tasks.get(i)) + 1;
            response.append(taskNumber).append(". ").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                response.append(System.lineSeparator());
            }
        }
        return response.toString();
    }

    private void showResponse(String... responseLines) {
        String response = String.join(System.lineSeparator(), responseLines);
        lastResponse = response;
        if (shouldPrintResponses) {
            showLine();
            System.out.println(response);
            showLine();
        }
    }

    private void showLine() {
        System.out.println(LINE);
    }
}
