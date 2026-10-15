package snoopy.gui;

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

/**
 * Represents a chat message with an optional sender display picture.
 */
public class DialogBox extends HBox {

    private static final double DISPLAY_PICTURE_RADIUS = 28.0;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the specified text and display picture.
     *
     * @param text Text shown in the dialog box.
     * @param image Sender's display picture.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(
                DISPLAY_PICTURE_RADIUS, DISPLAY_PICTURE_RADIUS, DISPLAY_PICTURE_RADIUS));
    }

    /**
     * Creates a text-only dialog box without a display picture.
     *
     * @param text Text shown in the dialog box.
     */
    private DialogBox(String text) {
        this(text, null);
        getChildren().remove(displayPicture);
    }

    /**
     * Aligns a Snoopy reply to the left and applies its reply style.
     *
     * @param replyStyleClass CSS class that controls the reply's appearance.
     */
    private void flip(String replyStyleClass) {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(replyStyleClass);
    }

    /**
     * Creates a text-only dialog box for a command sent by the user.
     *
     * @param text Command sent by the user.
     * @return Dialog box aligned for the user.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Creates a dialog box for a response sent by Snoopy.
     *
     * @param text Text sent by Snoopy.
     * @param image Snoopy's display picture.
     * @return Dialog box aligned for Snoopy.
     */
    public static DialogBox getSnoopyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip("reply-label");
        return dialogBox;
    }

    /**
     * Creates a visually highlighted dialog box for an error response.
     *
     * @param text Error text sent by Snoopy.
     * @param image Snoopy's display picture.
     * @return Dialog box styled to draw attention to the error.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip("error-label");
        return dialogBox;
    }
}
