package ted;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One message in the conversation, shown as a speech bubble.
 * <p>
 * The two sides are deliberately drawn differently, because this is a person
 * talking to an app rather than two people chatting: the user's words sit on
 * the right in a solid bubble with no picture, while Ted's replies sit on the
 * left beside a small round picture of Ted. A reply explaining a problem gets
 * a red bubble of its own, so that it is not read past as a confirmation.
 * <p>
 * Each instance loads {@code DialogBox.fxml} as its own root and controller,
 * because unlike the main window there is no single dialog box for the FXML to
 * point at; every message needs its own. Adapted from the dialog box in the
 * SE-EDU JavaFX tutorial (https://se-education.org/guides/tutorials/javaFx.html).
 */
public class DialogBox extends HBox {
    /**
     * Widest a bubble may grow, as a fraction of the window's width. Keeping
     * bubbles narrower than the window leaves the side they sit on, and so
     * who is speaking, visible at a glance even for long replies.
     */
    private static final double MAX_BUBBLE_WIDTH_RATIO = 0.8;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, String bubbleStyleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The FXML ships inside the JAR, so failing to load it means a broken
            // build. Carrying on would only fail later with a NullPointerException.
            throw new IllegalStateException("Cannot load DialogBox.fxml", e);
        }

        dialog.setText(text);
        dialog.getStyleClass().add(bubbleStyleClass);
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_BUBBLE_WIDTH_RATIO));
    }

    /**
     * Returns a bubble for something the user said, on the right with no picture.
     *
     * @param text what the user typed.
     * @return the dialog box to add to the conversation.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "user-bubble");
        dialogBox.getChildren().remove(dialogBox.displayPicture);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    /**
     * Returns a bubble for something Ted said, on the left beside Ted's picture.
     *
     * @param text  what Ted replied.
     * @param image Ted's picture.
     * @return the dialog box to add to the conversation.
     */
    public static DialogBox getTedDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, "ted-bubble");
        dialogBox.showPicture(image);
        return dialogBox;
    }

    /**
     * Returns a red bubble for a reply in which Ted explains a problem, e.g.
     * a mistyped command, on the left beside Ted's picture.
     *
     * @param text  Ted's explanation of the problem.
     * @param image Ted's picture.
     * @return the dialog box to add to the conversation.
     */
    public static DialogBox getTedErrorDialog(String text, Image image) {
        DialogBox dialogBox = getTedDialog(text, image);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    /**
     * Shows the picture cropped to a circle. The middle square of the picture
     * is used, so that a picture that is not square is cropped rather than
     * squashed.
     *
     * @param image the speaker's picture.
     */
    private void showPicture(Image image) {
        double side = Math.min(image.getWidth(), image.getHeight());
        double left = (image.getWidth() - side) / 2;
        double top = (image.getHeight() - side) / 2;
        displayPicture.setImage(image);
        displayPicture.setViewport(new Rectangle2D(left, top, side, side));

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }
}
