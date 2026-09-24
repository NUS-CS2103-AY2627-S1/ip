package sage;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Represents one chat message and its speaker avatar. */
public class DialogBox extends HBox {
    private static final String SENDER_BUBBLE_STYLE = "-fx-background-color: #d9ecff;"
            + "-fx-background-radius: 12; -fx-border-radius: 12;";
    private static final String RECEIVER_BUBBLE_STYLE = "-fx-background-color: #ffffff;"
            + "-fx-background-radius: 12; -fx-border-radius: 12;";

    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }
        dialog.setText(text);
        dialog.setPadding(new Insets(7, 10, 7, 10));
        displayPicture.setImage(image);
    }

    /** Creates a sender message aligned to the right.
     *
     * @param text message text
     * @param image sender avatar image
     * @return right-aligned sender message
     */
    public static DialogBox getSenderDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.dialog.setStyle(SENDER_BUBBLE_STYLE);
        box.setAlignment(Pos.TOP_RIGHT);
        return box;
    }

    /** Creates a receiver message aligned to the left.
     *
     * @param text message text
     * @param image receiver avatar image
     * @return left-aligned receiver message
     */
    public static DialogBox getReceiverDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        ObservableList<Node> children = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(children);
        box.getChildren().setAll(children);
        box.dialog.setStyle(RECEIVER_BUBBLE_STYLE);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }
}
