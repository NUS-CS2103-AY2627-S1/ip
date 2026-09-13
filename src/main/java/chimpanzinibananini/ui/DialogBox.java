package chimpanzinibananini.ui;

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
 * Displays one message and a label identifying its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label speaker;

    private DialogBox(String text, String speakerName) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box layout", e);
        }
        dialog.setText(text);
        speaker.setText(speakerName);
    }

    /**
     * Creates a right-aligned dialog containing a user message.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You");
    }

    /**
     * Creates a left-aligned dialog containing a chatbot message.
     */
    public static DialogBox getChatbotDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "ChimpanziniBananini");
        dialogBox.flip();
        return dialogBox;
    }

    /** Flips this dialog so the speaker label appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
