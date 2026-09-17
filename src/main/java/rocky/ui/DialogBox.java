package rocky.ui;

import java.net.URL;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * One entry in the chat transcript. The two sides look different on purpose,
 * because the user is giving commands to an app rather than chatting with
 * another person: a command the user typed is echoed as a compact,
 * terminal-style line on the right beside a small picture of Ryland Grace,
 * while Rocky's reply is a wide amber card beside his larger picture, which
 * carries most of the information. A reply reporting an error with the
 * command switches to a dark red alert card with a "!" badge instead, so the
 * mistake catches the user's attention.
 *
 * <p>Both sides adapt to the window size: Rocky's cards grow with the window
 * up to a comfortable reading width, and long commands use more of a wide
 * window before wrapping.
 */
public class DialogBox extends HBox {
    private static final double ROCKY_AVATAR_SIZE = 44;
    // The user's picture is smaller than Rocky's, so his replies still carry the visual weight.
    private static final double USER_AVATAR_SIZE = 28;
    // A command wraps at 300px, or at 60% of its row in a wider window.
    private static final double COMMAND_WRAP_WIDTH = 300;
    private static final double COMMAND_WIDTH_SHARE = 0.6;
    // Rocky's cards stop growing here, so lines stay short enough to read comfortably.
    private static final double MAX_CARD_WIDTH = 640;
    private static final String COMMAND_PROMPT = "> ";
    // A command looks like a line typed into the ship's console: cyan text on a faintly outlined navy panel.
    private static final String COMMAND_STYLE =
            "-fx-background-color: #101733; -fx-text-fill: #8FE0F0; -fx-background-radius: 12;"
                    + " -fx-border-color: #24365F; -fx-border-width: 1; -fx-border-radius: 12;"
                    + " -fx-padding: 4 10 4 10; -fx-font-family: 'Menlo', 'Consolas', monospace;";
    private static final String CARD_SHAPE_STYLE = " -fx-background-radius: 12; -fx-padding: 10 14 10 14;";
    private static final String REPLY_CARD_STYLE =
            "-fx-background-color: #E0A040; -fx-text-fill: #1A1208;" + CARD_SHAPE_STYLE;
    // Errors use a dark red alert card instead of amber, so a mistake catches the user's eye.
    private static final String ERROR_CARD_STYLE = "-fx-background-color: #3A1113; -fx-text-fill: #FFB4B4;"
            + " -fx-border-color: #E5484D; -fx-border-width: 2; -fx-border-radius: 12;" + CARD_SHAPE_STYLE;
    private static final String ERROR_BADGE_TEXT_STYLE = "-fx-text-fill: #FFFFFF; -fx-font-weight: bold;";
    private static final double ERROR_BADGE_RADIUS = 10;
    private static final Color ERROR_COLOR = Color.web("#E5484D");
    private static final Color ROCKY_COLOR = Color.web("#E0A040");
    private static final Color USER_COLOR = Color.web("#2B5DAA");
    // A soft halo around each picture, like a light source seen in the dark.
    private static final double AVATAR_GLOW_RADIUS = 8;
    private static final Color ROCKY_GLOW_COLOR = Color.web("#E0A040", 0.4);
    private static final Color USER_GLOW_COLOR = Color.web("#8FE0F0", 0.35);

    /** Loaded once and shared by every dialog box, instead of being re-read for each message. */
    private static final Image ROCKY_AVATAR = loadImage("/images/Rocky.png");
    private static final Image USER_AVATAR = loadImage("/images/RylandGrace.png");

    private DialogBox(Pos alignment, Node... children) {
        setAlignment(alignment);
        setSpacing(8);
        setPadding(new Insets(4, 8, 4, 8));
        getChildren().addAll(children);
    }

