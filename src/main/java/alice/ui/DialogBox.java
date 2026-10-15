package alice.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message and its speaker avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label avatar;

    @FXML
    private Label dialog;

    private DialogBox(String text, String avatarText, String styleClass) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
        getStyleClass().add(styleClass);
    }

    /**
     * Creates a dialog aligned to the right for a user command.
     *
     * @param text The command text.
     * @return The created user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "You", "user-dialog");
        dialogBox.flip();
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.setMaxWidth(Double.MAX_VALUE);
        return dialogBox;
    }

    /**
     * Creates a dialog aligned to the left for Alice's response.
     *
     * @param text The response text.
     * @return The created bot dialog.
     */
    public static DialogBox getBotDialog(String text) {

        return new DialogBox(text, "◇", "bot-dialog");
    }

    /**
     * Creates a dialog that draws attention to an error in a user command.
     *
     * @param text The error explanation.
     * @return The formatted error dialog.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, "!", "error-dialog");
    }

    /**
     * Reverses the text and avatar positions.
     */
    private void flip() {
        List<Node> children = new ArrayList<>(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }
}
