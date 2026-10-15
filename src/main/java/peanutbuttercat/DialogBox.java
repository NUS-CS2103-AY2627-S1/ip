package peanutbuttercat;

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
import javafx.scene.layout.VBox;

/**
 * Presents a user command or an application response in the conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private VBox messageCard;

    @FXML
    private Label speaker;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        assert messageCard != null : "FXML must inject the message card before use";
        assert speaker != null : "FXML must inject the speaker label before use";
        assert dialog != null : "FXML must inject the dialog label before use";
        assert displayPicture != null : "FXML must inject the display picture before use";

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Configures the row as a branded application response with an avatar. */
    private void configureAsApplicationResponse() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("application-dialog-row");
        messageCard.getStyleClass().add("application-message-card");
        speaker.setText("PEANUTBUTTERCAT • TASK KEEPER");
        speaker.getStyleClass().add("application-speaker");
        dialog.getStyleClass().add("reply-label");
    }

    /** Configures the row as a compact command issued by the user. */
    private void configureAsUserCommand() {
        getStyleClass().add("user-dialog-row");
        messageCard.getStyleClass().add("user-message-card");
        speaker.setText("YOU • COMMAND");
        speaker.getStyleClass().add("user-speaker");
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
    }

    /** Applies a soft reply color that reflects the command's purpose. */
    private void applyCommandStyle(CommandType commandType) {
        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("status-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case UNKNOWN:
                dialog.getStyleClass().add("error-label");
                break;
            case LIST:
            case FIND:
            case ON:
            case STATISTICS:
                dialog.getStyleClass().add("query-label");
                break;
            case BYE:
                dialog.getStyleClass().add("farewell-label");
                break;
            default:
                break;
        }
    }

    /**
     * Creates a dialog box for a message entered by the user.
     *
     * @param text The message text.
     * @return A compact, right-aligned user command.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.configureAsUserCommand();
        return dialogBox;
    }

    /**
     * Creates a dialog box for a reply from PeanutButterCat.
     *
     * @param text The reply text.
     * @param image The PeanutButterCat avatar.
     * @return A left-aligned PeanutButterCat dialog box.
     */
    public static DialogBox getPeanutButterCatDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.configureAsApplicationResponse();
        return dialogBox;
    }

    /**
     * Creates a colored dialog box for a reply from PeanutButterCat.
     *
     * @param text The reply text.
     * @param image The PeanutButterCat avatar.
     * @param commandType Type of command that produced the reply.
     * @return A left-aligned, command-colored PeanutButterCat dialog box.
     */
    public static DialogBox getPeanutButterCatDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.configureAsApplicationResponse();
        dialogBox.applyCommandStyle(commandType);
        return dialogBox;
    }
}
