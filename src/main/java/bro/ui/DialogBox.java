package bro.ui;

import java.io.IOException;

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
    private static final String USER_STYLE_CLASS = "user-dialog";
    private static final String BRO_STYLE_CLASS = "bro-dialog";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a DialogBox with the specified text and image.
     *
     * @param text The text to display.
     * @param img  The speaker's avatar image.
     */
    public DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        setDisplayPicture(img);
    }

    /**
     * Initializes the dialog box by setting a circular clipping mask on the avatar image.
     */
    @FXML
    private void initialize() {
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Sets the avatar image, center-cropping it to a 1:1 aspect ratio to ensure
     * non-square images fit seamlessly inside the circular clip without distortion.
     *
     * @param img The avatar image to display.
     */
    private void setDisplayPicture(Image img) {
        if (img != null && img.getWidth() > 0 && img.getHeight() > 0) {
            double minDimension = Math.min(img.getWidth(), img.getHeight());
            double cropX = (img.getWidth() - minDimension) / 2.0;
            double cropY = (img.getHeight() - minDimension) / 2.0;
            displayPicture.setViewport(new Rectangle2D(cropX, cropY, minDimension, minDimension));
        }
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        this.setAlignment(Pos.BOTTOM_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /**
     * Creates a dialog box for the user with user-specific speech bubble styling.
     *
     * @param text The user's input text.
     * @param img  The user's avatar image.
     * @return A DialogBox configured for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.getStyleClass().add(USER_STYLE_CLASS);
        return db;
    }

    /**
     * Creates a dialog box for Bro's response with avatar on the left and bot speech bubble styling.
     *
     * @param text Bro's response text.
     * @param img  Bro's avatar image.
     * @return A DialogBox configured for Bro.
     */
    public static DialogBox getBroDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.getStyleClass().add(BRO_STYLE_CLASS);
        return db;
    }
}
