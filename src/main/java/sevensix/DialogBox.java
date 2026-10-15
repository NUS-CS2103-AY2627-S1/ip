package sevensix;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Displays one message in the SevenSix conversation.
 *
 * <p>The conversation is between a person and an app, so the two sides look different: the
 * user's short commands appear as compact colored bubbles on the right, while the chatbot's
 * longer replies appear as plain cards on the left, marked by a small round avatar.
 */
public class DialogBox extends HBox {
    /** Share of the conversation width a user bubble may use, keeping it visibly on one side. */
    private static final double USER_WIDTH_FRACTION = 0.8;
    /** Share of the conversation width a chatbot reply may use; replies can be long. */
    private static final double REPLY_WIDTH_FRACTION = 0.86;
    /** Radius of the chatbot avatar; kept small so it takes little space from the text. */
    private static final double AVATAR_RADIUS = 14.0;

    /** The label containing the message text. */
    private final Label messageLabel;

    /**
     * Creates a message row whose look is defined by a stylesheet class.
     *
     * <p>The message width follows the row width, so text rewraps when the window is resized.
     *
     * @param message the message text to display.
     * @param styleClass the stylesheet class that gives the message its look.
     * @param alignment the side of the conversation the message sits on.
     * @param widthFraction the largest share of the row width the message may use.
     */
    private DialogBox(String message, String styleClass, Pos alignment, double widthFraction) {
        messageLabel = createMessageLabel(message, styleClass, widthFraction);
        configureRow(alignment);
    }

    /** Creates a wrapping label whose width follows this message row.
     *
     * @param message the displayed text.
     * @param styleClass the message variant style.
     * @param widthFraction the maximum share of the row width.
     * @return the configured message label.
     */
    private Label createMessageLabel(String message, String styleClass, double widthFraction) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.maxWidthProperty().bind(widthProperty().multiply(widthFraction));
        // A wrapped label must grow taller instead of truncating its text.
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().addAll("message", styleClass);
        return label;
    }

    /** Positions the message on its side of the conversation.
     *
     * @param alignment the side used by this message.
     */
    private void configureRow(Pos alignment) {
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(alignment);
        getStyleClass().add("dialog-row");
        getChildren().add(messageLabel);
    }

    /**
     * Creates a right-aligned bubble for a command the user entered.
     *
     * <p>User messages have no avatar, because their color and side already identify them.
     *
     * @param message the command text.
     * @return the user's message row.
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, "user-message", Pos.TOP_RIGHT, USER_WIDTH_FRACTION);
    }

    /**
     * Creates a left-aligned card for a chatbot reply.
     *
     * @param message the reply text.
     * @return the chatbot's message row.
     */
    public static DialogBox getBotDialog(String message) {
        return new DialogBox(message, "bot-message", Pos.TOP_LEFT, REPLY_WIDTH_FRACTION).withAvatar();
    }

    /**
     * Creates a left-aligned, highlighted card for a chatbot reply that reports an error.
     *
     * @param message the error reply text.
     * @return the chatbot's error message row.
     */
    public static DialogBox getErrorDialog(String message) {
        return new DialogBox(message, "error-message", Pos.TOP_LEFT, REPLY_WIDTH_FRACTION).withAvatar();
    }

    /**
     * Places the chatbot's avatar in front of the message.
     *
     * @return this message row, so that the call can be chained.
     */
    private DialogBox withAvatar() {
        getChildren().addFirst(createAvatar());
        return this;
    }

    /**
     * Creates a small round badge showing the chatbot's initials.
     *
     * <p>The badge is drawn with shapes rather than loaded from an image file, so it scales
     * cleanly and has no background that could clash with the window.
     *
     * @return the avatar node.
     */
    private static StackPane createAvatar() {
        Circle circle = new Circle(AVATAR_RADIUS);
        circle.getStyleClass().add("avatar-circle");
        Label initials = new Label("76");
        initials.getStyleClass().add("avatar-text");
        StackPane avatar = new StackPane(circle, initials);
        avatar.setMinWidth(Region.USE_PREF_SIZE);
        // Keep the badge its own size so it lines up with the first line of the reply.
        avatar.setMaxHeight(Region.USE_PREF_SIZE);
        return avatar;
    }
}
