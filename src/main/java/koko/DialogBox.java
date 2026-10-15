package koko;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box in the Koko user interface.
 */
public class DialogBox extends HBox {
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.68;
    private static final double KOKO_MESSAGE_WIDTH_RATIO = 0.82;
    private static final double AVATAR_SIZE = 36;
    private static final Image KOKO_AVATAR = new Image(
            DialogBox.class.getResource("/images/koko-avatar.png").toExternalForm());

    private DialogBox(String text, String style, double messageWidthRatio, boolean showsAvatar) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.maxWidthProperty().bind(this.widthProperty().multiply(messageWidthRatio));
        label.setStyle(style);

        if (showsAvatar) {
            this.getChildren().add(createKokoAvatar());
        }

        this.getChildren().add(label);
        this.setFillHeight(false);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setSpacing(showsAvatar ? 8 : 0);
        HBox.setHgrow(label, Priority.NEVER);
    }

    /**
     * Creates Koko's compact, circular profile picture.
     *
     * @return the avatar displayed beside Koko's messages
     */
    private ImageView createKokoAvatar() {
        ImageView avatar = new ImageView(KOKO_AVATAR);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        return avatar;
    }

    /**
     * Creates a compact, right-aligned message bubble for the user's command.
     *
     * @param text the command entered by the user
     * @return a dialog box that displays the user's command
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(
                text,
                "-fx-background-color: #f97316;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-padding: 8 12 8 12;"
                        + "-fx-background-radius: 14 14 3 14;",
                USER_MESSAGE_WIDTH_RATIO,
                false);

        dialogBox.setAlignment(Pos.CENTER_RIGHT);
        return dialogBox;
    }

    /**
     * Creates a left-aligned message card for Koko's normal response.
     *
     * @param text the response from Koko
     * @return a dialog box that displays Koko's response
     */
    public static DialogBox getKokoDialog(String text) {
        DialogBox dialogBox = new DialogBox(
                text,
                "-fx-background-color: #eff8ff;"
                        + "-fx-text-fill: #164e63;"
                        + "-fx-font-size: 13px;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-background-radius: 3 14 14 14;"
                        + "-fx-border-color: #bae6fd;"
                        + "-fx-border-radius: 3 14 14 14;",
                KOKO_MESSAGE_WIDTH_RATIO,
                true);

        dialogBox.setAlignment(Pos.CENTER_LEFT);
        return dialogBox;
    }

    /**
     * Creates a high-contrast, left-aligned message card for an invalid command.
     *
     * @param text the explanation of the invalid command
     * @return a dialog box that displays an error response
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(
                "Koko squawks: command needs attention\n" + text,
                "-fx-background-color: #fef2f2;"
                        + "-fx-text-fill: #991b1b;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-background-radius: 3 14 14 14;"
                        + "-fx-border-color: #fb7185;"
                        + "-fx-border-width: 0 0 0 4;"
                        + "-fx-border-radius: 3 14 14 14;",
                KOKO_MESSAGE_WIDTH_RATIO,
                true);

        dialogBox.setAlignment(Pos.CENTER_LEFT);
        return dialogBox;
    }
}
