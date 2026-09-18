package bobby.gui;

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
 * Shows one chat message in the conversation.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 21.5;
    private static final double MAX_MESSAGE_WIDTH_RATIO = 0.74;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image, boolean isAvatarVisible) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml.", e);
        }

        assert dialog != null : "FXML should inject the dialog label.";
        assert displayPicture != null : "FXML should inject the display picture.";
        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_MESSAGE_WIDTH_RATIO));
        displayPicture.setImage(image);
        displayPicture.setPreserveRatio(false);
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        displayPicture.setManaged(isAvatarVisible);
        displayPicture.setVisible(isAvatarVisible);
    }

    /**
     * Returns a dialog box for the user's message.
     *
     * @param text message entered by the user.
     * @param image user's avatar image.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, true);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a dialog box for Bobby's response.
     *
     * @param text response from Bobby.
     * @param image Bobby's avatar image.
     * @param isError true if the response reports an error to the user.
     * @return Bobby dialog box.
     */
    public static DialogBox getBobbyDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image, true);
        dialogBox.flip();
        dialogBox.getStyleClass().add("bobby-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }

    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
    }
}
