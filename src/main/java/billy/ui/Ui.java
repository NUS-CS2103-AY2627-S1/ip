package billy.ui;

import java.util.List;
import java.util.Scanner;

/**
 * Everything Billy says to the user, and everything the user types back.
 *
 * <p>Gathering this in one class means the rest of Billy can describe *what*
 * happened without deciding *how* it appears. Nothing else needs to know that
 * messages are fenced by divider lines or that input arrives from the keyboard,
 * so moving Billy to a window later would be a change to this class alone.
 *
 * <p>Each message is printed as a block between two dividers, so the user can
 * always see where Billy's answer begins and ends.
 */
public class Ui {

    /** Horizontal line used to visually separate Billy's messages from the rest of the output. */
    private static final String DIVIDER =
            "____________________________________________________________";

    /** ASCII art of the chatbot's name, shown once when Billy starts up. */
    private static final String BANNER =
            " ____  _ _ _       \n"
            + "| __ )(_) | |_   _ \n"
            + "|  _ \\| | | | | | |\n"
            + "| |_) | | | | |_| |\n"
            + "|____/|_|_|_|\\__, |\n"
            + "             |___/ ";

    /**
     * How many tasks a list must hold before Billy remarks on its length.
     *
     * <p>Ten is high enough that an ordinary session never trips it by accident,
     * and low enough that a list worth remarking on does.
     */
    private static final int BUSY_LIST_SIZE = 10;

    /** Where the user's commands are read from. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Collects what Billy says instead of printing it, or null while Billy is
     * talking to the console.
     *
     * <p>The window needs Billy's answer as a piece of text it can put in a
     * dialog box, not as something already written to a console that is not
     * there. Collecting it here keeps that difference in the one class whose
     * job is how Billy's words reach the user.
     */
    private StringBuilder transcript = null;

    /**
     * Whether anything said since collecting started was reported as an error.
     *
     * <p>The console shows a mistake in the same shape as any other message, so
     * it has never needed to remember this. The window sets errors apart, and
     * this class is the only one that still knows which messages were errors by
     * the time they have been flattened into a single piece of text.
     */
    private boolean isErrorCollected = false;

    /**
     * Names a number of tasks with the matching plural, e.g. {@code 1 task} or
     * {@code 3 tasks}.
     *
     * <p>Lives here because it is a matter of wording, and is shared by the
     * commands that change the list and by the message shown when a saved list
     * is read, which would otherwise each spell the plural out for themselves.
     *
     * @param count how many tasks there are
     * @return the count with the right noun after it
     */
    public static String describeListSize(int count) {
        return count + (count == 1 ? " task" : " tasks");
    }

    /**
     * Names a set of task numbers the way a person would, e.g. {@code task 1},
     * {@code tasks 1 and 2} or {@code tasks 1, 2 and 5}.
     *
     * <p>Wording, so it lives here rather than in the command that happens to
     * need it. The last separator is "and" rather than a comma, which is what
     * stops the longest form reading as a fragment of a list.
     *
     * @param numbers the task numbers to name, in the order they should be read
     * @return the phrase naming them, with the right singular or plural
     */
    public static String describeTaskNumbers(List<Integer> numbers) {
        assert !numbers.isEmpty() : "There is nothing to name if no task matched.";
        if (numbers.size() == 1) {
            return "task " + numbers.get(0);
        }
        List<String> written = numbers.stream().map(String::valueOf).toList();
        String allButLast = String.join(", ", written.subList(0, written.size() - 1));
        return "tasks " + allButLast + " and " + written.get(written.size() - 1);
    }

    /**
     * Says how many tasks are now stored, e.g.
     * {@code That's 3 tasks on the books.}
     *
     * <p>Past {@value #BUSY_LIST_SIZE} tasks Billy has something to say about
     * it. The remark is here rather than in the commands so that adding and
     * deleting cannot disagree about when a list has got long.
     *
     * @param count how many tasks there are
     * @return the sentence to append to a confirmation
     */
    public static String describeNewListSize(int count) {
        String sentence = "That's " + describeListSize(count) + " on the books.";
        return count >= BUSY_LIST_SIZE ? sentence + " Ambitious." : sentence;
    }

