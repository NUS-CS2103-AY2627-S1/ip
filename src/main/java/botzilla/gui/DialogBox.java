package botzilla.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the
 * speaker's face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);

        // Source images aren't necessarily square, so centre-crop to a
        // square viewport first (like CSS's "object-fit: cover") before
        // clipping to a circle. Otherwise, with preserveRatio scaling,
        // the ImageView's rendered bounds shrink to match the image's
        // own aspect ratio rather than filling the fitWidth x fitHeight
        // box, and the circle clip ends up cut short on one axis.
        double side = Math.min(img.getWidth(), img.getHeight());
        double x = (img.getWidth() - side) / 2;
        double y = (img.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(x, y, side, side));

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and
     * text is on the right, used to distinguish Botzilla's replies from
     * the user's messages. Also swaps in the "reply-label" style class
     * so Botzilla's bubble is styled differently from the user's
     * (see DialogBox.css).
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a message from the user, with the display
     * picture on the right and text on the left.
     *
     * @param text text to display in the dialog box
     * @param img  display picture to show
     * @return the constructed dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for a message from Botzilla, flipped so the
     * display picture is on the left and text is on the right.
     *
     * @param text text to display in the dialog box
     * @param img  display picture to show
     * @return the constructed dialog box
     */
    public static DialogBox getBotzillaDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
