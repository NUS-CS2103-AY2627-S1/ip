package sioet.ui;

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

    private Sioet sioet;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    private final Image sioetImage =
            new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));


    /**
     * Injects the Sioet instance.
     *
     * @param sioet the Sioet chatbot
     */
    public void setSioet(Sioet sioet) {
        this.sioet = sioet;
    }

    /**
     * Handles user input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        String response = sioet.getResponse(input);

        DialogBox sioetDialog = sioet.wasLastResponseAnError()
                ? DialogBox.getErrorDialog(response, sioetImage)
                : DialogBox.getSioetDialog(response, sioetImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                sioetDialog
        );

        userInput.clear();

        // Scroll to the newest messages
        javafx.application.Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });

        if (input.trim().equalsIgnoreCase("bye")) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
        }
    }
}

