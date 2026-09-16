package lebronjames.ui;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;

/**
 * Deals with all interactions with the user: reading commands and wording the
 * replies that are shown back.
 *
 * <p>Gathering the input and output here means the rest of the chatbot never
 * calls {@code System.out} directly. Wording and layout can then be changed in
 * one place, and the task-handling classes stay free of display details.
 *
 * <p>This class is also where the chatbot's character lives. It speaks as
 * LeBron James, "The King": tasks are plays, the task list is the playbook, a
 * completed task is a bucket, and a command it cannot read is an airball. The
 * voice belongs here rather than being sprinkled through the command classes,
 * so rewording the chatbot means editing one file. The matching colours are in
 * {@code /view/style.css} and the crowned basketball is the banner below.
 *
 * <p>Replies are collected into a buffer rather than printed straight away.
 * {@link #getResponse()} hands back everything said since it was last called
 * and empties the buffer, so the same {@code Ui} serves both front ends: the
 * text interface prints that string, while the graphical one puts it in a
 * dialog bubble. Printing directly would have tied every {@code show} method
 * to the console.
 */
public class Ui {
    /** Horizontal rule the text interface prints above and below every reply. */
    public static final String SEPARATOR = "_".repeat(60);

    /**
     * Crowned basketball drawn at start-up.
     *
     * <p>The crown is the chatbot's badge: it is the first thing shown, and it
     * says who is talking before a word has been read. The lines are padded
     * rather than trimmed so the crown sits centred over the ball.
     */
    private static final String BANNER = "      /\\  /\\  /\\      \n"
            + "     (==========)      \n"
            + "       .-\"\"\"-.       \n"
            + "     .'  \\ | /  '.     \n"
            + "    /     \\|/     \\    \n"
            + "   ;-------+-------;   \n"
            + "    \\     /|\\     /    \n"
            + "     '.  / | \\  .'     \n"
            + "       '-...-'";

    /** Format used when echoing back the date asked about by the on command. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Reader for the user's commands, used by the text interface only.
     *
     * <p>The character set is stated explicitly rather than left to the
     * platform. {@link lebronjames.storage.Storage} always writes the save file
     * as UTF-8, so reading input as anything else would turn a description such
     * as "café run" into "cafÃ© run" on its way to disk.
     */
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    /** Reply being built up, until {@link #getResponse()} takes it away. */
    private final StringBuilder response = new StringBuilder();

    /**
     * Returns everything said since this method was last called, and clears the
     * buffer so the next reply starts empty.
     *
     * <p>Only blank lines are trimmed, not indentation. Stripping every leading
     * space would pull the first line of the crowned basketball flush against
     * the margin while the lines below it kept their padding, which tips the
     * crown over to one side.
     *
     * @return Reply text, with no leading or trailing blank lines.
     */
    public String getResponse() {
        String reply = response.toString().replaceAll("^\\R+", "").stripTrailing();
        response.setLength(0);
        return reply;
    }

    /**
     * Adds the greeting, the basketball banner, and the accepted task formats.
     */
    public void showWelcome() {
        say(BANNER);
        say("What's good? I'm LeBron James. They call me The King. 👑");
        say("You call the plays, I'll run the offense.");
        showTaskInstructions();
    }

    /**
     * Adds the accepted formats for adding each type of task.
     */
    public void showTaskInstructions() {
        say("Call a play with one of these formats:");
        say("1. Todo: todo <task>");
        say("2. Deadline: deadline <task> /by <date>");
        say("3. Event: event <task> /from <date> /to <date>");
        say("Dates use " + TaskDateTime.ACCEPTED_FORMATS + ".");
        say("Check the game plan for one day with: on <date>");
        say("Scout your playbook with: find <keyword>");
    }

    /**
     * Returns whether the user has typed another command.
     *
     * @return Whether more input is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command typed by the user.
     *
     * @return Line of input, exactly as typed.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Adds an error message, such as the explanation carried by a
     * {@link lebronjames.LebronJamesException LebronJamesException}.
     *
     * @param message Explanation to show the user.
     */
    public void showError(String message) {
        say(message);
    }

    /**
     * Reports that saved tasks were restored, staying silent when there were none.
     *
     * @param taskCount Number of tasks restored from the save file.
     */
    public void showLoaded(int taskCount) {
        // The count comes straight from List.size(), so a negative value would
        // mean the caller had passed something other than a real list size.
        assert taskCount >= 0 : "A task count can never be negative";

        if (taskCount > 0) {
            say("I pulled " + taskCount + " play(s) off the old tape. Type list to run it back.");
        }
    }

    /**
     * Reports that the save file could not be read and that the list starts empty.
     *
     * @param message Explanation of what went wrong.
     */
    public void showLoadingError(String message) {
        say(message);
        say("Starting the playbook empty. Saving will overwrite that file.");
    }

    /**
     * Adds the whole task list, numbered from one.
     *
     * @param tasks Tasks currently stored.
     */
    public void showTaskList(List<Task> tasks) {
        say("Here is your playbook:");
        showNumberedTasks(tasks);
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks currently stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        say("BUCKET! That play is on the board:");
        say("  " + task + " 🏀");
        say("That makes " + taskCount + " plays in the playbook.");
    }

    /**
     * Confirms that a task was removed and reports the new list size.
     *
     * @param task Task that was removed.
     * @param taskCount Number of tasks still stored.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        say("Subbed out. That play is off the board:");
        say("  " + task + " 🏀");
        say("That leaves " + taskCount + " plays in the playbook.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        say("AND-ONE! Chalk that one up:");
        say("  " + task + " 🏀");
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        say("Whistle blew. Back on the floor:");
        say("  " + task + " 🏀");
    }

    /**
     * Adds the tasks that fall on the given date, or a note that the day is free.
     *
     * @param tasks Tasks scheduled on that date.
     * @param date Date the user asked about.
     */
    public void showTasksOn(List<Task> tasks, LocalDate date) {
        if (tasks.isEmpty()) {
            say("No game on " + date.format(DISPLAY_DATE_FORMAT) + ". Rest day, put your feet up!");
            return;
        }

        say("Game plan for " + date.format(DISPLAY_DATE_FORMAT) + ":");
        showNumberedTasks(tasks);
    }

    /**
     * Adds the tasks that matched a search, or a note that none did.
     *
     * @param tasks Tasks whose descriptions contain the keyword.
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            say("No plays match that. Run the scout again with another keyword!");
            return;
        }

        say("Here are the plays that match:");
        showNumberedTasks(tasks);
    }

    /**
     * Adds the farewell message.
     */
    public void showGoodbye() {
        say("Game over. Taking my talents home. 👑🏀");
    }

    /**
     * Adds the given tasks as a numbered list starting at one.
     *
     * @param tasks Tasks to add, in list order.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            say((i + 1) + "." + tasks.get(i) + " 🏀");
        }
    }

    /**
     * Adds one line to the reply being built up.
     *
     * @param line Text to add, without a line break of its own.
     */
    private void say(String line) {
        response.append(line).append(System.lineSeparator());
    }
}
