package chatty.gui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one user or Chatty message in the conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label avatar;

    @FXML
    private Label dialog;

    /**
     * Loads the reusable dialog view and fills it with message content.
     *
     * @param text message displayed in the dialog.
     * @param avatarText short label identifying the speaker.
     */
    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box view.", exception);
        }

        dialog.setText(text.stripLeading());
        dialog.maxWidthProperty().bind(Bindings.max(180, widthProperty().multiply(0.82)));
        avatar.setText(avatarText);
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param text user message.
     * @return user dialog containing the message.
     */
    public static DialogBox createUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "ME");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getChildren().setAll(dialogBox.dialog, dialogBox.avatar);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a Chatty response.
     *
     * @param text Chatty response.
     * @return Chatty dialog containing the response.
     */
    public static DialogBox createChattyDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "O");
        dialogBox.getStyleClass().add("chatty-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned, highlighted dialog for an error response.
     *
     * @param text error response from Chatty.
     * @return error dialog containing the response.
     */
    public static DialogBox createErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "!");
        dialogBox.getStyleClass().addAll("chatty-dialog", "error-dialog");
        return dialogBox;
    }
}
