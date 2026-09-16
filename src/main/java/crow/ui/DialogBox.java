package crow.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays a chat message with a clear sender label.
 */
public class DialogBox extends HBox {
    @FXML
    private Label senderLabel;
    @FXML
    private Label messageLabel;

    /**
     * Loads the dialog layout and fills it with a sender and message.
     */
    private DialogBox(String sender, String message, String styleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        senderLabel.setText(sender);
        messageLabel.setText(message);
        getStyleClass().add(styleClass);
    }

    /**
     * Creates a dialog for a message sent by the user.
     */
    public static DialogBox createUserDialog(String message) {
        return new DialogBox("You", message, "user-message");
    }

    /**
     * Creates a dialog for a response sent by Crow.
     */
    public static DialogBox createCrowDialog(String message) {
        return new DialogBox("Crow", message, "crow-message");
    }
}
