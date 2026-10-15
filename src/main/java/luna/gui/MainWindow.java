package luna.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import luna.Luna;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image lunaImage = new Image(this.getClass().getResourceAsStream("/images/DaLuna.png"));
    private Luna luna;

    /**
     * Initializes the UI components after the FXML has been loaded.
     *
     * Binds the scroll pane's vertical scroll value to the height of the dialog container so the view
     * automatically scrolls to the latest dialog as new messages are added.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Luna instance used by this GUI.
     *
     * @param luna Luna backend instance.
     */
    public void setLuna(Luna luna) {
        this.luna = luna;
    }

    /**
     * Displays Luna's welcome message in the conversation.
     */
    public void showWelcomeMessage() {
        dialogContainer.getChildren().add(
                DialogBox.getLunaDialog(luna.getWelcomeMessage(), lunaImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Luna's reply
     * and then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = luna.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLunaDialog(response, lunaImage)
        );
        userInput.clear();

        if (luna.isExit()) {
            Platform.exit();
        }
    }
}
