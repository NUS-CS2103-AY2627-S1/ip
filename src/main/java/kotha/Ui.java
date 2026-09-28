package kotha;

import java.util.List;
import java.util.Scanner;

import kotha.tasks.Task;

/** Handles user input and output for Kotha. */
public class Ui {
    private static final String LINE_BREAK = "____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /** Displays the welcome message. */
    public void printWelcomeMessage() {
        String banner = " _  __   ___  _____ _   _    _    \n"
                + "| |/ /  / _ \\|_   _| | | |  / \\   \n"
                + "| ' /  | | | | | | | |_| | / _ \\  \n"
                + "| . \\  | |_| | | | |  _  |/ ___ \\ \n"
                + "|_|\\_\\  \\___/  |_| |_| |_/_/   \\_\\\n";
        System.out.println(banner);
        System.out.println(LINE_BREAK);
        System.out.println("Hello! I'm KOTHA.");
        System.out.println("What can I do for you?");
        System.out.println(LINE_BREAK);
    }

    /** Reads the next command from the user. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the goodbye message. */
    public void printGoodbye() {
        System.out.println(LINE_BREAK);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE_BREAK);
    }

    /** Displays all tasks with one-based numbering. */
    public void printTaskList(List<Task> tasks) {
        printTasks(tasks, "Here are the tasks in your list:");
    }

    /** Displays tasks matching a search keyword with one-based numbering. */
    public void printSearchResults(List<Task> tasks) {
        printTasks(tasks, "Here are the matching tasks in your list:");
    }

    /** Displays tasks with a heading and one-based numbering. */
    private void printTasks(List<Task> tasks, String heading) {
        System.out.println(LINE_BREAK);
        System.out.println(heading);
        for (int pointer = 0; pointer < tasks.size(); pointer++) {
            System.out.printf("%d.%s%n", pointer + 1, tasks.get(pointer));
        }
        System.out.println(LINE_BREAK);
    }

    /**
     * Displays the number of tasks currently stored.
     *
     * @param taskCount the number of tasks that the list contains.
     */
    public void printTaskCount(int taskCount) {
        String suffix = taskCount == 1 ? " task" : " tasks";
        System.out.println("Hiee you now you have " + taskCount + suffix + " in your list.");
        System.out.println(LINE_BREAK);
    }

    /** Displays an error generated while processing a command. */
    public void printErrorMessage(String errorMessage) {
        System.out.println(LINE_BREAK);
        System.out.println("Master thou has committed a misprision! " + errorMessage);
        System.out.println(LINE_BREAK);
    }
}
