package billy.gui;

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
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * One message in the conversation: a small picture of who said it, beside their
 * words.
 *
 * <p>The user's messages and Billy's are the same box built two ways round,
 * rather than two classes that would have to be kept in step with each other.
 *
 * <p>The two are shown differently on purpose. This is not a conversation
 * between two people: the user types short commands, and Billy answers with
 * whatever the command produced, which may be a list of thirty tasks. So the
 * user's words sit on the right in a bubble no wider than it needs to be, and
 * Billy's take the full width of the window on the left. Errors are Billy's
 * words in a third form again, since the whole point of an error is that it
 * should not be skimmed past.
 */
public class DialogBox extends HBox {

    /** Radius of the round mask over the speaker's picture. */
    private static final double PICTURE_RADIUS = 14.0;

    /**
     * How much of the box's width the user's bubble may take up.
     *
     * <p>Left short of the full width so that a user message never looks like a
     * reply, even when it is a long one.
     */
    private static final double USER_WIDTH_FRACTION = 0.72;

    /** Put before an error, so that it reads as a warning even out of context. */
    private static final String ERROR_MARKER = "⚠  ";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Builds a dialog box showing what someone said.
     *
     * <p>Private because the static factory methods below say which kind of box
     * is wanted, which reads better at the call site than two booleans would.
     *
     * @param text what was said
     * @param img the speaker's picture
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // Loading is what creates the labels used just below, so carrying on
            // would fail on a null one and report a missing label rather than the
            // layout file that never loaded. Rethrowing keeps the real cause.
            throw new IllegalStateException("Could not load the dialog box layout.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        // A round mask, so square pictures still read as avatars. The pictures
        // are deliberately small: there are only ever two speakers, so a large
        // one would spend width on saying something the reader knows already.
        displayPicture.setClip(new Circle(PICTURE_RADIUS, PICTURE_RADIUS, PICTURE_RADIUS));
    }

    /**
     * Returns a box for something the user said, with the picture on the right.
     *
     * @param text what the user typed
     * @param img the user's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img);
        box.dialog.getStyleClass().add("user-label");
        // Bound rather than set, so the bubble keeps its share of the width as
        // the user resizes the window.
        box.dialog.maxWidthProperty().bind(box.widthProperty().multiply(USER_WIDTH_FRACTION));
        return box;
    }

    /**
     * Returns a box for something Billy said, with the picture on the left.
     *
     * @param text what Billy replied
     * @param img Billy's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getBillyDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img);
        box.flip();
        box.dialog.getStyleClass().add("reply-label");
        return box;
    }

    /**
     * Returns a box for something that went wrong, marked so it stands out from
     * an ordinary reply.
     *
     * @param text what went wrong
     * @param img Billy's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox box = new DialogBox(ERROR_MARKER + text, img);
        box.flip();
        box.dialog.getStyleClass().add("error-label");
        return box;
    }

    /**
     * Turns this box around, so the picture is on the left and the text on the
     * right, and lets the text take whatever width is left over.
     *
     * <p>Putting the two speakers on opposite sides is what lets the reader tell
     * at a glance who said what, without labeling every message.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);

        // Billy's answers are the long ones, so they are given the full width
        // rather than being wrapped early into a narrow column.
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }
}
