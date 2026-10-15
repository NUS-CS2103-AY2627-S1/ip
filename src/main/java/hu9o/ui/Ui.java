package hu9o.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import hu9o.contact.Person;
import hu9o.contact.PersonNetwork;
import hu9o.task.Task;
import hu9o.task.TaskList;

/**
 * Owns every line Hu9o prints to the console.
 *
 * <p>Keeping all output in one class means the logic classes
 * ({@link hu9o.parser.Parser}, {@link hu9o.storage.Storage}, {@link TaskList})
 * never touch {@code System.out} directly, so the exact wording, spacing, and
 * conversational pauses live in a single place. {@code Ui} handles output;
 * {@link hu9o.parser.Parser} handles input.
 *
 * <p>A {@code Ui} runs in one of two modes. The default mode prints to the
 * console for the text CLI. A UI made by {@link #createForGui()} instead
 * captures its output in memory, drops the decorative separator rules and
 * pauses that only make sense in a terminal, and lets the GUI read each reply
 * back with {@link #readCaptured()}.
 */
public class Ui {
    /** Horizontal rule printed around each block of output. */
    private static final String SEPARATOR = "_________________________________";

    /** ASCII-art logo shown at start-up. */
    private static final String BANNER = " _   _           ___ \n"
            + "| | | | | | | | / _ \\   ___  \n"
            + "| |_| | | | | || (_) | / _ \\ \n"
            + "|  _  | | |_| | \\__,| | (_) |\n"
            + "|_| |_|  \\___/   /_/   \\___/ \n";

    /** Width, in characters, of the progress bar printed by {@link #showProgress(TaskList)}. */
    private static final int PROGRESS_BAR_WIDTH = 20;

    /** Destination for all output: the console, or a buffer in GUI mode. */
    private final PrintStream out;

    /** Backing buffer for GUI mode; {@code null} when printing to the console. */
    private final ByteArrayOutputStream guiBuffer;

    /** Whether an error message has been shown since the flag was last consumed. */
    private boolean hasShownError;

    /** Creates a UI that prints to the console for the text CLI. */
    public Ui() {
        this.out = System.out;
        this.guiBuffer = null;
    }

    /**
     * Creates a UI that writes into the given in-memory buffer.
     *
     * @param guiBuffer the buffer that captures every line of output.
     */
    private Ui(ByteArrayOutputStream guiBuffer) {
        this.guiBuffer = guiBuffer;
        this.out = new PrintStream(guiBuffer, true, StandardCharsets.UTF_8);
    }

    /**
     * Returns a UI whose output is captured in memory for the GUI to read back
     * instead of being printed to the console.
     *
     * @return a capturing UI.
     */
    public static Ui createForGui() {
        return new Ui(new ByteArrayOutputStream());
    }

    /** Returns whether this UI captures its output instead of printing it. */
    private boolean isCapturing() {
        return guiBuffer != null;
    }

