package minweeder.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import minweeder.Minweeder;

/**
 * Controller for the main GUI window: a scrollable list of dialog bubbles, a
 * text field for typing commands, and a button to send them.
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

    private Minweeder minweeder;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/UserImage.png"));
    private final Image minweederImage =
            new Image(this.getClass().getResourceAsStream("/images/MinweederImage.png"));

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Minweeder instance this window talks to, and shows its
     * welcome message.
     *
     * @param minweeder the Minweeder instance backing this window.
     */
    public void setMinweeder(Minweeder minweeder) {
        this.minweeder = minweeder;
        dialogContainer.getChildren().add(
                DialogBox.getMinweederDialog(minweeder.getWelcomeMessage(), minweederImage));
    }

    /**
     * Sends the text currently in the input field to Minweeder, displays both
     * the input and its response as dialog bubbles, clears the input field,
     * and closes the window if the command was a request to exit.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        userInput.clear();
        if (input.isEmpty()) {
            return;
        }
        String response = minweeder.getResponse(input);
        DialogBox reply = minweeder.isError()
                ? DialogBox.getErrorDialog(response, minweederImage)
                : DialogBox.getMinweederDialog(response, minweederImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                reply);
        if (minweeder.isExit()) {
            Platform.exit();
        }
    }
}
