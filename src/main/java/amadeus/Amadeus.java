package amadeus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import amadeus.parser.Parser;
import amadeus.storage.Storage;
import amadeus.task.Task;
import amadeus.task.TaskDateTime;
import amadeus.ui.Ui;

/**
 * The Amadeus chatbot itself: the task list and the rules for changing it.
 * <p>
 * This class understands what each command should do, and delegates how it is done -
 * parsing to {@link Parser}, saving and loading to {@link Storage}. What it deliberately
 * does <em>not</em> do is decide how a reply reaches the user: {@link #getResponse(String)}
 * hands back the reply as text, so the same logic drives both the console loop in
 * {@link #main(String[])} and the JavaFX window in {@link amadeus.ui.MainWindow}.
 * <p>
 * An {@link AmadeusException} raised by any command is caught here and returned as the
 * reply, so a mistyped command produces a message rather than ending the session.
 */
public class Amadeus {

    private static final int MAX_TASKS = 100;

    /**
     * Where the task list is kept between runs.
     * The folder and the file name are separate so that Storage can join them
     * with whatever separator the current operating system uses, and the path is
     * relative to the folder the app is started from so it works on any computer.
     */
    private static final String DATA_FOLDER = "data";
    private static final String DATA_FILE = "amadeus.txt";

    private final Storage storage;
    private final List<Task> tasks;

    /**
     * Anything worth telling the user about the save file, collected while loading.
     * Kept as a field because loading happens in the constructor but the messages are
     * only shown once the user interface is ready to display them.
     */
    private final List<String> loadMessages = new ArrayList<>();

    /** Whether the user has asked to quit. Read by the window so it knows to close. */
    private boolean isExit = false;

    /**
     * Whether the last thing the chatbot was asked to do reported a problem.
     * <p>
     * Set by {@link #getResponse(String)} for a command that failed, and by the
     * constructor for a save file that could not be read, so that both reach the user
     * the same way. The window reads it straight after asking for the text, and shows
     * that text as an error instead of as an ordinary reply.
     * <p>
     * A flag beside the text is the smaller of the two obvious designs: the alternative
     * is to return the text and the flag together in one object (a record), which is
     * tidier in that it cannot be read out of order, but would change the signature
     * every caller uses - including the console loop, which has no use for the flag.
     */
    private boolean isError = false;

    /**
     * Creates a chatbot whose task list is loaded from the save file.
     * <p>
     * A missing or damaged file is not fatal: the session simply starts with whatever
     * could be read, and the reason is recorded for {@link #getWelcome()} to report.
     */
    public Amadeus() {
        this.storage = new Storage(DATA_FOLDER, DATA_FILE);

        List<Task> loaded = new ArrayList<>();
        try {
            loaded = storage.load();
            loadMessages.addAll(storage.getLoadWarnings());
            // Skipped lines mean tasks the user saved but will not get back, which is
            // worth the same attention as a mistyped command rather than less.
            isError = !storage.getLoadWarnings().isEmpty();
            if (!loaded.isEmpty()) {
                loadMessages.add("I've loaded " + loaded.size() + " task(s) from your last session.");
            }
        } catch (AmadeusException e) {
            // The list could not be read at all, so the session starts empty rather
            // than stopping; the user is told why.
            loadMessages.add(e.getMessage());
            isError = true;
        }
        this.tasks = loaded;
    }

    /**
     * Starts the chatbot in the console and runs it until the user types "bye"
     * or the input ends.
     *
     * @param args command line arguments; not used, as the save file location is fixed.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Amadeus amadeus = new Amadeus();

        ui.showWelcome();
        if (!amadeus.loadMessages.isEmpty()) {
            ui.showReply(String.join("\n", amadeus.loadMessages));
            ui.showLine();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            ui.showReply(amadeus.getResponse(input));
            ui.showLine();

            if (amadeus.isExit()) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Returns the greeting shown before the user has typed anything, including any
     * notes about the save file.
     */
    public String getWelcome() {
        List<String> lines = new ArrayList<>();
        lines.add("Hello, I'm Amadeus, sir.");
        lines.add("Sir, what do you need assistance with?");
        lines.addAll(loadMessages);
        return String.join("\n", lines);
    }

