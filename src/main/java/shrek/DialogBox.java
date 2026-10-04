package shrek;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A reusable chat message control backed by {@code DialogBox.fxml}.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    @FXML
    private Label errorIndicator;

    @FXML
    private VBox messageContent;

    /**
     * Loads a dialog box from FXML and fills it with the supplied content.
     *
     * @param text  the message to display.
     * @param image the avatar of the message sender.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        getStyleClass().add("dialog-row");
    }

    /** Moves the avatar to the left and gives the reply bubble its own style. */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(messageContent, Priority.ALWAYS);
        messageContent.setMaxWidth(Double.MAX_VALUE);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a user-side dialog box.
     *
     * @param text  the user's message.
     * @param image the user's avatar.
     * @return a dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        HBox.setHgrow(dialogBox.messageContent, Priority.NEVER);
        dialogBox.messageContent.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.72));
        return dialogBox;
    }

    /**
     * Creates a Shrek-side dialog box and applies a command-specific style.
     *
     * @param text        the response from Shrek.
     * @param image       Shrek's avatar.
     * @param commandType the command that produced the response.
     * @return a dialog box aligned to the left.
     */
    public static DialogBox getShrekDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("shrek-dialog");
        dialogBox.flip();
        dialogBox.changeDialogStyle(text, commandType);
        return dialogBox;
    }

    /** Applies the Part 5 response colors to relevant command results. */
    private void changeDialogStyle(String response, CommandType commandType) {
        boolean isError = response.startsWith("OOPS");
        errorIndicator.setManaged(isError);
        errorIndicator.setVisible(isError);

        if (isError) {
            dialog.getStyleClass().add("error-label");
            return;
        }

        if (matchesAnyCommand(commandType, CommandType.TODO, CommandType.DEADLINE, CommandType.EVENT,
                CommandType.TAG)) {
            dialog.getStyleClass().add("add-label");
        } else if (matchesAnyCommand(commandType, CommandType.MARK, CommandType.UNMARK, CommandType.UNTAG)) {
            dialog.getStyleClass().add("marked-label");
        } else if (matchesAnyCommand(commandType, CommandType.DELETE)) {
            dialog.getStyleClass().add("delete-label");
        }
    }

    /**
     * Checks whether a command is one of the supplied commands.
     *
     * @param command the command to check.
     * @param candidates the commands that should match.
     * @return true when {@code command} appears in {@code candidates}.
     */
    private boolean matchesAnyCommand(CommandType command, CommandType... candidates) {
        for (CommandType candidate : candidates) {
            if (command == candidate) {
                return true;
            }
        }
        return false;
    }
}
