package bob.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 * Displays one chat message together with its speaker's picture.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads and fills one dialog box from FXML.
     *
     * @param text message to display
     * @param image picture representing the speaker
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load dialog box", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Places Bob's picture on the left and aligns his response to that side.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text user input
     * @param image user's display picture
     * @return dialog ready to add to the chat
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Bob's response.
     *
     * @param text Bob's response
     * @param image Bob's display picture
     * @param isError whether Bob's response contains an error
     * @return dialog ready to add to the chat
     */
    public static DialogBox getBobDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add(isError ? "error-bubble" : "bob-bubble");
        if (isError) {
            // Crop the dark frame in the supplied error image and mask its remaining corners.
            dialogBox.displayPicture.setViewport(new Rectangle2D(45, 35,
                    image.getWidth() - 90, image.getHeight() - 70));
            Rectangle roundedCrop = new Rectangle(99, 99);
            roundedCrop.setArcWidth(18);
            roundedCrop.setArcHeight(18);
            dialogBox.displayPicture.setClip(roundedCrop);
        }
        dialogBox.flip();
        return dialogBox;
    }
}
