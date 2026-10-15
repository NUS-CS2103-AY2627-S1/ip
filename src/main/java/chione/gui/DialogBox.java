package chione.gui;

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
 * One message in the conversation: a picture of who is speaking, and what they
 * said.
 *
 * <p>The user's messages and Chione's differ in which way round those two sit
 * and in how the text is dressed, so both are built here from one layout and
 * given a style class that {@code chione.css} knows. A dialog box is made
 * through {@link #getUserDialog}, {@link #getChioneDialog} or
 * {@link #getErrorDialog} rather than with
 * {@code new}, because the names say which speaker is meant, while a constructor
 * taking a picture would not.
 */
public class DialogBox extends HBox {
    /** Style class for what the user typed: a solid bubble on the right. */
    private static final String USER_BUBBLE = "user-bubble";

    /** Style class for what Chione said: a plain bubble on the left. */
    private static final String CHIONE_BUBBLE = "chione-bubble";

    /** Style class for a reply that reports a failure: a red-edged bubble on the left. */
    private static final String ERROR_BUBBLE = "error-bubble";

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Builds a dialog box from the shared layout.
     *
     * <p>Unusually, this loads FXML with itself as both the root and the
     * controller. That is what {@code fx:root} in {@code DialogBox.fxml} allows,
     * and it is what makes a {@code DialogBox} usable as an ordinary control
     * rather than something that has to be unwrapped from a loader first.
     *
     * @param text what was said
     * @param image the speaker's picture
     * @param bubbleStyle the style class that dresses the text for this speaker
     */
    private DialogBox(String text, Image image, String bubbleStyle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // As in Main: the layout is packaged with the program, so this can
            // only mean a broken build.
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.getStyleClass().add(bubbleStyle);
        displayPicture.setImage(image);
        clipPictureToCircle();
    }

    /**
     * Creates a dialog box for something the user said.
     *
     * @param text what the user typed
     * @param image the user's picture
     * @return a dialog box with the picture on the right
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, USER_BUBBLE);
    }

    /**
     * Creates a dialog box for something Chione said.
     *
     * @param text Chione's reply
     * @param image Chione's picture
     * @return a dialog box with the picture on the left
     */
    public static DialogBox getChioneDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, CHIONE_BUBBLE);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog box for something Chione could not do.
     *
     * <p>It sits where Chione's other replies sit, but is dressed differently,
     * so that a mistyped command is noticed at once rather than read as
     * another success in a column of successes.
     *
     * @param text Chione's explanation of what went wrong
     * @param image Chione's picture
     * @return a dialog box with the picture on the left and the text marked as an error
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, ERROR_BUBBLE);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Turns the dialog box around, so the picture is on the left and the text on
     * the right.
     *
     * <p>Having the two speakers face each other from opposite sides is what makes
     * the conversation readable at a glance, without labelling every line.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Shows the picture as a small circle rather than a square.
     *
     * <p>The two speakers never change, so the picture only needs to say
     * <em>which</em> of them is talking; a small round mark does that without
     * taking space from the text. The circle is sized from the picture's fitted
     * width, so it stays right if that width is changed in the FXML.
     */
    private void clipPictureToCircle() {
        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }
}
