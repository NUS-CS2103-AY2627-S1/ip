package potato.ui;

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
 * Custom control representing a dialog box consisting of an Avatar and Text label.
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

        // Enforce 40x40 container size
        displayPicture.setFitWidth(40.0);
        displayPicture.setFitHeight(40.0);

        // Circular avatar clipping technique adapted from:
        // https://guigarage.com/2015/11/round-images-with-javafx/
        // Dynamic clip bound to fitWidth and fitHeight (fixes semi-circle clipping)
        Circle clip = new Circle();
        clip.centerXProperty().bind(displayPicture.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(displayPicture.fitHeightProperty().divide(2));
        clip.radiusProperty().bind(displayPicture.fitWidthProperty().divide(2));
        displayPicture.setClip(clip);

        // Ensure text expands fully without clipping
        dialog.setMinHeight(Region.USE_PREF_SIZE);
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
     * Factory method for creating user dialog boxes.
     *
     * @param text Message text.
     * @param img User avatar image.
     * @return Formatted DialogBox instance.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Factory method for creating Potato bot response dialog boxes.
     *
     * @param text Response text.
     * @param img Bot avatar image.
     * @param isError True if the message represents an error/exception.
     * @return Formatted DialogBox instance.
     */
    public static DialogBox getPotatoDialog(String text, Image img, boolean isError) {
        var db = new DialogBox(text, img);
        db.flip();
        if (isError) {
            db.dialog.getStyleClass().add("error-label");
        } else {
            db.dialog.getStyleClass().add("reply-label");
        }
        return db;
    }

    /**
     * Convenience overload for standard non-error bot responses.
     */
    public static DialogBox getPotatoDialog(String text, Image img) {
        return getPotatoDialog(text, img, false);
    }
}