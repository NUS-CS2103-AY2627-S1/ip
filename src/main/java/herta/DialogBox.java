package herta;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;

/**
 * Displays a message with an optional Herta avatar.
 */
public class DialogBox extends HBox {
    private static final Logger LOGGER = Logger.getLogger(DialogBox.class.getName());
    private static final double MAX_MESSAGE_WIDTH_RATIO = 0.85;
    private static final double DIALOG_BOX_HORIZONTAL_INSETS = 10.0;
    private static final double LABEL_HORIZONTAL_INSETS = 12.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            var dialogResource = MainWindow.class.getResource("/view/DialogBox.fxml");
            if (dialogResource == null) {
                throw new IllegalStateException("Application files are incomplete; reinstall Herta.");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(dialogResource);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to load the dialog box.", e);
            throw new IllegalStateException("Application files are incomplete; reinstall Herta.", e);
        }

        dialog.setText(text);
        configureCopySupport();
        displayPicture.setImage(image);

        if (image == null) {
            displayPicture.setVisible(false);
            displayPicture.setManaged(false);
        }
    }

    /** Adds whole-message copy support without changing the existing bubble renderer. */
    private void configureCopySupport() {
        MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(event -> copyMessageToClipboard());
        dialog.setContextMenu(new ContextMenu(copyMenuItem));
    }

    /** Copies the message text to the system clipboard. */
    private void copyMessageToClipboard() {
        try {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(dialog.getText());
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Clipboard access failed.", e);
            dialog.setText(dialog.getText() + "\n(Copy unavailable.)");
        }
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a user message without an avatar.
     *
     * @param text the user's message.
     * @return a dialog box displaying the user's message.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null);
    }

    /**
     * Creates a dialog box for a Herta response with an avatar.
     *
     * @param text Herta's response.
     * @param image Herta's avatar image.
     * @return a dialog box displaying Herta's response.
     */
    public static DialogBox getHertaDialog(String text, Image image) {
        return getHertaDialog(text, image, null);
    }

    /**
     * Creates a dialog box for a Herta response with its semantic response style.
     *
     * @param text Herta's response.
     * @param image Herta's avatar image.
     * @param responseCategory the semantic category of the response.
     * @return a dialog box displaying Herta's response.
     */
    public static DialogBox getHertaDialog(String text, Image image, ResponseCategory responseCategory) {
        var dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.applyResponseStyle(responseCategory);
        return dialogBox;
    }

    /** Applies a font-size style to the message while preserving its semantic style. */
    void setFontSizeStyle(String fontSizeStyle) {
        dialog.setStyle(fontSizeStyle);
    }

    /** Resizes the avatar while preserving its aspect ratio. */
    void setAvatarSize(double size) {
        displayPicture.setFitHeight(size);
        displayPicture.setFitWidth(size);
    }

    /** Limits the message width while preserving enough room for the avatar. */
    void setMessageMaxWidth(double availableWidth) {
        double maximumMessageWidth = availableWidth * MAX_MESSAGE_WIDTH_RATIO;
        if (displayPicture.isManaged()) {
            double availableReplyWidth = availableWidth - displayPicture.getFitWidth()
                    - DIALOG_BOX_HORIZONTAL_INSETS - LABEL_HORIZONTAL_INSETS;
            maximumMessageWidth = Math.min(maximumMessageWidth, availableReplyWidth);
        }
        dialog.setMaxWidth(Math.max(0, maximumMessageWidth));
    }

    /**
     * Applies a semantic style to Herta's response label when a category is supplied.
     * A missing category leaves the default styling unchanged.
     *
     * @param responseCategory the semantic category of the response.
     */
    private void applyResponseStyle(ResponseCategory responseCategory) {
        if (responseCategory == null) {
            return;
        }

        String styleClass = switch (responseCategory) {
            case ADD -> "add-label";
            case MARK -> "marked-label";
            case UNMARK -> "unmarked-label";
            case DELETE -> "delete-label";
            case ARCHIVE -> "archive-label";
            case RESTORE -> "restore-label";
            case QUERY -> "query-label";
            case EXIT -> "exit-label";
            case USAGE_GUIDANCE -> "usage-guidance-label";
            case ERROR -> "error-label";
            default -> throw new IllegalStateException(
                    "Unsupported response category: " + responseCategory);
        };
        dialog.getStyleClass().add(styleClass);
    }
}
