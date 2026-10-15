package summer;

import java.nio.file.Path;

import summer.command.Command;
import summer.parser.Parser;
import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/**
 * The Summer chatbot. Wires together the {@link Ui}, {@link Storage}, and
 * {@link TaskList}.
 *
 * <p>{@link #main} runs the read-parse-execute loop against standard input; the
 * GUI instead creates a {@code Summer} and calls {@link #getResponse} per message.
 */
public class Summer {
    private static final int MAX_TASKS = 100;
    private static final Path DATA_FILE_PATH = Path.of("data", "summer.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit;
    private boolean isError;

    /** Creates a chatbot that loads its tasks from the default save file. */
    public Summer() {
        this.ui = new Ui();
        this.storage = new Storage(DATA_FILE_PATH);
        this.tasks = new TaskList(MAX_TASKS);
        this.storage.load().forEach(this.tasks::add);
        if (this.storage.getSkippedLineCount() > 0) {
            this.ui.showCorruptedLinesSkipped(this.storage.getSkippedLineCount());
        }
    }

    /**
     * Runs one line of user input and returns Summer's reply.
     *
     * @param input one line of user input
     * @return the reply text to show the user
     */
    public String getResponse(String input) {
        this.isError = false;
        try {
            Command command = Parser.parse(input);
            command.execute(this.tasks, this.ui, this.storage);
            this.isExit = command.isExit();
        } catch (SummerException e) {
            this.ui.showError(e.getMessage());
            this.isError = true;
        }
        return this.ui.flush();
    }

    /**
     * Returns whether the last {@link #getResponse} call was the {@code bye}
     * command. The GUI uses this to close its window after showing the goodbye.
     *
     * @return true if the user has just exited
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Returns whether the last {@link #getResponse} call failed with a
     * {@link SummerException}. The GUI uses this to highlight error replies.
     *
     * @return true if the last input caused an error
     */
    public boolean isError() {
        return this.isError;
    }

    /**
     * Starts the text-based chatbot: reads commands from standard input and
     * runs them until the user types {@code bye} or input ends.
     *
     * @param args command line arguments, not used by this program
     */
    public static void main(String[] args) {
        Summer summer = new Summer();
        Ui ui = summer.ui;

        ui.showWelcome();

        String fullCommand;
        while (!summer.isExit && (fullCommand = ui.readCommand()) != null) {
            ui.showLine();
            summer.getResponse(fullCommand); // prints as it goes; the returned copy is for the GUI
            ui.showLine();
        }
        ui.close();
    }
}
