package tally;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalInt;

import tally.parser.Command;
import tally.parser.Parser;
import tally.storage.LoadResult;
import tally.storage.Storage;
import tally.task.Task;
import tally.task.TaskList;
import tally.ui.Ui;

/**
 * Tally is a chatbot that helps the user keep a tally of their tasks, in a window
 * or in a terminal.
 *
 * <p>This class carries out the commands and leaves everything else to the parts it
 * holds together: Ui talks to the user, Parser makes sense of what they typed,
 * TaskList holds the tasks, and Storage keeps them on disk between runs. The window
 * calls getResponse for each line; the terminal calls run, which asks for lines
 * until the user leaves.
 */
public class Tally {
    /** Where the tally is kept, relative to the project root. */
    private static final Path DATA_FILE = Paths.get("data", "tally.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Replies replies;

    /** Whether replies are printed as they are made rather than handed back. */
    private final boolean isConsole;

    /** What went wrong while reading the data file, if anything did. */
    private final Optional<String> loadWarning;

    /** Whether the user has said goodbye. */
    private boolean isExiting;

    /**
     * Creates a chatbot working on the given data file, reading back whatever it
     * already holds.
     *
     * <p>A file that cannot be read is reported once the user has been greeted, and
     * the chatbot starts with an empty tally rather than refusing to run.
     *
     * @param dataFile where the tally is kept.
     */
    public Tally(Path dataFile) {
        this(dataFile, true);
    }

    /**
     * Creates a chatbot working on the tally's usual data file.
     *
     * <p>Where that file lives is Tally's own business, so a front end that wants the
     * usual one asks for it this way rather than naming the place itself.
     *
     * @param isConsole whether replies are printed and commands read from standard input.
     */
    public Tally(boolean isConsole) {
        this(DATA_FILE, isConsole);
    }

    /**
     * Creates a chatbot working on the given data file, replying either on the
     * console or as text for a caller to display.
     *
     * @param dataFile where the tally is kept.
     * @param isConsole whether replies are printed and commands read from standard input.
     */
    public Tally(Path dataFile, boolean isConsole) {
        this.isConsole = isConsole;
        this.ui = new Ui(isConsole);
        this.storage = new Storage(dataFile);
        TaskList loaded;
        Optional<String> warning;
        try {
            LoadResult result = storage.load();
            loaded = new TaskList(result.tasks());
            warning = result.note();
        } catch (TallyException exception) {
            loaded = new TaskList();
            warning = Optional.of(exception.getMessage());
        }
        this.tasks = loaded;
        this.replies = new Replies(loaded);
        this.loadWarning = warning;
    }

    /**
     * Returns what Tally says in reply to one command, for a caller that shows the
     * reply itself rather than having it printed.
     *
     * <p>Only a Tally answering a window has a reply to hand back. A console one
     * prints as it goes, so there is nothing left to collect afterwards.
     *
     * @param input the line the user typed.
     * @return everything Tally says in reply, which is never empty.
     */
    public String getResponse(String input) {
        assert !isConsole : "a console Tally prints its replies, leaving nothing to return";
        try {
            runCommand(input.trim());
        } catch (TallyException exception) {
            ui.showError(exception.getMessage());
        }
        return ui.takePendingResponse();
    }

    /**
     * Returns whether the user has said goodbye.
     *
     * <p>Once true it stays true, so a front end that keeps taking input after the
     * goodbye cannot be told the conversation is running again.
     *
     * @return true once the user has said goodbye.
     */
    public boolean isExiting() {
        return isExiting;
    }

    /**
     * Returns the greeting shown when the chatbot starts, together with any
     * complaint about the data file.
     *
     * <p>Only a Tally answering a window has a greeting to hand back. A console one
     * prints its own, which is why run greets the user rather than calling this.
     *
     * @return the opening message.
     */
    public String getGreeting() {
        assert !isConsole : "a console Tally prints its greeting, leaving nothing to return";
        greet();
        return ui.takePendingResponse();
    }

    /** Says hello, and reports anything that was wrong with the data file. */
    private void greet() {
        ui.showWelcome();
        loadWarning.ifPresent(ui::showError);
    }

    /** Greets the user, carries out commands until they leave, then says goodbye. */
    public void run() {
        greet();

        while (!isExiting && ui.hasNextCommand()) {
            try {
                runCommand(ui.readCommand());
            } catch (TallyException exception) {
                ui.showError(exception.getMessage());
            }
        }

        // The command that ended the conversation has already said goodbye. Reaching
        // here without one means the input ended instead, which is an ending too.
        if (!isExiting) {
            ui.showGoodbye();
        }
        ui.close();
    }

    /**
     * Carries out one command from the user.
     *
     * <p>Saying goodbye is a command like any other: it is carried out here, farewell
     * and all, rather than answered back for a caller to act on. That is what keeps the
     * farewell to the one command that asked for it, however many follow.
     *
     * @param line the line the user typed, with surrounding spaces removed.
     * @throws TallyException if Tally cannot carry out the command.
     */
    private void runCommand(String line) throws TallyException {
        Command command = Parser.parseCommand(line);
        assert command != null
                : "Parser.parseCommand returns a constant or throws, so it never yields null";
        String arguments = Parser.parseArguments(line);
        // Checked before bye is acted on, so that "bye now" is questioned rather than
        // quietly ending the conversation and taking the rest of the input with it.
        Parser.rejectUnwantedArguments(command, arguments);
        if (command == Command.BYE) {
            isExiting = true;
            ui.showGoodbye();
            return;
        }

        String[] replyLines = carryOut(command, arguments);

        // Saving after a command that only read the tally would rewrite the file for
        // nothing, and every rewrite is a chance to lose what is already there.
        if (command.canChangeTally()) {
            saveOrPutBack();
        }
        // The reply waits until the tally is safely written, so that a save that fails
        // is not announced as a success and taken back in the same breath.
        ui.show(replyLines);
    }

    /**
     * Writes the tally, and puts it back as it was if it cannot be written.
     *
     * <p>A command changes the tally before it can be saved, so a save that fails would
     * otherwise leave the user looking at a change a restart would take away. Reading the
     * file back is what says which of the two is real: what is on the tally, or what is
     * in the file.
     *
     * @throws TallyException naming what went wrong, and whether the change was kept.
     */
    private void saveOrPutBack() throws TallyException {
        try {
            storage.save(tasks.copyAsList());
            return;
        } catch (TallyException failure) {
            // The message is worked out first: throwing from inside the inner try would
            // be caught by its own catch, and report the wrong one of the two.
            String outcome;
            try {
                LoadResult fromFile = storage.load();
                tasks.replaceAll(fromFile.tasks());
                outcome = " The record has been put back to what the file holds."
                        + fromFile.note().map(damage -> " " + damage).orElse("");
            } catch (TallyException unreadable) {
                outcome = " The record here is as you left it,"
                        + " but a restart will not show it.";
            }
            throw new TallyException(failure.getMessage() + outcome);
        }
    }

    /**
     * Carries out one command, which by now is not the one that ends the conversation.
     *
     * <p>Commands are dispatched by a switch over the Command enum rather than by a
     * class per command carrying an execute method, which is how AddressBook-Level3 and
     * <a href="https://github.com/NUS-CS2103-AY2627-S1/ip/pull/535">some classmates</a>
     * build theirs. With ten commands of a few lines each, keeping them together shows
     * the whole conversation at once; the trade reverses once a command needs state of
     * its own.
     *
     * <p>Every constant has a branch and there is no default clause, which is what
     * makes the compiler refuse a new command nobody has wired up here. A default
     * clause would answer for it instead, and the omission would only show as a
     * failure once someone typed the new command.
     *
     * <p>The reply is returned rather than shown, so that the caller can hold it back
     * until the change it describes has been written.
     *
     * @param command what the user asked for.
     * @param arguments the rest of the line they typed, with surrounding spaces removed.
     * @return the lines to tell the user, in order.
     * @throws TallyException if Tally cannot carry the command out.
     */
    private String[] carryOut(Command command, String arguments) throws TallyException {
        // AI suggested switching to a switch statement instead of the if-else chain.
        // Arrow labels keep each branch self-contained.
        return switch (command) {
            case LIST -> replies.describeTally();
            case FIND -> replies.describeMatchingTasks(Parser.parseSearchText(arguments));
            case FREE -> replies.describeFreeDays(Parser.parseFreeQuery(arguments, LocalDate.now()));
            case MARK -> markTask(Parser.parseTaskIndex(arguments, tasks.size(), command));
            case UNMARK -> unmarkTask(Parser.parseTaskIndex(arguments, tasks.size(), command));
            case DELETE -> deleteTask(Parser.parseTaskIndex(arguments, tasks.size(), command));
            case TODO -> addTask(Parser.parseTodo(arguments));
            case DEADLINE -> addTask(Parser.parseDeadline(arguments));
            case EVENT -> addTask(Parser.parseEvent(arguments));
            case WINDOW -> addTask(Parser.parseWindow(arguments));
            // The caller deals with bye and never passes it on, so reaching it here is a
            // programming error rather than anything the user typed.
            case BYE -> throw new IllegalStateException("bye is not carried out here");
        };
    }

    /** Marks the task at the given place done, and returns it as it now reads. */
    private String[] markTask(int position) {
        Task task = tasks.get(position);
        task.markAsDone();
        return new String[] {"Marked done:", task.toString()};
    }

    /** Marks the task at the given place not done after all, and returns it as it now reads. */
    private String[] unmarkTask(int position) {
        Task task = tasks.get(position);
        task.markAsNotDone();
        return new String[] {"Marked not done. As you were:", task.toString()};
    }

    /** Takes the task at the given place off the tally, and says how many are left. */
    private String[] deleteTask(int position) {
        Task task = tasks.get(position);
        tasks.remove(task);
        String countSentence = replies.formatCountSentence();
        return new String[] {"Struck from the record:", task.toString(), countSentence};
    }

    /**
     * Adds a task to the tally and says what was recorded.
     *
     * <p>A task the tally already holds is refused rather than added beside it, since
     * two identical entries give the user no way to tell which is which, and marking
     * one done leaves the other looking like work still to do.
     *
     * @param task the task to add.
     * @return the lines to tell the user.
     * @throws TallyException if the tally already holds the same task.
     */
    private String[] addTask(Task task) throws TallyException {
        OptionalInt alreadyThere = tasks.findPositionOf(task);
        if (alreadyThere.isPresent()) {
            throw new TallyException(String.format(
                    "Already on record as task %d. Once is enough.",
                    alreadyThere.getAsInt() + 1));
        }
        tasks.add(task);
        String countSentence = replies.formatCountSentence();
        return new String[] {"Recorded:", task.toString(), countSentence};
    }

    /**
     * Starts the chatbot.
     *
     * @param args optionally the path of the data file to use, which lets the tests
     *     run against a file of their own rather than the real tally.
     */
    public static void main(String[] args) {
        Path dataFile = args.length > 0 ? Paths.get(args[0]) : DATA_FILE;
        new Tally(dataFile).run();
    }
}
