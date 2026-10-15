package doe;

import java.util.List;
import java.util.Scanner;

/**
 * Contains parse, print and other checks.
 * Main interface to communiate with the user.
 */
public class UserInterface {

    // ui banners as previous versions
    private static final String BANNER = "____________________________________________________________\n"
            + "     _          \n"
            + "  __| | ___  ___\n"
            + " / _` |/ _ \\ / _ \\\n"
            + "| (_| | (_) |  __/\n"
            + " \\__,_|\\___/ \\___|\n\n"
            + "hello! i'm doe :).\n"
            + "what can i do for you?\n"
            + "____________________________________________________________\n"
            + "1. neigh\n"
            + "2. meow\n"
            + "3. list\n"
            + "4. todo\n"
            + "____________________________________________________________\n"
            + "type \"bye\" to exit\n"
            + "____________________________________________________________\n";

    private static final String NEIGH = "____________________________________________________________\n"
            + "eurhggghhhhh!\n"
            + "____________________________________________________________\n";

    private static final String MEOW = "____________________________________________________________\n"
            + "meow!\n"
            + "____________________________________________________________\n";

    private static final String LIST = "____________________________________________________________\n"
            + "roles and responsibilities\n"
            + "1. survive nus cs\n"
            + "2. get a few internships\n"
            + "3. work at mcdonalds\n"
            + "4. retire as a manager (hopefully)\n"
            + "____________________________________________________________\n";

    private static final String TODO_MENU = "____________________________________________________________\n"
            + "modify todo list\n"
            + "1. add\n"
            + "2. remove\n"
            + "3. view\n"
            + "4. sort\n"
            + "5. mark\n"
            + "6. unmark\n"
            + "7. find\n"
            + "8. exit\n"
            + "____________________________________________________________\n";

    private static final String BYE = "____________________________________________________________\n"
            + "bye. hope to see you again soon!\n"
            + "____________________________________________________________\n";

    private static final String SAVED = "____________________________________________________________\n"
            + "item saved successfully!\n"
            + "____________________________________________________________\n";

    private static final String REMOVED = "____________________________________________________________\n"
            + "item removed successfully!\n"
            + "____________________________________________________________\n";

    private static final String ADD_MENU = "____________________________________________________________\n"
            + "what type of task would you like to add?\n"
            + "1. todo\n"
            + "2. deadline\n"
            + "3. event\n"
            + "4. exit\n"
            + "____________________________________________________________\n";

    private static final String ADD_PROMPT = "____________________________________________________________\n"
            + "what you want to add?\n"
            + "____________________________________________________________\n";

    private static final String REMOVE_PROMPT = "____________________________________________________________\n"
            + "what you want to remove?\n"
            + "____________________________________________________________\n";

    private static final String DEADLINE_DATE = "____________________________________________________________\n"
            + "input deadline in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1800):\n"
            + "____________________________________________________________\n";

    private static final String EVENT_DATE = "____________________________________________________________\n"
            + "input event time in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1100):\n"
            + "____________________________________________________________\n";

    private static final String MARK_DONE = "____________________________________________________________\n"
            + "to mark a task as done, enter its number from the list.\n"
            + "____________________________________________________________";

    private static final String MARK_UNDONE = "____________________________________________________________\n"
            + "to mark a task as not done, enter its number from the list.\n"
            + "____________________________________________________________";

    private static final String INVALID_DATE = "invalid date format. please use dd-mm-yyyy (e.g. 17-07-2004 1800).\n";

    private static final String INVALID_ADD_SELECTION =
            "that option is incorrect. please choose 1, 2, 3, todo, deadline, or event.";

    private static final String FIND_PROMPT = "____________________________________________________________\n"
            + "enter a keyword to search for:\n"
            + "____________________________________________________________\n";

    private final Scanner scanner;

