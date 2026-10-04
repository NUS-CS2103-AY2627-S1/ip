package penny.ui;

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
import penny.Penny;

/**
 * Acts as the controller for the main graphical user interface.
 * Handles user interactions, updates dialogue view, and communicates with Penny.
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

    private Penny penny;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image pennyImage = new Image(this.getClass().getResourceAsStream("/images/DaPenny.png"));

    /**
     * Initializes the controller after root elements have been processed.
     * Binds scroll pane to dialog container height to automatically scroll down.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "fx:id 'scrollPane' was not injected: check FXML file";
        assert dialogContainer != null : "fx:id 'dialogContainer' was not injected: check FXML file";
        assert userInput != null : "fx:id 'userInput' was not injected: check FXML file";
        assert sendButton != null : "fx:id 'sendButton' was not injected: check FXML file";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Penny instance and shows the initial welcome message.
     *
     * @param p The Penny chatbot instance.
     */
    public void setPenny(Penny p) {
        assert p != null : "Penny instance must not be null";
        penny = p;
        dialogContainer.getChildren().addAll(
                DialogBox.getPennyDialog(penny.getWelcomeMessage(), pennyImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Penny's reply,
     * and appends them to the dialog container. Clears user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert penny != null : "Penny instance must be initialized before handling user input";
        assert userInput != null : "userInput field must not be null";

        String input = userInput.getText();
        String response = penny.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPennyDialog(response, pennyImage)
        );
        userInput.clear();

        if (penny.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(3.0));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
