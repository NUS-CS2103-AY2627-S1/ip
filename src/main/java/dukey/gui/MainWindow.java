package dukey.gui;

import dukey.Dukey;
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

/**
 * Controls the main chat window.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image dukeyImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Dukey dukey;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot instance used by the GUI.
     *
     * @param dukey Chatbot application logic.
     */
    public void setDukey(Dukey dukey) {
        this.dukey = dukey;
        dialogContainer.getChildren().add(DialogBox.getDukeyDialog(
                "Hello! I'm Dukey, your calm task librarian.\n"
                        + "What shall we shelve, sort, or settle today?", dukeyImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = dukey.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeyDialog(response, dukeyImage, dukey.getResponseStyleClass()));
        userInput.clear();

        if (dukey.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
