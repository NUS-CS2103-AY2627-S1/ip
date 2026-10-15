package thomas.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import thomas.task.Task;
import thomas.task.TaskList;

/**
 * Deals with everything the user sees and types.
 * <p>
 * All console reading and writing lives here, so the rest of the program never
 * calls {@link System#out} or builds a {@link Scanner} of its own. That keeps
 * the look of the chatbot -- the indentation, the dividers, the wording of each
 * message -- in one file, and means the command handling can be read without
 * stepping over formatting code.
 * <p>
 * Wording a message and showing it are separate here. The {@code get...Message}
 * methods only word one, and a command returns what it gets rather than
 * printing it, because the same words have to reach a console and a dialog
 * bubble and only one of those is a console. The {@code show...} methods print,
 * and are what the console session uses to put a worded message on screen.
 * <p>
 * A {@code Ui} owns the {@link Scanner} over standard input, so the object is
 * created once and reused for the whole session rather than per command; a new
 * {@code Scanner} each time would buffer ahead and lose input.
 */
public class Ui {
    /** Indentation applied to every line of chatbot text. */
    private static final String INDENT = "     ";

    /**
     * Horizontal rule printed above and below each block of chatbot output.
     * Indented one space less than the text it wraps, as the sample output shows.
     */
    private static final String DIVIDER =
            "    ____________________________________________________________";

    /**
     * The two spoken lines of the greeting.
     * <p>
     * Named constants because both front ends open with them and neither owns
     * them: the console prints them under the banner, the window shows them in
     * the first dialog box, and they must not drift apart.
     */
    private static final String GREETING_NAME = "Peep peep! Thomas the Tank Engine, reporting for duty!";
    private static final String GREETING_OFFER = "What shall we haul today?";

    /** Reads the user's command lines from standard input. */
    private final Scanner userInput = new Scanner(System.in);

    /**
     * Prints lines as one chatbot block: indented, wrapped in dividers.
     * <p>
     * Public because it is the general-purpose way to say something; the named
     * methods below are the messages that are worded the same way every time.
     *
     * @param lines The text lines to display, without indentation or newlines.
     */
    public void showBlock(String... lines) {
        System.out.print(DIVIDER + "\n");
        for (String line : lines) {
            System.out.print(INDENT + line + "\n");
        }
        System.out.print(DIVIDER + "\n");
    }

    /**
     * Prints a message that was worded elsewhere, as one chatbot block.
     * <p>
     * The counterpart to the {@code get...Message} methods: they say what, this
     * says where. A blank message prints nothing at all, since a command such
     * as {@code bye} has nothing to say and dividers around nothing would look
     * like a fault.
     *
     * @param message The message to print, its lines separated by newlines.
     */
    public void showMessage(String message) {
        if (message.isBlank()) {
            return;
        }
        showBlock(message.split("\n", -1));
    }

    /**
     * Joins lines into one message, one per line.
     * <p>
     * A message carries only its own lines: the indentation and dividers are
     * console furniture, added by {@link #showBlock} at the moment of printing,
     * and a dialog bubble wants neither.
     *
     * @param lines The lines to join.
     * @return The lines separated by newlines.
     */
    private static String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Words how many wagons the train is pulling.
     * <p>
     * Every message that changes the length of the train ends with this line,
     * so it is worded once: the count is the same fact whichever command
     * changed it, and three copies of the sentence would drift apart.
     *
     * @param taskCount How many tasks are stored.
     * @return The line reporting the count.
     */
    private static String getWagonCountLine(int taskCount) {
        return "That's " + taskCount + " wagon(s) behind me now.";
    }

    /**
     * Reports that there is another command to read.
     *
     * @return True if the user has typed another line and the input has not ended.
     */
    public boolean hasNextCommand() {
        return userInput.hasNextLine();
    }

    /**
     * Reads one command line.
     * <p>
     * {@code nextLine()}, not {@code next()}: a command such as
     * {@code todo read book} is one line, and {@code next()} would hand back one
     * word at a time.
     *
     * @return The line the user typed, exactly as typed.
     */
    public String readCommand() {
        return userInput.nextLine();
    }

