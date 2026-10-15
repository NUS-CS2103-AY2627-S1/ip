package xiaozhi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import xiaozhi.command.Command;
import xiaozhi.command.CommandHistory;
import xiaozhi.exception.XiaoZhiException;
import xiaozhi.parser.Parser;
import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Entry point for the XiaoZhi chatbot.
 * <p>
 * Wires together a {@link Ui}, a {@link Storage} and a {@link TaskList},
 * then drives the read-parse-execute loop that turns each line of input
 * into a {@link Command} via {@link Parser} and runs it.
 * <p>
 * Also owns the {@link CommandHistory} that lets {@code undo} reverse a
 * previous command: every command that runs here without error is pushed
 * onto it if {@link Command#isUndoable()} says it can be reversed.
 */
public class XiaoZhi {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final CommandHistory history = new CommandHistory();
    private boolean isExit = false;
    private String commandType = "";

    /**
     * Creates a XiaoZhi that persists its tasks to the given file path.
     *
     * @param filePath Path to the save file, passed straight to {@link Storage}.
     */
    public XiaoZhi(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Save file path should never be null or blank.";
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Runs one line of input through the same parse-execute pipeline as
     * {@link #run()}, returning what would have been printed instead of
     * printing it. Meant for a GUI front-end (see {@code xiaozhi.gui}),
     * which has no console to print to.
     * <p>
     * {@link Ui} still reports results through {@code System.out}, so this
     * temporarily redirects standard output into a buffer for the duration
     * of the call and hands back whatever was written to it.
     *
     * @param input One line of user input, exactly as it would be typed at the console.
     * @return The response text XiaoZhi would have printed for that input.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage, history);
            if (command.isUndoable()) {
                history.push(command);
            }
            isExit = command.isExit();
            commandType = command.getClass().getSimpleName();
            if (isExit) {
                ui.showFarewell();
            }
        } catch (XiaoZhiException e) {
            commandType = "Error";
            ui.showError(e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().strip();
    }

    /**
     * Returns whether the most recent {@link #getResponse(String)} call was an exit command.
     *
     * @return {@code true} if XiaoZhi should close after that response was shown, {@code false} otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the simple class name of the {@link Command} that produced the
     * most recent {@link #getResponse(String)} reply, e.g. {@code "AddCommand"},
     * or {@code "Error"} if that input failed instead -- either because it
     * could not be parsed into a command at all, or because the resulting
     * command failed while executing (e.g. an invalid task number). Meant for
     * the GUI to pick a card style for the reply (see {@code xiaozhi.gui.DialogBox}),
     * with {@code "Error"} deliberately given a style that stands out from a
     * normal reply.
     *
     * @return The simple class name of the most recently executed command, or {@code "Error"}.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Runs the chatbot until the user issues an exit command.
     * Prints the banner and greeting first, then repeatedly reads a
     * command, parses and executes it, and reports any error without
     * stopping the loop, until a command signals it is time to exit.
     */
    public void run() {
        ui.showBanner();
        ui.showGreeting();
        boolean shouldExit = false;
        String input = ui.readCommand();

        while (!shouldExit) {
            try {
                Command command = Parser.parse(input);
                command.execute(tasks, ui, storage, history);
                if (command.isUndoable()) {
                    history.push(command);
                }
                shouldExit = command.isExit();
            } catch (XiaoZhiException e) {
                ui.showError(e.getMessage());
            }

            if (!shouldExit) {
                input = ui.readCommand();
            }
        }

        ui.showFarewell();
    }

    /**
     * Starts XiaoZhi, saving tasks to {@code ./data/xiaozhi.txt}.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new XiaoZhi("./data/xiaozhi.txt").run();
    }
}
