package meowmeow.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import meowmeow.task.Task;
import meowmeow.task.TaskStatus;

/**
 * Everything the user directly sees or types: reading command lines from
 * stdin, and building every message Meowmeow shows back. The wording itself
 * lives in {@link Messages}; this class decides <em>when</em> each message
 * is shown and, via {@link MessageStyle}, <em>how</em> it looks for the
 * console versus the GUI.
 *
 * <p>Each {@code show...} method <em>returns</em> its message as a plain
 * {@code String} rather than printing it, so the same text can be sent to
 * the console (wrapped in the divider by {@link #print(String)}) or shown
 * in the GUI as-is. Only {@link #print(String)} and, for the console,
 * {@link #showWarning} write to {@code System.out}.
 *
 * <p>{@code Ui} owns the {@link Scanner} over {@code System.in} and
 * implements {@link AutoCloseable} so a caller can hold it in a
 * try-with-resources block and be sure the scanner (and {@code System.in})
 * is closed even if the command loop throws. The scanner is only opened for
 * {@link MessageStyle#PLAIN}: the GUI never reads console input, so
 * {@link #hasNextCommand()} and {@link #readCommand()} are console-only and
 * throw if called in {@link MessageStyle#RICH}.
 */
public class Ui implements AutoCloseable {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "(=^-ω-^=)  " + NAME;

    private static final String DECOR_ADDED = "✅";
    private static final String DECOR_REMOVED = "🗑";
    private static final String DECOR_MARK = "✔";
    private static final String DECOR_PRIORITY = "⭐";
    private static final String DECOR_LIST = "📋";
    private static final String DECOR_FIND = "🔍";
    private static final String DECOR_ERROR = "❌";
    private static final String DECOR_WARNING = "⚠";

    private final MessageStyle style;
    private final Random random;
    private final Scanner scanner;
    private final List<String> pendingWarnings = new ArrayList<>();

    /**
     * Constructs a UI with the given style and randomness. The console entry
     * point passes {@link MessageStyle#PLAIN} with a fixed-seed {@link Random}
     * so its output stays reproducible for the console UI test plan; the GUI
     * passes {@link MessageStyle#RICH} with a real random source.
     *
     * @param style  how messages built by this UI should be rendered.
     * @param random the source of randomness used to pick between phrasings.
     */
    public Ui(MessageStyle style, Random random) {
        this.style = style;
        this.random = random;
        this.scanner = style == MessageStyle.PLAIN ? new Scanner(System.in) : null;
    }

