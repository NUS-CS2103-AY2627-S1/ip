package lumine.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Represents one chat message with the speaker's text and avatar.
 */
public class DialogBox extends VBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private HBox messageRow;
    @FXML
    private Region topDivider;
    @FXML
    private Region bottomDivider;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog box for a message from the user.
     *
     * @param text message text.
     * @param image avatar image.
     * @return a user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box for a message from Lumine.
     *
     * @param text message text.
     * @param image avatar image.
     * @return a Lumine dialog box.
     */
    public static DialogBox getLumineDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.formatAsLumineReply();
        return dialogBox;
    }

    /**
     * Places Lumine's avatar on the left and displays dividers around the reply.
     */
    private void formatAsLumineReply() {
        messageRow.getChildren().setAll(displayPicture, dialog);
        messageRow.setAlignment(Pos.TOP_LEFT);
        displayPicture.setClip(null);
        topDivider.setVisible(true);
        topDivider.setManaged(true);
        bottomDivider.setVisible(true);
        bottomDivider.setManaged(true);
        dialog.getStyleClass().add("reply-label");
    }
}
