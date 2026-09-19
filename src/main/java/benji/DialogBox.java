package benji;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Represents one chat message with text and an avatar.
 */
public class DialogBox extends HBox {
    // Chatgpt 5.6 Terra Medium was used to suggest the color palette
    private static final String USER_BUBBLE_STYLE =
            "-fx-background-color: white;"
                    + "-fx-text-fill: #1F2937;"
                    + "-fx-background-radius: 16 16 3 16;"
                    + "-fx-border-color: #94A3B8;"
                    + "-fx-border-width: 0.5;"
                    + "-fx-border-radius: 16 16 3 16;";

    private static final String BENJI_BUBBLE_STYLE =
            "-fx-background-color: #111827;"
                    + "-fx-text-fill: #00BFFF;"
                    + "-fx-background-radius: 16 16 16 3;"
                    + "-fx-border-radius: 16 16 16 3;";


    private final Label message;

    /**
     * Creates a chat message.
     *
     * @param text message to display
     * @param image avatar for the speaker
     */
    private DialogBox(String text, Image image) {
        NumberBinding avatarSize = Bindings.max(70.0,
                Bindings.min(140.0, widthProperty().multiply(0.175)));

        // Most of the codes were reused from se-education.org javafx tutorial
        // Chatgpt 5.6 Terra Medium was used to suggest some methods
        message = new Label(text);
        message.setWrapText(true);

        NumberBinding bubbleWidth = widthProperty().multiply(0.65);

        NumberBinding fontSize = Bindings.max(14.0,
                Bindings.min(28.0, widthProperty().divide(28.0)));

        message.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font(fontSize.doubleValue()), fontSize));

        message.maxWidthProperty().bind(bubbleWidth);
        message.setAlignment(Pos.CENTER_RIGHT);
        message.setTextAlignment(TextAlignment.RIGHT);
        message.setPadding(new Insets(10, 14, 10, 14));
        message.setStyle(USER_BUBBLE_STYLE);

        ImageView avatar = new ImageView(image);
        avatar.fitHeightProperty().bind(avatarSize);
        avatar.fitWidthProperty().bind(avatarSize);
        avatar.setPreserveRatio(true);

        setAlignment(Pos.TOP_RIGHT);
        setSpacing(10);
        setPadding(new Insets(4, 4, 4, 4));
        getChildren().addAll(message, avatar);
    }

    /**
     * Changes a user-style message into a left-aligned BENJI message.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children); // flip direction
        getChildren().setAll(children);

        setAlignment(Pos.TOP_LEFT);
        message.setStyle(BENJI_BUBBLE_STYLE);
        message.setAlignment(Pos.CENTER_LEFT);
        message.setTextAlignment(TextAlignment.LEFT);
    }

    /**
     * Creates a blue, right-aligned user speech bubble.
     *
     * @param text user message
     * @param image user avatar
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a white, left-aligned BENJI speech bubble.
     *
     * @param text BENJI message
     * @param image BENJI avatar
     * @return BENJI dialog box
     */
    public static DialogBox getBenjiDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}
