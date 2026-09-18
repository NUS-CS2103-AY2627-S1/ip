package meowmeow.ui;

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

import java.io.IOException;
import java.util.Collections;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 * <p>
 * Reused from the SE-EDU JavaFX tutorial, part 4
 * (https://se-education.org/guides/tutorials/javaFxPart4.html),
 * with minor modifications to naming.
 */
public class DialogBox extends HBox {
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
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box representing the user's message.
     *
     * @param text Text to display in the dialog box.
     * @param img Image to display as the speaker's face.
     * @return A dialog box aligned for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.setBubbleStyle("user-bubble");
        return db;

    }

    /**
     * Returns a dialog box representing MeowMeow's reply, with the image on the left.
     *
     * @param text Text to display in the dialog box.
     * @param img Image to display as the speaker's face.
     * @return A flipped dialog box aligned for MeowMeow.
     */
    public static DialogBox getMeowMeowDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        db.setBubbleStyle("bot-bubble");
        return db;
    }

    /**
     * Applies the bubble styling for one side of the conversation.
     *
     * @param bubbleClass Style class identifying the speaker.
     */
    private void setBubbleStyle(String bubbleClass) {
        dialog.getStyleClass().addAll("bubble", bubbleClass);
    }
}