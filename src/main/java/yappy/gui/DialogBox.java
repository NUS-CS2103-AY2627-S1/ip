package yappy.gui;

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

/**
 * Represents a dialog box containing the speaker's image and message.
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
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().subtract(96));
        HBox.setHgrow(dialog, Priority.ALWAYS);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for a message entered by the user.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for a response from Yappy.
     */
    public static DialogBox getYappyDialog(String text, Image image, String commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Places Yappy's image on the left and points the speech bubble towards it.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Tints Yappy's speech bubble according to the kind of command handled.
     */
    private void changeDialogStyle(String commandType) {
        if (commandType == null) {
            return;
        }

        switch (commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "ChangeMarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            case "ReminderCommand":
                dialog.getStyleClass().add("reminder-label");
                break;
            case "ErrorCommand":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                break;
        }
    }
}
