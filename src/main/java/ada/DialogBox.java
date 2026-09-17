package ada;

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
 * Represents a dialog box containing a speaker image and message.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainWindow.class.getResource(
                            "/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box so that Ada's image appears on the left.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());

        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Applies a style based on the command entered by the user.
     *
     * @param commandType command entered by the user
     */
    private void changeDialogStyle(String commandType) {
        if (commandType == null) {
            return;
        }

        switch (commandType) {
            case "todo":
            case "deadline":
            case "event":
                dialog.getStyleClass().add("add-label");
                break;
            case "mark":
            case "unmark":
                dialog.getStyleClass().add("marked-label");
                break;
            case "delete":
                dialog.getStyleClass().add("delete-label");
                break;
            case "error":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // Keep the default reply style.
        }
    }

    /**
     * Creates a dialog box for Ada's response.
     *
     * @param text response from Ada
     * @param image image representing Ada
     * @param commandType command used to select the response style
     * @return dialog box containing Ada's response
     */
    public static DialogBox getAdaDialog(
            String text, Image image, String commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Creates a dialog box for the user's input.
     *
     * @param text input entered by the user
     * @param image image representing the user
     * @return dialog box containing the user's input
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }
}
