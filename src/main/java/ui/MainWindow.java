package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the main chat window, handling user input and displaying
 * the conversation between the user and Remy.
 */
public class MainWindow {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private Remy remy;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private Image remyImage = new Image(this.getClass().getResourceAsStream("/images/remy.jpg"));

    /**
     * Initializes the controller after its root element has been processed:
     * binds the scroll pane to auto-scroll and displays Remy's greeting message.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "scrollPane not injected";
        assert dialogContainer != null : "dialogContainer not injected";
        assert userInput != null : "userInput not injected";
        assert sendButton != null : "sendButton not injected";
        assert userImage != null : "userImage failed to load";
        assert remyImage != null : "remyImage failed to load";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
            DialogBox.getRemyDialog(
                "You're here! I've been waiting~ I'm Remy, and I'll take care of everything for you.\nWhat can I do?",
                remyImage
            )
        );
    }

    /**
     * Injects the Remy backend instance used to generate responses.
     *
     * @param r the Remy instance to use.
     */
    public void setRemy(Remy r) {
        assert r != null : "Remy null";
        remy = r;
    }

    /**
     * Handles submission of user input: retrieves Remy's response, appends both
     * messages to the dialog container, clears the input field, and exits the
     * application if the input is "bye".
     */
    @FXML
    private void handleUserInput() {
        assert remy != null : "remy not set -> call setRemy first";

        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = remy.getResponse(input);
        assert response != null : "response null";

        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog(input, userImage),
            DialogBox.getRemyDialog(response, remyImage)
        );
        userInput.clear();
        if (input.equals("bye")) {
            javafx.application.Platform.exit();
        }
    }
}