    /**
     * Returns true once the user has entered the "bye" command.
     * <p>
     * The window uses this to close itself, which the console loop does by simply
     * running out of commands.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns true if the text just handed back reported a problem rather than a result.
     * <p>
     * Asked by the window immediately after {@link #getResponse(String)} or
     * {@link #getWelcome()}, so that a mistyped command is shown in a format that
     * catches the eye instead of looking like every other reply. The console interface
     * ignores it, because a console has no formatting to switch between.
     *
     * @return true if the last reply was about something going wrong.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Runs one command and returns what the chatbot has to say about it.
     * <p>
     * This is the single entry point for both user interfaces. Every way the input can
     * be wrong ends in an {@link AmadeusException}, which is caught here and turned into
     * the reply, so the caller never has to handle failure itself.
     *
     * @param input the whole line the user typed, exactly as typed.
     * @return the reply, possibly several lines separated by {@code \n}.
     */
    public String getResponse(String input) {
        List<String> lines = new ArrayList<>();
        // Cleared for every command, so the flag always describes this reply and never
        // a previous one.
        isError = false;

        // One try/catch around the whole command covers every way the input can be
        // wrong, so each branch below can assume its input is valid.
        try {
            String commandWord = Parser.parseCommandWord(input);

            switch (commandWord) {
                case "bye":
                    isExit = true;
                    lines.add("Buh bye, sir");
                    break;

                case "list":
                    lines.add("Here are the " + tasks.size() + " task(s) in your list, sir:");
                    addTaskList(lines, tasks);
                    break;

                case "on":
                    lines.addAll(handleOn(input));
                    break;

                case "find":
                    lines.addAll(handleFind(input));
                    break;

                case "mark":
                    lines.addAll(handleMark(input));
                    break;

                case "delete":
                    lines.addAll(handleDelete(input));
                    break;

                case "unmark":
                    lines.addAll(handleUnmark(input));
                    break;

                case "todo":
                case "deadline":
                case "event":
                    lines.addAll(handleNewTask(commandWord, input));
                    break;

                default:
                    throw new AmadeusException("A million apologies, I don't know what '"
                            + commandWord + "' means, sir.");
            }
        } catch (AmadeusException e) {
            // The exception message is written to be read by the user, so it can
            // simply become the reply.
            lines.add(e.getMessage());
            isError = true;
        }

        return String.join("\n", lines);
    }

    /**
     * Handles the "on" command: reports every task that falls on the given day.
     * Todos have no date, so {@link Task#occursOn(LocalDate)} simply answers no for them.
     *
     * @throws AmadeusException if the date is missing or cannot be understood.
     */
    private List<String> handleOn(String input) throws AmadeusException {
        LocalDate date = Parser.parseDate(input);
        return describeMatches(task -> task.occursOn(date),
                count -> "Here are the " + count + " task(s) on " + TaskDateTime.format(date) + ", sir:",
                "Nothing is happening on " + TaskDateTime.format(date) + ", sir.");
    }

    /**
     * Handles the "find" command: reports every task whose description contains the
     * keyword, mirroring how {@link #handleOn(String)} asks every task about a date.
     *
     * @throws AmadeusException if no keyword was given.
     */
    private List<String> handleFind(String input) throws AmadeusException {
        String keyword = Parser.parseKeyword(input);
        return describeMatches(task -> task.descriptionContains(keyword),
                count -> "Here are the matching tasks in your list, sir:",
                "I found no tasks matching '" + keyword + "', sir.");
    }

    /**
     * Tests every task against the given predicate and returns the reply: either the
     * given message when nothing matched, or the given header - built from how many
     * did - followed by the matches as a numbered list.
     * <p>
     * {@link #handleOn(String)} and {@link #handleFind(String)} both reduce to exactly
     * this shape - test every task against a question, then either say nothing
     * matched or show what did - so the shape is written once here instead of once
     * per command.
     *
     * @param predicate    decides whether a task matches.
     * @param header       builds the line shown above the list, given how many matched.
     * @param emptyMessage shown instead when nothing matched.
     */
    private List<String> describeMatches(Predicate<Task> predicate, IntFunction<String> header,
            String emptyMessage) {
        List<Task> matches = tasks.stream()
                .filter(predicate)
                .toList();

        List<String> lines = new ArrayList<>();
        if (matches.isEmpty()) {
            lines.add(emptyMessage);
        } else {
            lines.add(header.apply(matches.size()));
            addTaskList(lines, matches);
        }
        return lines;
    }

    /**
     * Handles the "mark" command: marks the given task as done.
     *
     * @throws AmadeusException if the task number is missing, not a number, or out of range.
     */
    private List<String> handleMark(String input) throws AmadeusException {
        Task task = getTaskByNumber(input);
        task.markAsDone();
        List<String> lines = new ArrayList<>();
        save(lines);
        lines.add("Fantastic! I've marked this task as done, sir:");
        addTask(lines, task);
        return lines;
    }

