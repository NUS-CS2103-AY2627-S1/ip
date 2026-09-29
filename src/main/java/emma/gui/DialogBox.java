package emma.gui;

import java.util.Collections;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One line of the conversation: what was said, next to the speaker's picture.
 *
 * <p>Only the measurements the code has to compute with are here; the colours,
 * fonts, padding and corners are all in {@code /css/emma.css}.
 */
public class DialogBox extends HBox {

    /**
     * The picture is kept small because it costs a column of width on every single
     * row, which is width the text cannot wrap in.
     */
    private static final double PICTURE_SIZE = 30;

    /**
     * How much of the window's width one bubble may take up. The rest is left as a
     * gutter on the opposite side, which is what makes it obvious at a glance who
     * is speaking.
     */
    private static final double MAX_WIDTH_SHARE = 0.78;

    /** Marks an error in the text too, for anyone who cannot tell the colours apart. */
    private static final String ERROR_PREFIX = "⚠ ";

    private final Label text;
    private final ImageView displayPicture;

    /**
     * Creates a dialog box with the speaker's picture on the right, as Emma's own
     * replies are shown mirrored.
     *
     * @param message what was said.
     * @param picture the speaker's picture.
     * @param bubbleStyleClass the CSS class that colours the bubble holding the message.
     */
    private DialogBox(String message, Image picture, String bubbleStyleClass) {
        text = new Label(message);
        text.setWrapText(true);
        text.getStyleClass().addAll("bubble", bubbleStyleClass);

        displayPicture = new ImageView(picture);
        displayPicture.setFitWidth(PICTURE_SIZE);
        displayPicture.setFitHeight(PICTURE_SIZE);
        displayPicture.setClip(new Circle(PICTURE_SIZE / 2, PICTURE_SIZE / 2, PICTURE_SIZE / 2));

        this.getStyleClass().add("dialog-box");
        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Ties how wide this bubble may grow to how wide the conversation is, so that a
     * long reply uses whatever room the window has instead of wrapping inside a
     * fixed column and scrolling away below the fold.
     *
     * @param conversationWidth the width the conversation is being shown in.
     */
    public void capWidthTo(ReadOnlyDoubleProperty conversationWidth) {
        text.maxWidthProperty().bind(conversationWidth.multiply(MAX_WIDTH_SHARE));
    }

    /** Turns the box around, so the picture is on the left and the text follows it. */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
    }

    /**
     * Creates the box for something the user typed, shown on the right.
     *
     * @param message what the user typed.
     * @param picture the user's picture.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String message, Image picture) {
        return new DialogBox(message, picture, "bubble-user");
    }

    /**
     * Creates the box for one of Emma's replies, shown on the left.
     *
     * @param message what Emma said.
     * @param picture Emma's picture.
     * @return the dialog box, already turned around.
     */
    public static DialogBox getEmmaDialog(String message, Image picture) {
        DialogBox box = new DialogBox(message, picture, "bubble-emma");
        box.flip();
        return box;
    }

    /**
     * Creates the box for a command Emma could not carry out, shown on the left like any
     * other reply but styled to stand out.
     *
     * @param message what Emma said about the problem.
     * @param picture Emma's picture.
     * @return the dialog box, already turned around.
     */
    public static DialogBox getEmmaErrorDialog(String message, Image picture) {
        DialogBox box = new DialogBox(ERROR_PREFIX + message, picture, "bubble-error");
        box.flip();
        return box;
    }
}
