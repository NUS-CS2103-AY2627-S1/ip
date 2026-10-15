package taterror.gui;

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
 * One chat bubble: a speaker's avatar next to a text label, loaded from
 * {@code view/DialogBox.fxml}. User messages and TA Terror's replies use the
 * same layout, mirrored - see {@link #getUserDialog} vs {@link #getTaTerrorDialog}.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Builds one dialog bubble showing {@code text} next to {@code img}, in the
     * default (user) layout: text on the left, avatar on the right.
     */
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
        clipToCircle();
    }

    /**
     * Masks {@link #displayPicture} to a circle matching its rendered size, so
     * avatars read as profile pictures instead of plain square thumbnails.
     */
    private void clipToCircle() {
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Mirrors this bubble for TA Terror's side of the conversation: reverses the
     * child order (avatar first, text second), left-aligns instead of the default
     * right-alignment, and swaps in the {@code reply-label} CSS style class so the
     * bubble renders with a different color than a user message.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog bubble for something the user said.
     *
     * @param text the message text to display
     * @param img the user's avatar image
     * @return a bubble in the default (right-aligned) layout
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog bubble for one of TA Terror's replies.
     *
     * @param text the reply text to display
     * @param img TA Terror's avatar image
     * @return a mirrored (left-aligned) bubble styled as a reply - see {@link #flip()}
     */
    public static DialogBox getTaTerrorDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
