package dingleberry;

import java.io.IOException;
import java.io.InputStream;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents a single chat row. Each row is rendered according to an
 * explicit {@link DialogType} rather than by styling based on message
 * text. User messages are compact, right-aligned bubbles with no avatar.
 * Every other message type (success, information, and error replies)
 * shares one structural layout: a small mascot or warning icon, a narrow
 * vertical accent bar, and a rounded card with a bold heading above its
 * body text. Only the accent color, icon, and card background differ
 * between calm normal replies and a visually urgent error.
 */
public final class DialogBox extends HBox {
    /** Distinguishes the visual treatment applied to a dialog row. */
    private enum DialogType {
        /** A compact, right-aligned bubble with no avatar. */
        USER,
        /** A calm cyan-accented card confirming a task change. */
        SUCCESS,
        /** A calm lavender-accented card for a neutral, informational
         *  reply. */
        INFORMATION,
        /** An urgent pink-accented card for an invalid command or a
         *  failure. */
        ERROR
    }

    /** Location of the cropped mascot artwork shown beside normal cards. */
    private static final String MASCOT_IMAGE_PATH =
            "/dingleberry/dingleberry_mascot_crop.png";
    /** Compact marker shown beside error cards. */
    private static final String WARNING_ICON_TEXT = "\u26A0";
    /** Widest a user bubble may grow before wrapping, as a row fraction. */
    private static final double USER_MAX_WIDTH_RATIO = 0.55;
    /** Widest a message card may grow before wrapping, as a row fraction. */
    private static final double CARD_MAX_WIDTH_RATIO = 0.88;
    /** Diameter of the icon shown beside every non-user row. */
    private static final int ICON_SIZE = 28;
    /** Width of the vertical accent bar beside a message card. */
    private static final int ACCENT_WIDTH = 4;
    /** Horizontal gap between an icon and its message card. */
    private static final int ROW_SPACING = 8;
    /** Vertical gap between a card's heading and its body text. */
    private static final int CARD_SPACING = 4;
    /** Mascot artwork shared by every success and information row's icon. */
    private static final Image MASCOT_IMAGE = loadMascotImage();

    private DialogBox(final DialogType type, final String heading,
                      final String text) {
        this.getStyleClass().add("dialog-box");
        this.setSpacing(ROW_SPACING);

        if (type == DialogType.USER) {
            final Label bubble = new Label(text);
            bubble.getStyleClass().addAll("bubble", "user-bubble");
            bubble.setWrapText(true);
            // Cap the bubble at a fraction of this row's width, which
            // itself stretches to match the conversation pane, so it wraps
            // instead of overflowing as the window is resized, without
            // forcing short messages to stretch and fill the extra space.
            bubble.maxWidthProperty().bind(
                    this.widthProperty().multiply(USER_MAX_WIDTH_RATIO));
            this.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().add(bubble);
            return;
        }

        final HBox cardRow = createCardRow(
                type, heading, text, this.widthProperty());
        this.setAlignment(Pos.TOP_LEFT);
        this.getChildren().addAll(createIcon(type), cardRow);
    }

    /**
     * Loads the mascot image or reports a missing or unreadable resource.
     *
     * @return the loaded mascot image.
     */
    private static Image loadMascotImage() {
        try (InputStream mascotStream = DialogBox.class.getResourceAsStream(
                MASCOT_IMAGE_PATH)) {
            if (mascotStream == null) {
                throw new IllegalStateException(
                        "Missing GUI resource: " + MASCOT_IMAGE_PATH);
            }
            return new Image(mascotStream);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not read GUI resource: " + MASCOT_IMAGE_PATH, e);
        }
    }

