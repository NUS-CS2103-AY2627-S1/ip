package lebron.gui;

import java.io.IOException;

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
 * A dialog box consisting of a Label containing text and an ImageView representing
 * the speaker's face, used to render one line of the conversation with LeBron.
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
            throw new IllegalStateException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(
                displayPicture.getFitWidth() / 2, displayPicture.getFitHeight() / 2,
                Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2));
    }

    /**
     * Flips this dialog box so the ImageView is on the left and the Label on the right,
     * and gives the Label LeBron's reply styling, used to visually distinguish LeBron's
     * replies from the user's messages.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(tmp);
        getChildren().setAll(tmp);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a message the user typed.
     *
     * @param text The user's message.
     * @param img The user's avatar.
     * @return The dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a (flipped) dialog box for one of LeBron's replies.
     *
     * @param text LeBron's reply.
     * @param img LeBron's avatar.
     * @return The dialog box.
     */
    public static DialogBox getLeBronDialog(String text, Image img) {
        var dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        return dialogBox;
    }
}
