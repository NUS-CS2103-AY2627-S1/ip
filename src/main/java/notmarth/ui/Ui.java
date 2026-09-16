package notmarth.ui;

import java.util.Scanner;

import notmarth.command.CommandCatalog;
import notmarth.model.Contact;
import notmarth.model.ContactList;
import notmarth.model.Task;
import notmarth.model.TaskList;

/**
 * Handles console input and output for the NotMarth chatbot.
 */
public class Ui {
    private static final String ADD_TASK_MESSAGE = "     Order received. I've added it to the battle plan:";
    private static final String LIST_TASKS_MESSAGE = "     Here are your current mission orders:";
    private static final String FIND_TASKS_MESSAGE = "     Here are the matching tasks in your list:";
    private static final String MARK_TASK_MESSAGE = "     Well fought! This order is complete:";
    private static final String ENGAGE_MESSAGE = "     Together, we can accomplish this. Engage!";
    private static final String UNMARK_TASK_MESSAGE = "     This order is back on the map:";
    private static final String DELETE_TASK_MESSAGE = "     This order has been withdrawn:";
    private static final String AVAILABLE_COMMANDS_MESSAGE =
            "Available commands: " + CommandCatalog.getVisibleCommandList();
    private static final String ERROR_MESSAGE_TEXT_PREFIX = "I couldn't process that, Divine One: ";
    private static final String ERROR_MESSAGE_PREFIX = "     " + ERROR_MESSAGE_TEXT_PREFIX;
    private static final String SOMMIE_MESSAGE =
            "     Sommie appears with a cheerful wag. Your battle plan has a loyal companion!";
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = " _   _  ___ _____ __  __    _    ____ _____ _   _\n"
            + "| \\ | |/ _ \\_   _|  \\/  |  / \\  |  _ \\_   _| | | |\n"
            + "|  \\| | | | || | | |\\/| | / _ \\ | |_) || | | |_| |\n"
            + "| |\\  | |_| || | | |  | |/ ___ \\|  _ < | | |  _  |\n"
            + "|_| \\_|\\___/ |_| |_|  |_/_/   \\_\\_| \\_|_| |_| |_|\n";

    private final Scanner scanner;

    /** Creates a console UI backed by standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the opening banner and NotMarth introduction. */
    public void showWelcome() {
        showSeparator();
        System.out.print(BANNER);
        System.out.println("Hello! I'm NotMarth, your not-quite-Emblem tactical assistant.");
        System.out.println("The Fell Dragon may be gone, but every battle still needs a plan.");
        System.out.println();
    }

    /**
     * Displays a startup warning after the opening banner.
     *
     * @param message the storage warning to display.
     */
    public void showStartupWarning(String message) {
        System.out.println();
        System.out.println(ERROR_MESSAGE_TEXT_PREFIX + message);
    }

