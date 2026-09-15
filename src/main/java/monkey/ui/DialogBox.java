package monkey.ui;

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

/** Displays one chat message together with its speaker's avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label avatarPlaceholder;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image, String placeholderText) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        avatarPlaceholder.setText(placeholderText);
        displayPicture.setImage(image);

        boolean hasImage = image != null;
        displayPicture.setVisible(hasImage);
        avatarPlaceholder.setVisible(!hasImage);
    }

    /** Creates a right-aligned dialog for a message entered by the user. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, "YOU");
    }

    /** Creates a left-aligned dialog for a response from Monkey. */
    public static DialogBox getMonkeyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, "M");
        dialogBox.flip();
        return dialogBox;
    }

    /** Places the avatar on the left and applies the reply bubble style. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
