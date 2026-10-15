package clearblue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import clearblue.command.Command;
import clearblue.command.UndoCommand;
import clearblue.parser.Parser;
import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Runs the Clearblue chatbot and responds to task-management commands.
 */
public class Clearblue {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit;
    private boolean isLastResponseError;
    private Command lastUndoableCommand;

    /**
     * Creates the chatbot, loading any previously saved tasks from the
     * given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Clearblue(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            Storage.LoadResult loadResult = storage.load();
            tasks = new TaskList(loadResult.tasks());
            if (loadResult.skippedLineCount() > 0) {
                ui.showCorruptedDataWarning(loadResult.skippedLineCount());
            }
        } catch (ClearblueException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Prints the banner and greeting, then reads and responds to commands
     * from standard input until a {@code bye} command is received or input
     * runs out.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                executeAndTrackUndo(command);
                isExit = command.isExit();
            } catch (ClearblueException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes one command line for the GUI, returning everything
     * the chatbot would normally print to the console as a single string.
     * Reuses the same {@link Parser}, {@link Command}, and {@link Ui} logic
     * as {@link #run()} by temporarily capturing standard output, so console
     * and GUI behaviour cannot drift apart.
     *
     * @param input one line of user input, as typed into the GUI
     * @return the chatbot's reply, with no leading or trailing blank lines
     */
    public String getResponse(String input) {
        return captureOutput(() -> {
            isLastResponseError = false;
            try {
                Command command = Parser.parse(input);
                executeAndTrackUndo(command);
                isExit = command.isExit();
            } catch (ClearblueException exception) {
                ui.showError(exception.getMessage());
                isLastResponseError = true;
            }
        });
    }

    /**
     * Executes {@code command}, wiring it into the undo history first: an
     * {@link UndoCommand} is handed the last undoable command to reverse,
     * and after a successful execute, {@code command} itself becomes the
     * new last undoable command if {@link Command#isUndoable()} says it is.
     * Only one level of undo is kept, so undoing clears the history rather
     * than pushing onto it.
     *
     * @param command command to execute
     * @throws ClearblueException if execution fails, e.g. there was nothing to undo
     */
    private void executeAndTrackUndo(Command command) throws ClearblueException {
        if (command instanceof UndoCommand undoCommand) {
            undoCommand.setCommandToUndo(lastUndoableCommand);
            lastUndoableCommand = null;
        }
        command.execute(tasks, ui, storage);
        if (command.isUndoable()) {
            lastUndoableCommand = command;
        }
    }

    /**
     * Returns whether the most recent command handled by
     * {@link #getResponse(String)} was a {@code bye} command, so the GUI
     * knows when to close its window.
     *
     * @return {@code true} if the chatbot should exit
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the most recent command handled by
     * {@link #getResponse(String)} failed, so the GUI knows to display that
     * reply as an error rather than a normal one.
     *
     * @return {@code true} if the last response was an error message
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Runs {@code action} with standard output redirected to a buffer, then
     * restores standard output and returns everything that was printed.
     *
     * @param action code whose console output should be captured
     * @return the captured output, with surrounding whitespace trimmed
     */
    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().trim();
    }

    /**
     * Starts the chatbot.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        new Clearblue("data/clearblue.txt").run();
    }
}
