package ari.ui;

import java.util.Scanner;

/**
 * Handles input from and output to the user.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";

    private static final String BANNER =
            "   ----   \n"
                    + "  / /\\ \\ \n"
                    + " / /__\\ \\ \n"
                    + "/ /    \\ \\ \n";

    private static final String COMMAND_LIST =
            "Here is a list of supported commands:\n\n"
                    + "Keyword  |                 Format                | Description \n\n"
                    + "todo     | todo <task>                           | "
                    + "Adds a task to your list of tasks! (e.g., todo read book)\n"
                    + "deadline | deadline <task> /by <time>            | "
                    + "Adds a task with a deadline. (e.g., deadline do something /by Sunday)\n"
                    + "event    | event <event> /from <time> /to <time> | "
                    + "Adds an event with 'from' and 'to' times. "
                    + "(e.g., event dinner party /from Monday 2pm /to 9pm)\n"
                    + "find     | find <keyword>                        | "
                    + "Finds tasks containing a keyword. (e.g., find book)\n"
                    + "mark     | mark <task ID>                        | "
                    + "Marks the task with the task ID as done! (e.g., mark 1)\n"
                    + "unmark   | unmark <task ID>                      | "
                    + "Does the opposite of mark. (e.g., unmark 1)\n"
                    + "delete   | delete <task ID>                      | "
                    + "Removes the specified task from the list (e.g., delete 1)\n"
                    + "list     | list                                  | "
                    + "Lists all your tasks in their current order. (e.g., list)\n"
                    + "sort     | sort                                  | "
                    + "Sorts tasks alphabetically, ignoring case; updates task IDs.\n"
                    + "exit     | exit                                  | "
                    + "Ends the program (e.g., exit)\n"
                    + "bye      | bye                                   | "
                    + "Serves the same purpose as 'exit' (e.g., bye)\n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the greeting and supported commands.
     */
    public void showWelcome() {
        showMessage(BANNER);
        showMessage("Hola, I'm Ari!\nNeed any help?\n");
        showMessage(COMMAND_LIST);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Full command entered by the user, or null at end of input.
     */
    public String readCommand() {
        return this.scanner.hasNextLine() ? this.scanner.nextLine() : null;
    }

    /**
     * Displays a message to the user.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays a message between divider lines.
     *
     * @param message Message to display.
     */
    public void showMessageWithLines(String message) {
        showMessage(DIVIDER);
        showMessage(message);
        showMessage(DIVIDER + "\n");
    }

    /**
     * Displays an error encountered while initializing storage.
     *
     * @param errorMessage Description of the error.
     */
    public void showStorageInitializationError(String errorMessage) {
        showMessage("Sorry, I couldn't initialize the storage file: " + errorMessage);
    }

    /**
     * Displays a message explaining that there are no saved tasks.
     */
    public void showNoSavedTasks() {
        showMessage("There are no saved tasks yet!");
    }

    /**
     * Displays a message confirming that saved tasks were loaded.
     */
    public void showTasksLoaded() {
        showMessage("All tasks were loaded!");
    }

    /**
     * Displays an error encountered while loading tasks.
     *
     * @param errorMessage Description of the error.
     */
    public void showLoadingError(String errorMessage) {
        showMessage("Sorry, I couldn't load your tasks: " + errorMessage);
    }

    /**
     * Displays a message confirming that tasks were saved.
     */
    public void showTasksSaved() {
        showMessage("I've saved your tasks.");
    }

    /**
     * Displays an error encountered while saving tasks.
     *
     * @param errorMessage Description of the error.
     */
    public void showSavingError(String errorMessage) {
        showMessage("Sorry, I couldn't save your tasks: " + errorMessage);
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        this.scanner.close();
    }
}
