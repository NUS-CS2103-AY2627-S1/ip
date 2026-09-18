package aster.gui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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
 * A dialog box showing one message, beside Aster's mark when the message is from Aster.
 *
 * <p>The layout is described in {@code /view/DialogBox.fxml}. A message from the user
 * sits on the right, while a reply from Aster is flipped so its mark comes first on the
 * left, which keeps the two sides of the conversation easy to tell apart.
 *
 * <p>Each dialog box also carries exactly one style class saying whose message it is, or
 * that it is a reply containing an error, so the stylesheet can show each kind its own
 * way. A reply containing an error sits on Aster's side like any other reply.
 *
 * <p>Aster's replies show Aster's mark beside them; the user's messages are shown without
 * a picture, set apart by their side and colour. A message from the user is kept to three
 * quarters of the row, so short commands read as compact entries, while Aster's replies,
 * which can hold a lot of text, may use the whole row.
 */
public class DialogBox extends HBox {
    // Shared with the stylesheet tests, which is why these are not private.
    static final String USER_STYLE_CLASS = "user-dialog";
    static final String ASTER_STYLE_CLASS = "aster-dialog";
    static final String ERROR_STYLE_CLASS = "error-dialog";

    private static final double USER_MESSAGE_WIDTH_RATIO = 0.75;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the message on the left and room for a picture on the right.
     *
     * @param text the message to show.
     * @param kindStyleClass the one style class saying what kind of message this is.
     */
    private DialogBox(String text, String kindStyleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The layout is packaged with Aster, so failing to load it is a fault in the
            // build rather than something the user can correct.
            throw new UncheckedIOException("Could not load the dialog box layout.", e);
        }
        dialog.setText(text);
        getStyleClass().add(kindStyleClass);
    }

    /**
     * Returns a dialog box for a message the user sent.
     *
     * @param text the message, exactly as the user typed it.
     * @return a dialog box on the right with no picture, its message kept to three quarters
     *     of the row.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_STYLE_CLASS);
        dialogBox.hidePicture();
        dialogBox.limitMessageWidth();
        return dialogBox;
    }

    /**
     * Returns a dialog box for a reply from Aster.
     *
     * @param text the reply, with its lines separated by line breaks.
     * @param image Aster's mark.
     * @return a dialog box with the mark on the left.
     */
    public static DialogBox getAsterDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, ASTER_STYLE_CLASS);
        dialogBox.displayPicture.setImage(image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns a dialog box for a reply from Aster that contains an error.
     *
     * <p>It sits on Aster's side like any other reply, and is styled so that it stands out.
     *
     * @param text the reply, with its lines separated by line breaks.
     * @param image Aster's mark.
     * @return a dialog box with the mark on the left, marked as containing an error.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, ERROR_STYLE_CLASS);
        dialogBox.displayPicture.setImage(image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Removes the picture from this dialog box, so the message alone fills its side of the row.
     */
    private void hidePicture() {
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
    }

    /**
     * Keeps the message to three quarters of this dialog box's width.
     *
     * <p>The row's width is set by the conversation it sits in, never by the message, so
     * the limit cannot feed back into the width it is taken from. Until the row has been
     * laid out its width is zero, and the message is left unlimited rather than squeezed
     * to nothing for that first pass.
     */
    private void limitMessageWidth() {
        dialog.maxWidthProperty().bind(
                Bindings.createDoubleBinding(this::computeMessageWidthLimit, widthProperty()));
    }

    /**
     * Returns the widest the message may be, given this dialog box's current width.
     *
     * <p>The limit is rounded down to a whole pixel, because the laid-out message is snapped
     * to whole pixels and could otherwise end up slightly wider than three quarters.
     *
     * @return three quarters of the width rounded down to a whole pixel, or no limit while
     *     the width is still zero.
     */
    private double computeMessageWidthLimit() {
        double width = getWidth();
        return width > 0
                ? Math.floor(width * USER_MESSAGE_WIDTH_RATIO)
                : Double.MAX_VALUE;
    }

    /**
     * Moves the picture to the left of the message and aligns the box to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
