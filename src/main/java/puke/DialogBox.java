package puke;

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

/** Represents one message in the Puke chat window. */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;
    @FXML
    private Label dialog;

    private DialogBox(String text, String speakerName, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a chat message", e);
        }
        getStyleClass().add(isUser ? "user-message" : "puke-message");
        speaker.setText(speakerName);
        dialog.setText(text);
        if (!isUser) {
            flip();
        }
    }

    /** Creates a dialog box for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You", true);
    }

    /** Creates a dialog box for a Puke response. */
    public static DialogBox getPukeDialog(String text) {
        return new DialogBox(text, "Puke", false);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
