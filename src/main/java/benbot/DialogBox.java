package benbot;

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

/** Displays one speaker's avatar and message in the conversation. */
public class DialogBox extends HBox {
    /** The message bubble. */
    @FXML
    private Label dialog;

    /** The small text avatar identifying the speaker. */
    @FXML
    private Label avatar;

    /** Loads a reusable dialog box from FXML. */
    private DialogBox(String text, String avatarText) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", e);
        }
        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /** Returns a right-aligned dialog spoken by the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text, "YOU");
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Returns a left-aligned dialog spoken by BenBot. */
    public static DialogBox getBotDialog(String text) {
        DialogBox box = new DialogBox(text, "BEN");
        box.flip();
        box.getStyleClass().add("bot-dialog");
        return box;
    }

    /** Places the avatar before the message for BenBot's side of the conversation. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
