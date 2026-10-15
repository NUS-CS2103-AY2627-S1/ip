package odysseus.ui;

// Reused from https://se-education.org/guides/tutorials/javaFx.html with minor modifications.

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
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

        // Crop the image to a centred square (like CSS object-fit: cover) so it
        // fills the avatar without distortion, then clip it into a circle.
        double side = Math.min(img.getWidth(), img.getHeight());
        double cropX = (img.getWidth() - side) / 2;
        double cropY = (img.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, side, side));

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
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
     * Returns a dialog box showing the user's message, with the picture on the right.
     *
     * @param text the user's message
     * @param img the user's avatar
     * @return a dialog box representing the user's turn
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Returns a dialog box showing Odysseus's reply, flipped so the picture is on the left.
     *
     * @param text Odysseus's reply
     * @param img Odysseus's avatar
     * @return a dialog box representing Odysseus's turn
     */
    public static DialogBox getOdysseusDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("odysseus-bubble");
        return db;
    }

    /**
     * Returns a dialog box showing an error reply from Odysseus, styled
     * distinctly so failures stand out from normal replies.
     *
     * @param text the error message
     * @param img Odysseus's avatar
     * @return a dialog box representing an error reply
     */
    public static DialogBox getOdysseusErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("error-bubble");
        return db;
    }
}
