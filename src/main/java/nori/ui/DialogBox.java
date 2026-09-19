package nori.ui;

import java.io.IOException;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Displays one chat message together with a speaker label, and an avatar where
 * the speaker has one.
 */
public final class DialogBox extends HBox {
    /** The size of Nori's avatar, small enough to identify the speaker without crowding the text. */
    private static final double AVATAR_SIZE = 38;

    /**
     * The share of the conversation's width one message may fill.
     *
     * A message stops short of the far edge so that the side it is aligned to
     * stays obvious, and so that a long line does not run the full width of a
     * maximised window, which is tiring to read back.
     */
    private static final double MESSAGE_WIDTH_FRACTION = 0.78;

    /** Style class marking a reply that reports something Nori could not do. */
    private static final String ERROR_STYLE_CLASS = "bubble-error";

    @FXML
    private StackPane avatarSlot;
    @FXML
    private Label dialog;
    @FXML
    private VBox messageColumn;
    @FXML
    private Label speakerLabel;

    /** Creates a styled dialog box for one speaker. */
    private DialogBox(String text, String speaker, String bubbleStyle) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        dialog.setStyle(ConversationFont.getFamilyStyle());
        dialog.getStyleClass().add(bubbleStyle);
        speakerLabel.setText(speaker);
    }

    /**
     * Creates a dialog aligned to the right for user input.
     *
     * The user is given no avatar. There are only ever two speakers here, and
     * the reader is one of them, so a picture of themselves tells them nothing
     * the alignment and the bubble's shape have not already said. Leaving it
     * out returns its width to the text and makes the two sides of the
     * conversation look less alike, which is the point.
     *
     * @param text the command entered by the user.
     * @return the user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "YOU", "user-bubble");
        dialogBox.alignToRight();
        return dialogBox;
    }

    /**
     * Creates a dialog aligned to the left for Nori's response.
     *
     * @param text Nori's response text.
     * @return Nori's dialog box.
     */
    public static DialogBox getNoriDialog(String text) {
        return getNoriDialog(text, false);
    }

    /**
     * Creates a dialog aligned to the left for Nori's response, marking an error as one.
     *
     * A reply saying a command could not be carried out is the reply a user
     * most needs to notice, and it arrives among replies that all look alike.
     * Marking it out means a user scanning the conversation can see that
     * something did not happen without reading every bubble to find out.
     *
     * @param text Nori's response text.
     * @param isError whether the response reports something Nori could not do.
     * @return Nori's dialog box.
     */
    public static DialogBox getNoriDialog(String text, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, isError ? "NORI · PROBLEM" : "NORI",
                "nori-bubble");
        dialogBox.avatarSlot.getChildren().setAll(new BotAvatar(AVATAR_SIZE));
        if (isError) {
            dialogBox.dialog.getStyleClass().add(ERROR_STYLE_CLASS);
            dialogBox.speakerLabel.getStyleClass().add(ERROR_STYLE_CLASS);
        }
        return dialogBox;
    }

    /**
     * Keeps this message's width in step with the conversation it sits in.
     *
     * The width is a share of what the conversation has rather than a fixed
     * number of pixels, so a message uses the room a wide window offers and
     * gives it back when the window is made narrow.
     *
     * @param conversationWidth the width of the conversation view.
     */
    void bindMessageWidthTo(ReadOnlyDoubleProperty conversationWidth) {
        messageColumn.maxWidthProperty().bind(conversationWidth.multiply(MESSAGE_WIDTH_FRACTION));
    }

    /** Right-aligns the message and drops the avatar slot the user does not use. */
    private void alignToRight() {
        Node[] remainingChildren = {messageColumn};
        getChildren().setAll(remainingChildren);
        setAlignment(Pos.TOP_RIGHT);
        messageColumn.setAlignment(Pos.TOP_RIGHT);
        speakerLabel.setAlignment(Pos.CENTER_RIGHT);
        dialog.setAlignment(Pos.CENTER_RIGHT);
    }
}
