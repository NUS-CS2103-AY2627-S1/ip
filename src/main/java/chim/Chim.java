package chim;

import java.util.Scanner;

import chim.exception.ChimException;
import chim.parser.Parser;
import chim.storage.Storage;
import chim.task.TaskList;
import chim.ui.Ui;

/**
 * Represents the Chim chatbot: wires together Ui, Storage, TaskList, and
 * Parser, and supports both a command-line loop and single-response
 * queries for a GUI.
 */
public class Chim {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private boolean lastResponseIsError;

    /**
     * Creates a Chim chatbot that loads and saves its tasks at the given
     * file path.
     *
     * @param filePath Relative path to the data file, e.g. "./data/chim.txt".
     */
    public Chim(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList(storage.load());
    }

    /**
     * Creates a Chim chatbot using the default data file path.
     */
    public Chim() {
        this("./data/chim.txt");
    }

    /**
     * Prints the greeting, then repeatedly reads and processes user
     * commands until the user types "bye" (command-line mode).
     */
    public void run() {
        ui.showWelcome();
        if (storage.getSkippedLineCount() > 0) {
            ui.printMessage(storage.getSkippedLineCount()
                    + " line(s) in your saved data looked off, so I skipped them.");
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim();
            try {
                String response = parser.parseAndExecute(input, tasks, ui, storage);
                ui.printMessage(response);
                if (input.equals("bye")) {
                    break;
                }
            } catch (ChimException e) {
                ui.printMessage(e.getMessage());
            } catch (NumberFormatException e) {
                ui.printMessage("OOPS!!! Please provide a valid task number.");
            } catch (ArrayIndexOutOfBoundsException e) {
                ui.printMessage("OOPS!!! That task number doesn't exist in your list.");
            }
        }

        scanner.close();
    }

    /**
     * Generates a response for the user's chat message (GUI mode).
     *
     * @param input Raw user input.
     * @return Chim's reply, or an error message if the input is invalid.
     */
    public String getResponse(String input) {
        try {
            String response = parser.parseAndExecute(input, tasks, ui, storage);
            lastResponseIsError = false;
            return response;
        } catch (ChimException e) {
            lastResponseIsError = true;
            return e.getMessage();
        } catch (NumberFormatException e) {
            return "That doesn't look like a valid task number to me!";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Hmm, I don't see a task with that number on your list.";
        }
    }

    /**
     * Returns whether the most recent call to getResponse resulted in an error.
     *
     * @return true if the last response was an error message.
     */
    public boolean isLastResponseError() {
        return lastResponseIsError;
    }

    /**
     * Returns whether the given input is the exit command.
     *
     * @param input Raw user input.
     * @return true if the input is "bye".
     */
    public boolean isExit(String input) {
        return input.equals("bye");
    }

    /**
     * Starts the Chim chatbot in command-line mode.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Chim("./data/chim.txt").run();
    }
}
