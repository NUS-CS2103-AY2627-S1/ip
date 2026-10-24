package echo.gui;

import java.io.IOException;
import java.util.Collections;

import echo.ui.Ui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A dialog bubble consisting of an avatar and a label of text from one
 * speaker (the user or Echo).
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Applies a CSS style class matching the command word Echo just ran,
     * so the bubble's colour hints at what happened (added, marked, or
     * deleted a task). Unrecognised or non-mutating commands (e.g.
     * {@code list}, {@code find}) get no extra style.
     */
    private void changeDialogStyle(String commandWord) {
        switch (commandWord) {
            case "todo":
            case "deadline":
            case "event":
                dialog.getStyleClass().add("add-label");
                break;
            case "mark":
            case "unmark":
                dialog.getStyleClass().add("marked-label");
                break;
            case "delete":
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                // No extra styling for list/find/on/bye/unrecognised commands.
        }
    }

    /**
     * Flips the dialog box so the avatar is on the left and text on the
     * right, and applies the reply styling. Used for Echo's own replies.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for something the user typed.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a flipped, command-styled dialog box for one of Echo's
     * replies. An error reply (one starting with {@link Ui#ERROR_PREFIX})
     * always gets the error styling, regardless of which command word
     * produced it -- otherwise, e.g. a failed {@code todo} would be
     * coloured as if it had succeeded.
     *
     * @param text Echo's response text.
     * @param img Echo's avatar.
     * @param commandWord The command word that produced this response
     *     (e.g. {@code "todo"}), used only to pick a highlight colour.
     */
    public static DialogBox getEchoDialog(String text, Image img, String commandWord) {
        var db = new DialogBox(text, img);
        db.flip();
        if (text.startsWith(Ui.ERROR_PREFIX)) {
            db.dialog.getStyleClass().add("error-label");
        } else {
            db.changeDialogStyle(commandWord);
        }
        return db;
    }
}
