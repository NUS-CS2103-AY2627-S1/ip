package orangutan.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Dialog box for a single message in the Orangutan GUI.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private StackPane imageContainer;

    /**
     * Constructor for a dialog box.
     * @param text The text to be displayed.
     * @param img The profile image to be displayed.
     */
    public DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();

            assert dialog != null : "DialogBox says: dialog should not be null!!!";
            assert displayPicture != null : "DialogBox says: displayPicture should not be null!!!";
            assert imageContainer != null : "DialogBox says: imageContainer should not be null!!!";

        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);

        // turn the image into a circle (note that in DialogBox.fxml this is a child of ImageContainer)
        displayPicture.setImage(img);
        double radius = displayPicture.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Creates a dialog box for user dialog.
     * @param text The text to be displayed.
     * @param img The profile image to be displayed.
     * @return Dialog box for user. This one is aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox userDialog = new DialogBox(text, img);
        userDialog.dialog.getStyleClass().add("user-label");
        return userDialog;
    }

    /**
     * Creates a dialog box for Orangutan's dialog.
     * @param text The text to be displayed.
     * @param img The profile image to be displayed.
     * @return Dialog box for Orangutan. This one is aligned to the left.
     */
    public static DialogBox getOrangutanDialog(String text, Image img) {
        DialogBox orangutanDialog = new DialogBox(text, img);
        orangutanDialog.flip();
        orangutanDialog.dialog.getStyleClass().add("reply-label");
        return orangutanDialog;
    }

    /**
     * flips dilaog box so that image is on the left and text on the right.
     */
    private void flip() {
        // solution provided by DeepSeek.
        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialog.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    }
}
