package duke;

import duke.ui.Response;
import duke.ui.Ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Handles user interaction in the JavaFX chatbot window.
 */
public class MainWindow {
    private static final String DATA_FILE_PATH = "data/duke.txt";

    private final Duke duke;

    @FXML
    private ScrollPane dialogScroll;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    /**
     * Creates the window controller with the usual save file.
     */
    public MainWindow() {
        this(new Duke(DATA_FILE_PATH));
    }

    /**
     * Creates a controller with a supplied chatbot, allowing tests to use isolated storage.
     *
     * @param duke Chatbot that handles this window's commands.
     */
    public MainWindow(Duke duke) {
        this.duke = duke;
    }

    /**
     * Initializes the FXML controls after they are loaded.
     */
    @FXML
    public void initialize() {
        addMessage(new Response(new Ui().formatGreeting(), false, false).displayText(), false, false);
        Response warning = duke.getStartupWarning();
        if (warning != null) {
            addMessage(warning.displayText(), false, true);
        }
        // Wait for wrapped text to lay out before scrolling to the newest reply.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> dialogScroll.setVvalue(1.0)));
        inputField.setMaxWidth(Double.MAX_VALUE);
        sendButton.setDefaultButton(true);
    }

    /**
     * Moves keyboard focus to the command input field.
     */
    public void focusInput() {
        inputField.requestFocus();
    }

    /**
     * Sends the user's command to the chatbot and appends the response.
     */
    @FXML
    private void handleUserInput() {
        String command = inputField.getText().trim();
        if (!command.isEmpty()) {
            addMessage(command, true, false);
        }

        Response response = duke.getReply(command);
        addMessage(response.displayText(), false, response.isError());
        if (!response.isError()) {
            inputField.clear();
        }

        if (response.isExit()) {
            inputField.setDisable(true);
            sendButton.setDisable(true);
            inputField.setPromptText("Conversation ended. Close the window when you're ready.");
        } else {
            focusInput();
        }
    }

    /**
     * Adds a responsive message card. Commands are narrower than the bot's detailed replies.
     */
    private void addMessage(String text, boolean isUser, boolean isError) {
        Label heading = new Label(isUser ? "YOU" : isError ? "GEEN · NEEDS A FIX" : "GEEN");
        heading.getStyleClass().add("message-heading");
        Label body = new Label(text);
        body.setWrapText(true);
        body.setMinWidth(0);
        body.setMaxWidth(Double.MAX_VALUE);
        body.getStyleClass().add("message-body");

        VBox card = new VBox(6, heading, body);
        card.setMinWidth(0);
        card.getStyleClass().addAll("message-card", isUser ? "user-message" : "bot-message");
        if (isError) {
            card.getStyleClass().add("error-message");
        }
        card.maxWidthProperty().bind(dialogContainer.widthProperty().multiply(isUser ? 0.8 : 0.96));

        HBox row = new HBox(card);
        row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        dialogContainer.getChildren().add(row);
    }
}
