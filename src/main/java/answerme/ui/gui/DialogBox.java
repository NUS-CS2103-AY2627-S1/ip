package answerme.ui.gui;

import java.io.IOException;
import java.util.Collections;

import answerme.ui.ResponseType;
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
 * Represents a dialog box containing a message and an accompanying image.
 */
public class DialogBox extends HBox {
    @FXML
    private ImageView displayPicture;

    @FXML
    private Label dialog;

    /**
     * Constructs a dialog box with the specified message and image.
     *
     * @param strInput the message to display.
     * @param img the profile image of the user.
     */
    public DialogBox(String strInput, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.setText(strInput);

        displayPicture.setImage(img);
        double size = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight());
        Circle clippedImage = new Circle(size / 2, size / 2, size / 2);
        displayPicture.setClip(clippedImage);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and
     * text on the right.
     */
    private void flip(ResponseType responseType) {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");

        switch (responseType) {
            case NORMAL:
                dialog.getStyleClass().add("normal-reply-label");
                break;
            case SUCCESS:
                dialog.getStyleClass().add("success-reply-label");
                break;
            case ERROR:
                dialog.getStyleClass().add("error-reply-label");
                break;
            default:
                throw new IllegalArgumentException("Unknown response type");
        }
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text the user's message.
     * @param image the user's profile image.
     * @return a dialog box displaying the user message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box for an AnswerMe response.
     *
     * @param text AnswerMe's response.
     * @param responseType the visual category of the response.
     * @param image AnswerMe's profile image.
     * @return a dialog box displaying the AnswerMe response.
     */
    public static DialogBox getAnswerMeDialog(String text, ResponseType responseType, Image image) {
        DialogBox db = new DialogBox(text, image);
        db.flip(responseType);
        return db;
    }
}
