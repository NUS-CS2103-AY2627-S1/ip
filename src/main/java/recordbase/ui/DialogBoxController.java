package recordbase.ui;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Creates a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBoxController extends HBox {
    private static final Map<Image, Rectangle2D> AVATAR_VIEWPORTS = new IdentityHashMap<>();
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a custom DialogBox view of a given string and an image.
     *
     * @param text the string for the image
     * @param img image to display beside the text
     */
    public DialogBoxController(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RecordGui.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();

            assert dialog != null : "Dialog label must be injected by FXML";
            assert displayPicture != null : "Display picture must be injected by FXML";

            dialog.setText(text);
            displayPicture.setImage(img);
            displayPicture.setViewport(AVATAR_VIEWPORTS.computeIfAbsent(img,
                    DialogBoxController::findVisibleBounds));
            dialog.maxWidthProperty().bind(widthProperty().multiply(0.72));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> childNodes = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(childNodes);
        this.getChildren().setAll(childNodes);
    }

    /**
     * Creates a right-aligned dialog styled as a user message.
     *
     * @param text message text to display
     * @param img selected user avatar
     * @return configured user dialog
     */
    public static DialogBoxController getUserDialog(String text, Image img) {
        assert text != null : "User dialog text must not be null";
        assert img != null : "User dialog image must not be null";

        var dialogBox = new DialogBoxController(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog styled as a response from Record.
     *
     * @param text response text to display
     * @param img Record's profile image
     * @return configured Record dialog
     */
    public static DialogBoxController getRecordDialog(String text, Image img) {
        assert text != null : "Record dialog text must not be null";
        assert img != null : "Record dialog image must not be null";

        var dialogBox = new DialogBoxController(text, img);
        dialogBox.getStyleClass().add("record-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a compact, visually grouped command reference.
     *
     * @param img Record's profile image
     * @return a left-aligned help dialog with styled command names
     */
    public static DialogBoxController getHelpDialog(Image img) {
        assert img != null : "Record profile image must not be null";

        var dialogBox = getRecordDialog("", img);
        VBox helpContent = new VBox(4);
        helpContent.getStyleClass().add("help-content");
        helpContent.getChildren().addAll(
                styledLabel("Record commands", "help-title"),
                styledLabel("ADD TASKS", "help-section"),
                helpRow("todo", "DESCRIPTION [DATE] [TIME]"),
                helpRow("deadline", "DESCRIPTION /by DATE [TIME]"),
                helpRow("event", "DESCRIPTION /from DATE [TIME] /to DATE [TIME]"),
                styledLabel("MANAGE TASKS", "help-section"),
                helpRow("list", "show every task"),
                helpRow("mark / unmark", "NUMBER"),
                helpRow("delete", "NUMBER"),
                styledLabel("APP", "help-section"),
                helpRow("clear", "clear the conversation"),
                helpRow("help", "show this reference"),
                helpRow("bye", "save and exit"),
                styledLabel("Dates: yyyymmdd  •  Times: hh:mm", "help-hint"));
        dialogBox.dialog.setText(null);
        dialogBox.dialog.setGraphic(helpContent);
        return dialogBox;
    }

    /**
     * Creates a label with the supplied help style.
     *
     * @param text label contents
     * @param styleClass CSS class to apply
     * @return configured label
     */
    private static Label styledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Creates one help row with a bold command followed by its concise description.
     *
     * @param command command name
     * @param description syntax or behavior summary
     * @return styled help row
     */
    private static TextFlow helpRow(String command, String description) {
        Text commandText = new Text(command + "  ");
        commandText.getStyleClass().add("help-command");
        Text descriptionText = new Text(description);
        descriptionText.getStyleClass().add("help-description");
        TextFlow row = new TextFlow(commandText, descriptionText);
        row.getStyleClass().add("help-row");
        return row;
    }

    /**
     * Finds the non-transparent portion of an avatar so differently padded source images occupy
     * the same visual area inside the fixed 36-pixel view.
     */
    private static Rectangle2D findVisibleBounds(Image image) {
        PixelReader pixels = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixels.getColor(x, y).getOpacity() > 0.05) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (minX > maxX || minY > maxY) {
            return new Rectangle2D(0, 0, width, height);
        }
        double contentWidth = maxX - minX + 1;
        double contentHeight = maxY - minY + 1;
        double size = Math.min(Math.max(contentWidth, contentHeight) * 1.08, Math.min(width, height));
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        return new Rectangle2D(Math.max(0, Math.min(centerX - size / 2, width - size)),
                Math.max(0, Math.min(centerY - size / 2, height - size)), size, size);
    }

    /**
     * Creates a visually prominent app reply for invalid commands and other input errors.
     *
     * @param text error message to display
     * @param img Record's profile image
     * @return a left-aligned error dialog
     */
    public static DialogBoxController getErrorDialog(String text, Image img) {
        var dialogBox = getRecordDialog(text, img);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
