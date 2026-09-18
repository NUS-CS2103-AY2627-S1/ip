package zhangwei;

import zhangwei.command.Command;
import zhangwei.parser.Parser;
import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * Entry point of ZhangWei, a chatbot that keeps track of the user's tasks.
 *
 * <p>This class only wires the other classes together and runs the command
 * loop: the {@link Ui} talks to the user, the {@link Parser} makes sense of
 * what was typed, each {@link Command} carries one instruction out, the
 * {@link TaskList} holds the tasks and the {@link Storage} keeps them on disk.
 *
 * <p>It offers two ways in. {@link #run()} drives the console version, reading
 * one line at a time until the user says bye. {@link #getResponse(String)}
 * answers one line and returns what would have been printed, which is what the
 * graphical version calls each time the user presses Send.
 */
public class ZhangWei {

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * A complaint about the save file, or null if it loaded cleanly. It is
     * held rather than printed because loading happens before the greeting,
     * and the message belongs after it, where the user is looking.
     */
    private final String loadMessage;

    /** Whether a command has asked for the session to end. */
    private boolean isExitRequested = false;

    /**
     * Whether the last {@link #getResponse(String)} answered with a complaint
     * rather than a result. The graphical version reads this to decide how the
     * reply should be shown.
     */
    private boolean isErrorResponse = false;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * <p>A save file that cannot be read is not fatal: the chatbot starts with
     * an empty list and says so, after the storage has kept a copy of the
     * file so it can be repaired, because a chatbot that still works is more
     * useful than one that refuses to start.
     *
     * @param filePath path of the text file the tasks are loaded from and
     *     saved to, e.g. "./data/zhangwei.txt".
     */
    public ZhangWei(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        String message = null;
        TaskList loadedTasks;
        try {
            Storage.LoadResult loaded = storage.loadTasks();
            loadedTasks = new TaskList(loaded.tasks());
            if (loaded.skippedLines() > 0) {
                message = "I could not understand " + loaded.skippedLines()
                        + " line(s) in your save file, so I skipped them.";
                if (loaded.backupPath() != null) {
                    message += " The original file is kept at "
                            + loaded.backupPath() + ".";
                }
            }
        } catch (ZhangWeiException e) {
            loadedTasks = new TaskList();
            message = e.getMessage() + " Starting with an empty task list.";
        }
        this.tasks = loadedTasks;
        this.loadMessage = message;
    }

    /** Greets the user, then runs commands until one of them ends the session. */
    public void run() {
        ui.showWelcome();
        if (loadMessage != null) {
            ui.showMessage(loadMessage);
        }

        while (!isExitRequested) {
            // The console has already shown everything said so far, so the
            // buffered copy kept for the graphical version is thrown away.
            ui.clearOutput();

            // Anything the chatbot can explain to the user arrives here as a
            // ZhangWeiException, so one handler reports them all.
            try {
                String fullCommand = ui.readCommand();
                // A blank line asks for nothing, so nothing is said about it.
                if (fullCommand.isBlank()) {
                    continue;
                }
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExitRequested = command.isExit();
            } catch (ZhangWeiException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting the graphical version shows before the user has
     * typed anything, together with any complaint about the save file.
     *
     * @return the text of the opening message.
     */
    public String getWelcomeMessage() {
        ui.showGreeting();
        if (loadMessage != null) {
            ui.showMessage(loadMessage);
        }
        return ui.drainOutput();
    }

    /**
     * Carries out one line of input and returns the chatbot's reply.
     *
     * <p>This is the console loop's body without the loop: the same parser,
     * the same commands and the same wording, gathered into a string instead
     * of left on the screen.
     *
     * @param input one line exactly as the user typed it.
     * @return what the chatbot has to say about it, or an empty string if the
     *     line was blank and so asked for nothing.
     */
    public String getResponse(String input) {
        assert input != null : "The graphical front end should never send a null line.";

        if (input.isBlank()) {
            return "";
        }

        isErrorResponse = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
        } catch (ZhangWeiException e) {
            isErrorResponse = true;
            ui.showError(e.getMessage());
        }
        return ui.drainOutput();
    }

    /**
     * Returns whether the reply to the last line of input was a complaint
     * about that line, so that the graphical version can highlight it.
     *
     * @return true if the last command failed, false if it was carried out.
     */
    public boolean isErrorResponse() {
        return isErrorResponse;
    }

    /**
     * Returns whether the last command asked for the session to end, so that
     * the graphical version knows when to close its window.
     *
     * @return true once the user has said bye, false until then.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Starts the console version of the chatbot with its default save file.
     *
     * <p>The graphical version starts from
     * {@link zhangwei.gui.Launcher} instead.
     *
     * @param args command line arguments, which this chatbot does not use.
     */
    public static void main(String[] args) {
        new ZhangWei("./data/zhangwei.txt").run();
    }
}
