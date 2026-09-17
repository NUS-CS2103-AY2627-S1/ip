package harvey.ui;

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
import javafx.scene.shape.Circle;
/**
 * One message in the conversation.
 * <p>
 * The two speakers are deliberately shown differently, because this is a person using a
 * tool rather than two people chatting. The user's message is a compact bubble on the
 * right with no picture, since the user knows who they are. Harvey's reply is a card that
 * takes the rest of the row beside a small avatar, since his replies (task lists, search
 * results) are the long text that needs the width.
 * <p>
 * The two static factory methods are the way to make one. A constructor could not do the
 * job alone, because the user's box and Harvey's differ after construction.
 */
public class DialogBox extends HBox {
    /** Fraction of the row the user's bubble may fill, so it never looks like a reply. */
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.75;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Builds an unstyled box, with the text followed by the picture.
     *
     * @param text the message to show.
     * @param img  the speaker's picture, or null if the box will not show one.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // Packaged with the program, so a failure here means a broken build.
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        clipDisplayPictureToCircle();
    }

    /** Clips the display picture into a circle, so that profile pictures appear round. */
    private void clipDisplayPictureToCircle() {
        double radius = displayPicture.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Styles the box as the user's: the picture is dropped and the bubble is capped to part
     * of the row's width. The cap is a binding, so it is recomputed whenever the window is
     * resized.
     */
    private void styleAsUser() {
        getChildren().remove(displayPicture);
        dialog.maxWidthProperty().bind(widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
    }

    /**
     * Styles the box as Harvey's: the avatar moves to the left and the reply grows to fill
     * the rest of the row.
     */
    private void styleAsHarvey() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Colors the box by the kind of command that produced it, so that an addition, a
     * deletion and a refusal can be told apart at a glance.
     *
     * @param commandType the simple class name of the command, or null to leave the
     *                    default styling alone.
     */
    private void changeDialogStyle(String commandType) {
        if (commandType == null) {
            return;
        }

        switch (commandType) {
            case "AddCommand":
                dialog.getStyleClass().add("add-label");
                break;
            case "MarkCommand":
            case "UnmarkCommand":
                dialog.getStyleClass().add("marked-label");
                break;
            case "DeleteCommand":
                dialog.getStyleClass().add("delete-label");
                break;
            case "ErrorCommand":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // list, find and bye keep the ordinary reply styling.
        }
    }

    /**
     * Returns a box showing something the user said, as a right-aligned bubble with no
     * picture.
     *
     * @param text the message.
     * @return the new box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, null);
        db.styleAsUser();
        return db;
    }

    /**
     * Returns a box showing something Harvey said, beside his avatar and colored by command.
     *
     * @param text        the message.
     * @param img         Harvey's picture.
     * @param commandType the simple class name of the command that produced the message.
     * @return the new box.
     */
    public static DialogBox getHarveyDialog(String text, Image img, String commandType) {
        DialogBox db = new DialogBox(text, img);
        db.styleAsHarvey();
        db.changeDialogStyle(commandType);
        return db;
    }
}
