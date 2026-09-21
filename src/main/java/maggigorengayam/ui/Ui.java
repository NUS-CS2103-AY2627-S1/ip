package maggigorengayam.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.util.DateTimeUtil;
import maggigorengayam.util.FreeTimeFinder.FreeSlot;

/**
 * Builds every message the program shows to the user (the welcome banner,
 * task confirmations, the divider line, errors), and reads command lines
 * from stdin. Command handling talks to this instead of formatting
 * messages itself, so "how things look" has one home and can change
 * without touching command logic.
 *
 * <p>Each {@code show*} method returns the message as a String rather than
 * printing it, so the same formatting can be reused by both the CLI (which
 * prints what it gets back) and the JavaFX GUI (which displays it as a
 * chat bubble instead).
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    /** Opens a Scanner over standard input for reading command lines. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Whether there is another line of input to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads one line of input, with leading/trailing whitespace trimmed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Closes the underlying input Scanner. */
    public void close() {
        scanner.close();
    }

    /** Returns the divider line shown before and after every command's output. */
    public String showLine() {
        return LINE;
    }

    /** Returns the startup welcome banner. */
    public String showWelcome() {
        return LINE + "\n"
                + "  __  __  _____    _    \n"
                + " |  \\/  |/ ____|  / \\   \n"
                + " | \\  / ||   __  / _ \\  \n"
                + " | |\\/| ||  |_ |/ ___ \\ \n"
                + " |_|  |_|\\_____/_/   \\_\\\n"
                + "Hello! I'm Maggi Goreng Ayam.\n"
                + "What can I do for you?\n"
                + LINE;
    }

    /** Returns the farewell message shown when the program exits. */
    public String showGoodbye() {
        return " Bye. Hope to see you again soon!";
    }

    /** Returns {@code message} prefixed with "OOPS!!!". */
    public String showError(String message) {
        return " OOPS!!! " + message;
    }

    /** Returns the list of supported commands and their syntax, shown for the {@code help} command. */
    public String showHelp() {
        return " Here's what I can do:\n"
                + " todo <description> - add a to-do task\n"
                + " deadline <description> /by <date>[ time] - add a task with a due date\n"
                + " event <description> /from <date>[ time] /to <date>[ time] - add a task spanning a time range\n"
                + " list - show every task\n"
                + " mark <n> - mark task n as done\n"
                + " unmark <n> - mark task n as not done\n"
                + " delete <n> - remove task n\n"
                + " find <keyword> - find tasks whose description contains keyword\n"
                + " on <date> - show deadlines/events occurring on a date\n"
                + " freetime <hours> <windowStartHHmm> <windowEndHHmm> - find the nearest free slot\n"
                + " help - show this list\n"
                + " bye - exit\n"
                + " Dates are yyyy-MM-dd (e.g. 2019-12-02); times are 24-hour HHmm (e.g. 1800).";
    }

    /** Shown once during startup - see the class doc. */
    public String showLoadWarning(int skippedLineCount) {
        return LINE + "\n"
                + " OOPS!!! " + skippedLineCount
                + " of your saved task(s) rosak already, I skip lor.\n"
                + LINE;
    }

    /** Shown once during startup - see the class doc. */
    public String showLoadingError() {
        return LINE + "\n"
                + " OOPS!!! Cannot load your saved tasks sia. Start with empty list first can.\n"
                + LINE;
    }

    /** Returns a warning that a task-list save to disk failed. */
    public String showSaveError() {
        return " OOPS!!! Cannot save to disk leh. Your change stay in memory only ah, don't close the app.";
    }

    /** Returns every task in {@code tasks}, numbered from 1. */
    public String showTaskList(TaskList tasks) {
        return " Here your list, see for yourself:" + numberedLines(tasks.getAll());
    }

    /** Returns {@code matches}, numbered from 1, under a header naming {@code dateLabel}. */
    public String showTasksOn(String dateLabel, List<Task> matches) {
        return " On " + dateLabel + ", this is what you got:" + numberedLines(matches);
    }

    /** Returns {@code matches}, numbered from 1, as the tasks found by the {@code find} command. */
    public String showMatchingTasks(List<Task> matches) {
        return " I find already, these all u got:" + numberedLines(matches);
    }

    /** e.g. "\n 1.[T][ ] read book\n 2.[D][ ] return book (...)" - one "\n {number}.{task}" line per task. */
    private String numberedLines(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> "\n " + (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining());
    }

    /** Returns confirmation that a free slot was found, with its exact date and start-end time. */
    public String showFreeSlot(FreeSlot slot) {
        return " Here, you free on " + DateTimeUtil.formatDateOnlyForDisplay(slot.date)
                + ", from " + DateTimeUtil.formatTimeForDisplay(slot.start)
                + " to " + DateTimeUtil.formatTimeForDisplay(slot.end) + ".";
    }

    /** Returns a message reporting that no free slot was found within the next {@code maxDaysAhead} days. */
    public String showNoFreeSlot(int maxDaysAhead) {
        return " OOPS!!! Cannot find that long a slot in next "
                + maxDaysAhead + " days leh. Try shorter one can or not?";
    }

    /** Returns confirmation that {@code task} was added, and the new task count. */
    public String showAdded(Task task, int taskCount) {
        return " Can can, added already:\n   " + task
                + "\n Now you got " + taskCount + " task(s) in the list alr.";
    }

    /** Returns confirmation that {@code task} was removed, and the new task count. */
    public String showRemoved(Task task, int taskCount) {
        return " Okay noted, I throw away already:\n   " + task
                + "\n Now you got " + taskCount + " task(s) in the list alr.";
    }

    /** Returns confirmation that {@code task} was marked as done. */
    public String showMarked(Task task) {
        return " Wah steady! Marked done already:\n   " + task;
    }

    /** Returns confirmation that {@code task} was marked as not done. */
    public String showUnmarked(Task task) {
        return " Okay lah, put back to not-done:\n   " + task;
    }
}
