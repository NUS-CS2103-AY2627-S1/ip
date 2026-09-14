package arrodes;

import arrodes.command.Command;
import arrodes.exception.ArrodesException;
import arrodes.parser.CommandParser;
import arrodes.storage.Storage;
import arrodes.task.TaskList;
import arrodes.ui.cli.Cli;
import arrodes.ui.gui.Gui;
import arrodes.ui.gui.GuiListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Runs the Arrodes command-line application.
 */
public class Arrodes extends Application {
    /** Maximum number of requests Arrodes can remember during one session. */
    private static final int MAX_ITEMS = 100;
    private Storage storage;
    private TaskList taskList;

    private Gui gui;
    private Cli cli;

    /** Creates an Arrodes application and loads its saved tasks. */
    public Arrodes() {
        this.storage = new Storage();
        try {
            taskList = storage.load(MAX_ITEMS);
        } catch (ArrodesException exception) {
            System.out.println(exception.getMessage());
            taskList = new TaskList(MAX_ITEMS);
        }
    }

    /**
     * Loads the graphical user interface layout and displays it in a window.
     *
     * @param stage primary application window
     */
    @Override
    public void start(Stage stage) {
        gui = new Gui();
        gui.attachInputListener((GuiListener) this::inputListener);

        Scene scene = new Scene(gui);
        stage.setTitle("Arrodes");
        stage.setScene(scene);
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        stage.show();
    }

    /** Runs the command-line interface until the user exits. */
    void run() {
        cli = new Cli();
        showCliStartupMessage();

        boolean isExit = false;
        while (!isExit) {
            isExit = processNextCliCommand();
        }
    }

    /** Displays the CLI startup message and its separator. */
    private void showCliStartupMessage() {
        cli.showOnLoadMessage();
        cli.showSeparator();
    }

    /** Processes one CLI command and returns whether it requests application exit. */
    private boolean processNextCliCommand() {
        String userCommand = cli.readUserInput();
        cli.showSeparator();
        if (userCommand == null) {
            cli.showOnExitMessage();
            return true;
        }
        try {
            return executeCliCommand(userCommand);
        } catch (ArrodesException knownArrodesException) {
            System.out.println(knownArrodesException.getMessage());
            return false;
        } catch (RuntimeException unexpectedException) {
            System.out.println(ArrodesException.UNEXPECTED_FAILURE);
            return false;
        } finally {
            cli.showSeparator();
        }
    }

    /** Parses and executes one CLI command. */
    private boolean executeCliCommand(String userCommand) {
        Command command = CommandParser.parse(userCommand);
        command.execute(cli, taskList, storage);
        return command.isExit();
    }

    /**
     * Processes a command submitted from the graphical user interface.
     *
     * @param userInput command submitted by the user
     */
    private void inputListener(String userInput) {
        try {
            Command command = CommandParser.parse(userInput);
            command.execute(gui, taskList, storage);
            if (command.isExit()) {
                Platform.exit();
            }
        } catch (ArrodesException knownArrodesException) {
            gui.showError(knownArrodesException.getMessage());
        } catch (RuntimeException unexpectedException) {
            gui.showError(ArrodesException.UNEXPECTED_FAILURE);
        }
    }

    /**
     * Starts Arrodes using the launcher mode selected by the command-line arguments.
     *
     * @param args optional interface selection argument
     */
    public static void main(String... args) {
        Launcher.main(args);
    }

}
