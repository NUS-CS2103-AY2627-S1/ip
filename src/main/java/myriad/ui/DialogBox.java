package myriad.ui;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

// AI-assisted: most of this file was written with Claude Code (Claude Opus),
// used by Ryan Chua, who reviewed and adapted the output.
/**
 * Represents one message in the conversation. The two sides are deliberately
 * drawn differently, because a chat with an app is not a chat between equals:
 * the user's commands are short, so they sit on the right as compact bubbles
 * with no picture, while Myriad's replies can run long, so they sit on the
 * left beside a small avatar and may use the rest of the width.
 *
 * The layout comes from {@code view/DialogBox.fxml}, which is an {@code fx:root} file: each
 * instance makes itself both the root and the controller of that file, so
 * that many dialog boxes can be built from one layout description.
 */
public class DialogBox extends HBox {

    /** Width and height the picture is drawn at, matching {@code DialogBox.fxml}. */
    private static final double PICTURE_SIZE = 32.0;

    /**
     * Room beside the picture that the bubble cannot use: the left and right
     * padding plus the spacing set in {@code DialogBox.fxml}. The bubble's own
     * padding and border from {@code dialog-box.css} are not counted, because
     * a Label's width already includes them.
     */
    private static final double BOX_CHROME_WIDTH = 24.0;

    /** Room left for the picture, the spacing and the padding around them. */
    private static final double NON_TEXT_WIDTH = PICTURE_SIZE + BOX_CHROME_WIDTH;

    /**
     * Largest share of the row a user's bubble may take. Keeping it below the
     * full width leaves a gap on the left, so the user's lines stay visibly
     * right-aligned even when a command is long enough to wrap.
     */
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.8;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing {@code message}, laid out as one of
     * Myriad's replies: the picture on the left and the text beside it.
     *
     * @param message what the speaker said.
     */
    private DialogBox(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The layout file ships inside the application, so a failure here
            // means a broken build rather than anything the user can act on.
            // Stopping now reports the real cause; carrying on would only fail
            // later with a NullPointerException on the unset dialog label.
            throw new IllegalStateException("Could not load the dialog box layout /view/DialogBox.fxml", e);
        }

        dialog.setText(message);
    }

    /**
     * Shows {@code picture} beside the text, cropped to a centred square and
     * clipped to a circle, so that pictures of any shape are shown as discs of
     * the same size. Cropping the source first matters: the view keeps the
     * picture's proportions, so a wide picture would otherwise be drawn
     * shorter than the circle and be cut off along the top and bottom.
     *
     * @param picture the picture to show, or null to leave the space blank.
     */
    private void showPicture(Image picture) {
        displayPicture.setImage(picture);
        if (picture == null) {
            return;
        }

        double side = Math.min(picture.getWidth(), picture.getHeight());
        // Centre the square viewport by trimming half the excess off each side.
        double offsetX = (picture.getWidth() - side) / 2;
        double offsetY = (picture.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(offsetX, offsetY, side, side));

        double radius = PICTURE_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Returns the picture stored at the given location on the classpath, or
     * null when there is no file there. A missing picture leaves a blank space
     * instead of stopping the window from opening.
     *
     * @param resourcePath classpath location of the image, e.g. {@code "/images/DaMyriad.png"}.
     * @return the loaded image, or null if the file is absent.
     */
    public static Image loadPicture(String resourcePath) {
        InputStream stream = DialogBox.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            return null;
        }
        return new Image(stream);
    }

    /**
     * Returns a dialog box for something the user typed: a right-aligned
     * bubble with no picture, since the user already knows who they are.
     *
     * @param message what the user typed.
     * @return a right-aligned dialog box without a picture.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.getChildren().remove(dialogBox.displayPicture);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-label");

        // A Label reports the width of its longest line as the width it needs,
        // and that demand travels up to the window, which grows to meet it.
        // Capping the width forces the text to wrap instead, so a long line
        // never widens the window.
        dialogBox.dialog.maxWidthProperty().bind(dialogBox.widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
        return dialogBox;
    }

    /**
     * Returns a dialog box for something the chatbot said.
     *
     * @param message the chatbot's reply.
     * @param picture Myriad's display picture, or null if none is available.
     * @return a dialog box with the picture on the left.
     */
    public static DialogBox getMyriadDialog(String message, Image picture) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.showPicture(picture);

        // Capped for the same reason as in getUserDialog, but a reply may use
        // all the room the picture leaves, since replies are often long lists.
        dialogBox.dialog.maxWidthProperty().bind(dialogBox.widthProperty().subtract(NON_TEXT_WIDTH));
        return dialogBox;
    }

    /**
     * Returns a dialog box for an error the chatbot reports, laid out like
     * any other reply but restyled so that a rejected command stands out
     * from the answers around it.
     *
     * @param message the error message.
     * @param picture Myriad's display picture, or null if none is available.
     * @return a dialog box with the picture on the left and the error style applied.
     */
    public static DialogBox getErrorDialog(String message, Image picture) {
        DialogBox dialogBox = getMyriadDialog(message, picture);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }

    /**
     * Returns a dialog box for a message that carries a warning, such as a
     * greeting that reports saved data could not be loaded. It is styled
     * apart from both ordinary replies and errors, because nothing the user
     * typed was wrong, yet the message still needs to be noticed.
     *
     * @param message the message containing the warning.
     * @param picture Myriad's display picture, or null if none is available.
     * @return a dialog box with the picture on the left and the warning style applied.
     */
    public static DialogBox getWarningDialog(String message, Image picture) {
        DialogBox dialogBox = getMyriadDialog(message, picture);
        dialogBox.dialog.getStyleClass().add("warning-label");
        return dialogBox;
    }
}
