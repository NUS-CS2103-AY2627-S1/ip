package whimsybot.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one chat message and its speaker avatar. */
public class DialogBox extends HBox {
    @FXML
    private ImageView avatar;
    @FXML
    private Label dialog;

    private DialogBox(String text, String avatarPath, String styleClass) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }
        dialog.setText(text);
        avatar.setImage(new Image(DialogBox.class.getResourceAsStream("/" + avatarPath)));
        avatar.setClip(new Circle(18.0, 18.0, 18.0));
        getStyleClass().add(styleClass);
    }

    /** Creates a dialog box aligned as a user message. */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "images/user-avatar.png", "user-dialog");
    }

    /** Creates a dialog box aligned as a Whimsy Bot message. */
    public static DialogBox getWhimsyBotDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "images/whimsybot-avatar.png", "bot-dialog");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getChildren().remove(dialogBox.avatar);
        dialogBox.getChildren().add(0, dialogBox.avatar);
        return dialogBox;
    }

    /** Creates a Whimsy Bot dialog box styled as an error response. */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getWhimsyBotDialog(text);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

}
