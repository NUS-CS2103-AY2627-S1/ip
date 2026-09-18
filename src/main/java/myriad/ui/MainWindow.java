package myriad.ui;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import myriad.Myriad;

// AI-assisted: most of this file was written with Claude Code (Claude Opus),
// used by Ryan Chua, who reviewed and adapted the output.
/**
 * Controls the chat window described by {@code view/MainWindow.fxml}: turns what the
 * user types into a request to the chatbot, adds both sides of the exchange
 * to the transcript, and lets the Up and Down keys recall earlier commands.
 */
public class MainWindow {

    /** How long the farewell stays on screen before the window closes. */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Myriad myriad;

    private final Image myriadPicture = DialogBox.loadPicture("/images/DaMyriad.png");

    /** Commands sent this session, for recalling with the arrow keys. */
    private final CommandHistory history = new CommandHistory();

    /**
     * Prepares the window once JavaFX has injected the controls named in the
     * layout file. Keeps the newest message in view by scrolling to the bottom
     * whenever the transcript grows, which happens every time a dialog box is
     * added. Also listens for the arrow keys in the text field.
     */
    @FXML
    public void initialize() {
        // A listener rather than a binding: a bound scroll position cannot be
        // changed by anything else, so the user could not scroll back up to
        // read earlier replies.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        userInput.setOnKeyPressed(this::handleHistoryKey);
    }

    /**
     * Replaces the text in the field with an earlier command on Up, or a
     * later one on Down, and moves the caret to the end so the user can edit
     * it straight away. The key press is consumed so that the text field does
     * not also act on it by moving the caret back to the start.
     *
     * @param event the key the user pressed in the text field.
     */
    private void handleHistoryKey(KeyEvent event) {
        Optional<String> recalled;
        if (event.getCode() == KeyCode.UP) {
            recalled = history.getPrevious();
        } else if (event.getCode() == KeyCode.DOWN) {
            recalled = history.getNext();
        } else {
            return;
        }

        recalled.ifPresent(command -> {
            userInput.setText(command);
            userInput.end();
        });
        event.consume();
    }

    /**
     * Injects the chatbot session this window talks to, and shows its greeting.
     * The greeting is shown as a warning when it reports that saved data
     * could not be loaded, so that the problem is not missed. Called by
     * {@link Main} once the layout has loaded, because a controller cannot be
     * given constructor arguments by {@code FXMLLoader}.
     *
     * @param myriad the session that answers what the user types.
     */
    public void setMyriad(Myriad myriad) {
        this.myriad = myriad;
        String greeting = myriad.getGreeting();
        if (myriad.hasLoadProblem()) {
            dialogContainer.getChildren().add(DialogBox.getWarningDialog(greeting, myriadPicture));
        } else {
            showMyriadMessage(greeting);
        }
    }

    /**
     * Puts the cursor in the text field, so that the user can type straight
     * away. Called after the window is shown, since a control cannot take
     * focus before it is part of a visible scene.
     */
    public void focusInput() {
        userInput.requestFocus();
    }

    /**
     * Sends whatever the user typed to the chatbot, and adds both their line
     * and the reply to the transcript. A blank line is ignored rather than
     * answered with an error, since pressing Enter on an empty field is a
     * slip rather than a command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        userInput.clear();
        history.add(input);
        if (input.isBlank()) {
            return;
        }

        String response = myriad.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        if (myriad.isLastResponseError()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(response, myriadPicture));
        } else {
            showMyriadMessage(response);
        }

        if (myriad.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    /**
     * Adds one of the chatbot's messages to the transcript.
     *
     * @param message the text to show.
     */
    private void showMyriadMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getMyriadDialog(message, myriadPicture));
    }

    /**
     * Closes the window a moment after an exit command, so that the farewell
     * is readable rather than flashing past as the window disappears.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
