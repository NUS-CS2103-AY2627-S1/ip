package alfred;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import alfred.task.Priority;
import alfred.task.Task;
import alfred.task.TaskList;

/**
 * Handles everything the user sees and types. Between {@link #startCapturing()}
 * and {@link #stopCapturing()}, replies are collected for the window instead
 * of printed.
 */
public class Ui {
    private static final String DIVIDER = "    " + "_".repeat(60);

    private static final String INDENT = "     ";

    /** Extra indent for a line that belongs under the one above it, such as a marked task. */
    private static final String SUB_INDENT = "  ";

    private static final String NAME = "Alfred Pennyworth";

    private static final String GREETING_INTRODUCTION = "Good day, sir. " + NAME + ", at your disposal.";

    private static final String GREETING_OFFER = "What may I do for you?";

    private static final String BANNER =
            "            _     _      _____  ____   _____  ____\n"
            + "           / \\   | |    |  ___||  _ \\ | ____||  _ \\\n"
            + "          / _ \\  | |    | |_   | |_) ||  _|  | | | |\n"
            + "         / ___ \\ | |___ |  _|  |  _ < | |___ | |_| |\n"
            + "        /_/   \\_\\|_____||_|    |_| \\_\\|_____||____/\n"
            + "                    P E N N Y W O R T H\n"
            + "\n"
            + "      Butler to the Wayne family  --  At your service";

    /** Never closed, because that would close standard input. */
    private final Scanner scanner = new Scanner(System.in);

    /** The lines said since capturing began, or null while replies are being printed. */
    private List<String> captured;

    /** Opens the console interface a run talks through. */
    public Ui() {
    }

    /**
     * Says whether there is another command to read.
     *
     * @return true if a line is waiting, false at the end of the input.
     */
    boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line, trimmed.
     *
     * @return the line the user typed, without leading or trailing spaces.
     */
    String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Starts collecting what is said instead of printing it. */
    void startCapturing() {
        assert captured == null : "already capturing";

        captured = new ArrayList<>();
    }

    /**
     * Returns everything said since {@link #startCapturing()}, and goes back to
     * printing.
     *
     * @return what was said, or an empty string if nothing was.
     */
    String stopCapturing() {
        assert captured != null : "stopCapturing without startCapturing";

        String said = String.join("\n", captured);
        captured = null;
        return said;
    }

