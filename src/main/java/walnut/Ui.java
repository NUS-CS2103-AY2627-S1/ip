package walnut;

import java.util.ArrayList;

/**
 * Handles console input and output for Walnut.
 */
public class Ui {

    private static final String ADDED_TASK_TEMPLATE = "Added task: %s%nYou have %d %s in your list.";
    private static final String REMOVED_TASK_TEMPLATE = "Task removed:%n%s%nYou have %d %s in your list.";

    /**
     * Displays the application banner.
     *
     * @return the ASCII-art banner string
     */
    public String showBanner() {
        return " _    _       _             _\n"
                + "| |  | | __ _| |_ __  _   _| |_\n"
                + "| |/\\| |/ _` | | '_ \\| | | | __|\n"
                + "|  /\\  | (_| | | | | | |_| | |_\n"
                + "|_/  \\_|\\__,_|_|_| |_|\\__,_|\\__|\n";
    }

    /**
     * Displays the welcome message.
     *
     * @return the greeting message shown to users
     */
    public String showGreeting() {
        return "Woof! I'm Walnut.\n"
                + "Ready to fetch some tasks?";
    }

    /**
     * Displays the farewell message.
     *
     * @return the farewell message shown when the application exits
     */
    public String showFarewell() {
        return "Session complete. Time for a well-earned rest.";
    }

    /**
     * Displays the supported task command formats.
     *
     * @return a message describing the expected task command format
     */
    public String showInvalidCommand() {
        return "That command went off-leash. Type /help to see the available commands.";
    }

    /**
     * Displays all tasks in the specified list.
     *
     * @param tasks Task list to display.
     * @return a formatted string containing all tasks in the list
     */
    public String showTaskList(TaskList tasks) {
        return "Here are the tasks I sniffed out:\n" + tasks.toString();
    }

    /**
     * Displays a message for an empty task list.
     *
     * @return a message indicating that the task list is empty
     */
    public String showEmptyTaskListMessage() {
        return "The list is waiting for its first task. Please add a task first.\n";
    }

    /**
     * Displays an invalid task-number message with the valid upper bound.
     *
     * @param size Number of tasks in the list.
     * @return an error message that includes the valid task-number range
     */
    public String showInvalidTaskNumber(int size) {
        assert size >= 0 : "Task-list size cannot be negative";
        return "That task number ran out of the yard. Please enter a number between 1 and "
                + size + ".\n";
    }

    /**
     * Displays a message when a task number is missing.
     *
     * @return a prompt asking the user to specify the task number
     */
    public String showMissingTaskNumber() {
        return "Please tell me which task needs attention.";
    }

    /**
     * Displays confirmation that a task was completed.
     *
     * @param task Task that was completed.
     * @return a confirmation message indicating the task was marked as done
     */
    public String showTaskMarkedAsDone(Task task) {
        return "Task marked as done. Give yourself a round of appaws!";
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task Task that was marked incomplete.
     * @return a confirmation message indicating the task was marked as not done
     */
    public String showTaskMarkedAsNotDone(Task task) {
        return "Task marked as incomplete. It still needs a little attention.";
    }

    /**
     * Displays a message when a task description is empty.
     *
     * @param taskType Type of task with the empty description.
     * @return an error message indicating the description cannot be empty
     */
    public String showEmptyDescription(String taskType) {
        return "Please give the task something to bark about. The description of a " + taskType
                + " cannot be empty.";
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param numberOfTasks Number of tasks currently in the list.
     * @return a message confirming the added task and current task count
     */
    public String showTaskAdded(Task task, int numberOfTasks) {
        String plural = numberOfTasks == 1 ? "task" : "tasks";
        return String.format(ADDED_TASK_TEMPLATE, task, numberOfTasks, plural);
    }

    /**
     * Displays the required event command format.
     *
     * @return a message describing the correct event command format
     */
    public String showInvalidEventFormat() {
        return "The event command needs a little obedience training."
                + "Please use the format: "
                + "event <description> /from <start time> /to <end time>\n";
    }

    /**
     * Displays the required deadline command format.
     *
     * @return a message describing the correct deadline command format
     */
    public String showInvalidDeadlineFormat() {
        return "That deadline needs better command training.\n"
                + "Please use the format: "
                + "deadline <description> /by <deadline>\n";
    }

    /**
     * Displays the required date and time format.
     *
     * @return a message describing the expected date/time format
     */
    public String showInvalidDateTime() {
        return "That date did not pass the sniff test. "
                + "Please use the format <YYYY-mm-dd HHmm>";
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task Task that was removed.
     * @param remainingTasks Number of tasks remaining in the list.
     * @return a message confirming the removed task and remaining task count
     */
    public String showTaskRemoved(Task task, int remainingTasks) {
        String plural = remainingTasks == 1 ? "task" : "tasks";
        return String.format(REMOVED_TASK_TEMPLATE, task, remainingTasks, plural);
    }

    /**
     * Displays a storage loading error.
     *
     * @return the loading error message shown when saved tasks cannot be loaded
     */
    public String showStorageLoadError() {
        return "I could not fetch your saved tasks.\n"
                + "The task file may have gone into hiding.";
    }

    /**
     * Displays a storage saving error.
     *
     * @return the saving error message shown when changes cannot be saved
     */
    public String showStorageSaveError() {
        return "Your task data slipped its leash. "
                + "Your previous task data has been preserved.";
    }

    /**
     * Displays a message when no search keyword is provided.
     *
     * @return a prompt asking the user to specify a search keyword
     */
    public String showEmptyKeyword() {
        return "Please provide a keyword for me to sniff out.";
    }

    /**
     * Displays the tasks that match the specified keyword.
     *
     * @param foundTasks Tasks that match the search keyword.
     * @return a formatted string containing the matching tasks
     */
    public String showFoundTasks(ArrayList<Task> foundTasks) {
        return "Here is what I dug up:\n"
                + TaskList.toString(foundTasks);
    }

    /**
     * Displays a message when the user enters no command.
     *
     * @return a message explaining that a command is required
     */
    public String showEmptyCommand() {
        return "I did not hear a command. Please give me something to fetch."
                + " Type /help to see the available commands.";
    }

    /**
     * Displays a message when an event's end time is not after its start time.
     *
     * @return a message indicating the event end time must be later than the start
     */
    public String showInvalidEventTimeRange() {
        return "That event’s timeline is chasing its tail. The event end time must be later than the start time.";
    }

    /**
     * Displays a message when a duplicate task is added.
     *
     * @return a message indicating the task already exists in the list
     */
    public String showDuplicateTask() {
        return "This task has already joined the pack and already exists in your task list.";
    }
}
