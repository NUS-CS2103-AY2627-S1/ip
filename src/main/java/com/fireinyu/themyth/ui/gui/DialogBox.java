package com.fireinyu.themyth.ui.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a text flow containing styled text from the speaker.
 */
public class DialogBox extends HBox {
    private static final List<Runnable> ON_DIALOG_CREATED_LISTENERS = new CopyOnWriteArrayList<>();

    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Private constructor for a DialogBox.
     *
     * @param textNodes The styled text nodes to display in the dialog box.
     * @param img The image to display.
     */
    private DialogBox(List<Text> textNodes, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getChildren().setAll(textNodes);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns the text flow node containing the dialog message.
     *
     * @return the text flow node.
     */
    TextFlow getDialog() {
        return dialog;
    }

    /**
     * Returns the image view displaying the speaker's avatar.
     *
     * @return the avatar image view.
     */
    ImageView getDisplayPicture() {
        return displayPicture;
    }

    /**
     * Registers a listener to be notified whenever a dialog box is created.
     *
     * @param listener the callback to run on dialog creation.
     */
    public static void addOnDialogCreatedListener(Runnable listener) {
        if (listener != null) {
            ON_DIALOG_CREATED_LISTENERS.add(listener);
        }
    }

    /**
     * Clears all registered dialog creation listeners.
     */
    public static void clearOnDialogCreatedListeners() {
        ON_DIALOG_CREATED_LISTENERS.clear();
    }

    /**
     * Notifies all registered listeners that a dialog box has been created.
     */
    private static void notifyDialogCreated() {
        for (Runnable listener : ON_DIALOG_CREATED_LISTENERS) {
            try {
                listener.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a dialog box for the user with sparkle animation.
     *
     * @param text The text from the user.
     * @param img The user's image.
     * @return A new DialogBox for the user.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(FlamboyantTextFormatter.formatPlain(text), img);
        SparkleAnimator.play(dialogBox, false);
        notifyDialogCreated();
        return dialogBox;
    }

    /**
     * Creates a dialog box for TheMyth with flamboyant rich text styling and sparkle animation.
     * This dialog box is flipped.
     *
     * @param text The text from TheMyth.
     * @param img TheMyth's image.
     * @return A new DialogBox for TheMyth.
     */
    public static DialogBox getTheMythDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(FlamboyantTextFormatter.format(text), img);
        dialogBox.flip();
        SparkleAnimator.play(dialogBox, true);
        notifyDialogCreated();
        return dialogBox;
    }
}
