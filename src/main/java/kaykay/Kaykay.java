package kaykay;

import java.io.IOException;

import kaykay.command.Command;
import kaykay.exception.KaykayException;
import kaykay.model.ApplicationData;
import kaykay.parser.Parser;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Entry point and coordinator for the Kaykay chatbot.
 */
public class Kaykay {
    /** Provides console input and output. */
    private final Ui ui;

    /** Loads and saves the chatbot's tasks. */
    private final Storage storage;

    /** Holds the information managed during this run. */
    private final ApplicationData data;

    /** Interprets each line of user input. */
    private final Parser parser;

    /** Whether loading the initial task data failed. */
    private final boolean hasInitialDataLoadFailed;

    /**
     * Creates a Kaykay chatbot using the given task data file.
     *
     * @param filePath path of the task data file.
     */
    public Kaykay(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a Kaykay chatbot using the given data file and UI.
     *
     * @param filePath path of the task data file.
     * @param ui UI used to render chatbot output.
     */
    public Kaykay(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);
        parser = new Parser();
        ApplicationData loadedData;
        boolean hasInitialDataLoadFailed;
        try {
            loadedData = storage.loadData();
            hasInitialDataLoadFailed = false;
        } catch (IOException exception) {
            loadedData = new ApplicationData();
            hasInitialDataLoadFailed = true;
        }
        data = loadedData;
        this.hasInitialDataLoadFailed = hasInitialDataLoadFailed;
    }

    /**
     * Processes one command and renders its result through the configured UI.
     *
     * @param input command entered by the user.
     * @return true if the command ends the chatbot session.
     */
    public boolean shouldExitAfterProcessingCommand(String input) {
        try {
            Command command = parser.parse(input);
            assert command != null : "Parser must return a command for valid input";
            command.execute(data, ui, storage);
            return command.isExit();
        } catch (KaykayException exception) {
            ui.showError(exception.getMessage());
        } catch (IOException exception) {
            ui.showError("I couldn't save your data. Please check the data folder.");
        }
        return false;
    }

    /** Shows a startup warning when the existing data could not be loaded. */
    public void showInitialDataLoadError() {
        if (hasInitialDataLoadFailed) {
            ui.showLoadingError();
        }
    }

    /** Shows a farewell that accurately describes whether startup data was protected. */
    public void showFarewell() {
        if (hasInitialDataLoadFailed) {
            ui.showProtectedDataFarewell();
        } else {
            ui.showFarewell();
        }
    }

    /** Runs the chatbot until the user says bye or input ends. */
    public void run() {
        ui.showWelcome();
        showInitialDataLoadError();
        while (ui.hasNextLine()) {
            String input = ui.readCommand();
            if (shouldExitAfterProcessingCommand(input)) {
                break;
            }
        }
        showFarewell();
    }

    /**
     * Starts Kaykay with its default task data file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Kaykay("data/kaykay.txt").run();
    }
}
