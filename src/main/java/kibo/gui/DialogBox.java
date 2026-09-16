package kibo.gui;

import java.io.IOException;
import java.net.URL;
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
 * Represents one message and its speaker in Kibo's chat window.
 */
public class DialogBox extends HBox {
    private static final String KIBO_AVATAR_TEXT = "K";
    private static final String USER_AVATAR_TEXT = "You";

    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text, String avatarText) {
        URL dialogResource = MainWindow.class.getResource("/view/DialogBox.fxml");
        if (dialogResource == null) {
            throw new IllegalStateException("DialogBox.fxml could not be found.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(dialogResource);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("DialogBox.fxml could not be loaded.", exception);
        }

        dialog.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a dialog box for text entered by the user.
     *
     * @param text user-entered text.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, USER_AVATAR_TEXT);
    }

    /**
     * Creates a dialog box for a response from Kibo.
     *
     * @param text Kibo's response.
     * @return Kibo dialog box.
     */
    public static DialogBox getKiboDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, KIBO_AVATAR_TEXT);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error reply with a visible label and a distinct bubble style.
     *
     * @param text error explanation and any corrective instructions.
     * @return Kibo error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getKiboDialog("Error\n" + text);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }

    /**
     * Places Kibo's avatar on the left and applies the reply bubble style.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        avatar.getStyleClass().add("kibo-avatar");
    }
}
