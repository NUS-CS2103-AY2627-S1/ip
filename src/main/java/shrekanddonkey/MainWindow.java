package shrekanddonkey;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI of the chatbot.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private ShrekAndDonkey shrekAndDonkey;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image shrekImage = new Image(this.getClass().getResourceAsStream("/images/DaShrek.png"));

    /**
     * Initializes the controller and binds the scroll pane to the dialog container height.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "fx:id 'scrollPane' was not injected";
        assert dialogContainer != null : "fx:id 'dialogContainer' was not injected";
        assert userInput != null : "fx:id 'userInput' was not injected";
        assert sendButton != null : "fx:id 'sendButton' was not injected";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the ShrekAndDonkey chatbot instance and displays the welcome message.
     *
     * @param chatbot chatbot instance.
     */
    public void setShrekAndDonkey(ShrekAndDonkey chatbot) {
        assert chatbot != null : "ShrekAndDonkey instance cannot be null";
        assert dialogContainer != null : "dialogContainer must be initialized before setting bot";
        shrekAndDonkey = chatbot;
        dialogContainer.getChildren().add(
                DialogBox.getShrekDialog(shrekAndDonkey.getWelcomeMessage(), shrekImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Shrek's reply,
     * then appends them to the dialog container and clears the text input.
     */
    @FXML
    private void handleUserInput() {
        assert shrekAndDonkey != null : "ShrekAndDonkey instance must be set before handling input";
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        boolean shouldExit = shrekAndDonkey.isExit(input);
        String response = shrekAndDonkey.getResponse(input);
        boolean isError = shrekAndDonkey.isLastResponseError();
        DialogBox shrekDialog = isError
                ? DialogBox.getShrekErrorDialog(response, shrekImage)
                : DialogBox.getShrekDialog(response, shrekImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                shrekDialog
        );
        userInput.clear();

        // Close the application after showing the goodbye message
        if (shouldExit) {
            Platform.exit();
        }
    }
}
