package am.ui;

import am.Am;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML controller for the main AM conversation window.
 */
public class MainWindow extends AnchorPane {
    private static final double SESSION_HINT_SPACE = 24;
    private static final String WELCOME_MESSAGE = "My name is AM.\nWhat do you want?";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextArea userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Label sessionEndHint;

    private Am am;

    /** Creates an empty controller for FXMLLoader to populate. */
    public MainWindow() {
    }

    /**
     * Installs the listener that keeps the conversation scrolled to its newest message.
     */
    @FXML
    private void initialize() {
        // Keep commands on one logical line even when pasted text contains line breaks or tabs.
        userInput.setTextFormatter(new TextFormatter<String>(change -> {
            change.setText(change.getText().replace("\r", "").replace("\n", "").replace("\t", ""));
            return change;
        }));
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleInputKeyPressed);
        dialogContainer.heightProperty().addListener(observable -> scrollToLatestMessage());
    }

    /**
     * Connects the window to the chatbot and displays its welcome message.
     *
     * @param am chatbot that processes commands and persists tasks
     */
    public void setAm(Am am) {
        this.am = am;
        appendDialog(DialogBox.getAmDialog(WELCOME_MESSAGE));
        userInput.requestFocus();
    }

    /** Preserves submission and focus navigation while the command field wraps visually. */
    private void handleInputKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            handleUserInput();
        } else if (event.getCode() == KeyCode.TAB) {
            event.consume();
            if (event.isShiftDown()) {
                scrollPane.requestFocus();
            } else {
                sendButton.requestFocus();
            }
        }
    }

    /** Processes a command entered in the text field or submitted by the button. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = am.getResponse(input);
        appendDialog(DialogBox.getUserDialog(input));
        appendDialog(am.isResponseError() ? DialogBox.getErrorDialog(response) : DialogBox.getAmDialog(response));
        if (am.shouldPreserveInput()) {
            userInput.requestFocus();
            userInput.positionCaret(userInput.getLength());
        } else {
            userInput.clear();
        }

        if (am.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            showSessionEndHint();
        }
    }

    /** Makes room below the disabled controls for a persistent session-ended hint. */
    private void showSessionEndHint() {
        sessionEndHint.setManaged(true);
        sessionEndHint.setVisible(true);
        AnchorPane.setBottomAnchor(userInput, AnchorPane.getBottomAnchor(userInput) + SESSION_HINT_SPACE);
        AnchorPane.setBottomAnchor(sendButton, AnchorPane.getBottomAnchor(sendButton) + SESSION_HINT_SPACE);
        AnchorPane.setBottomAnchor(scrollPane, AnchorPane.getBottomAnchor(scrollPane) + SESSION_HINT_SPACE);
    }

    /**
     * Adds a new message to the conversation.
     *
     * @param dialogBox message box to add
     */
    private void appendDialog(DialogBox dialogBox) {
        dialogContainer.getChildren().add(dialogBox);
    }

    /** Scrolls the conversation to the newest message after its layout changes. */
    private void scrollToLatestMessage() {
        scrollPane.setVvalue(1.0);
    }
}
