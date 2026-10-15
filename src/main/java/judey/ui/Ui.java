package judey.ui;

import judey.task.Task;
import judey.parser.Parser;

import java.util.List;
import java.util.Scanner;

/** Handles interactions with the user, including reading inputs and printing outputs. */
public class Ui {
    private static final String DIVIDER = "----------------------------------------";
    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Reads the next line of user input. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints a visual divider line. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /** Prints the startup welcome message and banner. */
    public void showWelcome() {
        showLine();
        String banner = "JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy\n"
                + "   JJ   uu   uu  dd   dd  ee       yy   yy\n"
                + "   JJ   uu   uu  dd   dd  eeeee     yyyyy\n"
                + "JJ JJ   uu   uu  dd   dd  ee         yyy\n"
                + " JJJ     uuuu u  dddddd   eeeeeee    yyy";
        System.out.println(banner + "\n");
        System.out.println("Meowdy! I'm Judey, your astronaut cat assistant.\n"
                + "Ready to launch your tasks into orbit?");
        showLine();
    }

    /** Prints the available commands and examples for using Judey. */
    public void showHelp() {
        showLine();
        System.out.println("Judey's Space-Cat Command Deck\n");
        System.out.println("Use these commands to keep your missions purrfectly organized.\n");
        System.out.println("Creating tasks:");
        System.out.println("  todo <description>\n    Example: todo read book\n");
        System.out.println("  deadline <description> /by <date and time>\n"
                + "    Format: d/M/yyyy HHmm\n"
                + "    Example: deadline submit report /by 31/12/2026 2359\n");
        System.out.println("  event <description> /from <date and time> /to <date and time>\n"
                + "    Format: d/M/yyyy HHmm\n"
                + "    Example: event team meeting /from 15/10/2026 1400 /to 15/10/2026 1600\n");
        System.out.println("Managing tasks:");
        System.out.println("  list\n    Example: list\n");
        System.out.println("  mark <task number>\n    Example: mark 1\n");
        System.out.println("  unmark <task number>\n    Example: unmark 1\n");
        System.out.println("  delete <task number>\n    Example: delete 1\n");
        System.out.println("Searching and filtering:");
        System.out.println("  events-on <date>\n"
                + "    Format: d/M/yyyy\n"
                + "    Example: events-on 2/12/2026\n");
        System.out.println("  find <keyword>\n    Example: find project meeting\n");
        System.out.println("Application:");
        System.out.println("  help\n    Example: help\n");
        System.out.println("  bye\n    Example: bye");
        showLine();
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        showLine();
        System.out.println("Bye for now, space cadet! Keep your tasks purrfectly organized.");
        showLine();
    }

    /** Prints a user-facing error message. */
    public void showError(String message) {
        showErrorMessage(stripHighlightMarkers(message));
    }

    /** Prints an error while preserving GUI-only highlight markers. */
    public void showErrorWithMarkup(String message) {
        showErrorMessage(message);
    }

    private void showErrorMessage(String message) {
        showLine();
        System.out.println("Oopsie! " + message);
        showLine();
    }

    private String stripHighlightMarkers(String message) {
        return message.replace(Parser.HIGHLIGHT_START, "").replace(Parser.HIGHLIGHT_END, "");
    }

    /** Prints an error message when storage fails to load data. */
    public void showLoadingError() {
        showLine();
        System.out.println("Warning: Mission control could not load your tasks. Starting with an empty log.");
        showLine();
    }

    /** Prints confirmation after a task is added. */
    public void showTaskAdded(Task task, int taskCount) {
        showLine();
        System.out.println("Purrfect! I've launched this task into your mission log:\n  " + task);
        System.out.println("You now have " + taskCount + " tasks in orbit.\n");
        showLine();
    }

    /** Prints confirmation after a task is removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        showLine();
        System.out.println("Mission control confirms: this task has been removed:");
        System.out.print("  " + task);
        System.out.println("You now have " + taskCount + " tasks in orbit.");
        showLine();
    }

    /** Prints notification when a task is marked or unmarked. */
    public void showTaskStatusChanged(Task task, boolean isDone) {
        showLine();
        if (isDone) {
            System.out.println("Purrfect! I've marked this task complete and ready for re-entry:\n  " + task);
        } else {
            System.out.println("This task is back on the active mission list:\n  " + task);
        }
        showLine();
    }
}
