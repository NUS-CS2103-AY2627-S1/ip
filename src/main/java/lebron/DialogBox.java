package lebron;

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

/**
 * One row of the conversation: a speech-bubble label, optionally next to a
 * small avatar. Created only through {@link #getUserDialog(String)} and
 * {@link #getLebronDialog(String, Image, boolean)}, which style and lay out
 * the two speakers differently rather than as plain mirror images of each
 * other.
 */
public class DialogBox extends HBox {
    /**
     * Margin (px) reserved outside the label for spacing/avatar, so the
     * label's max width can track this row's own width instead of being
     * capped at a fixed pixel width.
     */
    private static final double WIDTH_MARGIN = 46;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml failed to load", e);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(WIDTH_MARGIN));
        if (image == null) {
            displayPicture.setVisible(false);
            displayPicture.setManaged(false);
        } else {
            displayPicture.setImage(image);
        }
    }

    /** Mirrors the row (avatar on the left) so Lebron's messages read as replies. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        List<Node> children = new ArrayList<>(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Returns a dialog box for something the user typed. The user's avatar is
     * omitted entirely (rather than shown small): with only two fixed
     * speakers, right-alignment and the bubble colour already say who's
     * speaking, so the avatar would just be reclaimed empty space.
     *
     * @param text the user's input
     * @return the dialog box, right-aligned, styled as a user message
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Returns a dialog box for one of Lebron's responses, styled as an error
     * bubble when the response is one of Lebron's {@code OOPS!!!} messages so
     * mistakes stand out at a glance.
     *
     * @param text Lebron's response text
     * @param image Lebron's avatar
     * @param isError whether this response is an error message
     * @return the dialog box, left-aligned, avatar on the left
     */
    public static DialogBox getLebronDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add(isError ? "error-bubble" : "lebron-bubble");
        return dialogBox;
    }
}
