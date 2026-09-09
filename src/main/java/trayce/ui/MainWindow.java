package trayce.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import trayce.Trayce;

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

    private Trayce trayce;

    private Image userImage = new Image(this.getClass()
            .getResourceAsStream("/images/user.jpg"));

    private Image trayceImage = new Image(this.getClass()
            .getResourceAsStream("/images/DaTrayce.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Trayce instance.
     */
    public void setTrayce(Trayce d) {
        trayce = d;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Duke's reply, then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = trayce.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTrayceDialog(response, trayceImage)
        );

        userInput.clear();
    }
}
