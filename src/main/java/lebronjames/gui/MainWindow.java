package lebronjames.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lebronjames.LebronJames;

/**
 * Controller for the main window: it turns what the user types into a command
 * for the chatbot, and the chatbot's reply into a new speech bubble.
 *
 * <p>The fields marked {@code @FXML} are filled in by the FXML loader from the
 * matching {@code fx:id}s in {@code /view/MainWindow.fxml}, so they are not
 * assigned anywhere in this file.
 */
public class MainWindow {
    /** How long the window stays open after "bye", so the farewell can be read. */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private LebronJames lebronJames;

    /**
     * Portrait shown beside the chatbot's replies. There is no matching picture
     * of the user: see {@link DialogBox} for why only one side carries one.
     */
    private final Image lebronImage = loadImage("/images/DaLebron.jpg");

    /**
     * Prepares the window once the FXML loader has built it.
     *
     * <p>Named {@code initialize} so JavaFX calls it automatically; it is the
     * earliest point at which the injected controls exist.
     */
    @FXML
    public void initialize() {
        // Keep the newest message in view as the conversation grows past the
        // height of the window.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the chatbot whose replies it should show.
     *
     * @param lebronJames Chatbot to send the user's commands to.
     */
    public void setLebronJames(LebronJames lebronJames) {
        this.lebronJames = lebronJames;
    }

    /**
     * Shows the chatbot's greeting, so the window is not empty on start-up.
     */
    public void showWelcome() {
        addLebronReply(lebronJames.getWelcomeMessage(), lebronJames.isErrorReply());
    }

    /**
     * Sends whatever the user typed to the chatbot and shows both the command
     * and the reply, then clears the text field ready for the next command.
     *
     * <p>Bound to the send button and to the Enter key in the FXML file.
     */
    @FXML
    private void handleUserInput() {
        String commandText = userInput.getText();
        if (commandText.isBlank()) {
            // Nothing was typed, so there is no command to answer.
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(commandText));
        addLebronReply(lebronJames.getResponse(commandText), lebronJames.isErrorReply());
        userInput.clear();

        if (lebronJames.isExit()) {
            closeAfterGoodbye();
        }
    }

    /**
     * Adds one reply from the chatbot to the conversation.
     *
     * <p>A complaint about a bad command is drawn differently from an ordinary
     * answer, so the user can see that something went wrong without having to
     * read the message first.
     *
     * @param reply Text the chatbot said.
     * @param isError Whether the reply is a complaint rather than an answer.
     */
    private void addLebronReply(String reply, boolean isError) {
        DialogBox bubble = isError
                ? DialogBox.getErrorDialog(reply, lebronImage)
                : DialogBox.getLebronDialog(reply, lebronImage);
        dialogContainer.getChildren().add(bubble);
    }

    /**
     * Closes the window a moment after the user says bye.
     *
     * <p>The pause is what makes the farewell readable: exiting immediately
     * would remove the message in the same instant it appeared.
     */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Loads one of the pictures bundled with the application.
     *
     * @param resourcePath Path to the image inside the resources folder.
     * @return The loaded image.
     */
    private static Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}
