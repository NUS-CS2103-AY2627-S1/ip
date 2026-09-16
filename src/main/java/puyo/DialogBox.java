package puyo;

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
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing the text from the speaker.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a DialogBox with the specified text and profile image.
     *
     * @param text The text content of the message.
     * @param img  The profile image/avatar of the speaker.
     */
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
        displayPicture.setImage(img);

        // --- A-BetterGui (v): Profile Pictures / Avatars ---
        // Force explicit fit dimensions on ImageView so the bounds are strictly 80x80 pixels
        displayPicture.setFitWidth(80.0);
        displayPicture.setFitHeight(80.0);
        displayPicture.setPreserveRatio(true);

        // Create a perfect circle clip centered at (40, 40) with radius 40
        Circle clip = new Circle(40.0, 40.0, 40.0);
        displayPicture.setClip(clip);

        this.setPadding(new Insets(10, 15, 10, 15));
        this.setSpacing(10);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and the text is on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        this.getChildren().setAll(tmp);
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates and returns a dialog box representing the user.
     *
     * @param text The user input text.
     * @param img  The user profile image.
     * @return A DialogBox formatted for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates and returns a dialog box representing the chatbot (Puyo).
     *
     * @param text The bot response text.
     * @param img  The bot profile image.
     * @return A DialogBox formatted for the chatbot.
     */
    public static DialogBox getPuyoDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
