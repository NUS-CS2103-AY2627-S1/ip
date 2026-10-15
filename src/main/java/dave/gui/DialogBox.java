package dave.gui;

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
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * Custom control representing a dialog box containing an avatar and a text message.
 */
public class DialogBox extends HBox {

    /** Radius in pixels for avatar clipping circles. */
    private static final double AVATAR_RADIUS = 19.0;
    /** Horizontal space reserved for avatar, padding, and spacing. */
    private static final double HORIZONTAL_CHROME_PADDING = 75.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a new DialogBox with the specified text message and display image.
     *
     * @param text Text message to display.
     * @param img Image to use as the avatar, or null if no avatar is available.
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

        setAlignment(Pos.TOP_RIGHT);
        this.dialog.setText(text);
        this.dialog.setMinHeight(Region.USE_PREF_SIZE);
        this.dialog.maxWidthProperty().bind(this.widthProperty().subtract(HORIZONTAL_CHROME_PADDING));

        if (img != null) {
            this.displayPicture.setImage(img);
            clipAvatar(img);
        } else {
            this.displayPicture.setVisible(false);
            this.displayPicture.setManaged(false);
        }
    }

    /**
     * Clips the display picture as a circle centered on the avatar.
     *
     * @param img Avatar image to clip.
     */
    private void clipAvatar(Image img) {
        Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
        this.displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the image is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box representing the user's message.
     *
     * @param text Text sent by the user.
     * @param img Avatar image of the user.
     * @return DialogBox configured for user input.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.getStyleClass().add("user-dialog");
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Creates a dialog box representing Dave's standard response message.
     *
     * @param text Text response from Dave.
     * @param img Avatar image of Dave.
     * @return DialogBox configured for Dave's response.
     */
    public static DialogBox getDaveDialog(String text, Image img) {
        return getDaveDialog(text, img, false);
    }

    /**
     * Creates a dialog box representing Dave's response message with optional error styling.
     *
     * @param text Text response from Dave.
     * @param img Avatar image of Dave.
     * @param isError True if the response indicates an error, false for normal output.
     * @return DialogBox configured for Dave's response.
     */
    public static DialogBox getDaveDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.getStyleClass().add("dave-dialog");
        if (isError) {
            db.getStyleClass().add("error-dialog");
            db.dialog.getStyleClass().add("error-label");
        } else {
            db.dialog.getStyleClass().add("dave-label");
        }
        return db;
    }

    /**
     * Creates a dialog box representing Dave's error response message.
     *
     * @param text Error text response from Dave.
     * @param img Avatar image of Dave.
     * @return DialogBox configured with error styling.
     */
    public static DialogBox getDaveErrorDialog(String text, Image img) {
        return getDaveDialog(text, img, true);
    }
}
