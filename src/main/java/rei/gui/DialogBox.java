package rei.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Displays one message beside a compact speaker avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
        dialog.maxWidthProperty().bind(Bindings.max(0, widthProperty().subtract(64)));
    }

    /** Creates a right-aligned message entered by the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "YOU");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /** Creates a left-aligned response from Rei. */
    public static DialogBox getReiDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "REI");
        dialogBox.getStyleClass().add("rei-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /** Creates an error response with a visible label as well as a distinct color. */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getReiDialog("Please try again\n" + text);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /** Places the avatar on the left and the message on the right. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
