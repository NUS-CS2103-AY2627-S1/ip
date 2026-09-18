package milo;

import milo.command.Command;
import milo.exception.MiloException;
import milo.parser.Parser;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Entry point of the Milo chatbot.
 * Milo wires together the {@link Ui}, {@link Storage} and {@link TaskList}
 * and runs the read-parse-execute loop. It no longer knows how a command is
 * worded (that is {@link Parser}'s job) or how a task is stored on disk
 * (that is {@link Storage}'s job); it just connects the three together.
 */
public class Milo {
    /** Where the tasks are saved, relative to where Milo is run. */
    private static final String DATA_FILE_PATH = "data/milo.txt";

    /** How Milo talks to and reads from the user. */
    private final Ui ui;

    /** Where Milo's tasks are kept between runs. */
    private final Storage storage;

    /** The tasks currently in memory. */
    private final TaskList tasks;

    /** Whether the most recent {@link #getResponse} call was "bye". */
    private boolean isExit = false;

    /**
     * Whether the most recent {@link #getResponse} call could not be carried
     * out. Kept so an interface can show a failure differently from a normal
     * reply, which it otherwise has no way to tell apart: both come back as
     * plain text.
     */
    private boolean isError = false;

    /**
     * Creates Milo, loading whatever tasks were saved from a previous run.
     *
     * @param filePath where the tasks are saved, e.g. "data/milo.txt"
     */
    public Milo(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(storage.load());
    }

    /** Creates Milo, saving to and loading from the default save location. */
    public Milo() {
        this(DATA_FILE_PATH);
    }

    /** Greets the user, then reads and carries out commands until told to stop. */
    public void run() {
        ui.showWelcome();
        ui.showLoadStatus(storage.getLoadWarnings(), tasks.size());

        // hasNextCommand() guards against the input ending without "bye",
        // which would otherwise make readCommand() throw.
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            if (fullCommand.isEmpty()) {
                continue;
            }

            try {
                Command command = Parser.parse(fullCommand);
                rememberStateIfUndoable(command);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (MiloException e) {
                ui.showError(e.getMessage());
            }
        }

        // Printed once here regardless of why the loop ended, so input that
        // runs out without "bye" still gets a proper goodbye rather than the
        // session just stopping silently.
        ui.showGoodbye();
    }

    /**
     * Returns the message Milo opens a new session with: its greeting, plus
     * anything worth reporting about loading previously saved tasks. Meant
     * to be shown once when a new interface (such as the GUI) starts,
     * before the user has typed anything.
     *
     * @return the opening message
     */
    public String getWelcomeMessage() {
        String greeting = ui.getGreeting();
        String loadStatus = ui.showLoadStatus(storage.getLoadWarnings(), tasks.size());
        return loadStatus.isEmpty() ? greeting : greeting + "\n" + loadStatus;
    }

    /**
     * Parses and carries out one command, returning what Milo says back.
     * Used by interfaces (such as the GUI) that ask for one response at a
     * time, rather than driving the whole read-parse-execute loop the way
     * {@link #run} does.
     *
     * @param input one line of user input
     * @return Milo's reply, ready to be shown to the user
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            rememberStateIfUndoable(command);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
            isError = false;
            if (isExit) {
                ui.showGoodbye();
            }
            return ui.getLastResponse();
        } catch (MiloException e) {
            isExit = false;
            isError = true;
            return e.getMessage();
        }
    }

    /**
     * Snapshots the task list before a command that is about to change it,
     * so that "undo" has something to go back to. Asking the command rather
     * than checking its type here keeps the knowledge of which commands
     * change the list with the commands themselves.
     *
     * @param command the command about to be run
     */
    private void rememberStateIfUndoable(Command command) {
        if (command.isUndoable()) {
            tasks.saveSnapshot();
        }
    }

    /**
     * Returns whether the most recent {@link #getResponse} call failed.
     *
     * @return true if the last reply was an error message rather than the
     *         result of a command that ran
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns whether the most recent {@link #getResponse} call was "bye",
     * i.e. whether the session should now end.
     *
     * @return true if Milo is done
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts Milo, reading its saved tasks from and writing them back to
     * {@value #DATA_FILE_PATH}.
     *
     * @param args unused; Milo takes no command-line arguments
     */
    public static void main(String[] args) {
        new Milo().run();
    }
}
