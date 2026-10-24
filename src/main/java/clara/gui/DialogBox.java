package clara.gui;

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
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

// AI-assisted JavaFX GUI implementation. See CITATIONS.md [C-013].
// NOTE: AI-assisted GUI polish. See CITATIONS.md [C-019].

/**
 * A dialog box containing a speaker image and message text.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Clara's dialog box.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(24, 24, 24));
    }

    /**
     * Creates a right-aligned dialog for the user.
     *
     * @param text  the user's message
     * @return a user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.displayPicture.setManaged(false);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.limitMessageWidth(0.72);
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Clara.
     *
     * @param text  Clara's response
     * @param image Clara's avatar
     * @return a Clara dialog box
     */
    public static DialogBox getClaraDialog(String text, Image image, boolean isInputError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.limitMessageWidth(0.84);
        if (isInputError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }

    /**
     * Flips the dialog box so Clara's image appears to the left of the reply.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Caps a message bubble at a proportion of the available conversation width.
     *
     * @param widthRatio the largest fraction of this dialog box the bubble may use
     */
    private void limitMessageWidth(double widthRatio) {
        dialog.maxWidthProperty().bind(widthProperty().multiply(widthRatio));
        HBox.setHgrow(dialog, Priority.NEVER);
    }
}
