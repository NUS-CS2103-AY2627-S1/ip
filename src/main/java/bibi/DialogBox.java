package bibi;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * One turn of the conversation: a picture of the speaker beside what they said.
 *
 * <p>The two speakers get mirrored layouts, so the transcript reads as a chat
 * rather than as a list. The user's box keeps the FXML's order, picture on the
 * right; Bibi's is reversed by {@link #flip()}. Construction is private because
 * a caller should say who is speaking, through {@link #forUser} or
 * {@link #forBibi}, rather than remember to flip the box themselves.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing one speaker's words.
     *
     * @param text what was said
     * @param speakerImage the picture of whoever said it
     */
    private DialogBox(String text, Image speakerImage) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML));
            // This object is both the root and the controller, which is what lets
            // a dialog box be created in code and still be laid out by FXML.
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + DIALOG_BOX_FXML, exception);
        }

        dialog.setText(text);
        displayPicture.setImage(speakerImage);
    }

    /**
     * Returns a dialog box for something the user typed.
     *
     * @param text what the user typed
     * @param speakerImage the user's picture
     * @return a box with the picture on the right
     */
    public static DialogBox forUser(String text, Image speakerImage) {
        return new DialogBox(text, speakerImage);
    }

    /**
     * Returns a dialog box for one of Bibi's replies.
     *
     * @param text what Bibi said
     * @param speakerImage Bibi's picture
     * @return a box with the picture on the left
     */
    public static DialogBox forBibi(String text, Image speakerImage) {
        DialogBox reply = new DialogBox(text, speakerImage);
        reply.flip();
        return reply;
    }

    /**
     * Turns this box around, so the picture sits on the left of the words.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.setAlignment(Pos.TOP_LEFT);
    }
}
