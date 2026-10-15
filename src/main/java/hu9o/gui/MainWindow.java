package hu9o.gui;

import hu9o.Hu9o;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window.
 *
 * <p>It connects the scrolling dialog area, the text field, and the send button
 * to the {@link Hu9o} back end: each command becomes a pair of dialog boxes (the
 * user's line and Hu9o's reply), and a {@code bye} command closes the window
 * after a short delay so the farewell stays on screen. It also owns two
 * GUI-only views that never reach the {@link Hu9o} back end: the Contacts page
 * (toggled by {@link #contactsButton}) and the {@code clear} command, which
 * empties the chat display without touching any saved data.
 */
public class MainWindow extends AnchorPane {
    /** How long the farewell stays visible before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);
    /** How long the typing indicator stays on screen before the real reply replaces it. */
    private static final Duration TYPING_DELAY = Duration.millis(300);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button contactsButton;
    @FXML
    private ListView<String> contactsList;

    private Hu9o hu9o;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image hu9oImage =
            new Image(this.getClass().getResourceAsStream("/images/DaHu9o.jpg"));
    private final Image typingImage =
            new Image(this.getClass().getResourceAsStream("/images/DogTyping.gif"));

    /**
     * Scrolls to the newest message whenever the chat grows, and puts the cursor in the input box.
     * Called by the FXML loader.
     *
     * <p>A listener is used rather than binding {@code vvalue} to the chat height: a bound
     * property ignores the mouse wheel and touchpad, so scrolling would stay stuck at the
     * bottom until the scrollbar was clicked.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        // Deferred so it runs after Main has shown the window and the scene's initial focus is settled.
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Injects the back end and shows its greeting.
     *
     * @param hu9o the chatbot that produces replies.
     */
    public void setHu9o(Hu9o hu9o) {
        // Main always injects a real back end straight after loading the FXML.
        assert hu9o != null : "MainWindow requires a Hu9o back end";
        this.hu9o = hu9o;
        dialogContainer.getChildren().add(
                DialogBox.getHu9oDialog(hu9o.getWelcomeMessage(), hu9oImage));
    }

    /**
     * Reads the text field and routes it to the right handler: {@code clear}
     * wipes the chat display locally, anything else goes to {@link Hu9o} after
     * a brief typing indicator. Clears the field either way.
     */
    @FXML
    private void handleUserInput() {
        // The send button and text field are only live after setHu9o() has run.
        assert hu9o != null : "setHu9o() must be called before handling input";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        userInput.clear();

        if (hu9o.isClearCommand(input)) {
            clearChat();
            return;
        }

        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox typingDialog = DialogBox.getTypingDialog(typingImage);
        dialogContainer.getChildren().addAll(userDialog, typingDialog);

        PauseTransition delay = new PauseTransition(TYPING_DELAY);
        delay.setOnFinished(event -> showResponse(input, typingDialog));
        delay.play();
    }

    /**
     * Replaces the typing indicator with Hu9o's real reply, and triggers the
     * delayed exit if the input that produced it was a {@code bye}.
     *
     * @param input        the command that was sent.
     * @param typingDialog the placeholder bubble to replace.
     */
    private void showResponse(String input, DialogBox typingDialog) {
        String response = hu9o.getResponse(input);
        DialogBox hu9oDialog = hu9o.isLastResponseError()
                ? DialogBox.getErrorDialog(response, hu9oImage)
                : DialogBox.getHu9oDialog(response, hu9oImage);
        dialogContainer.getChildren().set(dialogContainer.getChildren().indexOf(typingDialog), hu9oDialog);

        if (hu9o.isExitCommand(input)) {
            exitAfterDelay();
        }
    }

    /**
     * Empties the chat display and shows the welcome message again, without
     * touching the task list, the contact network, or any saved data.
     */
    private void clearChat() {
        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().add(
                DialogBox.getHu9oDialog(hu9o.getWelcomeMessage(), hu9oImage));
    }

    /**
     * Toggles between the chat view and the Contacts page. Opening the
     * Contacts page refreshes it from {@link Hu9o#getContactSummaries()}.
     */
    @FXML
    private void handleContactsToggle() {
        assert hu9o != null : "setHu9o() must be called before handling input";
        boolean showingContacts = contactsList.isVisible();
        if (showingContacts) {
            contactsList.setVisible(false);
            contactsList.setManaged(false);
            scrollPane.setVisible(true);
            scrollPane.setManaged(true);
            contactsButton.setText("Contacts");
        } else {
            contactsList.getItems().setAll(hu9o.getContactSummaries());
            contactsList.setVisible(true);
            contactsList.setManaged(true);
            scrollPane.setVisible(false);
            scrollPane.setManaged(false);
            contactsButton.setText("Chat");
        }
    }

    /** Disables further input and closes the window once {@link #EXIT_DELAY} passes. */
    private void exitAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition delay = new PauseTransition(EXIT_DELAY);
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}
