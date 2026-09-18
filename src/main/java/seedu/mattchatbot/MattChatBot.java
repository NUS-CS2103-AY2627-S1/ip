package seedu.mattchatbot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskList;
import seedu.mattchatbot.task.TaskUpdate;

/**
 * Entry point of the MattChatBot chatbot.
 * <p>
 * Decides what to do with each command, leaving the details to the classes
 * that own them: Ui for talking to the user, Parser for reading the input,
 * TaskList for the tasks, and Storage for the save file.
 */
public class MattChatBot {

    /** Where the task list is kept between sessions. */
    private static final String SAVE_FILE = "data/mattchatbot.txt";

    /** Everything the user sees and types goes through here. */
    private final Ui ui;

    /** Reads and writes the save file. */
    private final Storage storage;

    /** The tasks the user is keeping track of. */
    private TaskList tasks;

    /**
     * A problem met while loading, to report once the welcome has been shown,
     * or null if loading went fine.
     */
    private String loadWarning;

    /** Whether the last command handled asked the chatbot to exit. */
    private boolean isExit = false;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param filePath where to load from and save to
     */
    public MattChatBot(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a chatbot that keeps its tasks in the given file and talks
     * through the given Ui.
     *
     * @param filePath where to load from and save to
     * @param ui       how replies reach the user
     */
    private MattChatBot(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
            List<String> reports = storage.getSkippedLineReports();
            if (!reports.isEmpty()) {
                loadWarning = describeSkippedLines(reports);
            }
        } catch (MattChatBotException e) {
            tasks = new TaskList();
            loadWarning = e.getMessage();
        }
    }

