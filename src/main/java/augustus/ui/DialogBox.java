package augustus.ui;

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
 * Represents a dialog box containing a message and an avatar.
 */
public class DialogBox extends HBox {

    private static final double AVATAR_SIZE = 72;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the given text and image.
     *
     * @param text Text to display in the dialog box.
     * @param img Image to display beside the text.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);

        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);

        Circle clip = new Circle(
                AVATAR_SIZE / 2,
                AVATAR_SIZE / 2,
                AVATAR_SIZE / 2
        );

        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box so that the image is on the left.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(this.getChildren());

        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text text entered by the user
     * @param img user's image
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box for Augustus.
     *
     * @param text response from Augustus
     * @param img Augustus's image
     * @return Augustus dialog box
     */
    public static DialogBox getAugustusDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error dialog box for Augustus.
     *
     * @param text error message from Augustus
     * @param img Augustus's image
     * @return Augustus error dialog box
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();

        String errorMessage = text.replaceFirst("^ERROR:\\s*", "");

        dialogBox.dialog.setText("⚠ ERROR\n" + errorMessage);

        dialogBox.dialog.setStyle(
                "-fx-background-color: #ffd6d6;"
                        + "-fx-text-fill: #8b1a1a;"
                        + "-fx-font-weight: bold;"
                        + "-fx-border-color: #c94c4c;"
                        + "-fx-border-width: 1.5;"
                        + "-fx-border-radius: 12;"
                        + "-fx-background-radius: 12;"
        );

        return dialogBox;
    }

    /**
     * Creates a welcome dialog box for Augustus.
     *
     * @param text welcome message
     * @param img Augustus's image
     * @return welcome dialog box
     */
    public static DialogBox getWelcomeDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();

        dialogBox.dialog.setStyle(
                "-fx-background-color: #dbeafe;"
                        + "-fx-text-fill: #1e3a5f;"
                        + "-fx-border-color: #6ea8d7;"
                        + "-fx-border-width: 1.5;"
                        + "-fx-border-radius: 12;"
                        + "-fx-background-radius: 12;"
                        + "-fx-padding: 10 14 10 14;"
        );

        return dialogBox;
    }
}
