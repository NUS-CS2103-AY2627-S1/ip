package della;

import java.io.IOException;
import java.util.Collections;

import della.command.Command;
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
    /** The label that displays the dialog message. */
    @FXML
    private Label dialog;

    /** The image that represents the speaker. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the specified message and speaker image.
     *
     * @param text the message to display.
     * @param img the image to display beside the message.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();

            getStylesheets().add(
                    MainWindow.class.getResource("/css/dialog-box.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);

        Circle clip = new Circle(35, 35, 35);
        displayPicture.setClip(clip);
        displayPicture.setFitWidth(70);
        displayPicture.setFitHeight(70);
        displayPicture.setPreserveRatio(false);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box so that the speaker image appears on the left and the message appears on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);

        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box representing a user's message.
     *
     * @param text the user's message.
     * @param img the image representing the user.
     * @return a right-aligned dialog box for the user's message.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Changes the dialog label style based on the command type.
     *
     * @param command the command that determines the response style.
     */
    private void changeDialogStyle(Command command) {
        switch(command) {
            case TODO, DEADLINE, EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK, UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case UNKNOWN:
                dialog.getStyleClass().add("unknown-label");
                break;
            default:
                // Do nothing
        }
    }

    /**
     * Returns a dialog box representing Della's response.
     *
     * @param text Della's response.
     * @param img the image representing Della.
     * @param command the command that determines the response style.
     * @return a left-aligned dialog box for Della's response.
     */
    public static DialogBox getDellaDialog(String text, Image img, Command command) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(command);
        return db;
    }
}

