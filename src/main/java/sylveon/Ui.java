package sylveon;

import java.util.ArrayList;
import java.util.Scanner;

/** Handles displaying messages and reading input for the chatbot. */
public class Ui {
    private static final String LINE =
            "----------<3----------<3----------<3----------";

    private static final String BANNER =
            "  ____          _                              \n"
                    + " / ___| _   _  | | __   __  ___   ___   _ __  \n"
                    + " \\___ \\| | | | | | \\ \\ / / / _ \\ / _ \\ | '_ \\ \n"
                    + "  ___) | |_| | | |  \\ V / |  __/| (_) || | | |\n"
                    + " |____/ \\__, | |_|   \\_/   \\___| \\___/ |_| |_|\n"
                    + "        |___/                                 ";

    private final Scanner scanner = new Scanner(System.in);

    /** Displays the chatbot welcome message. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hi! I'm Sylveon 💗 Ready to get your tasks sorted?\n" + LINE);
    }

    /** Reads a command from the user. */
    public String readCommand() {
        System.out.print("Enter your text: ");
        return scanner.nextLine();
    }

    /** Displays the commands supported by Sylveon. */
    public void showHelp() {
        System.out.println(LINE + "\nCommands: todo <description>, deadline <description> /by <yyyy-mm-dd>, "
                + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>, list, find <keyword>, "
                + "mark <number>, unmark <number>, delete <number>, sort, and bye.\n" + LINE);
    }

    /** Displays the chatbot goodbye message. */
    public void showBye() {
        System.out.println(LINE
                + "\nBye for now! Keep going—you've got this 💪\n"
                + LINE);
    }

    /** Displays all tasks in the task list. */
    public void showList(ArrayList<Task> tasks) {
        System.out.println(LINE);

        if (tasks.isEmpty()) {
            System.out.println("Your task list is clear! Add something when you're ready 😊");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("   " + (i + 1) + "." + tasks.get(i));
            }
        }

        System.out.println(LINE);
    }

    /** Displays sorted tasks and the sort criterion. */
    public void showSorted(String sortType, ArrayList<Task> tasks) {
        System.out.println(LINE);
        String order = sortType.equals("todo") || sortType.equals("all")
                ? "alphabetically" : "by date (earliest first)";
        String subject = sortType.equals("all") ? "tasks" : sortType + "s";
        System.out.println("Sorted " + subject + " " + order + ":");

        if (tasks.isEmpty()) {
            System.out.println("There are no matching tasks.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("   " + (i + 1) + "." + tasks.get(i));
            }
        }
        System.out.println(LINE);
    }

    /** Displays a confirmation that a task was marked as done. */
    public void showMarked(Task task) {
        System.out.println(LINE
                + "\n   Great job! You finished a task 🎉\n   "
                + task + "\n"
                + LINE);
    }

    /** Displays a confirmation that a task was marked as not done. */
    public void showUnmarked(Task task) {
        System.out.println(LINE
                + "\n   No problem! I've marked this task as not done yet 🙂\n   "
                + task + "\n"
                + LINE);
    }

    /** Displays a confirmation that a task was deleted. */
    public void showDeleted(Task task, int remainingTasks) {
        System.out.println(LINE
                + "\n   Done! That task has been removed 🗑️:\n     "
                + task
                + "\n   Now you have "
                + remainingTasks
                + " tasks left! Well done <3\n"
                + LINE);
    }

    /** Displays a confirmation that a task was added. */
    public void showAdded(String command) {
        System.out.println(LINE
                + "\n   Added: " + command
                + "\n"
                + LINE);
    }

    /** Displays an error message. */
    public void showError(String message) {
        System.out.println(LINE
                + "\n   " + message
                + "\n"
                + LINE);
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        System.out.println(LINE);
        System.out.println("Here are the matching tasks in your list:");

        if (tasks.isEmpty()) {
            System.out.println("There are no matching tasks.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("   " + (i + 1) + "." + tasks.get(i));
            }
        }

        System.out.println(LINE);
    }
}
