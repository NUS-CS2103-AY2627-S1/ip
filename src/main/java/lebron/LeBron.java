package lebron;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import lebron.command.Command;
import lebron.exception.LeBronException;
import lebron.parser.Parser;
import lebron.storage.Storage;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Entry point of the LeBron task-list application.
 * Wires together the {@link Ui}, {@link Storage}, and {@link TaskList} components.
 * Can be driven either by the CLI loop in {@link #run()} or, for the GUI,
 * one message at a time via {@link #getResponse(String)}.
 */
public class LeBron {
    private static final String SEPARATOR = "____________________________________________________________";

    private Storage storage;
    private TaskList taskList;
    private Ui ui;
    private boolean isExit = false;

    /**
     * Constructs a LeBron instance, loading any previously saved TaskList.
     * If no save file exists or it cannot be loaded, starts with an empty TaskList.
     */
    public LeBron() {
        this(new Storage());
    }

    /**
     * Constructs a LeBron instance using the given Storage, loading any previously saved
     * TaskList from it. Package-private; lets tests use a temporary Storage instead of
     * the real save file.
     *
     * @param storage The Storage to load from and save to.
     */
    LeBron(Storage storage) {
        ui = new Ui();
        this.storage = storage;
        try {
            taskList = storage.load();
        } catch (IOException e) {
            System.out.println(ui.showMessage("Couldn't load your saved grind list, starting fresh."));
            taskList = new TaskList(new ArrayList<>());
        }
    }

    /**
     * Runs the main command loop: greets the user, then repeatedly reads,
     * parses, and executes commands until a "bye" command is received.
     */
    public void run() {
        System.out.println(SEPARATOR);
        System.out.println(ui.showBanner());
        System.out.println(SEPARATOR);
        while (!isExit) {
            String input = ui.readCommand();
            String response = getResponse(input);
            System.out.println(indent(response));
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Parses and executes a single line of user input, returning LeBron's reply.
     * Used by the GUI to process one chat message at a time; {@link #run()} uses
     * it too, so the CLI and GUI always share the exact same command-handling logic.
     *
     * @param input Raw user input.
     * @return LeBron's reply to the input.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            String response = command.execute(taskList, ui);
            assert response != null : "Every Command should return a non-null reply message";
            isExit = command.isExit();
            return isExit ? response : response + saveAndGetErrorSuffix();
        } catch (LeBronException e) {
            return ui.showMessage(e.getMessage());
        } catch (DateTimeParseException e) {
            return ui.showMessage(
                    "That date don't look right, use yyyy-MM-dd HHmm man, e.g. 2019-10-15 1800.");
        }
    }

    /**
     * Saves the current TaskList to disk, returning an appendable error message
     * ("" on success) so the caller can tack any failure onto the command's own
     * reply instead of losing it.
     *
     * @return "" if the save succeeded, or a "\n"-prefixed error message if it failed.
     */
    private String saveAndGetErrorSuffix() {
        try {
            storage.save(taskList);
            return "";
        } catch (IOException e) {
            return "\n" + ui.showMessage("Couldn't save your grind list.");
        }
    }

    /**
     * Returns whether the most recent command processed should end the program.
     *
     * @return true if the program should exit, false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns LeBron's initial greeting, for display when a conversation starts.
     *
     * @return The greeting message.
     */
    public String getGreeting() {
        return ui.showBanner();
    }

    /**
     * Prefixes every line of the given text with four spaces, matching LeBron's CLI style.
     *
     * @param text The text to indent.
     * @return The indented text.
     */
    private static String indent(String text) {
        return "    " + text.replace("\n", "\n    ");
    }

    /**
     * Starts the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new LeBron().run();
    }
}
