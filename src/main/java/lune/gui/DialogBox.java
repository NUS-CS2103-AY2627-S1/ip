package lune.gui;

import java.io.IOException;

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
import javafx.scene.shape.Circle;

/**
 * A single chat message bubble: a Label holding the message text next to
 * an ImageView holding the speaker's avatar. Created right-aligned (user
 * style) by default; flip() switches it to left-aligned, for Lune's
 * replies.
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
        } catch (IOException e) {
            throw new IllegalStateException("Could not load /view/DialogBox.fxml", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        // Crop the avatar to a circle rather than showing its raw rectangle
        // — the classic chat-app look, and it hides the fact the source
        // photos aren't uniformly framed. Sized from the ImageView's own
        // FXML-configured fitWidth/fitHeight rather than a hardcoded value.
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Flips this dialog box so the image is on the left and the text on
     * the right, instead of the default right-aligned (image-on-the-right)
     * layout used for the user's own messages.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a right-aligned dialog box for a message the user typed.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog box for one of Lune's replies.
     */
    public static DialogBox getLuneDialog(String text, Image image) {
        DialogBox db = new DialogBox(text, image);
        db.flip();
        return db;
    }

    /**
     * Creates a left-aligned dialog box for one of Lune's replies, colored
     * according to commandType (e.g. "todo", "mark", "delete") so different
     * kinds of replies are visually distinct.
     */
    public static DialogBox getLuneDialog(String text, Image image, String commandType) {
        DialogBox db = getLuneDialog(text, image);
        db.changeDialogStyle(commandType);
        return db;
    }

    private void changeDialogStyle(String commandType) {
        switch (commandType) {
            case "todo":
            case "deadline":
            case "event":
                dialog.getStyleClass().add("add-label");
                break;
            case "mark":
            case "unmark":
                dialog.getStyleClass().add("marked-label");
                break;
            case "delete":
                dialog.getStyleClass().add("delete-label");
                break;
            case "archive":
                dialog.getStyleClass().add("archive-label");
                break;
            default:
                break;
        }
    }
}
