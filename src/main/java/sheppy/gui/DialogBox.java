package sheppy.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Displays one message with an avatar for either the user or Sheppy. */
public class DialogBox extends HBox {
    /** Displays the message text. */
    @FXML
    private Label dialogText;

    /** Identifies the speaker with a simple text avatar. */
    @FXML
    private Label avatar;

    /** Loads the reusable dialogue-box layout. */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialogue box.", exception);
        }
        dialogText.setText(text);
    }

    /**
     * Creates a dialogue box aligned and styled for the user.
     *
     * @param text the user's message
     * @return the configured dialogue box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("YOU");
        dialogBox.getChildren().clear();
        dialogBox.getChildren().addAll(dialogBox.dialogText, dialogBox.avatar);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialogue box aligned and styled for Sheppy.
     *
     * @param text Sheppy's message
     * @return the configured dialogue box
     */
    public static DialogBox getSheppyDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("S");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getStyleClass().add("sheppy-dialog");
        return dialogBox;
    }
}
