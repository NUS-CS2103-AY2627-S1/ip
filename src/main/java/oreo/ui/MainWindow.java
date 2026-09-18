package oreo.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import oreo.Oreo;

/** Controls the Oreo chat window and forwards input to Oreo. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    private Oreo oreo;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/User.png"));
    private final Image oreoImage = new Image(getClass().getResourceAsStream("/images/Oreo.png"));

    /** Binds scrolling to the bottom of the conversation. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.reply(
                "Hello! I'm Oreo.\nLet's get started, shall we?", oreoImage));
    }

    /** Supplies the Oreo application used by this window. */
    public void setOreo(Oreo oreo) {
        this.oreo = oreo;
    }

    /** Displays the user's command and Oreo's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = oreo.processCommand(input);
        DialogBox reply = DialogBox.reply(response, oreoImage);
        if (oreo.wasLastCommandError()) {
            reply.markAsError();
        }
        dialogContainer.getChildren().addAll(DialogBox.user(input, userImage), reply);
        userInput.clear();
    }
}
