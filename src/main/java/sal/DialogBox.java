package sal;

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
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of a circular profile picture
 * and a message bubble containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Circle displayPicture;

    private DialogBox(String text, Image img, String bubbleStyleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.getStyleClass().addAll("bubble", bubbleStyleClass);
        displayPicture.setFill(new ImagePattern(img, 0, 0, 1, 1, true));
    }

    /**
     * Flips the dialog box such that the profile picture is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for a user message, aligned to the right.
     *
     * @param text Message text.
     * @param img User avatar.
     * @return Dialog box showing the user's message.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, "user-bubble");
    }

    /**
     * Returns a dialog box for a Sal message, aligned to the left.
     *
     * @param text Message text.
     * @param img Sal avatar.
     * @param isError {@code true} to style the bubble as an error message.
     * @return Dialog box showing Sal's reply.
     */
    public static DialogBox getSalDialog(String text, Image img, boolean isError) {
        String bubbleStyleClass = isError ? "error-bubble" : "bot-bubble";
        var db = new DialogBox(text, img, bubbleStyleClass);
        db.flip();
        return db;
    }
}
