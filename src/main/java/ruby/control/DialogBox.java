package ruby.control;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ruby.MainWindow;

/**
 * Represents one chat bubble in the dialog area, pairing a message label with
 * the avatar image of whoever said it.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box from the shared FXML layout.
     *
     * @param text Message the bubble should show.
     * @param img  Avatar image to display beside the message.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("DialogBox.fxml could not be loaded", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        double side = Math.min(img.getWidth(), img.getHeight());
        displayPicture
                .setViewport(new Rectangle2D((img.getWidth() - side) / 2, (img.getHeight() - side) / 2, side, side));
    }

    /**
     * Reverses the bubble's layout so Ruby's messages align to the left with
     * the avatar after the label.
     */
    private void flip() {
        ObservableList<Node> reverseChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(reverseChildren);
        getChildren().setAll(reverseChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box styled as the user's message.
     *
     * @param text Message the user sent.
     * @param img  Avatar image of the user.
     * @return The user's dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a dialog box styled as Ruby's reply.
     *
     * @param text Message Ruby replied with.
     * @param img  Avatar image of Ruby.
     * @return Ruby's dialog box.
     */
    public static DialogBox getRubyDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.getStyleClass().add("ruby-dialog");
        return dialogBox;
    }
}
