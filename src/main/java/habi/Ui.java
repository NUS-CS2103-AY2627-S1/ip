package habi;

import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for HABI. */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = " _   _    _     ____   ___\n"
            + "| | | |  / \\   | __ )   |  |\n"
            + "| |_| | / _ \\  |  _ \\  |  |\n"
            + "|  _  |/ ___ \\ | |_) | |  |\n"
            + "|_| |_|_/   \\_\\|____/  _|_\n";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return True if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return The next trimmed command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the HABI banner and greeting. */
    public void showGreeting() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(
                "Hello! I'm HABI, your steady habit-building companion.\nWhat small step can we plan today?");
        System.out.println(DIVIDER);
    }

    /**
     * Prints response lines between the standard dividers.
     *
     * @param lines Response lines to print.
     */
    public void showResponse(String... lines) {
        System.out.println(DIVIDER);
        System.out.println(formatResponse(lines));
        System.out.println(DIVIDER);
    }

    /**
     * Formats response lines for either the console or graphical interface.
     *
     * @param lines Response lines to format.
     * @return Response lines separated by the platform line separator.
     */
    public static String formatResponse(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    /**
     * Prints a heading followed by a numbered task list.
     *
     * @param heading Heading shown before the tasks.
     * @param tasks Tasks to list.
     */
    public void showTaskList(String heading, List<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println(formatTaskList(heading, tasks));
        System.out.println(DIVIDER);
    }

    /**
     * Formats a heading followed by a one-based task list.
     *
     * @param heading Heading shown before the tasks.
     * @param tasks Tasks to list.
     * @return The formatted heading and task list.
     */
    public static String formatTaskList(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Formats a heading followed by a one-based note list.
     *
     * @param heading Heading shown before the notes.
     * @param notes Notes to list.
     * @return The formatted heading and notes in list order.
     */
    public static String formatNoteList(String heading, List<Note> notes) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < notes.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(notes.get(i));
        }
        return response.toString();
    }
}