    /** Displays the normal command instructions. */
    public void showPrompt() {
        System.out.println("What tactical command can I assist with?");
        System.out.println(AVAILABLE_COMMANDS_MESSAGE);
        System.out.println("Please enter dates in the format yyyy-mm-dd or dd/MM/yyyy HHmm");
        showSeparator();
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another input line can be read.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return the next command without surrounding whitespace.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a command before its result.
     *
     * @param command the command entered by the user.
     */
    public void showCommand(String command) {
        showSeparator();
        System.out.println("     " + command);
    }

    /** Displays the farewell message and closes the current output section. */
    public void showFarewell() {
        System.out.println("Until we meet again. Stay strong, Divine One!");
    }

    /** Displays the divider line used between console sections. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays the divider using the original descriptive method name. */
    public void showSeparator() {
        showLine();
    }

    /**
     * Displays a command-processing error.
     *
     * @param message the explanation of the invalid command or failed action.
     */
    public void showError(String message) {
        System.out.println(ERROR_MESSAGE_PREFIX + message);
    }

    /** Displays the hidden Sommie Easter egg. */
    public void showSommieMessage() {
        System.out.println(SOMMIE_MESSAGE);
    }

    /**
     * Displays a newly added task and the updated count.
     *
     * @param task the task that was added.
     * @param taskCount the new number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(ADD_TASK_MESSAGE);
        System.out.println("       " + task);
        showItemCount(taskCount, "task");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks the task list to display.
     */
    public void showTasks(TaskList tasks) {
        if (tasks.isEmpty()) {
            System.out.println("     You currently have no mission orders, Divine One.");
            return;
        }
        System.out.println(LIST_TASKS_MESSAGE);
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i + 1, tasks.get(i));
        }
    }

    /** Displays the heading for tasks matching a find keyword. */
    public void showFindTasksHeader() {
        System.out.println(FIND_TASKS_MESSAGE);
    }

    /**
     * Displays the result for a keyword with no matching tasks.
     *
     * @param keyword the keyword that was searched for.
     */
    public void showNoFindTasks(String keyword) {
        System.out.println("     No tasks in your list match \"" + keyword + "\".");
    }

    /**
     * Displays the heading for matching deadlines and events.
     *
     * @param displayDate the formatted date in the heading.
     */
    public void showDateTasksHeader(String displayDate) {
        System.out.println("     Here are the deadlines and events for " + displayDate + ":");
    }

    /**
     * Displays a matching task using its original task number.
     *
     * @param taskNumber the one-based task number.
     * @param task the matching task.
     */
    public void showNumberedTask(int taskNumber, Task task) {
        System.out.println("     " + taskNumber + "." + task);
    }

    /**
     * Displays the result for a date with no matching deadlines or events.
     *
     * @param displayDate the formatted date in the message.
     */
    public void showNoDateTasks(String displayDate) {
        System.out.println("     No deadlines or events are scheduled for " + displayDate + ".");
    }

    /** Displays all contacts in their current order. */
    public void showContacts(ContactList contacts) {
        if (contacts.size() == 0) {
            System.out.println("     You currently have no contacts, Divine One.");
            return;
        }
        System.out.println("     Here are your current contacts:");
        for (int i = 0; i < contacts.size(); i++) {
            showNumberedContact(i + 1, contacts.get(i));
        }
    }

    /** Displays a numbered contact. */
    public void showNumberedContact(int contactNumber, Contact contact) {
        System.out.println("     " + contactNumber + "." + contact);
    }

    /** Displays the result of adding a contact. */
    public void showContactAdded(Contact contact, int contactCount) {
        System.out.println("     Contact added to the battle plan:");
        System.out.println("       " + contact);
        showItemCount(contactCount, "contact");
    }

    /** Displays the heading for matching contacts. */
    public void showFindContactsHeader() {
        System.out.println("     Here are the matching contacts in your list:");
    }

    /** Displays the result for a contact search with no matches. */
    public void showNoFindContacts(String keyword) {
        System.out.println("     No contacts in your list match \"" + keyword + "\".");
    }

    /** Displays the result of deleting a contact. */
    public void showContactDeleted(Contact contact, int remainingContactCount) {
        System.out.println("     This contact has been withdrawn:");
        System.out.println("       " + contact);
        showItemCount(remainingContactCount, "contact");
    }

    /**
     * Displays the result of marking a task complete.
     *
     * @param task the task that was marked complete.
     */
    public void showTaskMarked(Task task) {
        System.out.println(MARK_TASK_MESSAGE);
        System.out.println(ENGAGE_MESSAGE);
        System.out.println("       " + task);
    }

    /**
     * Displays the result of marking a task incomplete.
     *
     * @param task the task that was marked incomplete.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(UNMARK_TASK_MESSAGE);
        System.out.println("       " + task);
    }

    /**
     * Displays the result of deleting a task.
     *
     * @param task the deleted task.
     * @param remainingTaskCount the number of tasks left.
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        System.out.println(DELETE_TASK_MESSAGE);
        System.out.println("       " + task);
        showItemCount(remainingTaskCount, "task");
    }

    private void showItemCount(int itemCount, String singularNoun) {
        String noun = itemCount == 1 ? singularNoun : singularNoun + "s";
        System.out.println("     Now you have " + itemCount + " " + noun + " in the list.");
    }
}
