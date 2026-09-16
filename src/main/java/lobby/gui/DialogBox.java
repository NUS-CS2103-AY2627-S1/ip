package lobby.gui;

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

/**
 * Represents one message and its speaker inside the chat window.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text, String avatarText) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the dialog box layout.", e);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param text message to display.
     * @return the user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "YOU");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned response written by Lobby.
     *
     * @param text response to display.
     * @return the Lobby dialog box
     */
    public static DialogBox getLobbyDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "L");
        dialogBox.flip();
        dialogBox.getStyleClass().add("lobby-dialog");
        return dialogBox;
    }

    /**
     * Creates a visibly labeled error response that does not rely on color alone.
     *
     * @param text explanation of the error and how to correct it.
     * @return the error dialog box
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getLobbyDialog("Error" + System.lineSeparator() + text);
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
