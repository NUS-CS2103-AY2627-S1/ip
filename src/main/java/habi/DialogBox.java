package habi;

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

/** Represents one user or HABI message in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private Label avatar;

    private DialogBox(String text, String avatarText) {
        FXMLLoader loader = new FXMLLoader(
                DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load dialog layout.", exception);
        }
        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a right-aligned dialog for a user command.
     *
     * @param text Command entered by the user.
     * @return Styled user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "You");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a HABI response.
     *
     * @param text Response produced by HABI.
     * @return Styled HABI dialog.
     */
    public static DialogBox getHabiDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "H");
        dialogBox.flip();
        dialogBox.getStyleClass().add("habi-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for an error response from HABI.
     *
     * @param text Error response produced by HABI.
     * @return Styled error dialog.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getHabiDialog(text);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