    /**
     * Words the greeting the chatbot opens with.
     * <p>
     * The spoken lines only. The banner {@link #showWelcome()} prints above
     * them is console furniture, exactly as the dividers and the indentation
     * are: it is ASCII art, and a dialog box draws its text in a proportional
     * font that would leave the train in pieces.
     *
     * @return The greeting to show the user.
     */
    public String getWelcomeMessage() {
        return joinLines(GREETING_NAME, GREETING_OFFER);
    }

    /** Prints the banner and greeting shown when the chatbot starts. */
    public void showWelcome() {
        // The first five lines are ASCII art spelling "Thomas". Every backslash
        // is written as \\ because a lone \ starts an escape sequence in Java.
        // Each line is passed separately, since showBlock indents one at a time.
        showBlock("  ________                              ",
                " /_  __/ /_  ____  ____ ___  ____ ______",
                "  / / / __ \\/ __ \\/ __ `__ \\/ __ `/ ___/",
                " / / / / / / /_/ / / / / / / /_/ (__  ) ",
                "/_/ /_/ /_/\\____/_/ /_/ /_/\\__,_/____/  ",
                GREETING_NAME,
                GREETING_OFFER);
    }

    /**
     * Words the farewell shown when the chatbot stops.
     *
     * @return The farewell to show the user.
     */
    public String getGoodbyeMessage() {
        return "Off to the sheds! Peep peep, see you down the line!";
    }

    /**
     * Prints the farewell shown when the chatbot stops.
     * <p>
     * For the console session that ends because the input ran out rather than
     * because {@code bye} was typed; a typed {@code bye} gets the same words
     * from {@link thomas.command.ExitCommand} instead.
     */
    public void showGoodbye() {
        showBlock(getGoodbyeMessage());
    }

    /**
     * Reports a problem with what the user asked for.
     * <p>
     * Split into lines exactly as {@link #showMessage} does, because an
     * explanation can run to more than one: the refusal of a duplicate task
     * quotes the existing task on a line of its own. Handed to
     * {@link #showBlock} whole, that second line would be printed without
     * the indent every other line has.
     *
     * @param message The explanation to show, already worded for the user.
     */
    public void showError(String message) {
        showBlock(message.split("\n", -1));
    }

    /**
     * Words the warning that the saved tasks could not be read at all.
     *
     * @param message The reason the file could not be read.
     * @return The warning to show the user.
     */
    public String getLoadingErrorMessage(String message) {
        return joinLines("Cinders and ashes! I couldn't read your saved tasks: " + message,
                "Setting off with an empty train.");
    }

    /**
     * Reports that the saved tasks could not be read at all.
     *
     * @param message The reason the file could not be read.
     */
    public void showLoadingError(String message) {
        showMessage(getLoadingErrorMessage(message));
    }

    /**
     * Words the warning about one save file line that could not be understood.
     *
     * @param message What was wrong with the line.
     * @return The warning to show the user.
     */
    public String getSkippedLineMessage(String message) {
        return "Cinders and ashes! I left a saved line in the yard, I couldn't read it: " + message;
    }

    /**
     * Reports one save file line that could not be understood, having skipped it.
     *
     * @param message What was wrong with the line.
     */
    public void showSkippedLine(String message) {
        showMessage(getSkippedLineMessage(message));
    }

    /**
     * Words the warning that the task list could not be written to disk.
     *
     * @param message The reason the file could not be written.
     * @return The warning to show the user.
     */
    public String getSavingErrorMessage(String message) {
        return "Cinders and ashes! I couldn't save your tasks: " + message;
    }

    /**
     * Confirms that a task was added and reports the new size of the list.
     * <p>
     * The three add commands share this acknowledgement, so it lives in one
     * place; {@code task} is a {@link Task}, and polymorphism picks the right
     * {@code toString()} for whichever subclass was actually added.
     *
     * @param task The task just stored.
     * @param taskCount How many tasks are now stored.
     * @return The confirmation to show the user.
     */
    public String getAddedMessage(Task task, int taskCount) {
        return joinLines("Coupled up! This wagon is on the train now:",
                "   " + task,
                getWagonCountLine(taskCount));
    }

    /**
     * Confirms that a task was removed and reports the new size of the list.
     *
     * @param task The task just removed.
     * @param taskCount How many tasks are left.
     * @return The confirmation to show the user.
     */
    public String getRemovedMessage(Task task, int taskCount) {
        return joinLines("Uncoupled! I've left this wagon in the siding:",
                "   " + task,
                getWagonCountLine(taskCount));
    }

