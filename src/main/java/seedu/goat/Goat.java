package seedu.goat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seedu.goat.command.Command;
import seedu.goat.parser.Parser;
import seedu.goat.storage.Storage;
import seedu.goat.task.Task;
import seedu.goat.task.TaskList;
import seedu.goat.ui.Ui;

/**
 * The GOAT chatbot, independent of how the user reaches it.
 * <p>
 * Every command handler returns its reply as text instead of printing it, so one chatbot
 * can back both the text interface driven by {@link #run()} and the JavaFX window that
 * calls {@link #getResponse(String)}. {@code todo}, {@code deadline} and {@code event}
 * add tasks, {@code mark} and {@code unmark} change their status, {@code delete} removes
 * one, {@code list} shows them and {@code bye} ends the conversation.
 */
public class Goat {

    /** Where the task list is saved when no other path is given. */
    public static final String DEFAULT_SAVE_PATH = "data/goat.txt";

    /** The bot's name, kept in one place so every message stays consistent. */
    private static final String NAME = "GOAT";

    /** The parting message, shown however the conversation ends. */
    private static final String GOODBYE = "Bye. Hope to see you again soon!";

    /** Handles all reading from and writing to the user. */
    private final Ui ui;

    /** Handles reading and writing the save file. */
    private final Storage storage;

    /** The tasks the user is tracking. */
    private TaskList tasks;

    /**
     * Message describing why loading failed, or null if it did not.
     * <p>
     * Held rather than reported immediately so that the greeting still comes first: being
     * shown an error before GOAT has said hello reads badly.
     */
    private String loadingError;

    /** Whether the user has said goodbye, which ends the conversation. */
    private boolean isFinished;

    /**
     * Creates a chatbot backed by the given save file.
     * <p>
     * A save file that cannot be read leaves GOAT running with an empty list rather than
     * refusing to start. Storage disables changes until the original file is repaired.
     *
     * @param filePath relative path to the save file.
     */
    public Goat(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (GoatException e) {
            this.loadingError = e.getMessage();
            this.tasks = new TaskList();
        }
        assert tasks != null : "A failed load must still leave an empty list to work with";
    }

    /** Greets the user, then handles typed commands until the conversation ends. */
    public void run() {
        ui.showBanner();
        ui.show(getGreeting());

        String loadOutcome = getLoadOutcome();
        if (!loadOutcome.isEmpty()) {
            ui.show(loadOutcome);
        }

        while (!isFinished && ui.hasNextCommand()) {
            ui.show(getResponse(ui.readCommand()));
        }

        if (!isFinished) {
            // Input ran out before a bye, as happens when a file is piped in. Sign off
            // anyway so that the transcript ends the same way either route.
            ui.show(GOODBYE);
        }
    }

    /**
     * Starts GOAT's text interface with the default save file.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        new Goat(DEFAULT_SAVE_PATH).run();
    }

    /**
     * Returns the opening message.
     *
     * @return the greeting, as two lines.
     */
    public String getGreeting() {
        return reply("Hello! I'm " + NAME, "What can I do for you?");
    }

    /**
     * Returns what happened when the save file was read.
     *
     * @return the message to show, or an empty string if there is nothing worth saying.
     */
    public String getLoadOutcome() {
        if (loadingError != null) {
            return loadingError;
        }

        int skipped = storage.getSkippedLineCount();
        if (skipped > 0) {
            return reply(
                    "Some of your save file was unreadable, so I skipped " + skipped
                            + (skipped == 1 ? " line." : " lines."),
                    "The " + tasks.size() + " tasks I could read are in your list.",
                    "Changes are disabled. Back up and repair the original file, then restart GOAT.");
        }
        if (!tasks.isEmpty()) {
            return "Welcome back. I restored " + tasks.size()
                    + (tasks.size() == 1 ? " task" : " tasks") + " from your last session.";
        }
        return "";
    }

