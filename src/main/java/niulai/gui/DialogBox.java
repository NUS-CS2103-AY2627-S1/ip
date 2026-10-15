package niulai.gui;

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

/** Represents one user or chatbot message in the conversation. */
public class DialogBox extends HBox {
    /** The label containing the message text. */
    @FXML
    private Label dialog;

    /** The label identifying the speaker. */
    @FXML
    private Label speaker;

    /**
     * Creates a dialog box and loads its reusable FXML layout.
     *
     * @param text the message to display
     * @param isUser whether the message was sent by the user
     */
    private DialogBox(String text, boolean isUser) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.requireResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create a chat dialog.", e);
        }

        dialog.setText(text);
        speaker.setText(isUser ? "You" : "NL");
        if (!isUser) {
            flip();
        }
    }

    /** Creates a right-aligned dialog box for a user command. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true);
    }

    /** Creates a left-aligned dialog box for a chatbot response. */
    public static DialogBox getBotDialog(String text) {
        return new DialogBox(text, false);
    }

    /** Flips the speaker label to the left side of the dialog text. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
