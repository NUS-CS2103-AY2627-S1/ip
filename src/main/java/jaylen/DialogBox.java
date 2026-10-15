package jaylen;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one user or Jaylen message in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label avatar;

    @FXML
    private Label dialog;

    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load a dialog box.", exception);
        }

        avatar.setText(avatarText);
        avatar.setAccessibleText(avatarText + " avatar");
        dialog.setText(text);
        dialog.setAccessibleText(text);
    }

    /** Places the user's avatar on the right side of the message. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_RIGHT);
    }

    /**
     * Creates a message spoken by the user.
     *
     * @param text the user's command
     * @return the styled user dialog
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "You");
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a message spoken by Jaylen.
     *
     * @param text Jaylen's response
     * @return the styled Jaylen dialog
     */
    public static DialogBox getJaylenDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "J");
        dialogBox.getStyleClass().add("jaylen-dialog");
        return dialogBox;
    }

    /**
     * Creates a visually prominent error message spoken by Jaylen.
     *
     * @param text the formatted error response
     * @return the styled error dialog
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "!");
        dialogBox.getStyleClass().addAll("jaylen-dialog", "error-dialog");
        return dialogBox;
    }

    /**
     * Makes the message bubble expand and wrap with the chat window.
     *
     * @param containerWidth the current width available for the conversation
     */
    public void bindDialogWidth(ObservableNumberValue containerWidth) {
        dialog.maxWidthProperty().bind(
                Bindings.max(240, Bindings.subtract(containerWidth, 110)));
    }
}
