package aegis;

import java.io.IOException;
import java.util.Scanner;

import aegis.ui.TextUi;

/**
 * Runs the Aegis chatbot, which manages tasks through command-line input.
 */
public class Aegis {

    private Storage storage;
    private TaskList tasks;
    private TextUi ui;
    private Parser parser;

    /**
     * Constructs an Aegis chatbot with a file path specified to store chat logs.
     */
    public Aegis(String filePath) {
        ui = new TextUi();
        storage = new Storage(filePath);
        parser = new Parser();
        assert ui != null : "TextUi must be initialized before handling user interactions";
        assert storage != null : "Storage must be initialized before loading tasks";
        assert parser != null : "Parser must be initialized before processing commands";
        try {
            tasks = new TaskList(storage.load());
        } catch (AegisException e) {
            System.out.println(ui.getLoadingErrorMessage());
            tasks = new TaskList();
        } catch (IOException e) {
            System.out.println(ui.getErrorMessage(e.getMessage()));
            tasks = new TaskList();
        }
        assert tasks != null : "TaskList must always be available after Aegis construction";
    }

    /**
     * Creates an Aegis object and calls run() on it.
     */
    public static void main(String[] args) {
        new Aegis("data/aegis.txt").run();
    }


    /**
     * Executes the chatbot interactions.
     */
    public void run() {
        System.out.println(ui.getStartMessage());
        Scanner scanner = new Scanner(System.in);
        boolean hasEnded = false;

        while (!hasEnded) {
            try {
                Command command = parser.parse(scanner.nextLine());
                System.out.println(command.execute(tasks, ui, storage));
                hasEnded = command.hasEnded();
            } catch (AegisException e) {
                System.out.println(ui.getErrorMessage(e.getMessage()));
            } catch (IOException e) {
                System.out.println(ui.getErrorMessage("Something went wrong: " + e.getMessage()));
            }
        }
    }

    /**
     * Returns the opening message shown when a chat session starts.
     */
    public String getStartMessage() {
        return ui.getStartMessage();
    }

    /**
     * Processes one user input and returns the chatbot response for the GUI.
     */
    public String getResponse(String input) {
        assert input != null : "GUI input should not be null";
        try {
            Command command = parser.parse(input);
            return command.execute(tasks, ui, storage);
        } catch (AegisException e) {
            return ui.getErrorMessage(e.getMessage());
        } catch (IOException e) {
            return ui.getErrorMessage("Something went wrong: " + e.getMessage());
        }
    }
}
