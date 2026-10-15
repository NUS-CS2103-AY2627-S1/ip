package xiaozhi.gui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableNumberValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * A dialog box showing one line of the conversation.
 * <p>
 * The chat is deliberately asymmetric, since it is a conversation between
 * the user and the app, not between two equal speakers: {@link #getUserDialog}
 * keeps a compact, right-aligned "bubble" for what the user typed, while
 * {@link #getXiaoZhiDialog} turns the box into a full-width, left-aligned
 * "card" for XiaoZhi's reply, additionally tinted by the kind of command
 * that produced it (see {@link #changeDialogStyle(String)}) and preceded by
 * Master Zhi's avatar (see {@link #showAvatar()}).
 */
public class DialogBox extends HBox {
    private static final Image MASTER_ZHI_AVATAR =
            new Image(DialogBox.class.getResourceAsStream("/images/MasterZhi.png"));

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
            throw new IllegalStateException("Could not load DialogBox.fxml.", e);
        }

        dialog.setText(text);
    }

    /**
     * Turns this dialog box into XiaoZhi's full-width reply card: left-aligned
     * and grown to fill whatever container it is placed in, instead of the
     * default compact, right-aligned bubble used for the user's own input.
     */
    private void makeXiaoZhiCard() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().setAll("bot-card");
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        showAvatar();
    }

    /**
     * Reveals Master Zhi's avatar to the left of the reply card, clipped to a
     * circle so it matches the rounded look of the rest of the GUI. Hidden
     * (and unmanaged, so it takes up no space) for the user's own bubble.
     */
    private void showAvatar() {
        displayPicture.setImage(MASTER_ZHI_AVATAR);
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
        displayPicture.setVisible(true);
        displayPicture.setManaged(true);
    }

    /**
     * Tints this dialog box's card according to the kind of command that
     * produced it, e.g. green for a task being added, or red for an error so
     * it stands out from a normal reply. Commands with no special styling
     * (list, find, exit, ...) are left with the plain {@code bot-card} look.
     *
     * @param commandType Simple class name of the {@code Command} that produced this reply,
     *         e.g. {@code "AddCommand"}, or {@code "Error"} if the input could not be parsed.
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
            case "UndoCommand":
                dialog.getStyleClass().add("undo-label");
                break;
            case "Error":
                dialog.getStyleClass().add("error-label");
                break;
            default:
                // No special styling for this command type; keep the plain bot-card look.
                break;
        }
    }

    /**
     * Caps how wide this dialog box's bubble may grow, as a fraction of
     * another region's width, so a user bubble stays a sensible size instead
     * of stretching edge-to-edge when the window is resized wider.
     *
     * @param containerWidth Width to size the cap relative to, e.g. the chat area's own width.
     * @param fraction Fraction of {@code containerWidth} the bubble may grow to, e.g. {@code 0.7}.
     */
    public void capBubbleWidth(ObservableNumberValue containerWidth, double fraction) {
        dialog.maxWidthProperty().bind(Bindings.multiply(fraction, containerWidth));
    }

    /**
     * Creates a dialog box for something the user said.
     *
     * @param text Text to display.
     * @return The user's dialog box, a compact bubble on the right.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Creates a dialog box for something XiaoZhi said.
     *
     * @param text Text to display.
     * @param commandType Simple class name of the {@code Command} that produced this reply,
     *         used to tint the card (see {@link #changeDialogStyle(String)}).
     * @return XiaoZhi's dialog box, a full-width card on the left with Master Zhi's avatar.
     */
    public static DialogBox getXiaoZhiDialog(String text, String commandType) {
        var dialogBox = new DialogBox(text);
        dialogBox.makeXiaoZhiCard();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }
}
