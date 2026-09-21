import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
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
 * One chat bubble, loaded from {@code view/DialogBox.fxml}: a message label,
 * optionally next to a speaker avatar. The conversation is asymmetric (the
 * user always knows which messages are theirs - they're right-aligned and
 * a different color) so only the bot's replies carry an avatar; a user
 * message has no {@code displayPicture} at all, freeing up width for text.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load view/DialogBox.fxml", e);
        }

        // Same FXML-injection contract as MainWindow: a null field here means
        // DialogBox.fxml and this controller have drifted out of sync.
        assert dialog != null && displayPicture != null : "FXML injection failed for one or more @FXML fields";
        dialog.setText(text);
        if (img == null) {
            // User messages carry no avatar - see the class doc.
            getChildren().remove(displayPicture);
        } else {
            displayPicture.setImage(img);
            clipToCircle(displayPicture);
        }
    }

    /** Clips {@code imageView} to a circle inscribed in its fit bounds, for a round avatar. */
    private static void clipToCircle(ImageView imageView) {
        double radius = Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2;
        imageView.setClip(new Circle(radius, radius, radius));
    }

    /** Mirrors this bubble to the top-left by reversing its children's order and alignment. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Keeps the bubble wrapping sensibly as the window is resized, instead of
     * staying a fixed pixel width forever: its max width tracks a fraction of
     * {@code containerWidth}, capped so a line doesn't get unreadably long on
     * a very wide window.
     */
    public void bindMaxWidth(ReadOnlyDoubleProperty containerWidth) {
        dialog.maxWidthProperty().bind(Bindings.min(420, containerWidth.multiply(0.72)));
    }

    /** Returns a bubble for a message the user typed, aligned to the top-right, with no avatar. */
    public static DialogBox getUserDialog(String text) {
        DialogBox db = new DialogBox(text, null);
        db.dialog.getStyleClass().add("bubble-user");
        return db;
    }

    /**
     * Returns a bubble for a Maggi Goreng Ayam response, aligned to the
     * top-left. {@code isError} styles it as an error (e.g. an unrecognized
     * command) instead of a normal reply, so it stands out at a glance.
     */
    public static DialogBox getMaggiGorengAyamDialog(String text, Image img, boolean isError) {
        var db = new DialogBox(text, img);
        db.dialog.getStyleClass().add(isError ? "bubble-error" : "bubble-bot");
        db.flip();
        return db;
    }
}
