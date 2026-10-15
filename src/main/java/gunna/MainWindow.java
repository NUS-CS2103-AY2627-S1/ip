package gunna;

import gunna.gui.DialogBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

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

    private Gunna gunna;

    private Image gunnaImage = new Image(this.getClass().getResourceAsStream("/images/DaGunna.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Gunna instance */
    public void setGunna(Gunna g) {
        gunna = g;
    }

    /** Shows the welcome message */
    public void showWelcomeMessage() {
        String welcome = gunna.getWelcomeMessage();
        dialogContainer.getChildren().add(
                DialogBox.getGunnaDialog(welcome, gunnaImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Gunna's reply
     * and then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Gunna.Response response = gunna.getResponseWithStatus(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                createGunnaDialog(response)
        );
        userInput.clear();
    }

    /**
     * Creates the appropriate Gunna dialog based on whether command processing failed.
     *
     * @param response The response returned after processing user input.
     * @return A normal or error-styled Gunna dialog.
     */
    private DialogBox createGunnaDialog(Gunna.Response response) {
        if (response.isError()) {
            return DialogBox.getErrorDialog(response.message(), gunnaImage);
        }
        return DialogBox.getGunnaDialog(response.message(), gunnaImage);
    }
}
