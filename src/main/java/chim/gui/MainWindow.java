package chim.gui;

import chim.Chim;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    private Chim chim;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpg"));
    private Image chimImage = new Image(this.getClass().getResourceAsStream("/images/DaChim.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Chim instance.
     *
     * @param c Chim instance to use for generating responses.
     */
    public void setChim(Chim c) {
        chim = c;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Chim's reply, then appends them to the dialog container. Clears the user
     * input and exits the application if the command was "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = chim.getResponse(input);
        boolean isError = chim.isLastResponseError();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getChimDialog(response, chimImage, isError)
        );
        userInput.clear();

        if (chim.isExit(input)) {
            Platform.exit();
        }
    }
}
