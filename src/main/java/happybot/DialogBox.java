package happybot;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message beside a small speaker badge.
 */
public class DialogBox extends HBox {
    private static final String BOT_BADGE = "HB";
    private static final String USER_BADGE = "YOU";

    private enum DialogRole {
        USER,
        BOT
    }

    private DialogBox(String text, DialogRole dialogRole) {
        Label message = new Label(text.stripLeading());
        Label badge = new Label(dialogRole == DialogRole.USER ? USER_BADGE : BOT_BADGE);

        message.setWrapText(true);
        message.setMaxWidth(330);
        message.getStyleClass().add("message-bubble");
        badge.getStyleClass().add("speaker-badge");

        if (dialogRole == DialogRole.USER) {
            setAlignment(Pos.TOP_RIGHT);
            message.getStyleClass().add("user-message");
            badge.getStyleClass().add("user-badge");
            getChildren().addAll(message, badge);
        } else {
            setAlignment(Pos.TOP_LEFT);
            message.getStyleClass().add("bot-message");
            badge.getStyleClass().add("bot-badge");
            getChildren().addAll(badge, message);
        }
    }

    /**
     * Returns a dialog box styled for the user.
     *
     * @param text The message to display.
     * @return The user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, DialogRole.USER);
    }

    /**
     * Returns a dialog box styled for HappyBot.
     *
     * @param text The message to display.
     * @return The HappyBot dialog box.
     */
    public static DialogBox getBotDialog(String text) {
        return new DialogBox(text, DialogRole.BOT);
    }
}
