package thomas;

import java.io.IOException;
import java.util.List;

import thomas.command.Command;
import thomas.command.History;
import thomas.parser.Parser;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Entry point for the Thomas chatbot.
 * <p>
 * Thomas reads commands, typed at a console or into the GUI window, and stores
 * three kinds of task:
 * {@code todo}, {@code deadline ... /by ...} and
 * {@code event ... /from ... /to ...}. The stored tasks are printed on
 * {@code list} and on {@code on <day>}, can be marked done with {@code mark} and
 * not done again with {@code unmark}, and removed with {@code delete}. The most
 * recent change to the list can be taken back with {@code undo}, one at a time,
 * back to the start of the session. It stops when the user types {@code bye} or
 * the input ends.
 * <p>
 * The list survives between runs: it is loaded from {@value #DATA_PATH} at
 * start-up and written back after every command that changes it, so no task is
 * lost even if the program is closed without typing {@code bye}.
 * <p>
 * None of that is done here. Talking to the user belongs to {@link Ui}, the save
 * file to {@link Storage}, holding the tasks to {@link TaskList}, understanding
 * a typed line to {@link Parser}, and carrying out what it asked for to a
 * {@link Command}. This class only assembles those parts and turns the handle:
 * read a line, parse it, run it, repeat.
 */
public class Thomas {
    /**
     * Where the task list is saved, as a path from the project root.
     * <p>
     * Relative paths resolve against the directory the program was started
     * from, so this assumes Thomas is run from the project root, as the test
     * script does. It is chosen here, rather than inside {@link Storage},
     * because deciding where the tasks live is this program's business while
     * reading and writing them is Storage's.
     */
    private static final String DATA_PATH = "./data/tasklist.txt";

    /** Reads and writes the save file. */
    private final Storage storage;

    /** Everything the user sees and types. */
    private final Ui ui;

    /**
     * The tasks being tracked.
     * <p>
     * Not final: an unreadable save file leaves this holding a fresh empty list
     * instead of the loaded one.
     */
    private TaskList tasks;

    /**
     * How to reverse each change made to the list this session.
     * <p>
     * One per chatbot rather than one per command, because {@code undo} has to
     * reach what an earlier command did. It is deliberately not loaded from or
     * written to the save file: a session starts with nothing to undo.
     */
    private final History history = new History();

    /**
     * Why the save file could not be read, or the empty string if it could.
     * <p>
     * Held rather than shown, because loading happens while the chatbot is
     * being built and there is no telling yet whether anyone is watching a
     * console. {@link #run()} is what reports it.
     */
    private String loadingErrorMessage = "";

    /**
     * The kind of the last command {@link #getResponse} ran, as a class name.
     * <p>
     * Only the GUI has any use for this: it is what {@link thomas.ui.DialogBox} colors
     * the reply bubble by. The console never asks, which is why {@link #run()}
     * does not set it.
     */
    private String commandType = "";

    /**
     * Whether the last command {@link #getResponse} ran was {@code bye}.
     * <p>
     * The GUI's counterpart to the {@code isExit} local in {@link #run()}: a
     * console session ends by falling out of a loop, a window by being closed,
     * so what stopping means is left to whoever asked for the command to run.
     */
    private boolean hasExited = false;

    /**
     * Whether the last line given to {@link #getResponse} was rejected.
     * <p>
     * Kept apart from {@link #commandType}, which is empty both for a rejected
     * line and before any line at all: the window shows a rejection in a
     * different bubble from a plain reply, so it has to be able to tell the
     * two apart.
     */
    private boolean hasErrored = false;

    /**
     * Starts a chatbot over the usual save file.
     * <p>
     * For the GUI, which has no say in where the tasks are kept: choosing that
     * is this class's business, so the window does not have to know the path.
     */
    public Thomas() {
        this(DATA_PATH);
    }

    /**
     * Starts a chatbot over one save file, loading whatever it already holds.
     * <p>
     * Nothing is printed here. Constructing a chatbot is not a console session:
     * the GUI builds one too, and it has no console to complain to. What went
     * wrong while loading is therefore remembered rather than shown, and
     * {@link #run()} is what puts it on screen for a console session.
     *
     * @param filePath Where the task list is kept.
     */
    public Thomas(String filePath) {
        // One Ui for the whole session: it owns the Scanner over standard
        // input, and a second one would buffer ahead and swallow commands.
        ui = new Ui();

        storage = new Storage(filePath);
        try {
            // Storage hands back plain tasks; wrapping them in a TaskList is
            // what adds the checked operations the commands rely on.
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            // An unreadable save file should not stop the chatbot: remember why
            // and carry on with an empty list rather than dying with a stack
            // trace.
            loadingErrorMessage = e.getMessage();
            tasks = new TaskList();
        }
    }

    /**
     * Reads and carries out commands until the user says {@code bye} or the
     * input ends.
     * <p>
     * A session is an opening, a run of commands, and a farewell, which is all
     * this method settles. What the first two of those involve belongs to the
     * two methods below it.
     * <p>
     * Both ways of ending are handled: {@code bye} answers true to
     * {@link Command#isExit()}, and input that simply runs out fails
     * {@link Ui#hasNextCommand()}. The farewell is owed either way, but a typed
     * {@code bye} has already had it from {@link thomas.command.ExitCommand},
     * so only the second case is left to say it here.
     */
    public void run() {
        showStartup();
        boolean isExit = readAndRunCommands();

        // No save here: every command that changes the list has already saved,
        // so the file is current even if the program never reaches this point.
        if (!isExit) {
            // The input ran out rather than saying bye, so the farewell that
            // ExitCommand would have given is still owed.
            ui.showGoodbye();
        }
    }

    /**
     * Shows the greeting, with any complaint about the save file after it.
     * <p>
     * The console's counterpart to {@link #getStartupMessage()}, which words
     * the same things for the GUI. The greeting goes first so that a complaint
     * arrives after Thomas has introduced itself rather than before.
     */
    private void showStartup() {
        ui.showWelcome();
        for (String complaint : getLoadingComplaints()) {
            // One block each, so a complaint reads as its own message rather
            // than as more of the greeting.
            ui.showMessage(complaint);
        }
    }

    /**
     * Reads and carries out commands until {@code bye} or the end of the input.
     * <p>
     * This loop does not know what any command does. It reads a line, asks
     * {@link Parser} for the command it names, runs it, and asks whether that
     * was the last one -- so a new command is a new class, and this method
     * never changes again.
     *
     * @return True if a command ended the session, false if the input ran out
     *         before one did.
     */
    private boolean readAndRunCommands() {
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String line = ui.readCommand();
                Command command = Parser.parse(line);
                // The line is noted rather than passed on, because a command knows
                // what it did but not how it was asked for: "delete 2" reaches
                // DeleteCommand as the number alone.
                history.startCommand(line);
                ui.showMessage(command.execute(tasks, ui, storage, history));
                isExit = command.isExit();
            } catch (ThomasException e) {
                // Every user mistake, whether noticed while reading the line or
                // while carrying it out, arrives here as one kind of exception
                // and is reported the same way. That is what keeps both the
                // parser and the commands free of error handling.
                ui.showError(e.getMessage());
            }
        }
        return isExit;
    }

    /**
     * Returns the greeting to open with, and any complaint about the save file.
     * <p>
     * The GUI's counterpart to the opening of {@link #run()}, which prints the
     * same things before reading its first command. A window has no such moment
     * of its own, so it asks for the words here and shows them as Thomas's
     * first dialog box. Without it a save file that could not be read would
     * pass unmentioned, and the first task added would overwrite it.
     *
     * @return The greeting, with any warning about the save file after it.
     */
    public String getStartupMessage() {
        StringBuilder message = new StringBuilder(ui.getWelcomeMessage());
        for (String complaint : getLoadingComplaints()) {
            message.append("\n").append(complaint);
        }
        return message.toString();
    }

    /**
     * Returns what went wrong while loading, worded for the user.
     * <p>
     * Shared by both front ends so that neither can start reporting something
     * the other does not. A file that could not be read at all has nothing to
     * say about individual lines, which is why the two are alternatives rather
     * than both.
     *
     * @return The warnings to show, empty when the save file loaded cleanly.
     */
    private List<String> getLoadingComplaints() {
        if (!loadingErrorMessage.isEmpty()) {
            return List.of(ui.getLoadingErrorMessage(loadingErrorMessage));
        }
        // Storage records the lines it could not read instead of printing them,
        // so they are worded here, where the Ui is.
        return storage.getSkipComplaints().stream()
                .map(ui::getSkippedLineMessage)
                .toList();
    }

    /**
     * Carries out one typed line and returns what Thomas says back.
     * <p>
     * The GUI's counterpart to {@link #run()}: the same parse, run, report
     * cycle, but for a single line, and the reply is handed back instead of
     * being printed. The kind of command is remembered on the way past, for
     * {@link #getCommandType()} to report.
     *
     * @param input The line the user typed.
     * @return Thomas's reply, ready for a dialog box.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            history.startCommand(input);
            String response = command.execute(tasks, ui, storage, history);
            // Every command owes the user an answer. A null one is not
            // caught anywhere downstream: it reaches the window as the
            // four characters "null" in a dialog box, which reads as a
            // reply rather than as the fault it is.
            assert response != null : "Command gave no reply: " + command.getClass().getSimpleName();
            // The simple name, not the full one: "AddCommand" is what the
            // dialog box matches its style classes against, not
            // "thomas.command.AddCommand".
            commandType = command.getClass().getSimpleName();
            hasExited = command.isExit();
            hasErrored = false;
            return response;
        } catch (ThomasException e) {
            // No command ran, so there is no kind of command to color by.
            commandType = "";
            hasErrored = true;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the last line given to {@link #getResponse} was rejected.
     *
     * @return True if the reply to it was an error message rather than a
     *         command's answer.
     */
    public boolean hasErrored() {
        return hasErrored;
    }

    /**
     * Returns the kind of the last command {@link #getResponse} ran.
     *
     * @return The command's simple class name, or the empty string if the last
     *         input named no command Thomas understood.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns whether the last command {@link #getResponse} ran ends the session.
     *
     * @return True if the user has said {@code bye}.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Starts a console session.
     * <p>
     * The GUI has an entry point of its own in {@link Launcher}; this one is
     * what the text-UI tests drive.
     *
     * @param args Command line arguments; unused.
     */
    public static void main(String[] args) {
        new Thomas(DATA_PATH).run();
    }
}
