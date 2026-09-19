package yuno.ui;

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
import javafx.scene.shape.Circle;

/**
 * Displays a chatbot message together with the sender's profile image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label text;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a right-aligned dialog box with the specified message and profile image.
     *
     * @param message Message to display.
     * @param image Profile image of the message sender.
     */
    private DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", exception);
        }

        text.setText(message);
        setDisplayPicture(image);
    }

    /**
     * Displays a top-centered square crop of the profile image in a circular frame.
     *
     * @param image Profile image to display.
     */
    private void setDisplayPicture(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        displayPicture.setImage(image);
        displayPicture.setViewport(new Rectangle2D(cropX, 0, cropSize, cropSize));
        displayPicture.setClip(new Circle(21, 21, 21));
    }

    /**
     * Reverses the child order and bottom-left-aligns this dialog box.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.BOTTOM_LEFT);
    }

    /**
     * Returns a right-aligned dialog box for a user message.
     *
     * @param message User message to display.
     * @param image User's profile image.
     * @return Dialog box containing the user message.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a left-aligned dialog box for a Yuno message.
     *
     * @param message Yuno message to display.
     * @param image Yuno's profile image.
     * @return Dialog box containing the Yuno message.
     */
    public static DialogBox getYunoDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("yuno-dialog");
        dialogBox.flip();
        return dialogBox;
    }
}
