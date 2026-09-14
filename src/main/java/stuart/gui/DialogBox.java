package stuart.gui;

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
import javafx.scene.shape.Circle;

/**
 * A chat bubble containing a speaker's message text, defined in
 * {@code /view/DialogBox.fxml}. The conversation is asymmetric - the user
 * knows it's them without a picture reminding them, so only Stuart's replies
 * show an avatar; see {@link #getUserDialog} and {@link #getStuartDialog}.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36.0;

    /** Wider than the FXML default, since a user bubble has no avatar competing for row width. */
    private static final double USER_BUBBLE_MAX_WIDTH = 300.0;

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
            throw new IllegalStateException("Could not load /view/DialogBox.fxml", e);
        }

        dialog.setText(text);
        if (img == null) {
            displayPicture.setVisible(false);
            displayPicture.setManaged(false);
            dialog.setMaxWidth(USER_BUBBLE_MAX_WIDTH);
        } else {
            displayPicture.setImage(img);
            displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        }
    }

    /**
     * Restyles this dialog box as a reply: aligned left, with the "other
     * speaker" bubble color, instead of the default right-aligned "mine".
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        dialog.getStyleClass().add("bubble-other");
    }

    /**
     * Creates a dialog box for a message the user sent. Shows no avatar,
     * since the user always knows it was them who sent it.
     *
     * @param text the message text
     * @return the dialog box, right-aligned, in the "mine" bubble color
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, null);
        db.dialog.getStyleClass().add("bubble-mine");
        return db;
    }

    /**
     * Creates a dialog box for one of Stuart's replies.
     *
     * @param text the reply text
     * @param img Stuart's avatar
     * @return the dialog box, avatar on the left, in the "other speaker" bubble color
     */
    public static DialogBox getStuartDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
