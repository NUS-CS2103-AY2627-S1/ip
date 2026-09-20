package duchess.gui;

import duchess.Duchess;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controller for the main Duchess chat window. */
public class MainWindow extends VBox {
    @FXML
    private Header header;

    @FXML
    private QuickCommands quickCommands;

    @FXML
    private ConversationView conversationView;

    @FXML
    private Composer composer;

    /** The shared command processor used by the GUI. */
    private Duchess duchess;

    /** Configures automatic scrolling after a new dialog is added. */
    @FXML
    public void initialize() {
        header.setHelpAction(this::handleHelp);
        quickCommands.setCommandAction(this::handleQuickCommand);
        composer.setSubmitAction(this::handleUserInput);
    }

    /**
     * Injects the command processor and adds the initial greeting.
     *
     * @param duchess the command processor used to answer user input
     */
    public void setDuchess(Duchess duchess) {
        this.duchess = duchess;
        conversationView.addDialogs(
                DialogBox.getDuchessDialog("Nine-Nine! Your task squad is on duty.\n\n"
                                + "I’m Duchess, your precinct task partner. Bring me a case;\n"
                                + "I’ll keep the paperwork under control. Cool cool cool.\n\n"
                                + "Start with New case, or open the Playbook for a briefing.", "welcome"));
        if (!duchess.getStartupWarning().isEmpty()) {
            conversationView.addDialogs(DialogBox.getDuchessDialog(duchess.getStartupWarning(), "error"));
            updateStatus("error");
        }
        composer.requestInputFocus();
    }

    /** Sends the current text to Duchess and appends both sides of the conversation. */
    private void handleUserInput() {
        submitCommand(composer.getInput());
    }

    /** Displays the command guide when the GUI Help button is pressed. */
    private void handleHelp() {
        submitCommand("help");
    }

    /** Runs read-only shortcuts immediately and prepares commands that need task details. */
    private void handleQuickCommand(String command) {
        switch (command) {
            case "list", "stats", "help", "bye" -> submitCommand(command);
            default -> prepareCommand(command + " ");
        }
    }

    /** Sends a command through the shared processor and appends the conversation bubbles. */
    private void submitCommand(String input) {
        if (input.isBlank() || duchess.isExitRequested()) {
            return;
        }
        String response = duchess.getResponse(input);
        String commandType = duchess.getCommandType();

        conversationView.addDialogs(
                DialogBox.getUserDialog(input),
                DialogBox.getDuchessDialog(response, commandType));
        updateStatus(commandType);
        composer.clearInput();
        composer.requestInputFocus();

        if (duchess.isExitRequested()) {
            composer.setInputDisabled(true);
            header.setHelpDisabled(true);
            quickCommands.setDisable(true);
            PauseTransition farewellPause = new PauseTransition(Duration.seconds(2));
            farewellPause.setOnFinished(event -> Platform.exit());
            farewellPause.play();
        }
    }

    /** Places a command starter in the input field and returns focus to the composer. */
    private void prepareCommand(String commandStarter) {
        composer.prepareInput(commandStarter);
    }

    /** Updates the compact header status to reflect the latest command result. */
    private void updateStatus(String commandType) {
        switch (commandType) {
            case "add", "mark", "unmark" -> {
                header.updateStatus("★ Squad updated. Noice.", "status-success");
            }
            case "delete" -> {
                header.updateStatus("★ Case archived", "status-success");
            }
            case "error" -> {
                header.updateStatus("Hold up, detective. Check that command.", "status-warning");
            }
            case "bye" -> {
                header.updateStatus("Nine-Nine! Signing off…", "status-muted");
            }
            case "help" -> {
                header.updateStatus("★ The squad playbook", "status-ready");
            }
            default -> {
                header.updateStatus("★ Squad ready. Let’s solve this.", "status-ready");
            }
        }
    }
}
