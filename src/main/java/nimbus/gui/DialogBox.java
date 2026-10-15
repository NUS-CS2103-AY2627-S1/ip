package nimbus.gui;

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
import nimbus.parser.Command;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's
 * face
 * and a label containing text from the speaker.
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
        double radius = displayPicture.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the
     * right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getNimbusDialog(String text, Image img, Command commandType, boolean isError) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType, isError);
        return db;
    }

    private void changeDialogStyle(Command commandType, boolean isError) {
        if (isError) {
            dialog.getStyleClass().add("error-label");
            return;
        }
        if (commandType == null) {
            dialog.getStyleClass().add("label");
            return;
        }
        switch (commandType) {
            case TODO: // Fallthrough
            case EVENT: // Falthrough
            case DEADLINE:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK: // Fallthrough
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                dialog.getStyleClass().add("label");
                break;
        }
    }
}
