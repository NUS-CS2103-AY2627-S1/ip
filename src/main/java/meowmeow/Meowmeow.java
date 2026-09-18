package meowmeow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import meowmeow.command.Command;
import meowmeow.parser.Parser;
import meowmeow.storage.Storage;
import meowmeow.task.TaskList;
import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * Meowmeow, a small command-line task tracker. This class is now just the
 * wiring: it holds the three collaborators - {@link Ui} for talking to the
 * user, {@link Storage} for the save file, {@link TaskList} for the tasks
 * themselves - and {@link #run()} reads commands and routes each one to
 * them. Understanding a command is {@link Parser}'s job; each command is
 * then a {@link Command} object that runs itself.
 *
 * <p>The console app uses {@link #run()}; the JavaFX GUI holds a
 * {@code Meowmeow} instead, shows {@link #startupMessages()} once when the
 * window opens, and calls {@link #getResponse(String)} once per line the
 * user types.
 */
public class Meowmeow {

    /**
     * The seed the console entry point gives its {@link Ui}, so every run
     * picks the same sequence of phrasings. Console output is asserted
     * byte-for-byte by {@code test/ui-test-plan.md}; the GUI, which is not,
     * seeds its own {@link Ui} with a real random source instead.
     */
    private static final long CONSOLE_SEED = 2024_09_16L;

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Wires up the collaborators and loads any previously saved tasks. The
     * save-file location is given as path segments (e.g. {@code "data"},
     * {@code "meowmeow.txt"}) that {@link Storage} joins with the current
     * OS's separator.
     *
     * <p>Unlike the assignment's sample constructor there is no try/catch
     * around the load: {@link Storage#load()} deals with a missing or
     * partly-corrupt file itself (starting empty, or skipping only the
     * unreadable lines with a warning through {@link Ui}) instead of
     * throwing, so there is no loading failure for this constructor to
     * recover from. Any such warning is buffered by {@code ui} and surfaces
     * through {@link #startupMessages()} (GUI) or prints immediately
     * (console), before the welcome banner, exactly as in the sample.
     *
     * @param ui    talks to the user; the caller picks its {@link MessageStyle}
     *              and randomness, so the console and the GUI can differ.
     * @param first the first segment of the save-file path, e.g. {@code "data"}.
     * @param more  any further path segments, e.g. {@code "meowmeow.txt"}.
     */
    public Meowmeow(Ui ui, String first, String... more) {
        this.ui = ui;
        storage = new Storage(ui, first, more);
        tasks = new TaskList(storage.load());
    }

    /**
     * Greets the user, then reads and runs commands until an
     * {@link meowmeow.command.ExitCommand ExitCommand} ("bye") or the end of input. The {@link Ui} (and
     * with it {@code System.in}) is closed on the way out.
     */
    public void run() {
        ui.print(ui.showWelcome());
        try {
            boolean isExit = false;
            // hasNextCommand() lets the loop also end gracefully on
            // end-of-input, e.g. piped input with no "bye" line.
            while (!isExit && ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                if (fullCommand.isEmpty()) {
                    // Blank lines aren't a command worth acting on.
                    continue;
                }
                // Parser and every Command report a problem by throwing a
                // MeowmeowException rather than printing, so this one catch
                // shows the friendly error for all of them.
                try {
                    Command command = Parser.parse(fullCommand);
                    ui.print(command.execute(tasks, ui, storage));
                    isExit = command.isExit();
                } catch (MeowmeowException e) {
                    ui.print(ui.showError(e.getMessage()));
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Returns the messages the GUI should show once, when the window opens:
     * any warning buffered while loading the save file, then the welcome
     * banner. The console shows the same information but immediately (a
     * load warning prints as it happens) and via {@link #run()}, so it has
     * no need to call this.
     *
     * @return the startup messages, in the order they should appear.
     */
    public List<Response> startupMessages() {
        List<Response> responses = new ArrayList<>();
        for (String warning : ui.drainWarnings()) {
            responses.add(new Response(warning, ResponseKind.WARNING));
        }
        responses.add(new Response(ui.showWelcome(), ResponseKind.NORMAL));
        return responses;
    }

    /**
     * Runs one line of user input and returns Meowmeow's reply, for the GUI.
     * A {@link MeowmeowException} (an unknown command, a bad task number) is
     * caught and its message returned as a {@link ResponseKind#ERROR} reply,
     * mirroring how {@link #run()} shows errors in the console. Any warning
     * raised while the command runs (e.g. a failed save) is returned
     * alongside it; "bye" is tagged {@link ResponseKind#EXIT} so the GUI
     * knows to close.
     *
     * @param input one line the user typed into the GUI.
     * @return the reply (or replies) Meowmeow should show in response.
     */
    public List<Response> getResponse(String input) {
        List<Response> responses = new ArrayList<>();
        try {
            Command command = Parser.parse(input);
            String text = command.execute(tasks, ui, storage);
            ResponseKind kind = command.isExit() ? ResponseKind.EXIT : ResponseKind.NORMAL;
            responses.add(new Response(text, kind));
            for (String warning : ui.drainWarnings()) {
                responses.add(new Response(warning, ResponseKind.WARNING));
            }
        } catch (MeowmeowException e) {
            responses.add(new Response(ui.showError(e.getMessage()), ResponseKind.ERROR));
        }
        return responses;
    }

    /**
     * Starts Meowmeow, saving to {@code ./data/meowmeow.txt}. Uses a fixed
     * random seed (see {@link #CONSOLE_SEED}) so the console's phrasing is
     * reproducible for the console UI test plan.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(CONSOLE_SEED));
        new Meowmeow(ui, "data", "meowmeow.txt").run();
    }
}
