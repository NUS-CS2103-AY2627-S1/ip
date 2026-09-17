package orbit;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handles console input and output for Orbit.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "  ___  ____  ____ ___ _____\n"
            + " / _ \\|  _ \\| __ )_ _|_   _|\n"
            + "| | | | |_) |  _ \\| |  | |\n"
            + "| |_| |  _ <| |_) | |  | |\n"
            + " \\___/|_| \\_\\____/___| |_|";
    private final Scanner scanner;

    /** @param scanner Source of user commands. */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /** @return Whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** @return Next trimmed user command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Formats ordered tasks for a CLI or GUI response without modifying the list.
     *
     * @param heading Text before the numbered tasks.
     * @param tasks Tasks to display, in display order.
     * @return Heading followed by one-based task lines, or only the heading when empty.
     */
    public static String formatTaskList(String heading, List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(index -> "\n " + (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining("", heading, ""));
    }

    /** Displays Orbit's greeting. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println(" Hello! I'm Orbit");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /** @param message Message to display between divider lines. */
    public void showMessage(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}
