package atlas.gui;

import java.io.IOException;
import java.net.URL;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * One chat message: an avatar picture beside a wrapping text bubble, laid out
 * the way a messaging application does it. Atlas's messages carry Atlas's
 * picture on the left and align left with the reply-label styling; the user's
 * messages carry the user's picture on the right and align right. An error reply
 * adds the error-label styling on top of the reply styling so it stands apart
 * from an ordinary reply. The layout is defined in view/DialogBox.fxml.
 *
 * <p>Both avatars are square pictures shown as a circle, so a picture that is
 * not square would sit letterboxed inside the circle instead of being cropped.
 *
 * <p>The message is a {@link Text} node inside a {@link TextFlow}: unlike a
 * bounded {@link javafx.scene.control.Label}, wrapped text is never
 * ellipsized, so the full message is always shown. The TextFlow reports the
 * width of its longest line, so the bubble chrome hugs the message text
 * instead of spanning the full chat width.
 */
public class DialogBox extends HBox {

    /** Width budget lost to window margins, scrollbar and bubble padding. */
    private static final double WRAP_WIDTH_INSETS = 62.0;

    /** Extra width budget the avatar standing beside every bubble needs. */
    private static final double AVATAR_WIDTH_INSETS = 52.0;

    /** Fraction of the usable width long bubbles may grow to. */
    private static final double BUBBLE_WIDTH_RATIO = 0.62;

    /** Long bubbles never get narrower than this while the window allows. */
    private static final double MIN_BUBBLE_TEXT_WIDTH = 360.0;

    /** Smallest sensible wrap width; narrower windows still read fine. */
    private static final double MIN_WRAP_WIDTH = 140.0;

    /** Horizontal bubble padding, added to the cap for the bubble's own max. */
    private static final double BUBBLE_H_PADDING = 24.0;

    /** Style class that marks a bubble as an error report. */
    private static final String ERROR_STYLE_CLASS = "error-label";

    /** Picture shown beside Atlas's replies. */
    private static final String ATLAS_PICTURE_RESOURCE = "/images/atlas-pfp.png";

    /** Picture shown beside the user's own messages. */
    private static final String USER_PICTURE_RESOURCE = "/images/user-pfp.png";

    /** Atlas's picture, loaded once and shared by every row that shows it. */
    private static final Image ATLAS_PICTURE = loadPicture(ATLAS_PICTURE_RESOURCE);

    /** The user's picture, loaded once and shared by every row that shows it. */
    private static final Image USER_PICTURE = loadPicture(USER_PICTURE_RESOURCE);

    /** Avatar square beside the bubble; the picture is fitted and clipped to it. */
    @FXML
    private StackPane avatar;

    /** Avatar picture, shown inside the avatar square. */
    @FXML
    private ImageView avatarPicture;

    /** Bubble chrome: carries the background, radius, border and padding. */
    @FXML
    private StackPane bubble;

    /** Wraps the message text and lets the chrome hug its longest line. */
    @FXML
    private TextFlow flow;

    @FXML
    private Text dialog;

    /**
     * Builds this control from DialogBox.fxml and fills in the given message.
     *
     * @param text the message text to display.
     * @param isAtlasReply whether this bubble is Atlas's reply (left-aligned,
     *                     Atlas's picture on the left) or the user's message
     *                     (right-aligned, the user's picture on the right).
     * @param isError whether this bubble reports an error, in which case it
     *                also carries the error styling.
     */
    private DialogBox(String text, boolean isAtlasReply, boolean isError) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        avatarPicture.setImage(isAtlasReply ? ATLAS_PICTURE : USER_PICTURE);
        fitPictureToAvatar();
        bindWrapWidthToWindow();
        bubble.getStyleClass().add(isAtlasReply ? "reply-label" : "label");
        if (isError) {
            bubble.getStyleClass().add(ERROR_STYLE_CLASS);
        }
        if (isAtlasReply) {
            this.setAlignment(Pos.TOP_LEFT);
        } else {
            // The fxml declares the avatar first, which suits Atlas's side of the
            // conversation; the user's own picture belongs after the bubble.
            avatar.toFront();
        }
    }

    /**
     * Fits the picture to the avatar square and clips it to a circle, the way a
     * messaging application shows a profile picture. Both follow the size the
     * stylesheet declares for the square, so changing that size cannot leave the
     * picture or its clip behind.
     */
    private void fitPictureToAvatar() {
        avatarPicture.fitWidthProperty().bind(avatar.widthProperty());
        avatarPicture.fitHeightProperty().bind(avatar.heightProperty());
        Circle clip = new Circle();
        clip.centerXProperty().bind(avatar.widthProperty().divide(2));
        clip.centerYProperty().bind(avatar.heightProperty().divide(2));
        clip.radiusProperty().bind(avatar.widthProperty().divide(2));
        avatarPicture.setClip(clip);
    }

    /**
     * Sizes the message like a chat bubble at every window width. Short text
     * hugs its content; long text wraps at about 62% of the usable width (or
     * the full usable width when the window is narrow), so bubbles grow and
     * shrink with the window instead of keeping a fixed default width. The
     * avatar beside each bubble is subtracted from the budget first, so a long
     * reply cannot push its own avatar off the edge. The scene is not known
     * until this row joins the scene graph, so the bindings are installed on
     * first attachment. A TextFlow never ellipsizes, so the full message always
     * stays visible.
     */
    private void bindWrapWidthToWindow() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                DoubleBinding usable = newScene.widthProperty()
                        .subtract(WRAP_WIDTH_INSETS + AVATAR_WIDTH_INSETS);
                DoubleBinding cap = Bindings.max(MIN_BUBBLE_TEXT_WIDTH,
                        usable.multiply(BUBBLE_WIDTH_RATIO));
                DoubleBinding wrap = Bindings.max(MIN_WRAP_WIDTH, Bindings.min(usable, cap));
                flow.maxWidthProperty().bind(wrap);
                bubble.maxWidthProperty().bind(wrap.add(BUBBLE_H_PADDING));
            }
        });
    }

    /**
     * Reads an avatar picture from the application resources.
     *
     * @param resource path of the picture on the classpath.
     * @return the picture, ready to be shown.
     * @throws IllegalStateException if the picture is missing from the build,
     *                               which would otherwise show as a blank circle.
     */
    private static Image loadPicture(String resource) {
        URL url = DialogBox.class.getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Avatar picture missing from the build: " + resource);
        }
        return new Image(url.toExternalForm());
    }

    /**
     * Creates a dialog box for the user's message, aligned to the right with the
     * user's picture on the right.
     *
     * @param text the message text.
     * @return a dialog box aligned for the user.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, false, false);
    }

    /**
     * Creates a dialog box for Atlas's reply, aligned to the left with Atlas's
     * picture on the left.
     *
     * @param text the reply text.
     * @return a dialog box aligned for Atlas.
     */
    public static DialogBox getAtlasDialog(String text) {
        return new DialogBox(text, true, false);
    }

    /**
     * Creates a dialog box for an error Atlas reported, aligned to the left and
     * styled to stand apart from an ordinary reply.
     *
     * @param text the error message.
     * @return a dialog box styled as an error.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, true, true);
    }
}
