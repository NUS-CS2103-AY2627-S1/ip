package chione;

import chione.command.Command;
import chione.task.TaskList;

/**
 * The Chione chatbot.
 *
 * <p>Chione tracks three kinds of task (todo, deadline and event), and can list,
 * mark, unmark, delete and search them by date. Each line of input is turned by
 * {@link Parser} into a {@link Command}, which then carries itself out; anything
 * that cannot be carried out is reported as a {@link ChioneException} and
 * explained to the user, so the conversation continues until {@code bye}.
 *
 * <p>This class does very little itself. It owns a {@link Ui}, a {@link Storage}
 * and a {@link TaskList}, and hands all three to whichever command it was given.
 * It has no branch per command and no knowledge of what any command does, so a
 * new command can be added without this file changing at all.
 *
 * <p>There are two ways in. {@link #run()} holds a conversation on the console,
 * and {@link #getResponse(String)} answers a single line and hands the reply back
 * to whoever asked — which is how the window talks to Chione. Both go through the
 * same parser, the same commands and the same {@link Ui}, so the two interfaces
 * cannot drift apart in what they say or accept.
 */
public class Chione {
    /**
     * Where the task list is kept between runs.
     *
     * <p>Relative to the folder Chione is started from, so the program works on
     * any machine; {@link Storage} turns it into a path suited to the operating
     * system it finds itself on.
     */
    private static final String SAVE_FILE_PATH = "data/chione.txt";

    /** Everything the user sees, and everything they type. */
    private final Ui ui;

    /** Where the tasks are read from and written back to. */
    private final Storage storage;

    /** The tasks being tracked; replaced by the saved ones when the run starts. */
    private TaskList tasks;

    /**
     * Whether the user has said goodbye.
     *
     * <p>The console loop could simply ask the command it just carried out, but
     * the window has no loop to ask from: it needs to be able to check, after any
     * reply, whether that reply was the last one.
     */
    private boolean isExit;

    /**
     * Whether the most recent reply reported a failure rather than a success.
     *
     * <p>Kept for the same reason as {@link #isExit}: the window only ever sees
     * the text of a reply, and needs to know whether to show that text as an
     * error. The text interface never asks; its replies say so in words.
     */
    private boolean isLastResponseError;

    /**
     * Prepares a chatbot that will keep its tasks in the given file.
     *
     * <p>Nothing is read from disk yet: loading happens in {@link #run()}, so that
     * the greeting appears before any complaint about the save file rather than
     * after it.
     *
     * @param filePath where to keep the tasks, e.g. {@code "data/chione.txt"}
     */
    public Chione(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
        this.isExit = false;
        this.isLastResponseError = false;
    }

    /**
     * Prepares a chatbot that keeps its tasks in the usual place.
     *
     * <p>JavaFX builds the window's chatbot for us, and has no file path to offer,
     * so this constructor supplies the default one.
     */
    public Chione() {
        this(SAVE_FILE_PATH);
    }

    /** Greets the user on the console, then answers commands until they say goodbye. */
    public void run() {
        ui.showWelcome();
        this.tasks = loadTasks();
        ui.printResponse();

        // hasNextCommand() also stops the loop when the input runs out without a
        // "bye" -- when a script is piped in, or the user presses Ctrl+D.
        while (!isExit && ui.hasNextCommand()) {
            handleInput(ui.readCommand());
            ui.printResponse();
        }

        if (!isExit) {
            // The input ran out rather than the user saying goodbye, so no command
            // has said it for us.
            ui.showGoodbye();
            ui.printResponse();
        }
        ui.close();
    }

    /**
     * Greets the user and loads the saved tasks, for an interface that shows the
     * greeting in a dialog box rather than printing it.
     *
     * <p>Loading happens here rather than in the constructor so that the greeting
     * is already in hand when a complaint about the save file is added to it, and
     * the two arrive together.
     *
     * @return the greeting, followed by any complaint about the save file
     */
    public String getWelcomeMessage() {
        ui.showGreeting();
        this.tasks = loadTasks();
        return ui.consumeResponse();
    }

    /**
     * Answers one line of input.
     *
     * <p>This is the whole of what the window needs: hand over a line, get back
     * what Chione has to say about it.
     *
     * @param input one line of user input
     * @return Chione's reply, ready to be shown
     */
    public String getResponse(String input) {
        handleInput(input);
        return ui.consumeResponse();
    }

    /**
     * Reports whether the user has said goodbye.
     *
     * @return {@code true} once a command has ended the conversation
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Reports whether the most recent reply was an error.
     *
     * @return {@code true} if the last line of input could not be carried out
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Carries out one line of input, leaving whatever it has to say with the
     * {@link Ui} for the caller to collect.
     *
     * @param input one line of user input
     */
    private void handleInput(String input) {
        this.isLastResponseError = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            this.isExit = command.isExit();
        } catch (ChioneException e) {
            // Every anticipated problem arrives here with a message already
            // phrased for the user, so one catch block covers them all and the
            // conversation carries on instead of crashing.
            this.isLastResponseError = true;
            ui.showError(e.getMessage());
        }
    }

    /**
     * Reads the saved tasks, or starts afresh if they cannot be read.
     *
     * <p>Chione is still usable without its save file, so a loading problem is
     * reported and stepped over rather than allowed to stop the program.
     *
     * @return the stored tasks, or an empty list if they could not be loaded
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (ChioneException e) {
            ui.showLoadingError(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Starts Chione's text interface.
     *
     * <p>The window is started by {@link chione.gui.Launcher} instead. This entry
     * point is kept because a console conversation can be piped in from a file,
     * which makes it the easier of the two to test against.
     *
     * @param args ignored; Chione takes no command-line arguments
     */
    public static void main(String[] args) {
        new Chione().run();
    }
}
