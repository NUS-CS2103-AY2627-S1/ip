package victoria.ui;

import victoria.exception.VictoriaException;
import victoria.model.TaskList;
import victoria.storage.TaskFile;

/** Handles all console output that is part of Victoria's user interface. */
public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";

    /** Prints the startup screen shown during a normal launch. */
    public void showWelcome() {
        printAnimatedLine("> Victoria is warming up...", 10);
        printAnimatedLine("> Gathering your task list...", 10);
        printAnimatedLine("> All set—let's make today count!", 10);
        System.out.println();
        printAnimatedLine(getBanner(), 1);
        printSeparator();
        System.out.println("Hi! I'm Victoria, your cheerful task sidekick. What shall we tackle today?");
        printSeparator();
        printCommandFormat();
        printSeparator();
    }

    /** Returns the welcome text shown immediately in the graphical user interface. */
    public static String getGreetingMessage() {
        return getBanner()
                + HORIZONTAL_LINE + "\n"
                + "Hi! I'm Victoria, your cheerful task sidekick. What shall we tackle today?\n"
                + HORIZONTAL_LINE + "\n"
                + getCommandFormat()
                + HORIZONTAL_LINE;
    }

    /** Prints the outcome of loading saved tasks. */
    public void showLoadResult(TaskFile.LoadResult result, TaskList tasks) {
        switch (result.status()) {
        case LOADED:
            System.out.println("Your saved tasks are back—nice to see them again:");
            tasks.printTasks();
            break;
        case NO_FILE:
            System.out.println("No saved list yet—perfect, we have a fresh start!");
            break;
        case EMPTY:
            System.out.println("Your saved list is empty, so let's start fresh!");
            break;
        case NO_VALID_RECORDS:
            System.out.println("I couldn't find usable saved tasks, so let's start with a clean slate!");
            break;
        case ERROR:
            System.out.println("I couldn't open the saved list, so let's start with a clean slate!");
            break;
        default:
            throw new IllegalStateException("Unknown load status: " + result.status());
        }
        printSeparator();
    }

    /** Prints the separator used between interactive commands. */
    public void printSeparator() {
        System.out.println(HORIZONTAL_LINE);
    }

    /** Prints a user-facing command error without exposing implementation details. */
    public void showError(VictoriaException exception) {
        System.out.println(" Oops! " + exception.getMessage());
    }

    /** Prints the message shown when the session ends. */
    public void showFarewell() {
        System.out.println("That's all for now—great work today! See you soon!");
        printSeparator();
    }

    /** Prints the command grammar shown before input is accepted. */
    private void printCommandFormat() {
        System.out.print(getCommandFormat());
    }

    /** Returns the command reference shown when Victoria starts. */
    private static String getCommandFormat() {
        return ">> YOUR QUICK COMMAND GUIDE\n"
                + ">> todo <description>       ADD A TASK\n"
                + ">> deadline <description> /by <date> (yyyy-MM-dd)\n"
                + ">> event <description> /from <date> /to <date> (yyyy-MM-dd)\n"
                + ">> list                     SEE YOUR TASKS\n"
                + ">> list on <date> (yyyy-MM-dd) SEE WHAT'S SCHEDULED\n"
                + ">> find <keyword>          SEARCH YOUR TASKS\n"
                + ">> mark <number>            CELEBRATE A FINISHED TASK\n"
                + ">> unmark <number>          PUT A TASK BACK\n"
                + ">> delete <number>          REMOVE A TASK\n\n"
                + ">> READY WHEN YOU ARE!\n";
    }

    /** Returns Victoria's text banner. */
    private static String getBanner() {
        return "[ VICTORIA ]\n"
                + "Your cheerful task sidekick\n";
    }

    /** Prints text one character at a time to create the startup animation. */
    private void printAnimatedLine(String text, long delayMilliseconds) {
        for (char character : text.toCharArray()) {
            System.out.print(character);
            System.out.flush();
            pause(delayMilliseconds);
        }
        System.out.println();
    }

    /** Pauses between animated characters without losing interruption status. */
    private void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
