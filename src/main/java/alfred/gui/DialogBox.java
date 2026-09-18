package alfred.gui;

import java.io.IOException;
import java.util.Collections;

import alfred.ui.Ui;
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
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load a dialog box.", exception);
        }

        // DialogBox.fxml must wire both the message label and the avatar.
        assert dialog != null : "DialogBox.fxml must inject the dialog label";
        assert displayPicture != null : "DialogBox.fxml must inject the avatar image view";
        dialog.setText(text);
        displayPicture.setImage(image);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        clipDisplayPicture();
    }

    /**
     * Clips the avatar to a circle so it matches the butler-themed chat layout.
     */
    private void clipDisplayPicture() {
        double radius = displayPicture.getFitWidth() / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds the CSS class for {@code replyStyle} after the Alfred bubble has been flipped.
     */
    private void applyReplyStyle(Ui.ReplyStyle replyStyle) {
        if (replyStyle == Ui.ReplyStyle.ERROR) {
            dialog.getStyleClass().add("error-label");
        } else if (replyStyle == Ui.ReplyStyle.LIST) {
            dialog.getStyleClass().add("list-label");
        }
    }

    /**
     * Returns a dialog box for text typed by the user.
     *
     * @param text User input.
     * @param image User avatar.
     * @return Dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for Alfred's reply.
     *
     * @param text Alfred's reply.
     * @param image Alfred's avatar.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox getAlfredDialog(String text, Image image) {
        return getAlfredDialog(text, image, Ui.ReplyStyle.NORMAL);
    }

    /**
     * Returns a dialog box for Alfred's reply, styled for {@code replyStyle}.
     *
     * @param text Alfred's reply.
     * @param image Alfred's avatar.
     * @param replyStyle How the bubble should be presented.
     * @return Dialog box aligned to the left.
     */
    public static DialogBox getAlfredDialog(String text, Image image, Ui.ReplyStyle replyStyle) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.applyReplyStyle(replyStyle);
        return dialogBox;
    }
}