    /**
     * Runs one command and returns the reply.
     * <p>
     * Input GOAT refuses comes back as ordinary reply text rather than as an exception,
     * because both interfaces want to show the complaint and carry on rather than stop.
     *
     * @param input one line as typed by the user.
     * @return the text to show in response.
     */
    public String getResponse(String input) {
        try {
            Parser.ParsedCommand parsed = Parser.parse(input);
            Command command = parsed.command();
            String arguments = parsed.arguments();

            return switch (command) {
            case BYE -> {
                isFinished = true;
                yield GOODBYE;
            }
            case LIST -> listTasks();
            case MARK -> markTask(Parser.parseTaskNumber(arguments, command));
            case UNMARK -> unmarkTask(Parser.parseTaskNumber(arguments, command));
            case DELETE -> deleteTask(Parser.parseTaskNumber(arguments, command));
            case ON -> listTasksOn(Parser.parseDate(arguments));
            case FIND -> findTasks(Parser.parseKeyword(arguments));
            case TODO -> addTask(Parser.parseTodo(arguments));
            case DEADLINE -> addTask(Parser.parseDeadline(arguments));
            case EVENT -> addTask(Parser.parseEvent(arguments));
            };
        } catch (GoatException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns whether the conversation has been ended by a {@code bye}.
     *
     * @return true once a bye has been handled.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Joins the lines of a reply into the single block of text a reply is made of.
     * <p>
     * Varargs rather than an array parameter because every caller below knows its lines
     * at the point of the call, so they can be written out in place instead of being
     * packed into an array first. Replies whose length depends on the task list build a
     * list and join that instead, since varargs would buy them nothing.
     *
     * @param replyLines the lines, in the order they should appear.
     * @return the lines separated by newlines.
     */
    private static String reply(String... replyLines) {
        return String.join("\n", replyLines);
    }

    /**
     * Stores a new task and confirms it, along with the new task count.
     *
     * @param task the task to store.
     * @return the confirmation to show.
     * @throws GoatException if the updated list cannot be saved.
     */
    private String addTask(Task task) throws GoatException {
        assert task != null : "The parser returns a task or throws; it never returns null";

        int duplicate = tasks.indexOfDuplicate(task);
        if (duplicate >= 0) {
            // Refused the same way as any other unusable input, so that both the console
            // and the window show the complaint and carry on.
            throw new GoatException(reply("You already have this task:",
                    "  " + (duplicate + 1) + "." + tasks.get(duplicate)));
        }

        List<Task> updated = new ArrayList<>(tasks.asList());
        updated.add(task);
        storage.save(updated);
        tasks = new TaskList(updated);
        return reply("Got it. I've added this task:", "  " + task, taskCountSummary());
    }

    /**
     * Removes a task from the list and reports what was removed.
     *
     * @param taskNumber the position shown by {@code list}, counting from 1.
     * @return the confirmation to show.
     * @throws GoatException if the updated list cannot be saved.
     */
    private String deleteTask(int taskNumber) throws GoatException {
        // delete() returns the removed element, so the confirmation can show the task
        // even though it is no longer in the list. Later tasks shift down by one, which
        // is why list renumbers them automatically.
        List<Task> updated = new ArrayList<>(tasks.asList());
        Task removed = updated.remove(toIndex(taskNumber, Command.DELETE));
        storage.save(updated);
        tasks = new TaskList(updated);
        return reply("Noted. I've removed this task:", "  " + removed, taskCountSummary());
    }

    /**
     * Returns the sentence reporting how many tasks are now stored.
     *
     * @return text such as {@code Now you have 3 tasks in the list.}
     */
    private String taskCountSummary() {
        int count = tasks.size();
        return "Now you have " + count + (count == 1 ? " task" : " tasks") + " in the list.";
    }

    /**
     * Converts a task number the user typed into a position in the list.
     * <p>
     * The number itself was already checked by {@link Parser}; what is checked here is
     * whether it refers to a task that actually exists, which only the list can say.
     *
     * @param taskNumber the position the user typed, counting from 1.
     * @param command the command being run, named in the error messages.
     * @return the matching zero-based index.
     * @throws GoatException if the list is empty or the number is out of range.
     */
    private int toIndex(int taskNumber, Command command) throws GoatException {
        if (tasks.isEmpty()) {
            throw new GoatException("Your list is empty, so there is nothing to "
                    + command.getKeyword() + " yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GoatException("There is no task " + taskNumber + ". Pick a number"
                    + " from 1 to " + tasks.size() + ".");
        }

        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size() : "Only positions that exist survive the checks";
        return index;
    }

    /**
     * Marks a task as done and echoes it back.
     *
     * @param taskNumber the position shown by {@code list}, counting from 1.
     * @return the confirmation to show.
     * @throws GoatException if the updated list cannot be saved.
     */
    private String markTask(int taskNumber) throws GoatException {
        int index = toIndex(taskNumber, Command.MARK);
        Task task = tasks.get(index);
        saveStatus(task, true);
        return reply("Nice! I've marked this task as done:", "  " + task);
    }

    /**
     * Marks a task as not done and echoes it back.
     *
     * @param taskNumber the position shown by {@code list}, counting from 1.
     * @return the confirmation to show.
     * @throws GoatException if the updated list cannot be saved.
     */
    private String unmarkTask(int taskNumber) throws GoatException {
        int index = toIndex(taskNumber, Command.UNMARK);
        Task task = tasks.get(index);
        saveStatus(task, false);
        return reply("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Lists the dated tasks that fall on a given date.
     * <p>
     * A deadline matches when it is due that day; an event matches when the date lies
     * anywhere in its span. To-dos carry no date and so never match.
     *
     * @param date the date to query.
     * @return the matching tasks, numbered, or a note that nothing is scheduled.
     */
    private String listTasksOn(LocalDate date) {
        List<Task> matches = tasks.findOn(date);
        if (matches.isEmpty()) {
            return "Nothing is scheduled on " + DateFormats.format(date) + ".";
        }

        return numberedList("Here is what you have on " + DateFormats.format(date) + ":", matches);
    }

    /**
     * Lists the tasks whose description contains a keyword.
     *
     * @param keyword the text the user is looking for.
     * @return the matching tasks, numbered, or a note that nothing matched.
     */
    private String findTasks(String keyword) {
        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "No task in your list mentions \"" + keyword + "\".";
        }

        return numberedList("Here are the matching tasks in your list:", matches);
    }

    /**
     * Lists every stored task, numbered from 1, with its completion status.
     *
     * @return the tasks, numbered, under a heading.
     */
    private String listTasks() {
        return numberedList("Here are the tasks in your list:", tasks.asList());
    }

    /**
     * Returns a heading with tasks listed beneath it, numbered from 1.
     * <p>
     * The three listing commands differ only in their heading and in which tasks they
     * select, so the numbering lives here instead of being written out in each of them.
     * Numbering from the loop counter also states the position directly, where building
     * it from the length of the lines so far only worked while the heading was present.
     *
     * @param heading the line introducing the list.
     * @param tasksToShow the tasks to list, in the order they should appear.
     * @return the heading followed by one numbered task per line.
     */
    private static String numberedList(String heading, List<Task> tasksToShow) {
        List<String> lines = new ArrayList<>();
        lines.add(heading);
        for (int i = 0; i < tasksToShow.size(); i++) {
            lines.add((i + 1) + "." + tasksToShow.get(i));
        }
        return String.join("\n", lines);
    }
    /** Saves a status change, restoring the previous status if persistence fails. */
    private void saveStatus(Task task, boolean isDone) throws GoatException {
        boolean wasDone = task.isDone();
        task.setDone(isDone);
        try {
            storage.save(tasks.asList());
        } catch (GoatException e) {
            task.setDone(wasDone);
            throw e;
        }
    }

}
