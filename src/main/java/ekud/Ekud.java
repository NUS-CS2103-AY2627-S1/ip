package ekud;

import ekud.command.Command;
import ekud.command.DeleteCommand;
import ekud.command.MarkCommand;
import ekud.command.TaskCreatingCommand;
import ekud.command.UnmarkCommand;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.TaskList;
import ekud.ui.DialogStyle;
import ekud.ui.Ui;

/**
 * Entry point for the Ekud chatbot.
 * On startup, it loads any saved tasks. The CLI greets the user and reads
 * commands until {@code bye}. The GUI calls {@link #getResponse} once per
 * message. Command parsing, disk storage, the in-memory list, and user-facing
 * text are delegated to {@link Parser}, {@link Storage}, {@link TaskList},
 * and {@link Ui} respectively.
 */
public class Ekud {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    /**
     * Set if the save file exists but cannot be read. Shown after the welcome
     * banner so the greeting is not interrupted by a raw constructor error.
     */
    private final String startupError;
    private boolean isExit;
    private DialogStyle lastDialogStyle;

    /**
     * Creates a chatbot and loads tasks from {@link Storage#DEFAULT_PATH}.
     * If the file is missing, the list starts empty. If the file cannot be
     * read, the list also starts empty and {@link #startupError} is set.
     */
    public Ekud() {
        this(new Ui(), new Storage(Storage.DEFAULT_PATH));
    }

    /**
     * Creates a chatbot with the given UI and storage.
     * The GUI and tests use this so they can avoid the terminal and the default
     * save file.
     *
     * @param ui      where replies are sent
     * @param storage where the task list is persisted
     */
    public Ekud(Ui ui, Storage storage) {
        // Callers (GUI, CLI, tests) must supply both collaborators; a null here is a bug.
        assert ui != null : "Ui must be provided";
        assert storage != null : "Storage must be provided";
        this.ui = ui;
        this.storage = storage;
        this.isExit = false;
        this.lastDialogStyle = DialogStyle.NONE;
        String error = null;
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (EkudException e) {
            error = e.getMessage();
            loaded = new TaskList();
        }
        this.tasks = loaded;
        this.startupError = error;
    }

    /**
     * Starts the command-line chatbot: prints a greeting (including a dad joke),
     * then reads and runs commands until the user types {@code bye}.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Ekud().run();
    }

    /**
     * Runs the welcome screen and the main command loop.
     */
    public void run() {
        ui.showWelcome();
        if (startupError != null) {
            ui.showError(startupError);
        }
        boolean isRunning = true;
        while (isRunning) {
            ui.showPrompt();
            String input = ui.readCommand();
            try {
                Command command = executeInput(input);
                isRunning = !command.isExit();
            } catch (EkudException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting text for the first GUI dialog box, without the
     * ASCII banner. Startup save-file errors are returned separately by
     * {@link #getStartupError} so the GUI can style them as error bubbles.
     *
     * @return welcome text without the ASCII banner
     */
    public String getWelcomeMessage() {
        ui.showWelcome();
        return ui.getLastMessage();
    }

    /**
     * Returns the save-file error from startup, if any.
     *
     * @return the error text, or {@code null} if the file loaded or was missing
     */
    public String getStartupError() {
        return startupError;
    }

    /**
     * Parses and executes one command, then returns the reply text for the GUI.
     *
     * @param input the line typed by the user
     * @return the reply to show in a dialog box
     */
    public String getResponse(String input) {
        try {
            Command command = executeInput(input);
            isExit = command.isExit();
            lastDialogStyle = toDialogStyle(command);
            return ui.getLastMessage();
        } catch (EkudException e) {
            lastDialogStyle = DialogStyle.ERROR;
            ui.showError(e.getMessage());
            return ui.getLastMessage();
        }
    }

    /**
     * Returns whether the last {@link #getResponse} was the exit command.
     *
     * @return {@code true} if the GUI should close
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the dialog-bubble style for the last {@link #getResponse}.
     * {@link DialogStyle#ERROR} when the last input was invalid.
     * {@link DialogStyle#NONE} when the last successful command has no tint.
     *
     * @return the style for the latest reply
     */
    public DialogStyle getDialogStyle() {
        return lastDialogStyle;
    }

    /**
     * Parses one line and runs it against the task list.
     *
     * @param input the line typed by the user
     * @return the command that ran
     * @throws EkudException if parsing or execution fails
     */
    private Command executeInput(String input) throws EkudException {
        Command command = Parser.parse(input);
        // Parser throws on invalid input; a null return would be a bug in Parser, not the user.
        assert command != null : "Parser.parse returned null instead of a Command";
        command.execute(tasks, ui, storage);
        return command;
    }

    /**
     * Maps a command to the reply-bubble style used by the GUI.
     *
     * @param command the command that just ran
     * @return the matching style, or {@link DialogStyle#NONE}
     */
    private static DialogStyle toDialogStyle(Command command) {
        // getResponse calls this only after Parser.parse succeeds, so command must exist.
        assert command != null : "toDialogStyle received a null command";
        if (command instanceof TaskCreatingCommand) {
            return DialogStyle.ADD;
        }
        if (command instanceof MarkCommand || command instanceof UnmarkCommand) {
            return DialogStyle.CHANGE_MARK;
        }
        if (command instanceof DeleteCommand) {
            return DialogStyle.DELETE;
        }
        return DialogStyle.NONE;
    }
}
