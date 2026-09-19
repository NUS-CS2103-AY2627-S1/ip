package bob;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Controller for the main GUI.
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

    private Bob bob;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image bobImage = new Image(this.getClass().getResourceAsStream("/images/DaBob.png"));

    /** Initializes the dialog pane's scroll behavior. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bob instance used to process user commands.
     *
     * @param b Bob instance to use for command processing.
     */
    public void setBob(Bob b) {
        bob = b;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Bob's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        String response = bob.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getBobDialog(response, bobImage));
        userInput.clear();
        if (new Parser().parse(input) == CommandType.BYE) {
            Window window = userInput.getScene().getWindow();
            window.hide();
        }
    }
}
