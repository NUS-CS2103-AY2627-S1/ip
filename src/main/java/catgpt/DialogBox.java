package catgpt;

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
 * Represents one message and its speaker in the graphical conversation.
 */
public class DialogBox extends HBox {
    private static final String USER_AVATAR = "YOU";
    private static final String CAT_AVATAR = "😹";

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
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog layout", error);
        }

        dialog.setText(text);
        dialog.setAccessibleText(text);
        avatar.setText(avatarText);
        getStyleClass().add("dialog-box");
    }

    /**
     * Creates a right-aligned message from the user.
     *
     * @param text User's command.
     * @return Dialog box containing the command.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_AVATAR);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned response from CatGPT.
     *
     * @param text CatGPT's response.
     * @return Dialog box containing the response.
     */
    public static DialogBox getCatDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, CAT_AVATAR);
        dialogBox.flip();
        dialogBox.getStyleClass().add("cat-dialog");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
