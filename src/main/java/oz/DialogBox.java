package oz;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * A custom control representing a dialog box consisting of an avatar and a
 * selectable message area.
 */
public class DialogBox extends HBox {

    /** Maximum share of a dialog row occupied by its text bubble. */
    private static final double DIALOG_WIDTH_RATIO = 0.75;

    /** Minimum height of a single-line message bubble. */
    private static final double MINIMUM_DIALOG_HEIGHT = 34;

    /** CSS selector for the scroll pane inside the message area. */
    private static final String SCROLL_PANE_SELECTOR = ".scroll-pane";

    /** CSS selector for the text content inside the message area. */
    private static final String TEXT_CONTENT_SELECTOR = ".content";

    /** Label for the action that copies a message to the system clipboard. */
    private static final String COPY_MESSAGE_MENU_TEXT = "Copy message";

    /** Classpath location of the dialog box layout. */
    private static final String DIALOG_BOX_FXML_PATH = "/view/DialogBox.fxml";

    /** Error raised when the dialog layout cannot be loaded. */
    private static final String DIALOG_BOX_LOAD_ERROR = "Unable to load the dialog box layout.";

    /** CSS class applied to every Oz reply. */
    private static final String REPLY_LABEL_STYLE_CLASS = "reply-label";

    /** CSS class applied to successful task additions. */
    private static final String ADD_LABEL_STYLE_CLASS = "add-label";

    /** CSS class applied to task completion changes. */
    private static final String MARKED_LABEL_STYLE_CLASS = "marked-label";

    /** CSS class applied to task deletions. */
    private static final String DELETE_LABEL_STYLE_CLASS = "delete-label";

    /** CSS class applied to errors. */
    private static final String ERROR_LABEL_STYLE_CLASS = "error-label";

    /** Read-only selectable area containing the dialog message. */
    @FXML
    private TextArea dialog;

    /** Avatar picture representing the speaker. */
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a dialog box with the specified text and avatar image.
     *
     * @param text  Message text to display.
     * @param image Avatar image of the speaker.
     */
    private DialogBox(String text, Image image) {
        loadLayout();
        configureContent(text, image);
        configureAutomaticHeight();
        configureCopyMenu();
    }

    /** Loads and connects this control to its FXML layout. */
    private void loadLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML_PATH));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException(DIALOG_BOX_LOAD_ERROR, exception);
        }
    }

    /**
     * Populates the loaded dialog controls.
     *
     * @param text Message text to display.
     * @param image Avatar image of the speaker.
     */
    private void configureContent(String text, Image image) {
        this.dialog.setText(text);
        this.displayPicture.setImage(image);
    }

    /** Wires size changes to automatic message-bubble height updates. */
    private void configureAutomaticHeight() {
        this.dialog.maxWidthProperty().bind(this.widthProperty().multiply(DIALOG_WIDTH_RATIO));
        this.dialog.skinProperty().addListener((observable, oldSkin, newSkin) -> configureTextViewport());
        this.dialog.widthProperty().addListener((observable, oldWidth, newWidth) -> updateDialogHeight());
        this.dialog.fontProperty().addListener((observable, oldFont, newFont) -> updateDialogHeight());
    }

    /** Recalculates the bubble height after its internal viewport is laid out. */
    private void configureTextViewport() {
        Node scrollPaneNode = this.dialog.lookup(SCROLL_PANE_SELECTOR);
        if (!(scrollPaneNode instanceof ScrollPane internalScrollPane)) {
            return;
        }

        internalScrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                updateDialogHeight());
    }

    /** Updates the message area height to display all wrapped text without an inner scrollbar.
     *  Credit to GeraldFSE's finding that text content was clipped inside message bubbles
     *  Link to conversation: https://github.com/NUS-CS2103-AY2627-S1/forum/issues/522#issuecomment-5752656706
     */
    private void updateDialogHeight() {
        this.dialog.applyCss();
        Node scrollPaneNode = this.dialog.lookup(SCROLL_PANE_SELECTOR);
        Node textContentNode = this.dialog.lookup(TEXT_CONTENT_SELECTOR);
        if (!(scrollPaneNode instanceof ScrollPane internalScrollPane)
                || !(textContentNode instanceof Region textContent)) {
            return;
        }

        double viewportWidth = internalScrollPane.getViewportBounds().getWidth();
        double viewportHeight = internalScrollPane.getViewportBounds().getHeight();
        if (viewportWidth <= 0) {
            return;
        }

        double contentHeight = textContent.prefHeight(viewportWidth);
        double controlDecorationHeight = this.dialog.getHeight() - viewportHeight;
        double requiredHeight = contentHeight + controlDecorationHeight;
        this.dialog.setPrefHeight(Math.max(MINIMUM_DIALOG_HEIGHT, Math.ceil(requiredHeight)));
    }

    /** Adds a context-menu action that copies this dialog's complete text. */
    private void configureCopyMenu() {
        MenuItem copyMessageItem = new MenuItem(COPY_MESSAGE_MENU_TEXT);
        copyMessageItem.setOnAction((event) -> copyDialogTextToClipboard());
        this.dialog.setContextMenu(new ContextMenu(copyMessageItem));
    }

    /** Copies the complete message, rather than only selected text, to the clipboard. */
    private void copyDialogTextToClipboard() {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(this.dialog.getText());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    /** Aligns an Oz reply with the avatar on the left and message on the right. */
    private void alignAsOzReply() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> temporaryChildren = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(temporaryChildren);
        this.getChildren().setAll(temporaryChildren);
        this.dialog.getStyleClass().add(REPLY_LABEL_STYLE_CLASS);
    }

    /**
     * Returns a dialog box for the user, aligned to the right.
     *
     * @param text Text of the user's message.
     * @return DialogBox for the user.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.getChildren().remove(dialogBox.displayPicture);
        return dialogBox;
    }

    /**
     * Returns a dialog box for Oz with default styling, flipped so the avatar is on
     * the left.
     *
     * @param text  Text of Oz's response message.
     * @param image Oz's avatar image.
     * @return DialogBox for Oz.
     */
    public static DialogBox getOzDialog(String text, Image image) {
        return getOzDialog(text, image, ResponseType.DEFAULT);
    }

    /**
     * Returns a dialog box for Oz with command-specific styling, flipped so the
     * avatar is on the left.
     *
     * @param text        Text of Oz's response message.
     * @param image       Oz's avatar image.
     * @param responseType Response category used to style the dialog bubble.
     * @return DialogBox for Oz.
     */
    public static DialogBox getOzDialog(String text, Image image, ResponseType responseType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.alignAsOzReply();
        dialogBox.applyResponseStyle(responseType);
        return dialogBox;
    }

    /**
     * Applies a command-specific style class to the dialog bubble.
     *
     * @param responseType Response category indicating which CSS style class to
     *                     apply.
     */
    private void applyResponseStyle(ResponseType responseType) {
        if (responseType == null) {
            return;
        }

        switch (responseType) {
            case ADD:
                this.dialog.getStyleClass().add(ADD_LABEL_STYLE_CLASS);
                break;
            case CHANGE_MARK:
                this.dialog.getStyleClass().add(MARKED_LABEL_STYLE_CLASS);
                break;
            case DELETE:
                this.dialog.getStyleClass().add(DELETE_LABEL_STYLE_CLASS);
                break;
            case ERROR:
                this.dialog.getStyleClass().add(ERROR_LABEL_STYLE_CLASS);
                break;
            default:
                // Do nothing for default or unspecified command types
                break;
        }
    }
}
