package alfred.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import alfred.task.Task;
import alfred.task.TaskDateTime;

/**
 * Reads user input and presents chatbot replies for the console or GUI.
 */
public class Ui {
    /**
     * How the GUI should present the latest chatbot reply.
     */
    public enum ReplyStyle {
        /** Conversational reply using the default bubble style. */
        NORMAL,
        /** Error reply, shown with a darker red bubble. */
        ERROR,
        /** Numbered task list, shown in a monospaced font. */
        LIST
    }

    /** Horizontal divider used to frame chatbot messages. */
    private static final String LINE =
            "    ____________________________________________________________";

    /** Indent applied to chatbot message text on the console. */
    private static final String INDENT = "     ";

    /** Difference between a 0-based list index and the 1-based number shown to the user. */
    private static final int USER_NUMBERING_OFFSET = 1;

    private static final String BANNER =
            "        _    _  __              _\n"
                    + "       / \\  | |/ _|_ __ ___  __| |\n"
                    + "      / _ \\ | | |_| '__/ _ \\/ _` |\n"
                    + "     / ___ \\| |  _| | |  __/ (_| |\n"
                    + "    /_/   \\_\\_|_| |_|  \\___|\\__,_|";

    /** One syntax line per command shown by {@code help}. */
    private static final String[] HELP_SYNTAX_LINES = {
        "todo DESCRIPTION",
        "deadline DESCRIPTION /by DATE",
        "event DESCRIPTION /from START /to END",
        "list",
        "list archive",
        "find KEYWORD",
        "on DATE",
        "mark INDEX",
        "unmark INDEX",
        "delete INDEX",
        "delete archive INDEX",
        "archive INDEX",
        "archive all",
        "restore INDEX",
        "restore all",
        "help",
        "bye"
    };

    /** Reads commands from standard input when running as a console app. */
    private final Scanner scanner;

    /** {@code true} to print framed console output; {@code false} for GUI replies only. */
    private final boolean isConsole;

    /** Accumulates the latest reply so the GUI can display it. */
    private final StringBuilder replyBuffer;

    /** Presentation style of the reply currently in {@code replyBuffer}. */
    private ReplyStyle replyStyle;

    /** Creates a UI that reads from standard input and prints to the console. */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI for the console or for collecting GUI replies.
     *
     * @param isConsole {@code true} to print framed console output.
     */
    public Ui(boolean isConsole) {
        this.isConsole = isConsole;
        this.scanner = isConsole ? new Scanner(System.in) : null;
        this.replyBuffer = new StringBuilder();
        this.replyStyle = ReplyStyle.NORMAL;
    }

    /** Prints the welcome banner and opening prompt. */
    public void showWelcome() {
        startFrame();
        if (isConsole) {
            System.out.println(BANNER);
        }
        appendLine("Good day. I'm Alfred, at your service.");
        appendLine("How may I assist you?");
        endFrame();
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        showReply("Until next time. I shall be here should you require me.");
    }

    /** Prints the compact list of commands Alfred understands. */
    public void showHelp() {
        startListFrame();
        appendLine("Certainly. These are the commands I understand:");
        for (String syntaxLine : HELP_SYNTAX_LINES) {
            appendLine(syntaxLine);
        }
        endFrame();
    }

    /**
     * Returns the next command line typed by the user.
     *
     * @return Raw command text, not including the trailing newline.
     */
    public String readCommand() {
        // readCommand is used only by the console loop, which constructs Ui with a scanner.
        assert scanner != null : "readCommand is only used in console mode";
        return scanner.nextLine();
    }

    /**
     * Returns the accumulated reply and clears the buffer.
     *
     * @return Latest chatbot reply, without console dividers or indent.
     */
    public String consumeReply() {
        String reply = replyBuffer.toString();
        replyBuffer.setLength(0);
        return reply;
    }

    public ReplyStyle getReplyStyle() {
        return replyStyle;
    }

    /**
     * Prints an error framed as a chatbot reply.
     *
     * @param message Explanation shown after the error prefix.
     */
    public void showError(String message) {
        showReply("I'm afraid I must report: " + message);
        replyStyle = ReplyStyle.ERROR;
    }

    /** Prints the message used when the save file cannot be read. */
    public void showLoadingError() {
        showError("I could not read your saved tasks, sir.");
    }

    /**
     * Confirms that {@code task} was added.
     *
     * @param task Newly stored task.
     * @param taskCount Number of tasks after the add.
     */
    public void showTaskAdded(Task task, int taskCount) {
        // AddCommand shows a confirmation only after inserting a task.
        assert task != null : "Added task should exist";
        assert taskCount >= 1 : "Adding a task leaves at least one item in the list";
        showReply("Very good. I've added this task:\n  "
                + task.getDisplayText() + "\n"
                + "You now have " + taskCount + " tasks in your list.");
    }

