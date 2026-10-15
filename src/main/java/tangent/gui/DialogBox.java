package tangent.gui;

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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label speakerName;
    @FXML
    private ImageView displayPicture;
    @FXML
    private VBox messageContent;

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
        dialog.setWrapText(true);
        dialog.setMinWidth(0);
        dialog.setMaxWidth(560);
        messageContent.setMaxWidth(560);
        messageContent.getStyleClass().add("user-bubble");
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(18, 18, 18));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.BOTTOM_LEFT);
        getStyleClass().add("bot-dialog");
        messageContent.getStyleClass().remove("user-bubble");
        messageContent.getStyleClass().add("bot-bubble");
        speakerName.setText("Tangent");
    }

    /**
     * Returns the user's version of the dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.setAlignment(Pos.BOTTOM_RIGHT);
        dialogBox.speakerName.setText("You");
        return dialogBox;
    }

    /**
     * Returns Tangent's dialog box.
     */
    public static DialogBox getTangentDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /** Returns Tangent's dialog box, optionally styled as an error. */
    public static DialogBox getTangentDialog(String text, Image img, boolean isError) {
        DialogBox dialogBox = getTangentDialog(text, img);
        if (isError) {
            dialogBox.messageContent.getStyleClass().add("error-bubble");
        }
        return dialogBox;
    }
}
