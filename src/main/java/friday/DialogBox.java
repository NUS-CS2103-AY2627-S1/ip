package friday;

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
import javafx.scene.shape.Rectangle;

/**
 * Represents one message bubble in the chat window.
 */
public class DialogBox extends HBox {
    private static final double PROFILE_PICTURE_CORNER_RADIUS = 12;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml could not be loaded.", e);
        }

        dialog.setText(text);
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
        } else {
            displayPicture.setImage(image);
            Rectangle clip = new Rectangle(displayPicture.getFitWidth(), displayPicture.getFitHeight());
            clip.setArcWidth(PROFILE_PICTURE_CORNER_RADIUS);
            clip.setArcHeight(PROFILE_PICTURE_CORNER_RADIUS);
            displayPicture.setClip(clip);
        }
    }

    /**
     * Creates a dialog box for user input.
     *
     * @param text message text
     * @return dialog box for the user
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /**
     * Creates a dialog box for Friday's response.
     *
     * @param text message text
     * @param image display picture
     * @param isError whether the response reports an error
     * @return dialog box for Friday
     */
    public static DialogBox getFridayDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.formatFridayDialog(isError);
        return dialogBox;
    }

    /**
     * Creates a non-error dialog box for Friday.
     *
     * @param text message text
     * @param image display picture
     * @return dialog box for Friday
     */
    public static DialogBox getFridayDialog(String text, Image image) {
        return getFridayDialog(text, image, false);
    }

    /**
     * Creates a welcome bubble with fixed-width lettering for the ASCII banner.
     *
     * @param text welcome message and optional reminders
     * @param image Friday's profile picture
     * @return welcome dialog box
     */
    public static DialogBox getWelcomeDialog(String text, Image image) {
        DialogBox dialogBox = getFridayDialog(text, image);
        dialogBox.dialog.getStyleClass().add("welcome-label");
        return dialogBox;
    }

    private void formatFridayDialog(boolean isError) {
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        if (isError) {
            dialog.getStyleClass().add("error-label");
        }
    }
}
