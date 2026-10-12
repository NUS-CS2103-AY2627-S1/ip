package braun.ui;

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
 * Custom control representing a dialog bubble paired with an avatar in the GUI chat view.
 * Displays user entries on the right and Braun's broadcast messages on the left.
 *
 * Adapted from the SE-EDU JavaFX tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        assert text != null : "Dialog text cannot be null.";
        assert img != null : "Avatar image cannot be null.";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert dialog != null : "FXML dialog label must be injected.";
        assert displayPicture != null : "FXML displayPicture ImageView must be injected.";

        dialog.setText(text);
        displayPicture.setImage(img);

        Circle clip = new Circle(25, 25, 25);
        displayPicture.setClip(clip);
        displayPicture.getStyleClass().add("avatar-view");

        dialog.maxWidthProperty().bind(widthProperty().subtract(90));
    }

    /**
     * Flips the dialog box orientation so that the avatar is positioned on the left
     * and the dialog bubble appears on the right for Braun's responses.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box representing a message sent by the user (Field Explorer).
     *
     * @param text the message text entered by the user.
     * @param img the user avatar image.
     * @return a configured {@code DialogBox} styled for user messages.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-dialog");
        return db;
    }

    /**
     * Creates a dialog box representing a broadcast response from Braun.
     *
     * @param text the broadcast response text from Braun.
     * @param img Braun's CRT television avatar image.
     * @return a configured and flipped {@code DialogBox} styled for Braun's responses.
     */
    public static DialogBox getBraunDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("braun-dialog");
        return db;
    }

    /**
     * Creates a dialog box representing an error broadcast response from Braun,
     * styled with a high-visibility crimson warning theme.
     *
     * @param text the error broadcast text.
     * @param img Braun's CRT television avatar image.
     * @return a configured and flipped {@code DialogBox} styled for error responses.
     */
    public static DialogBox getBraunErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.dialog.getStyleClass().add("braun-error-dialog");
        return db;
    }
}
