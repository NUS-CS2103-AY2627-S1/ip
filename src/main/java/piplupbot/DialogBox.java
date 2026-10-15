package piplupbot;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents one message in the window's conversation: the words in a speech
 * bubble, beside a round picture of whoever said them.
 *
 * <p>The user's messages sit on the right with the picture last, as in most chat
 * apps, and the bot's sit on the left with the picture first. That is the only
 * difference in how they are built, so one class serves both. Its constructor
 * is private, and the three factory methods -- {@link #getUserDialog},
 * {@link #getBotDialog} and {@link #getErrorDialog} -- say which kind of message
 * is wanted, which reads better at the call than a {@code true} or
 * {@code false} would.</p>
 *
 * <p>What a message is made of is described in the layout file
 * {@code view/DialogBox.fxml}, laid out as one of the user's; for the bot's,
 * this class swaps the two parts round. How it looks -- the bubble's color, its
 * rounded corners, the gaps around it -- is decided by the window's stylesheet,
 * {@code main.css}, which finds each part by its style class: {@code dialog-box}
 * for the whole message and {@code bubble} for the words, both given in the
 * layout file, plus {@code user}, {@code bot} or {@code error}, given here, for
 * whose message it is.</p>
 *
 * <p>It is an {@link HBox} -- a row of nodes, laid out left to right -- so the
 * window can add it to the conversation like any other node.</p>
 */
public class DialogBox extends HBox {
    /** Where the layout file for one message is, among the program's own files. */
    private static final String DIALOG_BOX_PATH = "/view/DialogBox.fxml";

    /**
     * How wide a bubble may grow, as a fraction of the width of its row in the
     * conversation, which is as wide as the window. Past it, the words wrap.
     */
    private static final double MAX_BUBBLE_WIDTH_FRACTION = 0.8;

    /** The speech bubble holding the words. */
    @FXML
    private Label bubble;

    /** The round picture of whoever said the words. */
    @FXML
    private ImageView pictureView;

    /**
     * Creates a message with the given words and picture, on the user's side of
     * the conversation or on the bot's.
     *
     * @param text       The words of the message.
     * @param picture    A picture of whoever said them.
     * @param isFromUser Whether the user said them, rather than the bot.
     */
    private DialogBox(String text, Image picture, boolean isFromUser) {
        FXMLLoader loader = new FXMLLoader(GuiUi.getResourceUrl(DIALOG_BOX_PATH));
        // The layout file fills in this object rather than making a new one, and
        // this object is also its controller, so the parts land in the fields above.
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            // The file comes with the program, so failing to read it is a bug in the
            // program rather than something the user could fix.
            throw new IllegalStateException("Cannot load " + DIALOG_BOX_PATH, e);
        }

        bubble.setText(text);
        // A binding keeps the limit up to date by itself: whenever the row's width
        // changes, as the window is resized, the bubble's maximum width follows.
        bubble.maxWidthProperty().bind(widthProperty().multiply(MAX_BUBBLE_WIDTH_FRACTION));
        pictureView.setImage(picture);
        if (!isFromUser) {
            // The layout file puts the words first, as on the user's side; the bot's
            // side is its mirror image.
            getChildren().setAll(pictureView, bubble);
            setAlignment(Pos.TOP_LEFT);
        }
    }

    /**
     * Returns a message from the user, for the right-hand side of the
     * conversation.
     *
     * @param text    What the user typed.
     * @param picture The user's picture.
     * @return The message, ready to add to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image picture) {
        DialogBox box = new DialogBox(text, picture, true);
        box.getStyleClass().add("user");
        return box;
    }

    /**
     * Returns a reply from the bot, for the left-hand side of the conversation.
     *
     * @param text    The reply, with a line break between its lines.
     * @param picture The bot's picture.
     * @return The reply, ready to add to the conversation.
     */
    public static DialogBox getBotDialog(String text, Image picture) {
        DialogBox box = new DialogBox(text, picture, false);
        box.getStyleClass().add("bot");
        return box;
    }

    /**
     * Returns the bot's explanation of an error. It sits where the bot's other
     * replies do, but the stylesheet gives it a color of its own, so that a
     * mistake stands out from an ordinary reply.
     *
     * @param text    The explanation, with a line break between its lines.
     * @param picture The bot's picture.
     * @return The explanation, ready to add to the conversation.
     */
    public static DialogBox getErrorDialog(String text, Image picture) {
        DialogBox box = new DialogBox(text, picture, false);
        box.getStyleClass().add("error");
        return box;
    }
}
