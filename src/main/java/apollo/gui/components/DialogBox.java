package apollo.gui.components;

import java.io.IOException;
import java.util.Collections;

import apollo.ResponseType;
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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        Circle clip = new Circle(25, 25, 25);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    private void changeDialogStyle(ResponseType responseType) {
        switch (responseType) {
            case ADD:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK_CHANGE:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case LIST:
                dialog.getStyleClass().add("list-label");
                break;
            case ERROR:
                dialog.getStyleClass().add("error-label");
                break;
            case BYE:
                dialog.getStyleClass().add("bye-label");
                break;
            default:
                // Use the default reply style for all other commands.
        }
    }

    public static DialogBox getApolloDialog(String text, Image img, ResponseType responseType) {
        var db = new DialogBox(text, img);
        db.getStyleClass().add("apollo-dialog");
        db.flip();
        db.changeDialogStyle(responseType);
        return db;
    }
}
