package nova.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays a user command, a Nova response, or an error in its distinct visual format.
 */
public class DialogBox extends HBox {
    private static final double MAXIMUM_DIALOG_WIDTH = 340.0;
    private static final String NOVA_SYMBOL = "✦";
    private static final String ERROR_SYMBOL = "!";

    private DialogBox(String message, DialogType dialogType) {
        Label dialog = new Label(message);
        dialog.setWrapText(true);
        dialog.setMaxWidth(MAXIMUM_DIALOG_WIDTH);

        getStyleClass().add("dialog-box");
        if (dialogType == DialogType.USER) {
            setAlignment(Pos.TOP_RIGHT);
            dialog.getStyleClass().add("user-command");
            getChildren().add(dialog);
            return;
        }

        boolean isError = dialogType == DialogType.ERROR;
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(isError ? "error-dialog" : "nova-dialog");

        Label avatar = new Label(isError ? ERROR_SYMBOL : NOVA_SYMBOL);
        avatar.getStyleClass().addAll("avatar", isError ? "error-avatar" : "nova-avatar");
        getChildren().addAll(avatar, dialog);
    }

    /**
     * Creates a dialog showing a command entered by the user.
     *
     * @param message command text to display.
     * @return user-side dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, DialogType.USER);
    }

    /**
     * Creates a dialog showing Nova's response.
     *
     * @param message response text to display.
     * @return Nova-side dialog box.
     */
    public static DialogBox getNovaDialog(String message) {
        return new DialogBox(message, DialogType.NOVA);
    }

    /**
     * Creates a visually prominent dialog showing a command error.
     *
     * @param message error explanation to display.
     * @return error dialog box.
     */
    public static DialogBox getErrorDialog(String message) {
        return new DialogBox(message, DialogType.ERROR);
    }

    /**
     * Identifies the visual treatment used for a dialog.
     */
    private enum DialogType {
        USER,
        NOVA,
        ERROR
    }
}
