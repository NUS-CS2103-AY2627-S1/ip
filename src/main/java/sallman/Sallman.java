package sallman;

import java.util.Arrays;
import java.util.List;

import sallman.command.Command;

/**
 * Entry point of the saLLMan chatbot.
 * <p>
 * Holds the three parts the app is built from and runs the command loop over
 * them: a {@link Ui} for talking to the user, a {@link Storage} for the saved
 * task list, and a {@link TaskList} for the tasks themselves.
 * <p>
 * The loop itself knows nothing about individual commands: it asks
 * {@link Parser} for a {@link Command} and lets that command carry itself out.
 */
public class Sallman {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "saLLMan";

    /**
     * Where the task list is saved when no other path is given on the command
     * line. Relative to the folder the app is run from, so it works on any
     * computer.
     */
    private static final String DEFAULT_DATA_PATH = "data/sallman.txt";

    /** Talks to the user. */
    private final Ui ui;

    /** Reads and writes the saved task list. */
    private final Storage storage;

    /** The tasks being tracked. */
    private final TaskList tasks;

    /** Set once a command asks to end the session. */
    private boolean isExitRequested;

    /** Name of the command class that answered the last {@link #getResponse}. */
    private String lastCommandType = "";

    /** Whether the last {@link #getResponse} reported a problem. */
    private boolean isLastResponseError;

    /**
     * Creates a chatbot backed by the given data file, loading whatever is
     * already saved there.
     * <p>
     * A file that cannot be read leaves the chatbot usable with an empty list,
     * rather than stopping it from starting at all.
     *
     * @param filePath where the task list is saved
     */
    public Sallman(String filePath) {
        this(filePath, true);
    }

    /**
     * Creates a chatbot backed by the given data file.
     *
     * @param filePath            where the task list is saved
     * @param isPrintingToConsole false when a GUI will display the replies
     *                            instead of the console printing them
     */
    public Sallman(String filePath, boolean isPrintingToConsole) {
        this.ui = new Ui(isPrintingToConsole);
        this.storage = new Storage(filePath);
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (SallmanException e) {
            ui.showError(e);
            loaded = new TaskList();
        }
        // Whichever branch above ran, the chatbot starts with a usable list:
        // a failed load leaves an empty one rather than nothing at all.
        assert loaded != null : "the chatbot must start with a task list";
        this.tasks = loaded;
    }

    /** Greets the user, then handles commands until they ask to stop. */
    public void run() {
        ui.showWelcome(NAME);
        ui.showSkippedLines(storage.getSkippedLines());

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            if (fullCommand.isEmpty()) {
                continue; // a blank line is not worth complaining about
            }
            try {
                Command command = Parser.parse(fullCommand);
                // Parser.parse either returns a command or throws, so there is
                // no third case for the loop to handle.
                assert command != null : "parser returned no command for: " + fullCommand;
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (SallmanException e) {
                ui.showError(e);
            } catch (RuntimeException e) {
                ui.showError(unexpectedError(e));
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting shown when the chatbot starts, for a front end that
     * displays it rather than letting the console print it.
     *
     * @return the welcome message, and any complaint about the saved data
     */
    public String getGreeting() {
        ui.showWelcome(NAME);
        ui.showSkippedLines(storage.getSkippedLines());
        return ui.drainText();
    }

    /**
     * Carries out one command and returns what the chatbot has to say about it.
     * <p>
     * Errors come back as text like any other reply, since a GUI has nowhere to
     * let an exception escape to.
     *
     * @param input one line of user input
     * @return the reply to show the user
     */
    public String getResponse(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        isLastResponseError = false;
        try {
            Command command = Parser.parse(trimmed);
            assert command != null : "parser returned no command for: " + trimmed;
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
            lastCommandType = command.getClass().getSimpleName();
        } catch (SallmanException e) {
            ui.showError(e);
            lastCommandType = "";
            isLastResponseError = true;
        } catch (RuntimeException e) {
            ui.showError(unexpectedError(e));
            lastCommandType = "";
            isLastResponseError = true;
        }
        return ui.drainText();
    }

    /**
     * Turns a failure the chatbot did not anticipate into a reply.
     * <p>
     * This is the outermost point a command runs from. An exception escaping it
     * would end the console session, or leave the GUI with a reply that never
     * arrives, so a bug is reported as one failed command instead. The details
     * still go to the error stream for whoever is debugging.
     *
     * @param e the unexpected failure
     * @return an error phrased for the user
     */
    private static SallmanException unexpectedError(RuntimeException e) {
        e.printStackTrace();
        return new SallmanException("I apologise, but something went wrong on my side while doing that.",
                "Please check your list with: list");
    }

    /**
     * Returns whether the last command asked to end the session, so a GUI knows
     * when to close its window.
     *
     * @return true once a command has asked to exit
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the reply to the last input reported a problem, so a front
     * end can make errors stand out from ordinary replies.
     *
     * @return true if the last command could not be carried out
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns the kind of command that answered the last input, for a front end
     * that styles replies by what happened.
     *
     * @return the command's class name, or an empty string if it failed
     */
    public String getLastCommandType() {
        return lastCommandType;
    }

    /**
     * Starts the chatbot.
     *
     * @param args optionally the data file path, for tests that must not touch
     *             the real task list
     */
    public static void main(String[] args) {
        new Sallman(dataPathFrom(Arrays.asList(args))).run();
    }

    /**
     * Returns the file to keep the task list in: the first command-line argument
     * if one was given, otherwise the default.
     * <p>
     * Both the console and the GUI decide the file here, so naming a file works
     * the same whichever one is started.
     *
     * @param args the command-line arguments the app was started with
     * @return the path of the data file to use
     */
    public static String dataPathFrom(List<String> args) {
        return args.isEmpty() ? DEFAULT_DATA_PATH : args.get(0);
    }
}
