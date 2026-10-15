package jeff.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One row of the conversation transcript: a circular profile picture next to a
 * speech-bubble-styled {@link Label}. The three factory methods below control
 * whose row it is (the user's typed command, or one of Jeff's replies) and,
 * for Jeff's replies, whether the reply reports an error, which is styled
 * differently (see {@code error-label} in {@code css/dialog.css}) so mistakes
 * catch the user's eye instead of blending into the rest of the transcript.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36;
    private static final double SPACING = 10;
    private static final double MAX_BUBBLE_WIDTH = 320;

    private DialogBox(String text, Image image, String bubbleStyleClass, boolean userOnRight) {
        Label bubble = new Label(text);
        bubble.getStyleClass().add(bubbleStyleClass);
        bubble.setWrapText(true);
        bubble.setMaxWidth(MAX_BUBBLE_WIDTH);

        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        setSpacing(SPACING);
        setPadding(new Insets(4, 8, 4, 8));
        if (userOnRight) {
            setAlignment(Pos.CENTER_RIGHT);
            getChildren().addAll(bubble, avatar);
        } else {
            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(avatar, bubble);
        }
    }

    /** Builds the row for a command the user typed, right-aligned with the user's profile picture. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, "user-label", true);
    }

    /** Builds the row for one of Jeff's ordinary replies, left-aligned with the robot profile picture. */
    public static DialogBox getReplyDialog(String text, Image image) {
        return new DialogBox(text, image, "reply-label", false);
    }

    /** Builds the row for a reply that reports an error, styled to stand out from ordinary replies. */
    public static DialogBox getErrorDialog(String text, Image image) {
        return new DialogBox(text, image, "error-label", false);
    }
}
