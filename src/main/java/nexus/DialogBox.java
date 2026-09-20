package nexus;

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
 * Represents a message together with an image of its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /** Loads the dialog layout and fills it with the supplied content. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(21.0, 21.0, 21.0));
    }

    /** Flips the dialog so the image appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text message to display.
     * @param image image representing the user.
     * @return the configured user dialog
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a Nexus response.
     *
     * @param text message to display.
     * @param image image representing Nexus.
     * @return the configured Nexus dialog
     */
    public static DialogBox getNexusDialog(String text, Image image) {
        return getNexusDialog(text, image, false);
    }

    /**
     * Creates a left-aligned Nexus dialog with optional error emphasis.
     *
     * @param text message to display.
     * @param image image representing Nexus.
     * @param isError whether the dialog represents an error.
     * @return the configured Nexus dialog
     */
    public static DialogBox getNexusDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("nexus-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }
}
