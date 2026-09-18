package bobby.gui;

import bobby.Bobby;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for Bobby's main GUI window.
 */
public class MainWindow extends AnchorPane {
    private static final String COMMAND_ERROR_PREFIX = "The path is misty:";
    private static final String LOADING_ERROR_PREFIX = "The old scroll is clouded:";

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/frombobby.png"));
    private final Image bobbyImage = new Image(getClass().getResourceAsStream("/images/tobobby.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Bobby bobby;

    /**
     * Initializes FXML-linked controls after the view has been loaded.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML should inject the scroll pane.";
        assert dialogContainer != null : "FXML should inject the dialog container.";
        assert userInput != null : "FXML should inject the user input field.";
        assert sendButton != null : "FXML should inject the send button.";

        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the Bobby instance that generates chatbot responses.
     *
     * @param bobby Bobby instance to use for command handling.
     */
    public void setBobby(Bobby bobby) {
        assert bobby != null : "Main should inject a Bobby instance.";

        this.bobby = bobby;
        String welcomeMessage = bobby.getWelcomeMessage();
        dialogContainer.getChildren().add(
                DialogBox.getBobbyDialog(welcomeMessage, bobbyImage, isErrorResponse(welcomeMessage)));
    }

    /**
     * Creates dialog boxes for the user's input and Bobby's response.
     */
    @FXML
    private void handleUserInput() {
        assert bobby != null : "Bobby should be injected before handling input.";

        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        String bobbyText = bobby.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getBobbyDialog(bobbyText, bobbyImage, isErrorResponse(bobbyText)));
        userInput.clear();

        if (bobby.isExitCommand(userText)) {
            Platform.exit();
        }
    }

    private boolean isErrorResponse(String response) {
        assert response != null : "Response should be non-null before styling.";

        return response.startsWith(COMMAND_ERROR_PREFIX) || response.startsWith(LOADING_ERROR_PREFIX);
    }
}
