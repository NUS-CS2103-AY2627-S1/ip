package friday.ui;

import java.time.LocalDate;
import java.util.Scanner;

import friday.contact.Contact;
import friday.task.Task;
import friday.task.TaskDateTime;

/**
 * Handles console input and presentation without parsing commands or changing tasks.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String TASK_NUMBER_HINT = "Use the number shown here with mark/unmark.";
    private final Scanner scanner;

    /**
     * Creates a console UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Checks for input so EOF ends the command loop without an exception.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line unchanged; interpreting it is the caller's responsibility.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the separator between console interactions.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the welcome text; the caller can show loading errors before the closing separator.
     */
    public void showWelcome() {
        showLine();
        System.out.println("FRIDAY online. Let's keep your day under control.");
    }

    /**
     * Prints the farewell message and closing separator.
     */
    public void showGoodbye() {
        System.out.println("FRIDAY signing off. I'll be here when you return.");
        showLine();
    }

    /**
     * Prints FRIDAY's ready-for-work greeting.
     */
    public void showGreeting() {
        System.out.println("At your service. What's the next task?");
    }

    /**
     * Prints FRIDAY's concise acknowledgment.
     */
    public void showThanks() {
        System.out.println("Anytime. Organization is rather my specialty.");
    }

    /**
     * Prints the supported commands and their argument formats.
     */
    public void showHelp() {
        System.out.println("""
                Command briefing:
                Tasks:
                  todo DESCRIPTION
                  deadline DESCRIPTION /by DEADLINE
                  event DESCRIPTION /from START /to END
                  list
                  find KEYWORD
                  on yyyy-MM-dd
                  mark TASK_NUMBER
                  unmark TASK_NUMBER
                  delete TASK_NUMBER
                Dates: yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm
                Contacts:
                  contact add NAME /phone PHONE [/email EMAIL]
                  contact add NAME /email EMAIL
                  contact list
                  contact find KEYWORD
                  contact delete NUMBER
                Session: hello | thanks | help | bye
                """.stripTrailing());
    }

    /**
     * Displays a validation error without deciding which command or input caused it.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints the task-list heading and task-number hint.
     */
    public void showTaskListHeader() {
        System.out.println("Your task briefing:");
        System.out.println(TASK_NUMBER_HINT);
    }

    /**
     * Prints the keyword-search heading and explains how to use the original task numbers.
     */
    public void showFindHeader() {
        System.out.println("Task scan complete. Here's what matches:");
        System.out.println(TASK_NUMBER_HINT);
    }

    /**
     * Reports that no task description contains the search keyword.
     */
    public void showNoMatchingTasks() {
        System.out.println("Task scan complete. No matches found.");
    }
    /** Prints the dated-task heading and original-task-number hint. */
    public void showDateHeader(LocalDate date) {
        System.out.println("Scheduled deadlines and events on " + TaskDateTime.format(date.atStartOfDay()) + ":");
        System.out.println(TASK_NUMBER_HINT);
    }

    /**
     * Displays the supplied original list number, including for filtered results.
     */
    public void showNumberedTask(int taskNumber, Task task) {
        System.out.println(taskNumber + "." + task);
    }

    /**
     * Reports that no deadlines or events match the requested date.
     */
    public void showNoTasksOnDate() {
        System.out.println("Schedule clear. No deadlines or events on this date.");
    }

    /**
     * Displays the added task and the resulting task count.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Task logged. Here's the next objective:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays the removed task and the remaining task count.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Task removed from your agenda:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that the supplied task is done.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Objective complete. Nicely handled:");
        showTask(task);
    }

    /**
     * Displays confirmation that the supplied task is not done.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("Task reopened. Back on the radar:");
        showTask(task);
    }

    /**
     * Reports that the selected task is already not done.
     */
    public void showAlreadyUnmarked() {
        System.out.println("This task is already not marked as done.");
    }

    /**
     * Warns that loading failed and saving is disabled to protect existing data.
     */
    public void showLoadingError() {
        System.out.println("Warning: I couldn't load data/friday.txt. "
                + "Check the file and restart; saving is disabled to protect existing data.");
    }

    /**
     * Warns that changes remain in memory until the save file is repaired and Friday restarts.
     */
    public void showSavingDisabled() {
        System.out.println("Warning: This change is only in memory; saving is disabled until you fix the file "
                + "and restart.");
    }

    /**
     * Warns that saving failed and session changes remain only in memory.
     */
    public void showSavingError() {
        System.out.println("Warning: I couldn't save data/friday.txt. "
                + "Your changes are only in memory; check the folder and file permissions.");
    }

    /** Shows a contact listing heading and the original-number deletion hint. */
    public void showContactListHeader(boolean isSearch) {
        System.out.println(isSearch ? "Contact scan complete. Here's what matches:"
                : "Your contact directory:");
        System.out.println("Use the number shown here with contact delete.");
    }

    /** Displays a contact with its original one-based list number. */
    public void showNumberedContact(int number, Contact contact) {
        System.out.println(number + "." + contact);
    }

    /** Confirms addition and displays the resulting contact count. */
    public void showContactAdded(Contact contact, int count) {
        System.out.println("Contact logged. Here's the entry:");
        System.out.println("  " + contact);
        showContactCount(count);
    }

    /** Confirms deletion and displays the remaining contact count. */
    public void showContactDeleted(Contact contact, int count) {
        System.out.println("Contact removed from your directory:");
        System.out.println("  " + contact);
        showContactCount(count);
    }

    /** Warns that the original contact file is protected after a loading failure. */
    public void showContactLoadingError() {
        System.out.println("Warning: I couldn't load data/contacts.txt. Check the file and restart; "
                + "contact saving is disabled to protect existing data.");
    }

    /** Explains that contact changes cannot persist until the damaged file is repaired. */
    public void showContactSavingDisabled() {
        System.out.println("Warning: This contact change is only in memory; contact saving is disabled "
                + "until you fix the file and restart.");
    }

    /** Reports a contact write failure while preserving the in-memory session. */
    public void showContactSavingError() {
        System.out.println("Warning: I couldn't save data/contacts.txt. Your contact changes are only in memory; "
                + "check the folder and file permissions.");
    }

    /** Selects singular or plural contact-count feedback. */
    private void showContactCount(int count) {
        System.out.println("Now you have " + count + " contact" + (count == 1 ? "" : "s") + " in the list.");
    }

    private void showTask(Task task) {
        System.out.println("  " + task);
    }

    /**
     * Keeps singular and plural task-count feedback consistent for additions and deletions.
     */
    private void showTaskCount(int taskCount) {
        System.out.println("Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s")
                + " in the list.");
    }
}
