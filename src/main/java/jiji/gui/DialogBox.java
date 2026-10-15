package jiji.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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
 * Represents a dialog box consisting of an ImageView to represent the speaker's avatar
 * and a label containing text from the speaker.
 * <p>
 * Adapted and enhanced from the SE-EDU JavaFX Tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
 */
public class DialogBox extends HBox {

    private static final double AVATAR_RADIUS = 21.0;
    private static final double HORIZONTAL_CHROME_WIDTH = 80.0;
    private static final double MIN_DIALOG_WIDTH = 100.0;

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

        // Circular clipping for avatar image
        Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
        displayPicture.setClip(clip);

        // Dynamically bind maximum dialog width to enable responsive wrapping
        dialog.maxWidthProperty().bind(Bindings.max(MIN_DIALOG_WIDTH,
                this.widthProperty().subtract(HORIZONTAL_CHROME_WIDTH)));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     *
     * @param isError True if the response represents an error message, false otherwise.
     */
    private void flip(boolean isError) {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        if (isError) {
            dialog.getStyleClass().add("error-label");
        }
    }

    /**
     * Creates a user dialog box aligned to the right.
     *
     * @param text The user message text.
     * @param img The user avatar image.
     * @return A new DialogBox for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        return db;
    }

    /**
     * Creates a Jiji dialog box flipped and aligned to the left with error styling support.
     *
     * @param text Jiji's response text.
     * @param img Jiji's avatar image.
     * @param isError True if the response is an error message, false otherwise.
     * @return A new flipped DialogBox for Jiji.
     */
    public static DialogBox getJijiDialog(String text, Image img, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip(isError);
        return db;
    }

    /**
     * Creates a standard Jiji dialog box flipped and aligned to the left.
     *
     * @param text Jiji's response text.
     * @param img Jiji's avatar image.
     * @return A new flipped DialogBox for Jiji.
     */
    public static DialogBox getJijiDialog(String text, Image img) {
        return getJijiDialog(text, img, false);
    }
}
