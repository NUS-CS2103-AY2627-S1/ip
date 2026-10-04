package shrek;

import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Shrek chat window declared in {@code MainWindow.fxml}.
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

    private final Image userImage = loadImage("/images/DaDonkey.png");
    private final Image shrekImage = loadImage("/images/DaShrek.png");
    private Shrek shrek;

    /** Sets up automatic scrolling after FXML injects the controls. */
    @FXML
    public void initialize() {
        dialogContainer.setSpacing(8);
        dialogContainer.setFillWidth(true);
        dialogContainer.setMinWidth(0);
        dialogContainer.setMaxWidth(Double.MAX_VALUE);
        scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                dialogContainer.setPrefWidth(newBounds.getWidth()));
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot model used by this controller.
     *
     * @param chatbot the Shrek chatbot instance.
     */
    public void setShrek(Shrek chatbot) {
        shrek = chatbot;
        addShrekMessage("Hello! I'm Shrek.\nWhat can I do for you?", CommandType.UNKNOWN);
    }

    /** Handles both the Send button and the Enter key in the text field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        addUserMessage(input);
        userInput.clear();

        String response = shrek.getResponse(input);
        addShrekMessage(response, shrek.getLastCommandType());

        if (shrek.getLastCommandType() == CommandType.BYE) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        } else {
            userInput.requestFocus();
        }
    }

    /** Adds a right-aligned user message to the conversation. */
    private void addUserMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getUserDialog(message, userImage));
    }

    /** Adds a left-aligned Shrek message to the conversation. */
    private void addShrekMessage(String message, CommandType commandType) {
        dialogContainer.getChildren().add(DialogBox.getShrekDialog(message, shrekImage, commandType));
    }

    /** Loads a required image resource from the application classpath. */
    private static Image loadImage(String path) {
        InputStream stream = MainWindow.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Missing image resource: " + path);
        }
        return new Image(stream);
    }
}