    /**
     * Describes the lines of the save file that could not be read, and why.
     *
     * @param reports one report per skipped line
     * @return the wording to show alongside the greeting
     */
    private static String describeSkippedLines(List<String> reports) {
        String heading = reports.size() == 1
                ? "I was unable to make sense of one line in your saved file,"
                        + " and have set it aside:"
                : "I was unable to make sense of " + reports.size()
                        + " lines in your saved file, and have set them aside:";
        return heading + System.lineSeparator()
                + reports.stream()
                        .map(report -> "  " + report)
                        .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Creates a chatbot on the default save file whose replies are returned as
     * text rather than printed, for a GUI to display.
     *
     * @return a chatbot ready to answer {@link #getResponse(String)}
     */
    public static MattChatBot forGui() {
        return forGui(SAVE_FILE);
    }

    /**
     * Creates a chatbot on the given save file whose replies are returned as
     * text rather than printed.
     * <p>
     * The file is passed in for the same reason {@link Storage} takes one: so
     * that a caller which is not the real application -- a test, or a second
     * window on a different list -- can point somewhere other than the file
     * the user's own tasks live in.
     *
     * @param filePath where to load from and save to
     * @return a chatbot ready to answer {@link #getResponse(String)}
     */
    public static MattChatBot forGui(String filePath) {
        return new MattChatBot(filePath, Ui.forGui());
    }

    /**
     * Returns the greeting a GUI should show before the user types anything,
     * including any complaint about the save file.
     *
     * @return the greeting, marked as a complaint if the save file was unusable
     */
    public Reply getWelcomeMessage() {
        showGreeting();
        return new Reply(ui.takeShownText(), loadWarning != null);
    }

    /**
     * Handles one line of input and returns what the chatbot says back.
     * <p>
     * This is the GUI's way in, and it is the counterpart of one turn of
     * {@link #runCommandLoop()}: a failed command is reported in the returned
     * text rather than thrown, so that one bad command does not end the
     * conversation.
     *
     * @param input one line as the user typed it
     * @return the reply, empty if the input was blank, marked as a complaint
     *         if the command could not be carried out
     */
    public Reply getResponse(String input) {
        String command = input.trim();
        if (command.isEmpty()) {
            return new Reply("", false);
        }
        // Whether this turn went wrong is known here and nowhere else, so it
        // is recorded now rather than left for the GUI to infer from wording.
        boolean isError = false;
        try {
            isExit = handleCommand(command);
            if (isExit) {
                ui.showGoodbye();
            }
        } catch (MattChatBotException e) {
            ui.showError(e.getMessage());
            isError = true;
        }
        return new Reply(ui.takeShownText(), isError);
    }

    /**
     * Returns whether the user has asked to exit, so that a GUI knows to close
     * its window.
     *
     * @return true once a bye command has been handled
     */
    public boolean isExit() {
        return isExit;
    }

    /** Greets the user, handles commands until they say bye, then signs off. */
    public void run() {
        showGreeting();
        runCommandLoop();
        ui.showGoodbye();
    }

    /**
     * Starts the chatbot, reading and writing the default save file.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        new MattChatBot(SAVE_FILE).run();
    }

    /**
     * Shows the welcome message, followed by any complaint about the save
     * file, as one block.
     * <p>
     * Shared by the console and GUI entry points so that a session starts the
     * same way whichever one is used.
     */
    private void showGreeting() {
        ui.showWelcome();
        if (loadWarning != null) {
            ui.show(loadWarning, "The entries I could read remain intact.");
        }
    }

    /**
     * Reads commands until the user says bye, reporting any problem and
     * carrying on rather than ending the session.
     */
    private void runCommandLoop() {
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isEmpty()) {
                continue;
            }
            try {
                boolean isExit = handleCommand(input);
                if (isExit) {
                    return;
                }
            } catch (MattChatBotException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Carries out one command.
     * <p>
     * Written as a switch expression over {@link Command}: because every case
     * yields the exit flag, the compiler requires the switch to cover every
     * command, so adding a command to the enum will not compile until it is
     * handled here.
     *
     * @param input one non-empty line as the user typed it
     * @return true if the user asked to exit
     * @throws MattChatBotException if the command cannot be carried out
     */
    private boolean handleCommand(String input) throws MattChatBotException {
        // Both callers drop blank input before getting here, so a blank line
        // reaching this point would mean one of those guards had been lost.
        // Parser would then read a command word that is not there.
        assert !input.isBlank() : "blank input is filtered out before dispatch";
        Command command = Parser.parseCommand(input);
        String argument = Parser.parseArgument(input);

        return switch (command) {
            case BYE -> true;
            case LIST -> {
                listTasks();
                yield false;
            }
            case ON -> {
                listTasksOn(Parser.parseOnDate(argument));
                yield false;
            }
            case FIND -> {
                listMatchingTasks(Parser.parseKeyword(argument));
                yield false;
            }
            case MARK -> {
                setDone(Parser.parseTaskNumber(argument, Command.MARK), true);
                yield false;
            }
            case UNMARK -> {
                setDone(Parser.parseTaskNumber(argument, Command.UNMARK), false);
                yield false;
            }
            case DELETE -> {
                deleteTask(Parser.parseTaskNumber(argument, Command.DELETE));
                yield false;
            }
            case UPDATE -> {
                updateTask(Parser.parseUpdateTarget(argument),
                        Parser.parseUpdateChanges(argument));
                yield false;
            }
            case TODO -> {
                addTask(Parser.parseTodo(argument));
                yield false;
            }
            case DEADLINE -> {
                addTask(Parser.parseDeadline(argument));
                yield false;
            }
            case EVENT -> {
                addTask(Parser.parseEvent(argument));
                yield false;
            }
        };
    }

    /**
     * Stores one task and confirms it to the user.
     *
     * @param task the task to store
     * @throws MattChatBotException if the updated list cannot be saved
     */
    private void addTask(Task task) throws MattChatBotException {
        // Parser either returns a task or throws, so null here would mean a
        // parse method had gained a silent failure path.
        assert task != null : "Parser returns a task or throws";
        if (tasks.contains(task)) {
            throw new MattChatBotException("That is already on your list:"
                    + System.lineSeparator() + "  " + task);
        }
        tasks.add(task);
        storage.save(tasks);
        ui.show("Very good. I have noted it:", "  " + task, countSummary());
    }

    /**
     * Removes the task at the given position and confirms it to the user.
     *
     * @param index the task's position, 0-based
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private void deleteTask(int index) throws MattChatBotException {
        Task removed = tasks.remove(index);
        storage.save(tasks);
        ui.show("Consider it struck from the list:", "  " + removed, countSummary());
    }

    /**
     * Marks the task at the given position as done or not done.
     *
     * @param index  the task's position, 0-based
     * @param isDone true to mark the task done, false to reverse it
     * @throws MattChatBotException if there is no such task, or saving fails
     */
    private void setDone(int index, boolean isDone) throws MattChatBotException {
        Task task = tasks.get(index);
        String confirmation;
        if (isDone) {
            task.markAsDone();
            confirmation = "Excellent. I have marked it complete:";
        } else {
            task.markAsNotDone();
            confirmation = "Very well. It is outstanding once more:";
        }
        storage.save(tasks);
        ui.show(confirmation, "  " + task);
    }

    /**
     * Changes parts of the task at the given position and confirms the result.
     * <p>
     * The task decides whether the requested parts suit its type, and refuses
     * before changing anything, so a rejected update leaves the list as it
     * was and nothing is saved.
     *
     * @param index   the task's position, 0-based
     * @param changes the parts to change
     * @throws MattChatBotException if there is no such task, the update does
     *         not suit its type, or saving fails
     */
    private void updateTask(int index, TaskUpdate changes) throws MattChatBotException {
        Task task = tasks.get(index);
        task.applyUpdate(changes);
        storage.save(tasks);
        ui.show("Duly amended:", "  " + task);
    }

    /** Prints every stored task, numbered from 1, with its type and status. */
    private void listTasks() {
        showTasks(tasks.asList(), "Your list, as it stands:",
                "Your list is quite empty. You might begin with "
                        + Command.TODO.getKeyword() + ", " + Command.DEADLINE.getKeyword()
                        + " or " + Command.EVENT.getKeyword() + ".");
    }

    /**
     * Prints the tasks that fall on a given date.
     *
     * @param date the date to report on
     */
    private void listTasksOn(LocalDate date) {
        String shownDate = DateTimes.format(date);
        showTasks(tasks.getTasksOn(date),
                "Your engagements for " + shownDate + ":",
                "Nothing at all on " + shownDate + ".");
    }

    /**
     * Prints the tasks whose description contains the given keyword.
     *
     * @param keyword the text the user is looking for
     */
    private void listMatchingTasks(String keyword) {
        showTasks(tasks.getTasksMatching(keyword),
                "The entries matching your enquiry:",
                "Nothing matches \"" + keyword + "\", I'm afraid.");
    }

    /**
     * Shows a set of tasks, or says why there are none to show.
     * <p>
     * Every command that lists tasks needs the same two cases, and only the
     * wording differs, so each caller supplies its own wording and leaves the
     * shape of the reply here.
     *
     * @param shown        the tasks to list
     * @param heading      the line above them when there are some
     * @param emptyMessage what to say instead when there are none
     */
    private void showTasks(ArrayList<Task> shown, String heading, String emptyMessage) {
        if (shown.isEmpty()) {
            ui.show(emptyMessage);
            return;
        }
        ui.show(numbered(heading, shown));
    }

    /**
     * Builds a block of output: a heading, then the tasks numbered from 1.
     *
     * @param heading the first line
     * @param shown   the tasks to number
     * @return the lines to print
     */
    private static String[] numbered(String heading, ArrayList<Task> shown) {
        // Every caller checks for the empty case first and shows its own
        // wording for it, so a heading with nothing under it is a mistake.
        assert !shown.isEmpty() : "the empty case is reported by the caller, not numbered";
        Stream<String> numbered = IntStream.range(0, shown.size())
                .mapToObj(i -> (i + 1) + "." + shown.get(i));
        return Stream.concat(Stream.of(heading), numbered).toArray(String[]::new);
    }

    /**
     * Returns the sentence reporting how many tasks remain, e.g.
     * {@code Now you have 4 tasks in the list.}
     */
    private String countSummary() {
        return "You now have " + tasks.describeSize() + " in your list.";
    }
}
