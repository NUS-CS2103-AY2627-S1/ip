package alice;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all interactions with the user.
 *
 * <p>Every {@code show*} method here prints its message to the console (for
 * the text UI) <em>and</em> returns that same message as a String. This lets
 * the JavaFX GUI (see {@link MainWindow}) display the exact same wording in
 * a dialog box, without duplicating the message-formatting logic in two
 * places.
 *
 * <p>Alice is deliberately a reluctant assistant: she grumbles about being
 * asked, and denies caring how it turns out. That attitude is confined to
 * the wording around the information and never the information itself -
 * task numbers, counts, dates and the reason a command failed are all
 * stated as plainly as they ever were, because a chatbot whose personality
 * obscures what went wrong is simply a worse chatbot.
 */
public class Ui {
    // The faces are written as Unicode escapes so this file stays pure
    // ASCII. It therefore compiles to the same bytes whatever encoding a
    // machine or CI runner happens to default to, without the build having
    // to pin a source encoding. Naming them also keeps the messages below
    // readable, and lets one face be reused by several of them.

    /** Sceptical side-eye. */
    private static final String FACE_SIDE_EYE = "(\uFFE2_\uFFE2)";
    /** Indignant, chin-up huff. */
    private static final String FACE_HUFFY = "(\uFFE3^\uFFE3)";
    /** Quietly pleased with herself. */
    private static final String FACE_SMUG = "(\uFFE3\u03C9\uFFE3)";
    /** Caught mildly off guard. */
    private static final String FACE_SURPRISED = "(\u30FB_\u30FB)";
    /** Flat and unimpressed. */
    private static final String FACE_FLAT = "(\uFFE3_\uFFE3)";
    /** Long-suffering sigh. */
    private static final String FACE_EXASPERATED = "(\uFF1B\u4E00_\u4E00)";
    /** Thrown by something unexpected. */
    private static final String FACE_CONFUSED = "(\u30FB_\u30FB;)";

    /**
     * Displays the welcome message shown when the program starts.
     *
     * @return the welcome message.
     */
    public String showWelcome() {
        return print("Oh. It's you. " + FACE_SIDE_EYE,
                "Well? What do you want me to keep track of?");
    }

    /**
     * Displays the goodbye message shown when the user exits the program.
     *
     * @return the goodbye message.
     */
    public String showGoodbye() {
        return print("Finally. Go on then. " + FACE_HUFFY,
                "...Don't miss your deadlines. Not that I'd care.");
    }

    /**
     * Displays an error message to the user.
     *
     * <p>The cause is passed through word for word; only the framing around
     * it is Alice's, so an error stays exactly as easy to act on as it was
     * before she had any personality.
     *
     * @param message the error message to display.
     * @return the formatted error message.
     */
    public String showError(String message) {
        // Some causes run to several lines (the list of valid commands, for
        // one). The face belongs beside the complaint on the first line,
        // not stranded at the end of the explanation that follows it.
        int firstBreak = message.indexOf('\n');
        if (firstBreak < 0) {
            return print("Huh? " + message + " " + FACE_CONFUSED);
        }
        return print("Huh? " + message.substring(0, firstBreak) + " " + FACE_CONFUSED,
                message.substring(firstBreak + 1));
    }

    /**
     * Displays a confirmation message after a task has been added.
     *
     * @param task the task that was added.
     * @param taskCount the total number of tasks after adding.
     * @return the formatted confirmation message.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return print("Fine, I wrote it down. " + FACE_SMUG,
                "  " + task,
                "That's " + taskCount + " now. Hope you're planning to actually do them.");
    }

    /**
     * Displays the full list of tasks currently in the task list.
     *
     * @param tasks the task list to display.
     * @return the formatted task list.
     */
    public String showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            return print("Nothing here. Must be nice, having no responsibilities. " + FACE_SMUG);
        }
        String[] lines = new String[tasks.size() + 1];
        lines[0] = "Here. Don't make me say it twice. " + FACE_HUFFY;
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        return print(lines);
    }

    /**
     * Displays a confirmation message after a task has been marked as done.
     *
     * @param task the task that was marked.
     * @return the formatted confirmation message.
     */
    public String showMarked(Task task) {
        return print("Oh? You actually finished something. " + FACE_SURPRISED, "  " + task);
    }

    /**
     * Displays a confirmation message after a task has been marked as not done.
     *
     * @param task the task that was unmarked.
     * @return the formatted confirmation message.
     */
    public String showUnmarked(Task task) {
        return print("Changed your mind already? Typical. " + FACE_SIDE_EYE, "  " + task);
    }

    /**
     * Displays a confirmation message after a task has been deleted.
     *
     * @param task the task that was removed.
     * @param remainingCount the number of tasks remaining after deletion.
     * @return the formatted confirmation message.
     */
    public String showDeleted(Task task, int remainingCount) {
        return print("Gone. Not that it matters to me. " + FACE_FLAT,
                "  " + task,
                remainingCount + " left.");
    }

    /**
     * Displays a confirmation message after a deadline has been snoozed
     * (pushed back).
     *
     * @param deadline the deadline that was snoozed, with its new date already applied.
     * @return the formatted confirmation message.
     */
    public String showSnoozed(Deadline deadline) {
        return print("Putting it off again, are we? ...Fine. " + FACE_EXASPERATED, "  " + deadline);
    }

    /**
     * Displays problems encountered while loading the saved task list, so
     * that a corrupted or unreadable data file is visible to the user
     * instead of being discovered only when tasks turn out to be missing.
     *
     * @param warnings the problems found while loading; must not be empty.
     * @return the formatted warning message.
     */
    public String showLoadWarnings(List<String> warnings) {
        assert warnings != null && !warnings.isEmpty() : "there must be at least one warning to show";
        String[] lines = new String[warnings.size() + 1];
        lines[0] = "Your save file is a mess. I did what I could. " + FACE_EXASPERATED;
        for (int i = 0; i < warnings.size(); i++) {
            lines[i + 1] = "  " + warnings.get(i);
        }
        return print(lines);
    }

    /**
     * Displays the list of tasks matching a search.
     *
     * @param matches the list of matching tasks to display.
     * @return the formatted list of matching tasks.
     */
    public String showFoundTasks(ArrayList<Task> matches) {
        if (matches.isEmpty()) {
            return print("Nothing matched. Try remembering what you actually wrote. " + FACE_FLAT);
        }
        String[] lines = new String[matches.size() + 1];
        lines[0] = "These matched. You're welcome, by the way. " + FACE_SMUG;
        for (int i = 0; i < matches.size(); i++) {
            lines[i + 1] = (i + 1) + "." + matches.get(i);
        }
        return print(lines);
    }

    /**
     * Prints one or more lines to the console, joined with newlines, and
     * returns the joined text so callers can send the exact same content to
     * the GUI. Passing a single line behaves exactly as it did before this
     * method took varargs, since Java wraps a lone argument into a
     * one-element array automatically.
     *
     * @param lines the line(s) making up the message.
     * @return the lines joined into a single message.
     */
    private String print(String... lines) {
        assert lines != null && lines.length > 0 : "at least one line must be given to print";
        String message = String.join("\n", lines);
        System.out.println(message);
        return message;
    }
}
