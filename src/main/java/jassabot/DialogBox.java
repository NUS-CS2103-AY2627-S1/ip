package jassabot;

import java.io.IOException;
import java.util.Collections;

import jassabot.parser.CommandType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * Displays a chat message with a scalable garden or person avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private StackPane displayPicture;
    @FXML
    private SVGPath avatarIcon;

    private DialogBox(String text, boolean isBot) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialog.setText(text);
        if (!isBot) {
            // Keep the user distinct from JassaBot's sprout without relying on emoji fonts.
            avatarIcon.setContent("M12 3 A4 4 0 1 1 12 11 A4 4 0 1 1 12 3 "
                    + "M4 21 V19 C4 11 20 11 20 19 V21 Z");
            displayPicture.getStyleClass().add("user-avatar");
        }
        displayPicture.setAccessibleText(isBot ? "JassaBot" : "You");
    }

    /**
     * Places the bot avatar on the left and its response on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Applies a subtle botanical color for successful task changes.
     */
    private void changeDialogStyle(CommandType commandType) {
        switch (commandType) {
            case TODO, DEADLINE, EVENT -> dialog.getStyleClass().add("add-label");
            case MARK, UNMARK -> dialog.getStyleClass().add("marked-label");
            case DELETE -> dialog.getStyleClass().add("delete-label");
            default -> {
                // Other responses retain the default reply style.
            }
        }
    }

    /**
     * Creates a bot reply with its portrait on the left and a command-specific color.
     */
    public static DialogBox getJassaBotDialog(String text, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, true);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Creates a user message with its portrait on the right.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, false);
    }
}