    /**
     * Returns {@code true} while there is another line of input to read. Lets
     * the command loop end gracefully on end-of-input (e.g. piped input with
     * no "bye" line) instead of {@link #readCommand()} throwing.
     *
     * @throws IllegalStateException if this UI is not {@link MessageStyle#PLAIN}.
     */
    public boolean hasNextCommand() {
        requireConsole();
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line of input, trimmed of surrounding whitespace.
     *
     * @throws IllegalStateException if this UI is not {@link MessageStyle#PLAIN}.
     */
    public String readCommand() {
        requireConsole();
        return scanner.nextLine().trim();
    }

    /** Rejects a console-only operation when this UI has no scanner to read from. */
    private void requireConsole() {
        if (scanner == null) {
            throw new IllegalStateException("Console input is unavailable in " + style + " mode.");
        }
    }

    /**
     * Prints {@code body} to the console wrapped in the standard divider
     * block, matching Meowmeow's console reply format. The GUI does not use
     * this - it shows the {@code show...} strings directly.
     *
     * @param body the message body, as returned by a {@code show...} method.
     */
    public void print(String body) {
        System.out.println(box(body));
    }

    /** Returns the welcome banner shown once at startup. */
    public String showWelcome() {
        String greeting = Messages.welcome(style, random);
        return style == MessageStyle.PLAIN ? join(BANNER, greeting) : greeting;
    }

    /** Returns the farewell banner shown in response to "bye". */
    public String showFarewell() {
        String line = Messages.farewell(style, random);
        if (style == MessageStyle.PLAIN) {
            return join(" /\\_/\\", "( ^.^ )  " + line, " > ^ <");
        }
        return line;
    }

    /**
     * Returns an error message: a randomly chosen in-character opener,
     * followed by the factual detail the caller supplies unchanged. A
     * message may already contain {@code \n} to span several lines.
     *
     * @param message the factual error text to show, from the throw site.
     * @return the message, ready to display.
     */
    public String showError(String message) {
        String[] factualLines = message.split("\n", -1);
        String[] lines = new String[factualLines.length + 1];
        lines[0] = Messages.errorLeadIn(random);
        System.arraycopy(factualLines, 0, lines, 1, factualLines.length);
        return render(DECOR_ERROR, join(lines));
    }

    /**
     * Shows a non-fatal warning from {@link meowmeow.storage.Storage Storage} (an unreadable line, a
     * failed save). For {@link MessageStyle#PLAIN} it is also printed
     * straight to {@code System.out} as a plain line, since it can happen
     * while loading - before the conversation proper - and outside any
     * command's response. Either way it is buffered for
     * {@link #drainWarnings()}, which is how the GUI surfaces it.
     *
     * @param message the warning text to show.
     */
    public void showWarning(String message) {
        pendingWarnings.add(render(DECOR_WARNING, message));
        if (style == MessageStyle.PLAIN) {
            System.out.println(message);
        }
    }

    /**
     * Returns and clears the warnings buffered by {@link #showWarning} since
     * the last call to this method.
     *
     * @return the buffered warnings, oldest first; empty if none are pending.
     */
    public List<String> drainWarnings() {
        List<String> drained = List.copyOf(pendingWarnings);
        pendingWarnings.clear();
        return drained;
    }

    /**
     * Returns the confirmation that a task was added, with the new task
     * count in the right singular/plural wording.
     *
     * @param task      the task that was just added.
     * @param taskCount the number of tasks now in the list.
     * @return the confirmation message.
     */
    public String showAdded(Task task, int taskCount) {
        String body = join(Messages.added(random),
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
        return render(DECOR_ADDED, body);
    }

    /**
     * Returns the confirmation that a task was removed, with the new task
     * count in the right singular/plural wording.
     *
     * @param task      the task that was just removed.
     * @param taskCount the number of tasks now in the list.
     * @return the confirmation message.
     */
    public String showRemoved(Task task, int taskCount) {
        String body = join(Messages.removed(random),
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
        return render(DECOR_REMOVED, body);
    }

    /**
     * Returns the confirmation that a task's done/not-done status changed.
     *
     * @param status the task's new status.
     * @param task   the task whose status changed.
     * @return the confirmation message.
     */
    public String showStatusChange(TaskStatus status, Task task) {
        String header = status == TaskStatus.DONE ? Messages.markedDone(random) : Messages.markedNotDone(random);
        return render(DECOR_MARK, join(header, "   " + task));
    }

    /**
     * Returns the confirmation that a task's priority changed.
     *
     * @param task the task whose priority changed.
     * @return the confirmation message.
     */
    public String showPriorityChange(Task task) {
        return render(DECOR_PRIORITY, join(Messages.priorityChanged(random), "   " + task));
    }

    /**
     * Returns the whole task list, numbered from 1 - the response to a bare
     * "list".
     *
     * @param tasks the tasks to list, in list order.
     * @return the numbered list message.
     */
    public String showTasks(List<Task> tasks) {
        return render(DECOR_LIST, numberedList(Messages.listHeader(random), tasks));
    }

    /**
     * Returns the tasks matching a "list &lt;date&gt;" query. {@code matches}
     * is already filtered to that day by the caller; {@code dateLabel} is the
     * day shown in the header (e.g. {@code "Dec 2 2019"}). The numbers here
     * restart at 1 for this filtered view.
     *
     * @param dateLabel the date as it should read in the message.
     * @param matches   the tasks occurring on that date, in list order.
     * @return the numbered list message, or a "free day" line if empty.
     */
    public String showTasksOn(String dateLabel, List<Task> matches) {
        if (matches.isEmpty()) {
            return render(DECOR_LIST, String.format(Messages.emptyDate(random), dateLabel));
        }
        return render(DECOR_LIST, numberedList(String.format(Messages.dateHeader(random), dateLabel), matches));
    }

    /**
     * Returns the tasks matching a "find &lt;keyword&gt;" query. {@code matches}
     * is already filtered by the caller; the numbers here restart at 1 for
     * this filtered view. An empty result gets its own line.
     *
     * @param matches the tasks whose description contains the keyword, in
     *     list order.
     * @return the numbered list message, or a "no matches" line if empty.
     */
    public String showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            return render(DECOR_FIND, Messages.emptyFind(random));
        }
        return render(DECOR_FIND, numberedList(Messages.findHeader(random), matches));
    }

    /**
     * Returns {@code tasks} as a 1-based numbered list under {@code header}.
     * Shared by the "list", "list &lt;date&gt;" and "find" responses, which
     * differ only in that header line and their empty-result wording (which
     * the callers handle before calling here).
     *
     * @param header the line shown above the numbered tasks.
     * @param tasks  the tasks to number, in the order given.
     * @return the header, then one " {@code n.<task>}" line per task.
     */
    private String numberedList(String header, List<Task> tasks) {
        String numbered = IntStream.range(0, tasks.size())
                .mapToObj(i -> " " + (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return numbered.isEmpty() ? header : header + "\n" + numbered;
    }

    /** Returns "task" for a count of 1, "tasks" otherwise. */
    private String taskWord(int count) {
        return count == 1 ? "task" : "tasks";
    }

    /** Joins message lines with a newline into a single body string. */
    private String join(String... lines) {
        return String.join("\n", lines);
    }

    /** Wraps a message body between the divider lines for console output. */
    private String box(String body) {
        return DIVIDER + "\n" + body + "\n" + DIVIDER;
    }

    /**
     * Renders a message body for this UI's {@link MessageStyle}: unchanged
     * for {@link MessageStyle#PLAIN}, or with each line's leading space
     * stripped and {@code decoration} placed before the first line for
     * {@link MessageStyle#RICH}.
     *
     * @param decoration the symbol to lead the first line with in
     *     {@link MessageStyle#RICH}; ignored for {@link MessageStyle#PLAIN}.
     * @param body       the message body, built with {@link MessageStyle#PLAIN}'s
     *                   leading-space convention.
     * @return the body rendered for this UI's style.
     */
    private String render(String decoration, String body) {
        if (style == MessageStyle.PLAIN) {
            return body;
        }
        String[] lines = body.split("\n", -1);
        StringBuilder rendered = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                rendered.append("\n");
            }
            String stripped = lines[i].stripLeading();
            rendered.append(i == 0 ? decoration + " " + stripped : stripped);
        }
        return rendered.toString();
    }

    /** Closes the underlying scanner (and with it {@code System.in}), if one was opened. */
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
