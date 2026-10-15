package bucket;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * One message in the conversation, shown as a bubble with an optional picture.
 *
 * The two speakers deliberately do not look alike. The conversation is between a
 * person and a tool rather than between two people, so mirroring one side into the
 * other would suggest a symmetry that is not there. That is why this no longer
 * reverses the same row for the second speaker.
 *
 * Bucket's replies sit on the left, run the full width of the window and use a
 * fixed-width font, because they are mostly task lists whose columns need to line
 * up and which are long enough to want the space. What the user typed is a short
 * echo, so it sits right, hugs its own text and keeps the proportional font.
 * Replies reporting a problem are tinted and carry a bar down their left edge.
 *
 * One of these is created per message, so the layout is loaded from DialogBox.fxml
 * with the fx:root construct: the box being built is set as both root and controller,
 * which lets the same FXML be reused for every message.
 */
public class DialogBox extends HBox {
    /** Matches the fitWidth of the ImageView in DialogBox.fxml. */
    private static final double AVATAR_SIZE = 24.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing one message.
     *
     * @param text Message to show.
     * @param img Picture of whoever said it, or null to show no picture at all.
     */
    private DialogBox(String text, Image img) {
        assert text != null : "a dialog box needs text to show";

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The IOException above is caught rather than rethrown, so a failed load lets
        // execution continue with both fields still null. These assertions name the
        // missing fx:id instead of leaving a bare NullPointerException.
        assert dialog != null : "fx:id \"dialog\" missing from DialogBox.fxml";
        assert displayPicture != null : "fx:id \"displayPicture\" missing from DialogBox.fxml";

        dialog.setText(text);
        setPicture(img);
    }

    /**
     * Returns a dialog box for something the user said: right-aligned, in the accent
     * colour, sized to its own text and carrying no picture.
     *
     * The user needs no portrait of themselves. Dropping it hands that width back to
     * Bucket's replies, which are the ones long enough to need it.
     *
     * @param text Message the user typed.
     * @return Dialog box to add to the conversation.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, null);
        db.setAlignment(Pos.TOP_RIGHT);
        db.dialog.getStyleClass().addAll("user-bubble", "user-text");
        return db;
    }

    /**
     * Returns a dialog box for something Bucket said: left-aligned, running the full
     * width, with a small round picture. An error is tinted rather than grey.
     *
     * @param response Reply from the chatbot, which knows whether it is an error.
     * @param img Picture of Bucket.
     * @return Dialog box to add to the conversation.
     */
    public static DialogBox getBucketDialog(Response response, Image img) {
        assert response != null : "a dialog box needs a reply to show";

        DialogBox db = new DialogBox(response.text(), img);
        db.setAlignment(Pos.TOP_LEFT);

        // Letting the label take the leftover width is what makes a long task list
        // use the window rather than stopping at some fixed column.
        HBox.setHgrow(db.dialog, Priority.ALWAYS);
        db.dialog.setMaxWidth(Double.MAX_VALUE);

        if (response.isError()) {
            db.dialog.getStyleClass().addAll("error-bubble", "bot-text", "error-text");
        } else {
            db.dialog.getStyleClass().addAll("bot-bubble", "bot-text");
        }
        return db;
    }

    /**
     * Shows the given picture cropped to a circle, or removes the picture entirely
     * when there is none.
     *
     * Clipping here rather than relying on the image file means any picture dropped
     * in comes out round, square corners and all.
     *
     * @param img Picture to show, or null for none.
     */
    private void setPicture(Image img) {
        if (img == null) {
            // Unmanaged as well as invisible, so it leaves no gap where it would be.
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
            return;
        }

        displayPicture.setImage(img);

        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }
}
