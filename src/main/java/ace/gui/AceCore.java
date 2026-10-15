package ace.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import ace.exception.AceException;
import ace.parser.Parser;
import ace.storage.Storage;
import ace.task.TaskManager;
import ace.ui.Ui;

/**
 * Adapts Ace's existing command-line logic for the GUI. Runs the exact same
 * {@link Parser}/{@link TaskManager}/{@link Storage}/{@link Ui} code the CLI
 * uses, capturing whatever it prints so it can be returned as a String
 * instead — this keeps GUI responses byte-for-byte consistent with the CLI's
 * own output, since it's the same code producing them, not a re-implementation.
 */
public class AceCore {
    private final TaskManager taskManager;
    private final Ui ui;
    private boolean isExit;
    private boolean isError;

    /** Creates a new AceCore, loading any previously saved tasks. */
    public AceCore() {
        this.taskManager = new TaskManager();
        this.ui = new Ui();
        Storage.load(taskManager);
    }

    /**
     * Returns the greeting shown in the GUI's first chat bubble. This is a
     * GUI-specific plain-text greeting, separate from {@link Ui#showWelcome()}'s
     * CLI banner (ASCII logo and dividers), which doesn't suit a chat bubble.
     *
     * @return the welcome message text.
     */
    public String getWelcomeMessage() {
        return "Hi! I'm Ace, your poker-themed task manager! Shall I deal you out a few cards to get started?";
    }

    /**
     * Processes one line of user input and returns Ace's response — the same
     * text the CLI would have printed for that input, minus the CLI's own
     * divider lines, which don't apply inside a chat bubble.
     *
     * @param input the raw line of user input.
     * @return Ace's response text.
     */
    public String getResponse(String input) {
        isError = false;
        return capture(() -> {
            try {
                isExit = Parser.parseCommand(input, taskManager, ui);
            } catch (AceException exception) {
                isError = true;
                ui.showError(exception.getMessage());
            }
        });
    }

    /**
     * Returns whether the most recently processed command was "bye".
     *
     * @return true if the last command was "bye", false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the most recently processed command resulted in an
     * error (i.e. threw an {@link AceException}), so the GUI can highlight
     * that reply differently from a normal response.
     *
     * @return true if the last command's response was an error message.
     */
    public boolean isError() {
        return isError;
    }

    private String capture(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().stripTrailing();
    }
}
