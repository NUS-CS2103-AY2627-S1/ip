package shannon;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import shannon.exception.ShannonException;
import shannon.task.Task;

/**
 * The words the chatbot says, and the terminal it says them in.
 * <p>
 * The class has two jobs, kept deliberately apart:
 * <ul>
 *   <li>the {@code get...Message} methods <em>build</em> a reply and hand it back as a string;
 *       they touch neither the screen nor the keyboard.</li>
 *   <li>{@link #readCommand()}, {@link #showLine()}, {@link #showLogo()} and
 *       {@link #showMessage(String)} are the only places that talk to the terminal.</li>
 * </ul>
 * That split is what lets the same chatbot run in two skins. The terminal version reads a line
 * and prints the message it gets back; the JavaFX window asks for exactly the same message and
 * puts it in a dialog bubble instead. Neither the tasks nor the commands know the difference.
 */
public class Ui {

    /** The line drawn above and below each reply, to separate one exchange from the next. */
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________";

    /** Reads the user's typing. Kept as a field so the one scanner lasts the whole session. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Reads the next line the user types at the terminal.
     *
     * @return the line, exactly as typed.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns whether there is another line to read, so that {@link #readCommand()} can be called
     * safely.
     * <p>
     * It is {@code false} once the input has ended, which happens when the user presses Ctrl-D
     * (Ctrl-Z then Enter on Windows) or when a file piped in as input runs out. Reading past that
     * point would throw an exception and crash the chatbot.
     *
     * @return {@code true} if a line can still be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Prints the separator line to the terminal. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Prints a message to the terminal.
     * Blank messages are skipped, so a reply that has nothing to say leaves no empty line.
     *
     * @param message the text to show, normally one built by a {@code get...Message} method.
     */
    public void showMessage(String message) {
        if (!message.isEmpty()) {
            System.out.println(message);
        }
    }

    /**
     * Prints the ASCII-art logo to the terminal.
     * <p>
     * Terminal-only, and so a {@code show...} method rather than a {@code get...Message} one:
     * the art relies on every character being the same width and on lines nearly sixty
     * characters long, neither of which a narrow chat bubble can promise.
     */
    public void showLogo() {
        String logo = "   oo_    \\\\  //       \\\\\\  ///\\\\\\  ///   .-.   \\\\\\  ///\n"
                + "  /  _)-< (o)(o)   /)  ((O)(O))((O)(O)) c(O_O)c ((O)(O))\n"
                + "  \\__ `.  ||  || (o)(O) | \\ ||  | \\ || ,'.---.`, | \\ ||\n"
                + "     `. | |(__)|  //\\\\  ||\\\\||  ||\\\\||/ /|_|_|\\ \\||\\\\||\n"
                + "     _| | /.--.\\ |(__)| || \\ |  || \\ || \\_____/ ||| \\ |\n"
                + "  ,-'   |-'    `-/,-. | ||  ||  ||  ||'. `---' .`||  ||\n"
                + " (_..--'        -'   ''(_/  \\_)(_/  \\_) `-...-' (_/  \\_)";
        System.out.println(logo);
    }

    /**
     * Returns the greeting shown once at start-up.
     *
     * @return the welcome text.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Shannon!\nWhat can I do for you?";
    }

    /**
     * Returns the parting message.
     *
     * @return the goodbye text.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns the explanation of something that went wrong.
     * A method of its own, even though it only passes the text through, so that the wording of
     * every error can later be decorated in one place.
     *
     * @param message the text to show, normally a {@link ShannonException}'s message.
     * @return the error text.
     */
    public String getErrorMessage(String message) {
        return message;
    }

    /**
     * Returns the confirmation that a task was added, including how many tasks there are now.
     *
     * @param task      the task that was just added.
     * @param taskCount how many tasks are in the list now.
     * @return the confirmation text.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task + "\n" + getTaskCountMessage(taskCount);
    }

    /**
     * Returns the confirmation that one or more tasks were deleted, including how many are left.
     *
     * @param deletedTasks the tasks that were just removed, in the order the user named them.
     * @param taskCount    how many tasks are left in the list.
     * @return the confirmation text.
     */
    public String getTaskDeletedMessage(List<Task> deletedTasks, int taskCount) {
        String heading = "Noted. I've removed "
                + pluralize(deletedTasks.size(), "this task:", "these tasks:");
        return heading + "\n" + getIndentedTasks(deletedTasks)
                + "\n" + getTaskCountMessage(taskCount);
    }

