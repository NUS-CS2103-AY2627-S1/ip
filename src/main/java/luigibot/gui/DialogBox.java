package luigibot.gui;

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
 * Displays one chatbot message beside its speaker's image.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", exception);
        }

        this.dialog.setText(text);
        this.displayPicture.setImage(image);
    }

    /**
     * Creates a dialog aligned for a message from the user.
     *
     * @param text message displayed in the dialog.
     * @param image image representing the user.
     * @return dialog aligned for the user.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog aligned for a message from LuigiBot.
     *
     * @param text message displayed in the dialog.
     * @param image image representing LuigiBot.
     * @return dialog aligned for LuigiBot.
     */
    public static DialogBox getLuigiDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("luigi-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Places the speaker image on the left side of the message.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
    }
}
