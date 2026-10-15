package edith.gui;

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

/** Represents one message and its speaker avatar in the conversation. */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 24.0;
    private static final Image EDITH_PHOTO = loadPhoto("/images/edith.png");
    private static final Image USER_PHOTO = loadPhoto("/images/user.png");

    @FXML
    private ImageView avatar;
    @FXML
    private Label dialog;

    private DialogBox(String text, Image photo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the dialog box view.", e);
        }
        dialog.setText(text);
        avatar.setImage(photo);
        avatar.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
    }

    /** Loads a bundled profile photo from the application resources. */
    private static Image loadPhoto(String resourcePath) {
        return new Image(DialogBox.class.getResource(resourcePath).toExternalForm());
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param text message text to display
     * @return a user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_PHOTO);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned message written by Edith.
     *
     * @param text message text to display
     * @return an Edith dialog box
     */
    public static DialogBox getEdithDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, EDITH_PHOTO);
        dialogBox.flip();
        dialogBox.getStyleClass().add("edith-dialog");
        return dialogBox;
    }

    /** Places the avatar on the left for messages written by Edith. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
