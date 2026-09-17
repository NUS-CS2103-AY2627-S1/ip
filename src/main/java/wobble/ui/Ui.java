package wobble.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import wobble.parser.DateTimeParser;
import wobble.tasks.Task;
import wobble.tasks.TaskList;

/** Handles all interaction between Wobble and the user. */
public class Ui {
    private static final int SHUTDOWN_DOT_COUNT = 10;
    private static final long SHUTDOWN_DOT_DELAY_MILLIS = 60;

    /** Displays Wobble's welcome message. */
    public void showWelcome() {
        System.out.println("==============================");
        System.out.println("  WOBBLE // Systems Online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("Memory tray calibrated. Awaiting your next mission.");
        System.out.println("What shall we remember?");
        System.out.println("==============================");
    }

    /** Displays the command formats supported by Wobble. */
    public void showHelp() {
        System.out.println("WOBBLE COMMAND DECK // QUICK REFERENCE");
        System.out.println("Pick a command below. Type help again whenever you need a systems check.");
        System.out.println();
        System.out.println("Add tasks:");
        System.out.println("  todo <description>");
        System.out.println("  deadline <description> /by <date/time>");
        System.out.println("  event <description> /from <date/time> /to <date/time>");
        System.out.println();
        System.out.println("Manage tasks:");
        System.out.println("  list");
        System.out.println("  find <keyword>");
        System.out.println("  mark <number>");
        System.out.println("  unmark <number>");
        System.out.println("  delete <number>");
        System.out.println("  remove <number> (alias)");
        System.out.println();
        System.out.println("Dates and reminders:");
        System.out.println("  due on <date>");
        System.out.println("  reminders [number of days]");
        System.out.println("  Date: yyyy-MM-dd, yyyy.MM.dd, or yyyy/MM/dd");
        System.out.println("  Time: <date> HHmm (e.g., 2026-09-15 1800)");
        System.out.println("  Time: <date> HH:mm (e.g., 2026-09-15 18:00)");
        System.out.println("  Valid time range: 00:00 to 23:59 (24-hour clock)");
        System.out.println("  Date-only deadlines are due at the end of that date");
        System.out.println("  Example: deadline submit report /by 2026-09-15 1800");
        System.out.println();
        System.out.println("Exit:");
        System.out.println("  bye");
    }

    /** Reads the next command, or returns null when input ends. */
    public String readCommand(Scanner scanner) {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays a task list. */
    public void showTasks(TaskList taskList) {
        System.out.println("Whirr... memory tray scan complete.");
        if (taskList.size() == 0) {
            System.out.println("Nothing is wobbling on the tray yet. A very tidy tray!");
        }
        for (int i = 1; i <= taskList.size(); i++) {
            System.out.println(i + ". " + taskList.get(i));
        }
    }

    /** Displays tasks matching a keyword and preserves their original list numbers. */
    public void showMatchingTasks(TaskList taskList, String keyword) {
        System.out.println("Signal scan complete. Here are the matching tasks in your list:");
        List<Integer> matchingTaskNumbers = taskList.find(keyword);
        if (matchingTaskNumbers.isEmpty()) {
            System.out.println("No tasks match that keyword. Wobble searched everywhere!");
        }
        for (int taskNumber : matchingTaskNumbers) {
            System.out.println(taskNumber + ". " + taskList.get(taskNumber));
        }
    }

    /** Displays unfinished deadlines and events within the requested day range. */
    public void showReminders(TaskList taskList, List<Integer> taskNumbers,
            LocalDateTime now, int days) {
        if (taskNumbers.isEmpty()) {
            System.out.println("Radar clear. No upcoming reminders are wobbling in the next "
                    + days + " days.");
            return;
        }
        System.out.println("Radar sweep complete. Here are your upcoming reminders:");
        for (int taskNumber : taskNumbers) {
            System.out.println(taskNumber + ". " + taskList.get(taskNumber));
        }
        System.out.println("Reminder window starts " + DateTimeParser.format(now) + ".");
    }

    /** Displays an error diagnostic. */
    public void showDiagnostic(String message) {
        System.out.println("Wobble diagnostic: " + message);
    }

    /** Displays the successful task-addition message. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Beep boop! Task docked in my memory tray:");
        System.out.println("  " + task);
        System.out.println("I'll keep an eye on it.");
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        System.out.println("Bye, human! Wobble is signing off.");
        System.out.println("SHUTDOWN_SEQUENCE :: START");
        showShutdownStep("save memory");
        showShutdownStep("lock task tray");
        showShutdownStep("disconnect");
        System.out.println("STATUS :: OFFLINE // beep... boop.");
        System.out.println("See you on the next boot, human.");
        System.out.println("==============================");
    }

    /** Displays one shutdown operation with progressive dots before confirming completion. */
    private void showShutdownStep(String operation) {
        System.out.print("> " + operation + " ");
        for (int dotCount = 0; dotCount < SHUTDOWN_DOT_COUNT; dotCount++) {
            System.out.print(".");
            System.out.flush();
            pauseForShutdownDot();
        }
        System.out.println(" OK");
    }

    /** Pauses briefly so the shutdown progress is visible without delaying the app excessively. */
    private void pauseForShutdownDot() {
        try {
            Thread.sleep(SHUTDOWN_DOT_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
