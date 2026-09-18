package lune.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lune.Lune;

/**
 * Controller for MainWindow.fxml: the main chat window. Wires up user
 * input (Enter key or Send button) to Lune's getResponse(), appending
 * each exchange as a pair of dialog boxes to the scrolling chat area.
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

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image luneImage = new Image(this.getClass().getResourceAsStream("/images/DaLune.png"));

    private Lune lune;

    /**
     * Called automatically by the FXMLLoader once the FXML fields above
     * are injected. Keeps the chat area scrolled to the latest message.
     */
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Lune instance this window sends user input to.
     */
    public void setLune(Lune lune) {
        this.lune = lune;
    }

    /**
     * Handles the Send button/Enter-key action: shows the user's message,
     * asks Lune for a response, shows that too, then clears the input
     * field. On "bye", closes the window shortly after showing Lune's
     * farewell, matching the console app's behavior of exiting on "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = lune.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLuneDialog(response, luneImage, lune.getCommandType()));
        userInput.clear();
        if (input.equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
