package lumine;

import lumine.command.Command;
import lumine.parser.Parser;
import lumine.storage.Storage;
import lumine.task.TaskList;
import lumine.ui.GuiUi;
import lumine.ui.Ui;

/**
 * Coordinates the components of Lumine, a personal task-management chatbot.
 *
 * <p>Wires together the {@link Ui}, {@link Parser}, {@link Storage}, and
 * {@link TaskList} components, then drives the read-parse-execute loop
 * until the user exits.</p>
 */
public class Lumine {
    private TaskList taskList;
    private Ui ui;
    private Parser parser;
    private String loadError;
    private boolean isExitRequested;

    /**
     * Creates a new Lumine instance backed by the given save file.
     * If the file cannot be loaded, the error message is stored and
     * shown to the user when the main loop starts.
     *
     * @param filePath path to the task save file (created if absent).
     */
    public Lumine(String filePath) {
        ui = new Ui();
        parser = new Parser();
        Storage storage = new Storage(filePath);
        try {
            taskList = new TaskList(storage);
        } catch (LumineException e) {
            loadError = e.getMessage();
            taskList = new TaskList(storage, false);
        }
    }

    /**
     * Starts the main input loop: reads commands from the user,
     * parses and executes each one, and repeats until an
     * {@link lumine.command.ExitCommand} is encountered or input ends.
     */
    public void run() {
        ui.showGreetings();

        if (loadError != null) {
            ui.showSeparator();
            ui.showMessage(loadError);
            ui.showSeparator();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showSeparator();
            try {
                Command command = parser.parse(fullCommand);
                taskList.setCurrentCommand(fullCommand);
                command.execute(taskList, ui);
                isExit = command.isExit();
            } catch (LumineException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Returns Lumine's greeting and any task-load error for the GUI.
     * The load error is shown only once because it applies to application startup.
     *
     * @return the greeting text for the conversation window.
     */
    public String getGreeting() {
        GuiUi guiUi = new GuiUi();
        guiUi.showGreetings();

        if (loadError != null) {
            guiUi.showMessage(loadError);
            loadError = null;
        }
        return guiUi.getOutput();
    }

    /**
     * Executes one user command and returns Lumine's response for the GUI.
     *
     * @param input the command entered in the conversation window.
     * @return the response produced by the command or parser.
     */
    public String getResponse(String input) {
        GuiUi guiUi = new GuiUi();
        try {
            Command command = parser.parse(input);
            taskList.setCurrentCommand(input);
            command.execute(taskList, guiUi);
            isExitRequested = command.isExit();
        } catch (LumineException exception) {
            guiUi.showMessage(exception.getMessage());
        }
        return guiUi.getOutput();
    }

    /**
     * Returns whether the most recently processed command ended the session.
     *
     * @return whether Lumine has received an exit command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Starts a new Lumine session with the default save file.
     */
    public static void main(String[] args) {
        new Lumine("data/lumine.txt").run();
    }
}
