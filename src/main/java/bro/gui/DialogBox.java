package bro.gui;

import bro.Personality;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/** Displays one chat message with a compact avatar identifying its speaker. */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 20;
    private static final double DIALOG_MAX_WIDTH = 330;

    private DialogBox(String text, String speaker, boolean isUser) {
        Label dialog = new Label(text.strip());
        dialog.setMaxWidth(DIALOG_MAX_WIDTH);
        dialog.setWrapText(true);
        dialog.getStyleClass().addAll("dialog-label", isUser ? "user-dialog" : "bro-dialog");

        StackPane avatar = createAvatar(speaker, isUser);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setSpacing(10);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("dialog-box");

        if (isUser) {
            getChildren().addAll(dialog, avatar);
        } else {
            getChildren().addAll(avatar, dialog);
        }
    }

    /** Creates a right-aligned dialog for user input. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You", true);
    }

    /** Creates a left-aligned dialog for Bro's response. */
    public static DialogBox getBroDialog(String text) {
        return new DialogBox(text, Personality.PERSONA_NAME, false);
    }

    /** Creates a circular initial badge that does not require external image files. */
    private StackPane createAvatar(String speaker, boolean isUser) {
        Circle circle = new Circle(AVATAR_RADIUS);
        circle.getStyleClass().add(isUser ? "user-avatar" : "bro-avatar");

        Label initial = new Label(speaker.substring(0, 1));
        initial.getStyleClass().add("avatar-initial");

        StackPane avatar = new StackPane(circle, initial);
        avatar.setAccessibleText(speaker);
        return avatar;
    }
}
