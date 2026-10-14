package verity;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import verity.command.Command;
import verity.command.UndoCommand;
import verity.command.Undoable;
import verity.parser.Parser;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Entry point and top-level orchestrator: owns the {@link Ui}, {@link Storage}, and
 * {@link TaskList}, and drives the read-parse-execute loop.
 */
public class Verity {
    /** Prefix applied to replies caused by invalid input or an unavailable operation. */
    public static final String ERROR_PREFIX = "ERROR!!! >.<\n";

    private final Ui ui;
    private final Storage storage;
    /** Completed reversible actions, with the most recent action at the top. */
    private final Deque<Undoable> undoHistory = new ArrayDeque<>();
    private TaskList tasks;
    private boolean isExit = false;
    /** Mistakes in a row, without a successful command in between; resets to 0 on success. */
    private int consecutiveErrorCount = 0;

    /**
     * Creates Verity, loading any previously saved tasks from the given file.
     *
     * @param filePath Relative path to the save file, e.g. "data/verity.txt".
     */
    public Verity(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadWarning(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main read-parse-execute loop until an {@code ExitCommand} is executed.
     */
    public void run() {
        ui.showWelcome();
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                ui.showResponse(getResponse(fullCommand));
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes a single line of input, for use by a GUI (or any caller that wants
     * one command's response as a string rather than driving the read-parse-execute loop).
     * Check {@link #isExit()} afterwards to know whether the user asked to exit.
     *
     * @param input Full line of user input.
     * @return Response to show the user.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input, undoHistory.peek());
            String response = command.execute(tasks, storage);
            updateUndoHistory(command);
            isExit = command.isExit();
            consecutiveErrorCount = 0;
            return response;
        } catch (VerityException e) {
            consecutiveErrorCount++;
            return ERROR_PREFIX + e.getMessage() + annoyanceFor(consecutiveErrorCount);
        }
    }

    /**
     * Returns an escalating reaction to append after a mistake, based on how many mistakes in a
     * row have happened (with no successful command in between): none for the first, mildly
     * annoyed for the second, openly exasperated from the third onward.
     *
     * @param errorCount Number of consecutive mistakes, including the one just made.
     * @return Reaction to append to the error message, or an empty string for the first mistake.
     */
    private static String annoyanceFor(int errorCount) {
        if (errorCount <= 1) {
            return "";
        } else if (errorCount == 2) {
            return "\nAgain? Read what I just told you.";
        } else {
            return "\nI am NOT explaining this again. >:(";
        }
    }

    /**
     * Returns whether a response represents a recoverable user-facing error.
     * The GUI uses this to make errors visually distinct from ordinary replies.
     *
     * @param response Response returned by {@link #getResponse(String)}.
     * @return {@code true} if the response starts with {@link #ERROR_PREFIX}.
     */
    public static boolean isErrorResponse(String response) {
        return response != null && response.startsWith(ERROR_PREFIX);
    }

    /**
     * Updates the session's undo stack after executing the given command. An {@code undo} removes
     * the action it just reversed; an undoable command is pushed; a non-mutating command (such as
     * {@code list} or {@code find}) leaves the stack unchanged. There is deliberately no redo.
     *
     * @param executed Command that was just executed.
     */
    private void updateUndoHistory(Command executed) {
        if (executed instanceof UndoCommand) {
            if (!undoHistory.isEmpty()) {
                undoHistory.pop();
            }
        } else if (executed instanceof Undoable undoable) {
            undoHistory.push(undoable);
        }
    }

    /**
     * Returns whether the most recently executed command was an exit command.
     *
     * @return {@code true} if the user asked to exit.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Entry point of the program: creates Verity backed by the given save file and runs it.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Verity("data/verity.txt").run();
    }
}
