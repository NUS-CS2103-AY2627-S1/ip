package gepit.ui;

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
 * Represents a dialog box containing a message and display picture.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        dialog.maxWidthProperty().bind(widthProperty().subtract(120));
    }

    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box representing a user message.
     *
     * @param text Message text.
     * @param image User display picture.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box representing a Gepit response.
     *
     * @param text Message text.
     * @param image Gepit display picture.
     * @return Gepit dialog box.
     */
    public static DialogBox getGepitDialog(String text, Image image) {
        return getGepitDialog(text, image, false);
    }

    /**
     * Returns a dialog box representing a Gepit response.
     *
     * @param text Message text.
     * @param image Gepit display picture.
     * @param isError Whether to apply the error presentation.
     * @return Gepit dialog box.
     */
    public static DialogBox getGepitDialog(
            String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }
}
