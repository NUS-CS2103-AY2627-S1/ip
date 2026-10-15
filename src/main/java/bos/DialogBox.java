package bos;

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

/**
 * Represents a dialog box consisting of an image to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final String USER_DIALOG_STYLE_CLASS = "user-dialog";
    private static final String BOS_DIALOG_STYLE_CLASS = "bos-dialog";

    @FXML
    private Label dialogText;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout", exception);
        }

        assert dialogText != null && displayPicture != null
                : "Dialog controls must be injected after loading the layout";

        dialogText.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Places the display picture on the left and the message text on the right.
     */
    private void placeDisplayPictureOnLeft() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a message sent by the user.
     *
     * @param text message to display.
     * @param image user's display image.
     * @return dialog box with the user's image on the right.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add(USER_DIALOG_STYLE_CLASS);
        return dialogBox;
    }

    /**
     * Creates a dialog box for a message sent by Bos.
     *
     * @param text message to display.
     * @param image Bos's display image.
     * @return dialog box with Bos's image on the left.
     */
    public static DialogBox createBosDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add(BOS_DIALOG_STYLE_CLASS);
        dialogBox.placeDisplayPictureOnLeft();
        return dialogBox;
    }
}
