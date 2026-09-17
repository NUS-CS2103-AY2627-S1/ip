package harvey.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import harvey.task.Task;
import harvey.task.TaskList;
/**
 * Owns the wording of everything Harvey says, and the reading of everything the user types.
 * <p>
 * The {@code format} methods build a reply and hand it back rather than printing it. That
 * split is what lets one set of wording serve two front ends: the text interface prints
 * what it is given, while the graphical interface puts the same text inside a dialog
 * bubble. Only the handful of {@code print} methods below write to {@code System.out}, and
 * only the text interface calls them.
 */
public class Ui {
    /** Horizontal line used to separate Harvey's replies from the user's input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII art of the chatbot's name. Text interface only; it would not line up in a window. */
    private static final String BANNER = " _   _     _     ____  __     __ _____ __   __\n"
            + "| | | |   / \\   |  _ \\ \\ \\   / /| ____|\\ \\ / /\n"
            + "| |_| |  / _ \\  | |_) | \\ \\ / / |  _|   \\ V / \n"
            + "|  _  | / ___ \\ |  _ <   \\ V /  | |___   | |  \n"
            + "|_| |_|/_/   \\_\\|_| \\_\\   \\_/   |_____|  |_|  ";

    /**
     * Prefix put in front of anything that went wrong, so mistakes read consistently.
     * Harvey is a lawyer, so he objects rather than apologizes.
     */
    private static final String ERROR_PREFIX = "Objection! ";

    /** Reads the lines the user types. Kept as a field so one Scanner serves the whole run. */
    private final Scanner scanner;

    /** Creates a user interface that reads from standard input and writes to standard output. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the user has typed anything more.
     * This is false once input runs out, e.g. the user pressed Ctrl-D.
     *
     * @return true if there is another line to read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line the user typed.
     * Trimming here means stray spaces around the input do not hide the command word,
     * so no caller has to remember to do it.
     *
     * @return the line, without surrounding spaces.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns the welcome message shown when Harvey starts up.
     * The banner is left out because the graphical interface shows this text in a
     * proportional font, where the ASCII art would not line up.
     *
     * @return the greeting.
     */
    public String formatGreeting() {
        return formatLines("Harvey Specter. I close things.", "What's the case?");
    }

    /**
     * Joins lines into one reply, separated by the line ending this platform uses.
     * <p>
     * Varargs suit this because the number of lines differs at every call and is known
     * when the code is written. A caller lists its lines as ordinary arguments instead of
     * threading {@code System.lineSeparator()} between them by hand, which is easy to get
     * wrong and hard to read. Taking an array or a List instead would force every caller
     * to build a collection purely to pass it straight in.
     *
     * @param lines the lines, in the order they should appear.
     * @return the lines joined by the platform's line separator.
     */
    public String formatLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    /**
     * Returns the sentence reporting how much is left in the list after a change.
     * <p>
     * Adding and deleting both end their reply this way, so the wording lives here rather
     * than being spelled out in each command. Keeping it in one place is also what makes
     * the singular case worth handling: "1 case" instead of "1 cases".
     *
     * @param count how many tasks remain.
     * @return the closing sentence of an add or delete reply.
     */
    public String formatTaskCount(int count) {
        return "You've got " + count + (count == 1 ? " case" : " cases") + " on the docket.";
    }

    /**
     * Returns the parting message shown when the user says goodbye or input runs out.
     *
     * @return the farewell.
     */
    public String formatFarewell() {
        return "We're done here. Go win something.";
    }

    /**
     * Returns a report of something that went wrong.
     * Callers pass only the explanation; the objection in front is added here so that
     * every error reads the same way.
     *
     * @param message what went wrong, phrased for the user.
     * @return the explanation, with the objection in front.
     */
    public String formatError(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Returns the stored tasks, numbered from 1 as the user refers to them.
     * <p>
     * The numbering is display, not storage, so it is done here rather than in
     * {@link TaskList}: the list itself has no opinion about how it should look.
     *
     * @param tasks the tasks to show, assumed not empty.
     * @return the heading followed by the numbered tasks.
     */
    public String formatTaskList(TaskList tasks) {
        return formatNumberedTasks("Here's your docket:", tasks);
    }

    /**
     * Returns the tasks that matched a search, numbered from 1.
     * <p>
     * The numbers count the matches rather than naming positions in the full list, which
     * is what the worked example in the requirements shows.
     *
     * @param tasks the matching tasks, assumed not empty.
     * @return the heading followed by the numbered matches.
     */
    public String formatMatchingTasks(TaskList tasks) {
        return formatNumberedTasks("Here's what I dug up:", tasks);
    }

    /**
     * Returns an opening line followed by the tasks, numbered from 1.
     *
     * @param heading the line shown above the tasks.
     * @param tasks   the tasks to show, assumed not empty.
     * @return the heading followed by the numbered tasks.
     */
    private String formatNumberedTasks(String heading, TaskList tasks) {
        List<Task> shown = tasks.asList();

        // IntStream.range rather than a stream of the tasks themselves, because each line
        // needs the task's position as well as the task. Task.toString() supplies the
        // "[X] description" part, and the position is shown counting from 1.
        Stream<String> numbered = IntStream.range(0, shown.size())
                .mapToObj(i -> (i + 1) + "." + shown.get(i));

        // A varargs parameter is an array underneath, so a method declared with one also
        // accepts an array. That is what a run of lines whose length is not known until
        // run time has to be passed as.
        return formatLines(Stream.concat(Stream.of(heading), numbered).toArray(String[]::new));
    }

    /** Prints the banner and welcome message. Text interface only. */
    public void printGreeting() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(formatGreeting());
        System.out.println(DIVIDER);
    }

    /**
     * Prints one reply, wrapped in a divider so it stands out from the lines the user
     * typed. Text interface only; the graphical interface separates replies visually
     * instead, so a divider there would only be clutter.
     *
     * @param message the text to show to the user.
     */
    public void printReply(String message) {
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
