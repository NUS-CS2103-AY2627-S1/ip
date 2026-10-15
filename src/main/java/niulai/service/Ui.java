package niulai.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import niulai.model.Deadline;
import niulai.model.Event;
import niulai.model.Task;
import niulai.model.TaskList;

/**
 * Handles interaction between NiuLai and the user.
 */
public class Ui {
    /** The banner shown when NiuLai starts. */
    private static final String BANNER = "|\\ | | |  | |     /\\  |\n"
            + "| \\| | \\__/ |___ /~~\\ |\n";

    /** The separator printed around command responses. */
    private static final String LINE = "    ____________________________________________________________";

    /** Formats dates used by the find command in chatbot output. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Reads commands from the user's standard input. */
    private final Scanner scanner;

    /** Creates a UI connected to standard input and output. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows the startup banner and greeting. */
    public void showWelcome() {
        writeLine(BANNER);
        writeLine(LINE);
        writeLine("     Hello! I'm NiuLai!");
        writeLine("     What can I do for you?");
        writeLine(LINE + "\n");
    }

    /** @return whether another user command is available */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the next command without surrounding whitespace
     */
    public String readCommand() {
        return scanner.nextLine().strip();
    }

    /** Shows the separator before a command response. */
    public void showSeparator() {
        writeLine(LINE);
    }

    /** Shows the farewell message. */
    public void showBye() {
        writeLine("     Bye. Hope not to see you again.");
        writeLine(LINE);
    }

    /** Shows every task in display order. */
    public void showList(TaskList tasks) {
        writeLine("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            writeLine("     " + (i + 1) + "." + tasks.get(i));
        }
        showSeparatorAndBlankLine();
    }

    /** Shows deadlines and events that occur on a specified date. */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        writeLine("     Here are the deadlines and events on "
                + date.format(DISPLAY_DATE_FORMATTER) + ":");

        int matches = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean occursOnDate = task instanceof Deadline deadline && deadline.occursOn(date)
                    || task instanceof Event event && event.occursOn(date);

            if (occursOnDate) {
                writeLine("     " + (i + 1) + "." + task);
                matches++;
            }
        }

        if (matches == 0) {
            writeLine("     No deadlines or events found.");
        }

        showSeparatorAndBlankLine();
    }

    /** Shows tasks whose descriptions contain a keyword, ignoring letter case. */
    public void showTasksContaining(TaskList tasks, String keyword) {
        writeLine("     Here are the matching tasks in your list:");

        var matchingTasks = tasks.findByDescription(keyword);
        int matches = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (matchingTasks.contains(task)) {
                writeLine("     " + (i + 1) + "." + task);
                matches++;
            }
        }

        if (matches == 0) {
            writeLine("     No matching tasks found.");
        }

        showSeparatorAndBlankLine();
    }

    /** Shows the confirmation for marking a task as done. */
    public void showTaskMarked(Task task) {
        writeLine("     Nice! I've marked this task as done:");
        writeLine("       " + task);
        showSeparatorAndBlankLine();
    }

    /** Shows the confirmation for marking a task as not done. */
    public void showTaskUnmarked(Task task) {
        writeLine("     OK, I've marked this task as not done yet:");
        writeLine("       " + task);
        showSeparatorAndBlankLine();
    }

    /** Shows the confirmation for deleting a task. */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        writeLine("     Noted. I've removed this task:");
        writeLine("       " + task);
        writeLine("     Now you have " + remainingTaskCount + " tasks in the list.");
        showSeparatorAndBlankLine();
    }

    /** Shows the confirmation for adding a task. */
    public void showTaskAdded(Task task, int taskCount) {
        writeLine("     Got it. I've added this task:");
        writeLine("       " + task);
        writeLine("     Now you have " + taskCount + " tasks in the list.");
        showSeparatorAndBlankLine();
    }

    /** Shows the confirmation for undoing the most recent state-changing command. */
    public void showUndo() {
        writeLine("     OK, I've undone the last command.");
        showSeparatorAndBlankLine();
    }

    /** Shows the error produced when there is no successful command to undo. */
    public void showNoUndo() {
        showError("NOOO!!! There is nothing to undo.");
    }

    /** Shows a command-processing error. */
    public void showError(String message) {
        writeLine("     " + message);
        showSeparatorAndBlankLine();
    }

    /** Shows a warning for saved lines skipped while recovering valid tasks. */
    public void showRecoveryWarning(List<Integer> lineNumbers) {
        List<Integer> lines = List.copyOf(lineNumbers);
        if (lines.isEmpty()) {
            return;
        }

        String label = lines.size() == 1 ? "line " : "lines ";
        String numbers = String.join(", ", lines.stream().map(String::valueOf).toList());
        showSeparator();
        writeLine("     Warning: I skipped invalid task data on " + label + numbers + ".");
        writeLine("     The original file will be backed up before your next saved change.");
        showSeparatorAndBlankLine();
    }

    /** Shows an actionable error produced when saved tasks cannot be loaded. */
    public void showLoadingError(String message) {
        showSeparator();
        writeLine("     NOOO!!! " + message);
        showSeparatorAndBlankLine();
    }

    /** Shows a separator followed by a blank line. */
    private void showSeparatorAndBlankLine() {
        writeLine(LINE + "\n");
    }

    /** Writes one line of output to the interface's destination. */
    protected void writeLine(String text) {
        System.out.println(text);
    }
}