    /**
     * Confirms that {@code task} was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        // DeleteCommand shows a confirmation only after a successful removal.
        assert task != null : "Deleted task should exist";
        assert taskCount >= 0 : "Task count cannot be negative after a deletion";
        showReply("Noted. I've removed this task:\n  "
                + task.getDisplayText() + "\n"
                + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that {@code archivedCount} live tasks were archived.
     *
     * @param archivedCount Number of tasks just archived.
     * @param liveCount Number of live tasks remaining.
     */
    public void showTasksArchived(int archivedCount, int liveCount) {
        assert archivedCount >= 1 : "Archive confirmation is only shown after archiving a task";
        String reply = "Very good. I've archived " + archivedCount + " tasks.";
        if (liveCount == 0) {
            reply += " Your list is empty.";
        }
        showReply(reply);
    }

    /**
     * Confirms that {@code restoredCount} archived tasks were restored.
     *
     * @param restoredCount Number of tasks just restored.
     * @param liveCount Number of live tasks after the restore.
     */
    public void showTasksRestored(int restoredCount, int liveCount) {
        assert restoredCount >= 1 : "Restore confirmation is only shown after restoring a task";
        showReply("Very good. I've restored " + restoredCount + " tasks.\n"
                + "You now have " + liveCount + " tasks in your list.");
    }

    /**
     * Prints archived tasks in file order.
     *
     * @param tasks Archived tasks to display.
     */
    public void showArchivedTaskList(List<Task> tasks) {
        startListFrame();
        appendLine("Certainly. Here are the archived tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            appendNumberedTask(i, tasks.get(i));
        }
        if (tasks.isEmpty()) {
            appendLine("None, sir.");
        }
        endFrame();
    }

    /**
     * Confirms that {@code task} was marked or unmarked.
     *
     * @param task Updated task.
     * @param isDone {@code true} if the task was marked done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String response = isDone
                ? "Very good. I've marked this task as done:\n  "
                : "Certainly. I've marked this task as not done:\n  ";
        showReply(response + task.getDisplayText());
    }

    /**
     * Prints every task in insertion order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        startListFrame();
        appendLine("Certainly. Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendNumberedTask(i, tasks.get(i));
        }
        endFrame();
    }

    /**
     * Prints deadlines and events that fall on {@code query}'s calendar date.
     *
     * @param tasks Full task list, so original indices can be shown.
     * @param query Date typed after the {@code on} command.
     */
    public void showTasksOn(List<Task> tasks, TaskDateTime query) {
        // OnCommand is only created with a successfully parsed date.
        assert query != null : "Date query should already be parsed";
        LocalDate date = query.toLocalDate();
        startListFrame();
        appendLine("Certainly. Here are the deadlines and events on "
                + query.toDisplayDate() + ":");
        int matchCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                appendNumberedTask(i, tasks.get(i));
                matchCount++;
            }
        }
        if (matchCount == 0) {
            appendLine("None, sir.");
        }
        endFrame();
    }

    /**
     * Prints tasks whose descriptions matched a {@code find} keyword.
     * Matches are numbered from 1 in the order they appear in the list.
     *
     * @param tasks Matching tasks to display.
     */
    public void showFoundTasks(List<Task> tasks) {
        startListFrame();
        appendLine("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendNumberedTask(i, tasks.get(i));
        }
        if (tasks.isEmpty()) {
            appendLine("None, sir.");
        }
        endFrame();
    }

    /**
     * Appends a task as {@code n.displayText}, converting {@code zeroBasedIndex} to a 1-based
     * list number.
     */
    private void appendNumberedTask(int zeroBasedIndex, Task task) {
        int displayNumber = zeroBasedIndex + USER_NUMBERING_OFFSET;
        appendLine(String.format("%2d.", displayNumber) + task.getDisplayText());
    }

    /** Frames a single chatbot reply between divider lines. */
    private void showReply(String message) {
        startFrame();
        String[] lines = message.split("\n", -1);
        for (String line : lines) {
            appendLine(line);
        }
        endFrame();
    }

    private void startFrame() {
        replyStyle = ReplyStyle.NORMAL;
        if (isConsole) {
            System.out.println(LINE);
        }
    }

    /** Starts a framed reply that the GUI should render as a numbered list. */
    private void startListFrame() {
        startFrame();
        replyStyle = ReplyStyle.LIST;
    }

    private void endFrame() {
        if (isConsole) {
            System.out.println(LINE);
        }
    }

    private void appendLine(String line) {
        if (replyBuffer.length() > 0) {
            replyBuffer.append('\n');
        }
        replyBuffer.append(line);
        if (isConsole) {
            System.out.println(INDENT + line);
        }
    }
}
