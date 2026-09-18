package chud.gui;

import java.io.InputStream;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One entry in the chat log. The user's own input and Chud's replies are deliberately styled
 * differently rather than as two sides of the same bubble -- this is a person typing commands at
 * an app, not two people chatting, so the user's line reads as a quiet, compact "echo" of what
 * was typed (right-aligned, muted, narrower, no avatar -- you already know who you are), while
 * Chud's reply is the actual content people come here to read: left-aligned, full-weight, shown
 * next to Chud's avatar, and free to use most of the window's width, since a `list`/`find` reply
 * can be several lines long. An error reply gets its own style on top of Chud's, so a bad command
 * stands out at a glance instead of blending into normal output.
 */
public class DialogBox extends HBox {
    private static final double USER_WIDTH_FRACTION = 0.6;
    private static final double CHUD_WIDTH_FRACTION = 0.78;
    private static final double AVATAR_SIZE = 32;

    // null if the avatar asset is missing or fails to load -- createAvatar() then skips adding
    // an avatar entirely rather than the whole GUI crashing at class-load time over a cosmetic
    // resource, e.g. if the packaged asset ever goes missing or gets corrupted.
    private static final Image CHUD_AVATAR = loadAvatar();

    private DialogBox(String message, String styleClass, Pos alignment,
            ReadOnlyDoubleProperty containerWidth, double widthFraction, boolean showAvatar) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.maxWidthProperty().bind(containerWidth.multiply(widthFraction));
        label.getStyleClass().add(styleClass);

        setAlignment(alignment);
        setSpacing(8);
        ImageView avatar = showAvatar ? createAvatar() : null;
        if (avatar != null) {
            getChildren().addAll(avatar, label);
        } else {
            getChildren().add(label);
        }
    }

    /**
     * Loads Chud's avatar image, or returns null if the resource is missing or fails to load --
     * a broken/absent cosmetic asset shouldn't crash the whole GUI at class-load time.
     */
    private static Image loadAvatar() {
        try (InputStream stream = DialogBox.class.getResourceAsStream("/images/chud.png")) {
            return stream == null ? null : new Image(stream);
        } catch (Exception e) {
            return null;
        }
    }

    /** Returns a small, circularly-cropped ImageView of Chud's avatar, or null if it's unavailable. */
    private static ImageView createAvatar() {
        if (CHUD_AVATAR == null) {
            return null;
        }
        ImageView avatar = new ImageView(CHUD_AVATAR);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(false);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        avatar.getStyleClass().add("avatar");
        return avatar;
    }

    /**
     * Creates a right-aligned, compact echo of something the user typed. Its width tracks
     * {@code containerWidth} (the dialog column's width) so it stays sensibly sized as the
     * window is resized.
     */
    public static DialogBox getUserDialog(String message, ReadOnlyDoubleProperty containerWidth) {
        return new DialogBox(message, "user-bubble", Pos.CENTER_RIGHT, containerWidth,
                USER_WIDTH_FRACTION, false);
    }

    /**
     * Creates a left-aligned bubble (with Chud's avatar) for one of Chud's replies, styled as an
     * error (distinct color/border) if {@code isError} is true. Its width tracks
     * {@code containerWidth} the same way {@link #getUserDialog} does, but is allowed to grow
     * wider, since replies (e.g. a task list) tend to need more room than a typed command does.
     */
    public static DialogBox getChudDialog(String message, boolean isError, ReadOnlyDoubleProperty containerWidth) {
        String styleClass = isError ? "error-bubble" : "chud-bubble";
        return new DialogBox(message, styleClass, Pos.CENTER_LEFT, containerWidth,
                CHUD_WIDTH_FRACTION, true);
    }
}