    /**
     * Sleeps quietly so the start-up and shut-down messages feel conversational.
     * The interrupt flag is restored rather than crashing the program, since a
     * missed pause is harmless. Does nothing in GUI mode, where the call runs on
     * the JavaFX thread and a sleep would freeze the window.
     *
     * @param millis how long to pause, in milliseconds.
     */
    private void pause(long millis) {
        if (isCapturing()) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /** Prints the banner and the "loading" message shown before tasks are read. */
    public void showWelcome() {
        out.println(SEPARATOR);
        out.print(BANNER);
        out.println(SEPARATOR + "\n");
        out.println("Give me a second....Loading tasks...");
        pause(1000);
    }

    /** Prints the greeting shown once tasks have finished loading. */
    public void showReady() {
        out.println("Successful! Use list to view the tasks.");
        pause(500);
        out.println("Woof! I'm Hu9o!");
        pause(500);
        out.println("What can I do for you?\n");
    }

    /** Prints the prompt that asks the user for the next command. */
    public void displayQuery() {
        out.print("> ");
    }

    /** Opens a block of command output with a separator line, except in GUI mode. */
    public void showBlockStart() {
        if (isCapturing()) {
            return;
        }
        out.println(SEPARATOR + "\n");
    }

    /** Closes a block of command output with a separator line, except in GUI mode. */
    public void showBlockEnd() {
        if (isCapturing()) {
            return;
        }
        out.println(SEPARATOR + "\n");
    }

    /** Prints the progress messages shown while tasks are written to disk. */
    public void showSaving() {
        out.println("\n Saving data...");
        pause(500);
        out.println(" Successfully saved data");
        pause(200);
    }

    /** Prints a friendly reply to a greeting such as {@code hi} or {@code hello}. */
    public void showGreeting() {
        out.println("Woof! Hello there! What can I do for you?");
    }

    /** Prints the farewell shown after the user types {@code bye}. */
    public void showFarewell() {
        out.println(SEPARATOR);
        out.println("\nBye. Hope to see you again soon! (wags tail)");
        out.println(SEPARATOR + "\n");
    }

    /**
     * Prints the whole task list, one numbered task per line.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        int index = 1;
        for (Task task : tasks) {
            out.println(index + ". " + task);
            index++;
        }
    }

    /**
     * Prints a progress bar showing how many tasks are marked done out of the total.
     *
     * @param tasks the tasks to summarise.
     */
    public void showProgress(TaskList tasks) {
        int total = tasks.size();
        if (total == 0) {
            out.println("No tasks yet -- nothing to track progress on!");
            return;
        }
        long done = tasks.countDone();
        int filled = (int) Math.round(PROGRESS_BAR_WIDTH * (double) done / total);
        int percent = (int) Math.round(100.0 * done / total);
        String bar = "█".repeat(filled) + "░".repeat(PROGRESS_BAR_WIDTH - filled);
        out.println("[" + bar + "] " + done + "/" + total + " tasks completed (" + percent + "%)");
    }

    /**
     * Prints the tasks that matched a find command, or a notice when none did.
     *
     * @param matches the tasks whose description contained the keyword.
     */
    public void showMatchingTasks(TaskList matches) {
        if (matches.isEmpty()) {
            out.println("No matching tasks found.");
            return;
        }
        out.println("Here are the matching tasks in your list:");
        showTaskList(matches);
    }

    /**
     * Confirms that a task was added and reports the new task count.
     *
     * @param task      the task that was added.
     * @param taskCount the number of tasks now in the list.
     */
    public void showTaskAdded(Task task, int taskCount) {
        out.println("Got it. I've added this task:\n\t" + task);
        out.println("Now you have " + taskCount + (taskCount == 1 ? " task" : " tasks") + " in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task that was marked.
     */
    public void showTaskMarked(Task task) {
        out.println("Nice! I've marked this task as done:\n\t" + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        out.println("Ok, I've marked this task as not done yet:\n\t" + task);
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task the task that was removed.
     */
    public void showTaskDeleted(Task task) {
        out.println("Got it. Deleted the following task:\n\t" + task);
    }

    /**
     * Confirms that a person was added and reports the new person count.
     *
     * @param person      the person that was added.
     * @param personCount the number of people now in the network.
     */
    public void showPersonAdded(Person person, int personCount) {
        out.println("Got it. I've added this person:\n\t" + person);
        out.println("Now you have " + personCount + (personCount == 1 ? " person" : " people") + " in the network.");
    }

    /**
     * Prints the whole person network, one numbered person per line.
     *
     * @param people the people to display.
     */
    public void showPersonList(PersonNetwork people) {
        int index = 1;
        for (Person person : people) {
            out.println(index + ". " + person);
            index++;
        }
    }

    /**
     * Prints the people that matched a findperson command, or a notice when none did.
     *
     * @param matches the people whose name contained the keyword.
     */
    public void showMatchingPeople(PersonNetwork matches) {
        if (matches.isEmpty()) {
            out.println("No matching people found.");
            return;
        }
        out.println("Here are the matching people in your network:");
        showPersonList(matches);
    }

    /**
     * Confirms that a person was deleted.
     *
     * @param person the person that was removed.
     */
    public void showPersonDeleted(Person person) {
        out.println("Got it. Deleted the following person:\n\t" + person);
    }

    /**
     * Confirms that two people were linked.
     *
     * @param first  one of the two people that were connected.
     * @param second the other person that was connected.
     */
    public void showLinkAdded(Person first, Person second) {
        out.println("Got it. Linked " + first.getName() + " and " + second.getName() + ".");
    }

    /**
     * Prints the people connected to a person, or a notice when there are none.
     *
     * @param person      the person whose connections were looked up.
     * @param connections the people linked to them.
     */
    public void showConnections(Person person, List<Person> connections) {
        if (connections.isEmpty()) {
            out.println(person.getName() + " has no connections yet.");
            return;
        }
        out.println(person.getName() + " is connected to:");
        int index = 1;
        for (Person connection : connections) {
            out.println(index + ". " + connection.getName());
            index++;
        }
    }

    /**
     * Confirms that a person is now selected: session commands like todo and
     * list will apply to their own task list until deselected.
     *
     * @param person the person now selected.
     */
    public void showSelected(Person person) {
        out.println("Now viewing " + person.getName()
                + "'s list. todo/list/mark/... apply to them until you deselect.");
    }

    /**
     * Confirms that the current selection was cleared.
     *
     * @param wasSelected {@code true} if someone was actually selected beforehand.
     */
    public void showDeselected(boolean wasSelected) {
        if (wasSelected) {
            out.println("Back to your own list.");
        } else {
            out.println("No one was selected.");
        }
    }

    /**
     * Prints a recoverable error message from a {@code Hu9oException}.
     *
     * @param message the user-facing explanation of what went wrong.
     */
    public void showError(String message) {
        hasShownError = true;
        out.println(message);
    }

    /** Prints the hint shown when a date could not be parsed. */
    public void showDateError() {
        hasShownError = true;
        out.println("Invalid Date...Use day/month/year 12 hr time\n\ti.e 12/8/26 330 pm");
    }

    /**
     * Returns everything written since the previous call and clears the buffer.
     * Only meaningful for a UI created by {@link #createForGui()}.
     *
     * @return the captured text, with surrounding blank lines removed.
     */
    public String readCaptured() {
        String captured = guiBuffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").strip();
        guiBuffer.reset();
        return captured;
    }

    /**
     * Returns whether an error message was shown since this method last ran,
     * then resets the flag so the next command starts clean.
     *
     * @return {@code true} if the most recent output was an error message.
     */
    public boolean consumeErrorShown() {
        boolean shown = hasShownError;
        hasShownError = false;
        return shown;
    }
}