    /**
     * Returns the words Billy opens with, without the banner around them.
     *
     * <p>Asked for by name so that the window can open with the same greeting
     * the console does. The banner is left out of it because ASCII art of the
     * name only lines up in a font whose letters are all the same width.
     *
     * @return the greeting, spanning two lines.
     */
    public static String getGreeting() {
        return "BILLY HERE! Keeper of lists, guardian of things you would otherwise forget.\n"
                + "Todos, deadlines, events. Say 'list' and I'll spill it all. 'help' if you're lost.";
    }

    /**
     * Returns Billy's sign-off.
     *
     * @return the farewell, on one line.
     */
    public static String getFarewell() {
        return "Off you go! I'll be right here, guarding the list. Vigilantly.";
    }

    /** Prints the startup banner and welcomes the user. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(getGreeting());
        System.out.println(DIVIDER);
    }

    /** Prints Billy's farewell before the program exits. */
    public void showGoodbye() {
        System.out.println(getFarewell());
        System.out.println(DIVIDER);
    }

    /**
     * Starts collecting what Billy says rather than printing it.
     *
     * <p>Called before each command the window carries out, so that the answer
     * can be handed back as text instead of disappearing into a console.
     */
    public void startCollecting() {
        transcript = new StringBuilder();
        isErrorCollected = false;
    }

    /**
     * Returns everything said since collecting started, and goes back to
     * printing.
     *
     * @return what Billy said, with no divider lines and no trailing blank line.
     */
    public String stopCollecting() {
        assert transcript != null : "stopCollecting was called without a matching startCollecting.";
        String collected = transcript.toString().strip();
        transcript = null;
        return collected;
    }

    /**
     * Prints one message as a block between divider lines.
     *
     * <p>Takes the message a line at a time, since that is what Billy's answers
     * are: a sentence, then the task it concerns, then how many are left. The
     * callers used to glue those together with {@code \n} themselves, which put
     * the shape of the message in every command rather than here.
     *
     * @param lines the message, one line per argument
     */
    public void show(String... lines) {
        // Varargs makes show() with no arguments compile, and it would print an
        // empty block between two dividers rather than failing outright.
        assert lines.length > 0 : "A message must have at least one line.";
        String message = String.join("\n", lines);
        if (transcript != null) {
            // No dividers: a dialog box already shows where the message ends.
            transcript.append(message).append("\n");
            return;
        }
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /**
     * Prints something that went wrong.
     *
     * <p>Errors are printed exactly like any other message, since a console has
     * no color to spend on them. What this adds over {@link #show(String...)}
     * is the record that the message was an error, which is what lets the
     * window show it differently.
     *
     * @param lines what went wrong, phrased for the person typing the command,
     *              one line per argument
     */
    public void showError(String... lines) {
        isErrorCollected = true;
        show(lines);
    }

    /**
     * Returns whether anything said since collecting started was an error.
     *
     * <p>Meaningful only between {@link #startCollecting()} and
     * {@link #stopCollecting()}; outside those it reports on whichever run of
     * collecting happened last.
     *
     * @return whether the collected message reports a failure.
     */
    public boolean isErrorCollected() {
        return isErrorCollected;
    }

    /**
     * Returns whether there is another command to read.
     *
     * <p>False once the input runs out, which is what happens when the user
     * presses Ctrl+D or a file of commands ends.
     *
     * @return whether the user has typed anything more
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command.
     *
     * <p>Surrounding spaces are removed here, so no caller has to remember to do
     * it before making sense of the line.
     *
     * @return the line the user typed, trimmed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Stops reading input. Called once the conversation is over. */
    public void close() {
        scanner.close();
    }
}
