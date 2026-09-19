package leo;

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
import javafx.scene.shape.Circle;

/**
 * One row in the GUI's chat log: a circular avatar next to a speech
 * bubble of text, representing either something the user typed or one
 * of Leo's replies. Built from {@code DialogBox.fxml}.
 */
public class DialogBox extends HBox {
    // A-BetterGui: a light red background/border and dark red text for
    // error replies (see Reply, Leo#getReply), distinct enough from
    // the default bubble style that a mistake is obvious without
    // having to actually read the message - while still keeping the
    // same shape/padding/font as every other bubble, so it reads as
    // "the same kind of thing, but flagged" rather than a completely
    // different element.
    private static final String ERROR_STYLE =
            "-fx-background-color: #fdecea; -fx-background-radius: 8; "
                    + "-fx-border-color: #f5b8b0; -fx-border-radius: 8; "
                    + "-fx-border-width: 1; -fx-text-fill: #7a1f13; -fx-padding: 4;";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image, boolean isError, boolean isUser) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);

        // A-BetterGui: colour-codes the bubble by who sent it (see
        // theme.css's .user-bubble/.leo-bubble) - both style classes
        // are harmless to apply unconditionally since only one is ever
        // added per instance.
        dialog.getStyleClass().add(isUser ? "user-bubble" : "leo-bubble");

        if (isError) {
            dialog.setStyle(ERROR_STYLE);
        }

        // Clip the square avatar image to a circle, matching its
        // rounded position in the layout.
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    // Mirrors the dialog box (avatar on the left, text on the right)
    // and left-aligns it, for Leo's replies - so that the user's own
    // messages and Leo's replies read as visually distinct, on
    // opposite sides of the chat log.
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for something the user typed.
     *
     * @param text the user's input
     * @param image the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, false, true);
    }

    /**
     * Creates a (mirrored) dialog box for one of Leo's replies.
     *
     * @param text Leo's reply
     * @param image Leo's avatar
     * @param isError whether this reply is an error message - shown
     *     with a red-tinted style (see {@link #ERROR_STYLE}) instead of
     *     the default bubble style, so a mistake stands out at a
     *     glance in the chat log
     * @return the dialog box
     */
    public static DialogBox getLeoDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image, isError, false);
        dialogBox.flip();
        return dialogBox;
    }
}
