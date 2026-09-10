package surveyprogram.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import surveyprogram.processor.ReplyType;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip(ReplyType replyType) {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.BOTTOM_LEFT);
        dialog.getStyleClass().addAll("reply-label", "black-green-label");
        if (replyType.getStyleClass() != null) {
            dialog.getStyleClass().addAll("palette-reply", replyType.getStyleClass());
        }
    }

    /**
     * Creates a dialog box for a message entered by the user.
     *
     * @param text message to display
     * @param img image representing the user
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for an application reply.
     *
     * @param text reply to display
     * @param img image representing the application
     * @return application reply dialog box
     */
    public static DialogBox getReplyDialog(String text, Image img) {
        return getReplyDialog(text, img, ReplyType.DEFAULT);
    }

    /**
     * Creates a categorized application reply.
     *
     * @param text reply to display
     * @param img image representing the application
     * @param replyType visual category of the reply
     * @return application reply dialog box
     */
    public static DialogBox getReplyDialog(String text, Image img, ReplyType replyType) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip(replyType);
        return dialogBox;
    }
}
