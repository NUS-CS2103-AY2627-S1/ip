package moon;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Displays one user or Moon message in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private ImageView moonAvatar;

    @FXML
    private Label messageLabel;

    @FXML
    private Label userAvatar;

    private final String message;
    private final boolean userMessage;
    private final boolean errorMessage;

    /** Creates a dialog message with its speaker direction. */
    public DialogBox(String message, boolean userMessage) {
        this.message = message;
        this.userMessage = userMessage;
        this.errorMessage = message.startsWith("Oof!");
    }

    /** Initializes the message label after the dialog FXML has been loaded. */
    @FXML
    private void initialize() {
        assert messageLabel != null : "DialogBox.fxml must inject the message label.";
        assert moonAvatar != null : "DialogBox.fxml must inject the Moon avatar.";
        assert userAvatar != null : "DialogBox.fxml must inject the user avatar.";

        messageLabel.setText(message);
        getStyleClass().add(userMessage ? "user-message" : "moon-message");
        if (errorMessage) {
            getStyleClass().add("error-message");
        }

        getChildren().setAll(userMessage ? userAvatar : moonAvatar, messageLabel);
        setAlignment(userMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    }
}
