package pebby;

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
 * Represents a compact message in the conversation between a user and Pebby.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(24, 24, 24));
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78));
    }

    /**
     * Styles the dialog as a Pebby response with the icon on the left.
     */
    private void formatAsPebbyResponse(boolean isError) {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(isError ? "error-label" : "reply-label");
    }

    /**
     * Styles the dialog as a compact message sent by the user.
     */
    private void formatAsUserMessage() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
        setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().add("user-label");
    }

    /**
     * Creates a right-aligned dialog that displays text sent by the user.
     *
     * @param text text sent by the user
     * @param image image associated with the user
     * @return the formatted user dialog
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.formatAsUserMessage();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog that displays Pebby's response.
     *
     * @param text response from Pebby
     * @param image Pebby's profile image
     * @return the formatted Pebby dialog
     */
    public static DialogBox getPebbyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.formatAsPebbyResponse(isErrorMessage(text));
        return dialogBox;
    }

    /** Returns whether a response is an input error that should be emphasized. */
    static boolean isErrorMessage(String response) {
        return response.startsWith("Invalid") || response.startsWith("Unknown") || response.startsWith("Error");
    }
}
