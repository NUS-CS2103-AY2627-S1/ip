package apollo.gui;

import apollo.Apollo;
import apollo.ResponseType;
import apollo.gui.components.DialogBox;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String UNDO_AVAILABLE_STYLE_CLASS = "undo-available";
    private static final Duration EXIT_DELAY = Duration.seconds(5);
    private static final String WELCOME_MESSAGE =
            "The sun rises upon your tasks. Speak, mortal, and let Apollo bring order to your day.";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button undoButton;

    private Apollo apollo;
    /** Retains the exit timer until its delayed callback has run. */
    private final PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Default_pfp.jpg"));
    private Image apolloImage = new Image(this.getClass().getResourceAsStream("/images/Apollo.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Apollo instance used to process commands. */
    public void setApollo(Apollo apollo) {
        this.apollo = apollo;
        dialogContainer.getChildren().add(
                DialogBox.getApolloDialog(WELCOME_MESSAGE, apolloImage, ResponseType.DEFAULT));
        if (apollo.getLoadingError() != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getApolloDialog(apollo.getLoadingError(), apolloImage, ResponseType.ERROR));
        }
        updateUndoButtonState();
        userInput.requestFocus();
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Apollo's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        displayCommandResponse(input);
        userInput.clear();
    }

    /** Processes an undo command when the Undo button is pressed. */
    @FXML
    private void handleUndo() {
        if (!apollo.canUndo()) {
            return;
        }
        displayCommandResponse("undo");
        userInput.requestFocus();
    }

    /** Processes a command and appends the user and Apollo dialog boxes. */
    private void displayCommandResponse(String input) {
        String response = apollo.getResponse(input);
        ResponseType responseType = apollo.getResponseType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getApolloDialog(response, apolloImage, responseType)
        );
        updateUndoButtonState();
        if (responseType == ResponseType.BYE) {
            scheduleWindowClose();
        }
    }

    /** Disables further commands and closes the window after the farewell can be read. */
    private void scheduleWindowClose() {
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);
        this.undoButton.setDisable(true);

        this.exitDelay.setOnFinished(event -> Platform.exit());
        this.exitDelay.playFromStart();
    }

    /** Enables and styles the Undo button when the undo history is non-empty. */
    private void updateUndoButtonState() {
        boolean isUndoAvailable = this.apollo != null && this.apollo.canUndo();
        this.undoButton.setDisable(!isUndoAvailable);

        if (isUndoAvailable) {
            if (!this.undoButton.getStyleClass().contains(UNDO_AVAILABLE_STYLE_CLASS)) {
                this.undoButton.getStyleClass().add(UNDO_AVAILABLE_STYLE_CLASS);
            }
        } else {
            this.undoButton.getStyleClass().remove(UNDO_AVAILABLE_STYLE_CLASS);
        }
    }
}
