package amy;
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

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private static final String LIGHT_DIALOG_STYLESHEET =
            DialogBox.class.getResource("/ css/dialog-box.css").toExternalForm();
    private static final String DARK_DIALOG_STYLESHEET =
            DialogBox.class.getResource("/ css/dark-dialog-box.css").toExternalForm();
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
        getStylesheets().add(LIGHT_DIALOG_STYLESHEET);
        getStyleClass().add("user-message");
        dialog.getStyleClass().add("user-label");
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().remove("user-message");
        getStyleClass().add("amy-message");
        dialog.getStyleClass().remove("user-label");
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getAmyDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Creates an Amy response dialog, optionally styled as an error.
     *
     * @param text the response text
     * @param img the response image
     * @param isError whether the response represents an error
     * @return the styled Amy response dialog
     */
    public static DialogBox getAmyDialog(String text, Image img, boolean isError) {
        DialogBox dialogBox = getAmyDialog(text, img);
        if (isError) {
            dialogBox.getStyleClass().add("error-message");
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }

    /** Applies or removes the dark dialog theme for this message. */
    public void setDarkMode(boolean isDarkMode) {
        String stylesheet = isDarkMode ? DARK_DIALOG_STYLESHEET : LIGHT_DIALOG_STYLESHEET;
        getStylesheets().setAll(stylesheet);
    }
}
