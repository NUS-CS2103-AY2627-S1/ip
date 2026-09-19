package leo;

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
 * Controller for {@code MainWindow.fxml}: the scrollable chat log, the
 * text field the user types commands into, and the send button. Wires
 * user input to {@link Leo#getReply(String)} and displays both sides of
 * the conversation as {@link DialogBox} rows - Leo's own replies styled
 * differently when they are error messages (see {@link Reply}).
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button listButton;
    @FXML
    private Button todoButton;
    @FXML
    private Button deadlineButton;
    @FXML
    private Button eventButton;
    @FXML
    private Button byeButton;

    private Leo leo;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image leoImage =
            new Image(this.getClass().getResourceAsStream("/images/DaLeo.png"));

    /**
     * Called automatically by the FXML loader once the annotated
     * fields above have been injected. Keeps the chat log scrolled to
     * the newest message as it grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the {@link Leo} instance to send commands to,
     * and shows Leo's opening greeting as the first chat bubble. Called
     * once by {@link Main} right after the FXML is loaded.
     *
     * @param leo the Leo instance backing this window
     */
    public void setLeo(Leo leo) {
        this.leo = leo;
        dialogContainer.getChildren().add(
                DialogBox.getLeoDialog(
                        "Hey there! I'm Leo 🦁 - think of me as your friendly task "
                                + "sidekick.\nWhat can I help you get done today? (Not sure where "
                                + "to start? The buttons below have you covered.)",
                        leoImage,
                        false
                )
        );
    }

    /**
     * Called when the user presses Enter in the text field or clicks
     * Send. Adds a dialog box for the user's own input and one for
     * Leo's reply, then clears the text field. If the command was
     * "bye", closes the app shortly after, giving the user a moment to
     * read the goodbye message (mirroring the console version, which
     * ends the chat loop straight after showing it).
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.trim().isEmpty()) {
            return;
        }

        Reply reply = leo.getReply(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLeoDialog(reply.getText(), leoImage, reply.isError())
        );
        userInput.clear();
        // A-BetterGui: keeps the caret in the text field after every
        // command, so the user can immediately keep typing without
        // having to click back into it first.
        userInput.requestFocus();

        if (Parser.getCommandWord(input).equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    /**
     * Handles the "List" quick-action button.
     */
    @FXML
    private void handleListButton() {
        submitCommand("list");
    }

    /**
     * Handles the "Todo" quick-action button.
     */
    @FXML
    private void handleTodoButton() {
        prefillCommand("todo ");
    }

    /**
     * Handles the "Deadline" quick-action button.
     */
    @FXML
    private void handleDeadlineButton() {
        prefillCommand("deadline DESCRIPTION /by yyyy-MM-dd HHmm");
    }

    /**
     * Handles the "Event" quick-action button.
     */
    @FXML
    private void handleEventButton() {
        prefillCommand("event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
    }

    /**
     * Handles the "Bye" quick-action button.
     */
    @FXML
    private void handleByeButton() {
        submitCommand("bye");
    }

    /**
     * Sends {@code command} exactly as if the user had typed it into
     * the text field and pressed Enter/Send - used by the quick-action
     * buttons whose command needs no further input from the user
     * (List, Bye).
     *
     * @param command the full command text to send
     */
    private void submitCommand(String command) {
        userInput.setText(command);
        handleUserInput();
    }

    /**
     * Fills the text field with a ready-to-edit command template and
     * focuses it, selecting the "DESCRIPTION" placeholder (if the
     * template has one) so typing immediately replaces it - used by
     * the quick-action buttons whose command needs the user's own
     * details (Todo/Deadline/Event) before it can be sent, so the
     * exact command word and delimiters are never something the user
     * has to recall from memory.
     *
     * @param template the command template to place in the text field
     */
    private void prefillCommand(String template) {
        userInput.setText(template);
        userInput.requestFocus();

        int placeholderStart = template.indexOf("DESCRIPTION");
        if (placeholderStart == -1) {
            userInput.positionCaret(template.length());
        } else {
            userInput.selectRange(placeholderStart, placeholderStart + "DESCRIPTION".length());
        }
    }
}