    /**
     * Returns a dialog box echoing the command the user typed, e.g. "> mark 2",
     * with a small picture of Ryland Grace after it.
     *
     * @param text the command the user entered.
     */
    public static DialogBox getUserDialog(String text) {
        Label command = new Label(COMMAND_PROMPT + text);
        command.setWrapText(true);
        command.setStyle(COMMAND_STYLE);
        DialogBox dialog = new DialogBox(Pos.CENTER_RIGHT, command,
                createAvatar(USER_AVATAR, USER_COLOR, USER_GLOW_COLOR, USER_AVATAR_SIZE));

        // Bound to the row's width, so the wrapping updates live as the window is resized.
        command.maxWidthProperty().bind(
                Bindings.max(dialog.widthProperty().multiply(COMMAND_WIDTH_SHARE), COMMAND_WRAP_WIDTH));
        return dialog;
    }

    /**
     * Returns a dialog box showing one of Rocky's replies, with his picture.
     *
     * @param text the reply to display, which may span several lines.
     */
    public static DialogBox getRockyDialog(String text) {
        return createRockyDialog(createCard(text, REPLY_CARD_STYLE));
    }

    /**
     * Returns a dialog box showing a reply that explains what was wrong with
     * the user's command, as a dark red alert card with a "!" badge, so the
     * mistake catches the user's attention.
     *
     * @param text the error message to display.
     */
    public static DialogBox getRockyErrorDialog(String text) {
        Label card = createCard(text, ERROR_CARD_STYLE);
        card.setGraphic(createErrorBadge());
        card.setGraphicTextGap(10);
        return createRockyDialog(card);
    }

    /**
     * Returns a card holding the text, which stretches across the rest of its
     * row as the window grows, up to a comfortable reading width.
     */
    private static Label createCard(String text, String cardStyle) {
        Label card = new Label(text);
        card.setWrapText(true);
        card.setMaxWidth(MAX_CARD_WIDTH);
        card.setStyle(cardStyle);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    /** Returns a dialog box with Rocky's picture beside the given card. */
    private static DialogBox createRockyDialog(Label card) {
        Node avatar = createAvatar(ROCKY_AVATAR, ROCKY_COLOR, ROCKY_GLOW_COLOR, ROCKY_AVATAR_SIZE);
        return new DialogBox(Pos.TOP_LEFT, avatar, card);
    }

    /** Returns a round red "!" badge, the familiar sign that something went wrong. */
    private static Node createErrorBadge() {
        Label mark = new Label("!");
        mark.setStyle(ERROR_BADGE_TEXT_STYLE);
        return new StackPane(new Circle(ERROR_BADGE_RADIUS, ERROR_COLOR), mark);
    }

    /**
     * Loads an image bundled from src/main/resources, e.g. "/images/Rocky.png".
     *
     * @return the image, or null if it isn't there, so the chat still works without it.
     */
    private static Image loadImage(String resourcePath) {
        URL url = DialogBox.class.getResource(resourcePath);
        return (url == null) ? null : new Image(url.toExternalForm());
    }

    /**
     * Returns a round profile picture cut from a square at the top middle of
     * the image, or a plain colored circle if the image couldn't be loaded.
     * The square starts at the top because in a portrait the face is usually
     * near the top; in a wide image the square covers the full height anyway.
     * Either way the picture is given a soft halo in the given glow color, so
     * it stands out against the dark sky behind the transcript.
     */
    private static Node createAvatar(Image image, Color placeholderColor, Color glowColor, double size) {
        double radius = size / 2;
        DropShadow glow = new DropShadow(AVATAR_GLOW_RADIUS, glowColor);
        if (image == null || image.isError()) {
            Circle placeholder = new Circle(radius, placeholderColor);
            placeholder.setEffect(glow);
            return placeholder;
        }

        double side = Math.min(image.getWidth(), image.getHeight());
        ImageView avatar = new ImageView(image);
        avatar.setViewport(new Rectangle2D((image.getWidth() - side) / 2, 0, side, side));
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(radius, radius, radius));
        avatar.setEffect(glow);
        return avatar;
    }
}
