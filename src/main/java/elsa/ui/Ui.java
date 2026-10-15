package elsa.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import elsa.Dates;
import elsa.task.Reminder;
import elsa.task.Task;
import elsa.task.TaskList;

/**
 * Everything the chatbot says to the user and reads back from them.
 *
 * <p>This class is the only one that words what the chatbot says. The rest of
 * the program decides <em>what</em> has happened and leaves this class to decide
 * <em>how</em> it is put, so a change to the wording, the borders or the
 * greeting is made here and nowhere else.
 *
 * <p>Wording a message and showing it are separate. Every {@code get...Message}
 * method returns text and displays nothing, because the chatbot is spoken to
 * through two faces: a terminal, which prints the text between borders through
 * {@link #show}, and a window, which puts the same text in a dialog box. Only
 * the terminal reads from the keyboard, so {@link #readCommand} stays here too.
 */
public class Ui {
    /**
     * Horizontal line that separates one message block from the next.
     * Drawn as a row of ASCII "snowflakes" to suit the chatbot's name.
     * String.repeat builds the row so the width is stated once, as a number.
     */
    private static final String BORDER = "   " + " *".repeat(30);

    /** Indentation applied to every line of message text inside a block. */
    private static final String INDENT = "     ";

    /** Prefix added to every error message shown to the user. */
    private static final String ERROR_PREFIX = "OLAF!!! ";

    // Each "\\" in the source produces a single backslash in the ASCII-art banner.
    private static final String BANNER = " _____ _           \n"
            + "|  ___| |___  __ _ \n"
            + "| |__ | / __|/ _` |\n"
            + "|  __|| \\__ \\ (_| |\n"
            + "|_____|_|___/\\__,_|";

    private static final String GREETING = "Hello! I'm Elsa.\n"
            + "Do you want to build a snowman?";

    private static final String FAREWELL = "The cold never bother me anyways!";

    /** Shown in place of the list when there is nothing in it. */
    private static final String EMPTY_LIST = "Into the Unknown.";

    /**
     * Where the user's typing is read from. Held as a field so that the one
     * Scanner lasts for the whole session; a new one per line would work but
     * would keep opening a fresh reader on the same stream.
     */
    private final Scanner scanner;

    /** Creates a user interface that reads from the keyboard and writes to the screen. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the user has typed another line.
     * This is false when the input ends without a "bye", which happens when the
     * chatbot is fed a file of commands rather than being typed at.
     *
     * @return true if there is another line to read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line the user typed, without the spaces around it.
     *
     * @return the line, trimmed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Returns the banner and greeting that open a session in the terminal.
     *
     * @return the opening message, banner first.
     */
    public String getWelcomeMessage() {
        return BANNER + "\n" + GREETING;
    }

    /**
     * Returns the greeting that opens a session in the window.
     *
     * <p>The banner is left out. It is drawn out of punctuation, which lines up
     * only in a font whose characters are all one width; a window's font is not,
     * so the banner would arrive crooked and is left to the terminal.
     *
     * <p>The window has no menu, so a first-time user needs telling where to
     * look. It points at "help" rather than listing the commands itself, because
     * a wall of usage lines is a poor first thing to meet and is only wanted
     * once.
     *
     * @return the opening message.
     */
    public String getGreetingMessage() {
        return GREETING + "\n\nType \"help\" to see what I can do.";
    }

