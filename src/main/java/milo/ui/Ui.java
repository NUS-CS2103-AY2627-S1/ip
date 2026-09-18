package milo.ui;

import java.util.List;
import java.util.Scanner;

/**
 * Deals with all interaction with the user: reading what they type and
 * printing what Milo says back. Nothing outside this class touches
 * {@code System.out} or {@code System.in} directly, so the console format
 * (dividers, indentation) only needs to be gotten right in one place.
 * <p>
 * Milo speaks in one consistent voice: eager and warm, brief to the point of
 * clipped, and willing to own a mistake without making a fuss of it. The dog
 * it is named after shows up in the verbs (fetching, sniffing out, burying)
 * rather than in exclamation marks, so the personality does not get in the
 * way of reading a long task list.
 */
public class Ui {
    /** Indentation applied to every line Milo prints. */
    private static final String INDENT = "    ";

    /** Horizontal line used to separate blocks of Milo's output. */
    private static final String DIVIDER =
            INDENT + "____________________________________________________________";

    /**
     * ASCII-art banner spelling out the chatbot's name.
     * Note there is no trailing newline: showResponse supplies it.
     */
    private static final String BANNER = " __  __  _  _         \n"
            + "|  \\/  |(_)| |  ___  \n"
            + "| |\\/| || || | / _ \\ \n"
            + "| |  | || || || (_) |\n"
            + "|_|  |_||_||_| \\___/ ";

    /** The plain greeting shown alongside the banner. */
    private static final String GREETING = "Woof! Milo here.\nWhat are we getting done today?";

    /** Where user input is read from. */
    private final Scanner scanner = new Scanner(System.in);

    /** The plain text (no dividers or indentation) of the most recent {@link #showResponse}. */
    private String lastResponse = "";

    /** Prints the banner and the opening greeting. */
    public void showWelcome() {
        showResponse(BANNER, GREETING);
    }

    /**
     * Returns the opening greeting without the banner, for an interface
     * (such as the GUI) that doesn't display console-style ASCII art.
     *
     * @return the greeting text
     */
    public String getGreeting() {
        return GREETING;
    }

    /** Prints the closing message shown when the user exits. */
    public void showGoodbye() {
        showResponse("Off I go. Your list is safe with me!");
    }

    /**
     * Reports what happened while loading the saved tasks: any warnings from
     * an unreadable save file, or, if the load was clean, how many tasks were
     * carried over. Prints nothing on a clean load of an empty list, since a
     * first-time user has nothing to be told about yet.
     *
     * @param warnings  problems noticed while loading, empty if none
     * @param taskCount how many tasks were loaded
     * @return what was shown, or an empty string if there was nothing to report
     */
    public String showLoadStatus(List<String> warnings, int taskCount) {
        if (!warnings.isEmpty()) {
            showResponse(warnings.toArray(new String[0]));
        } else if (taskCount > 0) {
            showResponse("Fetched " + describeCount(taskCount) + " from last time. "
                    + "Say list to see them.");
        } else {
            return "";
        }
        return lastResponse;
    }

    /**
     * Reports an error to the user.
     *
     * @param message what went wrong, phrased for the user to read
     */
    public void showError(String message) {
        showResponse(message);
    }

    /**
     * Describes a number of tasks in words, so that a list of one is not
     * reported as "1 tasks". Small, but it is the sort of thing that makes a
     * chatbot sound careless.
     *
     * @param count how many tasks
     * @return e.g. "1 task" or "3 tasks"
     */
    public static String describeCount(int count) {
        return count == 1 ? "1 task" : count + " tasks";
    }

    /**
     * Returns whether there is another line of input to read.
     * Checking this before {@link #readCommand()} lets the caller exit
     * gracefully when input ends without the user typing "bye", instead of
     * {@code readCommand()} throwing.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one line of input from the user.
     *
     * @return the line, with leading and trailing whitespace removed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the given lines as one block: framed by divider lines above and
     * below, indented to match Milo's output format, and followed by a blank
     * line that separates it from whatever the user types next.
     * A line that itself contains newlines (such as the banner) is split so
     * that every physical line receives the same indentation.
     *
     * @param lines the lines of text to display inside the block
     */
    public void showResponse(String... lines) {
        lastResponse = String.join("\n", lines);

        System.out.println(DIVIDER);
        for (String line : lines) {
            for (String physicalLine : line.split("\n")) {
                System.out.println(INDENT + " " + physicalLine);
            }
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Returns the plain text of the most recent {@link #showResponse} call,
     * without the console-only dividers and indentation. Meant for an
     * interface (such as the GUI) that renders a response itself instead of
     * relying on this class's console formatting.
     *
     * @return the plain text of the last response, or an empty string if
     *         nothing has been shown yet
     */
    public String getLastResponse() {
        return lastResponse;
    }
}
