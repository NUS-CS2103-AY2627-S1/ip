package ui;

import java.io.IOException;
import java.util.Collections;

import friedberg.ResponseType;
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
import javafx.scene.shape.Rectangle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
            assert dialog != null : "DialogBox.fxml must inject dialog";
            assert displayPicture != null : "DialogBox.fxml must inject displayPicture";
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        clipDisplayPictureCorners();
    }

    /**
     * Clips the avatar to slightly rounded corners to match its rounded border frame.
     */
    private void clipDisplayPictureCorners() {
        Rectangle roundedClip = new Rectangle(displayPicture.getFitWidth(), displayPicture.getFitHeight());
        roundedClip.setArcWidth(18);
        roundedClip.setArcHeight(18);
        displayPicture.setClip(roundedClip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().remove("user-label");
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds a semantic style class to Friedberg's response bubble.
     * The enum keeps GUI styling independent from exact parser class names.
     *
     * @param responseType category of response to style
     */
    private void changeDialogStyle(ResponseType responseType) {
        switch (responseType) {
            case ADD:
                dialog.getStyleClass().add("add-label");
                break;
            case STATUS:
                dialog.getStyleClass().add("status-label");
                break;
            case REMOVE:
                dialog.getStyleClass().add("remove-label");
                break;
            case ERROR:
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // The base reply-label style is sufficient for ordinary responses.
        }
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text user's message
     * @param image user's avatar
     * @return dialog displaying the user's message
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for Friedberg.
     *
     * @param text chatbot's response
     * @param image chatbot's avatar
     * @param responseType category used to style the response bubble
     * @return dialog displaying the chatbot's response
     */
    public static DialogBox getFriedbergDialog(String text, Image image, ResponseType responseType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(responseType);
        return dialogBox;
    }
}
