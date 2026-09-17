package nori.ui;

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
 * Displays one user or Nori message in the conversation.
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
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a dialog displayed on the user's side.
     *
     * @param text Message to display.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "You");
    }

    /**
     * Creates a dialog displayed on Nori's side.
     *
     * @param text Message to display.
     * @return Nori dialog box.
     */
    public static DialogBox getNoriDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "Nori");
        dialogBox.styleAsNoriReply();
        return dialogBox;
    }

    /**
     * Creates an emphasized Nori reply that explains invalid input.
     *
     * @param text Error guidance to display.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getNoriDialog(text);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        dialogBox.avatar.getStyleClass().add("error-avatar");
        return dialogBox;
    }

    /**
     * Places Nori's avatar on the left and applies the reply style.
     */
    private void styleAsNoriReply() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-bubble");
    }
}
