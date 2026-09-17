package glennon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import glennon.command.Command;
import glennon.exception.GlennonException;
import glennon.task.TaskList;

/**
 * Starts the Glennon chatbot.
 */
public class Glennon {
    /** Interface used for all console input and output. */
    private final Ui ui;

    /** Storage used to load and save missions. */
    private final Storage storage;

    /** Missions available during the current application session. */
    private TaskList missions;

    /** Whether the GUI session has attempted to load saved missions. */
    private boolean isInitialized;

    /** Whether the GUI session has received an exit command. */
    private boolean hasExited;

    /** Load failure that prevents the GUI from overwriting unreadable data. */
    private String startupError;

    /**
     * Creates a Glennon application using the standard mission data file.
     */
    public Glennon() {
        this("data/glennon.txt");
    }

    /**
     * Creates a Glennon application using the specified data file.
     *
     * @param dataPath path of the file used to persist missions.
     */
    public Glennon(String dataPath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(dataPath));
        this.missions = new TaskList();
    }

    /**
     * Loads saved missions once and returns the GUI greeting or startup error.
     *
     * @return greeting suitable for a dialog bubble.
     */
    public String getWelcome() {
        initializeSession();
        return startupError == null
                ? "Hey there! Glennon online.\nWhat's the mission?"
                : "Mission control alert!\n" + startupError;
    }

    /**
     * Executes one GUI command using the same parser and commands as the console.
     *
     * @param input complete command entered by the user.
     * @return formatted response without console dividers.
     */
    public String getResponse(String input) {
        return getCommandResponse(input).text();
    }

    /**
     * Executes one GUI command and preserves its error status for presentation.
     *
     * @param input complete command entered by the user.
     * @return formatted response and whether the command reported an error.
     */
    public CommandResponse getCommandResponse(String input) {
        initializeSession();
        StringWriter response = new StringWriter();
        Ui responseUi = new Ui(new PrintWriter(response, true));
        if (startupError != null) {
            responseUi.showError(startupError);
        } else if (hasExited) {
            responseUi.showGoodbye();
        } else {
            hasExited = executeCommand(input, responseUi);
        }
        String text = normalizeLineEndings(response.toString()).stripTrailing();
        return new CommandResponse(text, responseUi.hasError());
    }

    /**
     * Converts platform-specific line endings into line feeds for GUI responses.
     *
     * @param text response text that may contain platform-specific line endings.
     * @return response text using line feeds consistently.
     */
    static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }

    /**
     * Returns whether the GUI session has received an exit command.
     *
     * @return true when the session has received an exit command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Reports whether loading saved missions failed and blocked this session.
     *
     * @return true when saved data could not be loaded safely.
     */
    public boolean hasStartupError() {
        return startupError != null;
    }

    /** Loads data before the first GUI interaction without discarding session changes. */
    private void initializeSession() {
        if (isInitialized) {
            return;
        }
        try {
            missions = new TaskList(storage.loadMissions());
        } catch (GlennonException e) {
            startupError = e.getMessage();
        }
        isInitialized = true;
    }

    /**
     * Parses and executes one command, reporting recoverable failures through
     * the supplied user interface.
     *
     * @param input complete command entered by the user.
     * @param currentUi interface that receives the command result.
     * @return true when the command ends the current session.
     */
    private boolean executeCommand(String input, Ui currentUi) {
        try {
            Command command = Parser.parse(input);
            command.execute(missions, currentUi, storage);
            return command.isExit();
        } catch (GlennonException e) {
            currentUi.showError(e.getMessage());
            return false;
        }
    }

    /**
     * Greets the user, loads saved missions, processes commands, and closes the
     * user interface when input ends or the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();

        try {
            missions = new TaskList(storage.loadMissions());
        } catch (GlennonException e) {
            ui.showError(e.getMessage());
            ui.close();
            return;
        }

        boolean isSigningOff = false;
        while (!isSigningOff && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            isSigningOff = executeCommand(command, ui);
            ui.showDivider();
        }

        ui.close();
    }

    /**
     * Starts Glennon using its standard mission data file.
     *
     * @param args command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        new Glennon("data/glennon.txt").run();
    }

}
