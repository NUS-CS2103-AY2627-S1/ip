package toothless.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Displays one chat message beside its speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final char STAR = '★';
    private static final double MESSAGE_FONT_SIZE = 14.0;
    private static final Font MESSAGE_FONT = loadMessageFont();
    private static final Font SYMBOL_FONT = Font.font("System", MESSAGE_FONT_SIZE);

    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box from its reusable FXML layout.
     *
     * @param text message to display.
     * @param image avatar belonging to the speaker.
     */
    private DialogBox(String text, Image image) {
        URL dialogResource = Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "DialogBox.fxml must be available on the classpath");
        FXMLLoader fxmlLoader = new FXMLLoader(dialogResource);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a Toothless dialog box", exception);
        }

        addMessageText(text);
        if (!"Comic Neue".equals(MESSAGE_FONT.getFamily())) {
            getStyleClass().add("message-font-fallback");
        }
        dialog.maxWidthProperty().bind(widthProperty().subtract(70.0));
        displayPicture.setImage(image);
    }

    /**
     * Adds text runs so the star uses a font that contains its glyph.
     */
    private void addMessageText(String text) {
        int segmentStart = 0;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == STAR) {
                if (segmentStart < index) {
                    addTextRun(text.substring(segmentStart, index), false);
                }
                addTextRun(String.valueOf(STAR), true);
                segmentStart = index + 1;
            }
        }
        if (segmentStart < text.length()) {
            addTextRun(text.substring(segmentStart), false);
        }
    }

    /**
     * Adds one styled run to the wrapping conversation bubble.
     */
    private void addTextRun(String value, boolean isStar) {
        Text run = new Text(value);
        run.getStyleClass().add("message-content");
        if (isStar) {
            run.getStyleClass().add("star-glyph");
        }
        run.setFont(isStar ? SYMBOL_FONT : MESSAGE_FONT);
        dialog.getChildren().add(run);
    }

    /**
     * Loads the bundled conversation font, using the system font if it is unavailable.
     */
    private static Font loadMessageFont() {
        try (InputStream fontStream = DialogBox.class.getResourceAsStream("/fonts/ComicNeue-Regular.ttf")) {
            if (fontStream == null) {
                return Font.font("System", MESSAGE_FONT_SIZE);
            }
            Font font = Font.loadFont(fontStream, MESSAGE_FONT_SIZE);
            return font != null && "Comic Neue".equals(font.getFamily())
                    ? font : Font.font("System", MESSAGE_FONT_SIZE);
        } catch (IOException | IllegalArgumentException exception) {
            return Font.font("System", MESSAGE_FONT_SIZE);
        }
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param text message to display.
     * @param image user's avatar.
     * @return configured user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.setAccessibleText("You said: " + text);
        return dialogBox;
    }

    /**
     * Creates a left-aligned message written by Toothless.
     *
     * @param text message to display.
     * @param image Toothless's avatar.
     * @return configured Toothless dialog box
     */
    public static DialogBox getToothlessDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("toothless-dialog");
        dialogBox.setAccessibleText("Toothless said: " + text);
        return dialogBox;
    }

    /**
     * Creates a Toothless message styled for a validation or storage error.
     *
     * @param text error message to display.
     * @param image Toothless's avatar.
     * @return configured error dialog box
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getToothlessDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        dialogBox.setAccessibleText("Toothless error: " + text);
        return dialogBox;
    }

    /**
     * Places Toothless's avatar on the left of his message.
     */
    private void flip() {
        getChildren().remove(displayPicture);
        getChildren().add(0, displayPicture);
        setAlignment(Pos.TOP_LEFT);
    }
}
