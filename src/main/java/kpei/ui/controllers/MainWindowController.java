package kpei.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import kpei.CommandCenter;
import kpei.datatypes.TaskList;

/**
 * Main window controller coordinating the Banner, CLI Terminal, and List View components.
 */
public class MainWindowController {

    @FXML
    private Node banner;

    @FXML
    private BannerController bannerController;

    @FXML
    private Node cliTerminal;

    @FXML
    private CliTerminalController cliTerminalController;

    @FXML
    private Node listView;

    @FXML
    private ListViewController listViewController;

    private CommandCenter commandCenter;

    /**
     * Initializes the controller and binds the terminal command consumer.
     */
    @FXML
    public void initialize() {
        cliTerminalController.setCommandConsumer(this::handleCommand);
    }

    /**
     * Injects the commandCenter instance.
     *
     * @param commandCenter The CommandCenter controller instance.
     */
    public void setDependencies(CommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    /**
     * Refreshes the task list displayed in the right-hand List View component.
     *
     * @param taskList The TaskList of tasks to display.
     */
    public void refreshTaskList(TaskList taskList) {
        listViewController.updateTasks(taskList);
    }

    /**
     * Handles command submission to commandCenter.
     *
     * @param input The command entered by the user.
     */
    private void handleCommand(String input) {
        boolean isExit = commandCenter.executeCommand(input);

        if (isExit) {
            Platform.exit();
        }
    }

    /**
     * Returns the nested controller for the CLI terminal.
     *
     * @return The {@link CliTerminalController} instance.
     */
    public CliTerminalController getCliTerminalController() {
        return cliTerminalController;
    }
}
