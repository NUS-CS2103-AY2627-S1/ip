package shannon.gui;

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
 * One turn in the conversation: a small round avatar beside what was said.
 * <p>
 * The layout comes from {@code /view/DialogBox.fxml}, which uses the {@code fx:root} construct.
 * That is what allows this class to <em>be</em> the {@link HBox} the FXML describes rather than
 * merely contain one, so a dialog box can be dropped straight into the conversation.
 * <p>
 * The two speakers deliberately do <em>not</em> look alike. This is a conversation between a
 * person and a program, not between two people, so the usual symmetrical pair of chat bubbles
 * would be misleading as well as wasteful: Shannon's replies are the long ones and are left as
 * plain text on the background, while the user's short commands get a filled bubble on the
 * right. Which of the three looks a box wears is decided entirely by the CSS class its label is
 * given here; see {@code /css/main.css}.
 * <p>
 * The constructor is private; callers use the named factory methods instead. Naming the cases
 * makes it impossible to forget to flip Shannon's box, and reads better at the call site than a
 * boolean argument would.
 */
public class DialogBox extends HBox {

    /** The width and height of the avatar, in pixels. Also the diameter of its circular clip. */
    private static final double AVATAR_SIZE = 26.0;

    /** How much of the box's width the user's bubble may take before it wraps. */
    private static final double USER_TEXT_WIDTH_FRACTION = 0.72;

    /** How much of the box's width Shannon's text may take. Larger: her replies are longer. */
    private static final double SHANNON_TEXT_WIDTH_FRACTION = 0.88;

    /** The CSS class that draws a filled, right-aligned bubble for the user. */
    private static final String STYLE_CLASS_USER = "user-bubble";

    /** The CSS class that draws Shannon's plain text with a thin accent rule. */
    private static final String STYLE_CLASS_SHANNON = "shannon-text";

    /** The CSS class that draws an error: red text on a tinted panel. */
    private static final String STYLE_CLASS_ERROR = "error-text";

    /** What was said. Filled in by the FXML loader, matched by the {@code fx:id} in the file. */
    @FXML
    private Label dialog;

    /** The speaker's avatar. Also filled in by the FXML loader. */
    @FXML
    private ImageView displayPicture;

    /**
     * Builds one dialog box from the FXML layout, with its avatar cropped to a circle.
     *
     * @param text what the speaker said.
     * @param image the speaker's avatar.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            // setRoot before load, so the loader fills in this object instead of making a new
            // HBox; setController so the @FXML fields above are the ones it injects into.
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // A missing or malformed FXML file is a packaging mistake, not something the user
            // can act on, so it is reported to the console rather than shown in the window.
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        cropAvatarToCircle();
    }

    /**
     * Returns a box for something the user said: a filled bubble, avatar on the right.
     *
     * @param text what the user typed.
     * @param image the user's avatar.
     * @return the box, ready to be added to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.style(STYLE_CLASS_USER, USER_TEXT_WIDTH_FRACTION);
        return dialogBox;
    }

    /**
     * Returns a box for something Shannon said: plain text, avatar on the left.
     *
     * @param text Shannon's reply.
     * @param image Shannon's avatar.
     * @return the box, ready to be added to the conversation.
     */
    public static DialogBox getShannonDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.style(STYLE_CLASS_SHANNON, SHANNON_TEXT_WIDTH_FRACTION);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns a box for something that went wrong, such as a command Shannon did not understand.
     * <p>
     * Laid out exactly like an ordinary reply from Shannon, because that is what it is, but
     * colored so that it cannot be mistaken for a confirmation. A user who mistypes a command
     * and is answered in the same calm grey as a success is liable to carry on believing it
     * worked.
     *
     * @param text the explanation of what went wrong.
     * @param image Shannon's avatar.
     * @return the box, ready to be added to the conversation.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.style(STYLE_CLASS_ERROR, SHANNON_TEXT_WIDTH_FRACTION);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Gives the text its appearance and stops it from growing wider than its share of the box.
     * <p>
     * The width is <em>bound</em> to a fraction of the box rather than set to a number, so it is
     * recomputed whenever the window is resized; a fixed width would either overflow a narrow
     * window or waste a wide one. Once a maximum exists, {@code wrapText} in the FXML has
     * somewhere to wrap, and long text grows downwards instead of sideways.
     *
     * @param styleClass the CSS class that decides how this box looks.
     * @param widthFraction how much of the box's width the text may occupy, from 0 to 1.
     */
    private void style(String styleClass, double widthFraction) {
        assert widthFraction > 0 && widthFraction <= 1 : "A fraction of the width must be in (0, 1]";
        dialog.getStyleClass().add(styleClass);
        dialog.maxWidthProperty().bind(widthProperty().multiply(widthFraction));
    }

    /**
     * Crops the avatar to a circle, so the corners of the picture do not sit as squares on the
     * background. A clip is used rather than a pre-cut image file, so any picture can be dropped
     * into {@code /images} without being edited first.
     */
    private void cropAvatarToCircle() {
        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Swaps the picture and the text, so the picture ends up on the left.
     * The children are copied out, reversed, and put back, because an observable list cannot be
     * reordered while it is being read.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
