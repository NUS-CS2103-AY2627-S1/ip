package yapbot.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A single row in the GUI's conversation view: a label holding either the
 * user's input or YapBot's response, aligned to one side of the window
 * depending on who "said" it. YapBot's rows also carry an avatar icon.
 */
public class DialogBox extends HBox {

    private static final double WRAP_WIDTH = 300.0;
    private static final double ICON_SIZE = 80.0;

    /**
     * YapBot's usual avatar, shown next to every response. Package-visible
     * (rather than private) so {@code MainWindowTestFX} can identify which
     * icon a dialog row ended up showing.
     */
    static final Image DEFAULT_ICON = loadIcon("/images/bot-icon.png");

    /**
     * Shown instead of {@link #DEFAULT_ICON} on any rejected/invalid input,
     * via {@link #forYapBotError(String)}.
     */
    static final Image CONFUSED_ICON = loadIcon("/images/confused-icon.png");

    private DialogBox(String text, Pos alignment, String bubbleStyleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(WRAP_WIDTH);
        label.getStyleClass().addAll("chat-bubble", bubbleStyleClass);

        setAlignment(alignment);
        setSpacing(8.0);
        getChildren().add(label);
    }

    /**
     * Creates a dialog row for text the user typed, right-aligned and styled
     * as a "user" bubble (maroon background). Carries no avatar icon.
     *
     * @param text the user's input.
     * @return the dialog row to add to the conversation view.
     */
    public static DialogBox forUser(String text) {
        return new DialogBox(text, Pos.CENTER_RIGHT, "user-bubble");
    }

    /**
     * Creates a dialog row for a successful YapBot response, left-aligned
     * and styled as a "bot" bubble (cream background), with the default
     * avatar icon.
     *
     * @param text YapBot's response text.
     * @return the dialog row to add to the conversation view.
     */
    public static DialogBox forYapBot(String text) {
        return forYapBot(text, DEFAULT_ICON);
    }

    /**
     * Creates a dialog row for a YapBot response that rejected the user's
     * input (an unrecognized command, or any other validation error), styled
     * the same way as {@link #forYapBot(String)} but with the confused icon
     * instead of the default one.
     *
     * @param text YapBot's response text.
     * @return the dialog row to add to the conversation view.
     */
    public static DialogBox forYapBotError(String text) {
        return forYapBot(text, CONFUSED_ICON);
    }

    private static DialogBox forYapBot(String text, Image icon) {
        DialogBox dialogBox = new DialogBox(text, Pos.CENTER_LEFT, "bot-bubble");

        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(ICON_SIZE);
        imageView.setFitHeight(ICON_SIZE);
        imageView.setPreserveRatio(true);
        dialogBox.getChildren().add(0, imageView);

        return dialogBox;
    }

    /**
     * Loads an icon image bundled as a resource.
     *
     * @param resourcePath the classpath-relative resource path, e.g. {@code "/images/bot-icon.png"}.
     * @return the loaded image.
     */
    private static Image loadIcon(String resourcePath) {
        return new Image(DialogBox.class.getResourceAsStream(resourcePath));
    }
}
