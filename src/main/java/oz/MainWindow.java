package oz;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    /** Introductory guidance displayed when the GUI opens. */
    private static final String WELCOME_MESSAGE =
            "Welcome! *adjusts spectacles* Ready to log some tasks? "
                    + "Try 'list', 'todo <task>', or 'find <keyword>'.";

    /** Time allowed for reading the farewell message before the application closes. */
    private static final Duration FAREWELL_DISPLAY_DURATION = Duration.seconds(1.5);

    /** Classpath location of the Oz avatar image. */
    private static final String OZ_IMAGE_PATH = "/images/DaOz.png";

    /** Scroll pane containing the chat dialog. */
    @FXML
    private ScrollPane scrollPane;

    /** Vertical container holding all user and bot dialog bubbles. */
    @FXML
    private VBox dialogContainer;

    /** Text input field for user commands. */
    @FXML
    private TextField userInput;

    /** Button triggering submission of user command. */
    @FXML
    private Button sendButton;

    /** The Oz chatbot instance handling command logic. */
    private Oz oz;

    /** Oz avatar image. */
    private final Image ozImage = new Image(this.getClass().getResourceAsStream(OZ_IMAGE_PATH));

    /**
     * Initializes the controller, binding the scroll pane to the dialog container's height.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> userInput.getText().isBlank(),
                userInput.textProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Injects the Oz chatbot instance into the controller.
     *
     * @param oz The Oz instance to inject.
     */
    public void setOz(Oz oz) {
        assert oz != null : "The Oz controller must be non-null";
        this.oz = oz;
        this.dialogContainer.getChildren().add(DialogBox.getOzDialog(WELCOME_MESSAGE, this.ozImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Oz's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText();
        if (input.isBlank()) {
            return;
        }

        CommandResult response = this.oz.getResponse(input);
        displayConversationTurn(input, response);
        this.userInput.clear();

        if (this.oz.isExit()) {
            scheduleApplicationExit();
            return;
        }
        this.userInput.requestFocus();
    }

    /**
     * Adds the user's message and Oz's response to the conversation.
     *
     * @param input User command to display.
     * @param response Oz response to display.
     */
    private void displayConversationTurn(String input, CommandResult response) {
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getOzDialog(response.message(), this.ozImage, response.type()));
    }

    /** Disables command input and exits after the farewell message is visible. */
    private void scheduleApplicationExit() {
        this.userInput.setDisable(true);
        this.sendButton.disableProperty().unbind();
        this.sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(FAREWELL_DISPLAY_DURATION);
        delay.setOnFinished((event) -> Platform.exit());
        delay.play();
    }
}
