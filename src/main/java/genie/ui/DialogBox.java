package genie.ui;

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

/** A reusable chat bubble for a user or Genie message. */
public class DialogBox extends HBox {
    private static final double PROFILE_PICTURE_SIZE = 64.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box", e);
        }

        assert dialog != null : "Dialog label was not injected from DialogBox.fxml";
        assert displayPicture != null : "Profile image was not injected from DialogBox.fxml";
        assert text != null : "Dialog text must not be null";
        assert image != null : "Profile image must not be null";

        dialog.setText(text);
        displayPicture.setImage(image);
        setMaxWidth(Double.MAX_VALUE);
    }

    /** Creates a right-aligned user message with the user's profile image. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.resizeProfilePicture(PROFILE_PICTURE_SIZE);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned Genie message with the bot's profile image. */
    public static DialogBox getGenieDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.resizeProfilePicture(PROFILE_PICTURE_SIZE);
        ObservableList<Node> children = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(children);
        box.getChildren().setAll(children);
        box.dialog.getStyleClass().add("reply-label");
        box.getStyleClass().add("genie-dialog");
        return box;
    }

    /** Creates a left-aligned Genie error message with the bot's profile image. */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox box = getGenieDialog(text, image);
        box.getStyleClass().add("error-dialog");
        return box;
    }

    /** Enlarges a profile picture while keeping its circular crop aligned. */
    private void resizeProfilePicture(double size) {
        displayPicture.setFitWidth(size);
        displayPicture.setFitHeight(size);
        displayPicture.setClip(new Circle(size / 2, size / 2, size / 2));
    }
}
