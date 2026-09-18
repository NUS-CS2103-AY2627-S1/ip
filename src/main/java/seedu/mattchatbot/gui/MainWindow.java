package seedu.mattchatbot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import seedu.mattchatbot.MattChatBot;
import seedu.mattchatbot.Reply;

/**
 * Controller for the chatbot's window: takes what the user types, asks the
 * chatbot about it, and adds both to the conversation.
 */
public class MainWindow {

    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    /** Scrolls the conversation when it grows past the window. */
    @FXML
    private ScrollPane scrollPane;

    /** Holds the dialog boxes, oldest first. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types a command. */
    @FXML
    private TextField userInput;

    /** Sends whatever is in the text field. */
    @FXML
    private Button sendButton;

    /** The chatbot answering the user. */
    private MattChatBot chatBot;

    /** Creates the controller. FXMLLoader calls this itself when loading the window. */
    public MainWindow() {
    }

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        // Set rather than bind. A bound vvalue cannot be changed by anything
        // else, including the scroll pane reacting to the user's wheel or
        // scrollbar, which would leave earlier messages unreachable. Listening
        // for the container growing scrolls to the bottom when a message
        // arrives while leaving the user free to scroll back afterwards.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Gives the window the chatbot to talk to, and shows its greeting.
     *
     * @param chatBot the chatbot answering the user
     */
    public void setChatBot(MattChatBot chatBot) {
        this.chatBot = chatBot;
        dialogContainer.getChildren().add(toDialogBox(chatBot.getWelcomeMessage()));
    }

    /**
     * Returns the dialog box that suits a reply, so that a complaint is not
     * shown in the same format as an ordinary answer.
     *
     * @param reply what the chatbot said
     * @return a box styled for an error or for a normal reply
     */
    private static DialogBox toDialogBox(Reply reply) {
        return reply.isError()
                ? DialogBox.forError(reply.text())
                : DialogBox.forChatBot(reply.text());
    }

    /**
     * Shows the user's command and the chatbot's reply, then clears the text
     * field ready for the next command.
     * <p>
     * On a bye command the window closes after a short pause, so the farewell
     * is readable rather than vanishing with the window.
     */
    @FXML
    private void handleUserInput() {
        // FXML wires this handler up before setChatBot is called, so an
        // unusually quick first click would otherwise fail with a bare NPE.
        assert chatBot != null : "setChatBot runs before the window handles input";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        Reply reply = chatBot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.forUser(input),
                toDialogBox(reply));
        userInput.clear();

        if (chatBot.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
