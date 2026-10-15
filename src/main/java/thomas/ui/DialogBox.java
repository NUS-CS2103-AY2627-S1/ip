package thomas.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One turn of the conversation: a bubble of text, and for Thomas's turns a
 * small round avatar beside it.
 * <p>
 * The two sides are deliberately not drawn alike. What the user typed is a
 * short line that is already known to them, so it sits on the right as a
 * compact tinted bubble with no picture. What Thomas says back can be a whole
 * task list, so it sits on the left, is given as much of the width as it
 * needs, and carries the avatar that tells the two apart at a glance.
 */
public class DialogBox extends HBox {
    /** Widest a user bubble may be, as a share of the conversation's width. */
    private static final double USER_BUBBLE_WIDTH_SHARE = 0.75;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

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
     * Returns a dialog box holding what the user typed.
     * <p>
     * The bubble is capped at part of the width so that a long line wraps into
     * a column on the right rather than stretching across the window and
     * looking like one of Thomas's replies.
     *
     * @param text The line the user typed.
     * @return The dialog box to append to the conversation.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getChildren().remove(dialogBox.displayPicture);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-label");
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().multiply(USER_BUBBLE_WIDTH_SHARE));
        return dialogBox;
    }

    /**
     * Returns a dialog box holding Thomas's reply, avatar on the left and
     * colored by what produced it.
     * <p>
     * A rejected line gets the error bubble whatever else is true of it, since
     * the point of that bubble is not to be missed. Otherwise the tint follows
     * the command, so a change to the list looks different from a listing.
     *
     * @param text Thomas's reply.
     * @param img Thomas's avatar.
     * @param commandType The simple class name of the command that ran.
     * @param isError Whether the reply is an error message rather than a
     *                command's answer.
     * @return The dialog box to append to the conversation.
     */
    public static DialogBox getThomasDialog(String text, Image img, String commandType,
            boolean isError) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.showAvatarOnLeft(img);
        dialogBox.dialog.getStyleClass().add("reply-label");
        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-label");
        } else {
            dialogBox.addCommandStyle(commandType);
        }
        return dialogBox;
    }

    /**
     * Moves the avatar to the left of the text, and clips it to a circle.
     * <p>
     * The clip is a circle the size of the picture's frame, so the picture is
     * assumed to be roughly square; a tall one would have its top and bottom
     * cut off.
     *
     * @param img The avatar to show.
     */
    private void showAvatarOnLeft(Image img) {
        displayPicture.setImage(img);
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
        getChildren().setAll(displayPicture, dialog);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Tints the bubble by the kind of command that produced the reply.
     * <p>
     * The names are Thomas's own command classes, as
     * {@link thomas.Thomas#getCommandType()} reports them. Anything unlisted keeps the
     * plain reply bubble, so a new command needs a case here only if it earns a
     * colour of its own.
     *
     * @param commandType The simple class name of the command that ran.
     */
    private void addCommandStyle(String commandType) {
        String styleClass = switch (commandType) {
            case "AddCommand" -> "add-label";
            case "MarkCommand", "UnmarkCommand" -> "marked-label";
            case "DeleteCommand", "UndoCommand" -> "delete-label";
            // Every other reply keeps the plain bubble.
            default -> "";
        };
        if (!styleClass.isEmpty()) {
            dialog.getStyleClass().add(styleClass);
        }
    }
}
