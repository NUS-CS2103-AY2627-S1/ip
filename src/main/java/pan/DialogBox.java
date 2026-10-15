package pan;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
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
 * One chat bubble, with its layout loaded from {@code DialogBox.fxml}: a
 * wrapped-text {@link Label} beside a circular avatar {@link ImageView}.
 *
 * <p>The FXML uses the {@code <fx:root>} construct, so each {@code DialogBox}
 * instance is both the root node and the controller of its own copy of the
 * layout. A user bubble keeps the avatar on the right; PanPan's bubble is
 * {@linkplain #flip() flipped} so the avatar sits on the left.
 *
 * <p>Colours and padding come from {@code styles.css}; this class only decides
 * which style class each bubble wears, and applies the two things CSS cannot
 * express - the circular crop of the avatar and the cap on the bubble's width.
 */
public class DialogBox extends HBox {

    /** Width and height of the avatar in pixels; must match the FXML's fit size. */
    private static final double AVATAR_SIZE = 40.0;

    /** Gap between the bubble and its avatar; must match the FXML's spacing. */
    private static final double AVATAR_GAP = 10.0;

    /**
     * How much of the chat area's width one bubble may fill. Below 1.0 so a
     * long reply wraps into a column instead of running edge to edge, which is
     * what makes the two speakers tellable apart at a glance.
     */
    private static final double BUBBLE_WIDTH_FRACTION = 0.8;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image avatar) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // A missing or malformed FXML file is a packaging bug, not a user
            // error, so fail loudly instead of showing a broken bubble.
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        dialog.getStyleClass().add("user-bubble");
        displayPicture.setImage(avatar);
        cropAvatarToCircle();
    }

    /**
     * Crops the avatar to a circle by clipping it with one. A clip is a shape,
     * not a style, so this cannot live in the stylesheet. Both source images
     * are square, so the circle is inscribed exactly.
     */
    private void cropAvatarToCircle() {
        double radius = AVATAR_SIZE / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Reverses the children (avatar first, then text), left-aligns the row and
     * swaps the bubble's colour, turning a user bubble into a "them" bubble.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);

        dialog.getStyleClass().remove("user-bubble");
        dialog.getStyleClass().add("pan-bubble");
    }

    /**
     * Caps this bubble at {@value #BUBBLE_WIDTH_FRACTION} of the chat area's
     * width, following it as the window is resized.
     *
     * <p>The width is passed in rather than read from this box, because a
     * child whose maximum width is derived from its own parent's width would
     * be asking the layout to depend on its own result.
     *
     * <p>The avatar shares the row with the bubble, so the fraction alone is
     * not enough: at narrow window widths four fifths of the chat area is
     * still wider than the room left beside a 40px avatar, and the text would
     * run off the edge. Whichever limit is tighter wins.
     *
     * @param chatWidth width available to a row of bubbles.
     */
    public void limitWidthTo(ObservableDoubleValue chatWidth) {
        dialog.maxWidthProperty().bind(Bindings.createDoubleBinding(
            () -> Math.min(chatWidth.get() * BUBBLE_WIDTH_FRACTION,
                           chatWidth.get() - AVATAR_SIZE - AVATAR_GAP),
            chatWidth));
    }

    /** Returns a bubble for something the user said, with the avatar on the right. */
    public static DialogBox getUserDialog(String text, Image avatar) {
        return new DialogBox(text, avatar);
    }

    /** Returns a bubble for something PanPan said, with the avatar on the left. */
    public static DialogBox getPanDialog(String text, Image avatar) {
        DialogBox box = new DialogBox(text, avatar);
        box.flip();
        return box;
    }
}
