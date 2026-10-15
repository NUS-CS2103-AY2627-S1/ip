package doge.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents one compact, styled message in the conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView avatar;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        getStyleClass().add("dialog-box");
    }

    /** Returns a right-aligned message representing the user's command. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getStyleClass().add("user-message");
        dialogBox.avatar.setVisible(false);
        dialogBox.avatar.setManaged(false);
        return dialogBox;
    }

    /** Returns a left-aligned message representing Doge's response. */
    public static DialogBox getDogeDialog(String text, boolean isError) {
        DialogBox dialogBox = new DialogBox(text);
        if (isError) {
            dialogBox.getStyleClass().add("error-message");
        }
        return dialogBox;
    }
}
