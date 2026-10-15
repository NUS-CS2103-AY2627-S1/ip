package shai.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import shai.exception.ShaiException;
import shai.parser.DateTimeParser;
import shai.task.Reminder;
import shai.task.Task;
import shai.task.TaskList;

/**
 * Handles Shai's interactions with the user through the console.
 *
 * <p>The command-processing logic remains in {@link Shai}; this class keeps
 * input handling and presentation details out of that logic.</p>
 */
public class Ui {
    private static final String LINE = "\t____________________________________________________________";
    private static final long GOODBYE_DELAY_MILLIS = 2000;
    /** Destination for user-facing messages. */
    private final PrintStream output;

    /** Source of commands for the console interface. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(System.out, System.in);
    }

    /**
     * Creates an output-only user interface for non-console clients.
     *
     * @param output destination for user-facing messages
     */
    public Ui(PrintStream output) {
        this(output, InputStream.nullInputStream());
    }

    private Ui(PrintStream output, InputStream input) {
        this.output = output;
        scanner = new Scanner(input);
    }

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next command from standard input. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the greeting shown when Shai starts. */
    public void showBanner() {
        String banner = LINE + "\n"
                + "\t  ____  _           _\n"
                + "\t / ___|| |__   __ _(_)\n"
                + "\t \\___ \\| '_ \\ / _` | |\n"
                + "\t  ___) | | | | (_| | |\n"
                + "\t |____/|_| |_|\\__,_|_|\n"
                + "\t" + getGreeting().replace("\n", "\n\t") + "\n"
                + LINE
                + "\n";

        output.println(banner);
    }

    /** Returns the short greeting used by graphical clients. */
    public String getGreeting() {
        return "Yo, I'm Shai. What's good, King? Ready to get things done?";
    }

    /** Prints the separator before processing a command. */
    public void showCommandStart() {
        output.println(LINE);
    }

    /** Prints the separator after processing a command. */
    public void showCommandEnd() {
        output.println(LINE + "\n");
    }

    /** Prints an error encountered while loading tasks. */
    public void showLoadingError(ShaiException exception) {
        showError(exception);
    }

    /** Prints an error produced while processing a command. */
    public void showError(ShaiException exception) {
        output.println("\t" + exception.getMessage());
    }

    /** Prints the response for the {@code bye} command. */
    public void showGoodbye() {
        output.println("\tUntil next time, King. Keep winning.");
    }

    /** Waits briefly so the user can read the goodbye message before Shai exits. */
    public void waitBeforeExit() {
        try {
            Thread.sleep(GOODBYE_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /** Prints every task currently stored in the task list. */
    public void showTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            output.println("\tThe lineup is empty, King.");
            return;
        }
        showTaskList(tasks, "\tHere's the current lineup, King.");
    }

    /** Prints every task whose description contains the requested keyword. */
    public void showMatchingTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            output.println("\tNothing showed up on the scouting report, King.");
            return;
        }
        showTaskList(tasks, "\tHere's what I found on the scouting report, King.");
    }

    /** Prints reminders scheduled within the supported upcoming window. */
    public void showReminders(TaskList tasks, LocalDateTime now) {
        List<Reminder> reminders = tasks.findUpcomingReminders(now);
        if (reminders.isEmpty()) {
            output.println("\tNo reminders on the board. Stay ready, King.");
            return;
        }

        List<Reminder> upcomingReminders = reminders.stream()
                .filter(reminder -> !reminder.getReminderTime().isBefore(now))
                .toList();
        List<Reminder> missedReminders = reminders.stream()
                .filter(reminder -> reminder.getReminderTime().isBefore(now))
                .toList();

        if (!upcomingReminders.isEmpty()) {
            output.println("\tHere's the reminder lineup, King.");
            showReminderLines(upcomingReminders, "reminder");
        }
        if (!missedReminders.isEmpty()) {
            if (!upcomingReminders.isEmpty()) {
                output.println();
            }
            output.println("\tMissed reminders, King. Handle these while they're still upcoming:");
            showReminderLines(missedReminders, "missed reminder");
        }
    }

    /** Prints reminders with the supplied label for their scheduled time. */
    private void showReminderLines(List<Reminder> reminders, String timeLabel) {
        for (Reminder reminder : reminders) {
            output.println("\t" + (reminder.getTaskIndex() + 1) + "." + reminder.getTask()
                    + " (" + timeLabel + ": "
                    + DateTimeParser.formatForDisplay(reminder.getReminderTime()) + ")");
        }
    }

    /** Prints automatic notices for reminders due soon. */
    public void showAutomaticReminders(List<Reminder> reminders) {
        if (reminders.isEmpty()) {
            return;
        }

        output.println("\tReminder alert, King. Time to lock in.");
        for (Reminder reminder : reminders) {
            output.println("\t" + (reminder.getTaskIndex() + 1) + "." + reminder.getTask()
                    + " (reminder: " + DateTimeParser.formatForDisplay(reminder.getReminderTime()) + ")");
        }
    }

    /** Prints the response for updating a reminder. */
    public void showReminderUpdated(int taskNumber, Task task, LocalDateTime reminderTime) {
        output.println("\tLocked in. I'll remind you at "
                + DateTimeParser.formatForDisplay(reminderTime) + ", King.");
        output.println("\t  " + task);
    }

    /** Prints the response for disabling a reminder. */
    public void showReminderDisabled(int taskNumber, Task task) {
        output.println("\tThat reminder's been benched, King.");
        output.println("\t  " + task);
    }

    /** Prints a task list under the supplied heading. */
    private void showTaskList(TaskList tasks, String heading) {
        output.println(heading);
        for (int i = 1; i <= tasks.size(); i++) {
            output.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Prints the response for marking a task as done. */
    public void showMarked(Task task) {
        output.println("\tThat play worked perfectly, King.");
        output.println("\t  " + task);
    }

    /** Prints the response for marking a task as not done. */
    public void showUnmarked(Task task) {
        output.println("\tThat play worked perfectly, King.");
        output.println("\t  " + task);
    }

    /** Prints the response for deleting a task. */
    public void showDeleted(Task task, int remainingTaskCount) {
        output.println("\tThat one's been sent to the bench.");
        output.println("\t  " + task);
        output.println("\tRoster updated, King. You now have " + remainingTaskCount
                + " tasks on the board.");
    }

    /** Prints the response for adding a task. */
    public void showAdded(Task task, int taskCount) {
        output.println("\tAdded to the lineup, King.");
        output.println("\t  " + task);
        output.println("\tRoster updated, King. You now have " + taskCount
                + " tasks on the board.");
    }
}
