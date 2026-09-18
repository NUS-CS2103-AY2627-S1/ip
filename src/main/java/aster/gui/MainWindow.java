package aster.gui;

import aster.Aster;
import aster.Response;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the chat window described in {@code /view/MainWindow.fxml}.
 *
 * <p>Every message the user sends appears in a dialog box of its own, followed by
 * Aster's reply. The window only passes each message to {@link Aster#getResponse(String)}
 * and shows what comes back, so working out the reply is left entirely to Aster.
 */
public class MainWindow {
    // Long enough to read the farewell before the window closes.
    private static final Duration CLOSE_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane conversationColumn;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    // The Aster mark is original artwork drawn for Aster.
    private final Image asterImage = new Image(MainWindow.class.getResourceAsStream("/images/aster.png"));

    private Aster aster;

    /**
     * Creates the controller; the FXML loader calls this before injecting the controls.
     */
    public MainWindow() {
    }

    /**
     * Keeps the newest message in view whenever the conversation grows.
     *
     * <p>The column holding the conversation is what scrolls, so its height, rather than the
     * height of the messages inside it, decides how far there is to scroll. A listener is
     * used rather than binding the scroll position, so the user can still scroll back up to
     * read earlier messages.
     */
    @FXML
    public void initialize() {
        conversationColumn.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects this window to the Aster it talks to, and shows Aster's opening reply.
     *
     * <p>If the conversation cannot start because the saved tasks could not be read, the
     * reply explains why and no message can be sent, but the window stays open so that
     * the explanation can be read.
     *
     * @param aster the chatbot whose conversation this window shows.
     */
    public void setAster(Aster aster) {
        this.aster = aster;
        Response opening = aster.startConversation();
        showAsterReply(opening);
        if (opening.isExit()) {
            disableInput();
        }
    }

    /**
     * Sends the typed message to Aster and shows the exchange.
     *
     * <p>A message of nothing but spaces is ignored. Any other message is shown exactly
     * as typed, before Aster's reply, whatever that reply turns out to be. If the reply
     * ends the conversation, no further message can be sent and the window closes
     * shortly afterwards.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        userInput.clear();
        userInput.requestFocus();
        if (input.isBlank()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        Response reply = aster.getResponse(input);
        showAsterReply(reply);
        if (reply.isExit()) {
            disableInput();
            closeAfterDelay();
        }
    }

    /**
     * Adds a dialog box showing one of Aster's replies, marked as an error if the reply
     * contains one.
     *
     * @param reply the reply to show.
     */
    private void showAsterReply(Response reply) {
        DialogBox dialogBox = reply.isError()
                ? DialogBox.getErrorDialog(reply.message(), asterImage)
                : DialogBox.getAsterDialog(reply.message(), asterImage);
        dialogContainer.getChildren().add(dialogBox);
    }

    /**
     * Stops the user from sending any further message.
     */
    private void disableInput() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }

    /**
     * Closes the application once the farewell has had time to be read.
     */
    private void closeAfterDelay() {
        PauseTransition delay = new PauseTransition(CLOSE_DELAY);
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}
