package billy;

import java.nio.file.Path;
import java.util.ArrayList;

import billy.command.Command;
import billy.parser.Parser;
import billy.storage.Storage;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Billy is a friendly chatbot that keeps a list of tasks for the user.
 *
 * <p>Tasks come in three types (todo, deadline and event). They can be listed,
 * searched for by word or by day, marked as done, marked as not done again, and
 * deleted. Anything Billy cannot make sense of is reported as a
 * {@link BillyException} rather than crashing. Typing {@code bye} ends the
 * conversation.
 *
 * <p>The list is kept on the hard disk by {@link Storage}: it is read back when
 * Billy starts and written out again after every change, so closing Billy no
 * longer loses the user's tasks.
 *
 * <p>Deadlines and events carry real dates rather than free text, so Billy can
 * tidy up how they are shown and answer {@code on} with the tasks falling on a
 * given day.
 *
 * <p>Billy itself now only coordinates: {@link Parser} turns what was typed into
 * a {@link Command}, which carries itself out against the {@link TaskList}, the
 * {@link Storage} and the {@link Ui}. Billy neither knows nor cares which
 * commands exist, so adding one leaves this class untouched.
 *
 * <p>A Billy is an object rather than a collection of static methods, so its
 * helpers are settled once when it is built and cannot be swapped underneath it
 * afterwards. Two Billys over different files could run without either knowing
 * about the other.
 */
public class Billy {

    /**
     * Where the task list is kept between runs.
     *
     * <p>{@link Path#of(String, String...)} joins the parts with whatever separator
     * the computer uses, so this works on macOS, Linux and Windows alike; writing
     * {@code "data/billy.txt"} by hand would not. The path is relative, so Billy
     * keeps its data beside wherever it is run from rather than in a fixed place
     * that may not exist on someone else's computer.
     */
    private static final Path DATA_FILE = Path.of("data", "billy.txt");

    /** Reads the task list from its file and writes it back again. */
    private final Storage storage;

    /** Says everything the user sees, and reads everything the user types. */
    private final Ui ui;

    /** The user's tasks, as they stood when Billy started, and as they change. */
    private final TaskList tasks;

    /**
     * What stopped the saved list being read, or null if nothing did.
     *
     * <p>Remembered rather than reported on the spot, because the greeting has to
     * come first and a constructor has no business writing to the screen. Its one
     * job is to leave the object ready to work; saying so is {@link #run()}'s.
     */
    private final String loadError;

    /**
     * Whether the last command answered through {@link #getResponse(String)}
     * asked to end the conversation.
     *
     * <p>The console loop keeps this in a local variable, since it is the thing
     * doing the looping. The window has no loop of its own to hold it in: it
     * hands over one command at a time and asks afterwards whether that was the
     * last, so the answer has to survive between calls.
     */
    private boolean isExitRequested = false;

    /**
     * Whether the last command answered through {@link #getResponse(String)}
     * failed.
     *
     * <p>Kept for the same reason as {@link #isExitRequested}: the window asks
     * about the answer after it has been handed back, so anything it needs to
     * know about that answer has to outlive the call that produced it.
     */
    private boolean isLastResponseError = false;

    /**
     * Builds a Billy that keeps its tasks in the usual file.
     *
     * <p>Offered for the window, which has no reason to care where the list is
     * kept. Tests still name a file of their own through the other constructor.
     */
    public Billy() {
        this(DATA_FILE);
    }

    /**
     * Builds a Billy that keeps its tasks in a particular file.
     *
     * <p>Whatever goes wrong while reading, Billy is left able to work: a missing,
     * unreadable or damaged file costs at most the tasks it held, never the
     * session. That is why the failure is recorded rather than thrown.
     *
     * @param filePath where the task list is kept between runs
     */
    public Billy(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        // These two cannot be initialized where they are declared, as the coding
        // standard would otherwise ask: the value depends on whether loading
        // threw. Assigning the final fields directly from the try and the catch
        // does not compile either, since the compiler cannot rule out the field
        // having been set before the exception arrived. Hence the temporaries.
        TaskList loaded;
        String failure;
        try {
            loaded = new TaskList(storage.load());
            failure = null;
        } catch (BillyException e) {
            loaded = new TaskList();
            failure = e.getMessage();
        }
        tasks = loaded;
        loadError = failure;
    }