    /** Prints the banner and the welcome message. A captured greeting leaves out the banner. */
    void showWelcome() {
        if (captured != null) {
            reply(GREETING_INTRODUCTION, GREETING_OFFER);
            return;
        }

        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(INDENT + GREETING_INTRODUCTION);
        System.out.println(INDENT + GREETING_OFFER);
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Prints the parting message. */
    public void showFarewell() {
        reply("Very good, sir. I shall be here when you need me.");
    }

    /**
     * Reports that tasks were restored from the save file.
     *
     * @param taskCount how many tasks came back.
     */
    void showLoaded(int taskCount) {
        reply("I've brought back " + describeCount(taskCount, "task") + " from last time, sir.");
    }

    /**
     * Warns that part of the save file could not be understood, and says
     * whether a copy of it was kept.
     *
     * @param skippedLines how many lines were left out.
     * @param backup where a copy of the file was kept, or null if none could
     *        be.
     */
    void showSkippedLines(int skippedLines, Path backup) {
        String warning = "I could not make sense of " + describeCount(skippedLines, "line")
                + " in your saved tasks, sir.";
        if (backup == null) {
            reply(warning, "I have left them out, and they will be gone once the list changes,",
                    "as I could not keep a copy of the file.");
            return;
        }
        reply(warning, "I have left them out, but kept a copy of the file beside it as "
                + backup.getFileName() + ".");
    }

    /**
     * Reports something the user can put right, such as a mistyped command.
     *
     * @param message the refusal, written for the person who typed the command.
     */
    void showError(String message) {
        reply(message);
    }

    /**
     * Reports a fault in the program itself, rather than in what was typed.
     *
     * @param e the fault that escaped the command that caused it.
     */
    void showInternalError(RuntimeException e) {
        reply("Something went wrong on my end, sir: " + e,
                "Your tasks are unharmed. Do carry on.");
    }

    /**
     * Confirms that a task has been stored.
     *
     * @param task the task that was just added.
     * @param taskCount how many tasks are stored now that it has been added.
     */
    public void showAdded(Task task, int taskCount) {
        assert taskCount > 0 : "the list holds at least the task just added";

        reply("Very good, sir. I've added this task:",
                SUB_INDENT + task,
                "That makes " + describeCount(taskCount, "task") + " on your list.");
    }

    /**
     * Confirms that a task has been dropped from the list.
     *
     * @param task the task that was removed.
     * @param taskCount how many tasks are left.
     */
    public void showRemoved(Task task, int taskCount) {
        reply("As you wish, sir. I've removed this task:",
                SUB_INDENT + task,
                "That leaves " + describeCount(taskCount, "task") + " on your list.");
    }

    /**
     * Confirms that a task is now done.
     *
     * @param task the task that was marked.
     */
    public void showMarked(Task task) {
        reply("Splendid, sir. I've marked this task as done:", SUB_INDENT + task);
    }

    /**
     * Confirms that a task is no longer done.
     *
     * @param task the task that was unmarked.
     */
    public void showUnmarked(Task task) {
        reply("Very well, sir. I've marked this task as not done yet:", SUB_INDENT + task);
    }

    /**
     * Confirms that a task's priority has changed.
     *
     * @param task the task whose priority was set or cleared.
     */
    public void showPrioritySet(Task task) {
        String message = task.getPriority() == Priority.NONE
                ? "Very good. I've taken the priority off this task:"
                : "Very good. I've set this task's priority:";
        reply(message, SUB_INDENT + task);
    }

    /**
     * Prints the stored tasks as a numbered list, numbered from 1.
     *
     * @param tasks the tasks to show, in the order they are stored.
     */
    public void showList(TaskList tasks) {
        replyNumbered("Here are the tasks on your list, sir:", numberTasks(tasks, task -> true));
    }

    /**
     * Prints the tasks that fall on one day, each numbered by its place in
     * the whole list so that the number works with {@code mark}.
     *
     * @param tasks every stored task, in the order they are stored.
     * @param date the day being asked about.
     */
    public void showTasksOn(TaskList tasks, LocalDate date) {
        String when = Dates.format(date);
        List<String> lines = numberTasks(tasks, task -> task.occursOn(date));
        if (lines.isEmpty()) {
            reply("You have nothing on " + when + ", sir.");
            return;
        }
        replyNumbered("Here is what you have on " + when + ":", lines);
    }

    /**
     * Prints the tasks whose description contains a keyword, each numbered by
     * its place in the whole list.
     *
     * @param tasks every stored task, in the order they are stored.
     * @param keyword the text being searched for.
     */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        List<String> lines = numberTasks(tasks, task -> task.matches(keyword));
        if (lines.isEmpty()) {
            reply("I found no matching tasks, sir.");
            return;
        }
        replyNumbered("Here are the matching tasks on your list, sir:", lines);
    }

    /**
     * Returns the display lines for the tasks a test accepts, each numbered by
     * its place in the whole list.
     *
     * @param tasks every stored task, in the order they are stored.
     * @param isShown the test a task has to pass to be shown.
     * @return the numbered lines, in list order, and empty if nothing matched.
     */
    private static List<String> numberTasks(TaskList tasks, Predicate<Task> isShown) {
        return IntStream.range(0, tasks.size())
                .filter(i -> isShown.test(tasks.get(i)))
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .toList();
    }

    /**
     * Prints a heading and the lines belonging under it as one block.
     *
     * @param heading the line that introduces the ones below it.
     * @param lines the lines to show under it, in the order they are to appear.
     */
    private void replyNumbered(String heading, List<String> lines) {
        reply(Stream.concat(Stream.of(heading), lines.stream()).toArray(String[]::new));
    }

    /**
     * Prints one or more lines inside a divider block, each indented, followed
     * by a blank line, or collects them while capturing.
     *
     * @param lines the lines to display, in order.
     */
    private void reply(String... lines) {
        assert lines.length > 0 : "a reply needs at least one line";

        if (captured != null) {
            captured.addAll(List.of(lines));
            return;
        }

        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(INDENT + line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Returns a count with its noun, made plural unless there is exactly one,
     * for example {@code 1 task} or {@code 2 tasks}.
     *
     * @param count how many there are.
     * @param noun the singular form of what is being counted.
     * @return the count and the noun, ready to drop into a sentence.
     */
    private static String describeCount(int count, String noun) {
        assert count >= 0 : "cannot describe " + count + " of " + noun;

        return count + " " + noun + (count == 1 ? "" : "s");
    }
}
