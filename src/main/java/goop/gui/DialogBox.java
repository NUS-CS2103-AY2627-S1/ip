package goop.gui;

import java.io.IOException;
import java.net.URL;
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

/**
 * Represents one chat message and the avatar of its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        URL fxmlFile = DialogBox.class.getResource("/view/DialogBox.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlFile);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog box view.", error);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.75));
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for text entered by the user.
     *
     * @param text User's command text.
     * @param image User avatar image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for a response from Goop.
     *
     * @param text Goop's response text.
     * @param image Goop avatar image.
     * @return Goop dialog box.
     */
    public static DialogBox getGoopDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (text.startsWith("ERROR:")) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }

    /**
     * Places the avatar on the left and adjusts the response bubble style.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
