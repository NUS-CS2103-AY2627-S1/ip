package nelson.ui;

import java.util.List;
import java.util.Scanner;

import nelson.task.Task;

/** Handles Nelson's console input and output. */
public class Ui {
    /** Reads commands from standard input. */
    private final Scanner scanner = new Scanner(System.in);

    /** Displays Nelson's startup banner. */
    public void showWelcome() {
        String logo =
                "       _   __     __\n"
                        + "       / | / /___ / /________  ____\n"
                        + "      /  |/ / __ \\/ / ___/ __ \\/ __ \\\n"
                        + "     / /|  /  __/ / (__  ) /_/ / / / /\n"
                        + "    /_/ |_/\\___/_/_/____/\\____/_/ /_/\n";
        System.out.println("System booting...\n" + logo);
        showSeparator();
        System.out.println("    Molo! I have a surprise for you. Your move!");
        System.out.println("    Type your move, or are you just going to let your time run out?");
        showSeparator();
    }

    /**
     * Returns whether another command is available.
     * @return {@code true} when another input line exists
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command.
     * @return the next input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard command separator. */
    public void showSeparator() {
        System.out.println("    ____________________________________________________________");
    }

    /** Displays the resignation response. */
    public void showGoodbye() {
        System.out.println("    Molo! Resigning already? Pathetic. I win.");
        showSeparator();
    }

    /**
     * Displays one or more normal response lines.
     *
     * @param messages response text without the standard indentation
     */
    public void show(String... messages) {
        for (String message : messages) {
            System.out.println("    " + message);
        }
    }

    /**
     * Displays an error response.
     * @param message error text without the standard indentation
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Displays matching tasks with one-based list numbering.
     *
     * @param tasks tasks matching the user's search keyword.
     */
    public void showMatchingTasks(List<Task> tasks) {
        show("Here are the matching tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            show((index + 1) + "." + tasks.get(index));
        }
    }
}
