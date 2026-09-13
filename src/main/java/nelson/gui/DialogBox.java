package nelson.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/** A chat bubble containing one message and its speaker's avatar. */
public class DialogBox extends HBox {
    private static final int AVATAR_SIZE = 32;

    /** The text displayed inside this message bubble. */
    private final Label text;
    /** The profile picture displayed beside the message. */
    private final ImageView displayPicture;

    /**
     * Creates a dialog box with a message and avatar.
     *
     * @param message message text to display
     * @param avatar avatar image to display
     */
    private DialogBox(String message, Image avatar) {
        text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(520);
        text.setPadding(new Insets(10, 14, 10, 14));
        text.getStyleClass().add("message-bubble");

        displayPicture = new ImageView(avatar);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);

        setAlignment(Pos.TOP_LEFT);
        setSpacing(10);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("dialog-box");
        getChildren().addAll(displayPicture, text);
    }

    /**
     * Creates a left-aligned Nelson message bubble.
     *
     * @param message message text from Nelson
     * @return a styled Nelson dialog box
     */
    public static DialogBox forNelson(String message) {
        DialogBox dialog = new DialogBox(message, createAvatar(Color.web("#c99700")));
        dialog.text.getStyleClass().add("nelson-bubble");
        return dialog;
    }

    /**
     * Creates a right-aligned user message bubble.
     *
     * @param message message text from the user
     * @return a styled user dialog box
     */
    public static DialogBox forUser(String message) {
        DialogBox dialog = new DialogBox(message, createAvatar(Color.web("#315b7d")));
        dialog.setAlignment(Pos.TOP_RIGHT);
        dialog.getChildren().clear();
        dialog.getChildren().addAll(dialog.text, dialog.displayPicture);
        dialog.text.getStyleClass().add("user-bubble");
        return dialog;
    }

    /**
     * Creates a left-aligned error bubble that draws attention to invalid moves.
     *
     * @param message error message from Nelson
     * @return a styled error dialog box
     */
    public static DialogBox forError(String message) {
        DialogBox dialog = new DialogBox(message, createAvatar(Color.web("#a83232")));
        dialog.text.getStyleClass().add("error-bubble");
        return dialog;
    }

    /** Creates a simple circular avatar image without external resource files. */
    private static Image createAvatar(Color color) {
        int size = 42;
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();
        double center = (size - 1) / 2.0;
        double radius = size / 2.0 - 1;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double distance = Math.hypot(x - center, y - center);
                writer.setColor(x, y, distance <= radius ? color : Color.TRANSPARENT);
            }
        }
        return image;
    }
}
