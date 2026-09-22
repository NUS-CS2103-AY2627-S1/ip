package steph;

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
 * One chat bubble in the conversation: a wrapped-text {@link Label} beside a
 * circular avatar {@link ImageView}, laid out by {@code DialogBox.fxml}.
 *
 * <p>Instances are created through the static factory methods rather than a
 * constructor: {@link #getUserDialog} makes the user's bubble (avatar on the
 * right), and {@link #getStephDialog} makes Steph's bubble (avatar flipped to
 * the left and the bubble tinted according to the command that produced it).
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
     * Flips the bubble so the avatar is on the left and the text is
     * left-aligned, and re-tags the label so its "reply" corner styling
     * applies. Used for Steph's side of the conversation.
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(nodes);
        this.getChildren().setAll(nodes);
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds a command-specific style class to the reply bubble so its colour
     * signals what the command did (feature 6), or the distinct error style
     * (feature 7) when the command failed instead.
     *
     * @param commandType The command's {@link Command} name, or {@code ""} when
     *                    the command was not recognised.
     * @param isError     Whether the reply is an error message; when true this
     *                    takes precedence over {@code commandType}.
     */
    private void changeDialogStyle(String commandType, boolean isError) {
        if (isError) {
            dialog.getStyleClass().add("error-label");
            return;
        }
        switch (commandType) {
            case "TODO", "DEADLINE", "EVENT" -> dialog.getStyleClass().add("add-label");
            case "MARK", "UNMARK" -> dialog.getStyleClass().add("marked-label");
            case "DELETE" -> dialog.getStyleClass().add("delete-label");
            case "FIND" -> dialog.getStyleClass().add("find-label");
            default -> {
                // LIST and "bye" keep the default reply styling.
            }
        }
    }

    /**
     * Creates the bubble for a line the user typed.
     *
     * @param text The user's message.
     * @param img  The user's avatar image.
     * @return A bubble with the avatar on the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates the bubble for one of Steph's replies.
     *
     * @param text        Steph's reply text.
     * @param img         Steph's avatar image.
     * @param commandType The {@link Command} name that produced the reply, used
     *                    to tint the bubble; {@code ""} for none.
     * @param isError     Whether the reply is an error message, styled distinctly
     *                    from a successful command's reply.
     * @return A flipped, command-tinted (or error-styled) bubble.
     */
    public static DialogBox getStephDialog(String text, Image img, String commandType, boolean isError) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType, isError);
        return db;
    }
}
