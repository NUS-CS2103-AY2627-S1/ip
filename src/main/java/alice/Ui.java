package alice;

import java.io.PrintStream;

/**
 * Handles all interactions with the user, including displaying messages,
 * task lists, error messages, and formatting outputs for the console.
 */
public class Ui {
    private static final String DOTTED_LINE = "----------------------------------------------------------";
    private final PrintStream output;
    private boolean errorResponse;

    /**
     * Constructs a UI that writes to the standard output stream.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Constructs a UI that writes to the supplied output stream.
     *
     * @param output The destination for UI messages.
     */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /**
     * Prints the welcome banner and greeting message to the console.
     */
    public void printGreetings() {
        String greetings = """
                ----------------------------------------------------------
                ██████  ██     ████  █████  █████
                ██  ██  ██      ██   ██     ██
                ██████  ██      ██   ██     ████
                ██  ██  ██      ██   ██     ██
                ██  ██  █████  ████  █████  █████
                Hello! I'm Alice, your cheerful task companion!
                Let's make your plans feel a little lighter.
                ----------------------------------------------------------""";
        output.println(greetings);
    }

    /**
     * Prints a plain-ASCII greeting banner for the graphical chat interface.
     */
    public void printChatGreetings() {
        String greetings = """
                ----------------------------------------------------------
                AAAAA   L        I   CCCC   EEEEE
               A     A  L        I  C       E
               AAAAAAA  L        I  C       EEEE
               A     A  L        I  C       E
               A     A  LLLLL    I   CCCC   EEEEE

                Hello! I'm Alice, your cheerful task companion!
                Let's make your plans feel a little lighter.
                ----------------------------------------------------------""";
        output.println(greetings);
    }

    /**
     * Prints the goodbye message when the user exits the application.
     */
    public void quitMessage() {
        output.println(DOTTED_LINE);
        output.println("Bye for now! I'll keep your tasks safe until next time.");
        output.println(DOTTED_LINE);
    }

    /**
     * Prints a visual separator line (dotted line) to structure the output.
     */
    public void showSeparator() {
        output.println(DOTTED_LINE);
    }

    /**
     * Prints a plain informational message.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        output.println(message);
    }

    /**
     * Prints an error message followed by a separator line.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        errorResponse = true;
        output.println(message);
        showSeparator();
    }

    /**
     * Clears the response category before Alice processes another command.
     */
    public void resetResponseState() {
        errorResponse = false;
    }

    /**
     * Returns whether the current response represents an error.
     *
     * @return true if an error was shown after the latest reset.
     */
    public boolean isErrorResponse() {
        return errorResponse;
    }

    /**
     * Prints a confirmation message indicating that a task was successfully added.
     *
     * @param tasks The updated task list (used to retrieve the most recently added task and total count).
     */
    public void showAddTask(TaskList tasks) {
        output.printf("""
                Lovely! I've added this task:
                  %s
                Tasks in your list: %d.
                """,
                tasks.get(tasks.size() - 1), tasks.size());
        showSeparator();
    }

    /**
     * Prints a confirmation message indicating that a task was successfully deleted.
     *
     * @param task        The task that was removed.
     * @param totalCount  The remaining number of tasks after deletion.
     */
    public void showDeleteTask(Task task, int totalCount) {
        output.printf("""
                    All cleared! I've removed this task:
                      %s
                    Tasks left in your list: %d.
                    """, task, totalCount);
        showSeparator();
    }

    /**
     * Prints a confirmation message indicating that a task was successfully marked as done.
     *
     * @param task The task that was marked as done.
     */
    public void showMarkTask(Task task) {
        output.println("Nicely done! You've completed this task:");
        output.printf("  %s\n", task);
        showSeparator();
    }

    /**
     * Prints a confirmation message indicating that a task was successfully unmarked.
     *
     * @param task The task that was marked as not done.
     */
    public void showUnmarkTask(Task task) {
        output.println("No worries! I've marked this task as not done:");
        output.printf("  %s\n", task);
        showSeparator();
    }

    /**
     * Prints the current list of tasks to the console.
     * If the list is empty, a message is displayed.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            output.println("Your list is clear. Add a task whenever you're ready!");
        } else {
            output.println("Here is what you have planned:");
            for (int i = 0; i < tasks.size(); i++) {
                output.printf("%d.%s\n", i + 1, tasks.get(i));
            }
        }
        showSeparator();
    }

    /**
     * Prints tasks that occur on a specific date.
     * For deadlines, it checks the exact 'by' date.
     * For events, it checks if the date falls within the event's range.
     *
     * @param tasks The task list to search in.
     * @param date  The date to filter tasks by.
     */
    public void showTasksOnDate(TaskList tasks, java.time.LocalDate date) {
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy");
        output.println("Here is what you have planned for " + date.format(formatter) + ":");

        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean isOnDate = false;

            if (task instanceof Deadlines) {
                Deadlines d = (Deadlines) task;
                if (d.getBy().equals(date)) {
                    isOnDate = true;
                }
            } else if (task instanceof Events) {
                Events e = (Events) task;
                if (!e.getFrom().isAfter(date) && !e.getTo().isBefore(date)) {
                    isOnDate = true;
                }
            }

            if (isOnDate) {
                output.println((i + 1) + "." + task);
                found = true;
            }
        }

        if (!found) {
            output.println("Nothing is scheduled for this date.");
        }
        showSeparator();
    }

    /**
     * Prints a generic error message when an unknown or unrecognized command is entered.
     */
    public void showUnknownCommand() {
        showError("Aiyo, I didn't quite understand that. Try one of the commands shown below.");
    }

    /**
     * Displays the matching tasks for a given keyword search.
     *
     * @param matchingTasks The list of tasks that matched the keyword.
     * @param keyword       The keyword that was searched for.
     */
    public void showMatchingTasks(TaskList matchingTasks, String keyword) {
        if (matchingTasks.isEmpty()) {
            output.println("I couldn't find any tasks containing \"" + keyword + "\".");
        } else {
            output.println("I found these matching tasks:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                output.printf("%d.%s\n", i + 1, matchingTasks.get(i));
            }
        }
        showSeparator();
    }

    /**
     * Prints a confirmation message after restoring an earlier task-list state.
     */
    public void showUndoSuccess() {
        output.println("Done! I've turned back one step.");
        showSeparator();
    }

    /**
     * Prints an error when there is no earlier task-list state to restore.
     */
    public void showUndoUnavailable() {
        showError("There is no earlier change to undo.");
    }
}
