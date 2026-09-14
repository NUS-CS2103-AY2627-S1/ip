package toot.ui;

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
 * Displays one chat message beside an avatar.
 */
public class DialogBox extends HBox {
    private static final String TOOT_AVATAR = "🐣";
    private static final String USER_AVATAR = "🙂";

    @FXML
    private Label avatar;
    @FXML
    private Label message;

    private DialogBox(String text, String avatarText) {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout.", exception);
        }
        message.setText(text);
        avatar.setText(avatarText);
    }

    /**
     * Creates a right-aligned dialog for a user command.
     *
     * @param text User command to display.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, USER_AVATAR);
    }

    /**
     * Creates a left-aligned dialog for Toot's response.
     *
     * @param text Toot response to display.
     * @return Toot dialog box.
     */
    public static DialogBox getTootDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, TOOT_AVATAR);
        dialogBox.flip();
        if (text.startsWith("Oh crumbs!")) {
            dialogBox.message.getStyleClass().add("error-message");
        }
        return dialogBox;
    }

    /**
     * Places the avatar on the left and applies Toot's reply styling.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("toot-dialog");
        message.getStyleClass().add("toot-message");
    }
}
