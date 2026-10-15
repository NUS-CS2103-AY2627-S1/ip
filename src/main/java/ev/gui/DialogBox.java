package ev.gui;

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
 * One entry in the conversation: the speaker's picture next to what was said.
 *
 * <p>The box is built from its own FXML file, with the same object serving as both root
 * and controller, so the rest of the code can treat it as a plain control.
 *
 * <p>The two speakers do not look alike, because they are not alike: what the user typed
 * is a short message in a bubble on the right, while EV's reply is flat text that runs the
 * full width, since it is often a list that would be cramped inside a bubble.
 */
public class DialogBox extends HBox {

    /** Side of the avatar in pixels, matching the ImageView in the FXML. */
    private static final double AVATAR_SIZE = 32.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load /view/DialogBox.fxml", e);
        }

        assert dialog != null && displayPicture != null
                : "DialogBox.fxml must declare fx:id dialog and displayPicture";

        dialog.setText(text);
        displayPicture.setImage(image);
        cropToCircle();
    }

    /**
     * Returns a box for something the user said, with the picture on the right.
     *
     * @param text what the user typed.
     * @param image the user's picture.
     * @return the new dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a box for something EV said, with the picture on the left.
     *
     * @param text EV's reply.
     * @param image EV's picture.
     * @return the new dialog box.
     */
    public static DialogBox getEvDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        return box;
    }

    /**
     * Returns a box for EV refusing to do something, marked so that it stands out from an
     * ordinary reply and is not mistaken for a result.
     *
     * @param text EV's explanation of what went wrong.
     * @param image EV's picture.
     * @return the new dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox box = getEvDialog(text, image);
        box.dialog.getStyleClass().add("error-label");
        return box;
    }

    /**
     * Flips the box so that the picture is on the left and the text on the right, and lets
     * the text take whatever width is left, which is what a long reply needs.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);

        dialog.getStyleClass().add("reply-label");
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }

    /**
     * Clips the avatar to a circle, so that a rectangular photo does not sit as a hard
     * edged block against the rest of the window.
     */
    private void cropToCircle() {
        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }
}