    /**
     * Builds the accent-bar-and-card row shared by every non-user message,
     * keeping the heading and body as separate labels instead of one
     * combined, sub-string-styled string.
     *
     * @param type the dialog type, which selects the accent and card
     *     color.
     * @param heading a short description of the message.
     * @param body the message content shown below the heading.
     * @param rowWidth the width of the enclosing dialog row, used to cap
     *     the card so it wraps responsively instead of overflowing.
     * @return the assembled accent-and-card row.
     */
    private static HBox createCardRow(final DialogType type,
                                      final String heading,
                                      final String body,
                                      final ReadOnlyDoubleProperty rowWidth) {
        final String variantStyleClass = variantStyleClass(type);

        final Label headingLabel = new Label(heading);
        headingLabel.getStyleClass().add("message-heading");
        headingLabel.setWrapText(true);

        final Label bodyLabel = new Label(body);
        bodyLabel.getStyleClass().add("message-body");
        bodyLabel.setWrapText(true);

        final VBox card = new VBox(CARD_SPACING, headingLabel, bodyLabel);
        card.getStyleClass().addAll("message-card", variantStyleClass);
        // Cap the card at a fraction of the row's width, which itself
        // stretches to match the conversation pane, so it wraps instead of
        // overflowing as the window is resized, without forcing short
        // messages to stretch and fill the extra space.
        card.maxWidthProperty().bind(
                rowWidth.multiply(CARD_MAX_WIDTH_RATIO));

        final Region accent = new Region();
        accent.getStyleClass().addAll("message-accent", variantStyleClass);
        accent.setMinWidth(ACCENT_WIDTH);
        accent.setMaxWidth(ACCENT_WIDTH);
        // HBox stretches both children to the row's height by default
        // (fillHeight), which already matches the accent to the card's
        // natural height; binding it to card.heightProperty() instead
        // created a circular height feedback loop that made the whole
        // card balloon to fill the conversation pane.

        return new HBox(accent, card);
    }

    /**
     * Maps a dialog type to the style class that colors its card and
     * accent bar.
     *
     * @param type the dialog type to look up.
     * @return the variant's style class name.
     */
    private static String variantStyleClass(final DialogType type) {
        switch (type) {
        case SUCCESS:
            return "success-message";
        case INFORMATION:
            return "information-message";
        default:
            return "error-message";
        }
    }

    /**
     * Creates the icon shown beside a non-user row: a warning marker for
     * errors, or the mascot avatar for calmer replies.
     *
     * @param type the dialog type to render an icon for.
     * @return the assembled icon node.
     */
    private static Node createIcon(final DialogType type) {
        return type == DialogType.ERROR
                ? createWarningIcon() : createMascotIcon();
    }

    /**
     * Creates the small circular mascot avatar shown beside success and
     * information cards.
     *
     * @return the assembled avatar image view.
     */
    private static ImageView createMascotIcon() {
        final ImageView avatar = new ImageView(MASCOT_IMAGE);
        avatar.setFitWidth(ICON_SIZE);
        avatar.setFitHeight(ICON_SIZE);
        avatar.setPreserveRatio(true);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(
                ICON_SIZE / 2.0, ICON_SIZE / 2.0, ICON_SIZE / 2.0));
        avatar.getStyleClass().add("bot-avatar");
        avatar.setAccessibleText("Dingleberry mascot");
        return avatar;
    }

    /**
     * Creates the compact warning marker shown beside error cards.
     *
     * @return the assembled warning marker label.
     */
    private static Label createWarningIcon() {
        final Label icon = new Label(WARNING_ICON_TEXT);
        icon.getStyleClass().add("warning-icon");
        icon.setMinSize(ICON_SIZE, ICON_SIZE);
        icon.setMaxSize(ICON_SIZE, ICON_SIZE);
        icon.setAlignment(Pos.CENTER);
        icon.setAccessibleText("Warning");
        return icon;
    }

    /**
     * Creates a compact, right-aligned dialog row representing the user's
     * message.
     *
     * @param text the message the user typed.
     * @return the assembled dialog row.
     */
    public static DialogBox getUserDialog(final String text) {
        return new DialogBox(DialogType.USER, null, text);
    }

    /**
     * Creates a calm, cyan-accented card confirming that a task was added,
     * deleted, marked, or unmarked.
     *
     * @param heading a short description of the change, such as
        *     "Taskie added! 🎉".
     * @param body the confirmation details shown below the heading.
     * @return the assembled dialog row.
     */
    public static DialogBox getSuccessDialog(final String heading,
                                             final String body) {
        return new DialogBox(DialogType.SUCCESS, heading, body);
    }

    /**
     * Creates a calm, lavender-accented card for a neutral reply, such as
     * a task list or the welcome message.
     *
     * @param heading a short description of the reply, such as
        *     "Your taskies 📋".
     * @param body the reply content shown below the heading.
     * @return the assembled dialog row.
     */
    public static DialogBox getInformationDialog(final String heading,
                                                 final String body) {
        return new DialogBox(DialogType.INFORMATION, heading, body);
    }

    /**
     * Creates a dedicated error dialog row with a warning marker, a short
     * heading, and the underlying detail message, distinct from an
     * ordinary reply.
     *
     * @param heading a short description of the error, such as
        *     "Dingleberry is confused! 🤔".
     * @param message the detail message explaining what went wrong.
     * @return the assembled dialog row.
     */
    public static DialogBox getErrorDialog(final String heading,
                                           final String message) {
        return new DialogBox(DialogType.ERROR, heading, message);
    }
}

