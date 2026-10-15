package peter.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box containing a speaker image and message text.
 */
public class DialogBox extends HBox {
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
        dialog.maxWidthProperty().bind(widthProperty().subtract(76));
        displayPicture.setClip(new Circle(18, 18, 18));
    }

    /**
     * Applies the compact, right-aligned style used for user commands.
     */
    private void styleAsUser() {
        setAlignment(Pos.TOP_RIGHT);
        getStyleClass().add("user-dialog");
        dialog.getStyleClass().add("user-message");
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
    }

    /**
     * Applies the left-aligned assistant style, with optional error emphasis.
     *
     * @param isError Whether this response describes invalid input.
     */
    private void styleAsPeter(boolean isError) {
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("peter-dialog");
        dialog.getStyleClass().add(isError ? "error-message" : "peter-message");
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text Message text.
     * @return A right-aligned user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.styleAsUser();
        return dialogBox;
    }

    /**
     * Creates a dialog box for Peter's response.
     *
     * @param text Response text.
     * @param img Peter image.
     * @return A left-aligned Peter dialog box.
     */
    public static DialogBox getPeterDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.styleAsPeter(false);
        return dialogBox;
    }

    /**
     * Creates an emphasized dialog box for an invalid command.
     *
     * @param text Error text.
     * @param img Peter's image.
     * @return A left-aligned error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.styleAsPeter(true);
        return dialogBox;
    }
}
