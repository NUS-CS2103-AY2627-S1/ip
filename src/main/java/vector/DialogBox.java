package vector;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
    }

    /**
     * Aligns the dialog box such that the text is on the left.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a user dialog box.
     *
     * @param text The text to display.
     * @return A dialog box representing the user.
     */
    public static DialogBox getUserDialog(String text) {
        var db = new DialogBox(text);
        db.getStyleClass().add("user-bubble");
        return db;
    }

    /**
     * Changes the dialog style based on the command type.
     *
     * @param commandType The type of the command.
     */
    private void changeDialogStyle(String commandType) {
        switch (commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "MarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            case "Error":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // Do nothing
        }
    }

    /**
     * Creates a Vector dialog box.
     *
     * @param text The text to display.
     * @param commandType The command type to determine styling.
     * @return A dialog box representing Vector.
     */
    public static DialogBox getVectorDialog(String text, String commandType) {
        var db = new DialogBox(text);
        db.flip();
        db.getStyleClass().add("vector-bubble");
        db.changeDialogStyle(commandType);
        return db;
    }
}