    /**
     * Handles the "unmark" command: marks the given task as not yet done.
     *
     * @throws AmadeusException if the task number is missing, not a number, or out of range.
     */
    private List<String> handleUnmark(String input) throws AmadeusException {
        Task task = getTaskByNumber(input);
        task.markAsNotDone();
        List<String> lines = new ArrayList<>();
        save(lines);
        lines.add("OK, it has been marked as undone, sir:");
        addTask(lines, task);
        return lines;
    }

    /**
     * Handles the "delete" command: removes the given task from the list.
     *
     * @throws AmadeusException if the task number is missing, not a number, or out of range.
     */
    private List<String> handleDelete(String input) throws AmadeusException {
        Task removed = getTaskByNumber(input);
        tasks.remove(removed);
        List<String> lines = new ArrayList<>();
        save(lines);
        lines.add("Fantastic! I've removed this task, sir:");
        addTask(lines, removed);
        lines.add("Now you have " + tasks.size() + " task(s) in your list, sir");
        return lines;
    }

    /**
     * Reads the task number out of a "mark"/"unmark"/"delete" command and returns the
     * task it refers to.
     * <p>
     * All three commands start by parsing that number and looking up the same task
     * before going on to do their own thing with it, so that shared first step is
     * written once here instead of three times.
     *
     * @throws AmadeusException if the number is missing, not a number, or out of range.
     */
    private Task getTaskByNumber(String input) throws AmadeusException {
        int index = Parser.parseTaskIndex(input, tasks.size());
        // parseTaskIndex() already checked the number the user typed against
        // tasks.size(), so index is guaranteed to be in range here; the get() below
        // cannot throw IndexOutOfBoundsException. The assertion makes that guarantee
        // explicit instead of leaving it as something the reader has to trust by
        // re-reading parseTaskIndex().
        assert index >= 0 && index < tasks.size()
                : "parseTaskIndex() should return an index within [0, tasks.size())";
        return tasks.get(index);
    }

    /**
     * Handles the "todo", "deadline" and "event" commands: parses and adds the new task.
     *
     * @param commandWord which of the three commands this is.
     * @param input       the whole line the user typed.
     * @throws AmadeusException if the list is already full, or the line is malformed.
     */
    private List<String> handleNewTask(String commandWord, String input) throws AmadeusException {
        if (tasks.size() == MAX_TASKS) {
            throw new AmadeusException("My list is full, a thousand apologies, sir.");
        }

        // The parser builds the right kind of Task and throws if the line is
        // malformed, so nothing is stored on a bad command.
        Task task;
        if (commandWord.equals("todo")) {
            task = Parser.parseTodo(input);
        } else if (commandWord.equals("deadline")) {
            task = Parser.parseDeadline(input);
        } else {
            task = Parser.parseEvent(input);
        }

        tasks.add(task);
        // The guard above stops a task being added once the list is already at
        // MAX_TASKS, so from this method alone the list can never grow past it.
        // (It does not account for a save file edited by hand to already hold more
        // than MAX_TASKS tasks - Storage.load() applies no such limit - which is a
        // real gap in the guard above; the assertion is left in place specifically
        // so that case would be caught during testing rather than pass silently.)
        assert tasks.size() <= MAX_TASKS : "task list grew past MAX_TASKS";
        List<String> lines = new ArrayList<>();
        save(lines);
        lines.add("Got it added, sir:");
        addTask(lines, task);
        lines.add("Now you have " + tasks.size() + " task(s) in your list, sir");
        return lines;
    }

    /**
     * Appends one task on its own indented line, the way it appears in confirmation
     * messages such as "Got it added:".
     */
    private void addTask(List<String> lines, Task task) {
        lines.add("   " + task);
    }

    /**
     * Appends tasks as a numbered list starting at 1, because that is the number the
     * user types back in commands such as "mark 2".
     */
    private void addTaskList(List<String> lines, List<Task> tasksToShow) {
        for (int i = 0; i < tasksToShow.size(); i++) {
            lines.add((i + 1) + "." + tasksToShow.get(i));
        }
    }

    /**
     * Writes the current list to disk, appending a note if that failed.
     * <p>
     * Called after every command that changes the list, so the saved file is never out
     * of date. A failure is reported but not thrown any further: the change the user
     * just made is still valid in memory, so there is no reason to end the session
     * over it.
     */
    private void save(List<String> lines) {
        try {
            storage.save(tasks);
        } catch (AmadeusException e) {
            lines.add(e.getMessage());
            // The change itself worked, but the user needs to know it will not survive
            // the session, so the whole reply is flagged rather than passing quietly.
            isError = true;
        }
    }
}
