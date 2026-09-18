package waffles;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Waffles GUI window.
 */
public class MainWindow {
    /** The scrollable history of chatbot messages. */
    @FXML
    private ScrollPane scrollPane;

    /** The container holding the dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** The text field used to enter commands. */
    @FXML
    private TextField userInput;

    /** The button used to submit a command. */
    @FXML
    private Button sendButton;

    /** The chatbot model that processes GUI commands. */
    private Waffles waffles;

    /** The avatar shown beside user messages. */
    private final Image userImage;

    /** The avatar shown beside Waffles messages. */
    private final Image wafflesImage;

    /** Creates the controller and loads the two dialog avatars. */
    public MainWindow() {
        userImage = new Image(getClass().getResourceAsStream("/images/User.png"));
        wafflesImage = new Image(getClass().getResourceAsStream("/images/Waffles.png"));
    }

    /** Keeps the latest dialog visible when the conversation grows. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(
                (observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                sendButton.setDisable(newValue == null || newValue.isBlank()));
        sendButton.setDisable(true);
    }

    /**
     * Injects the chatbot model used to process user commands.
     *
     * @param waffles the chatbot model
     */
    public void setWaffles(Waffles waffles) {
        this.waffles = waffles;
    }

    /** Handles both the Send button and Enter key events from the text field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = waffles.getResponse(input);
        DialogBox responseDialog = response.startsWith("Oops")
                ? DialogBox.getErrorDialog(response, wafflesImage)
                : DialogBox.getWafflesDialog(response, wafflesImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), responseDialog);
        userInput.clear();
        userInput.requestFocus();
    }
}
