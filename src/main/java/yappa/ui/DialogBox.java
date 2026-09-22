package yappa.ui;

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
import yappa.util.DateUtil;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's
 * face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final String STYLE_USER_DIALOG = "user-dialog";
    private static final String STYLE_YAPPA_DIALOG = "yappa-dialog";
    private static final String STYLE_USER_TIMESTAMP = "user-timestamp-label";
    private static final String STYLE_YAPPA_TIMESTAMP = "yappa-timestamp-label";
    private static final String STYLE_ERROR = "dialog-error";

    @FXML
    private Label dialog;
    @FXML
    private Label timestamp;
    @FXML
    private VBox dialogTextContainer;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the specified text and speaker image.
     *
     * @param content      Text to display.
     * @param speakerImage Speaker image to display.
     */
    public DialogBox(String content, Image speakerImage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox.fxml", e);
        }

        dialog.setText(content);
        timestamp.setText(DateUtil.nowAsTimeLabel());
        displayPicture.setImage(speakerImage);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the
     * right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.BOTTOM_LEFT);
    }

    /**
     * Creates a dialog box aligned as a user message.
     *
     * @param text         Text to display.
     * @param speakerImage Speaker image to display.
     * @return User-aligned dialog box.
     */
    public static DialogBox getUserDialog(String text, Image speakerImage) {
        DialogBox dialogBox = new DialogBox(text, speakerImage);

        dialogBox.dialogTextContainer.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add(STYLE_USER_DIALOG);
        dialogBox.timestamp.getStyleClass().add(STYLE_USER_TIMESTAMP);

        return dialogBox;
    }

    /**
     * Creates a dialog box aligned as a Yappa message.
     *
     * @param text         Text to display.
     * @param speakerImage Speaker image to display.
     * @param isError      Whether the message represents an error.
     * @return Yappa-aligned dialog box.
     */
    public static DialogBox getYappaDialog(String text, Image speakerImage, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, speakerImage);
        dialogBox.dialog.getStyleClass().add(STYLE_YAPPA_DIALOG);
        if (isError) {
            dialogBox.dialog.getStyleClass().add(STYLE_ERROR);
        } else {
            dialogBox.dialog.getStyleClass().remove(STYLE_ERROR);
        }
        dialogBox.timestamp.getStyleClass().add(STYLE_YAPPA_TIMESTAMP);
        dialogBox.flip();
        return dialogBox;
    }
}
