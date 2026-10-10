package jasper.gui;

import java.io.IOException;

import jasper.command.CommandResult;
import jasper.command.CommandType;
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
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an image view and a text label.
 */
public class DialogBox extends HBox {
    /** Label containing the text of the dialog */
    @FXML
    private Label dialog;
    /** Image view displaying the avatar of the speaker */
    @FXML
    private ImageView displayPic;

    /**
     * Constructs a dialog box with the specified text and display image.
     *
     * @param text Text content of the dialog.
     * @param img Image avatar of the speaker.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            // Note: Use setController() here instead of fx:controller in the fxml file
            // as there is no no-argument constructor for this class
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPic.setImage(img);
        displayPic.setClip(new Circle(20, 20, 20));
    }

    /**
     * Returns a dialog box representing the user's message.
     *
     * @param text Text inputted by the user.
     * @param img Image representing the user's avatar.
     * @return Dialog box representing the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Returns a dialog box representing Jasper's response.
     *
     * @param result Command result to be displayed.
     * @param img Image representing Jasper's avatar.
     * @return Dialog box representing Jasper.
     */
    public static DialogBox getJasperDialog(CommandResult result, Image img) {
        DialogBox db = new DialogBox(result.response(), img);
        db.flip();
        db.changeDialogStyle(result.commandType());
        return db;
    }

    /**
     * Flips the dialog box such that the image is on the left and text on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(tmp);
        getChildren().setAll(tmp);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Changes the styling of the dialog box based on the executed command type.
     *
     * @param cmdType Type of command that was executed.
     */
    private void changeDialogStyle(CommandType cmdType) {
        assert cmdType != null : "Result of executing command is missing";
        switch(cmdType) {
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case UNMARK:
                dialog.getStyleClass().add("unmarked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case ERROR:
                dialog.getStyleClass().add("error-label");
                break;
            default:
                break;
        }
    }
}