    /**
     * Returns the confirmation that one or more tasks had their done flag changed.
     *
     * @param markedTasks the tasks that were just changed, in the order the user named them.
     * @param isDone      {@code true} if they were marked done, {@code false} if unmarked.
     * @return the confirmation text.
     */
    public String getTaskMarkedMessage(List<Task> markedTasks, boolean isDone) {
        String wording = pluralize(markedTasks.size(), "this task", "these tasks");
        String heading = isDone
                ? "Nice! I've marked " + wording + " as done:"
                : "OK, I've marked " + wording + " as not done yet:";
        return heading + "\n" + getIndentedTasks(markedTasks);
    }

    /**
     * Returns the tasks in the order they were added, numbered from 1.
     *
     * @param tasks the tasks to list; a message is returned instead if there are none.
     * @return the numbered list, or a note that the list is empty.
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your list is empty!";
        }
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns the tasks that matched a {@code find}, or a note that none did.
     * <p>
     * The numbers shown here count the matches, not the positions in the whole list, so
     * {@code delete 2} after a find does not delete the second task shown. Reusing
     * {@link #getTaskListMessage} keeps one numbering style; only the empty case needs its own
     * wording, since "Your list is empty!" would be untrue when the list has tasks that simply
     * did not match.
     *
     * @param tasks the tasks that matched the keyword.
     * @return the numbered matches, or a note that there were none.
     */
    public String getFoundTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found!";
        }
        return "Here are the matching tasks in your list:\n" + getTaskListMessage(tasks);
    }

    /**
     * Returns a report of how many tasks were restored from the save file, or an empty string
     * when there were none, so a first run is not cluttered with "I've loaded 0 tasks".
     *
     * @param taskCount how many tasks were loaded.
     * @return the report, or an empty string.
     */
    public String getLoadedMessage(int taskCount) {
        if (taskCount == 0) {
            return "";
        }
        return "I've loaded " + taskCount + pluralize(taskCount, " task", " tasks")
                + " from your last session.";
    }

    /**
     * Returns a warning that part of the save file could not be read, so the user is not left
     * wondering where those tasks went, or an empty string when no lines were skipped.
     *
     * @param skippedCount how many lines were skipped.
     * @param filePath     the save file, named so the user can go and repair it.
     * @return the warning, or an empty string.
     */
    public String getSkippedLinesMessage(int skippedCount, String filePath) {
        if (skippedCount == 0) {
            return "";
        }
        return "I couldn't understand " + skippedCount
                + pluralize(skippedCount, " line", " lines") + " in " + filePath
                + ", so I've left " + pluralize(skippedCount, "it", "them") + " out.";
    }

    /**
     * Returns how a save file that could not be fully loaded has been kept from being
     * overwritten, or an empty string when the whole file was loaded and nothing needed keeping.
     *
     * @param filePath       the save file.
     * @param backupFilePath where the file was copied, or an empty string if no copy was made.
     * @param isSaveBlocked  whether saving has been switched off because the copy failed.
     * @return the explanation, or an empty string.
     */
    public String getBackupMessage(String filePath, String backupFilePath, boolean isSaveBlocked) {
        if (isSaveBlocked) {
            return "I couldn't make a backup copy of " + filePath + " either, so I won't save any"
                    + " changes over it. Please fix the file, then restart me to save again.";
        }
        if (backupFilePath.isEmpty()) {
            return "";
        }
        return "Nothing is lost: I've kept a copy of the original file at " + backupFilePath + ".";
    }

    /**
     * Returns how many tasks are in the list, for use after one has been added or deleted.
     *
     * @param taskCount how many tasks are in the list now.
     * @return the count, worded as a sentence.
     */
    private String getTaskCountMessage(int taskCount) {
        return "Now you have " + taskCount + pluralize(taskCount, " task", " tasks")
                + " in the list.";
    }

    /**
     * Returns the given tasks one to a line, each indented, as the confirmations show them.
     *
     * @param tasks the tasks to lay out; the confirmations never call this with none.
     * @return the indented lines, with no newline at the end.
     */
    private static String getIndentedTasks(List<Task> tasks) {
        assert !tasks.isEmpty() : "A confirmation is only shown after at least one task changed";
        StringBuilder lines = new StringBuilder();
        for (Task task : tasks) {
            if (!lines.isEmpty()) {
                lines.append("\n");
            }
            lines.append("  ").append(task);
        }
        return lines.toString();
    }

    /**
     * Picks the singular or plural wording for a count.
     * A tiny helper, but it keeps the {@code count == 1 ? ... : ...} test out of five messages.
     *
     * @param count      the number the wording has to agree with.
     * @param singular   the wording to use when {@code count} is 1.
     * @param pluralForm the wording to use otherwise.
     * @return whichever of the two fits the count.
     */
    private static String pluralize(int count, String singular, String pluralForm) {
        assert count >= 0 : "A count cannot be negative";
        return count == 1 ? singular : pluralForm;
    }
}
