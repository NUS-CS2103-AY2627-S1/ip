package shai.gui;

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

/** Represents a dialog box containing a speaker image and message text. */
public class DialogBox extends HBox {
    /** Width and height of each circular avatar. */
    private static final double AVATAR_SIZE = 68.0;
    /** Maximum portion of the conversation width occupied by message text. */
    private static final double MAX_DIALOG_WIDTH_RATIO = 0.75;

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
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_DIALOG_WIDTH_RATIO));
        displayPicture.setImage(img);
        displayPicture.setPreserveRatio(false);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        clipAvatarToCircle();
    }

    /** Clips the avatar image so that it completely fills a circular profile picture. */
    private void clipAvatarToCircle() {
        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /** Flips the dialog box so that the image appears on the left. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text message text
     * @param img speaker image
     * @return dialog box aligned for a user message
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for a Shai response.
     *
     * @param text response text
     * @param img speaker image
     * @return dialog box aligned for a Shai response
     */
    public static DialogBox getShaiDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /** Creates a Shai response styled as an error message. */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = getShaiDialog(text, img);
        db.dialog.getStyleClass().add("error-label");
        return db;
    }
}
