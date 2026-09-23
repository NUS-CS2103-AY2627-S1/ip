package max.ui;
import max.task.Task; import java.util.List;
import java.util.stream.Collectors;

public class Ui {
    private String lastResponse = "";
    private static String banner = "  _____  _____ ___  ___\n" 
                                 + " /     \\\\__  \\ \\  \\/  /\n" 
                                 + "|  Y Y  \\/ __ \\_>    < \n"
                                 + "|__|_|  (____  /__/\\_ \\\n" 
                                 + "      \\/     \\/      \\/\n";
    private static String greetings = "Max. Ready.\n"
                                         + "Enter a command.";
    private static String bar = "________________________________________";
    private static String bye =  "Done.\n";
    private static String help = "Available commands:\n"
            + "help - Show this help message.\n"
            + "todo <description> - Add a todo task.\n"
            + "deadline <description> /by <date [time]> - Add a deadline task.\n"
            + "event <description> /from <date [time]> /to <date [time]> - Add an event.\n"
            + "list - Show all tasks.\n"
            + "sort - Sort tasks by deadline.\n"
            + "mark <task number> - Mark a task as done.\n"
            + "unmark <task number> - Mark a task as not done.\n"
            + "delete <task number> - Delete a task.\n"
            + "on <date> - Show tasks on a date.\n"
            + "find <keyword> - Find tasks containing a keyword.\n"
            + "bye - Exit Max.";

    /**
     * Print line separator.
     *
     * Print line to separate commands of user and the bot 
     * 
     * @example
     *
     * ```
     * ui.showline();
     * 
     * prints 
     * ________________________________________
     * ```
     */
    public void showLine() {
        System.out.println(bar);
    }

    /**
     * Display error message.
     *
     * @param msg String : error message to be displayed.
     *
     * @example
     * ```
     * ui.showError("Invalid date");
     * prints: 
     *  Invalid date 
     * ```
     */

    public void showError(String msg) {
        showResponse(msg);
    }

    /**
     * Display the title of the bot.
     *
     * @example
     * ```
     * ui.showBanner();
     * ```
     */
    public void showBanner() {
        System.out.println(banner);
        System.out.println(greetings);
        System.out.println(bar);
 
    }

    /**
     * Returns the greeting shown when Max starts.
     *
     * @return Max's greeting
     */
    public String getGreetings() {
        return greetings;
    }

    /**
     * Display deleted task message.
     *
     * @param task Task : the task deleted.
     * @param remainingCount int : number of task left after deletion 
     * @return Type and description of the returned object.
     *
     * @example
     * ```
    * 
     * ```
     */
    public void showTaskDeleted(Task task, int remainingCount) {
        showResponse("Deleted.\n"
                + task + "\n"
                + remainingCount + " task(s) remaining.");
    }

    /**
     * Display the details of the task marked done.
     *
     * @param task task marked as done.
     *
     * @example
     * ```
     * Task task = new Todo("read book");
     * Ui().showTaskMarkedDone(task);
     *
     * Display:
     * [T][X] read book 
     * ```
     */
    public void showTaskMarkedDone(Task task) {
        showResponse("Marked done.\n" + task);
    }

    /**
     * Display the task that was marked not done.
     *
     * @param task Task that was to be marked not done.
     */
    public void showTaskMarkedNotDone(Task task) {
        showResponse("Marked not done.\n" + task);
    }

    /**
     * Display all the task in a list.
     *
     * @param tasks list of task to be displayed 
     *
     */
    public void showAllTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("No tasks.");
            return;
        }
        
        String response = "Tasks:\n"
                            + tasks.stream()
                                .map(Task::toString)
                                .collect(Collectors.joining("\n", "", "\n"));

        showResponse(response.stripTrailing());

    }

    /**
     * Displays the task list after sorting deadline tasks chronologically.
     *
     * @param tasks tasks to be displayed
     */
    public void showSortedTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("No tasks.");
            return;
        }

        String response = "Sorted by deadline:\n"
                + tasks.stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        showResponse(response);
    }

    /**
     * Show task on specified date.
     *
     * @param  tasks list of task to be displayed.
     *
     */
    public void showTasksOn(List<Task> tasks) {
        String response = tasks.stream().map(Task::toString).collect((Collectors.joining("\n", "", "\n")));
        showResponse(response.stripTrailing());
    }
    /**
     * Show task added.
     *
     * @param task task to be added.
     * @param totalTasks number of task in total 
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showResponse("Added.\n" + task + "\n"
                + totalTasks + " task(s) in the list.");
    }

    /**
     * Display good BYE message.
     *
     */
    public void showGoodBye() {
        showResponse(bye);
    }

    /** Displays the commands supported by Max and their usage. */
    public void showHelp() {
        showResponse(help);
    }


    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showResponse("No matching tasks.");
            return;
        }
        String response = "Matches:\n" +
                            tasks.stream().map(Task::toString).collect(Collectors.joining("\n", "", "\n"));
        showResponse(response.toString());
    }

    /**
     * Returns the most recent response displayed by this UI.
     *
     * @return the most recent displayed response
     */
    public String getLastResponse() {
        return lastResponse;
    }

    private void showResponse(String response) {
        lastResponse = response;
        System.out.println(response);
    }
} 
