package sumo.gui;

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

/** Represents one chat message and the avatar of its speaker. */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML = "/view/DialogBox.fxml";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the dialog box layout.", exception);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(82).multiply(0.85));
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog aligned for a user message.
     *
     * @param text message to display.
     * @param image user's avatar, or {@code null} if it is not available.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog aligned for a Sumo response.
     *
     * @param text message to display.
     * @param image Sumo's avatar, or {@code null} if it is not available.
     * @return Sumo dialog box.
     */
    public static DialogBox getSumoDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("assistant-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /** Places the avatar on the left for a Sumo response. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
