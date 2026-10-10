package zsiggy;

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
 * Represents a dialog box containing a profile image and text.
 */
public class DialogBox extends HBox {

    private static final String USER_DIALOG_STYLE =
            "-fx-background-color: #DCEEFF;"
                    + "-fx-background-radius: 12;"
                    + "-fx-padding: 8 12 8 12;"
                    + "-fx-font-size: 14px;";

    private static final String ZSIGGY_DIALOG_STYLE =
            "-fx-background-color: #E3EDE4;"
                    + "-fx-background-radius: 12;"
                    + "-fx-padding: 8 12 8 12;"
                    + "-fx-font-size: 14px;";

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box.
     *
     * @param text The text to display.
     * @param image The profile image to display.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));

            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(80));
        dialog.setStyle(USER_DIALOG_STYLE);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box so that Zsiggy's message appears on the left.
     */
    private void flip() {
        ObservableList<Node> nodes =
                FXCollections.observableArrayList(this.getChildren());

        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.CENTER_LEFT);
        dialog.setStyle(ZSIGGY_DIALOG_STYLE);
    }

    /**
     * Creates a user dialog box.
     *
     * @param text The user's message.
     * @param image The user's profile image.
     * @return A user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a Zsiggy dialog box.
     *
     * @param text Zsiggy's message.
     * @param image Zsiggy's profile image.
     * @return A Zsiggy dialog box.
     */
    public static DialogBox getZsiggyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (text.startsWith("Oi.")) {
            dialogBox.dialog.setStyle(ZSIGGY_DIALOG_STYLE + "-fx-background-color: #FFE0DC;");
        }
        return dialogBox;
    }
}
