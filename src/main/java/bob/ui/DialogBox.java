package bob.ui;

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
import javafx.scene.shape.Circle;

/**
 * Represents one speech bubble in the conversation: a picture of whoever is
 * speaking, and what they said.
 *
 * <p>This is a control of the project's own, made by putting two of JavaFX's
 * together — an {@link ImageView} and a {@link Label} side by side in an
 * {@link HBox}. Writing it as a class rather than building the same pair of
 * controls at each place they are needed means the conversation is added to one
 * bubble at a time, and that what a bubble looks like is settled in one file.
 *
 * <p>Its layout is read from {@code /view/DialogBox.fxml} rather than written out
 * here, for the same reason the window's is: the arrangement and the sizes are the
 * part most often adjusted by eye, and keeping them in FXML means adjusting them
 * without touching Java. The {@code fx:root} at the top of that file is what lets
 * the loaded layout become <em>this</em> {@code HBox} rather than a new one, which
 * is how a class can be a control and load its own appearance at the same time.
 *
 * <p>The two speakers get the same bubble the two ways round: the user's picture
 * on the right with the text to its left, and the chatbot's mirrored by
 * {@link #flip()}. That is the whole of what distinguishes them on screen, and it
 * is enough — a reader can tell at a glance who said what without either bubble
 * being labeled.
 *
 * <p>Made only through {@link #getUserDialog} and {@link #getBobDialog}. The
 * constructor is private because a bubble that had been made but not yet told
 * which way round it faces would be a half-built thing, and neither caller has any
 * use for one.
 */
public class DialogBox extends HBox {

    /**
     * What a bubble of the chatbot's shows while its answer is still being waited
     * for. Three dots because that is what an unfinished sentence looks like, and
     * because it is short enough that the bubble is plainly a placeholder rather
     * than something worth reading.
     */
    private static final String TYPING_TEXT = "...";

    /** What was said. */
    @FXML
    private Label dialog;

    /** The speaker's picture. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a bubble showing one thing said by one speaker, laid out as the user
     * side: picture on the right, text to its left.
     *
     * @param text what was said, which may run to several lines.
     * @param image the speaker's picture.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The FXML is packaged alongside the class that loads it, so failing to
            // find or read it means a broken build rather than anything the user did.
            throw new IllegalStateException("Could not load the layout of a dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        clipPictureToCircle();
    }

    /**
     * Rounds the speaker's picture off into a circle.
     *
     * <p>Both pictures are drawn as discs on a transparent background, so this
     * only trims the corners that were already empty. It is done here rather than
     * in the FXML because the clip has to be the same size as the picture, and
     * that size is set in one place there.
     */
    private void clipPictureToCircle() {
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Turns this bubble around, so the picture is on the left and the text to its
     * right.
     *
     * <p>Reversing the children is what moves the picture, and the alignment is
     * what keeps a short line of text beside it rather than pushed out to the far
     * edge of the window.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("bob-bubble");
    }

    /**
     * Returns a bubble for something the user typed, laid out along the right-hand
     * side of the conversation.
     *
     * @param text what the user typed.
     * @param image the user's picture.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a bubble for something the chatbot said, laid out along the left-hand
     * side of the conversation so that it reads as the other half of it.
     *
     * @param text what the chatbot said.
     * @param image the chatbot's picture.
     */
    public static DialogBox getBobDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns an empty bubble of the chatbot's, showing only that it is about to
     * say something, for {@link #showResponse} to fill in a moment later.
     *
     * <p>The bubble is put in the conversation the instant the user presses Enter,
     * before the answer is shown, so that a pause before the answer reads as the
     * chatbot composing it rather than as the window having missed the key.
     *
     * <p>Taking the place in the conversation straight away is also what keeps the
     * order right. A user who types a second line while the first is still being
     * answered would otherwise see the two answers arrive after both questions;
     * because each question's bubble is already sitting where its answer will go,
     * the conversation reads in the order it happened however fast it is typed.
     *
     * @param image the chatbot's picture.
     */
    public static DialogBox getTypingBobDialog(Image image) {
        return getBobDialog(TYPING_TEXT, image);
    }

    /**
     * Replaces the placeholder from {@link #getTypingBobDialog} with what the
     * chatbot actually had to say.
     *
     * <p>A bubble holding a complaint is colored differently from one holding an
     * answer, so that a mistyped command is obvious at a glance rather than having
     * to be read for. That is the whole of the difference: it is still the chatbot
     * speaking, in a bubble on the same side with the same picture beside it.
     *
     * @param text    what the chatbot said, which may run to several lines.
     * @param isError whether that text explains why the command could not be
     *                carried out, rather than reporting that it was.
     */
    public void showResponse(String text, boolean isError) {
        dialog.setText(text);
        if (isError) {
            dialog.getStyleClass().add("error-bubble");
        }
    }
}
