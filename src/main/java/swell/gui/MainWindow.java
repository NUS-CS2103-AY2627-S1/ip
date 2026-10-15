package swell.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import swell.Swell;

/**
 * Controls the main Swell chat window.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_AVATAR_PATH = "/images/Moon.png";
    private static final String SWELL_AVATAR_PATH = "/images/Star.png";
    private static final String ERROR_RESPONSE_PREFIX = "Oops!";

    private final Image userImage = new Image(getClass().getResourceAsStream(USER_AVATAR_PATH));
    private final Image swellImage = new Image(getClass().getResourceAsStream(SWELL_AVATAR_PATH));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Swell swell;

    /**
     * Sets up automatic scrolling after FXML fields are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Swell chatbot used by this window.
     *
     * @param swell chatbot instance.
     */
    public void setSwell(Swell swell) {
        this.swell = swell;
        dialogContainer.getChildren().add(DialogBox.getSwellDialog(swell.getGreeting(), swellImage));
    }

    /**
     * Handles user input from the text field or send button.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            userInput.clear();
            return;
        }

        String response = swell.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                getSwellDialog(response));
        userInput.clear();

        if (swell.isExit()) {
            disableInputControls();
        }
    }

    private void disableInputControls() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }

    private DialogBox getSwellDialog(String response) {
        if (response.startsWith(ERROR_RESPONSE_PREFIX)) {
            return DialogBox.getSwellErrorDialog(response, swellImage);
        }
        return DialogBox.getSwellDialog(response, swellImage);
    }
}
