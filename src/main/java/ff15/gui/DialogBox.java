package ff15.gui;

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
 * One turn of the conversation. The two sides are deliberately not symmetric:
 * this is a chat between a person and a program, not between two people, so
 * the user's lines sit on the right as plain bubbles with no picture, while
 * FF15's replies sit on the left beside its face, the way a messaging app
 * shows the other party.
 *
 * <p>The box is its own root and its own controller, so one FXML file describes
 * every dialog box in the window.
 */
public class DialogBox extends HBox {
    /** How wide and tall FF15's face is shown, in pixels. Small on purpose: a marker, not a portrait. */
    private static final double AVATAR_SIZE = 36.0;

    /**
     * How much of the row a bubble may take before wrapping. Under the full width, so
     * a long message still reads as a bubble on one side rather than a bar across both.
     */
    private static final double BUBBLE_SHARE = 0.82;

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

    /** Returns a dialog box for something the user typed: right-aligned, with no picture. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.getChildren().remove(box.displayPicture);
        box.dialog.maxWidthProperty().bind(box.widthProperty().multiply(BUBBLE_SHARE));
        box.setAlignment(Pos.TOP_RIGHT);
        box.getStyleClass().add("user-box");
        box.dialog.getStyleClass().add("user-label");
        return box;
    }

    /**
     * Returns a dialog box for something FF15 replied: left-aligned, beside its
     * face. The face sits at the bottom of the bubble, where the bubble's tail
     * points, so a long reply reads as coming from it rather than floating above it.
     */
    public static DialogBox getFf15Dialog(String text, Image image) {
        DialogBox box = new DialogBox(text);
        box.showAsCircle(image);
        box.getChildren().setAll(box.displayPicture, box.dialog);
        // The face and the gap after it come off the row before the bubble's share is taken.
        box.dialog.maxWidthProperty().bind(
                box.widthProperty().subtract(AVATAR_SIZE + box.getSpacing()).multiply(BUBBLE_SHARE));
        box.setAlignment(Pos.BOTTOM_LEFT);
        box.getStyleClass().add("reply-box");
        box.dialog.getStyleClass().add("reply-label");
        return box;
    }

    /**
     * Returns a dialog box for an error FF15 reported. It is laid out like any
     * other reply, but styled to stand out, since an error is the one reply the
     * user must not skim past.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox box = getFf15Dialog(text, image);
        box.dialog.getStyleClass().add("error-label");
        return box;
    }

    /**
     * Returns a dialog box standing in for a reply that is still being worked
     * out. It is laid out like any other reply, but faded, so it reads as a
     * placeholder rather than as something FF15 has said.
     */
    public static DialogBox getThinkingDialog(String text, Image image) {
        DialogBox box = getFf15Dialog(text, image);
        box.dialog.getStyleClass().add("thinking-label");
        return box;
    }

    /**
     * Shows {@code image} as a small circle. The picture is cropped to its central
     * square first, so a portrait that is taller than it is wide still fills the
     * circle instead of leaving flat edges top and bottom.
     */
    private void showAsCircle(Image image) {
        double side = Math.min(image.getWidth(), image.getHeight());
        double left = (image.getWidth() - side) / 2;
        double top = (image.getHeight() - side) / 2;
        displayPicture.setViewport(new Rectangle2D(left, top, side, side));
        displayPicture.setImage(image);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);

        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }
}
