package littledaisy.ui;

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

/** Displays one speaker's message and a small identifying avatar. */
public class DialogBox extends HBox {
    private static final String USER_AVATAR = "You";
    private static final String DAISY_AVATAR = "Daisy";

    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    /** Loads the reusable dialog view and fills it with one message. */
    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box", e);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /** Places the avatar on the left for littleDaisy's replies. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("daisy-dialog");
        avatar.getStyleClass().add("daisy-avatar");
    }

    /** Returns a right-aligned dialog for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, USER_AVATAR);
    }

    /** Returns a left-aligned dialog for a littleDaisy response. */
    public static DialogBox getDaisyDialog(String text) {
        DialogBox box = new DialogBox(text, DAISY_AVATAR);
        box.flip();
        return box;
    }

    /** Returns a visually prominent left-aligned error response. */
    public static DialogBox getErrorDialog(String text) {
        DialogBox box = getDaisyDialog(text);
        box.dialog.getStyleClass().add("error-dialog");
        box.avatar.getStyleClass().add("error-avatar");
        return box;
    }
}
