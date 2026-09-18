package tally.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** One turn of the conversation: a picture of whoever spoke, and what they said. */
public class DialogBox extends HBox {
    /**
     * How much of the window's width one side's words may take.
     *
     * <p>Bound to the window rather than fixed, so that widening it gives the text the
     * room rather than leaving a margin that grows while the words stay in a column.
     * Short of the whole width, so that which side spoke is still read at a glance.
     */
    private static final double SHARE_OF_WIDTH = 0.85;

    @FXML
    private Label dialogText;
    @FXML
    private ImageView speakerPicture;

    private DialogBox(String text, Image picture) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("A dialog box could not be built.", exception);
        }
        dialogText.setText(text);
        speakerPicture.setImage(picture);
        cropToCircle(speakerPicture);
        dialogText.maxWidthProperty().bind(widthProperty().multiply(SHARE_OF_WIDTH));
    }

    /**
     * Crops a picture to a circle.
     *
     * <p>The corners of a square portrait are the part that shows the backdrop it was
     * cut from rather than the face, so taking them off is what lets the picture sit on
     * the window's own colour instead of on a patch of its own.
     *
     * @param picture the view to crop, already given the size it will be shown at.
     */
    private static void cropToCircle(ImageView picture) {
        double radius = picture.getFitWidth() / 2;
        picture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Returns a box showing what the user said, with their picture on the right.
     *
     * @param text what the user typed.
     * @param picture the user's picture.
     * @return the box to add to the conversation.
     */
    static DialogBox createUserDialog(String text, Image picture) {
        return new DialogBox(text, picture);
    }

    /**
     * Returns a box showing what Tally said, with its picture on the left.
     *
     * <p>Tally's boxes are mirrored so the two speakers face each other, which is
     * what lets a reader tell them apart without reading a word.
     *
     * @param text what Tally replied.
     * @param picture Tally's picture.
     * @return the box to add to the conversation.
     */
    static DialogBox createTallyDialog(String text, Image picture) {
        DialogBox box = new DialogBox(text, picture);
        box.flip();
        box.getStyleClass().add("from-tally");
        return box;
    }

    /** Puts this box's picture on the left of its text rather than the right. */
    private void flip() {
        List<Node> children = new ArrayList<>(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