    /**
     * Initializes the UserInterface by opening a new Scanner for system input.
     */
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input from the user.
     *
     * @return The user's input string.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            throw new java.util.NoSuchElementException("console input closed");
        }
        return scanner.nextLine();
    }

    // collection of printing functions for ease of showing ui

    /** Prints the main welcome banner to the terminal. */
    public void printBanner() { System.out.println(BANNER); }

    /** Prints a neigh sound to the terminal. */
    public void printNeigh() { System.out.println(NEIGH); }

    /** Prints a meow sound to the terminal. */
    public void printMeow() { System.out.println(MEOW); }

    /** Prints a static list of roles to the terminal. */
    public void printList() { System.out.println(LIST); }

    /** Prints the todo list management menu to the terminal. */
    public void printTodoMenu() { System.out.println(TODO_MENU); }

    /** Prints the add task menu to the terminal. */
    public void printAddMenu() { System.out.println(ADD_MENU); }

    /** Prints a prompt asking the user what task they want to add. */
    public void printAddPrompt() { System.out.println(ADD_PROMPT); }

    /** Prints a prompt asking the user what task they want to remove. */
    public void printRemovePrompt() { System.out.println(REMOVE_PROMPT); }

    /** Prints a prompt asking the user to input a deadline date. */
    public void printDeadlineDate() { System.out.println(DEADLINE_DATE); }

    /** Prints a prompt asking the user to input an event date. */
    public void printEventDate() { System.out.println(EVENT_DATE); }

    /** Prints a prompt for marking a task as done. */
    public void printMarkPrompt() { System.out.println(MARK_DONE); }

    /** Prints a prompt for marking a task as not done. */
    public void printUnmarkPrompt() { System.out.println(MARK_UNDONE); }

    /** Prints a farewell message to the user. */
    public void printBye() { System.out.println(BYE); }

    /** Prints a confirmation that an item was saved successfully. */
    public void printSaved() { System.out.println(SAVED); }

    /** Prints a confirmation that an item was removed successfully. */
    public void printRemoved() { System.out.println(REMOVED); }

    /** Closes the scanner to free up system resources. */
    public void closeScanner() { scanner.close(); }

    /** Prints the find menu prompt */
    public void printFindPrompt() { System.out.println(FIND_PROMPT); }

    /**
     * Prints the provided list of todo items with numerical formatting.
     *
     * @param todoItems The list of tasks to be printed.
     */
    public void printTodoList(List<Task> todoItems) {
        System.out.println("____________________________________________________________");
        System.out.println("here are the tasks in your list:");
        for (int i = 0; i < todoItems.size(); i++) {
            Task task = todoItems.get(i);
            System.out.println((i + 1) + ". " + task);
        }
        System.out.println("____________________________________________________________");
    }

    /**
     * Formats a sorted list with a separate section for tasks without dates.
     * Numbering continues across sections to match the backing task list.
     *
     * @param sortedTasks Tasks already sorted by date, with undated tasks last.
     * @return The list text shown after sorting in either interface.
     */
    public static String sortedTodoListText(List<Task> sortedTasks) {
        StringBuilder result = new StringBuilder("here are the tasks in your list:");
        boolean hasUndatedSection = false;
        for (int i = 0; i < sortedTasks.size(); i++) {
            Task task = sortedTasks.get(i);
            if (!hasUndatedSection && task.getDate() == null) {
                result.append("\n\nno deadlines:");
                hasUndatedSection = true;
            }
            result.append('\n').append(i + 1).append(". ").append(task);
        }
        return result.toString();
    }

    /**
     * Prints the sorted list using the console's usual separators.
     *
     * @param sortedTasks Tasks already sorted by date.
     */
    public void printSortedTodoList(List<Task> sortedTasks) {
        System.out.println("____________________________________________________________");
        System.out.println(sortedTodoListText(sortedTasks));
        System.out.println("____________________________________________________________");
    }

    /**
     * Reads the task number from the user and returns the zero-based index.
     * Returns -1 when the input is invalid or out of bounds.
     *
     * @param numberOfTasks The current total count of tasks available.
     * @return The zero-based integer index of the selected task, or -1 if invalid.
     */
    public int getTaskIndex(int numberOfTasks) {
        assert numberOfTasks >= 0 : "A task count cannot be negative";
        if (numberOfTasks == 0) {
            System.out.println("there are no tasks to mark yet. add a task first.");
            return -1;
        }
        System.out.println("enter the task number:");
        try {
            int taskNumber = Integer.parseInt(readCommand().strip());
            if (taskNumber >= 1 && taskNumber <= numberOfTasks) {
                return taskNumber - 1;
            }
        } catch (NumberFormatException exception) {
            // Handled by unexpected input message below
        }
        printUnexpectedInputMessage(
                "that task number is incorrect. please enter a number from 1 to " + numberOfTasks + ".");
        return -1;
    }

    /**
     * Displays an existing invalid-input message through the application's input exception.
     *
     * @param message The detailed string message highlighting the unexpected input.
     */
    public static void printUnexpectedInputMessage(String message) {
        try {
            throw new UnexpectedInputException(message);
        } catch (UnexpectedInputException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Ensures that the "|" delimiter is not inputted by the user to prevent corruption of the .txt storage file.
     *
     * @param input The text string to evaluate.
     * @return True if the string contains "|", false otherwise.
     */
    public static boolean preventCorrupt(String input) {
        if (input.contains("|")) {
            printUnexpectedInputMessage("error: input cannot contain '|' character.");
            return true;
        }
        return false;
    }

    /**
     * Displays menu for find option.
     *
     * @param matchingTasks List of tasks that is found suitable.
     */
    public void printMatchingTasks(List<Task> matchingTasks, List<Task> allTasks) {
        System.out.println("____________________________________________________________");
        if (matchingTasks.isEmpty()) {
            System.out.println("no matching tasks found.");
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((allTasks.indexOf(matchingTasks.get(i)) + 1) + ". " + matchingTasks.get(i));
            }
        }
        System.out.println("____________________________________________________________");
    }
}

















