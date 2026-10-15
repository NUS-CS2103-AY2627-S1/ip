import java.io.IOException;
import java.util.Collections;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
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
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Displays the user's input in the GUI as a DialogBox.
     *
     * @param text the user's input.
     * @param img the user's profile image.
     * @return the response displayed inside a DialogBox.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    private void changeDialogStyle(String commandType) {
        switch(commandType) {
            case "mark":
                dialog.getStyleClass().add("add-label");
                break;
            case "task":
                dialog.getStyleClass().add("marked-label");
                break;
            case "delete":
                dialog.getStyleClass().add("add-label");
                break;
            case "list":
                dialog.getStyleClass().add("list-label");
                break;
            case "unmark":
                dialog.getStyleClass().add("marked-label");
                break;
            case "greeting":
                dialog.getStyleClass().add("enter-exit-label");
                break;
            case "goodbye":
                dialog.getStyleClass().add("enter-exit-label");
                break;
            case "error":
                dialog.getStyleClass().add("error-label");
                break;
            case "alreadyDone":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // Do nothing
        }
    }

    /**
     * Displays the chatbot's responses in the GUI as a DialogBox.
     *
     * @param text the chatbot's response.
     * @param img the chatbot's profile image.
     * @param commandType the type of command from the user's input.
     * @return the response displayed inside a DialogBox.
     */
    public static DialogBox getSunnyDialog(String text, Image img, String commandType) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType);
        return db;
    }
}
