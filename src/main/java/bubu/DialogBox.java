package bubu;

import java.io.IOException;
import java.util.Collections;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of a speaker image, name, and text message.
 */
public class DialogBox extends HBox {
    /** Standard profile-picture size used for each conversation participant. */
    private static final double PROFILE_PICTURE_SIZE = 48.0;
    private static final double PROFILE_PICTURE_CORNER_RADIUS = 18.0;
    private static final double MAX_MESSAGE_COLUMN_WIDTH = 420.0;
    private static final double MINIMUM_RESIZABLE_WIDTH = 0.0;
    private static final String DIALOG_LAYOUT_RESOURCE = "/view/DialogBox.fxml";
    private static final String DIALOG_LAYOUT_ERROR_MESSAGE = "Unable to load the dialog layout.";
    private static final String SPEAKER_NAME_STYLE_CLASS = "speaker-name";
    private static final String DIALOG_BUBBLE_STYLE_CLASS = "dialog-bubble";
    private static final String REPLY_STYLE_CLASS = "reply-label";
    private static final String USER_STYLE_CLASS = "user-label";
    private static final String ERROR_STYLE_CLASS = "error-label";
    private static final String USER_NAME = "You";
    private static final String BUBU_NAME = "Bubu";

    @FXML
    private TextFlow dialog;
    @FXML
    private Text dialogText;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Label speakerName;
    @FXML
    private VBox messageColumn;

    private DialogBox(String text, Image img, String speaker) {
        loadDialogLayout();
        configureMessage(text, speaker);
        configureProfilePicture(img);
        configureResizableLayout();
    }

    /** Loads the FXML layout and connects its elements to this dialog box. */
    private void loadDialogLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(DIALOG_LAYOUT_RESOURCE));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException(DIALOG_LAYOUT_ERROR_MESSAGE, e);
        }
    }

    /** Sets the message text and speaker name shown in this dialog box. */
    private void configureMessage(String text, String speaker) {
        dialogText.setText(text);
        dialog.getStyleClass().add(DIALOG_BUBBLE_STYLE_CLASS);
        speakerName.setText(speaker.toUpperCase());
        speakerName.getStyleClass().add(SPEAKER_NAME_STYLE_CLASS);
    }

    /** Configures the speaker image with a small rounded-square crop. */
    private void configureProfilePicture(Image img) {
        displayPicture.setImage(img);
        displayPicture.setFitWidth(PROFILE_PICTURE_SIZE);
        displayPicture.setFitHeight(PROFILE_PICTURE_SIZE);
        Rectangle clip = new Rectangle(PROFILE_PICTURE_SIZE, PROFILE_PICTURE_SIZE);
        clip.setArcWidth(PROFILE_PICTURE_CORNER_RADIUS);
        clip.setArcHeight(PROFILE_PICTURE_CORNER_RADIUS);
        displayPicture.setClip(clip);
    }

    /** Allows the message bubble to grow vertically and shrink horizontally when needed. */
    private void configureResizableLayout() {
        setMinHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Double.MAX_VALUE);
        messageColumn.setMinHeight(Region.USE_PREF_SIZE);
        messageColumn.setPrefHeight(Region.USE_COMPUTED_SIZE);
        messageColumn.setMaxHeight(Double.MAX_VALUE);
        messageColumn.setMinWidth(MINIMUM_RESIZABLE_WIDTH);
        messageColumn.setMaxWidth(MAX_MESSAGE_COLUMN_WIDTH);
        dialog.setMinWidth(MINIMUM_RESIZABLE_WIDTH);
        dialog.setMaxWidth(MAX_MESSAGE_COLUMN_WIDTH);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        dialog.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialog.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        messageColumn.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(REPLY_STYLE_CLASS);
    }

    /**
     * Returns a dialog box for the user.
     *
     * @param text The text to be displayed in the dialog box.
     * @param img The image to be displayed in the dialog box.
     * @return A dialog box for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img, USER_NAME);
        dialogBox.setAlignment(Pos.CENTER_RIGHT);
        dialogBox.dialog.getStyleClass().add(USER_STYLE_CLASS);
        return dialogBox;
    }

    /**
     * Returns a dialog box for Bubu.
     *
     * @param text The text to be displayed in the dialog box.
     * @param img The image to be displayed in the dialog box.
     * @return A dialog box for Bubu.
     */
    public static DialogBox getBubuDialog(String text, Image img) {
        DialogBox bubuDialog = new DialogBox(text, img, BUBU_NAME);
        bubuDialog.flip();
        return bubuDialog;
    }

    /** Applies the attention-grabbing style used for invalid commands. */
    public void setErrorStyle() {
        dialog.getStyleClass().add(ERROR_STYLE_CLASS);
    }
}
