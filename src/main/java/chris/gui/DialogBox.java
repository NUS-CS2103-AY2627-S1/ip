package chris.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one chat message and its speaker marker. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }
        dialog.setText(text);
        // Keep long task lists readable without making short messages fill the row.
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.78).subtract(36));
    }

    /** Returns a dialog styled for a message entered by the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("You");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /** Returns a dialog styled for a response from Chris. */
    public static DialogBox getChrisDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("C");
        dialogBox.getStyleClass().add("chris-dialog");
        dialogBox.moveAvatarToLeft();
        return dialogBox;
    }

    /** Returns a wider, aligned response for task lists and command help. */
    public static DialogBox getReferenceDialog(String text) {
        DialogBox dialogBox = getChrisDialog(text);
        dialogBox.getStyleClass().add("reference-dialog");
        dialogBox.dialog.maxWidthProperty().unbind();
        dialogBox.dialog.maxWidthProperty().bind(dialogBox.widthProperty().subtract(44));
        return dialogBox;
    }

    /** Returns a visually distinct response for invalid commands or storage errors. */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getChrisDialog(text);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    private void moveAvatarToLeft() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
