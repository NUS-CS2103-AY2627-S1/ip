package potatobot.ui.gui;

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
 * Represents one message and its speaker's display picture in the chat.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_CORNER_DIAMETER = 24;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the specified message and image.
     *
     * @param text  Message displayed in the dialog box.
     * @param image Display picture belonging to the speaker.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog aligned for a message sent by the user.
     *
     * @param text  User's message.
     * @param image User's display picture.
     * @return Dialog box for the user's message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.roundUserPicture();
        return dialogBox;
    }

    /**
     * Center-crops the user's picture to a square and rounds its corners.
     * The surrounding container carries the shadow so the image clip does not cut
     * it off.
     */
    private void roundUserPicture() {
        Image image = displayPicture.getImage();
        double side = Math.min(image.getWidth(), image.getHeight());
        displayPicture.setViewport(new Rectangle2D(
                (image.getWidth() - side) / 2, (image.getHeight() - side) / 2, side, side));

        Rectangle clip = new Rectangle(displayPicture.getFitWidth(), displayPicture.getFitHeight());
        clip.setArcWidth(AVATAR_CORNER_DIAMETER);
        clip.setArcHeight(AVATAR_CORNER_DIAMETER);
        displayPicture.setClip(clip);
    }

    /**
     * Creates a dialog aligned for a reply sent by PotatoBot.
     *
     * @param text  PotatoBot's reply.
     * @param image PotatoBot's display picture.
     * @param isError Whether the reply describes a failure and needs error styling.
     * @return Dialog box for PotatoBot's reply.
     */
    public static DialogBox getBotDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }

    /**
     * Reverses the child order and alignment to place PotatoBot's image on the
     * left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("bot-label");
    }
}
