package nova.gui;

import java.io.IOException;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * One message in the conversation.
 *
 * <p>The two sides are drawn differently on purpose. This is a conversation
 * between a person and a tool, not between two peers: what the user typed sits
 * right-aligned in a solid bubble with no avatar, since there is only ever one
 * user and a picture of them tells the reader nothing. Nova's replies sit
 * left-aligned on a quiet surface behind a small round mark, and switch to a
 * red treatment when the reply reports a problem.
 */
public class DialogBox extends HBox {
    /** Share of the available width a bubble may occupy before it wraps. */
    private static final double MAX_WIDTH_FRACTION = 0.82;

    /** Diameter of the avatar, matching fitWidth/fitHeight in the FXML. */
    private static final double AVATAR_SIZE = 24.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView avatar;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box", e);
        }
        dialog.setText(text);
    }

    /**
     * Creates a box for something the user said: right-aligned, no avatar.
     *
     * @param text what the user typed
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.dialog.getStyleClass().add("bubble-user");
        box.setAlignment(Pos.TOP_RIGHT);
        // The user is the only person here, so their picture carries no
        // information; dropping it buys width back for the text.
        box.getChildren().remove(box.avatar);
        return box;
    }

    /**
     * Creates a box for something Nova said: left-aligned, behind a round mark.
     *
     * @param text    the reply
     * @param mark    Nova's avatar
     * @param isError whether the reply reports a problem
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getNovaDialog(String text, Image mark, boolean isError) {
        DialogBox box = new DialogBox(text);
        box.dialog.getStyleClass().add(isError ? "bubble-error" : "bubble-nova");
        box.setAlignment(Pos.TOP_LEFT);
        box.avatar.setImage(mark);
        // Round the picture rather than shipping a round image, so any square
        // source drops in cleanly.
        double radius = AVATAR_SIZE / 2;
        box.avatar.setClip(new Circle(radius, radius, radius));
        return box;
    }

    /**
     * Keeps the bubble wrapping to the window rather than to a fixed width, so
     * the conversation reflows when the window is resized.
     *
     * @param containerWidth the width of the conversation column
     */
    public void wrapWithin(ReadOnlyDoubleProperty containerWidth) {
        dialog.maxWidthProperty().bind(containerWidth.multiply(MAX_WIDTH_FRACTION));
        HBox.setHgrow(dialog, Priority.NEVER);
        setMaxWidth(Region.USE_PREF_SIZE);
    }
}
