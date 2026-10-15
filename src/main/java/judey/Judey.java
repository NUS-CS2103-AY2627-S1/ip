package judey;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import judey.command.Command;
import judey.exception.JudeyException;
import judey.parser.Parser;
import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/** Main entry point for the judey.Judey task manager application. */
public class Judey {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates an instance of judey with a file path where the list of tasks would be stored
     * @param filePath location that the list of tasks is stored to
     */
    public Judey(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (JudeyException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Creates an instance of judey with the default save file location.
     * Needed by the GUI, which does not pass a file path explicitly.
     */
    public Judey() {
        this("data/judey.txt");
    }

    /**
     * Returns judey's welcome greeting as a String, for use by the GUI.
     * The CLI shows the same greeting via {@link Ui#showWelcome()}.
     *
     * @return the welcome message text
     */
    public String getWelcomeMessage() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(capturedOutput));
        try {
            ui.showWelcome();
        } finally {
            System.setOut(originalOut);
        }
        return capturedOutput.toString().trim();
    }

    /**
     * Processes a single line of user input and returns judey's reply as a String,
     * instead of printing it straight to the console. Used by the GUI.
     *
     * @param input raw command entered by the user
     * @return judey's response text
     */
    public String getResponse(String input) {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(capturedOutput));
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
        } catch (JudeyException e) {
            ui.showErrorWithMarkup(e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
        return capturedOutput.toString().trim();
    }

    /** Runs the chatbot main loop. */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (JudeyException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Judey("data/judey.txt").run();
    }
}
