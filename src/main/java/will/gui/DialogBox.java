package will.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * A single chat bubble: a message (with the time it was sent, in a
 * smaller muted line beneath it) paired with a speaker image. User
 * messages keep the image on the right (the default layout);
 * {@link #flip()} mirrors that for the chatbot's own messages, so the
 * two speakers read visually distinct from each other. Each bubble
 * also gets a "user-bubble" or "will-bubble" style class (see
 * style.css), which — besides coloring it — flattens the corner
 * nearest its avatar, so the bubble itself points at its speaker
 * without needing a separate tail shape.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 20.0;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    @FXML
    private TextFlow dialog;
    @FXML
    private Text messageText;
    @FXML
    private Text timestampText;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml failed to load", e);
        }

        // The timestamp flows right after the message instead of on its
        // own forced line, so a short bubble hugs its actual content
        // instead of being stretched to fit the timestamp's width.
        messageText.setText(text + "   ");
        timestampText.setText(LocalTime.now().format(TIME_FORMAT));
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
    }

    /** Mirrors this dialog box so the image sits on the left instead of the right. */
    private void flip() {
        List<Node> tmp = new ArrayList<>(getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for something the user typed.
     *
     * @param text The user's input.
     * @param img  The user's avatar.
     * @return A dialog box laid out with the avatar on the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-bubble");
        db.dialog.setTextAlignment(TextAlignment.RIGHT);
        return db;
    }

    /**
     * Creates a dialog box for one of Will's replies.
     *
     * @param text Will's reply text.
     * @param img  Will's avatar.
     * @return A dialog box laid out with the avatar on the left.
     */
    public static DialogBox getWillDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("will-bubble");
        db.flip();
        return db;
    }
}