    /**
     * Starts Billy, keeping the task list in the usual file.
     *
     * @param args not used; Billy takes its instructions from the conversation
     *             rather than from the command line
     */
    public static void main(String[] args) {
        new Billy(DATA_FILE).run();
    }

    /**
     * Holds the conversation, from the greeting to the farewell.
     *
     * <p>Everything this needs was settled when the object was built, so a Billy
     * can be made and run separately, and more than one could exist over
     * different files.
     */
    public void run() {
        ui.showWelcome();
        reportStartup();

        // Each command says for itself whether it was the last one, so this loop
        // never has to know which commands exist.
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                Command command = Parser.parse(ui.readCommand());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (BillyException e) {
                // Every problem with a command surfaces here, so this is the one
                // place a failure becomes a message. The loop carries on, so a
                // mistake never ends the conversation.
                ui.showError(e.getMessage());
            }
        }
        ui.close();
        ui.showGoodbye();
    }

    /**
     * Answers one command, as text rather than as something printed.
     *
     * <p>This is how the window talks to Billy. It runs the same parsing and
     * the same commands as the console conversation does, so the two cannot
     * drift apart; only where the words end up differs.
     *
     * @param input the line the user typed
     * @return what Billy has to say in reply, ready to put in a dialog box
     */
    public String getResponse(String input) {
        ui.startCollecting();
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
            if (isExitRequested) {
                // The console prints the farewell after its loop ends. The
                // window has no such moment, so the goodbye is said here.
                ui.show(Ui.getFarewell());
            }
        } catch (BillyException e) {
            ui.showError(e.getMessage());
        }
        isLastResponseError = ui.isErrorCollected();
        return ui.stopCollecting();
    }

    /**
     * Returns whether the last answered command failed.
     *
     * <p>The window shows a failure differently from an ordinary reply, and the
     * reply itself is only text by the time it gets there, so the difference
     * has to be asked about separately.
     *
     * @return whether the last reply reports a failure.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether the last answered command asked to end the conversation.
     *
     * @return whether Billy has been told to say goodbye.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns what Billy has to say about the saved list before the first
     * command is typed, or null when there is nothing worth saying.
     *
     * <p>Nothing is said when there is nothing to report, so a first run on a
     * fresh computer looks exactly as it did before saving existed.
     *
     * @return the startup message, or null if there is none.
     */
    public String getStartupMessage() {
        if (loadError != null) {
            return loadError;
        }

        ArrayList<String> notes = new ArrayList<>();
        if (!tasks.isEmpty()) {
            notes.add("Welcome back! Nothing moved while you were gone: "
                    + Ui.describeListSize(tasks.size()) + " still waiting.");
        }
        int skipped = storage.getSkippedLineCount();
        if (skipped > 0) {
            // Both halves have to agree on the number: "1 line were gibberish"
            // reads as a bug in Billy rather than a problem with the file.
            String noun = skipped == 1 ? " line" : " lines";
            String verdict = skipped == 1
                    ? " was gibberish to me, so I skipped it."
                    : " were gibberish to me, so I skipped them.";
            notes.add("Heads up: " + skipped + noun + " in " + storage.getPath() + verdict);
        }
        return notes.isEmpty() ? null : String.join("\n", notes);
    }

    /**
     * Returns whether the startup message reports a failure rather than news.
     *
     * <p>A saved list that could not be read is a problem the user should see
     * as one, while a list that loaded fine is not, so the window needs to tell
     * the two apart before it shows either.
     *
     * @return whether reading the saved list failed.
     */
    public boolean isStartupMessageError() {
        return loadError != null;
    }

    /**
     * Says what happened while the saved list was being read.
     *
     * <p>Nothing is said when there is nothing to report, so a first run on a
     * fresh computer looks exactly as it did before saving existed.
     */
    private void reportStartup() {
        String message = getStartupMessage();
        if (message == null) {
            return;
        }
        if (loadError != null) {
            ui.showError(message);
        } else {
            ui.show(message);
        }
    }
}
