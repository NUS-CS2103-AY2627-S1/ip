package app;

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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Represents a single message in the conversation. The user and the app are styled
 * asymmetrically (different alignment, width and colours) since they play different
 * roles in the conversation, rather than mirroring each other like two people talking.
 */
public class DialogBox extends HBox {
    @FXML
    private VBox bubble;
    @FXML
    private Label senderLabel;
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, String senderName, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        senderLabel.setText(senderName);
        dialog.setText(text);
        displayPicture.setImage(img);
        // Crop the square portrait into a rectangle so it blends with the bubble UI.
        displayPicture.setClip(new Rectangle(displayPicture.getFitWidth()*2,
                displayPicture.getFitHeight()*2));
    }

    /**
     * Reverses the child order so the avatar sits on the left of the bubble, matching
     * the convention of the app's replies appearing on the left of the conversation.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
    }

    /**
     * Styles this box as a compact, right-aligned bubble for messages typed by the user.
     * The bubble is capped at a fraction of the window width so it reads as a chat bubble
     * rather than a full-width block.
     */
    private void styleAsUser() {
        setAlignment(Pos.TOP_RIGHT);
        bubble.getStyleClass().add("user-bubble");
        bubble.maxWidthProperty().bind(widthProperty().multiply(0.75));
    }

    /**
     * Styles this box as a left-aligned, near-full-width panel for app responses, since
     * replies can be long (e.g. task lists) and benefit from the extra reading width.
     *
     * @param isError whether the response represents an error, to be visually flagged
     */
    private void styleAsApp(boolean isError) {
        flip();
        setAlignment(Pos.TOP_LEFT);
        bubble.getStyleClass().add(isError ? "error-bubble" : "app-bubble");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, "You", img);
        db.styleAsUser();
        return db;
    }

    public static DialogBox getDestroyerOfWorldsDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, "DestroyerOfWorlds", img);
        db.styleAsApp(isError);
        return db;
    }
}

