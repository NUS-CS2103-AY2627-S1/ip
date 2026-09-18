package kenbot;

import java.io.IOException;
import java.io.UncheckedIOException;

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
import javafx.scene.shape.Circle;

/**
 * One line of the conversation: a message beside the picture of whoever said it.
 *
 * <p>This is a custom control, so it is loaded differently from the main
 * window. Rather than the FXML naming its controller, the constructor hands the
 * loader this very object as both the root and the controller. That is what
 * {@code <fx:root>} in {@code DialogBox.fxml} means, and it is what lets a
 * dialog box be created with {@code new} like any other object.</p>
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, String bubbleStyle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            // Both must be set before load(): afterwards the loader has already
            // decided what to build and who to wire it to.
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // DialogBox.fxml is packaged inside the JAR, so failing to read it
            // means the build is broken rather than anything a user can fix or
            // this class can carry on without. Printing and continuing would
            // leave dialog and displayPicture null, and the next two lines
            // would then fail with a NullPointerException naming neither the
            // file nor the real cause.
            throw new UncheckedIOException("Could not load /view/DialogBox.fxml", e);
        }

        // The loader fills these in by matching each fx:id in DialogBox.fxml
        // against a field name here, at run time, and the compiler never opens
        // the FXML to check that the two still agree. A failed load throws
        // above, so reaching this line with a null field means a renamed
        // fx:id, which would otherwise surface as a NullPointerException on
        // the next line instead.
        assert dialog != null && displayPicture != null
                : "DialogBox.fxml did not fill in its controls";

        // Bound rather than set once: the window can be resized at any time, so
        // a fixed width would either waste space when the window grows or clip
        // when it shrinks. Six tenths keeps a long message readable instead of
        // letting one line run the full width of a large screen.
        dialog.maxWidthProperty().bind(this.widthProperty().multiply(0.6));

        dialog.getStyleClass().add(bubbleStyle);
        dialog.setText(text);

        if (img == null) {
            // The user's own side carries no picture: they know who they are,
            // and the space is better spent on the reply. Removed rather than
            // hidden so it takes up no room at all.
            this.getChildren().remove(displayPicture);
        } else {
            displayPicture.setImage(img);
            // A circle the size of the picture, centred on it. Done here
            // because JavaFX CSS cannot clip a node to a shape.
            double radius = displayPicture.getFitWidth() / 2;
            displayPicture.setClip(new Circle(radius, radius, radius));
        }
    }

    /**
     * Creates a dialog box for something the user said.
     *
     * <p>No picture: the user knows who they are, so the space it would take is
     * given to the reply instead.</p>
     *
     * @param text what the user typed
     * @return a dialog box aligned to the right
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null, "user-bubble");
    }

    /**
     * Creates a dialog box for something Kenbot said.
     *
     * @param text Kenbot's reply
     * @param img Kenbot's picture
     * @return a dialog box aligned to the left, mirroring the user's
     */
    public static DialogBox getKenbotDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img, "reply-bubble");
        box.flip();
        return box;
    }

    /**
     * Creates a dialog box for a command Kenbot refused.
     *
     * <p>Shown differently from an ordinary reply because the two mean opposite
     * things: one says a command was carried out, the other that it was not.
     * Presenting them identically leaves the user to read carefully to find out
     * which happened.</p>
     *
     * @param text the reason the command could not be used
     * @param img Kenbot's picture
     * @return a dialog box aligned to the left, styled as a problem
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox box = new DialogBox(text, img, "error-bubble");
        box.flip();
        return box;
    }

    /**
     * Mirrors this dialog box so the picture is on the left and the text on the
     * right, which is what tells the two speakers apart at a glance.
     */
    private void flip() {
        // Copied out first: getChildren() is a live list the scene graph is
        // watching, so it cannot be reversed in place.
        ObservableList<Node> reordered = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(reordered);
        this.getChildren().setAll(reordered);
        this.setAlignment(Pos.TOP_LEFT);
    }
}
