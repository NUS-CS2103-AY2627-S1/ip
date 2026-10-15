package zikiai.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Displays a wrapped chat message beside the speaker's tutorial avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the chat bubble layout.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(23.0, 23.0, 23.0));
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param text message to display.
     * @param image user's avatar.
     * @return user chat bubble.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.displayPicture.setManaged(false);
        box.displayPicture.setVisible(false);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /**
     * Creates a left-aligned chatbot reply.
     *
     * @param text reply to display.
     * @param image chatbot's avatar.
     * @return chatbot chat bubble.
     */
    public static DialogBox getZikiaiDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.getChildren().setAll(box.displayPicture, box.dialog);
        box.setAlignment(Pos.TOP_LEFT);
        box.getStyleClass().add("zikiai-dialog");
        box.dialog.getStyleClass().add("reply-label");
        return box;
    }
}
