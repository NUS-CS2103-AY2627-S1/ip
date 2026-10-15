package nova.gui;

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
 * Represents one message and its speaker avatar in the chat display.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        assert dialog != null : "FXML loader must inject the dialog label";
        assert avatar != null : "FXML loader must inject the avatar label";

        dialog.setText(text);
    }

    /**
     * Returns a dialog styled for a user message.
     *
     * @param text user message
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("You");
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a dialog styled for a Nova response.
     *
     * @param text Nova response
     * @return Nova dialog box
     */
    public static DialogBox getNovaDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText("N");
        dialogBox.getStyleClass().add("nova-dialog");
        dialogBox.placeAvatarOnLeft();
        return dialogBox;
    }

    /**
     * Places the avatar on the left for Nova responses.
     */
    private void placeAvatarOnLeft() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