    /**
     * Returns how every command is written, and how to write a date.
     *
     * <p>Four of the usage lines end in a date without saying what one looks
     * like, so the forms a date may take are named underneath. They are taken
     * from {@link Dates}, which is also what refuses a date it cannot read, so
     * the two cannot come to disagree about what is accepted.
     *
     * <p>The commands are shown in groups rather than as one list of twelve.
     * A list that long is read by scanning it for the one line that matters, and
     * a heading every few lines is what makes that scan short: a user who wants
     * to put something in has three lines to read instead of twelve.
     *
     * @param usagesByGroup how each command is written, under its heading
     * @return the help text.
     */
    public String getHelpMessage(Map<String, List<String>> usagesByGroup) {
        String groups = usagesByGroup.entrySet().stream()
                .map(group -> group.getKey() + group.getValue().stream()
                        .map(usage -> "\n  " + usage)
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n\n"));
        return "Here is what you can ask me:\n\n" + groups
                + "\n\nWrite a date as " + Dates.ACCEPTED_FORMS + ".";
    }

    /**
     * Returns the parting message.
     *
     * @return what the chatbot says as the session ends.
     */
    public String getFarewellMessage() {
        return FAREWELL;
    }

    /**
     * Returns something the chatbot could not do, marked as a complaint.
     *
     * @param message the explanation, written for the user
     * @return the explanation behind the chatbot's complaint prefix.
     */
    public String getErrorMessage(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Returns the confirmation shown after a task has been added.
     *
     * @param task      the task that was just added
     * @param taskCount how many tasks are in the list now
     * @return the confirmation text.
     */
    public String getAddedMessage(Task task, int taskCount) {
        return taskCountMessage("Frozen in place. I've added this task:", task, taskCount);
    }

    /**
     * Returns the confirmation shown after a task has been removed.
     *
     * @param task      the task that was just removed
     * @param taskCount how many tasks are left in the list
     * @return the confirmation text.
     */
    public String getRemovedMessage(Task task, int taskCount) {
        return taskCountMessage("Melted away. I've removed this task:", task, taskCount);
    }

    /**
     * Returns the confirmation shown after a task has been marked done.
     *
     * @param task the task that was marked
     * @return the confirmation text.
     */
    public String getMarkedMessage(Task task) {
        return "Let it go! I've marked this task as done:\n  " + task;
    }

    /**
     * Returns the confirmation shown after a task has been marked not done.
     *
     * @param task the task that was unmarked
     * @return the confirmation text.
     */
    public String getUnmarkedMessage(Task task) {
        return "Back into the cold. I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Returns the whole task list, numbered from 1.
     *
     * @param tasks the stored tasks, in the order they were added
     * @return the numbered list, or a stand-in line if there is nothing in it.
     */
    public String getTasksMessage(TaskList tasks) {
        // Every task belongs in this list, so the test accepts all of them and
        // "nothing was wanted" and "the list is empty" come to the same thing.
        return listTasks(tasks, task -> true,
                "Here are the tasks in your list:", EMPTY_LIST);
    }

    /**
     * Returns the tasks falling on one date.
     *
     * @param tasks the stored tasks, in the order they were added
     * @param date  the date being asked about
     * @return the matching tasks, or a line saying there are none.
     */
    public String getTasksOnMessage(TaskList tasks, LocalDate date) {
        // What falling on a date means differs by kind of task; see
        // Task.occursOn(), which deadlines and events answer differently.
        return listTasks(tasks, task -> task.occursOn(date),
                "Here are the tasks on " + Dates.format(date) + ":",
                "Nothing on " + Dates.format(date) + ".");
    }

    /**
     * Returns the tasks whose description contains a keyword.
     *
     * @param tasks    the stored tasks, in the order they were added
     * @param keywords the texts being searched for, one or more
     * @return the matching tasks, or a line saying there are none.
     */
    public String getMatchingTasksMessage(TaskList tasks, String... keywords) {
        // Only the description is searched, not the dates or the type marker;
        // see Task.matches().
        return listTasks(tasks, task -> task.matches(keywords),
                "Here are the matching tasks in your list:",
                "Nothing matching " + quoteAll(keywords) + ".");
    }

    /**
     * Returns the unfinished deadlines that need the user's attention, soonest
     * first, or a line saying there are none.
     *
     * <p>Each deadline keeps the number it has in the full list, as in the other
     * listings, so that it can be given straight to "mark" or "delete".
     *
     * @param reminders the deadlines to show, already in the order to show them
     * @param daysAhead how far ahead they were gathered, named when there are none
     * @return the reminders, or a line saying nothing needs attention.
     */
    public String getRemindersMessage(List<Reminder> reminders, int daysAhead) {
        if (reminders.isEmpty()) {
            return "Nothing is overdue, and nothing is due in the next " + daysAhead + " days.";
        }
        String lines = reminders.stream()
                .map(reminder -> "  " + numbered(reminder.index(), reminder.deadline())
                        + dueNote(reminder.daysUntilDue()))
                .collect(Collectors.joining("\n"));
        return "Here is what needs your attention:\n" + lines;
    }

    /**
     * Returns the note saying how soon a reminded deadline is due.
     *
     * <p>An overdue deadline gets no note here, because its own text already ends
     * with " -- overdue", and saying it twice would read as two separate facts.
     *
     * @param daysUntilDue how many days until the deadline is due; negative when overdue
     * @return the note, beginning with its separator, or the empty string
     */
    private static String dueNote(long daysUntilDue) {
        if (daysUntilDue < 0) {
            return "";
        }
        if (daysUntilDue == 0) {
            return " -- due today";
        }
        if (daysUntilDue == 1) {
            return " -- due tomorrow";
        }
        return " -- due in " + daysUntilDue + " days";
    }

    /**
     * Returns the tasks a test accepts, numbered and under a heading, or a
     * stand-in line when it accepts none.
     *
     * <p>The three methods above ask for different tasks, call the list
     * different things and say something different when it comes out empty.
     * Beyond that they want the same list built the same way, so it is built
     * here and each of them supplies only the three parts that are its own.
     *
     * <p>Each task keeps the number it has in the full list rather than being
     * renumbered from 1, so that a number read off any of these lists can be
     * given straight to "mark" or "delete". Renumbering would make those
     * commands act on the wrong task, because they count positions in the whole
     * list.
     *
     * @param tasks    the stored tasks, in the order they were added
     * @param isWanted decides whether a task belongs in this list.
     * @param heading  the line introducing the list.
     * @param ifNone   what to say instead when no task is wanted.
     * @return the heading followed by the numbered tasks, or ifNone.
     */
    private static String listTasks(TaskList tasks, Predicate<Task> isWanted,
            String heading, String ifNone) {
        // Streamed over positions rather than tasks, because the position is what
        // has to survive the filtering to become the number shown.
        String lines = IntStream.range(0, tasks.size())
                .filter(i -> isWanted.test(tasks.get(i)))
                .mapToObj(i -> numbered(i, tasks.get(i)))
                .collect(Collectors.joining("\n"));
        return lines.isEmpty() ? ifNone : heading + "\n" + lines;
    }

    /**
     * Returns the keywords in quotation marks, run together as English rather
     * than as a list, so that the complaint reads as a sentence: one on its own,
     * two joined by "or", and more than two separated by commas until the last.
     *
     * @param keywords the texts that were searched for
     * @return the keywords quoted and joined
     */
    private static String quoteAll(String... keywords) {
        assert keywords.length > 0 : "the complaint has to name a keyword";
        StringBuilder quoted = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                quoted.append(i == keywords.length - 1 ? " or " : ", ");
            }
            quoted.append("\"").append(keywords[i]).append("\"");
        }
        return quoted.toString();
    }

    /**
     * Returns a warning naming the lines of the data file that could not be read.
     *
     * @param problems one message per line that could not be understood
     * @param fileName the file the lines came from, named so the user can go and fix it
     * @return the warning, worded as a complaint.
     */
    public String getSkippedLinesMessage(ArrayList<String> problems, String fileName) {
        assert !problems.isEmpty() : "nothing was skipped, so there is nothing to warn about";
        String plural = (problems.size() == 1) ? "line" : "lines";
        String them = (problems.size() == 1) ? "it" : "them";
        String listed = problems.stream()
                .map(problem -> "\n  " + problem)
                .collect(Collectors.joining());
        // Said plainly, because the next change to the list rewrites the file.
        String message = "I could not understand " + problems.size() + " " + plural
                + " of " + fileName + ", so I have left " + them + " out:" + listed
                + "\nYour other tasks loaded normally. Saving will rewrite the file"
                + " without the " + plural + " above, so edit the file now if you"
                + " want to keep " + them + ".";
        return getErrorMessage(message);
    }

    /**
     * Returns one numbered line of a task listing.
     *
     * @param index the task's position in the full list, counted from 0
     * @param task  the task to show
     * @return the display number, a full stop, and the task
     */
    private static String numbered(int index, Task task) {
        // List indices start at 0, but the display numbering starts at 1.
        return (index + 1) + "." + task;
    }

    /**
     * Builds the confirmation shown after the list has gained or lost a task.
     *
     * @param lead      the opening line saying what happened
     * @param task      the task that was added or removed
     * @param taskCount how many tasks are in the list now
     * @return the confirmation text, spanning three lines
     */
    private static String taskCountMessage(String lead, Task task, int taskCount) {
        String plural = (taskCount == 1) ? "task" : "tasks";
        return lead + "\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " " + plural + " in the list.";
    }

    /**
     * Prints a message enclosed between two horizontal borders, indenting each
     * line so that it lines up inside the block.
     * Only the terminal shows messages this way. The window draws its own border
     * round each dialog box, and is handed the same text undecorated.
     *
     * @param message the text to display; may span several lines separated by "\n"
     */
    public void show(String message) {
        System.out.println(BORDER);
        for (String line : message.split("\n")) {
            System.out.println(INDENT + line);
        }
        System.out.println(BORDER);
        System.out.println();
    }
}