    /**
     * Confirms that a task is now done.
     *
     * @param task The task just marked.
     * @return The confirmation to show the user.
     */
    public String getMarkedMessage(Task task) {
        return joinLines("Delivered, right on time! This wagon is done:", "   " + task);
    }

    /**
     * Confirms that a task is no longer done.
     *
     * @param task The task just unmarked.
     * @return The confirmation to show the user.
     */
    public String getUnmarkedMessage(Task task) {
        return joinLines("Back on the train it goes! This wagon is not done yet:", "   " + task);
    }

    /**
     * Confirms that the most recent change has been taken back.
     * <p>
     * The line the user typed is quoted back rather than the change described,
     * because undo can be asked for many times in a row and the user has to be
     * able to tell how far back they have got. Quoting is also what lets one
     * wording serve every command: describing the change would need one sentence
     * for a task put back and another for a tick restored.
     * <p>
     * The count is reported even when undoing a {@code mark}, which does not
     * change it, so that the message reads the same way whatever was undone.
     *
     * @param typedLine The line that had asked for the change now reversed,
     *                  exactly as it was typed.
     * @param taskCount How many tasks are stored now that it has been reversed.
     * @return The confirmation to show the user.
     */
    public String getUndoneMessage(String typedLine, int taskCount) {
        return joinLines("Reversing! I've backed out of '" + typedLine + "'.",
                getWagonCountLine(taskCount));
    }

    /**
     * Words a header and the tasks at the given positions, numbered.
     * <p>
     * Every listing the chatbot shows takes this shape, and each numbers its
     * tasks by where they sit in the whole list rather than by where they sit
     * among those shown: a number on screen is the number {@code mark} and
     * {@code delete} take. Numbering what is shown 1, 2, 3 would read more
     * tidily and send the user to the wrong task, so the conversion from
     * position to displayed number happens here alone.
     * <p>
     * The header is included whether or not any position is given, so a
     * listing with no matches says so rather than saying nothing at all.
     *
     * @param tasks The whole task list.
     * @param header The line to open with.
     * @param positions The positions to show, counting from 0, in list order.
     * @return The numbered listing to show the user.
     */
    private static String getNumberedTasksMessage(TaskList tasks, String header,
            List<Integer> positions) {
        String[] entries = Stream.concat(
                        Stream.of(header),
                        positions.stream()
                                .map(position -> (position + 1) + ". " + tasks.get(position)))
                .toArray(String[]::new);
        return joinLines(entries);
    }

    /**
     * Words the whole task list, numbered from 1.
     *
     * @param tasks The tasks to show, in list order.
     * @return The numbered list to show the user.
     */
    public String getTaskListMessage(TaskList tasks) {
        // Every task is shown, so the positions are simply all of them. Built
        // as a list rather than counted out here, so that this listing is
        // numbered by the same code as the two filtered ones.
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            positions.add(i);
        }
        return getNumberedTasksMessage(tasks, "Here is every wagon on my train:", positions);
    }

    /**
     * Words the tasks whose description contains a keyword.
     * <p>
     * Which tasks match is {@link TaskList}'s question to answer, and how a
     * listing is numbered is {@link #getNumberedTasksMessage}'s, so all this
     * one settles is the wording of the header.
     *
     * @param tasks The whole task list.
     * @param keyword The text that was searched for.
     * @return The matches to show the user.
     */
    public String getMatchingTasksMessage(TaskList tasks, String keyword) {
        return getNumberedTasksMessage(tasks, "I searched the yard and found these wagons:",
                tasks.positionsMatching(keyword));
    }

    /**
     * Words the tasks that fall on one day.
     * <p>
     * As with {@link #getMatchingTasksMessage}, which tasks fall on the day is
     * {@link TaskList}'s question and the numbering is
     * {@link #getNumberedTasksMessage}'s, leaving only the header to settle
     * here.
     *
     * @param tasks The whole task list.
     * @param day The day to report on.
     * @return The matches to show the user.
     */
    public String getTasksOnDayMessage(TaskList tasks, LocalDate day) {
        return getNumberedTasksMessage(tasks,
                "Here is my timetable for " + day.format(Task.DATE_DISPLAY_DAY) + ":",
                tasks.positionsOn(day));
    }
}
