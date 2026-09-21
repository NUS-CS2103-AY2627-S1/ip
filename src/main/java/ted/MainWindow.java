package ted;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for Ted's main window.
 * <p>
 * Holds the conversation together: it puts what the user typed and what Ted
 * replied into the dialog container, and closes the window once Ted has said
 * goodbye. The layout itself is described in {@code MainWindow.fxml}. Adapted
 * from the main window in the SE-EDU JavaFX tutorial
 * (https://se-education.org/guides/tutorials/javaFx.html).
 */
public class MainWindow {
    /** How long Ted's goodbye stays on screen before the window closes. */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image tedImage = new Image(this.getClass().getResourceAsStream("/images/DaTed.png"));

    /** The chatbot answering in this window, supplied by {@link #setTed(Ted)}. */
    private Ted ted;

    /**
     * Creates the controller. {@code FXMLLoader} calls this itself when it loads
     * {@code MainWindow.fxml}, which is why it must be public and take no arguments.
     */
    public MainWindow() {
    }

    /**
     * Keeps the newest message in view as the conversation grows.
     * <p>
     * The scroll position is moved by a listener rather than bound to the
     * conversation's height: a bound position cannot be changed by anything
     * else, which would stop the user scrolling back up to read earlier replies.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /**
     * Gives this window the chatbot it should talk to, and shows its greeting.
     *
     * @param ted the chatbot answering in this window.
     */
    public void setTed(Ted ted) {
        this.ted = ted;
        dialogContainer.getChildren().add(createTedDialog(ted.getGreeting()));
    }

    /**
     * Shows what the user typed and how Ted replied, then clears the text field.
     * Called by the FXML for both the Send button and the Enter key.
     */
    @FXML
    private void handleUserInput() {
        assert ted != null : "Main calls setTed before showing the window, so input always has a Ted";
        String input = userInput.getText();
        String response = ted.getResponse(input);
        userInput.clear();
        // Clicking Send moves the focus to the button; handing it back lets the
        // user type the next command straight away.
        userInput.requestFocus();
        if (response.isEmpty()) {
            // A blank line gets no reply, so nothing is worth showing.
            return;
        }

        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input.strip()), createTedDialog(response));

        if (ted.isExit()) {
            // The goodbye would flash past if the window closed immediately.
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Returns a bubble for Ted's latest message, in red if it explains a problem.
     *
     * @param message what Ted just said.
     * @return the dialog box to add to the conversation.
     */
    private DialogBox createTedDialog(String message) {
        return ted.isError()
                ? DialogBox.getTedErrorDialog(message, tedImage)
                : DialogBox.getTedDialog(message, tedImage);
    }
}
