package suki;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * One message in the conversation: an avatar beside the text that was said.
 *
 * <p>This is a custom control, so the same class serves both speakers. The
 * user's boxes keep the avatar on the right; Suki's are flipped so the two
 * sides of the conversation are easy to tell apart at a glance.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;
    /**
     * Creates a dialog box showing the given text and avatar.
     *
     * <p>Private because the two named factory methods below say which speaker
     * a box belongs to, which reads better at the call site than a boolean.
     *
     * @param text the message to show
     */
    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /** Puts the avatar on the left and the text on the right. */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for something the user said.
     *
     * @param text the user's message
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text, "U");
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /**
     * Returns a dialog box for something Suki said, mirrored so that it does
     * not look like the user's messages.
     *
     * @param text Suki's reply
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getSukiDialog(String text) {
        DialogBox box = new DialogBox(text, "S");
        box.getStyleClass().add("suki-dialog");
        box.flip();
        return box;
    }

    /**
     * Returns a visually prominent Suki dialog for invalid commands.
     *
     * @param text the explanation of the error
     * @return an error-styled dialog box
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox box = getSukiDialog(text);
        box.getStyleClass().add("error-dialog");
        return box;
    }
}
