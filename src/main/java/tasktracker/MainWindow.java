package tasktracker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tasktracker.command.CommandType;

/**
 * Controller for the main GUI of TaskTracker.
 * Coordinates layout, handles user inputs, and manages conversational UI state.
 */
public class MainWindow extends AnchorPane {
    private static final String DEFAULT_PROMPT = "Type a command...";
    private static final String FAILED_PREFIX = "> ";
    private static final String SESSION_ENDED_MESSAGE = "Session ended. Please close window.";
    private static final String SESSION_ENDED_STYLE = "-fx-font-size: 14px;";
    private static final String DIMMED_STYLE_CLASS = "dimmed-bubble";
    private static final String GHOST_STYLE_CLASS = "ghost-mode";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TaskTracker taskTracker;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image taskTrackerImage =
            new Image(this.getClass().getResourceAsStream("/images/DaTaskTracker.png"));

    private String lastFailedInput = "";
    private boolean isGhostMode = false;

    /**
     * Initializes the scroll properties and binds key event listeners to the input field.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.addEventFilter(KeyEvent.KEY_TYPED, this::handleGhostKeyTyped);
    }

    /**
     * Injects the TaskTracker instance and displays the initial welcome greeting
     * along with any startup warning dialogs for corrupted data.
     *
     * @param t The TaskTracker logic engine instance.
     */
    public void setTaskTracker(TaskTracker t) {
        this.taskTracker = t;
        dialogContainer.getChildren().add(
                DialogBox.getTaskTrackerDialog(taskTracker.getWelcomeMessage(),
                        taskTrackerImage, null, false)
        );

        if (taskTracker.hasStartupWarnings()) {
            dialogContainer.getChildren().add(
                    DialogBox.getTaskTrackerDialog(taskTracker.getStartupWarnings(),
                            taskTrackerImage, null, true)
            );
        }
    }

    /**
     * Orchestrates user input handling by validating entry, obtaining bot output,
     * rendering dialog bubbles, and updating the state of the UI controls.
     */
    @FXML
    private void handleUserInput() {
        if (isGhostMode) {
            return;
        }

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            userInput.clear();
            return;
        }

        String response = taskTracker.getResponse(input);
        CommandType commandType = taskTracker.getLastCommandType();
        boolean isError = taskTracker.isErrorResponse();

        String displayedResponse = isError
                ? response + "\nTip: Type '>' to edit your previous command, or 'help' to see options."
                : response;

        renderDialog(input, displayedResponse, commandType, isError);
        updateUiState(commandType, isError, input);
    }

    /**
     * Renders and appends both the user dialogue box and the chatbot dialogue box
     * to the conversation container.
     */
    private void renderDialog(String input, String response, CommandType commandType, boolean isError) {
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        if (isError) {
            userDialog.getStyleClass().add(DIMMED_STYLE_CLASS);
        }

        dialogContainer.getChildren().addAll(
                userDialog,
                DialogBox.getTaskTrackerDialog(response, taskTrackerImage, commandType, isError)
        );
    }

    /**
     * Updates the status and values of the input controls based on execution outcome.
     */
    private void updateUiState(CommandType commandType, boolean isError, String input) {
        if (commandType == CommandType.BYE) {
            handleSessionEnd();
        } else if (isError) {
            handleErrorState(input);
        } else {
            handleSuccessState();
        }
    }

    /**
     * Intercepts key typing when the field is displaying a ghosted failed command.
     */
    private void handleGhostKeyTyped(KeyEvent event) {
        if (!isGhostMode) {
            return;
        }

        String character = event.getCharacter();
        if (character.isEmpty() || Character.isISOControl(character.charAt(0))) {
            return;
        }

        event.consume();

        if (">".equals(character)) {
            acceptGhostCommand();
        } else {
            replaceGhostCommand(character);
        }
    }

    /**
     * Accepts the grayed-out command, restores normal styling, and places the cursor at the end.
     */
    private void acceptGhostCommand() {
        exitGhostMode();
        userInput.setText(lastFailedInput);
        userInput.positionCaret(lastFailedInput.length());
    }

    /**
     * Discards the grayed-out command and starts a new input with the typed character.
     */
    private void replaceGhostCommand(String character) {
        exitGhostMode();
        userInput.setText(character);
        userInput.positionCaret(character.length());
    }

    /**
     * Removes the ghost styling and resets the state flag.
     */
    private void exitGhostMode() {
        isGhostMode = false;
        userInput.getStyleClass().remove(GHOST_STYLE_CLASS);
    }

    /**
     * Disables the user input fields and presents the terminal exit message.
     */
    private void handleSessionEnd() {
        exitGhostMode();
        userInput.setEditable(false);
        userInput.setDisable(true);
        sendButton.setDisable(true);
        userInput.setText(SESSION_ENDED_MESSAGE);
        userInput.setStyle(SESSION_ENDED_STYLE);
    }

    /**
     * Caches the failed input and displays it in full, grayed out in the text box.
     */
    private void handleErrorState(String failedInput) {
        lastFailedInput = failedInput;
        isGhostMode = true;
        if (!userInput.getStyleClass().contains(GHOST_STYLE_CLASS)) {
            userInput.getStyleClass().add(GHOST_STYLE_CLASS);
        }
        userInput.setText(FAILED_PREFIX + lastFailedInput);

        Platform.runLater(() -> {
            userInput.positionCaret(0);
        });
    }

    /**
     * Resets the input field and clears failed input caches.
     */
    private void handleSuccessState() {
        exitGhostMode();
        lastFailedInput = "";
        userInput.setPromptText(DEFAULT_PROMPT);
        userInput.clear();
    }
}
