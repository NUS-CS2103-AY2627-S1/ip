package aster.gui;

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
 * Represents one speaker's message in the conversation view.
 */
@SuppressWarnings("unused")
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param text message text.
     * @param image speaker image.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned Aster response.
     *
     * @param text response text.
     * @param image speaker image.
     * @return Aster dialog box.
     */
    public static DialogBox getAsterDialog(String text, Image image) {
        return getAsterDialog(text, image, false);
    }

    /** Creates an Aster response with optional error styling.
     *
     * @param text response text.
     * @param image speaker image.
     * @param isError whether the response reports an invalid command.
     * @return Aster dialog box.
     */
    public static DialogBox getAsterDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        ObservableList<Node> children = FXCollections.observableArrayList(dialogBox.getChildren());
        Collections.reverse(children);
        dialogBox.getChildren().setAll(children);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.getStyleClass().add("reply-label");
        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }
}
