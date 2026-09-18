package vega;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Displays one user or Vega message in the conversation. */
public class DialogBox extends HBox {
    private DialogBox(String text, String speaker, boolean isVega) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(320);
        message.getStyleClass().add("message");

        Label avatar = new Label(speaker);
        avatar.getStyleClass().add("avatar");
        getStyleClass().add("dialog-box");

        if (isVega) {
            setAlignment(Pos.TOP_LEFT);
            getStyleClass().add("vega-dialog");
            message.getStyleClass().add("vega-message");
            avatar.getStyleClass().add("vega-avatar");
            if (text.startsWith("OOPS!!!")) {
                getStyleClass().add("error-dialog");
                message.getStyleClass().add("error-message");
                avatar.setText("!");
            }
            getChildren().addAll(avatar, message);
        } else {
            setAlignment(Pos.TOP_RIGHT);
            getStyleClass().add("user-dialog");
            message.getStyleClass().add("user-message");
            avatar.getStyleClass().add("user-avatar");
            getChildren().addAll(message, avatar);
        }
    }

    /**
     * Creates a dialog for a command entered by the user.
     *
     * @param text User's command.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You", false);
    }

    /**
     * Creates a dialog for Vega's response.
     *
     * @param text Vega's response.
     * @return Vega dialog box.
     */
    public static DialogBox getVegaDialog(String text) {
        return new DialogBox(text, "V", true);
    }
}
