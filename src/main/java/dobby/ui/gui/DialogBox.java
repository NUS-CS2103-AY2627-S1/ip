package dobby.ui.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 * Displays a chat message, optionally beside the speaker's avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a right-aligned chat message.
     *
     * @param message Message to display.
     * @param image Avatar representing the speaker, or {@code null} to omit an avatar.
     */
    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog layout", e);
        }
        dialog.setText(message.stripTrailing());
        if (image == null) {
            getChildren().remove(displayPicture);
        } else {
            setDisplayPicture(image);
        }
    }

    /** Crops and clips a speaker image into the circular portrait used by the chat UI. */
    private void setDisplayPicture(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        double clipRadius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;

        displayPicture.setImage(image);
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setClip(new Circle(clipRadius, clipRadius, clipRadius));
    }

    /** Flips the dialog so Dobby's avatar appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        Polygon replyTail = new Polygon(0.0, 8.0, 10.0, 0.0, 10.0, 16.0);
        replyTail.getStyleClass().add("reply-tail");
        getChildren().add(1, replyTail);
        HBox.setMargin(replyTail, new Insets(13.0, -1.0, 0.0, 4.0));
        HBox.setMargin(dialog, new Insets(0.0, 7.0, 0.0, -1.0));
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog for a user message.
     *
     * @param message Message to display.
     * @return right-aligned user dialog.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, null);
        dialogBox.setPadding(new Insets(7.0, 0.0, 7.0, 10.0));
        HBox.setMargin(dialogBox.dialog, Insets.EMPTY);
        return dialogBox;
    }

    /**
     * Creates a dialog for a Dobby response.
     *
     * @param message Message to display.
     * @param image Dobby avatar.
     * @return left-aligned Dobby dialog.
     */
    public static DialogBox getDobbyDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }
}
