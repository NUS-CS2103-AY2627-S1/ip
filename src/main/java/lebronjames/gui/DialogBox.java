package lebronjames.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One message in the conversation.
 *
 * <p>The two sides are deliberately not drawn the same way, because the
 * conversation is not between two people: the user gives orders and the chatbot
 * answers them. A command is short and is shown as a narrow tinted bubble
 * pushed to the right with no picture at all, while a reply is long, is given
 * the full width on the left, and is the only side that carries a small
 * portrait. An error gets a third look again, so a mistyped command is obvious
 * without having to read the text.
 *
 * <p>This is a custom control, meaning it loads its own layout
 * ({@code /view/DialogBox.fxml}) and acts as that layout's controller. Doing it
 * this way lets {@link MainWindow} treat a whole message as a single node it can
 * drop into the conversation, instead of assembling a label and an image every
 * time someone speaks.
 *
 * <p>Instances are made through the three factory methods rather than a public
 * constructor, so a caller cannot produce a message that does not belong to one
 * of those three kinds.
 */
public class DialogBox extends HBox {
    /**
     * Diameter of the round speaker picture, in pixels.
     *
     * <p>The size is set from here rather than in the FXML so the picture and
     * the circle clipping it can never disagree: a clip smaller than the image
     * would slice its edges off, and a larger one would leave the corners
     * showing.
     *
     * <p>It is small on purpose. There are only ever two participants and one
     * of them never shows a picture, so a large portrait would identify the
     * speaker no better than a small one while pushing the text it belongs to
     * off the bottom of a short window.
     */
    private static final double PICTURE_SIZE = 32.0;

    /**
     * Width taken up by everything in a reply other than the bubble itself:
     * the portrait, the gap after it, and the padding on both sides.
     *
     * <p>Subtracting it from the row width is what stops a long reply from
     * being laid out wider than the window and clipped.
     */
    private static final double REPLY_MARGINS = PICTURE_SIZE + 8.0 + 10.0 + 10.0;

    /**
     * Share of the width a command may occupy.
     *
     * <p>Commands are short, and leaving a clear strip down the left makes the
     * right-hand column read as a separate voice from the replies.
     */
    private static final double COMMAND_WIDTH_SHARE = 0.75;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a message showing the given text.
     *
     * @param text What was said.
     * @param image Portrait to show beside the text, or null for no portrait.
     * @param bubbleStyle Style class deciding how the bubble is coloured.
     */
    private DialogBox(String text, Image image, String bubbleStyle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the dialog box layout.", exception);
        }

        dialog.setText(text);
        dialog.getStyleClass().add(bubbleStyle);

        if (image == null) {
            showAsCommand();
        } else {
            showAsReply(image);
        }
    }

    /**
     * Returns a message for something the user typed.
     *
     * @param text What the user typed.
     * @return Message ready to be added to the conversation.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null, "command-label");
    }

    /**
     * Returns a message for one of the chatbot's replies.
     *
     * @param text The chatbot's reply.
     * @param image Picture of the chatbot.
     * @return Message ready to be added to the conversation.
     */
    public static DialogBox getLebronDialog(String text, Image image) {
        return new DialogBox(text, image, "reply-label");
    }

    /**
     * Returns a message for a complaint about something the user typed, drawn
     * so it stands out from an ordinary reply.
     *
     * @param text Explanation of what was wrong.
     * @param image Picture of the chatbot.
     * @return Message ready to be added to the conversation.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        return new DialogBox(text, image, "error-label");
    }

    /**
     * Returns the largest square that fits in the middle of the given picture.
     *
     * <p>Showing a picture that is not square would otherwise go wrong twice
     * over: fitting it inside the avatar box letterboxes it, and the round clip
     * then keeps only a thin band across the middle of that. Cropping to a
     * square first means a photo of any shape fills the circle, and taking the
     * centre is what keeps the subject in frame.
     *
     * @param image Picture about to be shown.
     * @return Square region of the picture to display.
     */
    private static Rectangle2D squareCentreOf(Image image) {
        double sideLength = Math.min(image.getWidth(), image.getHeight());
        double leftOffset = (image.getWidth() - sideLength) / 2;
        double topOffset = (image.getHeight() - sideLength) / 2;
        return new Rectangle2D(leftOffset, topOffset, sideLength, sideLength);
    }

    /**
     * Lays this message out as a command: no portrait, pushed to the right, and
     * never wider than {@link #COMMAND_WIDTH_SHARE} of the window.
     */
    private void showAsCommand() {
        getChildren().remove(displayPicture);
        setAlignment(Pos.TOP_RIGHT);

        // Bound to the row rather than given a fixed width, so the bubble keeps
        // its proportions as the user resizes the window.
        dialog.maxWidthProperty().bind(widthProperty().multiply(COMMAND_WIDTH_SHARE));
    }

    /**
     * Lays this message out as a reply: a small round portrait on the left, and
     * the text filling whatever width is left.
     *
     * @param image Picture of the speaker.
     */
    private void showAsReply(Image image) {
        setAlignment(Pos.TOP_LEFT);

        displayPicture.setImage(image);
        displayPicture.setViewport(squareCentreOf(image));
        displayPicture.setFitWidth(PICTURE_SIZE);
        displayPicture.setFitHeight(PICTURE_SIZE);
        // A circular clip turns the square viewport into a round avatar, which
        // reads as a chat app without needing the images themselves to be round.
        displayPicture.setClip(new Circle(PICTURE_SIZE / 2, PICTURE_SIZE / 2, PICTURE_SIZE / 2));

        dialog.maxWidthProperty().bind(widthProperty().subtract(REPLY_MARGINS));
    }
}
