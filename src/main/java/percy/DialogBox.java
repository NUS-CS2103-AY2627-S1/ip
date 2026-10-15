package percy;

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

/**
 * One entry in the conversation: a wrapped text label next to a small avatar
 * image. The user's messages keep the default layout (text left, image right);
 * Percy's messages are flipped so the image sits on the left.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /** Swaps the child order so the avatar appears on the left. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        this.getChildren().setAll(tmp);
        this.setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for something the user said.
     *
     * @param text the user's message
     * @param img the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a (flipped) dialog box for one of Percy's replies.
     *
     * @param text Percy's response
     * @param img Percy's avatar
     * @return the dialog box
     */
    public static DialogBox getPercyDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates a (flipped) dialog box for one of Percy's error replies,
     * restyled in red so mistakes stand out from ordinary replies at a glance.
     *
     * @param text Percy's error message
     * @param img Percy's avatar
     * @return the dialog box
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.markAsError();
        return db;
    }

    /** Restyles the text bubble to stand out as an error. */
    private void markAsError() {
        dialog.setStyle("-fx-font-family: 'monospace'; -fx-text-fill: #7a1f1f; "
                + "-fx-background-color: #fdecec; -fx-border-color: #d64545; "
                + "-fx-border-width: 1.5; -fx-padding: 7; -fx-background-radius: 8; "
                + "-fx-border-radius: 8;");
    }
}
