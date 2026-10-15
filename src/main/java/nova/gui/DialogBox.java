package nova.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one user or Nova message in the conversation view. */
public class DialogBox extends HBox {
    /** Defines the diameter used for conversation avatars. */
    private static final double AVATAR_SIZE = 36.0;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a Nova dialog box.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /** Flips this dialog so that the image appears on the left. */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(nodes);
        this.getChildren().setAll(nodes);
        setAlignment(javafx.geometry.Pos.TOP_LEFT);
        dialog.getStyleClass().add("nova-label");
    }

    /** Creates a dialog box for the user's message. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /** Creates a dialog box for Nova's response. */
    public static DialogBox getNovaDialog(String text, Image image) {
        return getNovaDialog(text, image, false);
    }

    /** Creates a dialog box for Nova's response, optionally styled as an error. */
    public static DialogBox getNovaDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }
}
