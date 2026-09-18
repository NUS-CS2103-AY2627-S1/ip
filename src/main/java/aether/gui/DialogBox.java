package aether.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/** Displays one user or Aether message as a chat bubble with a speaker badge. */
public class DialogBox extends HBox {
    private static final double MESSAGE_WIDTH_RATIO = 0.78;

    private DialogBox(String text, String speaker, DialogType dialogType) {
        Label message = new Label(text);
        Label avatar = new Label(speaker);
        boolean isUser = dialogType == DialogType.USER;

        message.setWrapText(true);
        message.maxWidthProperty().bind(widthProperty().multiply(MESSAGE_WIDTH_RATIO));
        HBox.setHgrow(message, Priority.SOMETIMES);

        getStyleClass().add("dialog-box");
        message.getStyleClass().add(dialogType.getMessageStyle());
        avatar.getStyleClass().add(dialogType.getAvatarStyle());
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        if (isUser) {
            getChildren().addAll(message, avatar);
        } else {
            getChildren().addAll(avatar, message);
        }
    }

    /**
     * Creates a right-aligned dialog box for a command entered by the user.
     *
     * @param text the command to display
     * @return a user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "YOU", DialogType.USER);
    }

    /**
     * Creates a left-aligned dialog box for Aether's response.
     *
     * @param text the response to display
     * @return an Aether dialog box
     */
    public static DialogBox getAetherDialog(String text) {
        return new DialogBox(text, "*", DialogType.AETHER);
    }

    /**
     * Creates a visually highlighted dialog box for an invalid command.
     *
     * @param text the corrective error message to display
     * @return an Aether error dialog box
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, "!", DialogType.ERROR);
    }

    /** Defines the styling and alignment used by each kind of chat message. */
    private enum DialogType {
        USER("user-message", "user-avatar"),
        AETHER("aether-message", "aether-avatar"),
        ERROR("error-message", "error-avatar");

        private final String messageStyle;
        private final String avatarStyle;

        DialogType(String messageStyle, String avatarStyle) {
            this.messageStyle = messageStyle;
            this.avatarStyle = avatarStyle;
        }

        private String getMessageStyle() {
            return messageStyle;
        }

        private String getAvatarStyle() {
            return avatarStyle;
        }
    }
}
